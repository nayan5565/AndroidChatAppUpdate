package com.example.dev.chatapplication;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Dev on 1/17/2018.
 */

public class Utils {
    public static final String APP_NAME = "Chat App";

    // save data to sharedPreference
    public static void savePref(String name, String value) {
        SharedPreferences pref = MainApplication.getInstance().getContext().getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(name, value);
        editor.apply();
    }

    // get data from shared preference
    public static String getPref(String name, String defaultValue) {
        SharedPreferences pref = MainApplication.getInstance().getContext().getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        return pref.getString(name, defaultValue);
    }

    public static String getToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy" + "/n" + "hh:mm:ss");
        String day = sdf.format(new Date());
        return day;
    }
}
