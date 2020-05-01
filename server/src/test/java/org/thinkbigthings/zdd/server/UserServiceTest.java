package org.thinkbigthings.zdd.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thinkbigthings.zdd.dto.UserDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServiceTest {

    private UserRepository userRepo = Mockito.mock(UserRepository.class);
    private PasswordEncoder pwEncoder = Mockito.mock(PasswordEncoder.class);

    private UserService service;

    @BeforeEach
    public void setup() {
        service = new UserService(userRepo, pwEncoder);

        Mockito.when(userRepo.save(ArgumentMatchers.any(User.class))).then(AdditionalAnswers.returnsFirstArg());
        Mockito.when(userRepo.saveAndFlush(ArgumentMatchers.any(User.class))).then(AdditionalAnswers.returnsFirstArg());

    }

    @Test
    public void createUser() {

        String name = "newuserhere";
        UserDTO newUser = new UserDTO();

        newUser.username = name;
        newUser.email = name + "@email.com";

        UserDTO created = service.saveNewUser(newUser);

        assertEquals(name, created.username);
    }

    @Test
    public void createUserWithBadName() {

        String name = "first last";
        UserDTO newUser = new UserDTO();

        newUser.username = name;
        newUser.email = name + "@email.com";

        assertThrows(IllegalArgumentException.class, () -> service.saveNewUser(newUser));
    }

}
