package org.thinkbigthings.zdd.server;


import org.springframework.data.jpa.repository.JpaRepository;
import org.thinkbigthings.zdd.server.entity.Store;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    Optional<Store> findByName(String name);

}
