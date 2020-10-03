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
        SingleTokenRememberMeServices rememberMeServices = new SingleTokenRememberMeServices("appkey", userDetailsService, tokenRepository);
        rememberMeServices.setAlwaysRemember(true);
        rememberMeServices.setUseSecureCookie(true);
        // rememberMeServices.setCookieName();
        rememberMeServices.setTokenValiditySeconds(AbstractRememberMeServices.TWO_WEEKS_S);
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
