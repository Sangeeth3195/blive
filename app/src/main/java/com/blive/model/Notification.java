package com.blive.model;

/**
 * Created by sans on 27-09-2018.
 **/

public class Notification {

    private String message;
    private String created_at;
    private String user_id;
    private String broadcast_type;
    private String notification_id;
    private String notificationCount;
    private String profile_pic;
    private String time;
    private String status;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getNotificationCount() {
        return notificationCount;
    }

    public void setNotificationCount(String notificationCount) {
        this.notificationCount = notificationCount;
    }

    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }

    public String getBroadcast_type() {
        return broadcast_type;
    }

    public void setBroadcast_type(String broadcast_type) {
        this.broadcast_type = broadcast_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "message='" + message + '\'' +
                ", created_at='" + created_at + '\'' +
                ", user_id='" + user_id + '\'' +
                ", broadcast_type='" + broadcast_type + '\'' +
                ", notification_id='" + notification_id + '\'' +
                ", notificationCount='" + notificationCount + '\'' +
                '}';
    }
}
