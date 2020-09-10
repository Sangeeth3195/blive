package com.blive.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sans on 06-04-2019.
 **/

public class LeaderBoardResponse implements Serializable {

    @SerializedName("data")
    private Data data;
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;

    public class Data {

        @SerializedName("hour")
        private ArrayList<User> hourlyUsers;

        @SerializedName("day")
        private ArrayList<User> dayUsers;

        @SerializedName("week")
        private ArrayList<User> weeklyUsers;

        public ArrayList<User> getHourlyUsers() {
            return hourlyUsers;
        }

        public void setHourlyUsers(ArrayList<User> hourlyUsers) {
            this.hourlyUsers = hourlyUsers;
        }

        public ArrayList<User> getDayUsers() {
            return dayUsers;
        }

        public void setDayUsers(ArrayList<User> dayUsers) {
            this.dayUsers = dayUsers;
        }

        public ArrayList<User> getWeeklyUsers() {
            return weeklyUsers;
        }

        public void setWeeklyUsers(ArrayList<User> weeklyUsers) {
            this.weeklyUsers = weeklyUsers;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "hourlyUsers=" + hourlyUsers +
                    ", dayUsers=" + dayUsers +
                    ", weeklyUsers=" + weeklyUsers +
                    '}';
        }
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
