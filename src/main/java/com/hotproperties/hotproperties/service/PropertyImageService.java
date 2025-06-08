package com.hotproperties.hotproperties.service;

import com.hotproperties.hotproperties.entity.Property;
import org.springframework.web.multipart.MultipartFile;

public interface PropertyImageService {
    void storePropertyImages(Property property, MultipartFile[] images);
}
