package com.blive.model;

import com.google.gson.annotations.SerializedName;

public class AccountResponseInapp {
    @SerializedName("status")
    String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
