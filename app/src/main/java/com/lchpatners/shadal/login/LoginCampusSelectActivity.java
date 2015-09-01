package com.lchpatners.shadal.login;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.lchpatners.shadal.R;
import com.lchpatners.shadal.setting.CampusChangeController;
import com.lchpatners.shadal.util.LogUtils;

/**
 * Created by youngkim on 2015. 8. 20..
 */
public class LoginCampusSelectActivity extends ActionBarActivity {
    private static final String TAG = LogUtils.makeTag(LoginCampusSelectActivity.class);

    private Toolbar tbNavbar;
    private ListView lvCampusList;
    private Button buConfirmSelection;
    private CampusChangeController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_campus_select);

        controller = new CampusChangeController(LoginCampusSelectActivity.this);
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
        buConfirmSelection.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (controller.isCampusSelected()) {

                            controller.setCampus();
                            //Go to RootActivity at setCampus function
//                    Intent intent = new Intent(LoginCampusSelectActivity.this, RootActivity.class);
//                    startActivity(intent);


                        }
                    }
                }
        );
    }
}
