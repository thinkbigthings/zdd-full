package org.thinkbigthings.zdd.server.scraper.keystone;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.thinkbigthings.zdd.server.IntegrationTest;
import org.thinkbigthings.zdd.server.StoreItemRepository;
import org.thinkbigthings.zdd.server.entity.StoreItem;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ScrapeToDatabaseIntegrationTest extends IntegrationTest {

    protected static Logger LOG = LoggerFactory.getLogger(ScrapeToDatabaseIntegrationTest.class);

    private static String jsonContent;

    // this plugs in just a piece of the running app to our test code
    // we can use it for quickly bootstrapping test data without going through the API
    @Autowired
    private StoreItemRepository itemRepository;

    @BeforeAll
    public static void createTestData() throws IOException {

        LOG.info("");
        LOG.info("=======================================================================================");
        LOG.info("Creating test data");
        LOG.info("");

        Path path = Paths.get("src", "test", "resources", "devon-flower-20201218.json");
        jsonContent = Files.readString(path, StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("Store Items")
    public void testSaveScrapedItems() {

        long startCount = itemRepository.count();

        EntityExtractor extractor = new EntityExtractor();

        List<StoreItem> storeItems = extractor.extractItems(jsonContent);
        itemRepository.saveAll(storeItems);

        long endCount = itemRepository.count();

        assertTrue(storeItems.size() > 0);
        assertEquals(storeItems.size(), endCount - startCount);
    }

}
