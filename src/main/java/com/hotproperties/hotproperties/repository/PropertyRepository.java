package com.hotproperties.hotproperties.repository;

import com.hotproperties.hotproperties.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    // Add custom queries here if needed in the future
}
