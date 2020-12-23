package org.thinkbigthings.zdd.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thinkbigthings.zdd.dto.*;
import org.thinkbigthings.zdd.server.entity.*;
import org.thinkbigthings.zdd.server.scraper.keystone.Scraper;

import java.time.Instant;
import java.util.List;

@Service
public class StoreService {

    private static Logger LOG = LoggerFactory.getLogger(StoreService.class);

    private StoreRepository storeRepository;
    private Scraper scraper;

    public StoreService(StoreRepository repo, Scraper scraper) {
        this.storeRepository = repo;
        this.scraper = scraper;
    }

    @Transactional(readOnly = true)
    public Page<StoreRecord> getStores(Pageable page) {
        return storeRepository.loadSummaries(page);
    }

    @Transactional(readOnly = true)
    public Store getStore(String storeName) {
        return storeRepository.findByName(storeName).get();
    }

    @Transactional
    public Store saveNewStore(StoreRecord newStore) {
        return storeRepository.save(new Store(newStore.name(), newStore.website()));
    }

    @Transactional
    public void scrapeStore(String storeName) {
        Store store = storeRepository.findByName(storeName).get();
        List<StoreItem> items = scraper.scrape(store.getWebsite());
        updateStoreItems(storeName, items);
    }

    @Transactional
    public void updateStoreItems(String storeName, List<StoreItem> items) {

        Store store = storeRepository.findByName(storeName).get();

        store.getItems().clear();
        store.getItems().addAll(items);
        items.forEach(item -> item.setStore(store));

        store.setUpdated(Instant.now());

        storeRepository.save(store);
    }
}
