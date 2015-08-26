package com.lchpatners.shadal.restaurant;

import android.app.Activity;
import android.content.Context;

import com.lchpatners.shadal.restaurant.menu.RestaurantMenu;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;

/**
 * Created by Youngkim on 2015. 8. 26..
 */
public class RestaurantMenuController {
    private Context mContext;
    private Restaurant mRestaurant;
    private List<RestaurantMenu> mRestaurantMenus;

    public RestaurantMenuController(Context context, Restaurant restaurant) {
        this.mContext = context;
        this.mRestaurant = restaurant;
    }

    public List<RestaurantMenu> getRestaurantMenus() {
        Realm realm = Realm.getInstance(mContext);

        try {
            realm.beginTransaction();
            RealmQuery<Restaurant> query = realm.where(Restaurant.class);
            RealmList<RestaurantMenu> restaurantMenus = query.equalTo("id", mRestaurant.getId()).findFirst().getMenus();
            this.mRestaurantMenus = restaurantMenus;
            realm.commitTransaction();
        } finally {
            realm.close();
        }

        return mRestaurantMenus;
    }
}
