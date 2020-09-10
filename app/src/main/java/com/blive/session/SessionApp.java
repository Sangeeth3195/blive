package com.blive.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.blive.BLiveApplication;

/**
 * Created by sans on 06-03-2019.
 **/

public class SessionApp {

    private static SharedPreferences pref = BLiveApplication.getInstance().getSharedPreferences("SessionApp", Context.MODE_PRIVATE);


    public static void isFirst(boolean isFirst){
        pref.edit().putBoolean("isFirst", isFirst).apply();
    }

    public static boolean getIsFirst(){
        boolean isFirst = pref.getBoolean("isFirst", false);
        return isFirst;
    }

}
