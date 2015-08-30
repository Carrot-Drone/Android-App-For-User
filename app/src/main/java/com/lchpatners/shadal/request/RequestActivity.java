package com.lchpatners.shadal.request;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lchpatners.shadal.R;


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

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String details = detail.getText().toString();
                String email_address = email.getText().toString();
                RequestController.sendUserRequest(RequestActivity.this, email_address, details);
            }
        });

    }

}
