package org.thinkbigthings.zdd.perf;

import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;
import org.thinkbigthings.zdd.dto.AddressDTO;
import org.thinkbigthings.zdd.dto.UserDTO;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
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

    private URI users;
    private URI info;
    private URI health;

    private Random random = new Random();
    private Faker faker = new Faker(Locale.US, new Random());
    private ApiClient adminClient;

    private final Instant end;

    public LoadTester(AppProperties config) {

        baseUrl = "https://" + config.getHost() + ":" + config.getPort();

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
        range(0, 1000).forEach(i -> adminClient.post(users, createRandomUser()));
    }

    private void doCRUD() {

        UserDTO user = createRandomUser();
        adminClient.post(users, user);

        URI userUrl = URI.create(users.toString() + "/" + user.username);
        URI updatePasswordUrl = URI.create(users.toString() + "/" + user.username + "/password/update");

        UserDTO userData = adminClient.get(userUrl, UserDTO.class);

        // test user with own credentials
        ApiClient userClient = new ApiClient(user.username, user.plainTextPassword);
        userClient.get(userUrl, UserDTO.class);

        String newPassword = "password";
        userClient.post(updatePasswordUrl, newPassword);
        userClient = new ApiClient(user.username, newPassword);

        updateUserEditableData(userData);
        userClient.put(userUrl, userData);

        UserDTO updatedUser = adminClient.get(userUrl, UserDTO.class);

        if( ! userData.equals(updatedUser)) {
            String message = "user updates were not all persisted: " + userData + " vs " + updatedUser;
            throw new RuntimeException(message);
        }

        adminClient.get(info);

        adminClient.get(health);

        String page = adminClient.get(users);
    }

    private void updateUserEditableData(UserDTO user) {
        user.displayName = faker.name().name();
        user.phoneNumber = faker.phoneNumber().phoneNumber();
        user.heightCm = 150 + random.nextInt(40);
        user.email = faker.internet().emailAddress();
        user.addresses.add(randomAddress());
    }

    private UserDTO createRandomUser() {

        UserDTO newUser = new UserDTO();
        newUser.username = "user-" + randomUUID();
        newUser.plainTextPassword = "password";
        updateUserEditableData(newUser);
        return newUser;
    }

    private AddressDTO randomAddress() {

        AddressDTO address = new AddressDTO();

        Address randomAddress = faker.address();
        address.line1 = randomAddress.streetAddress();
        address.city = randomAddress.city();
        address.state = randomAddress.state();
        address.zip = randomAddress.zipCode();

        return address;
    }

}
