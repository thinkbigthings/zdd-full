package org.thinkbigthings.zdd.dto;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class UserDTO {

    public enum RoleDTO {
        // these are stored by ordinal in the database, so don't change the order!
        ADMIN, USER
    }

    public String username = "";
    public String email = "";
    public String displayName = "";
    public String phoneNumber = "";
    public String registrationTime = "";
    public int heightCm = 0;
    public Set<AddressDTO> addresses = new HashSet<>();
    public Set<String> roles = new HashSet<>();

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
                addresses.equals(userDTO.addresses) &&
                roles.equals(userDTO.roles);
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
                ", roles=" + roles +
                '}';
    }
}
