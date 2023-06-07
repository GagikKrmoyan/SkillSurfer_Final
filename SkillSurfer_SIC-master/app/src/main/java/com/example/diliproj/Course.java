package com.example.diliproj;

import java.util.HashMap;
import java.util.Map;

public class Course {
    private String title;
    private String description;
    private String place;
    private String paidOrFree;
    private double price;
    private String category;
    private String imageUrl;

    public Course() {

    }

    public Course(String title, String description, String place, String paidOrFree, double price, String category) {
        this.title = title;
        this.description = description;
        this.place = place;
        this.paidOrFree = paidOrFree;
        this.price = price;
        this.category = category;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPaidOrFree() {
        return paidOrFree;
    }

    public void setPaidOrFree(String paidOrFree) {
        this.paidOrFree = paidOrFree;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}




