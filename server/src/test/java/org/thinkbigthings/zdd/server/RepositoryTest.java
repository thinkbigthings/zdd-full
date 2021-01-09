package org.thinkbigthings.zdd.server;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.thinkbigthings.zdd.server.entity.Address;
import org.thinkbigthings.zdd.server.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest extends IntegrationTest {

    private static Logger LOG = LoggerFactory.getLogger(RepositoryTest.class);

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    public void testJoinFetch() {

        // observe the effect in the debugger and the SQL of deleting "JOIN FETCH u.roles"
        Optional<User> admin = userRepository.loadUserWithRoles("admin");
//        Optional<User> admin = userRepository.findByUsername("admin");
        Set<User.Role> roles = admin.map(user -> user.getRoles()).get();
        assertFalse(roles.isEmpty());
        assertTrue(admin.isPresent());

    }

    @Test
    public void testNPlusOne() {

        // even lazy associations can trigger N+1 queries when loaded
        // demonstrate what happens with .loadSummaries() and .loadUserWithRoles() vs this.
        List<User> recentUsers = userRepository.findRecent(PageRequest.of(0, 10));

        // demonstrate all the queries that happen from just a simple entity access
//        List<Address> addresses = recentUsers.stream()
//                .flatMap(u -> u.getAddresses().stream())
//                .collect(toList());

    }

    @Test
    public void testListUsers() {

        long numberUsers = userRepository.count();
        Page<User> userPage = userRepository.findAll(PageRequest.of(0, 10));

        List<User> users = userPage.getContent();

//        assertEquals(10, users.size());
        assertTrue(numberUsers > 0);
    }
}
