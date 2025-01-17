package com.lchpatners.shadal.restaurant_suggestion;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lchpatners.shadal.R;
import com.lchpatners.shadal.campus.CampusController;
import com.lchpatners.shadal.dao.RestaurantSuggestion;
import com.lchpatners.shadal.util.AnalyticsHelper;

import java.io.ByteArrayOutputStream;


public class RSbyUserActivity extends ActionBarActivity {


    private static final String CAMPUS_NAME = "campus_name";
    private static final String CAMPUS_ID = "campus_id";
    private static final int SELECTED_PICTURE_1 = 1;
    private static final int SELECTED_PICTURE_2 = 2;
    private static final int SELECTED_PICTURE_3 = 3;
    private static final int SELECTED_PICTURE_4 = 4;

    private static final int SELECTED_CAMPUS = 0;

    private ImageView iv_1, iv_2, iv_3, iv_4;
    private ImageView iv_delete_1, iv_delete_2, iv_delete_3, iv_delete_4;
    private boolean delete[];
    private TextView campusName;
    private EditText restaurantName, restaurantPhoneNumber;
    private String[] files = new String[4];

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.image_1:
                    if (!delete[0]) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, SELECTED_PICTURE_1);
                    } else {
                        iv_1.setImageResource(R.drawable.icon_add_plus);
                        iv_delete_1.setVisibility(View.INVISIBLE);
                        files[0] = "";
                    }
                    delete[0] = !delete[0];
                    break;
                case R.id.image_2:
                    if (!delete[1]) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, SELECTED_PICTURE_2);
                    } else {
                        iv_2.setImageResource(R.drawable.icon_add_plus);
                        iv_delete_2.setVisibility(View.INVISIBLE);
                        files[1] = "";
                    }
                    delete[1] = !delete[1];
                    break;
                case R.id.image_3:
                    if (!delete[2]) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, SELECTED_PICTURE_3);
                    } else {
                        iv_3.setImageResource(R.drawable.icon_add_plus);
                        iv_delete_3.setVisibility(View.INVISIBLE);
                        files[2] = "";
                    }
                    delete[2] = !delete[2];
                    break;
                case R.id.image_4:
                    if (!delete[3]) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, SELECTED_PICTURE_4);
                    } else {
                        iv_4.setImageResource(R.drawable.icon_add_plus);
                        iv_delete_4.setVisibility(View.INVISIBLE);
                        files[3] = "";
                    }
                    delete[3] = !delete[3];
                    break;
            }

        }
    };
    private int campusId;
    private Button suggestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsby_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        String title = getString(R.string.title_activity_rsby_user);

        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        iv_1 = (ImageView) findViewById(R.id.image_1);
        iv_2 = (ImageView) findViewById(R.id.image_2);
        iv_3 = (ImageView) findViewById(R.id.image_3);
        iv_4 = (ImageView) findViewById(R.id.image_4);
        iv_delete_1 = (ImageView) findViewById(R.id.icon_delete_1);
        iv_delete_2 = (ImageView) findViewById(R.id.icon_delete_2);
        iv_delete_3 = (ImageView) findViewById(R.id.icon_delete_3);
        iv_delete_4 = (ImageView) findViewById(R.id.icon_delete_4);
        campusName = (TextView) findViewById(R.id.campus_name);
        restaurantName = (EditText) findViewById(R.id.restaurant_name);
        restaurantPhoneNumber = (EditText) findViewById(R.id.restaurant_phone_number);

        campusId = CampusController.getCurrentCampus(RSbyUserActivity.this).getId();
        campusName.setText(CampusController.getCurrentCampus(RSbyUserActivity.this).getName());
        campusName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RSbyUserActivity.this, CampusSelectionActivity.class);
                startActivityForResult(intent, SELECTED_CAMPUS);

                AnalyticsHelper analyticsHelper = new AnalyticsHelper(RSbyUserActivity.this);
                analyticsHelper.sendEvent("UX", "select_campus_in_restaurant_suggestion", CampusController.getCurrentCampus(RSbyUserActivity.this).getName_kor_short());
            }
        });

        delete = new boolean[4];
        for (int i = 0; i < delete.length; i++) {
            delete[i] = false;
        }

        iv_1.setOnClickListener(onClickListener);
        iv_2.setOnClickListener(onClickListener);
        iv_3.setOnClickListener(onClickListener);
        iv_4.setOnClickListener(onClickListener);


        suggestion = (Button) findViewById(R.id.button);
        suggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (restaurantName.getText().length() <= 0) {
                    Toast.makeText(RSbyUserActivity.this, "음식점 이름을 입력해주세요", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RSbyUserActivity.this, "잠시만 기다려주세요!", Toast.LENGTH_LONG).show();
                    sendRestaurantSuggestion();
                }
            }
        });

    }

    private void sendRestaurantSuggestion() {

        RestaurantSuggestion restaurantSuggestion = new RestaurantSuggestion();
        restaurantSuggestion.setName(restaurantName.getText().toString());
        restaurantSuggestion.setCampus_id(Integer.valueOf(campusId));
        restaurantSuggestion.setIs_suggested_by_restaurant(0);
        restaurantSuggestion.setFiles(files);
        restaurantSuggestion.setPhone_number(restaurantPhoneNumber.getText().toString());
        RestaurantSuggestionController.sendRestaurantSuggestion(RSbyUserActivity.this, restaurantSuggestion);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECTED_CAMPUS) {
                campusName.setText(data.getStringExtra(CAMPUS_NAME));
                campusId = data.getIntExtra(CAMPUS_ID, 0);
            } else {
                Uri uri = data.getData();
                String[] projection = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(projection[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                Bitmap selectedImage = BitmapFactory.decodeFile(filePath, options);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP);

                switch (requestCode) {
                    case SELECTED_PICTURE_1:
                        iv_1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        iv_1.setImageBitmap(selectedImage);
                        iv_delete_1.setVisibility(View.VISIBLE);
                        files[0] = encodedImage;
                        break;
                    case SELECTED_PICTURE_2:
                        iv_2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        iv_2.setImageBitmap(selectedImage);
                        iv_delete_2.setVisibility(View.VISIBLE);
                        files[1] = encodedImage;
                        break;
                    case SELECTED_PICTURE_3:
                        iv_3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        iv_3.setImageBitmap(selectedImage);
                        iv_delete_3.setVisibility(View.VISIBLE);
                        files[2] = encodedImage;
                        break;
                    case SELECTED_PICTURE_4:
                        iv_4.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        iv_4.setImageBitmap(selectedImage);
                        iv_delete_4.setVisibility(View.VISIBLE);
                        files[3] = encodedImage;
                        break;
                }

            }
        }

        AnalyticsHelper analyticsHelper = new AnalyticsHelper(RSbyUserActivity.this);
        analyticsHelper.sendScreen("음식점제보 화면");
    }
