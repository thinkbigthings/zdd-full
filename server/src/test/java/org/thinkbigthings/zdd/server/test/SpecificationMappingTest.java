package org.thinkbigthings.zdd.server.test;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;
import org.thinkbigthings.zdd.dto.SavedSearches;
import org.thinkbigthings.zdd.server.SearchService;
import org.thinkbigthings.zdd.server.entity.StoreItem;
import org.thinkbigthings.zdd.server.test.data.TestData;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class SpecificationMappingTest {

    private SearchService searchService = new SearchService();

    @Test
    public void testMapSpecifications() throws Exception {

        SavedSearches userSearches = TestData.readSavedSearch("saved-search-cherry-diesel-or-high-thc.json");

        List<Specification<StoreItem>> specs = searchService.toSpec(userSearches, Instant.now());

        assertFalse(specs.isEmpty());
    }

}
