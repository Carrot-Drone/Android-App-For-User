package com.lchpatners.shadal.call;

import android.app.Activity;
import android.util.Log;

import com.lchpatners.shadal.campus.CampusController;
import com.lchpatners.shadal.restaurant.RestaurantController;
import com.lchpatners.shadal.restaurant.category.CategoryController;

import java.util.Calendar;

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

//    public List<RecentCall> getRecentCallList(Activity activity, String orderby) {
//        List<RecentCall> recentCallList = new RealmList<RecentCall>();
//
//        Realm realm = Realm.getInstance(activity);
//        realm.beginTransaction();
//
//        //TODO : get lead of this disgusting switch clause
//        switch (orderby) {
//            case ORDER_BY_CALL_RECENT:
//                try {
//                    RealmQuery<RecentCall> query = realm.where(RecentCall.class);
//                    recentCallList = query.findAll();
//                    realm.commitTransaction();
//                } catch (Exception e) {
//                    realm.cancelTransaction();
//                } finally {
//                    realm.close();
//                }
//            case ORDER_BY_NAME:
//                try {
//                    RealmQuery<RecentCall> query = realm.where(RecentCall.class);
//                    recentCallList = query.findAll();
//                    realm.commitTransaction();
//                } catch (Exception e) {
//                    realm.cancelTransaction();
//                } finally {
//                    realm.close();
//                }
//            case ORDER_BY_CALL_COUNT:
//                try {
//                    RealmQuery<RecentCall> query = realm.where(RecentCall.class);
//                    recentCallList = query.findAll();
//                    realm.commitTransaction();
//                } catch (Exception e) {
//                    realm.cancelTransaction();
//                } finally {
//                    realm.close();
//                }
//            default:
//                try {
//                    RealmQuery<RecentCall> query = realm.where(RecentCall.class);
//                    recentCallList = query.findAll();
//                    realm.commitTransaction();
//                } catch (Exception e) {
//                    realm.cancelTransaction();
//                } finally {
//                    realm.close();
//                }
//        }
//
//        return recentCallList;
//    }

    public static int getRecentCallCountByRestaurantId(Activity activity, int restaurant_id) {
        Realm realm = Realm.getInstance(activity);
        realm.beginTransaction();

        RealmResults<RecentCall> recentCallList = null;
        try {
            RealmQuery<RecentCall> query = realm.where(RecentCall.class).equalTo("restaurant_id", restaurant_id);
            recentCallList = query.findAll();
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            realm.cancelTransaction();
        } finally {
            realm.close();
        }

        return recentCallList.size();
    }

    public static void stackRecentCall(Activity mActivity, int restaurant_id) {
        if (checkHasRecentCall(mActivity, restaurant_id) != 1) {
            Realm realm = Realm.getInstance(mActivity);

            RecentCall recentCall = new RecentCall();
            recentCall.setRestaurant_id(restaurant_id);
            recentCall.setCampus_id(CampusController.getCurrentCampus(mActivity).getId());
            recentCall.setCategory_id(CategoryController.getRestaurantCategory(mActivity, restaurant_id));
            recentCall.setRestaurant_name(RestaurantController.getRestaurant(mActivity, restaurant_id).getName());
            recentCall.setResent_call_date(Calendar.getInstance().getTime());

            realm.beginTransaction();
            try {
                realm.copyToRealm(recentCall);
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
            RealmResults<RecentCall> recentCalls = query.findAll();
            recentCalls.sort("resent_call_date", RealmResults.SORT_ORDER_DESCENDING);
            if (recentCalls.size() != 0) {
                recentCall = recentCalls.get(0);
            }
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            realm.cancelTransaction();
        } finally {
            realm.close();
        }

        if (recentCall != null) {
            long recentCallTime = recentCall.getResent_call_date().getTime() + 3 * 60 * 60 * 1000;
            long currentTime = Calendar.getInstance().getTimeInMillis();

            if (recentCallTime >= currentTime) {
                hasRecentCall = 1;
            }
        }

        return hasRecentCall;
    }
}
