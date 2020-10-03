package org.thinkbigthings.zdd.perf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;

public class ApiClient {

    record Header(String name, String value) {}

    public static final class MediaType {
        public static final String APPLICATION_JSON_VALUE = "application/json";
    }

    private final Header JSON_CONTENT = new Header("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    private final ObjectMapper mapper = new ObjectMapper();

    // TODO warn or throw if using an insecure (not https) url because we are passing around basic auth

    private HttpClient client;
    private Header authHeader;
    private Duration syntheticLatency;

    public ApiClient(Header authHeader) {
        this(authHeader, Duration.ZERO);
    }

    public ApiClient(Header authHeader, Duration syntheticLatency) {

        this.syntheticLatency = syntheticLatency;

        this.authHeader = authHeader;

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
    }


    public static Header createTokenAuthHeader(HttpHeaders responseHeaders) {

        String cookies = responseHeaders.firstValue("set-cookie").get();
        String rememberMe = Arrays.stream(cookies.split(";"))
                .map(String::trim)
                .filter(c -> c.startsWith("remember-me="))
                .findFirst()
                .get();

        String token = rememberMe.substring("remember-me=".length());

        return new Header("Cookie", "remember-me="+token);
    }

    public static Header createBasicAuthHeader(String username, String password) {
        String encodedCredentials = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        return new Header("Authorization", "Basic " + encodedCredentials);
    }

    public void put(URI uri, Object body) {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .PUT(publisher(body))
                .setHeader(JSON_CONTENT.name(), JSON_CONTENT.value())
                .setHeader(authHeader.name(), authHeader.value())
                .build();

        sendWithLatency(request);
    }

    public void post(URI uri, Object body) {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(publisher(body))
                .setHeader(JSON_CONTENT.name(), JSON_CONTENT.value())
                .setHeader(authHeader.name(), authHeader.value())
                .build();

        sendWithLatency(request);
    }

    public HttpResponse<String> getResponse(URI uri) {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .setHeader(authHeader.name(), authHeader.value())
                .build();

        return sendWithLatency(request);
    }

    public String get(URI uri) {
        return getResponse(uri).body();
    }

    public <T> T get(URI uri, Class<T> jsonResponse) {

        return parse(get(uri), jsonResponse);
    }

    public HttpResponse<String> sendWithLatency(HttpRequest request) {

        try {

            // more on body handlers here https://openjdk.java.net/groups/net/httpclient/recipes.html
            // might be fun to have direct-to-json-object body handler
            sleep(syntheticLatency);
            HttpResponse<String> response = throwOnError(client.send(request, HttpResponse.BodyHandlers.ofString()));
            sleep(syntheticLatency);
            return response;
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    public <T> T parse(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public HttpRequest.BodyPublisher publisher(Object object) {

        String json;
        try {
            json = object instanceof String
                    ? object.toString()
                    : mapper.writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return HttpRequest.BodyPublishers.ofString(json);
    }

    public HttpResponse<String> throwOnError(HttpResponse<String> response) {

        if(response.statusCode() != 200) {
            String message = "Return status code was " + response.statusCode();
            message += " in call to " + response.request().uri();
            message += " with headers " + response.headers().map();
            message += " with body " + response.body();
            throw new RuntimeException(message);
        }

        return response;
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

}
