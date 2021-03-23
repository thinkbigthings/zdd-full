package org.thinkbigthings.zdd.dto;

import java.util.ArrayList;
import java.util.List;

public record SavedSearch(List<SearchParameter> parameters) {

    public SavedSearch() {
        this(List.of());
    }

    public SavedSearch withParameter(String field, String operator, String value) {
        List<SearchParameter> newParameters = new ArrayList<>(parameters);
        newParameters.add(new SearchParameter(field, operator, value));
        return new SavedSearch(List.copyOf(newParameters));
    }
}
