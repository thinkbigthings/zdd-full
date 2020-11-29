package org.thinkbigthings.zdd.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;
import org.thinkbigthings.zdd.client.ApiClientStateful;
import org.thinkbigthings.zdd.dto.AddressRecord;
import org.thinkbigthings.zdd.dto.PersonalInfo;
import org.thinkbigthings.zdd.dto.RegistrationRequest;
import org.thinkbigthings.zdd.dto.User;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// With Junit 5, we do not need @RunWith(SpringRunner.class) anymore.
// Spring tests are executed with @ExtendWith(SpringExtension.class),
// and @SpringBootTest, and the other @…Test annotations are already annotated with it.


@Tag("integration")
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    private Random random = new Random();
    private Faker faker = new Faker(Locale.US, new Random());

    private String baseUrl;
    private URI users;

    @Autowired
    private UserController controller;

    @LocalServerPort
    private int randomServerPort;

    @BeforeEach
    public void setupClass() {
        baseUrl = "https://localhost:" + randomServerPort;
        users = URI.create(baseUrl + "/user");
    }


    @Test
    @DisplayName("Basic Spring wiring")
    public void contextLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    @DisplayName("Register and manage new user")
    public void testRegisterNewUser() throws URISyntaxException {

        ApiClientStateful adminClient = new ApiClientStateful(baseUrl, "admin", "admin");

        RegistrationRequest registrationRequest = createRandomUserRegistration();
        String username = registrationRequest.username();
        String password = registrationRequest.plainTextPassword();

        URI userUrl = URI.create(users + "/" + username);
        URI updatePasswordUrl = URI.create(userUrl + "/password/update");
        URI infoUrl = URI.create(userUrl + "/personalInfo");
        URI registration = URI.create(baseUrl + "/registration");

        System.out.println("registering and logging in " + username);
        adminClient.post(registration, registrationRequest);
        ApiClientStateful newClient = new ApiClientStateful(baseUrl, username, password);

        // create user info and save it
        var updatedInfo = randomPersonalInfo();
        newClient.put(infoUrl, updatedInfo);
        PersonalInfo retrievedInfo = newClient.get(userUrl, User.class).personalInfo();
        assertEquals(updatedInfo, retrievedInfo);

        // change the user's password and make a new call with it
        String newPassword = "password";
        newClient.post(updatePasswordUrl, newPassword);
        newClient = new ApiClientStateful(baseUrl, username, newPassword);
        retrievedInfo = newClient.get(userUrl, User.class).personalInfo();
        assertEquals(updatedInfo, retrievedInfo);

    }

    @Test
    @DisplayName("Health Check")
    public void testListUsers() throws URISyntaxException, JsonProcessingException {

        URI health = new URI(baseUrl + "/actuator/health");

        ApiClientStateful adminClient = new ApiClientStateful(baseUrl, "admin", "admin");

        String healthResponse = adminClient.get(health);

        record Health(String status) {}
        ObjectMapper mapper = new ObjectMapper();
        Health healthMessage = mapper.readValue(healthResponse, Health.class);

        assertEquals("UP", healthMessage.status());

    }

    private PersonalInfo randomPersonalInfo() {

        return new PersonalInfo(
                faker.internet().emailAddress(),
                faker.name().name(),
                faker.phoneNumber().phoneNumber(),
                random.nextInt(40) + 150,
                Set.of(randomAddressRecord()));
    }

    private RegistrationRequest createRandomUserRegistration() {

        String username = "user-" + randomUUID();
        String password = "password";
        PersonalInfo info = randomPersonalInfo();

        return new RegistrationRequest(username, password, info.email());
    }

    private AddressRecord randomAddressRecord() {

        Address fakerAddress = faker.address();
        return new AddressRecord(fakerAddress.streetAddress(),
                fakerAddress.city(),
                fakerAddress.state(),
                fakerAddress.zipCode());
    }
}
