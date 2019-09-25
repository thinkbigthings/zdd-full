package org.thinkbigthings.zdd.dto;

import java.util.Objects;

public class AddressDTO {

    public String line1 = "";
    public String city = "";
    public String state = "";
    public String zip = "";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressDTO that = (AddressDTO) o;
        return line1.equals(that.line1) &&
                city.equals(that.city) &&
                state.equals(that.state) &&
                zip.equals(that.zip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line1, city, state, zip);
    }

    @Override
    public String toString() {
        return "AddressDTO{" +
                "line1='" + line1 + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zip='" + zip + '\'' +
                '}';
    }
}
