package org.thinkbigthings.zdd.dto;

import java.util.HashSet;
import java.util.Set;

public record User(String username,
                   String registrationTime,
                   Set<String> roles,
                   PersonalInfo personalInfo,
                   boolean isLoggedIn) {

    public User() {
        this("", "", new HashSet<>(), new PersonalInfo(), false);
    }

    public User withIsLoggedIn(boolean newValue) {
        return new User(username(), registrationTime(), roles(), personalInfo(), newValue);
    }
}

