package com.example.freelanceapp.artist.models;

public class Work {
    String id;
    String imageUrl;
    String artistId;

    public Work(String id, String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
    }

    public Work() {
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
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
