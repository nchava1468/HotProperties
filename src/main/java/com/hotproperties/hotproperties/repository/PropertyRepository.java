package com.hotproperties.hotproperties.repository;

import com.hotproperties.hotproperties.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PropertyRepository extends JpaRepository<Property, Long> {
}

