package com.blive.session;

/**
 * Created by sans on 9/27/2019.
 **/


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


/**
 * Created by CDS121 on 06-07-2018.
 */

public class SessionManager {
    private Context mContext;
    int PRIVATE_MODE = 0;

    public SessionManager(Context context) {
        mContext = context;
    }

    public String getSessionStringValue(String sessionName, String sessionKey) {
        SharedPreferences settings = getSession(sessionName);
        // Reading String value from SharedPreferences
        Log.w("Success", "String::: " + settings.getString(sessionKey, null));
        return settings.getString(sessionKey, "0");
    }


    private SharedPreferences getSession(String sessionName) {
        int PRIVATE_MODE = 0;
        return mContext.getSharedPreferences(sessionName, PRIVATE_MODE);
    }



    public void storeSessionStringvalue(String sessionName, String key, String value) {
        SharedPreferences settings = getSession(sessionName);
        // Writing String data to SharedPreferences
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

}
