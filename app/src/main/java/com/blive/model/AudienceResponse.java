package com.blive.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sans on 08-07-2019.
 **/

public class AudienceResponse
{
    @SerializedName("data")
    private AudienceResponse.Data data;
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;
    public class Data
    {
        @SerializedName("message")
        private String message;

        @SerializedName("last_page")
        private String last_page;

        @SerializedName("user_list")
        private ArrayList<User> usersList;

        private String status;

        public String getStatus(int i){
            return usersList.get(i).getStatus();
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }


        public ArrayList<User> getUserList(){
            return usersList;
        }

        public String getLast_page(){
            return last_page;
        }


        @Override
        public String toString() {
            return "Data{" +
                    "message='" + message + '\'' +
                    ", user_list=" + usersList +
                    ", last_page=" + last_page +
                    '}';
        }
    }


    public AudienceResponse.Data getData() {
        return data;
    }

    public String getStatus() {
        return status;
    }

    public void setData(AudienceResponse.Data data) {
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
