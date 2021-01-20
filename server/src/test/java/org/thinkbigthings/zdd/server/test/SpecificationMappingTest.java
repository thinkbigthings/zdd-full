package org.thinkbigthings.zdd.server.test;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;
import org.thinkbigthings.zdd.dto.SavedSearch;
import org.thinkbigthings.zdd.dto.SavedSearches;
import org.thinkbigthings.zdd.dto.SearchParameter;
import org.thinkbigthings.zdd.server.SearchService;
import org.thinkbigthings.zdd.server.entity.StoreItem;
import org.thinkbigthings.zdd.server.entity.StoreItem_;
import org.thinkbigthings.zdd.server.entity.Store_;
import org.thinkbigthings.zdd.server.test.data.TestData;

import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class SpecificationMappingTest {

    private SearchService searchService = new SearchService();

    @Test
    public void testMapSpecifications() throws Exception {

        SavedSearches userSearches = TestData.readSavedSearch("keystone-devon-saved-search.json");

        List<Specification<StoreItem>> specs = searchService.toSpec(userSearches);

        assertFalse(specs.isEmpty());
    }

}
