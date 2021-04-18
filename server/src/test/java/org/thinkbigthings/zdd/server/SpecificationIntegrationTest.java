package org.thinkbigthings.zdd.server;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.thinkbigthings.zdd.dto.RegistrationRequest;
import org.thinkbigthings.zdd.dto.SavedSearch;
import org.thinkbigthings.zdd.dto.SavedSearches;
import org.thinkbigthings.zdd.dto.StoreRecord;
import org.thinkbigthings.zdd.server.entity.StoreItem;
import org.thinkbigthings.zdd.server.entity.StoreItem_;
import org.thinkbigthings.zdd.server.entity.Subspecies;
import org.thinkbigthings.zdd.server.test.client.ApiClientStateful;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;
import static org.thinkbigthings.zdd.server.test.data.TestData.createRandomUserRegistration;
import static org.thinkbigthings.zdd.server.test.data.TestData.readItems;


public class SpecificationIntegrationTest extends IntegrationTest {

    protected static Logger LOG = LoggerFactory.getLogger(SpecificationIntegrationTest.class);

    private Pageable firstPage = PageRequest.of(0, 10);

    @Autowired
    private ItemService itemService;

    @Autowired
    private StoreService storeService;


    static List<StoreItem> results;
    static Instant lastScan;
    static SavedSearches searchStore;

    @BeforeAll
    public static void createTestData(@Autowired StoreService storeService, @LocalServerPort int randomServerPort) throws IOException {

        String storeName = "store-" + UUID.randomUUID();
        LOG.info("Using store name " + storeName);

        storeService.saveNewStore(new StoreRecord(storeName, storeName));
        storeService.updateStoreItems(storeName, readItems("devon-flower-20201218.json"));

        // get a time between one update and the next
        lastScan = Instant.now();

        // update again, then last scan will grab items changed since the first load
        storeService.updateStoreItems(storeName, readItems("devon-flower-20201221.json"));

        searchStore = SavedSearches.newSavedSearches().withStore(storeName);
    }

    @Test
    @DisplayName("Test Specification queries")
    public void testSpecifications() throws IOException {

        // find all with an always true search
        results = itemService.search(searchStore.withSearch(StoreItem_.WEIGHT_GRAMS, ">=", "0"), lastScan);
        assertEquals(16, results.size());

        // find by name
        results = itemService.search(searchStore.withSearch(StoreItem_.STRAIN, "=", "Coffee Ice Cream"), lastScan);
        assertEquals(2, results.size());

        // find by thc
        results = itemService.search(searchStore.withSearch(StoreItem_.THC_PERCENT, ">=", "25"), lastScan);
        assertEquals(2, results.size());

        // find by no cbd
        results = itemService.search(searchStore.withSearch(StoreItem_.CBD_PERCENT, "=", "0"), lastScan);
        assertEquals(12, results.size());

        // find by has cbd
        results = itemService.search(searchStore.withSearch(StoreItem_.CBD_PERCENT, ">", "0"), lastScan);
        assertEquals(4, results.size());

        // find by weight
        results = itemService.search(searchStore.withSearch(StoreItem_.WEIGHT_GRAMS, "=", "1"), lastScan);
        assertEquals(5, results.size());

        // find by indica
        String indica = String.valueOf(Subspecies.INDICA.ordinal());
        results = itemService.search(searchStore.withSearch(StoreItem_.SUBSPECIES, "=", indica), lastScan);
        assertEquals(5, results.size());

        // find by price
        results = itemService.search(searchStore.withSearch(StoreItem_.PRICE_DOLLARS, "<=", "15"), lastScan);
        assertEquals(4, results.size());

        // find by vendor
        results = itemService.search(searchStore.withSearch(StoreItem_.VENDOR, "=", "Terrapin"), lastScan);
        results.forEach(item -> assertEquals("Terrapin", item.getVendor()));
        results.forEach(item -> assertTrue(item.getAdded().isAfter(lastScan)));
        assertEquals(9, results.size());

    }

    @Test
    public void testFindByTerpeneAmounts() {

        // find by terpene amounts
        SavedSearch sleepyProfile = new SavedSearch()
                .withParameter(StoreItem_.MYRCENE_PERCENT, ">=", ".1")
                .withParameter(StoreItem_.LINALOOL_PERCENT, ">=", ".13")
                .withParameter(StoreItem_.TERPINOLENE_PERCENT, ">=", ".01")
                .withParameter(StoreItem_.VENDOR, "=", "Terrapin");

        SavedSearches sleepyTerappin = searchStore.withSearch(sleepyProfile);

        results = itemService.search(sleepyTerappin, lastScan);
        results.forEach(item -> assertEquals("Terrapin", item.getVendor()));
        results.forEach(item -> assertTrue(item.getAdded().isAfter(lastScan)));
        assertEquals(1, results.size());
    }

}
