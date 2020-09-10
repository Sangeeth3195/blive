package com.blive.model;

/**
 * Created by sans on 28-08-2018.
 **/

public class Gift {
    private String name;
    private String thumbnail;
    private String gif;
    private String type;
    private String price;
    private String duration;
    private String giftUrl;
    private String giftId;
    private String giftpath;
    private String giftIcon;
    private String icon;
    private int freeGiftCount;
    private String freeGiftTime;
    private int freeGiftId;

    public String getCombo() {
        return combo;
    }

    public void setCombo(String combo) {
        this.combo = combo;
    }

    public String getCombo_pack() {
        return combo_pack;
    }

    public void setCombo_pack(String combo_pack) {
        this.combo_pack = combo_pack;
    }

    private String combo;
    private String combo_pack;



    public int getFreeGiftCount() {
        return freeGiftCount;
    }

    public void setFreeGiftCount(int freeGiftCount) {
        this.freeGiftCount = freeGiftCount;
    }

    public String getFreeGiftTime() {
        return freeGiftTime;
    }

    public void setFreeGiftTime(String freeGiftTime) {
        this.freeGiftTime = freeGiftTime;
    }

    public int getFreeGiftId() {
        return freeGiftId;
    }

    public void setFreeGiftId(int freeGiftId) {
        this.freeGiftId = freeGiftId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getGiftIcon() {
        return giftIcon;
    }

    public void setGiftIcon(String giftIcon) {
        this.giftIcon = giftIcon;
    }

    public String getGiftUrl() {
        return giftUrl;
    }

    public void setGiftUrl(String giftUrl) {
        this.giftUrl = giftUrl;
    }

    public String getGiftId() {
        return giftId;
    }

    public void setGiftId(String giftId) {
        this.giftId = giftId;
    }

    public String getGiftpath() {
        return giftpath;
    }

    public void setGiftpath(String giftpath) {
        this.giftpath = giftpath;
    }

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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Gift{" +
                "name='" + name + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", gif='" + gif + '\'' +
                ", type='" + type + '\'' +
                ", price='" + price + '\'' +
                ", duration='" + duration + '\'' +
                ", giftpath='" + giftpath + '\'' +
                ", giftId='" + giftId + '\'' +
                ", giftUrl='" + giftUrl + '\'' +
                '}';
    }
}
