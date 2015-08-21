package com.lchpatners.shadal;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


public class RequestActivity extends ActionBarActivity {
    private static final String TAG = RequestActivity.class.getSimpleName();
    private EditText email;
    private EditText detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);


        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        String title = getString(R.string.title_activity_request);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        email = (EditText) findViewById(R.id.et_email);
        detail = (EditText) findViewById(R.id.et_detail);

        Toolbar bottomBar = (Toolbar) findViewById(R.id.bottom_bar);
        bottomBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String details = detail.getText().toString();
                Log.i(TAG, String.valueOf(email.getEditableText()) + "," + details);
                finish();
            }
        });

    }

}
