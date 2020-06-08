package org.thinkbigthings.zdd.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoleTest {


    @Test
    public void testOrdinalsNeverChange() {

        // these enums are stored by ordinal in the database, so don't change the order!
        assertEquals(0, User.Role.ADMIN.ordinal());
        assertEquals(1, User.Role.USER.ordinal());
    }
}
