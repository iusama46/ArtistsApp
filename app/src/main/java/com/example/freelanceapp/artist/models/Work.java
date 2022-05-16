package com.example.freelanceapp.artist.models;

public class Work {
    String id;
    String imageUrl;

    public Work(String id, String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
    }

    public Work() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
