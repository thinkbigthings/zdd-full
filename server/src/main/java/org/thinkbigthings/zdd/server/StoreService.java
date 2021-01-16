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
import java.util.*;
import java.util.function.Predicate;

import static java.util.function.Predicate.not;

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

    public static <T> boolean contains(Collection<T> collection, T element, Comparator<T> comparator) {
        return collection.stream().anyMatch(e -> comparator.compare(e, element) == 0);
    }

    public static <T> boolean retainAll(Collection<T> collection, Collection<T> elementsToRetain, Comparator<T> comparator) {
        return collection.removeIf(not(e -> contains(elementsToRetain, e, comparator)));
    }

    public static <T> boolean addAll(Collection<T> collection, Collection<T> elementsToAdd, Comparator<T> comparator) {
        return elementsToAdd.stream()
                .filter(not(e -> contains(collection, e, comparator)))
                .map(collection::add)
                .reduce(false, (b1, b2) -> b1 || b2);
    }

    @Transactional
    public void updateStoreItems(String storeName, List<StoreItem> newItems) {

        Store store = storeRepository.findByName(storeName).get();

        Set<StoreItem> originalItems = store.getItems();

        Comparator<StoreItem> comparator = Comparator.comparing(StoreItem::getStrain)
                .thenComparing(StoreItem::getSubspecies)
                .thenComparing(StoreItem::getWeightGrams)
                .thenComparing(StoreItem::getVendor);

        // remove old items not in new dataset, then add all new items
        // use comparator instead of .equals() since new items weren't persisted yet
        // could be made faster if we removed items from the new collection that are already in old collection
        retainAll(originalItems, newItems, comparator);
        addAll(originalItems, newItems, comparator);

        Instant added = Instant.now();
        newItems.forEach(item -> item.setAdded(added));
        newItems.forEach(item -> item.setStore(store));

        storeRepository.save(store);
    }
}
