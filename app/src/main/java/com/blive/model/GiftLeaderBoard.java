package com.blive.model;


import java.io.Serializable;
import java.util.ArrayList;

public class GiftLeaderBoard implements Serializable {

    private ArrayList<User> hour = new ArrayList<>();
    private ArrayList<User> day = new ArrayList<>();
    private ArrayList<User> week = new ArrayList<>();

    public GiftLeaderBoard() {
    }

    public ArrayList<User> getHour() {
        return hour;
    }

    public void setHour(ArrayList<User> hour) {
        this.hour = hour;
    }

    public ArrayList<User> getDay() {
        return day;
    }

    public void setDay(ArrayList<User> day) {
        this.day = day;
    }

    public ArrayList<User> getWeek() {
        return week;
    }

    public void setWeek(ArrayList<User> week) {
        this.week = week;
    }

    @Override
    public String toString() {
        return "GiftLeaderBoard{" +
                "hour=" + hour +
                ", day=" + day +
                ", week=" + week +
                '}';
    }
}
