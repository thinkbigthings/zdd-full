package org.thinkbigthings.zdd.server.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;

public class SingleTokenRememberMeServices extends PersistentTokenBasedRememberMeServices {

    protected PersistentTokenRepository tokenRepositorySubclassAccessor;

    /**
     * If true, each token authentication returns a new token for the subsequent call.
     * That is safer from a security point of view but risks a false positive for cookie theft
     * if the caller makes simultaneous requests.
     */
    protected boolean newTokenOnAutoLogin = true;

    public SingleTokenRememberMeServices(String key, UserDetailsService userDetailsService, PersistentTokenRepository tokenRepository) {
        super(key, userDetailsService, tokenRepository);
        tokenRepositorySubclassAccessor = tokenRepository;
    }

    public boolean isNewTokenOnAutoLogin() {
        return newTokenOnAutoLogin;
    }

    public void setNewTokenOnAutoLogin(boolean newTokenOnAutoLogin) {
        this.newTokenOnAutoLogin = newTokenOnAutoLogin;
    }

    /**
     * This is a copy of PersistentTokenBasedRememberMeServices that can determine whether to generate new tokens.
     *
     * @param cookieTokens
     * @param request
     * @param response
     * @return
     */
    protected UserDetails processAutoLoginCookie(String[] cookieTokens,
                                                 HttpServletRequest request, HttpServletResponse response) {

        if (cookieTokens.length != 2) {
            throw new InvalidCookieException("Cookie token did not contain " + 2
                    + " tokens, but contained '" + Arrays.asList(cookieTokens) + "'");
        }

        final String presentedSeries = cookieTokens[0];
        final String presentedToken = cookieTokens[1];

        PersistentRememberMeToken token = tokenRepositorySubclassAccessor.getTokenForSeries(presentedSeries);

        if (token == null) {
            // No series match, so we can't authenticate using this cookie
            throw new RememberMeAuthenticationException(
                    "No persistent token found for series id: " + presentedSeries);
        }

        // We have a match for this user/series combination
        if (!presentedToken.equals(token.getTokenValue())) {
            // Token doesn't match series value. Delete all logins for this user and throw
            // an exception to warn them.
            tokenRepositorySubclassAccessor.removeUserTokens(token.getUsername());

            throw new CookieTheftException(
                    messages.getMessage(
                            "PersistentTokenBasedRememberMeServices.cookieStolen",
                            "Invalid remember-me token (Series/token) mismatch. Implies previous cookie theft attack."));
        }

        if (token.getDate().getTime() + getTokenValiditySeconds() * 1000L < System.currentTimeMillis()) {
            throw new RememberMeAuthenticationException("Remember-me login has expired");
        }

        // Token also matches, so login is valid. Update the token value, keeping the
        // *same* series number.
        if (logger.isDebugEnabled()) {
            logger.debug("Refreshing persistent login token for user '"
                    + token.getUsername() + "', series '" + token.getSeries() + "'");
        }

        try {
            String tokenValue = newTokenOnAutoLogin ? generateTokenData() : token.getTokenValue();
            tokenRepositorySubclassAccessor.updateToken(token.getSeries(), tokenValue, new Date());
            setCookie(new String[]{token.getSeries(), token.getTokenValue()}, getTokenValiditySeconds(), request, response);
        } catch (Exception e) {
            logger.error("Failed to update token: ", e);
            throw new RememberMeAuthenticationException(
                    "Autologin failed due to data access problem");
        }

        return getUserDetailsService().loadUserByUsername(token.getUsername());
    }

}
