package com.lchpartners.apphelper.preference;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by swchoi06 on 8/31/14.
 */
public class PrefUtil {
    private final static String PREF_NAME = "prefUtil";

    public final static String VERSION_KEY = "APP_VERSION";
    public final static String VERSION = "2.0.0";

    public static String getVersion (Context c) {
        SharedPreferences pref = c.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        return pref.getString(VERSION_KEY, "1.0.0");
    }

    public static void setVersion(Context c) {
        Log.d("tag", "set Version to : "+VERSION);
        SharedPreferences pref = c.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(VERSION_KEY, VERSION);
        editor.commit();
    }
}