package com.blive.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sans on 09-04-2019.
 **/

public class ProfileResponse {

    @SerializedName("status")
    private String status;
    @SerializedName("data")
    private Data data;
    @SerializedName("message")
    private String message;

    public class Data {

        @SerializedName("isThisUserFollowing")
        private String isThisUserFollowing;

        @SerializedName("isThisUserFriends")
        private String isThisUserFriends;

        @SerializedName("isTheUserBlocked")
        private String isTheUserBlocked;

        @SerializedName("user_details")
        private User user;

        public String getIsThisUserFollowing() {
            return isThisUserFollowing;
        }

        public void setIsThisUserFollowing(String isThisUserFollowing) {
            this.isThisUserFollowing = isThisUserFollowing;
        }

        public String getIsThisUserFriends() {
            return isThisUserFriends;
        }

        public void setIsThisUserFriends(String isThisUserFriends) {
            this.isThisUserFriends = isThisUserFriends;
        }

        public String getIsTheUserBlocked() {
            return isTheUserBlocked;
        }

        public void setIsTheUserBlocked(String isTheUserBlocked) {
            this.isTheUserBlocked = isTheUserBlocked;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
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
