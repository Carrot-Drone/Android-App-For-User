package com.lchpatners.shadal;


import android.content.Context;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class AnalyticsHelper {

    Context context;
    Tracker tracker;

    public AnalyticsHelper(Context context) {
        this.context = context;
        GoogleAnalytics ga = GoogleAnalytics.getInstance(context);
        tracker = ga.newTracker(R.xml.tracker_configuation);
    }

    public void sendScreen(String screenName) {
        tracker.setScreenName(screenName);
        HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder();
        String campusName = Preferences.getCampusKoreanShortName(context);
        if (campusName != null) {
            builder.setCustomDimension(1, campusName);
        }
        tracker.send(builder.build());
    }

    public void sendEvent(String category, String action, String label) {
        tracker.send(new HitBuilders.EventBuilder()
                .setCustomDimension(1, Preferences.getCampusKoreanShortName(context))
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }

}
