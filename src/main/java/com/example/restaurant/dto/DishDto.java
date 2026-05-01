package com.example.restaurant.dto;

import java.math.BigDecimal;

public class DishDto {
    
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String imagePath;
    private boolean available;
    private Integer preparationTime;
    private Double rating;
    private Integer calories;
    
    public DishDto() {
    }
    
    public DishDto(Long id, String name, String description, BigDecimal price, 
                   String category, String imagePath, boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imagePath = imagePath;
        this.available = available;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getImagePath() {
        return imagePath;
    }
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    public Integer getPreparationTime() {
        return preparationTime;
    }
    
    public void setPreparationTime(Integer preparationTime) {
        this.preparationTime = preparationTime;
    }
    
    public Double getRating() {
        return rating;
    }
    
    public void setRating(Double rating) {
        this.rating = rating;
    }
    
    public Integer getCalories() {
        return calories;
    }
    
    public void setCalories(Integer calories) {
        this.calories = calories;
    }
}