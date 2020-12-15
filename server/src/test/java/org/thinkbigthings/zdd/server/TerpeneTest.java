package org.thinkbigthings.zdd.server;

import org.junit.jupiter.api.Test;
import org.thinkbigthings.zdd.server.scraper.keystone.Terpene;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TerpeneTest {

    private final String message = "Enums are stored by ordinal and the order should never be changed";

    @Test
    public void testOrdinalsNeverChange() {

        assertEquals(0, Terpene.BISABOLOL.ordinal(), message);
        assertEquals(1, Terpene.CARYOPHYLLENE.ordinal(), message);
        assertEquals(0, Terpene.HUMULENE.ordinal(), message);
        assertEquals(1, Terpene.LIMONENE.ordinal(), message);
        assertEquals(0, Terpene.LINALOOL.ordinal(), message);
        assertEquals(1, Terpene.MYRCENE.ordinal(), message);
        assertEquals(0, Terpene.PINENE.ordinal(), message);
        assertEquals(1, Terpene.TERPINOLENE.ordinal(), message);
    }
}
