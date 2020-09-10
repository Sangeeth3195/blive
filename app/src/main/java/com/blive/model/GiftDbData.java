package com.blive.model;


public class GiftDbData {


    public int id;
    public String giftId;
    public String giftUrl;
    public String type;
    public String duration;
    public String thumbnail;
    public String name;
    public byte[] giftImage;
    public String giftKey;
    public String giftpath;

    public String getGiftpath() {
        return giftpath;
    }

    public void setGiftpath(String giftpath) {
        this.giftpath = giftpath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGiftId() {
        return giftId;
    }

    public void setGiftId(String giftId) {
        this.giftId = giftId;
    }

    public String getGiftUrl() {
        return giftUrl;
    }

    public void setGiftUrl(String giftUrl) {
        this.giftUrl = giftUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
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

    public byte[] getGiftImage() {
        return giftImage;
    }

    public void setGiftImage(byte[] giftImage) {
        this.giftImage = giftImage;
    }

    public String getGiftKey() {
        return giftKey;
    }

    public void setGiftKey(String giftKey) {
        this.giftKey = giftKey;
    }
}
