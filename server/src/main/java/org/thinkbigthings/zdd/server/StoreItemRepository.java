package org.thinkbigthings.zdd.server;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thinkbigthings.zdd.server.entity.StoreItem;

public interface StoreItemRepository extends JpaRepository<StoreItem, Long> {

    @Query(value = "SELECT t FROM StoreItem t " +
            "JOIN FETCH t.terpeneAmounts " +
            "ORDER BY t.added DESC",
            countQuery = "SELECT COUNT(t) FROM StoreItem t")
    Page<StoreItem> findAllWithTerpenes(Pageable page);

    @Query(value = "SELECT t FROM StoreItem t " +
            "JOIN FETCH t.terpeneAmounts " +
            "WHERE t.store.name=:storename " +
            "ORDER BY t.added DESC",
            countQuery = "SELECT COUNT(t) FROM StoreItem t WHERE t.store.name=:storename")
    Page<StoreItem> findByStoreName(@Param("storename") String storename, Pageable page);

}
