package com.stag.keycloak.authentication;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.ws.rs.core.MultivaluedMap;
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
    private static final String STAG_USER_INFO = "stagUserInfo";
    private static final String STAG_LOGIN = "stag_login";
    private static final String KC_ACTION = "kc_action";

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> queryParams = context.getHttpRequest()
                                                            .getUri()
                                                            .getQueryParameters();

        // Check if we are handling a STAG login response
        if (queryParams.containsKey(STAG_USER_INFO)) {
            handleStagLoginResponse(context, queryParams.getFirst(STAG_USER_INFO)); // STAG login complete — continue flow
            return;
        }

        // Check if user clicked "Login with STAG" button
        if (STAG_LOGIN.equals(queryParams.getFirst(KC_ACTION))) {
            // Build a return URL back to Keycloak’s login-action endpoint
            URI returnUri = context.getUriInfo()
                                   .getRequestUriBuilder()
                                   .build();

            log.info(String.format("IsStagAuthenticator: Return to URI: %s", returnUri.toString()));

            // Redirect user to STAG login
            URI stagLoginUri = UriBuilder.fromUri(STAG_LOGIN_URL)
                                         .queryParam(STAG_REDIRECT_PARAM, returnUri.toString())
                                         .build();

            context.challenge(Response.seeOther(stagLoginUri).build());
            return;
        }

        log.info("IsStagAuthenticator: attempted() called");

        // Show Keycloak's login form (fallback or first load)
        context.attempted(); // let next flow handle (e.g. normal login page)
    }

    private void handleStagLoginResponse(AuthenticationFlowContext context, String stagUserInfo) {
        try {
            log.info("IsStagAuthenticator: action() called");

            String encodedUserInfo = context.getHttpRequest()
                                            .getUri()
                                            .getQueryParameters()
                                            .getFirst(STAG_USER_INFO);

            if (encodedUserInfo == null || encodedUserInfo.isEmpty()) {
                throw new RuntimeException("Missing stagUserInfo");
            }

            // Decode and parse JSON
            String decodedJson = new String(Base64.getUrlDecoder()
                                                  .decode(encodedUserInfo));

            log.info("IsStagAuthenticator: Decoded JSON: " + decodedJson);

            JsonReader jsonReader = Json.createReader(new StringReader(decodedJson));
            JsonObject userInfoPayload = jsonReader.readObject();
            JsonArray userInfoArray = userInfoPayload.getJsonArray(STAG_USER_INFO);

            if (userInfoArray.isEmpty()) {
                throw new RuntimeException("Empty stagUserInfo array");
            }

            // TODO: extract 'role' and 'ucitIdno'/'osCislo' and save them to the Keycloak

            // TODO: use Jackson or another library to parse the JSON if needed

            String firstName = userInfoPayload.getString("jmeno");
            String lastName = userInfoPayload.getString("prijmeni");

            JsonObject userDetails = userInfoArray.getJsonObject(0);

            String username = userDetails.getString("userName");
            String email = userDetails.getString("email");
            String role = userDetails.getString("role");
            JsonNumber ucitIndoJsonNumber = userDetails.getJsonNumber("ucitIdno");
            Long ucitIdno = ucitIndoJsonNumber != null ? ucitIndoJsonNumber.longValue() : null;
            String osCislo = userDetails.getString("osCislo", null);

            log.info(String.format("IsStagAuthenticator: [username=%s, email=%s, firstname=%s, lastname=%s, role=%s, ucitIdno=%s, osCislo=%s]", username, email, firstName, lastName, role, ucitIdno, osCislo));

            if (username == null || username.isEmpty()) {
                throw new RuntimeException("Missing username in stagUserInfo");
            }

            // Create or load user
            UserModel user = context.getSession()
                                    .users()
                                    .getUserByUsername(context.getRealm(), username);

            if (user == null) {
                System.out.println("Creating new user");
                user = context.getSession()
                              .users()
                              .addUser(context.getRealm(), username);
                user.setEnabled(true);

                if (email != null && !email.isEmpty()) {
                    System.out.println("Setting email");
                    user.setEmail(email);
                    user.setEmailVerified(true);
                }
                if (firstName != null && !firstName.isEmpty()) {
                    System.out.println("Setting firstname");
                    user.setFirstName(firstName);
                }
                if (lastName != null && !lastName.isEmpty()) {
                    System.out.println("Setting lastname");
                    user.setLastName(lastName);
                }
                if (role != null && !role.isEmpty()) {
//                    System.out.println("Setting role");
//                    user.setSingleAttribute("role", role);
                    // TODO: Assign role to the user (Create a role in Keycloak if it doesn't exist)
//                    user.grantRole(context.getRealm().getRole(role));
                }
                if (ucitIdno != null) {
                    System.out.println("Setting ucitIdno: " + ucitIdno);
                    user.setSingleAttribute("ucitIdno", ucitIdno.toString());
                }
                if (osCislo != null && !osCislo.isEmpty()) {
                    System.out.println("Setting osCislo: " + osCislo);
                    user.setSingleAttribute("osCislo", osCislo);
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
    public void action(AuthenticationFlowContext context) {
        log.warning("IsStagAuthenticator: action() called");
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