package com.lchpatners.shadal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.lchpatners.shadal.campus.Campus;
import com.lchpatners.shadal.login.LoginCampusSelectActivity;
import com.lchpatners.shadal.util.LogUtils;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class LandingActivity extends ActionBarActivity {
    private static final String TAG = LogUtils.makeTag(LandingActivity.class);
    private SharedPreferences spGlobalPref;
    private int entryActivity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        spGlobalPref = getSharedPreferences("GlobalPref", MODE_PRIVATE);
        Log.i(TAG, "Successfully get/init preferences");

        entryActivity = setEntryActivity();
        Log.i(TAG, "Successfully set entryActivity");

        //pause some second to show landing page
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(5 * 100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
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
            }
        };
        timerThread.start();
    }

    private int setEntryActivity() {
        Realm realm = Realm.getInstance(LandingActivity.this);
        RealmQuery<Campus> query = realm.where(Campus.class);
        RealmResults<Campus> campus = query.findAll();
        if (campus.size() == 1) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
