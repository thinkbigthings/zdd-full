package org.thinkbigthings.zdd.dto;

import java.util.List;

public record SavedSearches(List<String> storeNames, List<SavedSearch> searches) {
}
