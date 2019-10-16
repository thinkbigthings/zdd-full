package org.thinkbigthings.zdd.perf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.stereotype.Component;
import org.thinkbigthings.zdd.dto.AddressDTO;
import org.thinkbigthings.zdd.dto.UserDTO;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static java.util.UUID.randomUUID;
import static java.util.concurrent.CompletableFuture.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

@Component
public class LoadTester {

    public static final class MediaType {
        public static final String APPLICATION_JSON_VALUE = "application/json";
    }

    private HttpClient client;
    private Duration duration;
    private int numThreads;
    private Duration latency;
    private boolean insertOnly;
    private String baseUrl;

    private URI users;
    private URI info;
    private URI health;

    private Random random = new Random();
    private ObjectMapper mapper = new ObjectMapper();
    private Faker faker = new Faker(Locale.US, new Random());

    private final Instant end;

    public LoadTester(AppProperties config) {

        try {
            // clients are immutable and thread safe
            // don't check certificates (so can use self-signed) and don't verify hostname
            SSLContext sc = SSLContext.getInstance("TLSv1.3");
            sc.init(null, new TrustManager[]{new InsecureTrustManager()}, new SecureRandom());
            System.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());

            client = HttpClient.newBuilder()
                    .sslContext(sc)
                    .build();
        }
        catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }

        baseUrl = "https://" + config.getHost() + ":" + config.getPort() + "/api" ;

        users = URI.create(baseUrl + "/user");
        info = URI.create(baseUrl + "/actuator/info");
        health = URI.create(baseUrl + "/actuator/health");

        duration = config.getTestDuration();
        insertOnly = config.isInsertOnly();
        numThreads = config.getThreads();
        latency = config.getLatency();

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
        range(0, 1000).forEach(i -> post(users, randomUser()));
    }

    private void doCRUD() {

        UserDTO user = randomUser();
        post(users, user);

        URI userUrl = URI.create(users.toString() + "/" + user.username);
        UserDTO firstUserSave = get(userUrl, UserDTO.class);

        UserDTO updatedUser = randomUser(user.username);
        updatedUser.registrationTime = firstUserSave.registrationTime;
        put(userUrl, updatedUser);

        UserDTO secondUserSave = get(userUrl, UserDTO.class);

        if( ! updatedUser.equals(secondUserSave)) {
            String message = "user updates were not all persisted: " + updatedUser + " vs " + secondUserSave;
            throw new RuntimeException(message);
        }

        get(info);

        get(health);

        get(users);
    }

    private UserDTO randomUser() {

        return randomUser("user-" + randomUUID());
    }

    private UserDTO randomUser(String username) {

        UserDTO newUser = new UserDTO();
        newUser.username = username;
        newUser.displayName = faker.name().name();
        newUser.phoneNumber = faker.phoneNumber().phoneNumber();
        newUser.heightCm = 150 + random.nextInt(40);
        newUser.email = faker.internet().emailAddress();
        newUser.addresses.add(randomAddress());
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

    public void put(URI uri, UserDTO newUser) {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .PUT(jsonFor(newUser))
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();

        sendWithLatency(request);
    }

    public void post(URI uri, UserDTO newUser) {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(jsonFor(newUser))
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();

        sendWithLatency(request);
    }

    public String get(URI uri) {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = sendWithLatency(request);

        return response.body();
    }

    public <T> T get(URI uri, Class<T> jsonResponse) {

        return parse(get(uri), jsonResponse);
    }

    public <T> T parse(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public HttpRequest.BodyPublisher jsonFor(Object object) {

        String json;
        try {
            json = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return HttpRequest.BodyPublishers.ofString(json);
    }

    private void sleep(Duration sleepDuration) {
        if(sleepDuration.isZero()) {
            return;
        }
        try {
            Thread.sleep(sleepDuration.toMillis());
        }
        catch(InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public HttpResponse<String> sendWithLatency(HttpRequest request) {

        try {

            // more on body handlers here https://openjdk.java.net/groups/net/httpclient/recipes.html
            // might be fun to have direct-to-json-object body handler

            sleep(latency);
            HttpResponse<String> response = throwOnError(client.send(request, HttpResponse.BodyHandlers.ofString()));
            sleep(latency);
            return response;
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    public HttpResponse<String> throwOnError(HttpResponse<String> response) {

        if(response.statusCode() != 200) {
            String message = "Return status code was " + response.statusCode();
            throw new RuntimeException(message);
        }
        else {
            return response;
        }
    }

}
