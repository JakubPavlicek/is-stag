package com.stag.keycloak.authentication;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;

import java.io.StringReader;
import java.net.URI;
import java.util.Base64;
import java.util.logging.Logger;

public class IsStagAuthenticator implements Authenticator {

    private static final Logger log = Logger.getLogger(IsStagAuthenticator.class.getName());

    private static final String STAG_LOGIN_URL = "https://stag-demo.zcu.cz/ws/login";
    private static final String STAG_REDIRECT_PARAM = "originalURL";
    private static final String CALLBACK_PARAM = "stagUserInfo";
    private static final String REDIRECT_TRIGGER_PARAM = "kc_action";
    private static final String REDIRECT_TRIGGER_VALUE = "stag_login";

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        var query = context.getHttpRequest()
                           .getUri()
                           .getQueryParameters();

        // Check if we are handling a STAG login response
        if (query.containsKey(CALLBACK_PARAM)) {
            action(context); // STAG login complete — continue flow
            return;
        }

        // Check if user clicked "Login with STAG" button
        if (REDIRECT_TRIGGER_VALUE.equals(query.getFirst(REDIRECT_TRIGGER_PARAM))) {

            // TODO: remove "kc_action=stag_login" from the URL ???

            // Build return URL back to Keycloak’s login-action endpoint
            URI returnUri = context.getUriInfo()
                                   .getRequestUriBuilder()
                                   .replaceQueryParam(REDIRECT_TRIGGER_PARAM, REDIRECT_TRIGGER_VALUE)
                                   .build();

            log.info(String.format("IsStagAuthenticator: Return to URI: %s", returnUri.toString()));

            // Redirect user to STAG login
            URI stagLoginUri = UriBuilder.fromUri(STAG_LOGIN_URL)
                                         .queryParam(STAG_REDIRECT_PARAM, returnUri.toString())
                                         .build();

            context.challenge(Response.seeOther(stagLoginUri)
                                      .build());
            return;
        }

        log.info("IsStagAuthenticator: Attempted");

        // Show Keycloak's login form (fallback or first load)
        context.attempted(); // let next flow handle (e.g. normal login page)
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        try {
            log.info("IsStagAuthenticator: action() called");

            String encodedUserInfo = context.getHttpRequest()
                                            .getUri()
                                            .getQueryParameters()
                                            .getFirst(CALLBACK_PARAM);

            if (encodedUserInfo == null || encodedUserInfo.isEmpty()) {
                throw new RuntimeException("Missing stagUserInfo");
            }

            // Decode and parse JSON
            String decodedJson = new String(Base64.getUrlDecoder()
                                                  .decode(encodedUserInfo));

            log.info("IsStagAuthenticator: Decoded JSON: " + decodedJson);

            JsonReader jsonReader = Json.createReader(new StringReader(decodedJson));
            JsonObject userInfoPayload = jsonReader.readObject();
            JsonArray userInfoArray = userInfoPayload.getJsonArray(CALLBACK_PARAM);

            if (userInfoArray.isEmpty()) {
                throw new RuntimeException("Empty stagUserInfo array");
            }

            JsonObject userDetails = userInfoArray.getJsonObject(0);
            String username = userDetails.getString("userName");
            String email = userDetails.getString("email");
            String firstName = userInfoPayload.getString("jmeno");
            String lastName = userInfoPayload.getString("prijmeni");

            log.info(String.format("IsStagAuthenticator: [username=%s, email=%s, firstname=%s, lastname=%s]", username, email, firstName, lastName));

            if (username == null || username.isEmpty()) {
                throw new RuntimeException("Missing username in stagUserInfo");
            }

            // Create or load user
            UserModel user = context.getSession()
                                    .users()
                                    .getUserByUsername(context.getRealm(), username);

            if (user == null) {
                user = context.getSession()
                              .users()
                              .addUser(context.getRealm(), username);
                user.setEnabled(true);

                if (email != null && !email.isEmpty()) {
                    user.setEmail(email);
                    user.setEmailVerified(true);
                }
                if (firstName != null && !firstName.isEmpty()) {
                    user.setFirstName(firstName);
                }
                if (lastName != null && !lastName.isEmpty()) {
                    user.setLastName(lastName);
                }
            }

            // TODO: Redirect user to the React App after successful login

            // Success — complete login
            context.setUser(user);
            context.success();

        } catch (Exception e) {
            context.failureChallenge(
                AuthenticationFlowError.INVALID_CREDENTIALS,
                context.form()
                       .addError(new FormMessage("login-error", "Invalid STAG login: " + e.getMessage()))
                       .createErrorPage(Response.Status.BAD_REQUEST)
            );
        }
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // NO-OP
    }

    @Override
    public void close() {
        // NO-OP
    }

}