package com.lchpatners.shadal.restaurant_suggestion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.lchpatners.shadal.R;
import com.lchpatners.shadal.campus.Campus;
import com.lchpatners.shadal.setting.CampusChangeController;
import com.lchpatners.shadal.util.AnalyticsHelper;
import com.lchpatners.shadal.util.LogUtils;


/**
 * Created by eunhyeKim on 2015. 8. 31..
 */
public class CampusSelectionActivity extends ActionBarActivity {
    private static final String TAG = LogUtils.makeTag(CampusSelectionActivity.class);
    private static final String CAMPUS_NAME = "campus_name";
    private static final String CAMPUS_ID = "campus_id";
    private Toolbar tbNavbar;
    private ListView lvCampusList;
    private Button buConfirmSelection;
    private CampusChangeController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_campus_select);

        controller = new CampusChangeController(CampusSelectionActivity.this);
        controller.drawCampusList();

        tbNavbar = (Toolbar) findViewById(R.id.LoginCampusSelect_toolBar);
        tbNavbar.setTitle("캠퍼스 선택하기");
        setSupportActionBar(tbNavbar);

        lvCampusList = (ListView) findViewById(R.id.campusList);
        LayoutInflater inflater = getLayoutInflater();
        FrameLayout listFooterView = (FrameLayout) inflater.inflate(
                R.layout.list_footer, null);
        lvCampusList.addFooterView(listFooterView);

        buConfirmSelection = (Button) findViewById(R.id.LoginCampusSelect_confirmButton);
        buConfirmSelection.setText("선택하기");
        buConfirmSelection.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (controller.isCampusSelected()) {
                            Intent intent = new Intent();
                            Campus campus = controller.getCampus();
                            intent.putExtra(CAMPUS_NAME, campus.getName());
                            intent.putExtra(CAMPUS_ID, campus.getId());
                            setResult(RESULT_OK, intent);
                            finish();

                            AnalyticsHelper analyticsHelper = new AnalyticsHelper(CampusSelectionActivity.this);
                            analyticsHelper.sendEvent("UX", "select_campus_in_restaurant_suggestion_start", campus.getName_kor_short());
                        }


                    }
                }
        );


    }
}
