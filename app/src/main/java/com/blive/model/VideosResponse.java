package com.blive.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sans on 09-04-2019.
 **/

public class VideosResponse {

    @SerializedName("data")
    private Data data;
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;

    public class Data {

        @SerializedName("message")
        private String message;

        @SerializedName("videoList")
        private ArrayList<Video> videos;

        @SerializedName("last_page")
        private int last_page;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ArrayList<Video> getVideos() {
            return videos;
        }

        public void setVideos(ArrayList<Video> videos) {
            this.videos = videos;
        }

        public int getLast_page() {
            return last_page;
        }

        public void setLast_page(int last_page) {
            this.last_page = last_page;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "message='" + message + '\'' +
                    ", videos=" + videos +
                    ", last_page=" + last_page +
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
