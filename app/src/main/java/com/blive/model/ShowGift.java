package com.blive.model;

/**
 * Created by sans on 12-11-2018.
 **/

public class ShowGift {

    private String name,thumbnail,gif,type,price,message;
    private int duration;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getGif() {
        return gif;
    }

    public void setGif(String gif) {
        this.gif = gif;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ShowGift{" +
                "name='" + name + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", gif='" + gif + '\'' +
                ", type='" + type + '\'' +
                ", price='" + price + '\'' +
                ", duration='" + duration + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
