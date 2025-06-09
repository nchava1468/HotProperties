package com.hotproperties.hotproperties.service;

import com.hotproperties.hotproperties.entity.*;
import com.hotproperties.hotproperties.exceptions.InvalidPropertyParameterException;
import com.hotproperties.hotproperties.repository.PropertyImageRepository;
import com.hotproperties.hotproperties.repository.PropertyRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyImageRepository propertyImageRepository;
    private final PropertyImageService propertyImageService;

    @Autowired
    public PropertyServiceImpl(PropertyRepository propertyRepository, PropertyImageRepository propertyImageRepository, PropertyImageService propertyImageService) {
        this.propertyRepository = propertyRepository;
        this.propertyImageRepository = propertyImageRepository;
        this.propertyImageService = propertyImageService;
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
    public void addNewProperty(Property property, MultipartFile[] images) {

        validateTitle(property.getTitle());
        validatePrice(property.getPrice());
        validateLocation(property.getLocation());
        validateSize(property.getSize());

        propertyRepository.save(property);

        propertyImageService.storePropertyImages(property, images);

        propertyRepository.save(property);
    }

    @Override
    public void updateProperty(Long id, Property property, MultipartFile[] images) {

        Property existing = propertyRepository.findById(id).orElseThrow();

        validateTitle(property.getTitle());
        validatePrice(property.getPrice());
        validateLocation(property.getLocation());
        validateSize(property.getSize());

        existing.setTitle(property.getTitle());
        existing.setPrice(property.getPrice());
        existing.setLocation(property.getLocation());
        existing.setSize(property.getSize());
        if (property.getDescription() != null) {
            existing.setDescription(property.getDescription());
        }

        propertyImageService.storePropertyImages(property, images);

        propertyRepository.save(existing);
    }

    @Transactional
    @Override
    public void deleteImage(Long propertyId, Long imageId) {
        Property property = propertyRepository.findById(propertyId).orElseThrow();
        PropertyImage propertyImage = propertyImageRepository.findById(imageId).orElseThrow();
        property.getPropertyImages().remove(propertyImage);
        propertyImage.setProperty(null);
        propertyImageRepository.deleteById(imageId);
    }

    @Override
    @Transactional
    public void deletePropertyById(Long id) {

        Property property = propertyRepository.findById(id).orElseThrow();

        for (Favorite favorite : property.getFavorites()) {
            User user = favorite.getUser();
            user.getFavorites().remove(favorite);
        }

        for (Message message : property.getMessages()) {
            User user = message.getUser();
            user.getMessages().remove(message);
        }

        property.getFavorites().clear();
        property.getMessages().clear();

        propertyRepository.delete(property);
    }

    @Override
    public List<Property> filterProperties(Integer zip, Integer minSqFt, Integer minPrice, Integer maxPrice, String sort) {

        List<Property> properties = propertyRepository.findAll();
        List<Property> filteredProperties = new ArrayList<>();

        String zipString = (zip != null) ? zip.toString() : null;

        for (Property property : properties) {

            if (zipString != null && (property.getLocation() == null || !property.getLocation().contains(zipString))) {
                continue;
            }
            if (minSqFt != null && property.getSize() < minSqFt) {
                continue;
            }
            if (minPrice != null && property.getPrice() < minPrice) {
                continue;
            }
            if (maxPrice != null && property.getPrice() > maxPrice) {
                continue;
            }
            filteredProperties.add(property);
        }

        if (sort != null) {
            if (sort.equals("asc")) {
                filteredProperties.sort(Comparator.comparing(Property::getPrice));
            } else if (sort.equals("desc")) {
                filteredProperties.sort(Comparator.comparing(Property::getPrice).reversed());
            }
        }

        return filteredProperties;
    }

    // === VALIDATION METHODS ===

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidPropertyParameterException("Title is required");
        }
    }

    private void validatePrice(double price) {
        if (!(price > 0)) {
            throw new InvalidPropertyParameterException("Price must be greater than zero");
        }
    }

    private void validateLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            throw new InvalidPropertyParameterException("Location is required");
        }
    }

    private void validateSize(Integer size) {
        if (size == null || !(size > 0)) {
            throw new InvalidPropertyParameterException("Size must be a positive number");
        }
    }
}
