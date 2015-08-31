package com.lchpatners.shadal.call;

import android.app.Activity;

import com.lchpatners.shadal.campus.Campus;
import com.lchpatners.shadal.campus.CampusController;
import com.lchpatners.shadal.restaurant.RestaurantController;
import com.lchpatners.shadal.restaurant.category.CategoryController;

import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by youngkim on 2015. 8. 22..
 */
public class RecentCallController {
    public static final String ORDER_BY_NAME = "name";
    public static final String ORDER_BY_CALL_RECENT = "call_recent";
    public static final String ORDER_BY_CALL_COUNT = "call_count";

    public static List<RecentCall> getRecentCallList(Activity activity, String orderby) {
        RealmResults<RecentCall> queriedRecentCallList = null;

        Realm realm = Realm.getInstance(activity);
        try {
            realm.beginTransaction();
            RealmQuery<Campus> campusQuery = realm.where(Campus.class);
            Campus campus = campusQuery.findFirst();
            RealmQuery<RecentCall> RecentQuery = realm.where(RecentCall.class).equalTo("campus_id", campus.getId());
            queriedRecentCallList = RecentQuery.findAll();
            switch (orderby) {
                case ORDER_BY_CALL_RECENT:
                    queriedRecentCallList.sort("recent_call_date", RealmResults.SORT_ORDER_DESCENDING);
                    break;
                case ORDER_BY_NAME:
                    queriedRecentCallList.sort("restaurant_name");
                    break;
                case ORDER_BY_CALL_COUNT:
                    queriedRecentCallList.sort("call_count", RealmResults.SORT_ORDER_DESCENDING);
                    break;
                default:
                    queriedRecentCallList.sort("recent_call_date", RealmResults.SORT_ORDER_DESCENDING);
                    break;
            }
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            realm.cancelTransaction();
        } finally {
            realm.close();
        }

        return queriedRecentCallList;
    }

    public static int getRecentCallCountByRestaurantId(Activity activity, int restaurant_id) {
        Realm realm = Realm.getInstance(activity);
        RecentCall recentCall = null;
        try {
            realm.beginTransaction();
            RealmQuery<RecentCall> query = realm.where(RecentCall.class).equalTo("restaurant_id", restaurant_id);
            recentCall = query.findFirst();
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            realm.cancelTransaction();
        } finally {
            realm.close();
        }

        return recentCall != null ? recentCall.getCall_count() : 0;
    }

    public static void stackRecentCall(Activity activity, int restaurant_id) {
        if (checkHasRecentCall(activity, restaurant_id) != 1) {
            Realm realm = Realm.getInstance(activity);

            RecentCall recentCall = new RecentCall();
            recentCall.setRestaurant_id(restaurant_id);
            recentCall.setCampus_id(CampusController.getCurrentCampus(activity).getId());
            recentCall.setCategory_id(CategoryController.getRestaurantCategory(activity, restaurant_id));
            recentCall.setRestaurant_name(RestaurantController.getRestaurant(activity, restaurant_id).getName());
            recentCall.setRecent_call_date(Calendar.getInstance().getTime());

            realm.beginTransaction();
            try {
                RealmQuery<RecentCall> query = realm.where(RecentCall.class).equalTo("restaurant_id", restaurant_id);
                if (query.findFirst() != null) {
                    recentCall.setCall_count(query.findFirst().getCall_count() + 1);
                } else {
                    recentCall.setCall_count(1);
                }
                realm.copyToRealmOrUpdate(recentCall);
                realm.commitTransaction();
            } catch (Exception e) {
                e.printStackTrace();
                realm.cancelTransaction();
            } finally {
                realm.close();
            }
        }
    }

    public static int checkHasRecentCall(Activity activity, int restaurant_id) {
        int hasRecentCall = 0;
        RecentCall recentCall = null;

        Realm realm = Realm.getInstance(activity);
        realm.beginTransaction();
        try {
            RealmQuery<RecentCall> query = realm.where(RecentCall.class).equalTo("restaurant_id", restaurant_id);
            recentCall = query.findFirst();
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            realm.cancelTransaction();
        } finally {
            realm.close();
        }

        if (recentCall != null) {
            long recentCallTime = recentCall.getRecent_call_date().getTime() + 3 * 60 * 60 * 1000;
            long currentTime = Calendar.getInstance().getTimeInMillis();

            if (recentCallTime >= currentTime) {
                hasRecentCall = 1;
            }
        }

        return hasRecentCall;
    }
}
