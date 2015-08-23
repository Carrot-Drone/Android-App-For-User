package com.lchpatners.shadal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class RestaurantCorrectionActivity extends ActionBarActivity {
    static final String TAG = RestaurantCorrectionActivity.class.getSimpleName();
    EditText editText;
    TextView[] textView;
    ImageView[] checkBox;
    Button button;
    Boolean[] checked;
    int restaurantId;
    View.OnClickListener clickListener = new View.OnClickListener() {
        int position;

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.tv_1 || v.getId() == R.id.checkbox_1) {
                position = 0;
            } else if (v.getId() == R.id.tv_2 || v.getId() == R.id.checkbox_2) {
                position = 1;
            } else if (v.getId() == R.id.tv_3 || v.getId() == R.id.checkbox_3) {
                position = 2;
            } else if (v.getId() == R.id.tv_4 || v.getId() == R.id.checkbox_4) {
                position = 3;
            } else if (v.getId() == R.id.tv_5 || v.getId() == R.id.checkbox_5) {
                position = 4;
            } else if (v.getId() == R.id.et_detail) {
                editText.setFocusable(true);
                position = -1;
            } else if (v.getId() == R.id.button) {
                position = -1;
                String majorCorrection = "";
                String details = "";
                for (int i = 0; i < 5; i++) {
                    if (checked[i] == true) {
                        majorCorrection += textView[i].getText() + " ";
                    }
                }
                details = editText.getText().toString();
                Log.i(TAG, majorCorrection + "," + details);

                finish();
            }
            if ((position >= 0) && (position <= 4)) {
                if (checked[position] == false) {
                    checked[position] = !checked[position];
                    checkBox[position].setImageResource(R.drawable.icon_list_check_box_selected);
                } else {
                    checked[position] = !checked[position];
                    checkBox[position].setImageResource(R.drawable.icon_list_check_box_normal);
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_correction);

        Intent intent = getIntent();
        restaurantId = intent.getExtras().getInt("restaurant_id");

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        String title = getString(R.string.title_activity_restaurant_correction);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editText = (EditText) findViewById(R.id.et_detail);
        button = (Button) findViewById(R.id.button);

        textView = new TextView[5];
        textView[0] = (TextView) findViewById(R.id.tv_1);
        textView[1] = (TextView) findViewById(R.id.tv_2);
        textView[2] = (TextView) findViewById(R.id.tv_3);
        textView[3] = (TextView) findViewById(R.id.tv_4);
        textView[4] = (TextView) findViewById(R.id.tv_5);

        checkBox = new ImageView[5];
        checkBox[0] = (ImageView) findViewById(R.id.checkbox_1);
        checkBox[1] = (ImageView) findViewById(R.id.checkbox_2);
        checkBox[2] = (ImageView) findViewById(R.id.checkbox_3);
        checkBox[3] = (ImageView) findViewById(R.id.checkbox_4);
        checkBox[4] = (ImageView) findViewById(R.id.checkbox_5);

        checked = new Boolean[5];

        for (int i = 0; i < 5; i++) {
            textView[i].setOnClickListener(clickListener);
            checkBox[i].setOnClickListener(clickListener);
            checked[i] = false;
        }
        editText.setOnClickListener(clickListener);
        button.setOnClickListener(clickListener);


    }
}
