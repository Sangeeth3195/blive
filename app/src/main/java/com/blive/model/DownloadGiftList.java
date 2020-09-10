package com.blive.model;

import com.google.gson.annotations.SerializedName;

public class DownloadGiftList {

    @SerializedName("id")
    String id;

    @SerializedName("gif")
    String gif_image;

    @SerializedName("type")
    String type;


    @SerializedName("price")
    String price;

    @SerializedName("duration")
    String duration;

    @SerializedName("icon")
    String icon;

    @SerializedName("thumbnail")
    String thumbnail;

    @SerializedName("name")
    String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGif_image() {
        return gif_image;
    }

    public void setGif_image(String gif_image) {
        this.gif_image = gif_image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}