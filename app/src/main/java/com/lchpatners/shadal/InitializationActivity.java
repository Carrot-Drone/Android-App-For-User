package com.lchpatners.shadal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class InitializationActivity extends Activity {

    static Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialization);

        Button selectCampus = (Button)findViewById(R.id.select_campus);
        selectCampus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InitializationActivity.this, CampusSelectionActivity.class));
            }
        });
        confirm = (Button)findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InitializationActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
