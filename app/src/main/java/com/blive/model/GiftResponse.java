package com.blive.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sans on 12-04-2019.
 **/

public class GiftResponse {
    @SerializedName("data")
    private Data data;
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;

    public class Data {

        @SerializedName("message")
        private String message;

        @SerializedName("user_id")
        private String user_id;

        @SerializedName("gift_list")
        private ArrayList<Gift> gifts;

        @SerializedName("tool_list")
        private ArrayList<Gift> tools;

        @SerializedName("free_list")
        private ArrayList<Gift> freeGits;

        @SerializedName("over_all_gold")
        private String over_all_gold;

        @SerializedName("moon_level")
        private String moon_level;

        @SerializedName("moon_value")
        private String moon_value;

        @SerializedName("current_gold_value")
        private String current_gold_value;

        @SerializedName("broadcasterGiftValue")
        private String broadcasterGiftValue;

        @SerializedName("diamond")
        private int diamond;

        @SerializedName("guestGiftValue")
        private int guestGiftValue;

        @SerializedName("total")
        private int total;

        @SerializedName("free_gift_anim")
        private String freeGiftAnim;

        public String getGift_name() {
            return gift_name;
        }

        public void setGift_name(String gift_name) {
            this.gift_name = gift_name;
        }

        @SerializedName("gift_name")
        private String gift_name;

        public String getFreeGiftsAvailable() {
            return freeGiftsAvailable;
        }

        public void setFreeGiftsAvailable(String freeGiftsAvailable) {
            this.freeGiftsAvailable = freeGiftsAvailable;
        }

        @SerializedName("free_gifts_available")
        private String freeGiftsAvailable;

        public int getDiamond() {
            return diamond;
        }

        public void setDiamond(int diamond) {
            this.diamond = diamond;
        }

        public String getFreeGiftAnim() {
            return freeGiftAnim;
        }

        public void setFreeGiftAnim(String freeGiftAnim) {
            this.freeGiftAnim = freeGiftAnim;
        }

        @SerializedName("broadcasterFreeGift")
        int freegiftReceived;

        public int getFreegiftReceived() {
            return freegiftReceived;
        }

        public void setFreegiftReceived(int freegiftReceived) {
            this.freegiftReceived = freegiftReceived;
        }

        public String getBroadcasterGiftValue() {
            return broadcasterGiftValue;
        }

        public void setBroadcasterGiftValue(String broadcasterGiftValue) {
            this.broadcasterGiftValue = broadcasterGiftValue;
        }

        public int getGuestGiftValue() {
            return guestGiftValue;
        }

        public void setGuestGiftValue(int guestGiftValue) {
            this.guestGiftValue = guestGiftValue;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ArrayList<Gift> getGifts() {
            return gifts;
        }

        public void setGifts(ArrayList<Gift> gifts) {
            this.gifts = gifts;
        }

        public ArrayList<Gift> getTools() {
            return tools;
        }

        public void setTools(ArrayList<Gift> tools) {
            this.tools = tools;
        }

        public ArrayList<Gift> getFreeGits() {
            return freeGits;
        }

        public void setFreeGits(ArrayList<Gift> freeGits) {
            this.freeGits = freeGits;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getOver_all_gold() {
            return over_all_gold;
        }

        public void setOver_all_gold(String over_all_gold) {
            this.over_all_gold = over_all_gold;
        }

        public String getMoon_level() {
            return moon_level;
        }

        public void setMoon_level(String moon_level) {
            this.moon_level = moon_level;
        }

        public String getMoon_value() {
            return moon_value;
        }

        public void setMoon_value(String moon_value) {
            this.moon_value = moon_value;
        }

        public String getCurrent_gold_value() {
            return current_gold_value;
        }

        public void setCurrent_gold_value(String current_gold_value) {
            this.current_gold_value = current_gold_value;
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
