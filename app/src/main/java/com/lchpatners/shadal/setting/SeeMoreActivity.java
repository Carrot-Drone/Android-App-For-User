package com.lchpatners.shadal.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.lchpatners.shadal.R;
import com.lchpatners.shadal.campus.Campus;
import com.lchpatners.shadal.campus.CampusController;
import com.lchpatners.shadal.request.RequestActivity;
import com.lchpatners.shadal.util.AnalyticsHelper;
import com.lchpatners.shadal.util.LogUtils;

import io.realm.Realm;
import io.realm.RealmQuery;


public class SeeMoreActivity extends ActionBarActivity {
    private static final String TAG = LogUtils.makeTag(SeeMoreActivity.class);
    Toolbar toolbar;
    String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_more);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        mTitle = getString(R.string.see_more);
        toolbar.setTitle(mTitle);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        TextView campus = (TextView) findViewById(R.id.campus);
        campus.setText(getCampusName());
        TextView changeCampus = (TextView) findViewById(R.id.change_campus);
        changeCampus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SeeMoreActivity.this, CampusChangeActivity.class);
                startActivity(intent);

                AnalyticsHelper analyticsHelper = new AnalyticsHelper(SeeMoreActivity.this);
                analyticsHelper.sendEvent("UX", "select_campus_in_about", CampusController.getCurrentCampus(SeeMoreActivity.this).getName_kor_short());
            }
        });

        TextView request = (TextView) findViewById(R.id.request_of_camdal);
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SeeMoreActivity.this, RequestActivity.class);
                startActivity(intent);

                AnalyticsHelper analyticsHelper = new AnalyticsHelper(SeeMoreActivity.this);
                analyticsHelper.sendEvent("UX", "user_request", CampusController.getCurrentCampus(SeeMoreActivity.this).getName_kor_short());
            }
        });

        AnalyticsHelper analyticsHelper = new AnalyticsHelper(SeeMoreActivity.this);
        analyticsHelper.sendScreen("추천 화면");
    }

    private String getCampusName() {
        String campusName = "";
        Realm realm = Realm.getInstance(SeeMoreActivity.this);
        try {

            realm.beginTransaction();
            RealmQuery<Campus> query = realm.where(Campus.class);
            Campus currentCampus = query.findFirst();
            campusName = currentCampus.getName();

            realm.commitTransaction();
        } catch (Exception e) {
            realm.cancelTransaction();
        } finally {
            realm.close();
        }
        return campusName;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == android.R.id.home) {
            setResult(RESULT_OK);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
        super.onBackPressed();
    }
}
