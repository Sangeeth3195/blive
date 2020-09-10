package com.blive.session;

import android.content.Context;
import android.content.SharedPreferences;


import com.blive.BLiveApplication;
import com.blive.model.User;
import com.google.gson.Gson;


public class SessionUser {

    private static SharedPreferences pref = BLiveApplication.getInstance().getSharedPreferences("SessionUser", Context.MODE_PRIVATE);

    public static void saveUser(User user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        pref.edit().putString("user", json).apply();
    }

    public void saveUserNew(User user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        pref.edit().putString("user", json).apply();
    }

    public static User getUser() {
        Gson gson = new Gson();
        String json = pref.getString("user", "");
        return gson.fromJson(json, User.class);
    }

    public  User getUserData() {
        Gson gson = new Gson();
        String json = pref.getString("user", "");
        return gson.fromJson(json, User.class);
    }

    public static void isScreenSharing(boolean isScreenSHaring) {
        pref.edit().putBoolean("isScreenSharing", isScreenSHaring).apply();
    }

    public static boolean getIsScreenSharing() {
        return pref.getBoolean("isScreenSharing", false);
    }

    public static void saveToken(String token){
        pref.edit().putString("token", token).apply();
    }

    public static String getToken(){
        String token = pref.getString("token", "");
        return token;
    }

    public static void clearUserSession() {
        pref.edit().clear().apply();
    }

    public static void isRtmLoggedIn(boolean isLoggedIn) {
        pref.edit().putBoolean("isRTMLoggedIn", isLoggedIn).apply();
    }

    public static boolean getRtmLoginSession() {
        return pref.getBoolean("isRTMLoggedIn", false);
    }
}
