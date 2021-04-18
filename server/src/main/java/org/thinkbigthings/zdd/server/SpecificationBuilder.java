package org.thinkbigthings.zdd.server;

import org.springframework.data.jpa.domain.Specification;
import org.thinkbigthings.zdd.dto.SavedSearch;
import org.thinkbigthings.zdd.dto.SavedSearches;
import org.thinkbigthings.zdd.dto.SearchParameter;
import org.thinkbigthings.zdd.server.entity.StoreItem;
import org.thinkbigthings.zdd.server.entity.StoreItem_;
import org.thinkbigthings.zdd.server.entity.Store_;
import org.thinkbigthings.zdd.server.entity.Subspecies;

import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SpecificationBuilder {

    /**
     * Within SavedSearches, each individual search's parameters are ANDed together.
     * Then at this level, each individual SavedSearch are ORed together,
     * so results of those searches are all added together.
     */
    public Specification<StoreItem> toSpec(SavedSearches userSearches, Instant lastScanTime) {
        return toSpecs(userSearches, lastScanTime).stream()
                .reduce(Specification::or)
                .orElse(Specification.where(null));
    }

    /**
     * If the list of searches is empty, returns an empty List.
     * If the list of stores is empty, returns an empty List.
     *
     * @param userSearches
     * @param lastScanTime
     * @return
     */
    public List<Specification<StoreItem>> toSpecs(SavedSearches userSearches, Instant lastScanTime) {

        // CriteriaBuilder.and() by itself returns an always true predicate, and
        // CriteriaBuilder.or() returns an always false predicate
        // Can also check out .disjunction() and .conjunction()

        if(userSearches.active() && ! userSearches.storeNames().isEmpty()) {
            Specification<StoreItem> byStore = byStoreNames(userSearches.storeNames());
            Specification<StoreItem> sinceLastScan = byRecent(lastScanTime);

            return userSearches.searches().stream()
                    .map(this::toSpecs)
                    .flatMap(Optional::stream)
                    .map(spec -> spec.and(byStore))
                    .map(spec -> spec.and(sinceLastScan))
                    .collect(Collectors.toList());
        }
        else {
            return new ArrayList<>();
        }

    }

    // stores must always be present, search will be across all these
    private Specification<StoreItem> byRecent(Instant lastScanTime) {
        return (root, query, criteria) ->
                criteria.greaterThan(root.get(StoreItem_.ADDED), lastScanTime);
    }

    // stores must always be present, search will be across all these
    private Specification<StoreItem> byStoreNames(List<String> storeNames) {
        return (root, query, criteria) ->
                criteria.in(root.get(StoreItem_.STORE).get(Store_.NAME)).value(storeNames);
    }

    // at this level, all parameters must be true to be a match
    private Optional<Specification<StoreItem>> toSpecs(SavedSearch search) {
        return search.parameters().stream()
                .map(this::toSpecs)
                .reduce(Specification::and);
    }

    private Specification<StoreItem> toSpecs(SearchParameter search) {

        return (root, query, criteria) -> {

            // handle enum searches
            Object equalsSearchValue = search.value();
            if(search.field().equals(StoreItem_.SUBSPECIES)) {
                int ordinal = Integer.parseInt(search.value());
                equalsSearchValue = Subspecies.values()[ordinal];
            }

            // search field MUST match the specification field, e.g. StoreItem_.STRAIN
            Predicate persistencePredicate = switch(search.operator()) {
                case "<"  -> criteria.lessThan(            root.get(search.field()), search.value());
                case "<=" -> criteria.lessThanOrEqualTo(   root.get(search.field()), search.value());
                case "="  -> criteria.equal(               root.get(search.field()), equalsSearchValue);
                case ">=" -> criteria.greaterThanOrEqualTo(root.get(search.field()), search.value());
                case ">"  -> criteria.greaterThan(         root.get(search.field()), search.value());
                default -> throw new IllegalArgumentException("operator not recognized: " + search.operator());
            };

            return persistencePredicate;
        };
    }

}
