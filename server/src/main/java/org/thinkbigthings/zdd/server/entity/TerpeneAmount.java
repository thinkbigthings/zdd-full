package org.thinkbigthings.zdd.server.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "terpene_amount")
public class TerpeneAmount {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, insertable = false, nullable = false)
    private Long id;

    @Column(name = "terpene_id")
    @Enumerated(EnumType.ORDINAL)
    private Terpene terpene;

    @NotNull
    private BigDecimal terpenePercent;

    @NotNull
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "store_item_id", referencedColumnName = "id")
    private StoreItem storeItem;

    public TerpeneAmount() {

    }

    public TerpeneAmount(Terpene terpene, BigDecimal terpenePercent) {
        this.terpene = terpene;
        this.terpenePercent = terpenePercent;
    }

    public Terpene getTerpene() {
        return terpene;
    }

    public void setTerpene(Terpene terpene) {
        this.terpene = terpene;
    }

    public BigDecimal getTerpenePercent() {
        return terpenePercent;
    }

    public void setTerpenePercent(BigDecimal terpenePercent) {
        this.terpenePercent = terpenePercent;
    }

    public StoreItem getStoreItem() {
        return storeItem;
    }

    public void setStoreItem(StoreItem storeItem) {
        this.storeItem = storeItem;
    }
}
