package org.thinkbigthings.zdd.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thinkbigthings.zdd.dto.UserRecord;

import java.util.HashSet;

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
    public void testRecords() {

        UserRecord user = new UserRecord("a", "b", null,
                "a@b", "asdf", "1234", 99,
                new HashSet<>(), new HashSet<>());

        String s = user.toString();

        String name = "newuserhere";

//        UserDTO newUser = new UserDTO();
//
//        newUser.username = name;
//        newUser.email = name + "@email.com";
//
//        UserDTO created = service.saveNewUser(newUser);
//
//        assertEquals(name, created.username);
    }

    @Test
    public void createUser() {

        String name = "newuserhere";

        UserRecord newUser = new UserRecord(name, "b", null
                , name + "@email.com", "asdf", "1234", 99, new HashSet<>(), new HashSet<>());

        UserRecord created = service.saveNewUser(newUser);

        assertEquals(name, created.username());
    }

    @Test
    public void createUserWithBadName() {

        String name = "first last";

        UserRecord newUser = new UserRecord(name, "b", null,
                name + "@email.com", "asdf", "1234", 99,
                new HashSet<>(), new HashSet<>());

        assertThrows(IllegalArgumentException.class, () -> service.saveNewUser(newUser));
    }

}
