package com.example.careitable;

import java.util.Date;

public class DonationAdsFetchObject {

    private int category;
    private String city;
    private String datePosted;
    private String description;
    private Boolean isNew;
    private String location;

    @Override
    public String toString() {
        return "DonationAdsFetchObject{" +
                "category=" + category +
                ", city='" + city + '\'' +
                ", datePosted='" + datePosted + '\'' +
                ", description='" + description + '\'' +
                ", isNew=" + isNew +
                ", location='" + location + '\'' +
                ", owner='" + owner + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", photos=" + photos +
                ", state='" + state + '\'' +
                ", title='" + title + '\'' +
                ", timestamp=" + timestamp +
                ", isDonated=" + isDonated +
                '}';
    }

    private String owner;
    private String phoneNumber;
    private int photos;
    private String state;
    private String title;
    private Date timestamp;
    private Boolean isDonated;

    public Boolean getDonated() {
        return isDonated;
    }

    public void setDonated(Boolean donated) {
        isDonated = donated;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }



    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getNew() {
        return isNew;
    }

    public void setNew(Boolean aNew) {
        isNew = aNew;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getPhotos() {
        return photos;
    }

    public void setPhotos(int photos) {
        this.photos = photos;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
