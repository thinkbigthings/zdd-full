package org.thinkbigthings.zdd.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.server.ResponseStatusException;
import org.thinkbigthings.zdd.dto.AddressRecord;
import org.thinkbigthings.zdd.dto.PersonalInfo;
import org.thinkbigthings.zdd.dto.RegistrationRequest;
import org.thinkbigthings.zdd.server.entity.User;

import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private UserRepository userRepo = Mockito.mock(UserRepository.class);
    private PasswordEncoder pwEncoder = Mockito.mock(PasswordEncoder.class);

    private String savedUsername = "saveduser";
    private User savedUser = new User(savedUsername, savedUsername);
    private String strongPasswordHash = "strongencryptedpasswordhere";

    private UserService service;

    @BeforeEach
    public void setup() {

        service = new UserService(userRepo, pwEncoder);

        savedUser.setRegistrationTime(Instant.now());

        when(userRepo.save(ArgumentMatchers.any(User.class))).then(AdditionalAnswers.returnsFirstArg());
        when(userRepo.saveAndFlush(ArgumentMatchers.any(User.class))).then(AdditionalAnswers.returnsFirstArg());
        when(userRepo.findByUsername(eq(savedUser.getUsername()))).thenReturn(of(savedUser));
        when(pwEncoder.encode(ArgumentMatchers.any(String.class))).thenReturn(strongPasswordHash);
    }

    @Test
    public void updateUser() {

        Set<AddressRecord> addresses = Set.of(new AddressRecord("123 A St", "Philadelphia", "PA", "19109"));
        PersonalInfo updateInfo = new PersonalInfo("update@email.com", savedUsername+"1", "1234567890", 160, addresses);

        org.thinkbigthings.zdd.dto.User updatedUser = service.updateUser(savedUsername, updateInfo);

        assertEquals(updateInfo, updatedUser.personalInfo());
    }

    @Test
    public void createUser() {

        String name = "newuserhere";
        RegistrationRequest register = new RegistrationRequest(name, "b", "name@email.com");

        org.thinkbigthings.zdd.dto.User created = service.saveNewUser(register);

        assertEquals(name, created.username());
    }

    @Test
    public void getUser() {

        org.thinkbigthings.zdd.dto.User foundUser = service.getUser(savedUsername);

        assertEquals(savedUser.getUsername(), foundUser.username());
        assertEquals(savedUser.getDisplayName(), foundUser.personalInfo().displayName());
        assertEquals(savedUser.getEmail(), foundUser.personalInfo().email());
    }


    @Test
    public void updatePassword() {

        service.updatePassword(savedUsername, "newpassword");

        assertEquals(strongPasswordHash, savedUser.getPassword());
    }

    @Test
    public void blockDuplicateUsername() {

        when(userRepo.existsByUsername(ArgumentMatchers.any(String.class))).thenReturn(true);

        RegistrationRequest register = new RegistrationRequest("username", "b", "name@email.com");

        assertThrows(ResponseStatusException.class, () -> service.saveNewUser(register));
    }

    @Test
    public void blockConstraintViolationOnUpdate() {

        when(userRepo.save(ArgumentMatchers.any(User.class))).thenThrow(new ConstraintViolationException(new HashSet<>()));

        PersonalInfo updateInfo = new PersonalInfo("a", savedUsername+"1", "123", 160, new HashSet<>());

        assertThrows(ResponseStatusException.class, () -> service.updateUser(savedUsername, updateInfo));
    }

    @Test
    public void blockConstraintViolationOnRegister() {

        when(userRepo.save(ArgumentMatchers.any(User.class))).thenThrow(new ConstraintViolationException(new HashSet<>()));

        RegistrationRequest register = new RegistrationRequest("username", "b", "name@email.com");

        assertThrows(ResponseStatusException.class, () -> service.saveNewUser(register));
    }

    @Test
    public void blockUrlUnsafeUsername() {

        String name = "first last";

        RegistrationRequest register = new RegistrationRequest(name, "b", "name@email.com");

        assertThrows(ResponseStatusException.class, () -> service.saveNewUser(register));
    }

}
