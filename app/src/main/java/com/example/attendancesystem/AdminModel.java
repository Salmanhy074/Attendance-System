package com.example.attendancesystem;

public class AdminModel {
    String username, email, number, address, cnic;
    String profileImage;

    public AdminModel() {
    }

    public AdminModel(String username, String email, String number, String address, String cnic, String profileImage) {
        this.username = username;
        this.email = email;
        this.number = number;
        this.address = address;
        this.cnic = cnic;
        this.profileImage = profileImage;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }
    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImageUrl) {
        this.profileImage = profileImageUrl;
    }
}
