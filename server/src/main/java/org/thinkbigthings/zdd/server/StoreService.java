package org.thinkbigthings.zdd.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thinkbigthings.zdd.dto.*;
import org.thinkbigthings.zdd.server.entity.*;

@Service
public class StoreService {

    private static Logger LOG = LoggerFactory.getLogger(StoreService.class);

    private StoreRepository storeRepository;

    public StoreService(StoreRepository repo) {
        this.storeRepository = repo;
    }

    @Transactional(readOnly = true)
    public Store getStore(String storeName) {
        return storeRepository.findByName(storeName).get();
    }

    @Transactional
    public Store saveNewStore(StoreRecord newStore) {
        return storeRepository.save(new Store(newStore.name(), newStore.website()));
    }

}
