package com.blive.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sans on 05-04-2019.
 **/

public class GetGlobalPk {

    @SerializedName("status")
    private String status;

    @SerializedName("match")
    private Match match;
    @SerializedName("message")
    private String message;



    public class Match {

        @SerializedName("user_id")
        private String user_id;


        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        @Override
        public String toString() {
            return "Match{" +
                    "user_id='" + user_id + '\'' +
                    '}';
        }
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
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

    @Override
    public String toString() {
        return "GetGlobalPk{" +
                "status='" + status + '\'' +
                ", match=" + match.toString() +
                ", message='" + message + '\'' +
                '}';
    }
}
