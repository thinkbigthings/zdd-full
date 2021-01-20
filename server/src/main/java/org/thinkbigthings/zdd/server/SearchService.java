package org.thinkbigthings.zdd.server;

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

public class SearchService {


    public List<Specification<StoreItem>> toSpec(SavedSearches userSearches) {

        Specification<StoreItem> byStore = byStoreNames(userSearches.storeNames());

        return userSearches.searches().stream()
                .map(this::toSpec)
                .flatMap(Optional::stream)
                .map(spec -> spec.and(byStore))
                .collect(Collectors.toList());
    }

    // stores must always be present, search will be across all these
    private Specification<StoreItem> byStoreNames(List<String> storeNames) {
        return (root, query, criteria) ->
                criteria.in(root.get(StoreItem_.STORE).get(Store_.NAME)).value(storeNames);
    }

    // at this level, all parameters must be true to be a match
    private Optional<Specification<StoreItem>> toSpec(SavedSearch search) {
        return search.parameters().stream()
                .map(this::toSpec)
                .reduce(Specification::and);
    }

    private Specification<StoreItem> toSpec(SearchParameter search) {
        return (root, query, criteria) -> {

            // search field MUST match the specification field, e.g. StoreItem_.STRAIN
            Predicate persistencePredicate = switch(search.operator()) {
                case "<"  -> criteria.lessThan(            root.get(search.field()), search.value());
                case "<=" -> criteria.lessThanOrEqualTo(   root.get(search.field()), search.value());
                case "="  -> criteria.equal(               root.get(search.field()), search.value());
                case ">=" -> criteria.greaterThanOrEqualTo(root.get(search.field()), search.value());
                case ">"  -> criteria.greaterThan(         root.get(search.field()), search.value());
                default -> throw new IllegalArgumentException("operator not recognized: " + search.operator());
            };

            return persistencePredicate;
        };
    }

}
