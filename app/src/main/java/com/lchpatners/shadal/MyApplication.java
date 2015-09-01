package com.lchpatners.shadal;

import android.app.Application;

import com.flurry.android.FlurryAgent;

/**
 * Created by YoungKim on 2015. 9. 2..
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // configure Flurry
        FlurryAgent.setLogEnabled(false);
        // init Flurry
        FlurryAgent.init(this, "9WVZS5X9X9B53CZ562FN");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
