package org.thinkbigthings.zdd.server.scraper.keystone;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.thinkbigthings.zdd.server.scraper.keystone.Functional.uncheck;

public class Scraper {

    // immutable and thread safe
    public static final ObjectReader reader;

    static {
        ObjectMapper mapper = new ObjectMapper();
        reader = mapper.reader();
    }

    private Extractor extractor = new Extractor();

    public List<Item> scrape() {

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        URI site = URI.create("https://keystoneshops.com/menu/devon");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(site)
                .build();

        try {

            // TODO stream initial page load so it's not all loaded into memory
            // TODO refactor html parsing / filtering from network call, so can unit test grabbing data urls from html

            List<String> dataUrls = client.send(request, HttpResponse.BodyHandlers.ofLines()).body()
                    .filter(line -> line.trim().startsWith("window['ninja_table_instance_"))
                    .map(line -> line.substring(line.indexOf("=") + 1))
                    .map(uncheck((String line) -> reader.readValue(line, HashMap.class)))
                    .filter(jsonMap -> jsonMap.get("title").equals("new-website-devon-flower"))
                    .map(jsonMap -> ((Map)jsonMap.get("init_config")).get("data_request_url").toString())
                    .collect(toList());

            return dataUrls.stream()
                    .map(URI::create)
                    .map(dataUri -> HttpRequest.newBuilder().uri(dataUri).build())
                    .map(uncheck(uri -> client.send(uri, HttpResponse.BodyHandlers.ofString()).body()))
                    .flatMap(dataContent -> extractor.extractItems(dataContent).stream())
                    .collect(toList());
        }
        catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

    }

}
