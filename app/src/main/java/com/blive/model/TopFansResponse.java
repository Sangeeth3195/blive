package com.blive.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sans on 15-04-2019.
 **/

public class TopFansResponse {

    @SerializedName("data")
    private Data data;
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;

    public class Data {

        @SerializedName("message")
        private String message;

        @SerializedName("hour")
        private ArrayList<User> hourlyUsers;

        @SerializedName("week")
        private ArrayList<User> weeklyUsers;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ArrayList<User> getHourlyUsers() {
            return hourlyUsers;
        }

        public void setHourlyUsers(ArrayList<User> hourlyUsers) {
            this.hourlyUsers = hourlyUsers;
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
                    "message='" + message + '\'' +
                    ", hourlyUsers=" + hourlyUsers +
                    ", weeklyUsers=" + weeklyUsers +
                    '}';
        }
    }

    public Data getData() {
        return data;
    }

    public String getStatus() {
        return status;
    }

    public void setData(Data data) {
        this.data = data;
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
