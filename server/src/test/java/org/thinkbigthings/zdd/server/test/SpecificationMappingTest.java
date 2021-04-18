package org.thinkbigthings.zdd.server.test;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;
import org.thinkbigthings.zdd.dto.SavedSearches;
import org.thinkbigthings.zdd.server.SpecificationBuilder;
import org.thinkbigthings.zdd.server.entity.StoreItem;
import org.thinkbigthings.zdd.server.test.data.TestData;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SpecificationMappingTest {

    private SpecificationBuilder searchService = new SpecificationBuilder();

    @Test
    public void testMapSpecifications() throws Exception {

        SavedSearches userSearches = TestData.readSavedSearch("saved-search-cherry-diesel-or-high-thc.json");

        List<Specification<StoreItem>> specs = searchService.toSpecs(userSearches, Instant.now());

        assertFalse(specs.isEmpty());

        Specification<StoreItem> single = searchService.toSpec(userSearches, Instant.now());

        assertNotNull(single);
    }

}
