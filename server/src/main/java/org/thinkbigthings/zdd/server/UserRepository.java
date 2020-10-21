package org.thinkbigthings.zdd.server;


import org.springframework.data.jpa.repository.JpaRepository;
import org.thinkbigthings.zdd.server.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String name);

    // TODO consider returning entity not optional
    // so I don't have to check existence all the time myself
    // Spring Data throws the same exception, right?
    Optional<User> findByUsername(String name);

}
