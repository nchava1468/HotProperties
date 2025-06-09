package com.hotproperties.hotproperties.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private double price;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String location;

    private Integer size;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PropertyImage> propertyImages = new ArrayList<>();

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Favorite> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Message> messages = new ArrayList<>();

    public Property() {}

    public Property(String title, double price, String description, String location, Integer size) {
        this.title = title;
        this.location = location;
        this.price = price;
        this.size = size;
        this.description = description;
    }

    public void addPropertyImage(PropertyImage propertyImage) {
        propertyImages.add(propertyImage);
        propertyImage.setProperty(this);
    }

    public void addFavorite(Favorite favorite) {
        favorites.add(favorite);
        favorite.setProperty(this);
    }

    public void addMessage(Message message) {
        messages.add(message);
        message.setProperty(this);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<PropertyImage> getPropertyImages() {
        return propertyImages;
    }
    public void setPropertyImages(List<PropertyImage> propertyImages) {
        this.propertyImages = propertyImages;
    }

    public List<Favorite> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<Favorite> favorites) {
        this.favorites = favorites;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}