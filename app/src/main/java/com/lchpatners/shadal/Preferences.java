package com.lchpatners.shadal;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Guanadah on 2015-02-12.
 */
public class Preferences {

    private static final String PREFS_NAME = "Prefs";

    public static void setCampus(Context context, JSONObject campus) {
        try {
            setCampusEnglishName(context, campus.getString("name_eng"));
            setCampusKoreanName(context, campus.getString("name_kor"));
            setCampusKoreanShortName(context, campus.getString("name_kor_short"));
            setCampusEmail(context, campus.getString("email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void setCampusEnglishName(Context context, String campus) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("CAMPUS_ENG", campus);
        editor.apply();
    }

    public static void setCampusKoreanName(Context context, String campus) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("CAMPUS_KOR", campus);
        editor.apply();
    }

    public static void setCampusKoreanShortName(Context context, String campus) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("CAMPUS_KOR_SHORT", campus);
        editor.apply();
    }

    public static void setCampusEmail(Context context, String email) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("EMAIL", email);
        editor.apply();
    }

    public static String getCampusEmail(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString("EMAIL", "campusdal@gmail.com");
    }

    public static String getCampusKoreanShortName(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString("CAMPUS_KOR_SHORT", null);
    }

    public static String getCampusEnglishName(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString("CAMPUS_ENG", null);
    }

    public static void setDeviceUuid(Context context, String id) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("DEVICE_ID", id);
        editor.apply();
    }

    public static String getDeviceUuid(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString("DEVICE_ID", null);
    }
}
