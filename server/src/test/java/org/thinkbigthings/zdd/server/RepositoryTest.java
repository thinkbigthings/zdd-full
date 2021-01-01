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
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class RepositoryTest extends IntegrationTest {

    private static Logger LOG = LoggerFactory.getLogger(RepositoryTest.class);

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testJoinFetch() {

        // observe the effect in the debugger and the SQL of deleting "JOIN FETCH u.roles"
        Optional<User> admin = userRepository.loadUserWithRoles("admin");
        assertTrue(admin.isPresent());
    }

    @Disabled("seed data needs to be added in this test, otherwise it depends on other tests running first")
    @Test
    public void testOrphanRemoval() {

        LOG.info("findAll");
        Page<User> userPage = userRepository.findAll(PageRequest.of(0, 10));

        LOG.info("filter");
        List<User> usersWithAddresses = userPage.getContent().stream()
                .filter(Predicate.not(user -> user.getAddresses().isEmpty()))
                .collect(toList());

        assertTrue(usersWithAddresses.size() > 0);
    }

    @Test
    public void testNPlusOne() {

        // TODO this has the N+1 problem since address and session are eager
        // but even lazy associations can trigger N+1 queries when loaded
        // try join fetch in JPQL
//        List<User> recentUsers = userRepository.findRecentNative();

        List<User> recentUsers = userRepository.findRecent(PageRequest.of(0, 10));


        // shows all the queries that happen from just a simple entity access
//        List<Address> addresses = recentUsers.stream()
//                .flatMap(u -> u.getAddresses().stream())
//                .collect(toList());

//        assertTrue(addresses.size() > 0);
//        assertEquals(10, recentUsers.size());
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
