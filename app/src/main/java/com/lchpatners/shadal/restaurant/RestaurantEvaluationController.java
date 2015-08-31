package com.lchpatners.shadal.restaurant;

import android.app.Activity;

import io.realm.Realm;
import io.realm.RealmQuery;

/**
 * Created by YoungKim on 2015. 8. 26..
 */
public class RestaurantEvaluationController {
    private Activity mActivity;
    private final int GOOD = 1;
    private final int BAD = 0;

    public RestaurantEvaluationController(Activity activity) {
        this.mActivity = activity;
    }

    public void evaluate(int score, int restaurant_id) {
        Realm realm = Realm.getInstance(mActivity);

        realm.beginTransaction();
        RestaurantEvaluation restaurantEvaluation = new RestaurantEvaluation();
        restaurantEvaluation.setRestaurant_id(restaurant_id);

        if (score == GOOD) {
            restaurantEvaluation.setEvaluation(GOOD);
        } else if (score == BAD) {
            restaurantEvaluation.setEvaluation(BAD);
        }

        try {
            realm.copyToRealmOrUpdate(restaurantEvaluation);
            realm.commitTransaction();
        } catch (Exception e) {
            realm.cancelTransaction();
            e.printStackTrace();
        } finally {
            realm.close();
        }
    }

    public int getEvaluationByCurrentUser(int restaurant_id) {
        Realm realm = Realm.getInstance(mActivity);
        RealmQuery<RestaurantEvaluation> query =
                realm.where(RestaurantEvaluation.class).equalTo("restaurant_id", restaurant_id);
        RestaurantEvaluation restaurantEvaluation = query.findFirst();
        realm.close();

        //When there's no pre-evaluation by user, send -1 as score
        return restaurantEvaluation != null ? restaurantEvaluation.getEvaluation() : -1;
    }
}
