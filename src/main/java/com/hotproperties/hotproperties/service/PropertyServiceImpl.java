package com.hotproperties.hotproperties.service;

import com.hotproperties.hotproperties.entity.Property;
import com.hotproperties.hotproperties.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
