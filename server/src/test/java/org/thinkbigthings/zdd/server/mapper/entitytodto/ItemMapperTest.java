package org.thinkbigthings.zdd.server.mapper.entitytodto;

import org.junit.jupiter.api.Test;
import org.thinkbigthings.zdd.server.entity.Role;
import org.thinkbigthings.zdd.server.entity.StoreItem;
import org.thinkbigthings.zdd.server.scraper.keystone.Item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.thinkbigthings.zdd.server.test.data.TestData.randomItem;

public class ItemMapperTest {

    private ItemMapper toItemDto = new ItemMapper();

    @Test
    public void testOrdinalsNeverChange() {

        StoreItem entity = randomItem();

        Item item = toItemDto.apply(entity);

        assertEquals(entity.getThcPercent(), item.thc());
        assertEquals(entity.getCbdPercent(), item.cbd());
        assertEquals(entity.getTerpeneAmounts().size(), item.terpeneAmounts().size());
    }
}
