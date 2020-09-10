package com.blive.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sans on 30-07-2018.
 **/

public class User implements Parcelable {

    private String uid;
    private String name;
    private String username;
    private String email;
    private String user_id;
    private String activation_code;
    private String last_login;
    private String silver;
    private String diamond;
    private String mobile;
    private String followers;
    private String fans;
    private String friends;
    private String sent;
    private String received;
    private String profile_pic;
    private String status;
    private String about_me;
    private String gender;
    private String age;
    private String image;
    private String level;
    private String xp;
    private String moon_value;
    private String isTheUserFollowing;
    private String profile_pic1;
    private String profile_pic2;
    private String reference_user_id;
    private String date_of_birth;
    private String hobbies;
    private String carrier;
    private String latitude;
    private String longitude;
    private String is_this_user_checked_in;
    private String login_domain;
    private String broadcast_type;
    private String viewers_count;
    private String country;
    private String current_gold_value;
    private String over_all_gold;
    private String is_the_shared_app;
    private String is_the_user_has_gifted;
    private String is_this_user_viewed;
    private String state;
    private String city;
    private String moon_level_count;
    private String gift_value;
    private String giftSendValue;
    private String tools_applied;
    private String baggage_applied;
    private String is_this_user_blocked;
    private String is_the_user_id_hidden;
    private String is_the_age_hidden;
    private String is_the_gender_hide;
    private String is_the_location_hidden;
    private String is_the_user_changed_his_blive_ID;
    private String is_the_dob_hidden;
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
    private String text_muted;

    public String getRelationSymbol() {
        return relationSymbol;
    }

    public void setRelationSymbol(String relationSymbol) {
        this.relationSymbol = relationSymbol;
    }

    private String relationSymbol;

    public String getReseller() {
        return reseller;
    }

    public void setReseller(String reseller) {
        this.reseller = reseller;
    }

    private String reseller;
    private String broadcasterGiftValue;



    private String guest="";

    public String getGuest() {
        return guest;
    }

    public void setGuest(String guest) {
        this.guest = guest;
    }

    private String pk_broadcaster_id;
    private String pk_guest_id;
    private String pk_channelname="";

    private int id;
    private int pin;

    public int getFriend_blocked() {
        return friend_blocked;
    }

    public void setFriend_blocked(int friend_blocked) {
        this.friend_blocked = friend_blocked;
    }

    private int friend_blocked;
    private int guestGiftValue;
    private int total;
    private boolean isFollowing;
    private int pkTimeLeft = 0;



    @SerializedName("guest_details")
    public GuestDetail guestDetail;

    public GuestDetail getGuestDetail() {
        return guestDetail;
    }

