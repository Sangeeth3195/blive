package com.blive.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sans on 15-Oct-19.
 **/

public class PkGiftDetailsModel {

    @SerializedName("status")
    private String status;
    @SerializedName("data")
    Data DataObject;


    // Getter Methods

    public String getStatus() {
        return status;
    }

    public Data getData() {
        return DataObject;
    }

    // Setter Methods

    public void setStatus(String status) {
        this.status = status;
    }

    public void setData(Data dataObject) {
        this.DataObject = dataObject;
    }

    public class Data {
        @SerializedName("requester")

        Requester RequesterObject;
        @SerializedName("requestee")

        Requestee RequesteeObject;


        // Getter Methods

        public Requester getRequester() {
            return RequesterObject;
        }

        public Requestee getRequestee() {
            return RequesteeObject;
        }

        // Setter Methods

        public void setRequester(Requester requesterObject) {
            this.RequesterObject = requesterObject;
        }

        public void setRequestee(Requestee requesteeObject) {
            this.RequesteeObject = requesteeObject;
        }
    }

    public class Requestee {
        @SerializedName("gold")

        private int gold;

        @SerializedName("pts")

        private int pts;

        public int getOver_all_gold() {
            return over_all_gold;
        }

        public void setOver_all_gold(int over_all_gold) {
            this.over_all_gold = over_all_gold;
        }

        @SerializedName("over_all_gold")

        private int over_all_gold;

        public ArrayList<Application> getToppers() {
            return toppers;
        }

        public void setToppers(ArrayList<Application> toppers) {
            this.toppers = toppers;
        }

        @SerializedName("toppers")
        ArrayList<Application> toppers = new ArrayList<Application>();


        // Getter Methods

        public int getGold() {
            return gold;
        }

        public int getPts() {
            return pts;
        }

        // Setter Methods

        public void setGold(int gold) {
            this.gold = gold;
        }

        public void setPts(int pts) {
            this.pts = pts;
        }
    }

    public class Requester {
        @SerializedName("gold")

        private int gold;
        @SerializedName("pts")

        private int pts;

        public int getOver_all_gold() {
            return over_all_gold;
        }

        public void setOver_all_gold(int over_all_gold) {
            this.over_all_gold = over_all_gold;
        }

        @SerializedName("over_all_gold")

        private int over_all_gold;


        public ArrayList<Application> getToppers() {
            return toppers;
        }

        public void setToppers(ArrayList<Application> toppers) {
            this.toppers = toppers;
        }

        @SerializedName("toppers")
        ArrayList<Application> toppers = new ArrayList<Application>();


        // Getter Methods

        public int getGold() {
            return gold;
        }

        public int getPts() {
            return pts;
        }

        // Setter Methods

        public void setGold(int gold) {
            this.gold = gold;
        }

        public void setPts(int pts) {
            this.pts = pts;
        }

    }

    public class Application {
        @SerializedName("user_id")
        private String user_id;
        @SerializedName("name")

        private String name;
        @SerializedName("gift_value")

        private String gift_value;
        @SerializedName("profile_pic")
        private String profile_pic;
        // Getter Methods

        public String getUser_id() {
            return user_id;
        }

        public String getName() {
            return name;
        }

        public String getGift_value() {
            return gift_value;
        }

        public String getProfile_pic() {
            return profile_pic;
        }

        // Setter Methods

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setGift_value(String gift_value) {
            this.gift_value = gift_value;
        }

        public void setProfile_pic(String profile_pic) {
            this.profile_pic = profile_pic;
        }
    }

}
