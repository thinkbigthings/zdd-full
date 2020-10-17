package org.thinkbigthings.zdd.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public record User(@JsonProperty("username") String username,
                   @JsonProperty("registrationTime") String registrationTime,
                   @JsonProperty("roles") Set<String> roles,
                   @JsonProperty("personalInfo") PersonalInfo personalInfo,
                   @JsonProperty("isLoggedIn") boolean isLoggedIn) {

    public User withIsLoggedIn(boolean newValue) {
        return new User(username(), registrationTime(), roles(), personalInfo(), newValue);
    }
}

