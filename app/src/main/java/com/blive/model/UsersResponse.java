package com.blive.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sans on 05-04-2019.
 **/

public class UsersResponse {
    @SerializedName("data")
    private Data data;
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;

//    public class Data {
//
//        @SerializedName("message")
//        private String message;
//
//        @SerializedName("user_details")
//        private User user;
//
//        @SerializedName("user_list")
//        private ArrayList<User> users;
//
//        @SerializedName("guest_list")
//        private ArrayList<Audience> guests;
//
//        @SerializedName("audience_list")
//        private ArrayList<Audience> audiences;
//
//        @SerializedName("entranceEffect")
//        private String entranceEffect;
//
//        @SerializedName("last_page")
//        private int last_page;
//
//        @SerializedName("viewers_count")
//        private int viewers_count;
//
//        @SerializedName("broadcast_type")
//        private String broadcastType;
//
//        @SerializedName("Last_broad_time")
//        int lastBroadTime;
//
//        @SerializedName("gifts_available")
//        int giftAvailable;
//
//        @SerializedName("over_all_gold")
//        private int overAllGold;
//
//        @SerializedName("broadcasterFreeGift")
//        private int freeGiftCount;
//
//        public String getIsTheUserFollowing() {
//            return isTheUserFollowing;
//        }
//
//        public void setIsTheUserFollowing(String isTheUserFollowing) {
//            this.isTheUserFollowing = isTheUserFollowing;
//        }
//
//        @SerializedName("isTheUserFollowing")
//        private String isTheUserFollowing;
//
//        public int getOverAllGold() {
//            return overAllGold;
//        }
//
//        public void setOverAllGold(int overAllGold) {
//            this.overAllGold = overAllGold;
//        }
//
//        public int getGiftAvailable() {
//            return giftAvailable;
//        }
//
//        public void setGiftAvailable(int giftAvailable) {
//            this.giftAvailable = giftAvailable;
//        }
//
//        public int getLastBroadTime() {
//            return lastBroadTime;
//        }
//
//        public void setLastBroadTime(int lastBroadTime) {
//            this.lastBroadTime = lastBroadTime;
//        }
//
//        public String getMessage() {
//            return message;
//        }
//
//        public void setMessage(String message) {
//            this.message = message;
//        }
//
//        public User getUser() {
//            return user;
//        }
//
//        public void setUser(User user) {
//            this.user = user;
//        }
//
//        public ArrayList<User> getUsers() {
//            return users;
//        }
//
//        public void setUsers(ArrayList<User> users) {
//            this.users = users;
//        }
//
//        public ArrayList<Audience> getGuests() {
//            return guests;
//        }
//
//        public void setGuests(ArrayList<Audience> guests) {
//            this.guests = guests;
//        }
//
//        public ArrayList<Audience> getAudiences() {
//            return audiences;
//        }
//
//        public void setAudiences(ArrayList<Audience> audiences) {
//            this.audiences = audiences;
//        }
//
//        public String getEntranceEffect() {
//            return entranceEffect;
//        }
//
//        public void setEntranceEffect(String entranceEffect) {
//            this.entranceEffect = entranceEffect;
//        }
//
//        public int getLast_page() {
//            return last_page;
//        }
//
//        public void setLast_page(int last_page) {
//            this.last_page = last_page;
//        }
//
//        public int getViewers_count() {
//            return viewers_count;
//        }
//
//        public void setViewers_count(int viewers_count) {
//            this.viewers_count = viewers_count;
//        }
//
//        public String getBroadcastType() {
//            return broadcastType;
//        }
//
//        public void setBroadcastType(String broadcastType) {
//            this.broadcastType = broadcastType;
//        }
//
//        public int getFreeGiftCount() {
//            return freeGiftCount;
//        }
//
//        public void setFreeGiftCount(int freeGiftCount) {
//            this.freeGiftCount = freeGiftCount;
//        }
//
//        @NonNull
//        @Override
//        public String toString() {
//            return "Data{" +
//                    "message='" + message + '\'' +
//                    ", users=" + users +
//                    ", guests=" + guests +
//                    ", audiences=" + audiences +
//                    ", entranceEffect='" + entranceEffect + '\'' +
//                    ", last_page=" + last_page +
//                    ", viewers_count=" + viewers_count +
//                    ", broadcastType=" + broadcastType +
//                    '}';
//        }
//    }

    public class Data {

        @SerializedName("message")
        private String message;

        @SerializedName("user_details")
        private User user;

        @SerializedName("user_list")
        private ArrayList<User> users;

        @SerializedName("guest_list")
        private ArrayList<Audience> guests;

        @SerializedName("audience_list")
        private ArrayList<Audience> audiences;

        @SerializedName("entranceEffect")
        private String entranceEffect;

        @SerializedName("last_page")
        private int last_page;

        @SerializedName("viewers_count")
        private int viewers_count;

        @SerializedName("broadcast_type")
        private String broadcastType;

        @SerializedName("Last_broad_time")
        int lastBroadTime;