    public void setGuestDetail(GuestDetail guestDetail) {
        this.guestDetail = guestDetail;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

    public User() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getActivation_code() {
        return activation_code;
    }

    public void setActivation_code(String activation_code) {
        this.activation_code = activation_code;
    }

    public String getLast_login() {
        return last_login;
    }

    public void setLast_login(String last_login) {
        this.last_login = last_login;
    }

    public String getSilver() {
        return silver;
    }

    public void setSilver(String silver) {
        this.silver = silver;
    }

    public String getDiamond() {
        return diamond;
    }

    public void setDiamond(String diamond) {
        this.diamond = diamond;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public String getFans() {
        return fans;
    }

    public void setFans(String fans) {
        this.fans = fans;
    }

    public String getFriends() {
        return friends;
    }

    public void setFriends(String friends) {
        this.friends = friends;
    }

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }

    public String getReceived() {
        return received;
    }

    public void setReceived(String received) {
        this.received = received;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAbout_me() {
        return about_me;
    }

    public void setAbout_me(String about_me) {
        this.about_me = about_me;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getXp() {
        return xp;
    }

    public void setXp(String xp) {
        this.xp = xp;
    }

    public String getMoon_value() {
        return moon_value;
    }

    public void setMoon_value(String moon_value) {
        this.moon_value = moon_value;
    }

    public String getIsTheUserFollowing() {
        return isTheUserFollowing;
    }

    public void setIsTheUserFollowing(String isTheUserFollowing) {
        this.isTheUserFollowing = isTheUserFollowing;
    }

    public String getProfile_pic1() {
        return profile_pic1;
    }

    public void setProfile_pic1(String profile_pic1) {
        this.profile_pic1 = profile_pic1;
    }

    public String getProfile_pic2() {
        return profile_pic2;
    }

    public void setProfile_pic2(String profile_pic2) {
        this.profile_pic2 = profile_pic2;
    }

    public String getReference_user_id() {
        return reference_user_id;
    }

    public void setReference_user_id(String reference_user_id) {
        this.reference_user_id = reference_user_id;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getIs_this_user_checked_in() {
        return is_this_user_checked_in;
    }

    public void setIs_this_user_checked_in(String is_this_user_checked_in) {
        this.is_this_user_checked_in = is_this_user_checked_in;
    }

    public String getLogin_domain() {
        return login_domain;
    }

    public void setLogin_domain(String login_domain) {
        this.login_domain = login_domain;
    }

    public String getBroadcast_type() {
        return broadcast_type;
    }

    public void setBroadcast_type(String broadcast_type) {
        this.broadcast_type = broadcast_type;
    }

    public String getViewers_count() {
        return viewers_count;
    }

    public void setViewers_count(String viewers_count) {
        this.viewers_count = viewers_count;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrent_gold_value() {
        return current_gold_value;
    }

    public void setCurrent_gold_value(String current_gold_value) {
        this.current_gold_value = current_gold_value;
    }

    public String getOver_all_gold() {
        return over_all_gold;
    }

    public void setOver_all_gold(String over_all_gold) {
        this.over_all_gold = over_all_gold;
    }

    public String getIs_the_shared_app() {
        return is_the_shared_app;
    }

    public void setIs_the_shared_app(String is_the_shared_app) {
        this.is_the_shared_app = is_the_shared_app;
    }

    public String getIs_the_user_has_gifted() {
        return is_the_user_has_gifted;
    }

    public void setIs_the_user_has_gifted(String is_the_user_has_gifted) {
        this.is_the_user_has_gifted = is_the_user_has_gifted;
    }

    public String getIs_this_user_viewed() {
        return is_this_user_viewed;
    }

    public void setIs_this_user_viewed(String is_this_user_viewed) {
        this.is_this_user_viewed = is_this_user_viewed;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMoon_level_count() {
        return moon_level_count;
    }

    public void setMoon_level_count(String moon_level_count) {
        this.moon_level_count = moon_level_count;
    }

    public String getGift_value() {
        return gift_value;
    }

    public void setGift_value(String gift_value) {
        this.gift_value = gift_value;
    }

    public String getGiftSendValue() {
        return giftSendValue;
    }

    public void setGiftSendValue(String giftSendValue) {
        this.giftSendValue = giftSendValue;
    }

    public String getTools_applied() {
        return tools_applied;
    }

    public void setTools_applied(String tools_applied) {
        this.tools_applied = tools_applied;
    }

    public String getBaggage_applied() {
        return baggage_applied;
    }

    public void setBaggage_applied(String baggage_applied) {
        this.baggage_applied = baggage_applied;
    }

    public String getIs_this_user_blocked() {
        return is_this_user_blocked;
    }

    public void setIs_this_user_blocked(String is_this_user_blocked) {
        this.is_this_user_blocked = is_this_user_blocked;
    }

    public String getIs_the_user_id_hidden() {
        return is_the_user_id_hidden;
    }

    public void setIs_the_user_id_hidden(String is_the_user_id_hidden) {
        this.is_the_user_id_hidden = is_the_user_id_hidden;
    }

    public String getIs_the_age_hidden() {
        return is_the_age_hidden;
    }

    public void setIs_the_age_hidden(String is_the_age_hidden) {
        this.is_the_age_hidden = is_the_age_hidden;
    }

    public String getIs_the_gender_hide() {
        return is_the_gender_hide;
    }

    public void setIs_the_gender_hide(String is_the_gender_hide) {
        this.is_the_gender_hide = is_the_gender_hide;
    }

    public String getIs_the_location_hidden() {
        return is_the_location_hidden;
    }

    public void setIs_the_location_hidden(String is_the_location_hidden) {
        this.is_the_location_hidden = is_the_location_hidden;
    }

    public String getIs_the_user_changed_his_blive_ID() {
        return is_the_user_changed_his_blive_ID;
    }

    public void setIs_the_user_changed_his_blive_ID(String is_the_user_changed_his_blive_ID) {
        this.is_the_user_changed_his_blive_ID = is_the_user_changed_his_blive_ID;
    }

    public String getIs_the_dob_hidden() {
        return is_the_dob_hidden;
    }

    public void setIs_the_dob_hidden(String is_the_dob_hidden) {
        this.is_the_dob_hidden = is_the_dob_hidden;
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

    public String getText_muted() {
        return text_muted;
    }

    public void setText_muted(String text_muted) {
        this.text_muted = text_muted;
    }

    public static Creator<User> getCREATOR() {
        return CREATOR;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public String getBroadcasterGiftValue() {
        return broadcasterGiftValue;
    }

    public void setBroadcasterGiftValue(String broadcasterGiftValue) {
        this.broadcasterGiftValue = broadcasterGiftValue;
    }

    public int getGuestGiftValue() {
        return guestGiftValue;
    }

    public void setGuestGiftValue(int guestGiftValue) {
        this.guestGiftValue = guestGiftValue;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }


    public int getPkTimeLeft() {
        return pkTimeLeft;
    }

    public void setPkTimeLeft(int pkTimeLeft) {
        this.pkTimeLeft = pkTimeLeft;
    }

    public String getPk_broadcaster_id() {
        return pk_broadcaster_id;
    }

    public void setPk_broadcaster_id(String pk_broadcaster_id) {
        this.pk_broadcaster_id = pk_broadcaster_id;
    }

    public String getPk_guest_id() {
        return pk_guest_id;
    }

    public void setPk_guest_id(String pk_guest_id) {
        this.pk_guest_id = pk_guest_id;
    }

    public String getPk_channelname() {
        return pk_channelname;
    }

    public void setPk_channelname(String pk_channelname) {
        this.pk_channelname = pk_channelname;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.name);
        dest.writeString(this.username);
        dest.writeString(this.email);
        dest.writeString(this.user_id);
        dest.writeString(this.activation_code);
        dest.writeString(this.last_login);
        dest.writeString(this.silver);
        dest.writeString(this.diamond);
        dest.writeString(this.mobile);
        dest.writeString(this.followers);
        dest.writeString(this.fans);
        dest.writeString(this.friends);
        dest.writeString(this.sent);
        dest.writeString(this.received);
        dest.writeString(this.profile_pic);
        dest.writeString(this.status);
        dest.writeString(this.about_me);
        dest.writeString(this.gender);
        dest.writeString(this.age);
        dest.writeString(this.image);
        dest.writeString(this.level);
        dest.writeString(this.xp);
        dest.writeString(this.moon_value);
        dest.writeString(this.isTheUserFollowing);
        dest.writeString(this.profile_pic1);
        dest.writeString(this.profile_pic2);
        dest.writeString(this.reference_user_id);
        dest.writeString(this.date_of_birth);
        dest.writeString(this.hobbies);
        dest.writeString(this.carrier);
        dest.writeString(this.latitude);
        dest.writeString(this.longitude);
        dest.writeString(this.is_this_user_checked_in);
        dest.writeString(this.login_domain);
        dest.writeString(this.broadcast_type);
        dest.writeString(this.viewers_count);
        dest.writeString(this.country);
        dest.writeString(this.current_gold_value);
        dest.writeString(this.over_all_gold);
        dest.writeString(this.is_the_shared_app);
        dest.writeString(this.is_the_user_has_gifted);
        dest.writeString(this.is_this_user_viewed);
        dest.writeString(this.state);
        dest.writeString(this.city);
        dest.writeString(this.moon_level_count);
        dest.writeString(this.gift_value);
        dest.writeString(this.giftSendValue);
        dest.writeString(this.tools_applied);
        dest.writeString(this.baggage_applied);
        dest.writeString(this.is_this_user_blocked);
        dest.writeString(this.is_the_user_id_hidden);
        dest.writeString(this.is_the_age_hidden);
        dest.writeString(this.is_the_gender_hide);
        dest.writeString(this.is_the_location_hidden);
        dest.writeString(this.is_the_user_changed_his_blive_ID);
        dest.writeString(this.is_the_dob_hidden);
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
        dest.writeString(this.text_muted);
        dest.writeString(this.broadcasterGiftValue);
        dest.writeInt(this.id);
        dest.writeInt(this.pin);
        dest.writeInt(this.total);
        dest.writeInt(this.guestGiftValue);
    }

    protected User(Parcel in) {
        this.uid = in.readString();
        this.name = in.readString();
        this.username = in.readString();
        this.email = in.readString();
        this.user_id = in.readString();
        this.activation_code = in.readString();
        this.last_login = in.readString();
        this.silver = in.readString();
        this.diamond = in.readString();
        this.mobile = in.readString();
        this.followers = in.readString();
        this.fans = in.readString();
        this.friends = in.readString();
        this.sent = in.readString();
        this.received = in.readString();
        this.profile_pic = in.readString();
        this.status = in.readString();
        this.about_me = in.readString();
        this.gender = in.readString();
        this.age = in.readString();
        this.image = in.readString();
        this.level = in.readString();
        this.xp = in.readString();
        this.moon_value = in.readString();
        this.isTheUserFollowing = in.readString();
        this.profile_pic1 = in.readString();
        this.profile_pic2 = in.readString();
        this.reference_user_id = in.readString();
        this.date_of_birth = in.readString();
        this.hobbies = in.readString();
        this.carrier = in.readString();
        this.latitude = in.readString();
        this.longitude = in.readString();
        this.is_this_user_checked_in = in.readString();
        this.login_domain = in.readString();
        this.broadcast_type = in.readString();
        this.viewers_count = in.readString();
        this.country = in.readString();
        this.current_gold_value = in.readString();
        this.over_all_gold = in.readString();
        this.is_the_shared_app = in.readString();
        this.is_the_user_has_gifted = in.readString();
        this.is_this_user_viewed = in.readString();
        this.state = in.readString();
        this.city = in.readString();
        this.moon_level_count = in.readString();
        this.gift_value = in.readString();
        this.giftSendValue = in.readString();
        this.tools_applied = in.readString();
        this.baggage_applied = in.readString();
        this.is_this_user_blocked = in.readString();
        this.is_the_user_id_hidden = in.readString();
        this.is_the_age_hidden = in.readString();
        this.is_the_gender_hide = in.readString();
        this.is_the_location_hidden = in.readString();
        this.is_the_user_changed_his_blive_ID = in.readString();
        this.is_the_dob_hidden = in.readString();
        this.fansCount = in.readString();
        this.friendsCount = in.readString();
        this.followersCount = in.readString();
        this.total_gift_send = in.readString();
        this.total_gift_receiver = in.readString();
        this.broadcasting_min_target = in.readString();
        this.broadcasting_hours = in.readString();
        this.share = in.readString();
        this.gold = in.readString();
        this.share_target = in.readString();
        this.gold_target = in.readString();
        this.viewers_target = in.readString();
        this.text_muted = in.readString();
        this.broadcasterGiftValue = in.readString();
        this.id = in.readInt();
        this.pin = in.readInt();
        this.guestGiftValue = in.readInt();
        this.total = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", user_id='" + user_id + '\'' +
                ", activation_code='" + activation_code + '\'' +
                ", last_login='" + last_login + '\'' +
                ", silver='" + silver + '\'' +
                ", diamond='" + diamond + '\'' +
                ", mobile='" + mobile + '\'' +
                ", followers='" + followers + '\'' +
                ", fans='" + fans + '\'' +
                ", friends='" + friends + '\'' +
                ", sent='" + sent + '\'' +
                ", received='" + received + '\'' +
                ", profile_pic='" + profile_pic + '\'' +
                ", status='" + status + '\'' +
                ", about_me='" + about_me + '\'' +
                ", gender='" + gender + '\'' +
                ", age='" + age + '\'' +
                ", image='" + image + '\'' +
                ", level='" + level + '\'' +
                ", xp='" + xp + '\'' +
                ", moon_value='" + moon_value + '\'' +
                ", isTheUserFollowing='" + isTheUserFollowing + '\'' +
                ", profile_pic1='" + profile_pic1 + '\'' +
                ", profile_pic2='" + profile_pic2 + '\'' +
                ", reference_user_id='" + reference_user_id + '\'' +
                ", date_of_birth='" + date_of_birth + '\'' +
                ", hobbies='" + hobbies + '\'' +
                ", carrier='" + carrier + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", is_this_user_checked_in='" + is_this_user_checked_in + '\'' +
                ", login_domain='" + login_domain + '\'' +
                ", broadcast_type='" + broadcast_type + '\'' +
                ", viewers_count='" + viewers_count + '\'' +
                ", country='" + country + '\'' +
                ", current_gold_value='" + current_gold_value + '\'' +
                ", over_all_gold='" + over_all_gold + '\'' +
                ", is_the_shared_app='" + is_the_shared_app + '\'' +
                ", is_the_user_has_gifted='" + is_the_user_has_gifted + '\'' +
                ", is_this_user_viewed='" + is_this_user_viewed + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", moon_level_count='" + moon_level_count + '\'' +
                ", gift_value='" + gift_value + '\'' +
                ", giftSendValue='" + giftSendValue + '\'' +
                ", tools_applied='" + tools_applied + '\'' +
                ", baggage_applied='" + baggage_applied + '\'' +
                ", is_this_user_blocked='" + is_this_user_blocked + '\'' +
                ", is_the_user_id_hidden='" + is_the_user_id_hidden + '\'' +
                ", is_the_age_hidden='" + is_the_age_hidden + '\'' +
                ", is_the_gender_hide='" + is_the_gender_hide + '\'' +
                ", is_the_location_hidden='" + is_the_location_hidden + '\'' +
                ", is_the_user_changed_his_blive_ID='" + is_the_user_changed_his_blive_ID + '\'' +
                ", is_the_dob_hidden='" + is_the_dob_hidden + '\'' +
                ", fansCount='" + fansCount + '\'' +
                ", friendsCount='" + friendsCount + '\'' +
                ", followersCount='" + followersCount + '\'' +
                ", total_gift_send='" + total_gift_send + '\'' +
                ", total_gift_receiver='" + total_gift_receiver + '\'' +
                ", broadcasting_min_target='" + broadcasting_min_target + '\'' +
                ", broadcasting_hours='" + broadcasting_hours + '\'' +
                ", share='" + share + '\'' +
                ", gold='" + gold + '\'' +
                ", share_target='" + share_target + '\'' +
                ", gold_target='" + gold_target + '\'' +
                ", viewers_target='" + viewers_target + '\'' +
                ", text_muted='" + text_muted + '\'' +
                ", id=" + id +
                ", pin=" + pin +
                '}';
    }

    public class GuestDetail {
        @SerializedName("user_id")
        private String user_id;
        @SerializedName("profile")
        private String profile;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @SerializedName("id")
        private int id;

        public String getChannelname() {
            return channelname;
        }

        public void setChannelname(String channelname) {
            this.channelname = channelname;
        }

        @SerializedName("channelname")
        private String channelname="";

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        @SerializedName("username")
        private String username;




        @SerializedName("challenge_time_left")
        private String challenge_time_left;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getProfile() {
            return profile;
        }

        public void setProfile(String profile) {
            this.profile = profile;
        }

        public String getChallenge_time_left() {
            return challenge_time_left;
        }

        public void setChallenge_time_left(String challenge_time_left) {
            this.challenge_time_left = challenge_time_left;
        }
    }

}