package org.thinkbigthings.zdd.server.mapper.entitytodto;

import org.thinkbigthings.zdd.server.entity.StoreItem;
import org.thinkbigthings.zdd.server.scraper.keystone.Item;
import org.thinkbigthings.zdd.server.scraper.keystone.TerpeneAmount;

import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class ItemMapper implements Function<StoreItem, Item> {

    @Override
    public Item apply(StoreItem item) {
        return new Item(item.getSubspecies(),
                item.getStrain(),
                item.getThcPercent(),
                item.getCbdPercent(),
                item.getTerpeneAmounts().stream()
                        .map(t -> new TerpeneAmount(t.getTerpene(), t.getTerpenePercent()))
                        .collect(toList()),
                item.getWeightGrams(),
                item.getPriceDollars(),
                item.getVendor());
    }
}
