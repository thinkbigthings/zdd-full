package org.thinkbigthings.zdd.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.thinkbigthings.zdd.dto.SavedSearches;
import org.thinkbigthings.zdd.dto.StoreRecord;
import org.thinkbigthings.zdd.server.entity.Store;
import org.thinkbigthings.zdd.server.entity.StoreItem;
import org.thinkbigthings.zdd.server.entity.StoreItem_;
import org.thinkbigthings.zdd.server.entity.Store_;
import org.thinkbigthings.zdd.server.test.data.TestData;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.platform.commons.util.Preconditions.condition;
import static org.thinkbigthings.zdd.server.test.data.TestData.readItems;


public class SpecificationIntegrationTest extends IntegrationTest {

    protected static Logger LOG = LoggerFactory.getLogger(SpecificationIntegrationTest.class);

    private Pageable firstPage = PageRequest.of(0, 10);

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
        storeService.updateStoreItems(storeName, readItems("devon-flower-20201221.json"));

        SavedSearches userSearches = TestData.readSavedSearch("keystone-devon-saved-search.json");

        SearchService searchService = new SearchService();
        List<Specification<StoreItem>> specs = searchService.toSpec(userSearches);
        
        List<StoreItem> searchResults = specs.stream()
                .map(itemRepository::findAll)
                .flatMap(List::stream)
                .collect(toList());

        assertFalse(searchResults.isEmpty());
    }

}
