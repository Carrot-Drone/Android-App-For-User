package com.lchpatners.shadal.restaurant;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.lchpatners.shadal.RootActivity;
import com.lchpatners.shadal.campus.Campus;
import com.lchpatners.shadal.restaurant.category.Category;
import com.lchpatners.shadal.restaurant.flyer.Flyer;
import com.lchpatners.shadal.util.LogUtils;
import com.lchpatners.shadal.util.RetrofitConverter;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by YoungKim on 2015. 8. 28..
 */
public class RestaurantController {
    private static final String TAG = LogUtils.makeTag(RestaurantController.class);
    private static final String BASE_URL = "http://www.shadal.kr:3000";

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

    public static void insertOrUpdateAllRestaurantInfo(final Activity activity, Campus campus, final Class clazz) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(new RetrofitConverter().createRestaurantConverter()))
                .setEndpoint(BASE_URL) // The base API endpoint.
                .build();

        RestaurantAPI restaurantAPI = restAdapter.create(RestaurantAPI.class);

        restaurantAPI.getAllRestaurantList(campus.getId(), new Callback<List<Category>>() {
            @Override
            public void success(List<Category> categories, Response response) {
                Realm realm = Realm.getInstance(activity);
                realm.beginTransaction();

                try {
                    RealmQuery<Flyer> query = realm.where(Flyer.class);
                    RealmResults<Flyer> flyers = query.findAll();
                    flyers.clear();
                    realm.copyToRealmOrUpdate(categories);
                    realm.commitTransaction();
                } catch (Exception e) {
                    realm.cancelTransaction();
                    e.printStackTrace();
                } finally {
                    realm.close();
                }

                Intent intent = new Intent(activity, clazz);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
                activity.finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, error.toString());
                error.printStackTrace();
            }
        });
    }
}
