package org.thinkbigthings.zdd.server;

import org.junit.jupiter.api.Test;
import org.thinkbigthings.zdd.server.entity.Terpene;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TerpeneTest {

    private final String message = "Enums are stored by ordinal and the order should never be changed";

    @Test
    public void testOrdinalsNeverChange() {

        assertEquals(0, Terpene.BISABOLOL.ordinal(), message);
        assertEquals(1, Terpene.CARYOPHYLLENE.ordinal(), message);
        assertEquals(2, Terpene.HUMULENE.ordinal(), message);
        assertEquals(3, Terpene.LIMONENE.ordinal(), message);
        assertEquals(4, Terpene.LINALOOL.ordinal(), message);
        assertEquals(5, Terpene.MYRCENE.ordinal(), message);
        assertEquals(6, Terpene.PINENE.ordinal(), message);
        assertEquals(7, Terpene.TERPINOLENE.ordinal(), message);
    }
}
