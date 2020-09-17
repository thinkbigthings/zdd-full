package org.thinkbigthings.zdd.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Set;

public record PersonalInfo(@JsonProperty("email") String email,
                           @JsonProperty("displayName") String displayName,
                           @JsonProperty("phoneNumber") String phoneNumber,
                           @JsonProperty("heightCm") int heightCm,
                           @JsonProperty("addresses") Set<AddressRecord> addresses) {



}

