package com.hotproperties.hotproperties.service;

import com.hotproperties.hotproperties.entity.Property;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PropertyService {

    List<Property> findAllProperties();

    Property findById(Long id);

    @PreAuthorize("hasRole('AGENT')")
    void addNewProperty(Property property, MultipartFile[] images);

    @PreAuthorize("hasRole('AGENT')")
    void updateProperty(Long id, Property property, MultipartFile[] images);

    @PreAuthorize("hasRole('AGENT')")
    void deleteImage(Long propertyId, Long imageId);

    @PreAuthorize("hasRole('AGENT')")
    void deletePropertyById(Long id);

    List<Property> filterProperties(Integer zip, Integer minSqFt, Integer minPrice, Integer maxPrice, String sort);
}