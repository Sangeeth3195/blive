package com.blive.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GiftFromServer {

    @SerializedName("gift_list")
    List<DownloadGiftList>  giftSecureDataList;

    public List<DownloadGiftList> getGiftSecureDataList() {
        return giftSecureDataList;
    }

    public void setGiftSecureDataList(List<DownloadGiftList> giftSecureDataList) {
        this.giftSecureDataList = giftSecureDataList;
    }
}
