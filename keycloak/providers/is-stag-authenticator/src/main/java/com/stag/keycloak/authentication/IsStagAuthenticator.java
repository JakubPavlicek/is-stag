package com.stag.keycloak.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.stag.keycloak.authentication.dto.IsStagUser;
import com.stag.keycloak.authentication.dto.IsStagUserDetails;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordForm;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserProvider;
import org.keycloak.models.utils.FormMessage;

import java.net.URI;
import java.util.Base64;
import java.util.Optional;

public class IsStagAuthenticator extends UsernamePasswordForm {

    private static final Logger log = Logger.getLogger(IsStagAuthenticator.class.getName());

    /** Object mapper for JSON processing - Jdk8Module is needed because of Optional<> usage */
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module());

    // Configuration constants
    private static final String STAG_LOGIN_URL = "https://stag-demo.zcu.cz/ws/login";
    private static final String STAG_REDIRECT_PARAM = "originalURL";
    private static final String STAG_USER_TICKET = "stagUserTicket";
    private static final String STAG_USER_INFO = "stagUserInfo";
    private static final String ANONYMOUS = "anonymous";
    private static final String STAG_LOGIN = "stag_login";
    private static final String STAG_LOGIN_TRIGGER = "true";

    // Attribute keys
    private static final String PERSONAL_NUM_ATTR = "osCislo";
    private static final String TEACHER_ID_ATTR = "ucitIdno";

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        log.info("Authentication started");

        MultivaluedMap<String, String> queryParams = context.getHttpRequest()
                                                            .getUri()
                                                            .getQueryParameters();

        log.debug("Query parameters received: " + queryParams.keySet());

        // Check if we are handling a STAG login response
        if (queryParams.containsKey(STAG_USER_INFO)) {
            log.info("Handling STAG login response");

            // The user is trying to log in as an anonymous user
            if (ANONYMOUS.equals(queryParams.getFirst(STAG_USER_TICKET))) {
                log.info("Anonymous login detected");
                handleAnonymousLogin(context);
                return;
            }

            // The user is trying to log in as a regular user
            log.info("Regular STAG user login detected");
            handleUserLogin(context, queryParams.getFirst(STAG_USER_INFO));
            return;
        }

        // Redirect user to STAG if the "Login with IS/STAG" button was clicked
        if (STAG_LOGIN_TRIGGER.equals(queryParams.getFirst(STAG_LOGIN))) {
            log.info("STAG login trigger detected, redirecting to STAG login");
            redirectToStagLogin(context);
            return;
        }

        // If nothing above matched, proceed with standard Keycloak authentication
        log.info("Proceeding with standard Keycloak authentication");
        super.authenticate(context);
    }

    private void redirectToStagLogin(AuthenticationFlowContext context) {
        log.info("Redirecting to STAG login");

        // Build a return URL back to Keycloak's login-action endpoint
        URI returnUri = context.getUriInfo()
                               .getRequestUriBuilder()
                               .build();

        // Build the STAG login URL with the return URI as a query parameter
        URI stagLoginUri = UriBuilder.fromUri(STAG_LOGIN_URL)
                                     .queryParam(STAG_REDIRECT_PARAM, returnUri.toString())
                                     .build();

        // Redirect user to STAG login
        context.challenge(Response.seeOther(stagLoginUri).build());
    }

    private void handleAnonymousLogin(AuthenticationFlowContext context) {
        log.info("Handling anonymous login");

        RealmModel realm = context.getRealm();
        UserProvider userProvider = context.getSession().users();

        log.debug("Looking for anonymous user in realm: " + realm.getName());

        // Always use the same anonymous user
        UserModel anonymousUser = userProvider.getUserByUsername(realm, ANONYMOUS);

        if (anonymousUser == null) {
            handleAuthenticationError("Anonymous user not found in realm: " + realm.getName(), context);
            return;
        }

        log.info("Anonymous user found and authenticated successfully");
        context.setUser(anonymousUser);
        context.success();
    }

    private void handleUserLogin(AuthenticationFlowContext context, String stagUserInfo) {
        log.info("Handling user login");

        try {
            log.debug("Decoding and parsing STAG user info");

            // Decode and parse the user info
            IsStagUser isStagUser = objectMapper.readValue(
                Base64.getUrlDecoder().decode(stagUserInfo),
                IsStagUser.class
            );
            IsStagUserDetails isStagUserDetails = isStagUser.stagUserInfo().getFirst();

            log.info("Successfully parsed STAG user info for username: " + isStagUserDetails.userName());

            // Create or load the user
            RealmModel realm = context.getRealm();
            UserProvider userProvider = context.getSession().users();

            log.debug("Looking for existing user: " + isStagUserDetails.userName() + " in realm: " + realm.getName());
            UserModel user = userProvider.getUserByUsername(realm, isStagUserDetails.userName());

            if (user == null) {
                user = createUser(userProvider, realm, isStagUser, isStagUserDetails);
            }

            log.info("STAG authentication successful for user: " + isStagUserDetails.userName());
            context.setUser(user);
            context.success();
        } catch (Exception e) {
            handleAuthenticationError("Error during STAG login handling: " + e.getMessage(), context);
        }
    }

    private UserModel createUser(
        UserProvider userProvider,
        RealmModel realm,
        IsStagUser isStagUser,
        IsStagUserDetails isStagUserDetails
    ) {
        String userName = isStagUserDetails.userName();

        log.info("Creating user for username: " + userName);

        UserModel user = userProvider.addUser(realm, userName);
        user.setEnabled(true);
        user.setEmail(isStagUserDetails.email());
        user.setEmailVerified(true);
        user.setFirstName(isStagUser.name());
        user.setLastName(isStagUser.lastname());

        // Set additional attributes (student has osCislo, teacher has ucitIdno)
        isStagUserDetails.personalNumber()
                         .ifPresent(num -> {
                             log.debug("Setting personal number attribute for user: " + userName);
                             user.setSingleAttribute(PERSONAL_NUM_ATTR, num);
                         });
        isStagUserDetails.teacherId()
                         .ifPresent(id -> {
                             log.debug("Setting teacher ID attribute for user: " + userName);
                             user.setSingleAttribute(TEACHER_ID_ATTR, id.toString());
                         });

        String roleName = isStagUserDetails.role();

        Optional.ofNullable(realm.getRole(roleName))
                .ifPresent(role -> {
                    user.grantRole(role);
                    log.debug("Role '" + roleName + "' assigned to user: " + userName);
                });

        log.info("User creation completed successfully for: " + userName);
        return user;
    }

    private void handleAuthenticationError(String message, AuthenticationFlowContext context) {
        log.warn(message);

        context.failureChallenge(
            AuthenticationFlowError.INVALID_CREDENTIALS,
            context.form()
                   .addError(new FormMessage("login-error", message))
                   .createErrorPage(Response.Status.BAD_REQUEST)
        );
    }

}