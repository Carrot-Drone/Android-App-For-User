package com.lchpatners.shadal.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.lchpatners.shadal.R;
import com.lchpatners.shadal.RequestActivity;
import com.lchpatners.shadal.campus.Campus;
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

            }
        });

        TextView request = (TextView) findViewById(R.id.request_of_camdal);
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SeeMoreActivity.this, RequestActivity.class);
                startActivity(intent);
            }
        });
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
}
