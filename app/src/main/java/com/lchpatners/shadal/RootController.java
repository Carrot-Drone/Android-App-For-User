package com.lchpatners.shadal;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.lchpatners.shadal.campus.Campus;
import com.lchpatners.shadal.campus.CampusAPI;
import com.lchpatners.shadal.restaurant.RestaurantAPI;
import com.lchpatners.shadal.restaurant.category.Category;
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
 * Created by YoungKim on 2015. 8. 24..
 */
public class RootController {
    private static final String TAG = LogUtils.makeTag(RootController.class);
    private static final String BASE_URL = "http://www.shadal.kr:3000";

    private Activity mActivity;
    private Campus mCampus;

    public RootController(Activity activity) {
        mActivity = activity;
        setCampus();
    }

    public static void showDownloadCompleteToast(Context context) {
        Toast.makeText(context, "음식점 정보 저장 완료!", Toast.LENGTH_SHORT).show();
    }

    private void setCampus() {
        Realm realm = Realm.getInstance(mActivity);
        RealmQuery<Campus> query = realm.where(Campus.class);
        Campus campus = query.findFirst();
        mCampus = campus;
    }

    public Campus getCampus() {
        return mCampus;
    }

    public void updateCampusMetaData() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(new RetrofitConverter().createBasicConverter()))
                .setEndpoint(BASE_URL) // The base API endpoint.
                .build();

        CampusAPI campusAPI = restAdapter.create(CampusAPI.class);

        campusAPI.getCampusInfo(mCampus.getId(), new Callback<Campus>() {
            @Override
            public void success(Campus campus, Response response) {
                Realm realm = Realm.getInstance(mActivity);

                realm.beginTransaction();

                RealmQuery<Campus> query = realm.where(Campus.class);
                RealmResults<Campus> currentCampus = query.findAll();
                currentCampus.clear();

                //insert campus to realm
                realm.copyToRealm(campus);
                realm.commitTransaction();

                realm.close();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, error.toString());
                error.printStackTrace();
            }
        });
    }

    public void insertOrUpdateAllRestaurantInfo() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(new RetrofitConverter().createRestaurantConverter()))
                .setEndpoint(BASE_URL) // The base API endpoint.
                .build();

        RestaurantAPI restaurantAPI = restAdapter.create(RestaurantAPI.class);

        restaurantAPI.getAllRestaurantList(mCampus.getId(), new Callback<List<Category>>() {
            @Override
            public void success(List<Category> categories, Response response) {
                Realm realm = Realm.getInstance(mActivity);
                try {
                    realm.beginTransaction();

                    realm.copyToRealmOrUpdate(categories);

                    realm.commitTransaction();
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
}
