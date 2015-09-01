package com.lchpatners.shadal.setting;

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
import android.widget.Toast;

import com.lchpatners.shadal.R;
import com.lchpatners.shadal.campus.CampusController;
import com.lchpatners.shadal.util.LogUtils;

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
        tvCurrentCampus.setText(CampusController.getCurrentCampus(CampusChangeActivity.this).getName());

        mCampusListView = (ListView) findViewById(R.id.campusList);
        LayoutInflater inflater = getLayoutInflater();
        FrameLayout listFooterView = (FrameLayout) inflater.inflate(
                R.layout.list_footer, null);
        mCampusListView.addFooterView(listFooterView);

        mConfirmButton = (Button) findViewById(R.id.campusSelect_confirmButton);
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mController.isCampusSelected()) {
                    Toast.makeText(CampusChangeActivity.this, "음식점 정보를 받아옵니다", Toast.LENGTH_LONG).show();
                    mController.setCampus();
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
}
