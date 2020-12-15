package org.thinkbigthings.zdd.server;


import org.springframework.data.jpa.repository.JpaRepository;
import org.thinkbigthings.zdd.server.entity.StoreItem;

public interface StoreItemRepository extends JpaRepository<StoreItem, Long> {


}
