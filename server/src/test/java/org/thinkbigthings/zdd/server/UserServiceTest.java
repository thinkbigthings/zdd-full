package org.thinkbigthings.zdd.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thinkbigthings.zdd.dto.AddressRecord;
import org.thinkbigthings.zdd.dto.PersonalInfo;
import org.thinkbigthings.zdd.dto.RegistrationRequest;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;
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
    public void testRecordSerialization() throws Exception {

        AddressRecord userInfo = new AddressRecord("a", null,"PA", "12345");

        String serializedRecord = Paths.get("build", "serial.data").toString();
        try(var oos = new ObjectOutputStream(new FileOutputStream(serializedRecord))) {
            oos.writeObject(userInfo);
        }
        try(var ois = new ObjectInputStream(new FileInputStream(serializedRecord))) {
            System.out.println(ois.readObject());
        }

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

        assertThrows(IllegalArgumentException.class, () -> service.saveNewUser(register));
    }

}
