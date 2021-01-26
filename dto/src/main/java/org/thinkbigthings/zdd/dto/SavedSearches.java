package org.thinkbigthings.zdd.dto;

import java.util.ArrayList;
import java.util.List;

public record SavedSearches(List<String> storeNames, List<SavedSearch> searches, boolean active) {

    public static SavedSearches newSavedSearches() {
        return new SavedSearches(new ArrayList<>(), new ArrayList<>(), true);
    }

    public SavedSearches withStore(String storeName) {
        List<String> newStoreNames = new ArrayList<>(storeNames);
        newStoreNames.add(storeName);
        return new SavedSearches(List.copyOf(newStoreNames), searches, active);
    }

    public SavedSearches withSearch(String field, String operator, String value) {
        List<SavedSearch> newSearches = new ArrayList<>(searches);
        newSearches.add(new SavedSearch(List.of(new SearchParameter(field, operator, value))));
        return new SavedSearches(storeNames, List.copyOf(newSearches), active);
    }

    public SavedSearches clearSearch() {
        return new SavedSearches(storeNames, List.of(), active);
    }
}
