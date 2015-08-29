package com.lchpatners.shadal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.lchpatners.shadal.call.RecentCall;
import com.lchpatners.shadal.campus.Campus;
import com.lchpatners.shadal.campus.CampusAPI;
import com.lchpatners.shadal.login.LoginCampusSelectActivity;
import com.lchpatners.shadal.restaurant.RestaurantAPI;
import com.lchpatners.shadal.restaurant.category.Category;
import com.lchpatners.shadal.restaurant.flyer.Flyer;
import com.lchpatners.shadal.util.LogUtils;
import com.lchpatners.shadal.util.Preferences;
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

public class LandingActivity extends ActionBarActivity {
    private static final String TAG = LogUtils.makeTag(LandingActivity.class);
    private static final String BASE_URL = "http://www.shadal.kr:3000";
    private static final String TEMP_UPDATED_RESTAURANT_DATA = "containUpdatedData";
    private SharedPreferences spGlobalPref;
    private int entryActivity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        setSharedPreference();

        entryActivity = setEntryActivity();
        Log.i(TAG, "Successfully set entryActivity");

        //pause some second to show landing page
        SystemClock.sleep(5 * 100);
        switch (entryActivity) {
            case 0:
                Log.i(TAG, "to loginCampusSelect page");
                Intent campusSelectIntent = new Intent(LandingActivity.this,
                        LoginCampusSelectActivity.class);
                startActivity(campusSelectIntent);
                break;
            case 1:
                Intent mainIntent = new Intent(LandingActivity.this,
                        RootActivity.class);
                startActivity(mainIntent);
                break;
        }
    }

    private void setSharedPreference() {
        Preferences.getDeviceUuid(LandingActivity.this);
    }

    private int setEntryActivity() {
        Realm realm = Realm.getInstance(LandingActivity.this);
        RealmQuery<Campus> query = realm.where(Campus.class);
        RealmResults<Campus> campus = query.findAll();
        if (campus.size() == 1) {
            updateData(campus.get(0));
            return 1;
        } else {
            return 0;
        }
    }

    private void updateData(Campus campus) {
        updateCampusMetaData(campus);
        insertOrUpdateAllRestaurantInfo(campus);
    }

    private void updateCampusMetaData(Campus campus) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(new RetrofitConverter().createBasicConverter()))
                .setEndpoint(BASE_URL) // The base API endpoint.
                .build();

        CampusAPI campusAPI = restAdapter.create(CampusAPI.class);

        campusAPI.getCampusInfo(campus.getId(), new Callback<Campus>() {
            @Override
            public void success(Campus campus, Response response) {
                Realm realm = Realm.getInstance(LandingActivity.this);
                realm.beginTransaction();
                try {
                    RealmQuery<Campus> query = realm.where(Campus.class);
                    RealmResults<Campus> currentCampus = query.findAll();
                    currentCampus.clear();

                    //insert campus to realm
                    realm.copyToRealm(campus);

                    realm.commitTransaction();
                } catch (Exception e) {
                    realm.cancelTransaction();
                } finally {
                    realm.close();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, error.toString());
                error.printStackTrace();
            }
        });
    }

    private void insertOrUpdateAllRestaurantInfo(Campus campus) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(new RetrofitConverter().createRestaurantConverter()))
                .setEndpoint(BASE_URL) // The base API endpoint.
                .build();

        RestaurantAPI restaurantAPI = restAdapter.create(RestaurantAPI.class);

        restaurantAPI.getAllRestaurantList(campus.getId(), new Callback<List<Category>>() {
            @Override
            public void success(List<Category> categories, Response response) {
                Realm realm = Realm.getInstance(LandingActivity.this);
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
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, error.toString());
                error.printStackTrace();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
