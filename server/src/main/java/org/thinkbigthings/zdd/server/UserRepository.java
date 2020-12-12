package org.thinkbigthings.zdd.server;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.thinkbigthings.zdd.server.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String name);

    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.username=?1")
    Optional<User> loadUserWithRoles(String name);

    Optional<User> findByUsername(String name);

    @Query("SELECT u FROM User u ORDER BY u.registrationTime DESC ")
    List<User> findRecent(Pageable page);

    @Query(value = "SELECT * FROM app_user ORDER BY registration_time DESC LIMIT 3", nativeQuery = true)
    List<User> findRecentNative();

}
