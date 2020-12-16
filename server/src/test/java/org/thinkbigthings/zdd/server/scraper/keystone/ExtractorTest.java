package org.thinkbigthings.zdd.server.scraper.keystone;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExtractorTest {

    private Extractor extractor = new Extractor();

    @Test
    public void testExtractor() throws IOException {

        Path path = Paths.get("src", "test", "resources", "devon-flower.json");
        String content = Files.readString(path, StandardCharsets.UTF_8);

        List<Item> items = extractor.extractItems(content);

        assertEquals(35, items.size());
        System.out.println(items);
    }
}
