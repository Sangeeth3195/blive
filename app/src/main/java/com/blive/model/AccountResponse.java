package com.blive.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sans on 04-04-2019.
 **/

public class AccountResponse {

    @SerializedName("data")
    private Data data;
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;

    public class Data {

        @SerializedName("message")
        private String message;

        @SerializedName("user_details")
        private User user;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
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
