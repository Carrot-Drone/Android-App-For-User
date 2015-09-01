package com.lchpatners.shadal.util;


import android.app.Activity;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.lchpatners.shadal.R;
import com.lchpatners.shadal.campus.CampusController;

/**
 * Sends data for Google Analytics.
 */
public class AnalyticsHelper {

    Activity mActivity;
    /**
     * @see Tracker Tracker
     */
    Tracker tracker;

    /**
     * Initializes members.
     *
     * @param activity {@link Context}
     */
    public AnalyticsHelper(Activity activity) {
        this.mActivity = activity;
        GoogleAnalytics ga = GoogleAnalytics.getInstance(activity);
        tracker = ga.newTracker(R.xml.tracker_configuation);
    }

    /**
     * Set the screen information and send it to the Google Analytics. Used to
     * track movements on the user's screen. It can be either an {@link android.app.Activity
     * Activity} or a {@link android.support.v4.app.Fragment Fragment}.
     *
     * @param screenName The screen's name to be sent.
     */
    public void sendScreen(String screenName) {
        tracker.setScreenName(screenName);
        HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder();
        String campusName;
        if (CampusController.getCurrentCampus(mActivity) != null) {
            campusName = CampusController.getCurrentCampus(mActivity).getName_kor_short();
            builder.setCustomDimension(1, campusName);
        } else {
            campusName = "선택안함";
            builder.setCustomDimension(1, campusName);
        }
        tracker.send(builder.build());
    }

    /**
     * Send information about an event when it occurs.
     *
     * @param category The category of the event.
     * @param action   The action of the event.
     * @param label    The label of the event.
     */
    public void sendEvent(String category, String action, String label) {
        String campusName;
        if (CampusController.getCurrentCampus(mActivity) != null) {
            campusName = CampusController.getCurrentCampus(mActivity).getName_kor_short();
        } else {
            campusName = "선택안함";
        }
        tracker.send(new HitBuilders.EventBuilder()
                .setCustomDimension(1, campusName)
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }

}
