package org.thinkbigthings.zdd.server;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.thinkbigthings.zdd.dto.StoreRecord;
import org.thinkbigthings.zdd.server.entity.Store;
import org.thinkbigthings.zdd.server.entity.StoreItem;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;


public class ScraperIntegrationTest extends IntegrationTest {

    protected static Logger LOG = LoggerFactory.getLogger(ScraperIntegrationTest.class);

    private static final String storeName = "Keystone Devon";
    private static final String storeWebsite = "https://keystoneshops.com/menu/devon";

    @Autowired
    private StoreService storeService;

    @BeforeAll
    public static void createTestData(@Autowired StoreService storeService) {

        LOG.info("");
        LOG.info("=======================================================================================");
        LOG.info("Creating test data");
        LOG.info("");

        StoreRecord store = new StoreRecord(storeName, storeWebsite, "");
        storeService.saveNewStore(store);

    }

    @Disabled("This test is used with caution because it hits the live website")
    @Test
    @Transactional
    public void testScraper() {

        storeService.scrapeStore(storeName);

        Store store = storeService.getStore(storeName);
        Set<StoreItem> items = store.getItems();

        assertFalse(items.isEmpty());

    }



}
