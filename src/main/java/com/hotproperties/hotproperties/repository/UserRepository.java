package com.hotproperties.hotproperties.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hotproperties.hotproperties.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    List<User> findByManager(User manager);

    List<User> findByManagerIsNull();

    List<User> findAllByOrderByLastNameAsc();
}
