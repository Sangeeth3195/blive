package com.blive.fcm;

/**
 * Created by sans on 05/11/18.
 */
public class Config {

    // flag to identify whether to show single line
    // or multi line test push notification tray
    public static boolean appendNotificationMessages = true;

    // globe topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "globe";
    public static final String TOPIC_ALL = "all";
    public static final String TOPIC_DEV = "dev";
    public static final String TOPIC_STAGE = "stg";
    public static final String TOPIC_PROD = "prod";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // type of push messages
    public static final int PUSH_TYPE_LEVEL = 1;
    public static final int PUSH_TYPE_USER = 2;

    // id to handle the notification in the notification try
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";
}
