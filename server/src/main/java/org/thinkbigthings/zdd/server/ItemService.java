package org.thinkbigthings.zdd.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thinkbigthings.zdd.dto.SavedSearches;
import org.thinkbigthings.zdd.server.entity.StoreItem;
import org.thinkbigthings.zdd.server.mapper.entitytodto.ItemMapper;
import org.thinkbigthings.zdd.server.scraper.keystone.Item;

import java.time.Instant;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ItemService {

    private static Logger LOG = LoggerFactory.getLogger(ItemService.class);

    private StoreItemRepository itemRepository;

    private SpecificationBuilder specBuilder = new SpecificationBuilder();
    private ItemMapper toItemDto = new ItemMapper();

    public ItemService(StoreItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Transactional(readOnly = true)
    public Page<Item> findItems(Pageable page) {
        return itemRepository.findAll(page).map(toItemDto);
    }

    @Transactional(readOnly = true)
    public List<StoreItem> search(SavedSearches userSearches, Instant lastScan) {

        // TODO integration test a more complex query with mixed ors and ands
        // compare queries with both techniques and keep the best one

        Specification<StoreItem> search = specBuilder.toSpec(userSearches, lastScan);
        return itemRepository.findAll(search);

//        return specBuilder.toSpecs(userSearches, lastScan).stream()
//                .map(itemRepository::findAll)
//                .flatMap(List::stream)
//                .collect(toList());
    }
}
