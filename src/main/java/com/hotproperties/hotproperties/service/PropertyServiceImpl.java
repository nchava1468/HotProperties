package com.hotproperties.hotproperties.service;

import com.hotproperties.hotproperties.entity.Property;
import com.hotproperties.hotproperties.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;

    @Autowired
    public PropertyServiceImpl(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @Override
    public List<Property> findAllProperties() {
        return propertyRepository.findAll();
    }

    @Override
    public Property findById(Long id) {
        return propertyRepository.findById(id).orElseThrow();
    }

    @Override
    public List<Property> findAll() {
        return propertyRepository.findAll();
    }

    @Override
    public void addNewProperty(Property property) {
        validateTitle(property.getTitle());
        validatePrice(property.getPrice());
        validateLocation(property.getLocation());
        validateSize(property.getSize());

        propertyRepository.save(property);
    }

    // === VALIDATION METHODS ===

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
    }

    private void validatePrice(double price) {
        if (!(price > 0)) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
    }

    private void validateLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location is required");
        }
    }

    private void validateSize(Integer size) {
        if (size == null || !(size > 0)) {
            throw new IllegalArgumentException("Size must be a positive number");
        }
    }


}
