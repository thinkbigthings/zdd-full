package org.thinkbigthings.zdd.server.scraper.keystone;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScraperTest {

    private Scraper scraper = new Scraper();

    @Test
    public void testExtractor() throws IOException {

        Path path = Paths.get("src", "test", "resources", "keystone-devon-20201119.html");
        Stream<String> content = Files.lines(path);

        List<String> dataUrls = scraper.extractDataUrls(content);

        assertEquals(1, dataUrls.size());
        System.out.println(dataUrls);

    }



}
