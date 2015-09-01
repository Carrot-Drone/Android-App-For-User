package com.lchpatners.shadal;

import android.app.Activity;
import android.support.design.widget.NavigationView;
import android.widget.TextView;

import com.lchpatners.shadal.call.RecentCall;
import com.lchpatners.shadal.campus.Campus;
import com.lchpatners.shadal.campus.CampusController;
import com.lchpatners.shadal.restaurant.category.Category;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by eunhyekim on 2015. 8. 29..
 */
public class NavigationDrawerController {
    static TextView lastDay;
    static TextView categoryName;
    static TextView myCalls;
    static Campus mCampus;

    public static void updateNavigationDrawer(Activity activity, NavigationView navigationView) {
        mCampus = CampusController.getCurrentCampus(activity);

        lastDay = (TextView) navigationView.findViewById(R.id.last_day); //마지막 주문한날로부터
        categoryName = (TextView) navigationView.findViewById(R.id.category); //가장 많이 주문한 음식
        myCalls = (TextView) navigationView.findViewById(R.id.my_calls); //내 주문수

        setTotalCallCount(activity);
        setLastDay(activity);
        setTheMostOrderedFood(activity);
    }

    public static void setTotalCallCount(Activity activity) {

        Realm realm = Realm.getInstance(activity);
        int total_call_count = 0;
        RealmResults<RecentCall> recentCallList = null;
        try {
            realm.beginTransaction();
            RealmQuery<RecentCall> query = realm.where(RecentCall.class).equalTo("campus_id", mCampus.getId());
            recentCallList = query.findAll();
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            realm.cancelTransaction();
        } finally {
            realm.close();
        }
        if (recentCallList != null) {
            for (int i = 0; i < recentCallList.size(); i++)
                total_call_count += recentCallList.get(i).getCall_count();
        }
        myCalls.setText(total_call_count + "회");
    }


    public static void setLastDay(Activity activity) {

        int lastday = -1;
        Realm realm = Realm.getInstance(activity);
        RecentCall recentCall = null;
        try {
            realm.beginTransaction();
            RealmQuery<RecentCall> query = realm.where(RecentCall.class).equalTo("campus_id", mCampus.getId());
            recentCall = query.findFirst();
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            realm.cancelTransaction();
        } finally {
            realm.close();
        }
        if (recentCall != null) {
            long last = recentCall.getRecent_call_date().getTime();
            Date date = new Date();
            Long now = date.getTime();
            last = now - last;
            lastday = (int) last / (24 * 60 * 60 * 1000);
        }
        String day = (lastday == -1) ? "" : lastday + "일";
        lastDay.setText(day);
    }

    public static void setTheMostOrderedFood(Activity activity) {
        Realm realm = Realm.getInstance(activity);
        int[] count = new int[8];
        int[] category_id = new int[8];
        int theMostOrderedCategoryId = -1;
        for (int i = 0; i < count.length; i++) {
            count[i] = 0;
        }
        RealmResults<RecentCall> recentCallList = null;
        try {
            realm.beginTransaction();
            RealmQuery<RecentCall> query = realm.where(RecentCall.class).equalTo("campus_id", mCampus.getId());
            recentCallList = query.findAll();
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            realm.cancelTransaction();
        } finally {
            realm.close();
        }

        if (recentCallList != null) {
            for (int i = 0; i < recentCallList.size(); i++) {
                count[recentCallList.get(i).getCategory_id() % 8] += 1;
                category_id[recentCallList.get(i).getCategory_id() % 8] = recentCallList.get(i).getCategory_id();
            }
            int max = -1;
            for (int i = 0; i < count.length; i++) {
                if (count[i] > max) {
                    theMostOrderedCategoryId = category_id[i];
                    max = count[i];
                }
            }
        }
        Category category = null;
        if (theMostOrderedCategoryId >= 0) {
            realm = Realm.getInstance(activity);
            try {
                realm.beginTransaction();
                RealmQuery<Category> query = realm.where(Category.class).equalTo("id", theMostOrderedCategoryId);
                category = query.findFirst();
                realm.commitTransaction();
            } catch (Exception e) {
                e.printStackTrace();
                realm.cancelTransaction();
            } finally {
                realm.close();
            }

        }

        categoryName.setText((category != null) ? category.getTitle() : "");
    }
}