package org.thinkbigthings.zdd.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thinkbigthings.zdd.server.entity.Store;
import org.thinkbigthings.zdd.server.entity.StoreItem;
import org.thinkbigthings.zdd.server.mapper.entitytodto.ItemMapper;
import org.thinkbigthings.zdd.server.scraper.keystone.Item;
import org.thinkbigthings.zdd.server.scraper.keystone.Scraper;

import java.time.Instant;
import java.util.List;

import static java.util.stream.Collectors.toList;


@Service
public class ItemService {

    private static Logger LOG = LoggerFactory.getLogger(ItemService.class);

    private StoreItemRepository itemRepository;
    private StoreRepository storeRepository;
    private Scraper scraper;

    private ItemMapper toItemDto = new ItemMapper();

    public ItemService(StoreItemRepository itemRepository, StoreRepository storeRepository, Scraper scraper) {
        this.itemRepository = itemRepository;
        this.storeRepository = storeRepository;
        this.scraper = scraper;
    }

    @Transactional(readOnly = true)
    public List<Item> findItems() {
        return itemRepository.findAllWithTerpenes().stream()
                .map(toItemDto)
                .collect(toList());
    }

    @Transactional
    public void scrapeStore(String storeName) {

        Store store = storeRepository.findByName(storeName).get();

        List<StoreItem> items = scraper.scrape(store.getWebsite());

        updateStoreItems(store, items);

        storeRepository.save(store);
    }

    public void updateStoreItems(Store store, List<StoreItem> items) {

        store.getItems().clear();
        store.getItems().addAll(items);
        items.forEach(item -> item.setStore(store));

        store.setUpdated(Instant.now());
    }

}
