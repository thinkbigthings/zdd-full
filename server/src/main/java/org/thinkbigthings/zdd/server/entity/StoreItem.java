package org.thinkbigthings.zdd.server.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "store_item")
public class StoreItem {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, insertable = false, nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "store_item_id", referencedColumnName = "id")
    private Store store;

    @Column(name = "subspecies_id")
    @Enumerated(EnumType.ORDINAL)
    private Subspecies subspecies;

    @NotNull
    private String strain = "";

    @NotNull
    private BigDecimal thcPercent;

    @NotNull
    private BigDecimal cbdPercent;

    @OneToMany(fetch=FetchType.LAZY, mappedBy = "storeItem", cascade = CascadeType.ALL, orphanRemoval=true)
    private Set<TerpeneAmount> terpeneAmounts = new HashSet<>();

    @NotNull
    private BigDecimal weightGrams;

    @NotNull
    private Long priceDollars;

    @NotNull
    private String vendor = "";

    public StoreItem() {

    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Subspecies getSubspecies() {
        return subspecies;
    }

    public void setSubspecies(Subspecies subspecies) {
        this.subspecies = subspecies;
    }

    public String getStrain() {
        return strain;
    }

    public void setStrain(String strain) {
        this.strain = strain;
    }

    public BigDecimal getThcPercent() {
        return thcPercent;
    }

    public void setThcPercent(BigDecimal thcPercent) {
        this.thcPercent = thcPercent;
    }

    public BigDecimal getCbdPercent() {
        return cbdPercent;
    }

    public void setCbdPercent(BigDecimal cbdPercent) {
        this.cbdPercent = cbdPercent;
    }

    public Set<TerpeneAmount> getTerpeneAmounts() {
        return terpeneAmounts;
    }

    public void setTerpeneAmounts(Set<TerpeneAmount> terpeneAmounts) {
        this.terpeneAmounts = terpeneAmounts;
    }

    public BigDecimal getWeightGrams() {
        return weightGrams;
    }

    public void setWeightGrams(BigDecimal weightGrams) {
        this.weightGrams = weightGrams;
    }

    public Long getPriceDollars() {
        return priceDollars;
    }

    public void setPriceDollars(Long priceDollars) {
        this.priceDollars = priceDollars;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }
}
