package org.thinkbigthings.zdd.perf;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;
import org.thinkbigthings.zdd.dto.AddressRecord;
import org.thinkbigthings.zdd.dto.PersonalInfo;
import org.thinkbigthings.zdd.dto.RegistrationRequest;
import org.thinkbigthings.zdd.dto.UserRecord;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.IntStream;

import static java.util.UUID.randomUUID;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

@Component
public class LoadTester {

    private Duration duration;
    private int numThreads;
    private Duration latency;
    private boolean insertOnly;
    private String baseUrl;

    private URI registration;
    private URI users;
    private URI info;
    private URI health;

    private Random random = new Random();
    private Faker faker = new Faker(Locale.US, new Random());
    private ApiClient adminClient;

    private final Instant end;

    public LoadTester(AppProperties config) {

        baseUrl = "https://" + config.getHost() + ":" + config.getPort();

        registration = URI.create(baseUrl + "/registration");
        users = URI.create(baseUrl + "/user");
        info = URI.create(baseUrl + "/actuator/info");
        health = URI.create(baseUrl + "/actuator/health");

        duration = config.getTestDuration();
        insertOnly = config.isInsertOnly();
        numThreads = config.getThreads();
        latency = config.getLatency();

        adminClient = new ApiClient("admin", "admin", latency);

        System.out.println("Number Threads: " + numThreads);
        System.out.println("Insert only: " + insertOnly);
        System.out.println("Latency: " + latency.toMillis()+"ms");

        String hms = String.format("%d:%02d:%02d",
                duration.toHoursPart(),
                duration.toMinutesPart(),
                duration.toSecondsPart());

        System.out.println("Running test for " + hms + " (hh:mm:ss) connecting to " + baseUrl);

        end = Instant.now().plus(duration);
    }

    public static CompletableFuture<?> allOf(List<CompletableFuture<Void>> futures) {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[futures.size()]));
    }

    public void run() {

        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(numThreads);

        try {

            List<CompletableFuture<Void>> futures = IntStream.rangeClosed(1, numThreads)
                    .mapToObj(i -> runAsync(() -> makeCalls(), executor))
                    .collect(toList());

            allOf(futures).join();
        }
        catch(CompletionException e) {
            e.printStackTrace();
        }

        // this needs to be called or the program won't terminate
        executor.shutdown();
    }

    private boolean isDurationActive() {
        return Instant.now().isBefore(end);
    }

    private void makeCalls() {

        try {
            while(isDurationActive()) {
                if(insertOnly) {
                    doInserts();
                }
                else {
                    doCRUD();
                }
            }
        }
        catch(Exception e) {
            throw new CompletionException(e);
        }
    }

    private void doInserts() {
        range(0, 1000).forEach(i -> adminClient.post(registration, createRandomUserRegistration()));
    }

    private void doCRUD() {

        RegistrationRequest registrationRequest = createRandomUserRegistration();
        adminClient.post(registration, registrationRequest);
        String username = registrationRequest.username();
        String password = registrationRequest.plainTextPassword();

        URI userUrl = URI.create(users.toString() + "/" + username);
        URI updatePasswordUrl = URI.create(users.toString() + "/" + username + "/password/update");

        UserRecord userData = adminClient.get(userUrl, UserRecord.class);

        // test user with own credentials
        ApiClient userClient = new ApiClient(username, password, latency);
        userClient.get(userUrl, UserRecord.class);

        String newPassword = "password";
        userClient.post(updatePasswordUrl, newPassword);
        userClient = new ApiClient(username, newPassword);

        userData = updateUserEditableRecord(userData);
        userClient.put(userUrl, userData);

        UserRecord updatedUser = adminClient.get(userUrl, UserRecord.class);

        if( ! userData.equals(updatedUser)) {
            String message = "user updates were not all persisted: " + userData + " vs " + updatedUser;
            throw new RuntimeException(message);
        }

        adminClient.get(info);

        adminClient.get(health);

        String page = adminClient.get(users);
    }

    private UserRecord updateUserEditableRecord(UserRecord user) {

        Set<AddressRecord> addresses = user.addresses();
        addresses.add(randomAddressRecord());

        return new UserRecord(user.username(),
                                 user.plainTextPassword(),
                                 user.registrationTime(),
                                 faker.internet().emailAddress(),
                                 faker.name().name(),
                                 faker.phoneNumber().phoneNumber(),
                        random.nextInt(40) + 150,
                                 addresses,
                                 user.roles());
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

        return new RegistrationRequest(username, password, info);
    }

    private AddressRecord randomAddressRecord() {

        Address fakerAddress = faker.address();
        return new AddressRecord(fakerAddress.streetAddress(),
                fakerAddress.city(),
                fakerAddress.state(),
                fakerAddress.zipCode());
    }

}
