package com.blive.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by sans on 9/27/2019.
 **/

public class SessionNotification {



    private SharedPreferences prefs;

    public SessionNotification(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setnotificationcount(int usename) {
        prefs.edit().putInt("notification", usename).apply();
    }

    public int getnotificationcount() {
        int usename = prefs.getInt("notification",0);
        return usename;
    }
}
