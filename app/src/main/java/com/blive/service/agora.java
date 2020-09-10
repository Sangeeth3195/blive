package com.blive.service;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sans on 25-Nov-19.
 **/

public class agora {
    @SerializedName("status")
    String status;

    public String getLink() {
        return status;
    }

    public void setLink(String status) {
        this.status = status;
    }
}
