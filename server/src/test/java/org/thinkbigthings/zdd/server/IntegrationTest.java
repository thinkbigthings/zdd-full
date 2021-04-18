package org.thinkbigthings.zdd.server;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.thinkbigthings.zdd.dto.RegistrationRequest;
import org.thinkbigthings.zdd.server.test.client.ApiClientStateful;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.thinkbigthings.zdd.server.test.data.TestData.createRandomUserRegistration;

@Tag("integration")
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "logging.level.org.hibernate.SQL=DEBUG",
        "logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE",
        "spring.main.lazy-initialization=true",
        "spring.flyway.enabled=true",
        "spring.datasource.url=jdbc:tc:" + IntegrationTest.postgresContainer + ":///"
})
public class IntegrationTest {

    private static Logger LOG = LoggerFactory.getLogger(IntegrationTest.class);

    public static final String postgresContainer = "postgres:12";

    // need "autosave conservative" config, otherwise pg driver has caching issues with blue-green deployment
    // (org.postgresql.util.PSQLException: ERROR: cached plan must not change result type)
    protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(postgresContainer)
            .withUrlParam("autosave", "conservative")
            .withReuse(true);

    @LocalServerPort
    protected int randomServerPort;

    private static boolean cleared = false;

    @BeforeAll
    public static synchronized void clearDatabase(@Autowired Flyway flyway) {
        if( ! cleared) {
            cleared = true;
            flyway.clean();
            flyway.migrate();
        }
    }

    @DynamicPropertySource
    static void useDynamicProperties(DynamicPropertyRegistry registry) {

        // call start ourselves since we might reuse
        // instead of letting library manage it with @TestContainers and @Container
        postgres.start();

        // Mapped port can only be obtained after container is started.
        Map<String, Object> appProps = Map.of("spring.datasource.url", postgres.getJdbcUrl(),
                                                "spring.datasource.username", postgres.getUsername(),
                                                "spring.datasource.password", postgres.getPassword());

        // make these properties available to the app server before it starts up
        appProps.forEach((k,v) -> registry.add(k, ()-> v));

        // save the properties to a file so we can start up later if we want
        File tcProps = Paths.get("build", "postgres.properties").toFile();
        saveProperties(appProps, "testcontainer properties", tcProps);
    }

    @BeforeEach
    public void startup(TestInfo testInfo) {

        LOG.info("");
        LOG.info("=======================================================================================");
        LOG.info("Executing test " + testInfo.getDisplayName());
        LOG.info("using port " + randomServerPort);
        LOG.info("TestContainer jdbc url: " + postgres.getJdbcUrl());
        LOG.info("TestContainer username: " + postgres.getUsername());
        LOG.info("TestContainer password: " + postgres.getPassword());
        LOG.info("");
    }

    private static void saveProperties(Map<String, Object> appProps, String propsComment, File propsFile) {

        Properties props = new Properties();
        appProps.forEach((k,v) -> props.put(k,v));
        try (FileOutputStream edgeProps = new FileOutputStream(propsFile)) {
            propsFile.createNewFile();
            props.store(edgeProps, propsComment);
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

}
