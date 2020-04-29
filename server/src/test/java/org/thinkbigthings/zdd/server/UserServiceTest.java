package org.thinkbigthings.zdd.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.thinkbigthings.zdd.dto.UserDTO;

import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserServiceTest {


    private UserRepository userRepo = Mockito.mock(UserRepository.class);

    private UserService service;

    @BeforeEach
    public void setup() {
        service = new UserService(userRepo);

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

}
