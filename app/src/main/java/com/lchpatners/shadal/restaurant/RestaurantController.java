package com.lchpatners.shadal.restaurant;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.lchpatners.shadal.campus.Campus;
import com.lchpatners.shadal.campus.CampusController;
import com.lchpatners.shadal.restaurant.category.Category;
import com.lchpatners.shadal.restaurant.flyer.Flyer;
import com.lchpatners.shadal.util.LogUtils;
import com.lchpatners.shadal.util.RetrofitConverter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
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
    public static final String LIST_ALL = "list_all";
    public static final String LIST_OFFICE_HOUR = "list_office_hour";
    public static final String LIST_HAS_FLYER = "list_has_flyer";
    public static final String LIST_FLYER_OFFICE = "list_flyer_office";

    public static boolean officeHour = false;

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
                try {
                    realm.beginTransaction();

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
                Intent intent = new Intent(activity, clazz);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
                activity.finish();
            }
        });
    }

    public static List<Restaurant> getRestaurantList(Activity activity, int categoryNumber, String flag) {
        List<Restaurant> restaurantList = new ArrayList<>();

        Realm realm = Realm.getInstance(activity);
        try {
            realm.beginTransaction();
            RealmQuery<Category> categoryQuery = realm.where(Category.class).equalTo(
                    "campus_id", CampusController.getCurrentCampus(activity).getId());
            RealmList<Restaurant> allRestaurantList = categoryQuery.findAll().get(categoryNumber).getRestaurants();

            switch (flag) {
                case LIST_ALL:
                    restaurantList = allRestaurantList;
                    break;
                case LIST_OFFICE_HOUR:
                    for (Restaurant restaurant : allRestaurantList) {
                        if (RestaurantController.checkOfficeHour(
                                restaurant.getOpening_hours(), restaurant.getClosing_hours())) {
                            restaurantList.add(restaurant);
                        }
                    }
                    break;
                case LIST_HAS_FLYER:
                    for (Restaurant restaurant : allRestaurantList) {
                        if (restaurant.isHas_flyer()) {
                            restaurantList.add(restaurant);
                        }
                    }
                    break;
                case LIST_FLYER_OFFICE:
                    for (Restaurant restaurant : allRestaurantList) {
                        if (restaurant.isHas_flyer() && RestaurantController.checkOfficeHour(
                                restaurant.getOpening_hours(), restaurant.getClosing_hours())) {
                            restaurantList.add(restaurant);
                        }
                    }
                    break;
                default:
                    restaurantList = allRestaurantList;
                    break;
            }
            realm.commitTransaction();
        } catch (Exception e) {
            realm.cancelTransaction();
            e.printStackTrace();
        } finally {
            realm.close();
        }

        return restaurantList;
    }

    private static boolean checkOfficeHour(float openHour, float closeHour) {
        float currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        float currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
        float convertedMinute = (currentMinute / 60);
        float currentTime = currentHour + convertedMinute;

        if (openHour == 0.0 && closeHour == 24.0) {
            return true;
        } else if (openHour == 0.0 && closeHour == 0.0) {
            return true;
        } else {
            if (openHour < currentTime && currentTime < closeHour) {
                return true;
            } else {
                return false;
            }
        }
    }
}
