package com.hotproperties.hotproperties.service;

import com.hotproperties.hotproperties.entity.Property;
import com.hotproperties.hotproperties.entity.PropertyImage;
import com.hotproperties.hotproperties.exceptions.InvalidPropertyParameterException;
import com.hotproperties.hotproperties.exceptions.InvalidUserParameterException;
import com.hotproperties.hotproperties.repository.PropertyImageRepository;
import com.hotproperties.hotproperties.repository.PropertyRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyImageRepository propertyImageRepository;

    @Autowired
    public PropertyServiceImpl(PropertyRepository propertyRepository, PropertyImageRepository propertyImageRepository) {
        this.propertyRepository = propertyRepository;
        this.propertyImageRepository = propertyImageRepository;
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

        propertyRepository.save(property); // Save first to get ID

        if (images != null) {
            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    try {
                        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                        String uploadDir = "uploads/property-images/";
                        File dest = new File(uploadDir + fileName);
                        dest.getParentFile().mkdirs();
                        file.transferTo(dest);

                        PropertyImage img = new PropertyImage();
                        img.setFileName(fileName);
                        img.setProperty(property);
                        property.addPropertyImage(img);
                        propertyImageRepository.save(img);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        propertyRepository.save(property); // Save again with images
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
        existing.setDescription(property.getDescription());

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
    public void deletePropertyById(Long id) {
        propertyRepository.deleteById(id);
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
