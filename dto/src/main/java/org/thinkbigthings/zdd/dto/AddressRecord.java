package org.thinkbigthings.zdd.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddressRecord(@JsonProperty("line1") String line1,
                            @JsonProperty("city") String city,
                            @JsonProperty("state") String state,
                            @JsonProperty("zip") String zip) {}