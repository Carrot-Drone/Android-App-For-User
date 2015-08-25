package com.lchpatners.shadal.restaurant.menu;

import android.app.Activity;

import com.lchpatners.shadal.restaurant.Restaurant;

import io.realm.Realm;
import io.realm.RealmQuery;

/**
 * Created by Youngkim on 2015. 8. 25..
 */
public class MenuListController {
    private Activity mActivity;

    public MenuListController(Activity activity) {
        this.mActivity = activity;
    }

    public Restaurant getRestaurant(int restaurant_id) {
        Restaurant restaurant;

        Realm realm = Realm.getInstance(mActivity);
        realm.beginTransaction();

        RealmQuery<Restaurant> query = realm.where(Restaurant.class);
        restaurant = query.equalTo("id", restaurant_id).findFirst();

        realm.commitTransaction();
        realm.close();

        return restaurant;
    }
}
