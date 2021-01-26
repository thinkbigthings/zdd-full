package org.thinkbigthings.zdd.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;
import org.thinkbigthings.zdd.dto.SavedSearch;
import org.thinkbigthings.zdd.dto.SavedSearches;
import org.thinkbigthings.zdd.dto.SearchParameter;
import org.thinkbigthings.zdd.server.entity.StoreItem;
import org.thinkbigthings.zdd.server.entity.StoreItem_;
import org.thinkbigthings.zdd.server.entity.Store_;

import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SearchParsingTest {

    private ObjectMapper mapper = new ObjectMapper();

    private String savedSearchJson = """
        {
            "storeNames":["Keystone Devon"],
            "active": true,
            "searches":[
                {
                    "parameters":[
                        {"field":"strain","operator":"=","value":"Cherry Diesel"}
                    ]
                },
                {
                    "parameters":[
                        {"field":"thcPercent","operator":">=","value":"20"},
                        {"field":"weightGrams","operator":"=","value":"1"}
                    ]
                }
            ]
        }
        """;

    @Test
    public void testWriteSearchJson() throws Exception {

        SavedSearches userSearches = new SavedSearches(
                List.of("Keystone Devon"),
                List.of(
                        new SavedSearch(List.of(
                                new SearchParameter("strain", "=", "Cherry Diesel"))),
                        new SavedSearch(List.of(
                                new SearchParameter("thcPercent", ">=", "20"),
                                new SearchParameter("weightGrams", "=", "1"))
                )),
                true
        );

        String json = mapper.writeValueAsString(userSearches);
        System.out.println(json);
    }

    @Test
    public void testParseSearchJson() throws Exception {

        SavedSearches userSearches = mapper.readValue(savedSearchJson, SavedSearches.class);

        System.out.println(userSearches);
    }
}
