package org.thinkbigthings.zdd.server.scraper.keystone;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.thinkbigthings.zdd.server.entity.StoreItem;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.thinkbigthings.zdd.server.scraper.keystone.Functional.uncheck;

@Component
public class Scraper {

    private static Logger LOG = LoggerFactory.getLogger(Scraper.class);

    // Thread-safe if configuration is before ANY read or write calls
    private final ObjectMapper mapper = new ObjectMapper();

    // immutable and thread safe
    private final ObjectReader reader = mapper.reader();

    private EntityExtractor extractor;

    public Scraper(EntityExtractor extractor) {
        this.extractor = extractor;
    }

    public List<StoreItem> scrape(String keystoneUrl) {

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpRequest request = HttpRequest.newBuilder(URI.create(keystoneUrl)).build();

        try {

            List<String> dataUrls = extractDataUrls(client.send(request, HttpResponse.BodyHandlers.ofLines()).body());

            LOG.info("Scraping site: " + keystoneUrl);

            return dataUrls.stream()
                    .map(this::createRequest)
                    .map(uncheck(uri -> client.send(uri, HttpResponse.BodyHandlers.ofString()).body()))
                    .flatMap(dataContent -> extractor.extractItems(dataContent).stream())
                    .collect(toList());
        }
        catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpRequest createRequest(String url) {
        return HttpRequest.newBuilder().uri(URI.create(url)).build();
    }

    public List<String> extractDataUrls(Stream<String> htmlLines) {
        return htmlLines.filter(this::hasScriptData)
                .map(this::extractScriptData)
                .filter(scriptData -> scriptData.get("title").equals("new-website-devon-flower")) // flower only
                .map(scriptData -> extractDataUrl(scriptData))
                .collect(toList());
    }

    private String extractDataUrl(Map scriptData) {
        return ((Map)scriptData.get("init_config")).get("data_request_url").toString();
    }

    private boolean hasScriptData(String htmlLine) {
        return htmlLine.trim().startsWith("window['ninja_table_instance_");
    }

    private Map extractScriptData(String htmlLine) {
        try {
            return reader.readValue(htmlLine.substring(htmlLine.indexOf("=") + 1), HashMap.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
