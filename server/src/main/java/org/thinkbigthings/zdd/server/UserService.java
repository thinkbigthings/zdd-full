package org.thinkbigthings.zdd.server;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.thinkbigthings.zdd.dto.AddressDTO;
import org.thinkbigthings.zdd.dto.UserDTO;

import javax.validation.ConstraintViolationException;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
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

        try {
            return toDto(userRepo.save(user));
        }
        catch(ConstraintViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User can't be saved: " + e.getMessage());
        }
    }

    @Transactional
    public UserDTO saveNewUser(UserDTO userDto) {

        if( ! URLEncoder.encode(userDto.username, UTF_8).equals(userDto.username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username must be url-safe");
        }

        if(userRepo.existsByUsername(userDto.username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists " + userDto.username);
        }

        var user = fromDto(userDto);
        user.setRegistrationTime(Instant.now());
        user.setEnabled(true);

        try {
            return toDto(userRepo.save(user));
        }
        catch(ConstraintViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User can't be saved: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getUsers(Pageable page) {
        return userRepo.findAll(page).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public UserDTO getUser(String username) {

        if( ! userRepo.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Username does not exist" + username);
        }

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
