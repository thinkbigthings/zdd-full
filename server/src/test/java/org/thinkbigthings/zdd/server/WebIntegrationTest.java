package org.thinkbigthings.zdd.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.thinkbigthings.zdd.server.test.client.ApiClientStateful;
import org.thinkbigthings.zdd.dto.PersonalInfo;
import org.thinkbigthings.zdd.dto.RegistrationRequest;
import org.thinkbigthings.zdd.dto.User;
import org.thinkbigthings.zdd.server.test.client.ParsablePage;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;
import static org.thinkbigthings.zdd.server.test.data.TestData.createRandomUserRegistration;
import static org.thinkbigthings.zdd.server.test.data.TestData.randomPersonalInfo;


public class WebIntegrationTest extends IntegrationTest {

    protected static Logger LOG = LoggerFactory.getLogger(WebIntegrationTest.class);

    private static String baseUrl;
    private static URI users;

    private ObjectMapper mapper = new ObjectMapper();
    private static ApiClientStateful adminClient;

    private static String testUserName;
    private static String testUserPassword;
    private static URI testUserUrl;
    private static URI testUserUpdatePasswordUrl;
    private static URI testUserInfoUrl;


    // this plugs in just a piece of the running app to our test code
    // we can use it for quickly bootstrapping test data without going through the API
    @Autowired
    private UserService userService;

    @BeforeAll
    public static void createTestData(@Autowired UserService userService, @LocalServerPort int randomServerPort) {

        baseUrl = "https://localhost:" + randomServerPort;
        users = URI.create(baseUrl + "/user");

        RegistrationRequest testUserRegistration = createRandomUserRegistration();
        userService.saveNewUser(testUserRegistration);
        testUserName = testUserRegistration.username();
        testUserPassword = testUserRegistration.plainTextPassword();
        userService.updateUser(testUserName, randomPersonalInfo());

        testUserUrl = URI.create(users + "/" + testUserName);
        testUserUpdatePasswordUrl = URI.create(testUserUrl + "/password/update");
        testUserInfoUrl = URI.create(testUserUrl + "/personalInfo");

        adminClient = new ApiClientStateful(baseUrl, "admin", "admin");
    }


    @Test()
    @DisplayName("Admin list users")
    public void adminListUsers() throws JsonProcessingException {

        String results = adminClient.get(users);

        Page<User> page = mapper.readValue(results, new TypeReference<ParsablePage<User>>() {});

        assertTrue(page.isFirst());
    }

    @Test
    @DisplayName("Update user password")
    public void testUpdatePassword() {

        ApiClientStateful userClient = new ApiClientStateful(baseUrl, testUserName, testUserPassword);

        String newPassword = "password";
        userClient.post(testUserUpdatePasswordUrl, newPassword);
        userClient = new ApiClientStateful(baseUrl, testUserName, newPassword);
        PersonalInfo retrievedInfo = userClient.get(testUserUrl, User.class).personalInfo();
    }

    @Test
    @DisplayName("Update user info")
    public void updateUserInfo() {

        var updatedInfo = randomPersonalInfo();
        var savedInfo = userService.updateUser(testUserName, updatedInfo).personalInfo();
        assertEquals(updatedInfo, savedInfo);
    }

    @Test
    @DisplayName("Health Check")
    public void testHealth() throws URISyntaxException, JsonProcessingException {

        URI health = new URI(baseUrl + "/actuator/health");

        String healthResponse = adminClient.get(health);

        record Health(String status) {}
        Health healthMessage = mapper.readValue(healthResponse, Health.class);

        assertEquals("UP", healthMessage.status());
    }

}
