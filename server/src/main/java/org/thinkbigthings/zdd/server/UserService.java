package org.thinkbigthings.zdd.server;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.thinkbigthings.zdd.dto.AddressDTO;
import org.thinkbigthings.zdd.dto.UserDTO;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class UserService {


    private final UserRepository userRepo;

    public UserService(UserRepository repo) {
        userRepo = repo;
    }

    public UserDTO updateUser(String username, UserDTO userDto) {


        // TODO on update should not create new entity since that's an unnecessary instantiation and mapping

        var userData = fromDto(userDto);
        var user = userRepo.findByUsername(username);

        user.setEmail(userData.getEmail());
        user.setDisplayName(userData.getDisplayName());
        user.setPhoneNumber(userData.getPhoneNumber());
        user.setHeightCm(userData.getHeightCm());

        user.getAddresses().forEach(a -> a.setUser(null));
        user.getAddresses().clear();

        user.getAddresses().addAll(userData.getAddresses());
        user.getAddresses().forEach(a -> a.setUser(user));

        return toDto(userRepo.save(user));
    }

    public UserDTO saveNewUser(UserDTO userDto) {

        var user = fromDto(userDto);
        user.setRegistrationTime(Instant.now());
        user.setEnabled(true);

        return toDto(userRepo.save(user));
    }

    public List<UserDTO> getUsers() {

        return userRepo.findRecent().stream().map(this::toDto).collect(Collectors.toList());

    }

    public UserDTO getUser(String username) {

        return toDto(userRepo.findByUsername(username));
    }

    public User fromDto(UserDTO userData) {

        var user = new User(userData.username, userData.displayName);

        user.setEmail(userData.email);
        user.setPhoneNumber(userData.phoneNumber);
        user.setHeightCm(userData.heightCm);

        userData.addresses.stream()
                .map(this::fromDto)
                .peek(a -> a.setUser(user))
                .collect(Collectors.toCollection(() -> user.getAddresses()));

        return user;
    }



    public UserDTO toDto(User user) {

        var userData = new UserDTO();

        userData.displayName = user.getDisplayName();
        userData.email = user.getEmail();
        userData.username = user.getUsername();
        userData.registrationTime = user.getRegistrationTime().toString();
        userData.phoneNumber = user.getPhoneNumber();
        userData.heightCm = user.getHeightCm();

        user.getAddresses().stream()
                .map(this::toDto)
                .collect(Collectors.toCollection(() -> userData.addresses));

        return userData;
    }

    public Address fromDto(AddressDTO addressData) {

        var address = new Address();

        address.setLine1(addressData.line1);
        address.setCity(addressData.city);
        address.setState(addressData.state);
        address.setZip(addressData.zip);

        return address;
    }

    public AddressDTO toDto(Address address) {

        var addressData = new AddressDTO();

        addressData.line1 = address.getLine1();
        addressData.city = address.getCity();
        addressData.state = address.getState();
        addressData.zip = address.getZip();

        return addressData;
    }


}
