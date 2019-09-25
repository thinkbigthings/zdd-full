package org.thinkbigthings.zdd.dto;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class UserDTO {

    public String username = "";
    public String email = "";
    public String displayName = "";
    public String phoneNumber = "";
    public String registrationTime = "";
    public int heightCm = 0;
    public Set<AddressDTO> addresses = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return heightCm == userDTO.heightCm &&
                username.equals(userDTO.username) &&
                email.equals(userDTO.email) &&
                displayName.equals(userDTO.displayName) &&
                phoneNumber.equals(userDTO.phoneNumber) &&
                registrationTime.equals(userDTO.registrationTime) &&
                addresses.equals(userDTO.addresses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email, displayName, phoneNumber, registrationTime, heightCm, addresses);
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", displayName='" + displayName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", registrationTime='" + registrationTime + '\'' +
                ", heightCm=" + heightCm +
                ", addresses=" + addresses +
                '}';
    }
}
