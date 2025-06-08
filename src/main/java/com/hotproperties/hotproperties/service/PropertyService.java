package com.hotproperties.hotproperties.service;

import com.hotproperties.hotproperties.entity.Property;
import com.hotproperties.hotproperties.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface PropertyService {

    List<Property> findAllProperties();

    Property findById(Long id);

    @PreAuthorize("hasRole('AGENT')")
    void addNewProperty(Property property);

}
