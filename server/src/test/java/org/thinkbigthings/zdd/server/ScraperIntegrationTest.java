package org.thinkbigthings.zdd.server;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.thinkbigthings.zdd.dto.StoreRecord;
import org.thinkbigthings.zdd.server.entity.Store;
import org.thinkbigthings.zdd.server.entity.StoreItem;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.thinkbigthings.zdd.server.test.data.TestData.readItems;


public class ScraperIntegrationTest extends IntegrationTest {

    protected static Logger LOG = LoggerFactory.getLogger(ScraperIntegrationTest.class);

    private Pageable firstPage = PageRequest.of(0, 10);
    private static final String storeName = "Keystone Devon";
    private static final String storeWebsite = "https://keystoneshops.com/menu/devon";

    @Autowired
    private ItemService itemService;

    @Autowired
    private StoreService storeService;

    @BeforeAll
    public static void createTestData(@Autowired StoreService storeService) throws IOException {

        LOG.info("");
        LOG.info("=======================================================================================");
        LOG.info("Creating test data");
        LOG.info("");

        try {
            storeService.saveNewStore(new StoreRecord(storeName, storeWebsite, Instant.now()));
        }
        catch(Exception e) {
            LOG.info("Caught exception " + e);
            LOG.info("Continuing without store creation");
        }
    }

    @Test
    @Disabled("This test is broken but the scaping update time is about to be changed anyway")
    @DisplayName("Write items to database without hitting live server")
    public void testScraperFromDisk() throws IOException {

        String name = "testScraperFromDisk-" + UUID.randomUUID().toString();
        LOG.info("Using store name " + name);
        storeService.saveNewStore(new StoreRecord(name, name, Instant.now()));

        Store store = storeService.getStore(name);
        Instant beforeUpdateTime = store.getUpdated();
        long beforeUpdateSize = itemService.findItems(firstPage).getTotalElements();

        List<StoreItem> items = readItems();
        storeService.updateStoreItems(storeName, items);

        store = storeService.getStore(name);
        Instant afterUpdateTime = store.getUpdated();
        long afterUpdateSize = itemService.findItems(firstPage).getTotalElements();

        assertTrue(beforeUpdateTime.isBefore(afterUpdateTime));
        assertTrue(beforeUpdateSize < afterUpdateSize, beforeUpdateSize + " vs " + afterUpdateSize);
    }

    @Test
    @Disabled("This test is used with caution because it hits the live website")
    @DisplayName("Write items to database by hitting a live server")
    public void testScraper() {

        Store store = storeService.getStore(storeName);
        Instant beforeUpdate = store.getUpdated();
        long sizeBeforeUpdate = itemService.findItems(firstPage).getTotalElements();

        storeService.scrapeStore(storeName);

        store = storeService.getStore(storeName);
        long sizeAfterUpdate = itemService.findItems(firstPage).getTotalElements();

        assertTrue(beforeUpdate.isBefore(store.getUpdated()));
        assertTrue(sizeBeforeUpdate < sizeAfterUpdate, sizeBeforeUpdate + " vs " + sizeAfterUpdate);
    }

}
