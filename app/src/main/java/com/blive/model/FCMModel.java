package com.blive.model;

/**
 * Created by sans on 18-Sep-19.
 **/

public class FCMModel {
    String Notificationcontent;
    String NotificationImage;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    String date;

    public String getNotificationTitle() {
        return NotificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        NotificationTitle = notificationTitle;
    }

    String NotificationTitle;

    public String getNotificationcontent() {
        return Notificationcontent;
    }

    public void setNotificationcontent(String notificationcontent) {
        Notificationcontent = notificationcontent;
    }

    public String getNotificationImage() {
        return NotificationImage;
    }

    public void setNotificationImage(String notificationImage) {
        NotificationImage = notificationImage;
    }


}
