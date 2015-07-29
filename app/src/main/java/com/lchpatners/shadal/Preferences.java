package com.lchpatners.shadal;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Manages {@link android.content.SharedPreferences SharedPreferences}.
 * Saves campus meta-data and device UUID.
 */
public class Preferences {

    /**
     * Preferences name.
     */
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

    public static String getCampusKoreanName(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString("CAMPUS_KOR", null);
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

    /**
     * Get device {@link java.util.UUID UUID} from {@link com.lchpatners.shadal.Preferences
     * Preferences}, or generate newly when none is stored.
     * @param context {@link android.content.Context}
     * @return {@link java.util.UUID UUID} {@link java.lang.String String}.
     */
    public static String getDeviceUuid(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        final String id = settings.getString("DEVICE_ID", null);
        UUID uuid = null;
        if (id != null) {
            uuid = UUID.fromString(id);
        } else {
            final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            try {
                if (!"9774d56d682e549c".equals(androidId)) {
                    uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                } else {
                    final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                    uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            } finally {
                if (uuid != null) setDeviceUuid(context, uuid.toString());
            }
        }
        return uuid.toString();
    }
}
