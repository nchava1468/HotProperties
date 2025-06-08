package com.hotproperties.hotproperties.repository;

import com.hotproperties.hotproperties.entity.Favorite;
import com.hotproperties.hotproperties.entity.Property;
import com.hotproperties.hotproperties.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUser(User user);

    Favorite findByPropertyAndUser(Property property, User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM Favorite f WHERE f.id = :id")
    void deleteFavoriteById(@Param("id") Long id);

}
