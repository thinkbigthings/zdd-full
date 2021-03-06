package org.thinkbigthings.zdd.server;

import org.junit.jupiter.api.Test;
import org.thinkbigthings.zdd.server.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoleTest {

    private final String message = "Enums are stored by ordinal and the order should never be changed";

    @Test
    public void testOrdinalsNeverChange() {

        assertEquals(0, User.Role.ADMIN.ordinal(), message);
        assertEquals(1, User.Role.USER.ordinal(), message);
    }
}
