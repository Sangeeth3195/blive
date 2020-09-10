package com.blive.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sans on 06-04-2019.
 **/

public class FollowResponse {

    @SerializedName("status")
    private String status;
    @SerializedName("data")
    private Data data;
    @SerializedName("message")
    private String message;

    public class Data {

        @SerializedName("message")
        private String message;

        @SerializedName("fans_count")
        private String fans_count;

        @SerializedName("follower_count")
        private String follower_count;

        @SerializedName("friend_count")
        private String friend_count;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getFans_count() {
            return fans_count;
        }

        public void setFans_count(String fans_count) {
            this.fans_count = fans_count;
        }

        public String getFollower_count() {
            return follower_count;
        }

        public void setFollower_count(String follower_count) {
            this.follower_count = follower_count;
        }

        public String getFriend_count() {
            return friend_count;
        }

        public void setFriend_count(String friend_count) {
            this.friend_count = friend_count;
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
