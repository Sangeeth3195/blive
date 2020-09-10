package com.blive.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sans on 05-04-2019.
 **/

public class GenericResponse {

    @SerializedName("status")
    private String status;
    @SerializedName("data")
    private Data data;
    @SerializedName("message")
    private String message;

    public class Data {

        @SerializedName("message")
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
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
