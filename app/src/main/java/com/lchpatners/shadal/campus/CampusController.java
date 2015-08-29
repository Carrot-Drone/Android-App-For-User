package com.lchpatners.shadal.campus;

import android.app.Activity;
import android.util.Log;

import com.lchpatners.shadal.util.LogUtils;
import com.lchpatners.shadal.util.RetrofitConverter;

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
public class CampusController {
    private static final String TAG = LogUtils.makeTag(CampusController.class);
    private static final String BASE_URL = "http://www.shadal.kr:3000";

    public static Campus getCurrentCampus(Activity activity) {
        Realm realm = Realm.getInstance(activity);
        RealmQuery<Campus> query = realm.where(Campus.class);
        Campus campus = query.findFirst();
        return campus;
    }

    public static void updateCampusMetaData(final Activity activity, Campus campus) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(new RetrofitConverter().createBasicConverter()))
                .setEndpoint(BASE_URL) // The base API endpoint.
                .build();

        CampusAPI campusAPI = restAdapter.create(CampusAPI.class);

        campusAPI.getCampusInfo(campus.getId(), new Callback<Campus>() {
            @Override
            public void success(Campus campus, Response response) {
                Realm realm = Realm.getInstance(activity);
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
}
