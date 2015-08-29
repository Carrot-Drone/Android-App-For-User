package com.lchpatners.shadal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.lchpatners.shadal.campus.Campus;
import com.lchpatners.shadal.campus.CampusAPI;
import com.lchpatners.shadal.campus.CampusController;
import com.lchpatners.shadal.login.LoginCampusSelectActivity;
import com.lchpatners.shadal.restaurant.RestaurantAPI;
import com.lchpatners.shadal.restaurant.RestaurantController;
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
    private Campus mCampus;
    private static final String BASE_URL = "http://www.shadal.kr:3000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        setSharedPreference();

        switch (setEntryActivity()) {
            case 0:
                Log.i(TAG, "to loginCampusSelect page");
                //pause some second to show landing page
                SystemClock.sleep(5 * 100);
                Intent campusSelectIntent = new Intent(LandingActivity.this,
                        LoginCampusSelectActivity.class);
                startActivity(campusSelectIntent);
                break;
            case 1:
                Log.i(TAG, "to RootActivity page");
//                Intent intent = new Intent(LandingActivity.this, RootActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                break;
        }
    }

    private void setSharedPreference() {
        Preferences.getDeviceUuid(LandingActivity.this);
    }

    private int setEntryActivity() {
        int entryActivity = 0;

        Realm realm = Realm.getInstance(LandingActivity.this);

        RealmQuery<Campus> query = realm.where(Campus.class);
        Campus campus = query.findFirst();
        if (campus != null) {
            updateData(campus);
            entryActivity = 1;
        }
        realm.close();

        return entryActivity;
    }

    private void updateData(Campus campus) {
        CampusController.updateCampusMetaData(LandingActivity.this, campus);
        RestaurantController.insertOrUpdateAllRestaurantInfo(LandingActivity.this, campus, RootActivity.class);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
