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

import java.time.Instant;
import java.util.Set;

import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;

public class UserServiceTest {

    private UserRepository userRepo = Mockito.mock(UserRepository.class);
    private PasswordEncoder pwEncoder = Mockito.mock(PasswordEncoder.class);

    private UserService service;

    @BeforeEach
    public void setup() {
        service = new UserService(userRepo, pwEncoder);

        // in general, saving an object returns the same object
        Mockito.when(userRepo.save(ArgumentMatchers.any(User.class))).then(AdditionalAnswers.returnsFirstArg());
        Mockito.when(userRepo.saveAndFlush(ArgumentMatchers.any(User.class))).then(AdditionalAnswers.returnsFirstArg());
    }

    @Test
    public void updateUser() {

        String username = "usernamehere";
        User savedUser = new User(username, username);
        savedUser.setRegistrationTime(Instant.now());

        Mockito.when(userRepo.findByUsername(eq(username))).thenReturn(of(savedUser));

        Set<AddressRecord> addresses = Set.of(new AddressRecord("123 A St", "Philadelphia", "PA", "19109"));
        PersonalInfo updateInfo = new PersonalInfo("update@email.com", username+"1", "1234567890", 160, addresses);

        org.thinkbigthings.zdd.dto.User updatedUser = service.updateUser(username, updateInfo);

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
    public void createUserWithBadName() {

        String name = "first last";

        RegistrationRequest register = new RegistrationRequest(name, "b", "name@email.com");

        assertThrows(ResponseStatusException.class, () -> service.saveNewUser(register));
    }

}
