package org.thinkbigthings.zdd.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record UserRecord(@JsonProperty("username") String username,
                        @JsonProperty("plainTextPassword") String plainTextPassword,
                        @JsonProperty("registrationTime") String registrationTime,
                        @JsonProperty("email") String email,
                        @JsonProperty("displayName") String displayName,
                        @JsonProperty("phoneNumber") String phoneNumber,
                        @JsonProperty("heightCm") int heightCm,
                        @JsonProperty("addresses") Set<AddressRecord> addresses,
                        @JsonProperty("roles") Set<String> roles) {



}

