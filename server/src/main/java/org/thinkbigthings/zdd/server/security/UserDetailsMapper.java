package org.thinkbigthings.zdd.server.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.thinkbigthings.zdd.server.User;
import static org.springframework.security.core.userdetails.User.builder;

import java.util.function.Function;

public class UserDetailsMapper implements Function<User, UserDetails> {

    @Override
    public UserDetails apply(User user) {

        // TODO add rest of this data to database
        // we have a user enabled flag, but it is always set to false and otherwise unused

        return builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .roles("ADMIN", "USER")
                .build();
    }
}
