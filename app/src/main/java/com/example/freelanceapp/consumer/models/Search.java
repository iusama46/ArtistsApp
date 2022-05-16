package com.example.freelanceapp.consumer.models;

import java.util.List;
import java.util.Locale;

public class Search {
    String id;
    String name;
    String area;
    String experience;
    String hourlyRate;
    List<String> categories;




    public Search() {
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public Search(String id, String name, String area, String experience, String hourlyRate) {
        this.id = id;
        this.name = name;
        this.area = area;
        this.experience = experience;
        this.hourlyRate = hourlyRate;
    }

    public String getArea() {
        if (area != null)
            return area.toLowerCase(Locale.ROOT);
        else return "";
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(String hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
}
