package com.lchpatners.shadal.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lchpatners.shadal.R;
import com.lchpatners.shadal.RootActivity;
import com.lchpatners.shadal.campus.Campus;
import com.lchpatners.shadal.util.LogUtils;

import io.realm.Realm;
import io.realm.RealmQuery;

public class CampusChangeActivity extends ActionBarActivity {

    private static final String TAG = LogUtils.makeTag(CampusChangeActivity.class);

    private Toolbar mToolbar;
    private String mTitle;
    private ListView mCampusListView;
    private Button mConfirmButton;
    private TextView tvCurrentCampus;

    private CampusChangeController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_change);

        mController = new CampusChangeController(CampusChangeActivity.this);
        mController.drawCampusList();

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mTitle = getString(R.string.change_campus);
        mToolbar.setTitle(mTitle);
        setSupportActionBar(mToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        tvCurrentCampus = (TextView) findViewById(R.id.campus);
        tvCurrentCampus.setText(getCampusName());

        mCampusListView = (ListView) findViewById(R.id.campusSelect_campusList);
        LayoutInflater inflater = getLayoutInflater();
        FrameLayout listFooterView = (FrameLayout) inflater.inflate(
                R.layout.list_footer, null);
        mCampusListView.addFooterView(listFooterView);

        mConfirmButton = (Button) findViewById(R.id.campusSelect_confirmButton);
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mController.isCampusSelected()) {
                    mController.setCampus();
                    Intent intent = new Intent(CampusChangeActivity.this, RootActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String getCampusName() {
        String campusName = "";
        Realm realm = Realm.getInstance(CampusChangeActivity.this);
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
