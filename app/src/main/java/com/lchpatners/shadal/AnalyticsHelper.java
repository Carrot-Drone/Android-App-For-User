package com.lchpatners.shadal;


import android.content.Context;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Sends data for Google Analytics.
 */
public class AnalyticsHelper {

    Context context;
    /**
     * @see com.google.android.gms.analytics.Tracker Tracker
     */
    Tracker tracker;

    /**
     * Initializes members.
     * @param context {@link android.content.Context}
     */
    public AnalyticsHelper(Context context) {
        this.context = context;
        GoogleAnalytics ga = GoogleAnalytics.getInstance(context);
        tracker = ga.newTracker(R.xml.tracker_configuation);
    }

    /**
     * Set the screen information and send it to the Google Analytics. Used to
     * track movements on the user's screen. It can be either an {@link android.app.Activity
     * Activity} or a {@link android.support.v4.app.Fragment Fragment}.
     * @param screenName The screen's name to be sent.
     */
    public void sendScreen(String screenName) {
        tracker.setScreenName(screenName);
        HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder();
        String campusName = Preferences.getCampusKoreanShortName(context);
        if (campusName != null) {
            builder.setCustomDimension(1, campusName);
        }
        tracker.send(builder.build());
    }

    /**
     * Send information about an event when it occurs.
     * @param category The category of the event.
     * @param action The action of the event.
     * @param label The label of the event.
     */
    public void sendEvent(String category, String action, String label) {
        tracker.send(new HitBuilders.EventBuilder()
                .setCustomDimension(1, Preferences.getCampusKoreanShortName(context))
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }

}
