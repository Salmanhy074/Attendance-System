package com.example.attendancesystem;

public class AttendanceModel {
    private String userId;
    private String date;
    private String status;
    private String email;
    private String username;

    public AttendanceModel() {
    }

    public AttendanceModel(String userId, String date, String status, String email, String username) {
        this.userId = userId;
        this.date = date;
        this.status = status;
        this.email = email;
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

