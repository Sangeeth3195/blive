package com.blive.model;

import com.google.gson.annotations.SerializedName;

public class GetTarget {

    @SerializedName("status")
    private String status;
    @SerializedName("data")
    private Data data;

    public class Data {

        String fansCount;
        String friendsCount;
        String followersCount;
        String totalGiftSend;
        String totalGiftReceiver;
        String share;
        String gold;
        String broadcastingHours;
        String broadcastingMinTarget;
        String shareTarget;
        String goldTarget;
        String viewersTarget;

        public String getFansCount() {
            return fansCount;
        }

        public void setFansCount(String fansCount) {
            this.fansCount = fansCount;
        }

        public String getFriendsCount() {
            return friendsCount;
        }

        public void setFriendsCount(String friendsCount) {
            this.friendsCount = friendsCount;
        }

        public String getFollowersCount() {
            return followersCount;
        }

        public void setFollowersCount(String followersCount) {
            this.followersCount = followersCount;
        }

        public String getTotalGiftSend() {
            return totalGiftSend;
        }

        public void setTotalGiftSend(String totalGiftSend) {
            this.totalGiftSend = totalGiftSend;
        }

        public String getTotalGiftReceiver() {
            return totalGiftReceiver;
        }

        public void setTotalGiftReceiver(String totalGiftReceiver) {
            this.totalGiftReceiver = totalGiftReceiver;
        }

        public String getShare() {
            return share;
        }

        public void setShare(String share) {
            this.share = share;
        }

        public String getGold() {
            return gold;
        }

        public void setGold(String gold) {
            this.gold = gold;
        }

        public String getBroadcastingHours() {
            return broadcastingHours;
        }

        public void setBroadcastingHours(String broadcastingHours) {
            this.broadcastingHours = broadcastingHours;
        }

        public String getBroadcastingMinTarget() {
            return broadcastingMinTarget;
        }

        public void setBroadcastingMinTarget(String broadcastingMinTarget) {
            this.broadcastingMinTarget = broadcastingMinTarget;
        }

        public String getShareTarget() {
            return shareTarget;
        }

        public void setShareTarget(String shareTarget) {
            this.shareTarget = shareTarget;
        }

        public String getGoldTarget() {
            return goldTarget;
        }

        public void setGoldTarget(String goldTarget) {
            this.goldTarget = goldTarget;
        }

        public String getViewersTarget() {
            return viewersTarget;
        }

        public void setViewersTarget(String viewersTarget) {
            this.viewersTarget = viewersTarget;
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

}
