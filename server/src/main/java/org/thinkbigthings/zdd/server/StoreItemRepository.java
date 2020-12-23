package org.thinkbigthings.zdd.server;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.thinkbigthings.zdd.server.entity.StoreItem;

public interface StoreItemRepository extends JpaRepository<StoreItem, Long> {

    @Query(value = "SELECT t FROM StoreItem t JOIN FETCH t.terpeneAmounts",
            countQuery = "SELECT COUNT(t) FROM StoreItem t")
    Page<StoreItem> findAllWithTerpenes(Pageable page);

}
