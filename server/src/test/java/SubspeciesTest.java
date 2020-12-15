import org.junit.jupiter.api.Test;
import org.thinkbigthings.zdd.server.scraper.keystone.Subspecies;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubspeciesTest {

    private final String message = "Enums are stored by ordinal and the order should never be changed";

    @Test
    public void testOrdinalsNeverChange() {

        assertEquals(0, Subspecies.SATIVA.ordinal(), message);
        assertEquals(1, Subspecies.SATIVA_HYBRID.ordinal(), message);
        assertEquals(0, Subspecies.HYBRID.ordinal(), message);
        assertEquals(1, Subspecies.INDICA_HYBRID.ordinal(), message);
        assertEquals(0, Subspecies.INDICA.ordinal(), message);
        assertEquals(1, Subspecies.HIGH_CBD.ordinal(), message);
    }
}
