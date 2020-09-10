package com.blive.service;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sans on 25-Nov-19.
 **/

public class linkshorten {
    @SerializedName("link")
    String link;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
