package org.thinkbigthings.zdd.perf;

import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;
import org.thinkbigthings.zdd.dto.AddressRecord;
import org.thinkbigthings.zdd.dto.PersonalInfo;
import org.thinkbigthings.zdd.dto.RegistrationRequest;
import org.thinkbigthings.zdd.dto.User;

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
    private boolean insertOnly;
    private String baseUrl;

    private URI registration;
    private URI users;
    private URI info;
    private URI health;

    private Random random = new Random();
    private Faker faker = new Faker(Locale.US, new Random());
    private ApiClientStateful adminClient;

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

        adminClient = new ApiClientStateful(baseUrl, "admin", "admin");

        System.out.println("Number Threads: " + numThreads);
        System.out.println("Insert only: " + insertOnly);

//        String hms = String.format("%d:%02d:%02d",
//                duration.toHoursPart(),
//                duration.toMinutesPart(),
//                duration.toSecondsPart());
//
//        System.out.println("Running test for " + hms + " (hh:mm:ss) connecting to " + baseUrl);

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
        range(0, 100).forEach(i -> adminClient.post(registration, createRandomUserRegistration()));
    }

    private void doCRUD() {

//        ApiClient headerClient = new ApiClient(createBasicAuthHeader("admin", "admin"));
//        headerClient.get(URI.create(baseUrl + "/login"));

        adminClient.get(URI.create(users + "/" + "admin"), User.class);

        RegistrationRequest registrationRequest = createRandomUserRegistration();
        adminClient.post(registration, registrationRequest);
        String username = registrationRequest.username();
        String password = registrationRequest.plainTextPassword();


        URI userUrl = URI.create(users + "/" + username);
        URI updatePasswordUrl = URI.create(userUrl + "/password/update");
        URI infoUrl = URI.create(userUrl + "/personalInfo");

        ApiClientStateful newClient = new ApiClientStateful(baseUrl, username, password);
        newClient.get(userUrl, User.class);
        newClient.logout();

        newClient = new ApiClientStateful(baseUrl, username, password);

        User user = newClient.get(userUrl, User.class);
        System.out.println(user);

        String newPassword = "password";
        newClient.post(updatePasswordUrl, newPassword);

        var updatedInfo = randomPersonalInfo();
        newClient.put(infoUrl, updatedInfo);

        PersonalInfo retrievedInfo = adminClient.get(userUrl, User.class).personalInfo();

        if( ! retrievedInfo.equals(updatedInfo)) {
            String message = "user updates were not all persisted: " + retrievedInfo + " vs " + updatedInfo;
            throw new RuntimeException(message);
        }

        newClient.logout();
        try {
            newClient.get(userUrl, User.class);
        }
        catch(Exception e) {
            System.out.println("user was appropriately logged out");
        }



        adminClient.get(info);

        adminClient.get(health);

        String page = adminClient.get(users);
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
