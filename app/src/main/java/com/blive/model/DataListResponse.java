package com.blive.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sans on 09-04-2019.
 **/

public class DataListResponse {

    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private ArrayList<Country> list;

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

    public ArrayList<Country> getList() {
        return list;
    }

    public void setList(ArrayList<Country> list) {
        this.list = list;
    }
}