        @SerializedName("gifts_available")
        int giftAvailable;

        @SerializedName("over_all_gold")
        private int overAllGold;

        @SerializedName("broadcasterFreeGift")
        private int freeGiftCount;

        @SerializedName("guest_details")
        private GuestDetails guest_details;

        @SerializedName("isTheUserFollowing")
        private String isTheUserFollowing;

        public int getAudiencefreeGiftCount() {
            return audiencefreeGiftCount;
        }

        public void setAudiencefreeGiftCount(int audiencefreeGiftCount) {
            this.audiencefreeGiftCount = audiencefreeGiftCount;
        }

        @SerializedName("freeGiftCount")
        private int audiencefreeGiftCount;

        public String getIsTheUserFollowing() {
            return isTheUserFollowing;
        }

        public void setIsTheUserFollowing(String isTheUserFollowing) {
            this.isTheUserFollowing = isTheUserFollowing;
        }

        public int getLastBroadTime() {
            return lastBroadTime;
        }

        public void setLastBroadTime(int lastBroadTime) {
            this.lastBroadTime = lastBroadTime;
        }

        public int getGiftAvailable() {
            return giftAvailable;
        }

        public void setGiftAvailable(int giftAvailable) {
            this.giftAvailable = giftAvailable;
        }

        public int getOverAllGold() {
            return overAllGold;
        }

        public void setOverAllGold(int overAllGold) {
            this.overAllGold = overAllGold;
        }

        public int getFreeGiftCount() {
            return freeGiftCount;
        }

        public void setFreeGiftCount(int freeGiftCount) {
            this.freeGiftCount = freeGiftCount;
        }


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

        public ArrayList<User> getUsers() {
            return users;
        }

        public void setUsers(ArrayList<User> users) {
            this.users = users;
        }

        public ArrayList<Audience> getGuests() {
            return guests;
        }

        public void setGuests(ArrayList<Audience> guests) {
            this.guests = guests;
        }

        public ArrayList<Audience> getAudiences() {
            return audiences;
        }

        public void setAudiences(ArrayList<Audience> audiences) {
            this.audiences = audiences;
        }

        public String getEntranceEffect() {
            return entranceEffect;
        }

        public void setEntranceEffect(String entranceEffect) {
            this.entranceEffect = entranceEffect;
        }

        public int getLast_page() {
            return last_page;
        }

        public void setLast_page(int last_page) {
            this.last_page = last_page;
        }

        public int getViewers_count() {
            return viewers_count;
        }

        public void setViewers_count(int viewers_count) {
            this.viewers_count = viewers_count;
        }

        public String getBroadcastType() {
            return broadcastType;
        }

        public void setBroadcastType(String broadcastType) {
            this.broadcastType = broadcastType;
        }

        public GuestDetails getGuest_details() {
            return guest_details;
        }

        public void setGuest_details(GuestDetails guest_details) {
            this.guest_details = guest_details;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "message='" + message + '\'' +
                    ", user=" + user +
                    ", users=" + users +
                    ", guests=" + guests +
                    ", audiences=" + audiences +
                    ", entranceEffect='" + entranceEffect + '\'' +
                    ", last_page=" + last_page +
                    ", viewers_count=" + viewers_count +
                    ", broadcastType='" + broadcastType + '\'' +
                    ", guest_details=" + guest_details.toString() +
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

    @NonNull
    @Override
    public String toString() {
        return "UsersResponse{" +
                "data=" + data +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public  class GuestDetails{
        @SerializedName("user_id")
        private String user_id;
        @SerializedName("profile")
        private String profile;
        @SerializedName("challenge_time_left")
        private String challenge_time_left="";
        @SerializedName("pk_broadcaster_id")
        private String pk_broadcaster_id;
        @SerializedName("pk_guest_id")
        private String pk_guest_id;

        public String getPk_channelname() {
            return pk_channelname;
        }

        public void setPk_channelname(String pk_channelname) {
            this.pk_channelname = pk_channelname;
        }

        @SerializedName("pk_channelname")
        private String pk_channelname;
        public String getPk_broadcaster_id() {
            return pk_broadcaster_id;
        }

        public void setPk_broadcaster_id(String pk_broadcaster_id) {
            this.pk_broadcaster_id = pk_broadcaster_id;
        }

        public String getPk_guest_id() {
            return pk_guest_id;
        }

        public void setPk_guest_id(String pk_guest_id) {
            this.pk_guest_id = pk_guest_id;
        }



        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getProfile() {
            return profile;
        }

        public void setProfile(String profile) {
            this.profile = profile;
        }

        public String getChallenge_time_left() {
            return challenge_time_left;
        }

        public void setChallenge_time_left(String challenge_time_left) {
            this.challenge_time_left = challenge_time_left;
        }

        @Override
        public String toString() {
            return "GuestDetails{" +
                    "user_id='" + user_id + '\'' +
                    ", profile='" + profile + '\'' +
                    ", challenge_time_left='" + challenge_time_left + '\'' +
                    '}';
        }
    }

}
