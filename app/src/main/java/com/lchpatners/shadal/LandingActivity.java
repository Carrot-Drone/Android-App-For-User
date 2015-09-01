package com.lchpatners.shadal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.lchpatners.shadal.campus.Campus;
import com.lchpatners.shadal.campus.CampusController;
import com.lchpatners.shadal.login.LoginCampusSelectActivity;
import com.lchpatners.shadal.restaurant.RestaurantController;
import com.lchpatners.shadal.restaurant.RestaurantInfoActivity;
import com.lchpatners.shadal.util.LogUtils;
import com.lchpatners.shadal.util.Preferences;
import com.urqa.clientinterface.URQAController;

import io.realm.Realm;
import io.realm.RealmQuery;

public class LandingActivity extends ActionBarActivity {
    public static final String KAKAO_RESTAURANT_ID = "kakao_restaurant_id";
    public static final String KAKAO_CAMPUS = "kakao_restaurant_in_campus";
    private static final String TAG = LogUtils.makeTag(LandingActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //add URQA Libs, initialization.
        //TODO : reveal comment
        URQAController.InitializeAndStartSession(getApplicationContext(), "63FB46FA");

        setContentView(R.layout.activity_landing);

        setSharedPreference();
        Uri uri = getIntent().getData();
        if (uri != null) {
            String campus_short_name = uri.getQueryParameter("campusName");
            String restaurant_id = uri.getQueryParameter("restaurant_id");

            Intent intent = new Intent(LandingActivity.this, RestaurantInfoActivity.class);
            intent.putExtra(KAKAO_RESTAURANT_ID, Integer.parseInt(restaurant_id));
            intent.putExtra(KAKAO_CAMPUS, campus_short_name);
            startActivity(intent);
        } else {
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
