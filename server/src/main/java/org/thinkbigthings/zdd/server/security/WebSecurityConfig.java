package org.thinkbigthings.zdd.server.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private AppUserDetailsService userDetailsService;
//    private PersistentTokenRepository persistentTokenRepository;
//    RememberMeServices

    public WebSecurityConfig(AppUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    private static final String[] OPEN_ENDPOINTS = new String[]{
            "/", "/static/**", "/*.png", "/favicon.ico", "/manifest.json", "/actuator/**"
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers(OPEN_ENDPOINTS).permitAll()
                    .anyRequest().authenticated()
                    .and()
                .httpBasic()
                    .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .rememberMe()
                    .alwaysRemember(true)
                    .useSecureCookie(true)
                    .tokenRepository(new InMemoryTokenRepositoryImpl())
                    .userDetailsService(userDetailsService)
//                    .rememberMeParameter("remember me token name goes here")
//                    .rememberMeServices()
                    .and()
                .csrf()
                    .disable();
    }


    @Bean
    public PasswordEncoder createPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}