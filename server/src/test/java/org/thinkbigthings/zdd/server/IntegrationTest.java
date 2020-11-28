package org.thinkbigthings.zdd.server;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;
import org.thinkbigthings.zdd.client.ApiClientStateful;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// With Junit 5, we do not need @RunWith(SpringRunner.class) anymore.
// Spring tests are executed with @ExtendWith(SpringExtension.class),
// and @SpringBootTest, and the other @…Test annotations are already annotated with it.


@Tag("integration")
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    @Autowired
    private UserController controller;

    @LocalServerPort
    private int randomServerPort;


    @Test
    @DisplayName("Basic Spring wiring")
    public void contextLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    @DisplayName("Health Check")
    public void testListUsers() throws URISyntaxException {

        final String baseUrl = "https://localhost:" + randomServerPort;
        RestTemplate restTemplate = new RestTemplate();
        URI health = new URI(baseUrl + "/actuator/health");

        ApiClientStateful client = new ApiClientStateful(baseUrl, "admin", "admin");

        String healthResponse = client.get(health);

        assertEquals("{\"status\":\"UP\"}", healthResponse);

//        ResponseEntity<String> result = restTemplate.getForEntity(health, String.class);
//
//        assertEquals(200, result.getStatusCodeValue());
    }
}
