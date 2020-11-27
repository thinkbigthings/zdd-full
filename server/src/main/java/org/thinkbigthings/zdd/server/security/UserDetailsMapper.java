package org.thinkbigthings.zdd.server.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.thinkbigthings.zdd.server.entity.User;

import static java.util.stream.Collectors.toList;
import static org.springframework.security.core.userdetails.User.builder;

import java.util.Set;
import java.util.function.Function;

public class UserDetailsMapper implements Function<User, UserDetails> {

    @Override
    public UserDetails apply(User user) {

        return builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .disabled( ! user.isEnabled())
                .roles(toNames(user.getRoles()))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .build();
    }

    public String[] toNames(Set<User.Role> roles) {
        return roles.stream()
                .map(User.Role::name)
                .collect(toList())
                .toArray(new String[]{});
    }

}
