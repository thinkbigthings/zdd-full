package org.thinkbigthings.zdd.server;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.thinkbigthings.zdd.dto.AddressDTO;
import org.thinkbigthings.zdd.dto.UserDTO;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;


@Service
public class UserService {

    private UserRepository userRepo;
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository repo, PasswordEncoder passwordEncoder) {
        this.userRepo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void updatePassword(String username, String newPassword) {

        var user = userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("no user found for " + username));

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepo.save(user);
    }

    @Transactional
    public UserDTO updateUser(String username, UserDTO userDto) {

        var user = userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("no user found for " + username));

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
            throw new IllegalArgumentException("Username must be url-safe");
        }

        if(userRepo.existsByUsername(userDto.username)) {
            throw new IllegalArgumentException("Username already exists " + userDto.username);
        }

        var user = fromDto(userDto);
        user.setRegistrationTime(Instant.now());
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode(userDto.plainTextPassword));
        user.getRoles().add(User.Role.USER);

        try {
            return toDto(userRepo.save(user));
        }
        catch(ConstraintViolationException e) {
            e.getConstraintViolations().forEach(System.out::println);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User can't be saved: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getUsers(Pageable page) {
        return userRepo.findAll(page).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public UserDTO getUser(String username) {

        return userRepo.findByUsername(username)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException("no user found for " + username));
    }

    public User fromDto(UserDTO userData) {

        var user = new User(userData.username, userData.displayName);

        user.setEmail(userData.email);
        user.setPhoneNumber(userData.phoneNumber);
        user.setHeightCm(userData.heightCm);

        userData.addresses.stream()
                .map(this::fromDto)
                .peek(a -> a.setUser(user))
                .collect(toCollection(() -> user.getAddresses()));

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
                .collect(toCollection(() -> userData.addresses));

        user.getRoles().stream()
                .map(User.Role::name)
                .collect(toCollection(() -> userData.roles));

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
