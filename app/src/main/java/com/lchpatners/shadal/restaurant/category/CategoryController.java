package com.lchpatners.shadal.restaurant.category;

import android.app.Activity;

import io.realm.Realm;
import io.realm.RealmQuery;

/**
 * Created by YoungKim on 2015. 8. 29..
 */
public class CategoryController {
    public static int getRestaurantCategory(Activity activity, int restaurant_id) {
        Category category = null;

        Realm realm = Realm.getInstance(activity);
        realm.beginTransaction();
        try {
            RealmQuery<Category> query = realm.where(Category.class).equalTo("restaurants.id", restaurant_id);
            category = query.findFirst();
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            realm.cancelTransaction();
        } finally {
            realm.close();
        }

        return category.getId();
    }
}
