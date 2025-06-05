package com.hotproperties.hotproperties.entity;

import jakarta.persistence.*;

@Entity
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private double price;
    private String location;
    private int size;

    @Column(columnDefinition = "TEXT")
    private String description;

    public Property() {}

    public Property(String title, String location, double price, int size, String description) {
        this.title = title;
        this.location = location;
        this.price = price;
        this.size = size;
        this.description = description;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
