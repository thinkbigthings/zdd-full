package org.thinkbigthings.zdd.server;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thinkbigthings.zdd.dto.AddressDTO;
import org.thinkbigthings.zdd.dto.UserDTO;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


@Service
public class UserService {


    private final UserRepository userRepo;

    public UserService(UserRepository repo) {
        userRepo = repo;
    }

    @Transactional
    public UserDTO updateUser(String username, UserDTO userDto) {

        var user = userRepo.findByUsername(username);

        user.setEmail(userDto.email);
        user.setDisplayName(userDto.displayName);
        user.setPhoneNumber(userDto.phoneNumber);
        user.setHeightCm(userDto.heightCm);

        user.getAddresses().forEach(a -> a.setUser(null));
        user.getAddresses().clear();

        List<Address> newAddressEntities = userDto.addresses.stream().map(this::fromDto).collect(toList());
        user.getAddresses().addAll(newAddressEntities);
        user.getAddresses().forEach(a -> a.setUser(user));

        return toDto(userRepo.save(user));
    }

    @Transactional
    public UserDTO saveNewUser(UserDTO userDto) {

        if(userRepo.existsByUsername(userDto.username)) {
            throw new IllegalArgumentException("user already exists " + userDto.username);
        }

        var user = fromDto(userDto);
        user.setRegistrationTime(Instant.now());
        user.setEnabled(true);

        return toDto(userRepo.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getUsers() {

        return userRepo.findRecent().stream().map(this::toDto).collect(toList());
    }

    @Transactional(readOnly = true)
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
