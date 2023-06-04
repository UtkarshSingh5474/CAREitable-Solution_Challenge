package com.example.careitable.dao;

import java.util.List;

public class UserData {
    List<String> donationAdIds;
    String name, email, phone;


    public List<String> getDonationAdIds() {
        return donationAdIds;
    }

    public void setDonationAdIds(List<String> donationAdIds) {
        this.donationAdIds = donationAdIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

