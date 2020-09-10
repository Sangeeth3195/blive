package com.blive.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.blive.BLiveApplication;


public class SessionLogin {

    private static SharedPreferences pref = BLiveApplication.getInstance().getSharedPreferences("SessionLogin", Context.MODE_PRIVATE);

    public static void saveLoginSession() {
        pref.edit().putBoolean("isValid", true).apply();
    }

    public static void clearLoginSession() {
        pref.edit().clear().apply();
    }

    public static boolean getLoginSession() {
        return pref.getBoolean("isValid", false);
    }
}

