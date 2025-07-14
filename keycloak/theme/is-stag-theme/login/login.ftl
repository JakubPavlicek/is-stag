<#import "template.ftl" as layout>
<#import "field.ftl" as field>
<#import "buttons.ftl" as buttons>
<#import "social-providers.ftl" as identityProviders>
<#import "passkeys.ftl" as passkeys>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('username','password') displayInfo=realm.password && realm.registrationAllowed && !registrationDisabled??; section>
<!-- template: login.ftl -->

    <#if section = "header">
        ${msg("loginAccountTitle")}
    <#elseif section = "form">
        <div id="kc-form">
          <div id="kc-form-wrapper">
            <#if realm.password>
                <form id="kc-form-login" class="${properties.kcFormClass!}" onsubmit="login.disabled = true; return true;" action="${url.loginAction}" method="post" novalidate="novalidate">
                    <#if !usernameHidden??>
                        <#assign label>
                            <#if !realm.loginWithEmailAllowed>${msg("username")}<#elseif !realm.registrationEmailAsUsername>${msg("usernameOrEmail")}<#else>${msg("email")}</#if>
                        </#assign>
                        <@field.input name="username" label=label error=kcSanitize(messagesPerField.getFirstError('username','password'))?no_esc
                            autofocus=true autocomplete="${(enableWebAuthnConditionalUI?has_content)?then('username webauthn', 'username')}" value=login.username!'' />
                        <@field.password name="password" label=msg("password") error="" forgotPassword=realm.resetPasswordAllowed autofocus=usernameHidden?? autocomplete="current-password">
                            <#if realm.rememberMe && !usernameHidden??>
                                <@field.checkbox name="rememberMe" label=msg("rememberMe") value=login.rememberMe?? />
                            </#if>
                        </@field.password>
                    <#else>
                        <@field.password name="password" label=msg("password") forgotPassword=realm.resetPasswordAllowed autofocus=usernameHidden?? autocomplete="current-password">
                            <#if realm.rememberMe && !usernameHidden??>
                                <@field.checkbox name="rememberMe" label=msg("rememberMe") value=login.rememberMe?? />
                            </#if>
                        </@field.password>
                    </#if>

                    <input type="hidden" id="id-hidden-input" name="credentialId" <#if auth.selectedCredential?has_content>value="${auth.selectedCredential}"</#if>/>
                    <@buttons.loginButton />
                </form>

                <!-- IS/STAG button -->
                <div class="${properties.kcFormGroupClass!}">
                    <button id="stag-login-button"
                            class="${properties.kcButtonPrimaryClass} ${properties.kcButtonBlockClass} ${properties.kcMarginTopClass} stag-login-btn"
                            type="button">
                        ${msg("Login with IS/STAG")}
                    </button>
                </div>
            </#if>
            </div>
        </div>
        <@passkeys.conditionalUIData />
    <#elseif section = "socialProviders" >
        <#if realm.password && social.providers?? && social.providers?has_content>
            <@identityProviders.show social=social/>
        </#if>
    <#elseif section = "info" >
        <#if realm.password && realm.registrationAllowed && !registrationDisabled??>
            <div id="kc-registration-container">
                <div id="kc-registration">
                    <span>${msg("noAccount")} <a href="${url.registrationUrl}">${msg("doRegister")}</a></span>
                </div>
            </div>
        </#if>
    </#if>

</@layout.registrationLayout>

<script>
  document.addEventListener('DOMContentLoaded', function () {
    const OIDC_PARAMS = ['client_id', 'redirect_uri', 'state', 'response_type', 'scope'];
    const currentUrl = new URL(window.location.href);
    // A simple check to see if the current URL is the initial authentication request.
    const isInitialAuth = OIDC_PARAMS.every(param => currentUrl.searchParams.has(param));

    // When the login page first loads, it contains all the OIDC parameters.
    // We save this initial URL to session storage so we can restart the flow correctly later.
    if (isInitialAuth) {
      sessionStorage.setItem('initialAuthUrl', currentUrl.href);
    }

    const stagLoginButton = document.getElementById('stag-login-button');
    if (stagLoginButton) {
      stagLoginButton.addEventListener('click', function (e) {
        e.preventDefault();
        try {
          // Retrieve the saved initial URL. This will be used as the base for our redirect.
          const initialAuthUrl = sessionStorage.getItem('initialAuthUrl');

          if (!initialAuthUrl) {
            console.error('Initial authentication URL not found in session storage. Cannot proceed with IS/STAG login.');
            // You could display an error to the user here if desired.
            return;
          }

          const redirectUrl = new URL(initialAuthUrl);
          redirectUrl.searchParams.set('stag_login', 'true');

          // By redirecting to the full initial URL with the added parameter,
          // we restart the authentication flow correctly, preserving the OIDC context.
          window.location.href = redirectUrl.toString();
        } catch (error) {
          console.error('Failed to construct IS/STAG login redirect URL:', error);
        }
      });
    }
  });
</script>