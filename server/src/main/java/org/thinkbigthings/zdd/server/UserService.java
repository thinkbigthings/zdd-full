package org.thinkbigthings.zdd.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.thinkbigthings.zdd.dto.UserSummary;
import org.thinkbigthings.zdd.server.entity.Address;
import org.thinkbigthings.zdd.server.entity.User;

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

    private static Logger LOG = LoggerFactory.getLogger(UserService.class);

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

        List<Address> newAddressEntities = userData.addresses().stream()
                .map(this::fromRecord)
                .collect(toList());

        user.getAddresses().clear();
        user.getAddresses().addAll(newAddressEntities);
        newAddressEntities.forEach(a -> a.setUser(user));

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

        try {
            if( ! URLEncoder.encode(username, UTF_8).equals(username)) {
                throw new IllegalArgumentException("Username must be url-safe");
            }

            if(userRepo.existsByUsername(username)) {
                throw new IllegalArgumentException("Username already exists " + registration.username());
            }

            return toRecord(userRepo.save(fromRegistration(registration)));
        }
        catch(ConstraintViolationException e) {
            String constraintMessage = "User can't be saved: " + e.getMessage();
            String list = e.getConstraintViolations().stream().map(v -> v.toString()).collect(joining(", "));
            constraintMessage += " " + list;
            LOG.error(constraintMessage, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, constraintMessage);
        }
        catch(IllegalArgumentException e) {
            String constraintMessage = "User can't be saved: " + e.getMessage();
            LOG.error(constraintMessage, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, constraintMessage);
        }
    }

    @Transactional(readOnly = true)
    public Page<UserSummary> getUserSummaries(Pageable page) {

        return userRepo.loadSummaries(page);
    }

    @Transactional(readOnly = true)
    public org.thinkbigthings.zdd.dto.User getUser(String username) {

        return userRepo.findByUsername(username)
                .map(this::toRecord)
                .orElseThrow(() -> new EntityNotFoundException("no user found for " + username));
    }

    public PersonalInfo toPersonalInfoRecord(User user) {

        Set<AddressRecord> addresses = user.getAddresses().stream()
                .map(this::toAddressRecord)
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
                toPersonalInfoRecord(user),
                user.getSessions().size() > 0);
    }

    public AddressRecord toAddressRecord(Address address) {
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
