package org.thinkbigthings.zdd.server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.thinkbigthings.zdd.server.entity.Store;
import org.thinkbigthings.zdd.server.entity.StoreItem;
import org.thinkbigthings.zdd.server.scraper.keystone.Scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import static java.util.Optional.of;
import static java.util.function.Predicate.not;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.platform.commons.util.Preconditions.condition;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.thinkbigthings.zdd.server.StoreService.contains;
import static org.thinkbigthings.zdd.server.test.data.TestData.readItems;

public class StoreServiceTest {

    private StoreRepository storeRepo = Mockito.mock(StoreRepository.class);
    private Scraper scraper = Mockito.mock(Scraper.class);

    private String storeName = "keystone";

    private StoreService service;

    @BeforeEach
    public void setup() throws IOException {

        service = new StoreService(storeRepo, scraper);

        Store savedStore = new Store(storeName, "");
        savedStore.setItems(new HashSet<>(readItems("devon-flower-20201218.json")));

        when(storeRepo.findByName(eq(savedStore.getName()))).thenReturn(of(savedStore));
    }

    @Test
    public void updateItems() throws IOException {

        Store savedStore = storeRepo.findByName(storeName).get();

        List<StoreItem> newItems = readItems("devon-flower-20201221.json");
        List<StoreItem> oldItems = new ArrayList<>(savedStore.getItems());

        // precondition on specific items
        // in both: Agri-Kind, Sour Life Savers, 1.0g, high CBD
        // new: Terrapin, Sour Tangie, 3.5g, Sativa
        // removed: Cresco, Chunky Diesel, 3.5g, Hybrid

        String strainInBoth = "Sour Life Savers";
        String strainInOldOnly = "Chunky Diesel";
        String strainInNewOnly = "Sour Tangie";

        String preconditionMessage = "Must contain expected elements";
        condition(newItems.stream().anyMatch(item -> item.getStrain().equals(strainInBoth)),    preconditionMessage);
        condition(newItems.stream().anyMatch(item -> item.getStrain().equals(strainInNewOnly)), preconditionMessage);
        condition(oldItems.stream().anyMatch(item -> item.getStrain().equals(strainInBoth)),    preconditionMessage);
        condition(oldItems.stream().anyMatch(item -> item.getStrain().equals(strainInOldOnly)), preconditionMessage);

        service.updateStoreItems(storeName, newItems);



        assertAll(
                () -> savedStore.getItems().stream().anyMatch(item -> item.getStrain().equals(strainInBoth)),
                () -> savedStore.getItems().stream().anyMatch(item -> item.getStrain().equals(strainInNewOnly)),
                () -> savedStore.getItems().stream().noneMatch(item -> item.getStrain().equals(strainInOldOnly))
        );

    }

}
