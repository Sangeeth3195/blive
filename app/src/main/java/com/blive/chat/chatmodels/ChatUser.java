package com.blive.chat.chatmodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import io.realm.RealmModel;
import io.realm.annotations.Ignore;
import io.realm.annotations.RealmClass;

@RealmClass
public class ChatUser implements Parcelable, RealmModel {
    @Ignore
    private boolean online;
    @Exclude
    @Ignore
    private String nameToDisplay;
    @Ignore
    private boolean typing;
    @Ignore
    @Exclude
    private boolean selected;
    private String id, name, image;
    private long timestamp;

    public ChatUser() {
    }

    public static final Creator<ChatUser> CREATOR = new Creator<ChatUser>() {
        @Override
        public ChatUser createFromParcel(Parcel in) {
            return new ChatUser(in);
        }

        @Override
        public ChatUser[] newArray(int size) {
            return new ChatUser[size];
        }
    };

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public ChatUser(String id, String name, String image, String nameToDisplay, long timestamp) {
        this.id = id;
        this.name = name;
        this.online = false;
        this.image = image;
        this.typing = false;
        this.nameToDisplay = nameToDisplay;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatUser user = (ChatUser) o;
        return id.equals(user.id);
    }

    public String getNameToDisplay() {
        return nameToDisplay;
    }

    public void setNameToDisplay(String nameToDisplay) {
        this.nameToDisplay = nameToDisplay;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public boolean isTyping() {
        return typing;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isOnline() {
        return online;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }


    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public void setOnline(boolean online) {
        this.online = online;
    }

    public long getTimeStamp() {
        return timestamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timestamp = timeStamp;
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (online ? 1 : 0));
        parcel.writeString(nameToDisplay);
        parcel.writeByte((byte) (typing ? 1 : 0));
        parcel.writeByte((byte) (selected ? 1 : 0));
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(image);
        parcel.writeLong(timestamp);
    }

    protected ChatUser(Parcel in) {
        online = in.readByte() != 0;
        nameToDisplay = in.readString();
        typing = in.readByte() != 0;
        selected = in.readByte() != 0;
        id = in.readString();
        name = in.readString();
        image = in.readString();
        timestamp = in.readLong();
    }

    public static boolean validate(ChatUser user) {
        return user != null && user.getId() != null && user.getName() != null;
    }
}