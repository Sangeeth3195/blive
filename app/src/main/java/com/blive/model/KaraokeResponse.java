package com.blive.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sans on 15-04-2019.
 **/

public class KaraokeResponse {
    @SerializedName("data")
    private Data data;
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;

    public class Data {
        @SerializedName("songs_List")
        private ArrayList<KaraokeSong> karaokeSongs;
        @SerializedName("message")
        private String message;

        public ArrayList<KaraokeSong> getKaraokeSongs() {
            return karaokeSongs;
        }

        public void setKaraokeSongs(ArrayList<KaraokeSong> karaokeSongs) {
            this.karaokeSongs = karaokeSongs;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "karaokeSongs=" + karaokeSongs +
                    ", message='" + message + '\'' +
                    '}';
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
