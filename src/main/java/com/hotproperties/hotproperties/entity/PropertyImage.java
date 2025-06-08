package com.hotproperties.hotproperties.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class PropertyImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    @ManyToOne
    @JoinColumn(name = "property_id")
    @JsonIgnore
    private Property property;

    public PropertyImage () {
    }

    public PropertyImage(String fileName) {
        this.fileName = fileName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Property getProperty () {
        return property;
    }

    public void setProperty (Property property) {
        this.property = property;
    }
}
