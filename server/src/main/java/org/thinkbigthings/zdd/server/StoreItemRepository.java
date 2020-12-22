package org.thinkbigthings.zdd.server;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.thinkbigthings.zdd.server.entity.StoreItem;
import org.thinkbigthings.zdd.server.entity.User;

import java.util.List;
import java.util.Optional;

public interface StoreItemRepository extends JpaRepository<StoreItem, Long> {

    @Query("SELECT t FROM StoreItem t JOIN FETCH t.terpeneAmounts")
    List<StoreItem> findAllWithTerpenes();

}
