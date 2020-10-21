package org.thinkbigthings.zdd.server.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.thinkbigthings.zdd.server.entity.User;

import static org.springframework.security.core.userdetails.User.builder;

import java.util.function.Function;

public class UserDetailsMapper implements Function<User, UserDetails> {

    @Override
    public UserDetails apply(User user) {

        return builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .disabled( ! user.isEnabled())
                .roles(user.mapRoleNames())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .build();
    }
}
