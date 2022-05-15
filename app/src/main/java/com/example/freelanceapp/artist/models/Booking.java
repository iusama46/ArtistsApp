package com.example.freelanceapp.artist.models;

public class Booking {
    String uId;
    String bookedById;
    String bookedByName;
    String date;

    public Booking(String uId, String bookedById, String bookedByName, String date) {
        this.uId = uId;
        this.bookedById = bookedById;
        this.bookedByName = bookedByName;
        this.date = date;
    }

    public Booking() {
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getBookedById() {
        return bookedById;
    }

    public void setBookedById(String bookedById) {
        this.bookedById = bookedById;
    }

    public String getBookedByName() {
        return bookedByName;
    }

    public void setBookedByName(String bookedByName) {
        this.bookedByName = bookedByName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
