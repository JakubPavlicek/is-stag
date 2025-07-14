package com.stag.keycloak.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.stag.keycloak.authentication.dto.IsStagUser;
import com.stag.keycloak.authentication.dto.IsStagUserDetails;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
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
import java.util.logging.Logger;

public class IsStagAuthenticator extends UsernamePasswordForm {

    private static final Logger log = Logger.getLogger(IsStagAuthenticator.class.getName());
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module());

    // Configuration constants
    private static final String STAG_LOGIN_URL = "https://stag-demo.zcu.cz/ws/login";
    private static final String STAG_REDIRECT_PARAM = "originalURL";
    private static final String STAG_USER_INFO = "stagUserInfo";
    private static final String STAG_LOGIN = "stag_login";
    private static final String STAG_LOGIN_TRIGGER = "true";

    // Attribute keys
    private static final String PERSONAL_NUM_ATTR = "osCislo";
    private static final String TEACHER_ID_ATTR = "ucitIdno";

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        log.info("authenticate() called");

        MultivaluedMap<String, String> queryParams = context.getHttpRequest()
                                                            .getUri()
                                                            .getQueryParameters();

        // Check if we are handling a STAG login response
        if (queryParams.containsKey(STAG_USER_INFO)) {
            handleStagLoginResponse(context, queryParams.getFirst(STAG_USER_INFO));
            return;
        }

        // Check if user clicked "Login with IS/STAG" button
        if (STAG_LOGIN_TRIGGER.equals(queryParams.getFirst(STAG_LOGIN))) {
            redirectToStagLogin(context);
            return;
        }

        // If not, proceed with the default Keycloak authentication flow
        super.authenticate(context);
    }

    private void redirectToStagLogin(AuthenticationFlowContext context) {
        // Build a return URL back to Keycloak’s login-action endpoint
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

    private void handleStagLoginResponse(AuthenticationFlowContext context, String stagUserInfo) {
        try {
            // Decode and parse the user info
            IsStagUser isStagUser = objectMapper.readValue(
                Base64.getUrlDecoder().decode(stagUserInfo),
                IsStagUser.class
            );
            IsStagUserDetails isStagUserDetails = isStagUser.stagUserInfo().getFirst();

            logUserDetails(isStagUser, isStagUserDetails);

            // Create or load the user
            RealmModel realm = context.getRealm();
            UserProvider userProvider = context.getSession().users();
            UserModel user = userProvider.getUserByUsername(realm, isStagUserDetails.userName());

            if (user == null) {
                user = createUser(userProvider, realm, isStagUser, isStagUserDetails);
            }

            // Success — complete login
            context.setUser(user);
            context.success();
        } catch (Exception e) {
            handleAuthenticationError(context, "STAG login error: " + e.getMessage());
        }
    }

    private UserModel createUser(
        UserProvider userProvider,
        RealmModel realm,
        IsStagUser isStagUser,
        IsStagUserDetails isStagUserDetails
    ) {
        UserModel user = userProvider.addUser(realm, isStagUserDetails.userName());
        user.setEnabled(true);
        user.setEmail(isStagUserDetails.email());
        user.setEmailVerified(true);
        user.setFirstName(isStagUser.name());
        user.setLastName(isStagUser.lastname());

        // Set additional attributes (student has osCislo, teacher has ucitIdno)
        isStagUserDetails.personalNumber()
                         .ifPresent(num -> user.setSingleAttribute(PERSONAL_NUM_ATTR, num));
        isStagUserDetails.teacherIdentifier()
                         .ifPresent(id -> user.setSingleAttribute(TEACHER_ID_ATTR, id.toString()));

        // Assign a role to the user
        Optional.ofNullable(realm.getRole(isStagUserDetails.role()))
                .ifPresent(user::grantRole);

        return user;
    }

    private void logUserDetails(IsStagUser isStagUser, IsStagUserDetails isStagUserDetails) {
        String message = String.format(
            "IsStagAuthenticator: [username=%s, email=%s, firstname=%s, lastname=%s, role=%s, ucitIdno=%s, osCislo=%s]",
            isStagUserDetails.userName(),
            isStagUserDetails.email(),
            isStagUser.name(),
            isStagUser.lastname(),
            isStagUserDetails.role(),
            isStagUserDetails.teacherIdentifier().orElse(null),
            isStagUserDetails.personalNumber().orElse(null)
        );
        log.info(message);
    }

    private void handleAuthenticationError(AuthenticationFlowContext context, String message) {
        log.severe(message);
        context.failureChallenge(
            AuthenticationFlowError.INVALID_CREDENTIALS,
            context.form()
                   .addError(new FormMessage("login-error", message))
                   .createErrorPage(Response.Status.BAD_REQUEST)
        );
    }

}