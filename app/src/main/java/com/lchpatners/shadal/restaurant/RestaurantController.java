package com.lchpatners.shadal.restaurant;

import android.app.Activity;

import io.realm.Realm;
import io.realm.RealmQuery;

/**
 * Created by YoungKim on 2015. 8. 28..
 */
public class RestaurantController {
    public static Restaurant getRestaurant(Activity activity, int restaurant_id) {
        Restaurant restaurant = null;

        Realm realm = Realm.getInstance(activity);
        realm.beginTransaction();

        try {
            RealmQuery<Restaurant> query = realm.where(Restaurant.class);
            restaurant = query.equalTo("id", restaurant_id).findFirst();
            realm.commitTransaction();
        } catch (Exception e) {
            realm.cancelTransaction();
        } finally {
            realm.close();
        }

        return restaurant;
    }
}
