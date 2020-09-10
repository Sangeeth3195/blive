package com.blive.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

/**
 * Created by sans on 19-09-2018.
 **/

public class Audience implements Parcelable {

    private int id;
    private String user_id;
    private String profile_pic;
    private String name;
    private String username;
    private String level;
    private String over_all_gold;
    private String activation_code;
    private String isTheUserFollowing;
    private String uid;
    private String current_gold_value;
    private String moon_level;
    private String moon_value;
    private String tools;
    private String dpEffects;
    private String entranceEffect;
    private String reference_user_id;
    private String fansCount;
    private String friendsCount;
    private String followersCount;
    private String total_gift_send;
    private String total_gift_receiver;
    private String broadcasting_min_target;
    private String broadcasting_hours;
    private String share;
    private String gold;
    private String share_target;
    private String gold_target;
    private String viewers_target;
    private String video_muted;
    private String text_muted;
    private int isTextMuted = 0;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.user_id);
        dest.writeString(this.profile_pic);
        dest.writeString(this.name);
        dest.writeString(this.username);
        dest.writeString(this.level);
        dest.writeString(this.over_all_gold);
        dest.writeString(this.activation_code);
        dest.writeString(this.isTheUserFollowing);
        dest.writeString(this.uid);
        dest.writeString(this.current_gold_value);
        dest.writeString(this.moon_level);
        dest.writeString(this.moon_value);
        dest.writeString(this.tools);
        dest.writeString(this.dpEffects);
        dest.writeString(this.entranceEffect);
        dest.writeString(this.reference_user_id);
        dest.writeString(this.fansCount);
        dest.writeString(this.friendsCount);
        dest.writeString(this.followersCount);
        dest.writeString(this.total_gift_send);
        dest.writeString(this.total_gift_receiver);
        dest.writeString(this.broadcasting_min_target);
        dest.writeString(this.broadcasting_hours);
        dest.writeString(this.share);
        dest.writeString(this.gold);
        dest.writeString(this.share_target);
        dest.writeString(this.gold_target);
        dest.writeString(this.viewers_target);
        dest.writeString(this.video_muted);
        dest.writeString(this.text_muted);
        dest.writeInt(this.isTextMuted);
    }

    public Audience() {
    }

    protected Audience(Parcel in) {
        this.id = in.readInt();
        this.user_id = in.readString();
        this.profile_pic = in.readString();
        this.name = in.readString();
        this.username = in.readString();
        this.level = in.readString();
        this.over_all_gold = in.readString();
        this.activation_code = in.readString();
        this.isTheUserFollowing = in.readString();
        this.uid = in.readString();
        this.current_gold_value = in.readString();
        this.moon_level = in.readString();
        this.moon_value = in.readString();
        this.tools = in.readString();
        this.dpEffects = in.readString();
        this.entranceEffect = in.readString();
        this.reference_user_id = in.readString();
        this.fansCount = in.readString();
        this.friendsCount  = in.readString();
        this.followersCount= in.readString();
        this.total_gift_send= in.readString();
        this.total_gift_receiver= in.readString();
        this.broadcasting_min_target= in.readString();
        this.broadcasting_hours = in.readString();
        this.share = in.readString();
        this.gold= in.readString();
        this.share_target= in.readString();
        this.gold_target= in.readString();
        this.viewers_target= in.readString();
        this.video_muted= in.readString();
        this.text_muted= in.readString();
        this.isTextMuted = in.readInt();
    }

    public static final Creator<Audience> CREATOR = new Creator<Audience>() {
        @Override
        public Audience createFromParcel(Parcel source) {
            return new Audience(source);
        }

        @Override
        public Audience[] newArray(int size) {
            return new Audience[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getOver_all_gold() {
        return over_all_gold;
    }

    public void setOver_all_gold(String over_all_gold) {
        this.over_all_gold = over_all_gold;
    }

    public String getActivation_code() {
        return activation_code;
    }

    public void setActivation_code(String activation_code) {
        this.activation_code = activation_code;
    }

    public String getIsTheUserFollowing() {
        return isTheUserFollowing;
    }

    public void setIsTheUserFollowing(String isTheUserFollowing) {
        this.isTheUserFollowing = isTheUserFollowing;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCurrent_gold_value() {
        return current_gold_value;
    }

    public void setCurrent_gold_value(String current_gold_value) {
        this.current_gold_value = current_gold_value;
    }

    public String getMoon_level() {
        return moon_level;
    }

    public void setMoon_level(String moon_level) {
        this.moon_level = moon_level;
    }

    public String getMoon_value() {
        return moon_value;
    }

    public void setMoon_value(String moon_value) {
        this.moon_value = moon_value;
    }

    public String getTools() {
        return tools;
    }

    public void setTools(String tools) {
        this.tools = tools;
    }

    public String getDpEffects() {
        return dpEffects;
    }

    public void setDpEffects(String dpEffects) {
        this.dpEffects = dpEffects;
    }

    public String getEntranceEffect() {
        return entranceEffect;
    }

    public void setEntranceEffect(String entranceEffect) {
        this.entranceEffect = entranceEffect;
    }

    public String getReference_user_id() {
        return reference_user_id;
    }

    public void setReference_user_id(String reference_user_id) {
        this.reference_user_id = reference_user_id;
    }

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

    public String getTotal_gift_send() {
        return total_gift_send;
    }

    public void setTotal_gift_send(String total_gift_send) {
        this.total_gift_send = total_gift_send;
    }

    public String getTotal_gift_receiver() {
        return total_gift_receiver;
    }

    public void setTotal_gift_receiver(String total_gift_receiver) {
        this.total_gift_receiver = total_gift_receiver;
    }

    public String getBroadcasting_min_target() {
        return broadcasting_min_target;
    }

    public void setBroadcasting_min_target(String broadcasting_min_target) {
        this.broadcasting_min_target = broadcasting_min_target;
    }

    public String getBroadcasting_hours() {
        return broadcasting_hours;
    }

    public void setBroadcasting_hours(String broadcasting_hours) {
        this.broadcasting_hours = broadcasting_hours;
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

    public String getShare_target() {
        return share_target;
    }

    public void setShare_target(String share_target) {
        this.share_target = share_target;
    }

    public String getGold_target() {
        return gold_target;
    }

    public void setGold_target(String gold_target) {
        this.gold_target = gold_target;
    }

    public String getViewers_target() {
        return viewers_target;
    }

    public void setViewers_target(String viewers_target) {
        this.viewers_target = viewers_target;
    }

    public String getVideo_muted() {
        return video_muted;
    }

    public String getText_muted() {
        return text_muted;
    }

    public void setText_muted(String text_muted) {
        this.text_muted = text_muted;
    }

    public void setVideo_muted(String video_muted) {
        this.video_muted = video_muted;
    }

    public int getIsTextMuted() {
        return isTextMuted;
    }

    public void setIsTextMuted(int isTextMuted) {
        this.isTextMuted = isTextMuted;
    }

    public static Creator<Audience> getCREATOR() {
        return CREATOR;
    }

    @NonNull
    @Override
    public String toString() {
        return "Audience{" +
                "id='" + id + '\''+
                "user_id='" + user_id + '\'' +
                ", profile_pic='" + profile_pic + '\'' +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", level='" + level + '\'' +
                ", over_all_gold='" + over_all_gold + '\'' +
                ", activation_code='" + activation_code + '\'' +
                ", isTheUserFollowing='" + isTheUserFollowing + '\'' +
                ", uid='" + uid + '\'' +
                ", current_gold_value='" + current_gold_value + '\'' +
                ", moon_level='" + moon_level + '\'' +
                ", moon_value='" + moon_value + '\'' +
                ", tools='" + tools + '\'' +
                ", dpEffects='" + dpEffects + '\'' +
                ", entranceEffect='" + entranceEffect + '\'' +
                ", reference_user_id=" + reference_user_id +
                ", friendsCount=" + friendsCount +
                ", followersCount=" + followersCount +
                ", fansCount=" + fansCount +
                ", total_gift_send=" + total_gift_send +
                ", total_gift_receiver=" + total_gift_receiver +
                ", broadcasting_min_target=" + broadcasting_min_target +
                ", broadcasting_hours=" + broadcasting_hours +
                ", share=" + share +
                ", gold=" + gold +
                ", share_target=" + share_target +
                ", gold_target=" + gold_target +
                ", viewers_target=" + viewers_target +
                ", video_muted=" + video_muted +
                ", isTextMuted=" + isTextMuted +
                '}';
    }
}
