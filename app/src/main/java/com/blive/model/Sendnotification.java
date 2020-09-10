package com.blive.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sans on 30-Oct-19.
 **/

public class Sendnotification {
    @SerializedName("status")
    String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @SerializedName("data")
    String data;
}
