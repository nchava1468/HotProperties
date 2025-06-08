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

    void updateProperty(Long id, Property property, MultipartFile[] images);

    void deleteImage(Long propertyId, Long imageId);

    void deletePropertyById(Long id);
}