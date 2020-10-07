package org.thinkbigthings.zdd.server.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.RememberMeServices;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private RememberMeServices rememberMeServices;

    public WebSecurityConfig(RememberMeServices rememberMeServices) {
        this.rememberMeServices = rememberMeServices;
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
            .rememberMe()
                .rememberMeServices(rememberMeServices)
                .and()
            .csrf()
                .disable()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER) // Spring Security doesn't create it, so lets Spring Session create it?
                .and()
            .exceptionHandling()
                .accessDeniedHandler((req, resp, e) -> e.printStackTrace() )
                .and()
            .logout()
                .logoutSuccessHandler((req, rep, auth) -> System.out.println("Logout success"));
    }

}