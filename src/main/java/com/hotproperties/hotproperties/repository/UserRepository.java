package com.hotproperties.hotproperties.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hotproperties.hotproperties.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    List<User> findAllByOrderByLastNameAsc();
}
