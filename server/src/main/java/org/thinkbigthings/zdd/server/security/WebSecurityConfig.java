package org.thinkbigthings.zdd.server.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public WebSecurityConfig() {
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
            .csrf()
                .disable()
            .exceptionHandling()
                .accessDeniedHandler((req, resp, e) -> e.printStackTrace() )
                .and()
            .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true);

    }
}
