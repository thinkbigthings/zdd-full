package org.thinkbigthings.zdd.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thinkbigthings.zdd.dto.UserSummary;
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
    public Page<Item> findItems(Pageable page) {
        return itemRepository.findAllWithTerpenes(page).map(toItemDto);
    }

    @Transactional
    public void scrapeStore(String storeName) {

        List<StoreItem> items = scraper.scrape(storeName);

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
