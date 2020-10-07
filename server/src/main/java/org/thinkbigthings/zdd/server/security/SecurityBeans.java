package org.thinkbigthings.zdd.server.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.*;

@Configuration
public class SecurityBeans {

    @Bean
    public PersistentTokenBasedRememberMeServices createRememberMeServices(AppUserDetailsService userDetailsService, PersistentTokenRepository tokenRepository) {

        PersistentTokenBasedRememberMeServices rememberMeServices = new PersistentTokenBasedRememberMeServices("appkey", userDetailsService, tokenRepository);

        rememberMeServices.setAlwaysRemember(true);
        rememberMeServices.setUseSecureCookie(true);
        rememberMeServices.setTokenValiditySeconds(AbstractRememberMeServices.TWO_WEEKS_S);
        // rememberMeServices.setCookieName();

        return rememberMeServices;
    }

    @Bean
    public PersistentTokenRepository createPersistentTokenRepository() {
        return new InMemoryTokenRepositoryImpl();
    }

    @Bean
    public PasswordEncoder createPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
