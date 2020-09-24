package org.thinkbigthings.zdd.server;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.thinkbigthings.zdd.dto.AddressRecord;
import org.thinkbigthings.zdd.dto.PersonalInfo;
import org.thinkbigthings.zdd.dto.RegistrationRequest;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.*;


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
    public org.thinkbigthings.zdd.dto.User updateUser(String username, PersonalInfo userData) {

        var user = userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("no user found for " + username));

        user.setEmail(userData.email());
        user.setDisplayName(userData.displayName());
        user.setPhoneNumber(userData.phoneNumber());
        user.setHeightCm(userData.heightCm());

        user.getAddresses().forEach(a -> a.setUser(null));
        user.getAddresses().clear();

        List<Address> newAddressEntities = userData.addresses().stream().map(this::fromRecord).collect(toList());
        user.getAddresses().addAll(newAddressEntities);
        user.getAddresses().forEach(a -> a.setUser(user));

        try {
            return toRecord(userRepo.save(user));
        }
        catch(ConstraintViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User can't be saved: " + e.getMessage());
        }
    }

    @Transactional
    public org.thinkbigthings.zdd.dto.User saveNewUser(RegistrationRequest registration) {

        String username = registration.username();

        if( ! URLEncoder.encode(username, UTF_8).equals(username)) {
            throw new IllegalArgumentException("Username must be url-safe");
        }

        if(userRepo.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists " + registration.username());
        }

        try {
            return toRecord(userRepo.save(fromRegistration(registration)));
        }
        catch(ConstraintViolationException e) {
            e.getConstraintViolations().forEach(System.out::println);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User can't be saved: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Page<org.thinkbigthings.zdd.dto.User> getUsers(Pageable page) {
        return userRepo.findAll(page).map(this::toRecord);
    }

    @Transactional(readOnly = true)
    public org.thinkbigthings.zdd.dto.User getUser(String username) {

        return userRepo.findByUsername(username)
                .map(this::toRecord)
                .orElseThrow(() -> new EntityNotFoundException("no user found for " + username));
    }

    @Transactional(readOnly = true)
    public PersonalInfo getUserInfo(String username) {

        return userRepo.findByUsername(username)
                .map(this::toPersonalInfoRecord)
                .orElseThrow(() -> new EntityNotFoundException("no user found for " + username));
    }

    public PersonalInfo toPersonalInfoRecord(User user) {

        Set<AddressRecord> addresses = user.getAddresses().stream()
                .map(this::toUserRecord)
                .collect(toSet());

        return new PersonalInfo(user.getEmail(),
                user.getDisplayName(),
                user.getPhoneNumber(),
                user.getHeightCm(),
                addresses);
    }

    public org.thinkbigthings.zdd.dto.User toRecord(User user) {

        Set<String> roles = user.getRoles().stream()
                .map(User.Role::name)
                .collect(toSet());

        return new org.thinkbigthings.zdd.dto.User( user.getUsername(),
                user.getRegistrationTime().toString(),
                roles,
                toPersonalInfoRecord(user));
    }

    public AddressRecord toUserRecord(Address address) {
        return new AddressRecord(address.getLine1(),
                address.getCity(),
                address.getState(),
                address.getZip());
    }

    public User fromRegistration(RegistrationRequest registration) {

        var user = new User(registration.username(), registration.username());

        user.setDisplayName(registration.username());
        user.setEmail(registration.email());
        user.setRegistrationTime(Instant.now());
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode(registration.plainTextPassword()));
        user.getRoles().add(User.Role.USER);

        return user;
    }

    public Address fromRecord(AddressRecord addressData) {

        var address = new Address();

        address.setLine1(addressData.line1());
        address.setCity(addressData.city());
        address.setState(addressData.state());
        address.setZip(addressData.zip());

        return address;
    }

}
