package com.hotproperties.hotproperties.service;

import com.hotproperties.hotproperties.entity.Property;
import com.hotproperties.hotproperties.entity.PropertyImage;
import com.hotproperties.hotproperties.exceptions.InvalidPropertyImageParameterException;
import com.hotproperties.hotproperties.repository.PropertyImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class PropertyImageServiceImpl implements PropertyImageService {

    private final PropertyImageRepository propertyImageRepository;

    @Autowired
    public PropertyImageServiceImpl(PropertyImageRepository propertyImageRepository) {
        this.propertyImageRepository = propertyImageRepository;
    }

    @Override
    public void storePropertyImages(Property property, MultipartFile[] images) throws InvalidPropertyImageParameterException {

        if (images == null) {
            return;
        }

        for (MultipartFile file : images) {
            if (!file.isEmpty()) {
                try {
                    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

                    Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads", "property-images");
                    Files.createDirectories(uploadPath);

                    Path filePath = uploadPath.resolve(fileName);
                    file.transferTo(filePath.toFile());

                    PropertyImage propertyImage = new PropertyImage();
                    propertyImage.setFileName(fileName);
                    propertyImage.setProperty(property);
                    property.addPropertyImage(propertyImage);

                    propertyImageRepository.save(propertyImage);

                } catch (IOException e) {
                    throw new RuntimeException("Failed to save property image: " + file.getOriginalFilename(), e);
                }
            }
        }
    }

}
