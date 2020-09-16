package org.thinkbigthings.zdd.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Set;

public record RegistrationRequest(@JsonProperty("username") String username,
                                  @JsonProperty("plainTextPassword") String plainTextPassword,
                                  @JsonProperty("personalInfo") PersonalInfo personalInfo) {



}

