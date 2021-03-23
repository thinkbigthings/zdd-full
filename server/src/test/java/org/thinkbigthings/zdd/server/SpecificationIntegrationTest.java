package org.thinkbigthings.zdd.server;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.thinkbigthings.zdd.dto.SavedSearch;
import org.thinkbigthings.zdd.dto.SavedSearches;
import org.thinkbigthings.zdd.dto.SearchParameter;
import org.thinkbigthings.zdd.dto.StoreRecord;
import org.thinkbigthings.zdd.server.entity.StoreItem;
import org.thinkbigthings.zdd.server.entity.StoreItem_;
import org.thinkbigthings.zdd.server.entity.Subspecies;
import org.thinkbigthings.zdd.server.test.data.TestData;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;
import static org.thinkbigthings.zdd.server.test.data.TestData.readItems;


public class SpecificationIntegrationTest extends IntegrationTest {

    protected static Logger LOG = LoggerFactory.getLogger(SpecificationIntegrationTest.class);

    private Pageable firstPage = PageRequest.of(0, 10);

    private SearchService searchService = new SearchService();

    @Autowired
    private StoreService storeService;

    @Autowired
    private StoreItemRepository itemRepository;

    @Test
    @DisplayName("Test Specification queries")
    public void testSpecifications() throws IOException {

        String storeName = "testSpecifications-" + UUID.randomUUID().toString();
        LOG.info("Using store name " + storeName);

        storeService.saveNewStore(new StoreRecord(storeName, storeName));
        storeService.updateStoreItems(storeName, readItems("devon-flower-20201218.json"));

        // get a time between one update and the next
        Instant lastScan = Instant.now();

        // update again, then last scan will grab items changed since the first load
        storeService.updateStoreItems(storeName, readItems("devon-flower-20201221.json"));

        SavedSearches baseSearch = SavedSearches.newSavedSearches().withStore(storeName);
        List<StoreItem> results;

        // find all with an always true search
        results = search(baseSearch.withSearch(StoreItem_.WEIGHT_GRAMS, ">=", "0"), lastScan);
        assertEquals(16, results.size());

        // find by name
        results = search(baseSearch.withSearch(StoreItem_.STRAIN, "=", "Coffee Ice Cream"), lastScan);
        assertEquals(2, results.size());

        // find by thc
        results = search(baseSearch.withSearch(StoreItem_.THC_PERCENT, ">=", "25"), lastScan);
        assertEquals(2, results.size());

        // find by no cbd
        results = search(baseSearch.withSearch(StoreItem_.CBD_PERCENT, "=", "0"), lastScan);
        assertEquals(12, results.size());

        // find by has cbd
        results = search(baseSearch.withSearch(StoreItem_.CBD_PERCENT, ">", "0"), lastScan);
        assertEquals(4, results.size());

        // find by weight
        results = search(baseSearch.withSearch(StoreItem_.WEIGHT_GRAMS, "=", "1"), lastScan);
        assertEquals(5, results.size());

        // find by indica
        String indica = String.valueOf(Subspecies.INDICA.ordinal());
        results = search(baseSearch.withSearch(StoreItem_.SUBSPECIES, "=", indica), lastScan);
        assertEquals(5, results.size());

        // find by price
        results = search(baseSearch.withSearch(StoreItem_.PRICE_DOLLARS, "<=", "15"), lastScan);
        assertEquals(4, results.size());

        // find by vendor
        results = search(baseSearch.withSearch(StoreItem_.VENDOR, "=", "Terrapin"), lastScan);
        results.forEach(item -> assertEquals("Terrapin", item.getVendor()));
        results.forEach(item -> assertTrue(item.getAdded().isAfter(lastScan)));
        assertEquals(9, results.size());

        // TODO terpene amounts
        SavedSearch sleepProfile = new SavedSearch()
                .withParameter(StoreItem_.MYRCENE_PERCENT, ">=", ".5")
                .withParameter(StoreItem_.LINALOOL_PERCENT, ">=", ".1");
//                .withParameter(StoreItem_.TERPINOLENE_PERCENT, ">=", ".4")

        SavedSearches sleepTerps = baseSearch.withSearch(sleepProfile);
        results = search(sleepTerps, lastScan);
//        results.forEach(item -> assertEquals("Terrapin", item.getVendor()));
//        results.forEach(item -> assertTrue(item.getAdded().isAfter(lastScan)));
        assertEquals(9, results.size());

        // select id, strain, bisabolol_percent, caryophyllene_percent, humulene_percent, limonene_percent, pinene_percent, terpinolene_percent from store_item;
        // select id, strain, caryophyllene_percent, myrcene_percent, linalool_percent, terpinolene_percent from store_item;

    }

    public List<StoreItem> search(SavedSearches userSearches, Instant lastScan) {
        return searchService.toSpec(userSearches, lastScan).stream()
                .map(itemRepository::findAll)
                .flatMap(List::stream)
                .collect(toList());
    }

}
