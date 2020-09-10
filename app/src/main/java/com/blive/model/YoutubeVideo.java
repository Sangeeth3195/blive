package com.blive.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class YoutubeVideo {

    @SerializedName("data")
    private Data data;
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;

    public class Data {

        @SerializedName("YoutubeVideoList")
        private ArrayList<YoutubeResponse> youtubeResponses;

        public ArrayList<YoutubeResponse> getYoutubeResponses() {
            return youtubeResponses;
        }

        public void setYoutubeResponses(ArrayList<YoutubeResponse> youtubeResponses) {
            this.youtubeResponses = youtubeResponses;
        }

        @SerializedName("last_page")
        private int last_page;

        public int getLast_page() {
            return last_page;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "youtubeResponses=" + youtubeResponses +
                    ", last_page=" + last_page +
                    '}';
        }

        public void setLast_page(int last_page) {
            this.last_page = last_page;
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
