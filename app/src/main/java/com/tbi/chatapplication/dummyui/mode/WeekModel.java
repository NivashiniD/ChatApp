package com.tbi.chatapplication.dummyui.mode;

public class WeekModel {
    int id;
    private String date;
    private String day;
    private boolean isSelected;

    public WeekModel(int id, String date, String day, boolean isSelected) {
        this.id = id;
        this.date = date;
        this.day = day;
        this.isSelected = isSelected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
