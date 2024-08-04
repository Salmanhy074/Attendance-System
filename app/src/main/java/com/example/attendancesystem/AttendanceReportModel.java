package com.example.attendancesystem;

public class AttendanceReportModel {

    private String username;
    private int daysPresent;
    private int daysAbsent;
    private String grade;

    public AttendanceReportModel() {
    }

    public AttendanceReportModel(String username, int daysPresent, int daysAbsent, String grade) {
        this.username = username;
        this.daysPresent = daysPresent;
        this.daysAbsent = daysAbsent;
        this.grade = grade;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getDaysPresent() {
        return daysPresent;
    }

    public void setDaysPresent(int daysPresent) {
        this.daysPresent = daysPresent;
    }

    public int getDaysAbsent() {
        return daysAbsent;
    }

    public void setDaysAbsent(int daysAbsent) {
        this.daysAbsent = daysAbsent;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
