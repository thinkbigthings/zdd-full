package org.thinkbigthings.zdd.server.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.*;

import javax.sql.DataSource;

import static org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY;
import static org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices.TWO_WEEKS_S;

@Configuration
public class SecurityBeans {

    @Bean
    public PersistentTokenBasedRememberMeServices createRememberMeServices(AppUserDetailsService userDetails,
                                                                           PersistentTokenRepository tokenRepository)
    {
        PersistentTokenBasedRememberMeServices rememberMeServices;
        rememberMeServices = new PersistentTokenBasedRememberMeServices("appkey", userDetails, tokenRepository);

        rememberMeServices.setAlwaysRemember(true);
        rememberMeServices.setUseSecureCookie(true);
        rememberMeServices.setTokenValiditySeconds(TWO_WEEKS_S);
        rememberMeServices.setCookieName(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY);

        return rememberMeServices;
    }

    @Bean
    public PersistentTokenRepository createPersistentTokenRepository(DataSource dataSource) {

        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setCreateTableOnStartup(false);
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

    @Bean
    public PasswordEncoder createPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
