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

/**
 * Custom Keycloak Authenticator for IS/STAG integration.
 * <p>
 * This authenticator extends {@link UsernamePasswordForm} to provide a custom authentication flow
 * that integrates with the IS/STAG system. It handles redirects to the IS/STAG login page,
 * processes the response containing user information, and creates or updates users in Keycloak accordingly.
 * It also supports anonymous login handling.
 * </p>
 *
 * @author Jakub Pavlíček
 * @version 1.0.0
 */
public class IsStagAuthenticator extends UsernamePasswordForm {

    /** Logger for logging authentication events and errors */
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
    private static final String STUDENT_ID_ATTR = "studentId";
    private static final String TEACHER_ID_ATTR = "teacherId";

    /**
     * Authenticates the user based on the context and query parameters.
     * <p>
     * This method checks the query parameters to determine the flow:
     * <ul>
     *   <li>If {@code stagUserInfo} is present, it handles the login response from IS/STAG (either anonymous or regular user).</li>
     *   <li>If {@code stag_login} is set to "true", it redirects the user to the IS/STAG login page.</li>
     *   <li>Otherwise, it proceeds with the standard username/password authentication.</li>
     * </ul>
     * </p>
     *
     * @param context The {@link AuthenticationFlowContext} containing request and session information.
     */
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

    /**
     * Redirects the user to the external IS/STAG login page.
     * <p>
     * Constructs the redirect URL by appending the current request URI as the {@code originalURL} parameter.
     * </p>
     *
     * @param context The {@link AuthenticationFlowContext} used to build the redirect URI.
     */
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

    /**
     * Handles the anonymous login flow.
     * <p>
     * Attempts to find a user with the username "anonymous" in the current realm.
     * If found, the user is set in the context and authentication succeeds.
     * Otherwise, an authentication error is reported.
     * </p>
     *
     * @param context The {@link AuthenticationFlowContext} for user lookup and session management.
     */
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

    /**
     * Handles the regular user login flow based on IS/STAG user info.
     * <p>
     * Decodes and parses the Base64 encoded {@code stagUserInfo}.
     * It then looks up the user in Keycloak by username. If the user does not exist,
     * a new user is created. On successful processing, the user is authenticated.
     * </p>
     *
     * @param context The {@link AuthenticationFlowContext} for user management.
     * @param stagUserInfo The Base64 encoded JSON string containing user details from IS/STAG.
     */
    private void handleUserLogin(AuthenticationFlowContext context, String stagUserInfo) {
        log.info("Handling user login");

        try {
            log.debug("Decoding and parsing STAG user info");

            // Decode and parse the user info
            IsStagUser isStagUser = objectMapper.readValue(
                Base64.getDecoder().decode(stagUserInfo),
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

    /**
     * Creates a new user in Keycloak based on the provided IS/STAG data.
     * <p>
     * Sets the username, email, first name, last name, and additional attributes
     * (student ID or teacher ID) based on the {@link IsStagUserDetails}.
     * It also assigns the user a role if one is specified and exists in the realm.
     * </p>
     *
     * @param userProvider The {@link UserProvider} to manage users.
     * @param realm The {@link RealmModel} where the user will be created.
     * @param isStagUser The {@link IsStagUser} containing the user's name details.
     * @param isStagUserDetails The {@link IsStagUserDetails} containing specific user attributes and role.
     * @return The newly created {@link UserModel}.
     */
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
        user.setFirstName(isStagUser.firstName());
        user.setLastName(isStagUser.lastName());

        // Set additional attributes (student has studentId, teacher has teacherId)
        isStagUserDetails.studentId()
                         .ifPresent(num -> {
                             log.debug("Setting personal number attribute for user: " + userName);
                             user.setSingleAttribute(STUDENT_ID_ATTR, num);
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

    /**
     * Handles authentication errors by setting a failure challenge.
     * <p>
     * Logs the warning message and updates the authentication context with an invalid credentials error
     * and a corresponding error page.
     * </p>
     *
     * @param message The error message to display and log.
     * @param context The {@link AuthenticationFlowContext} to update with failure details.
     */
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