//
//    @Override
//    protected void onDestroy() {
//        Drawable d_1 = iv_1.getDrawable();
//        Drawable d_2 = iv_2.getDrawable();
//        Drawable d_3 = iv_3.getDrawable();
//        Drawable d_4 = iv_4.getDrawable();
//        if (d_1 instanceof BitmapDrawable) {
//            Bitmap b_1 = ((BitmapDrawable)d_1).getBitmap();
//            b_1.recycle();
//            b_1 = null;
//        }
//        if (d_2 instanceof BitmapDrawable) {
//            Bitmap b_2 = ((BitmapDrawable)d_2).getBitmap();
//            b_2.recycle();
//            b_2 = null;
//        }
//        if (d_3 instanceof BitmapDrawable) {
//            Bitmap b_3 = ((BitmapDrawable)d_3).getBitmap();
//            b_3.recycle();
//            b_3 = null;
//        }
//        if (d_4 instanceof BitmapDrawable) {
//            Bitmap b_4 = ((BitmapDrawable)d_4).getBitmap();
//            b_4.recycle();
//            b_4 = null;
//        }
//        d_1.setCallback(null);
//        d_2.setCallback(null);
//        d_3.setCallback(null);
//        d_4.setCallback(null);
//
//        super.onDestroy();
//    }
}
