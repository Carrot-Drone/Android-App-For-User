package com.lchpatners.shadal;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class RSbyOwnerActivity extends ActionBarActivity {

    private static final int SELECTED_PICTURE = 1;
    private static final int SELECTED_CAMPUS = 2;

    private ImageView iv_1, iv_2, iv_3;
    private int count = 0;
    ;
    private TextView campusName;
    private EditText restaurantName, restaurantPhoneNumber, officeHours;
    private String[] files = new String[3];
    private String campusId;
    private Button suggestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsby_owner);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        String title = getString(R.string.title_activity_rsby_owner);

        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        iv_1 = (ImageView) findViewById(R.id.image_1);
        iv_2 = (ImageView) findViewById(R.id.image_2);
        iv_3 = (ImageView) findViewById(R.id.image_3);
        campusName = (TextView) findViewById(R.id.campus_name);
        restaurantName = (EditText) findViewById(R.id.restaurant_name);
        restaurantPhoneNumber = (EditText) findViewById(R.id.restaurant_phone_number);
        officeHours = (EditText) findViewById(R.id.office_hours);

        campusName.setText(Preferences.getCampusKoreanName(this));
        campusId = Preferences.getCampusId(this);

        campusName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RSbyOwnerActivity.this, CampusSelectionActivity.class);
                intent.putExtra("from", "RSbyOwnerActivity");
                startActivityForResult(intent, SELECTED_CAMPUS);
            }
        });


        TextView addFlyer = (TextView) findViewById(R.id.add_flyer);
        addFlyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECTED_PICTURE);
            }
        });

        suggestion = (Button) findViewById(R.id.button);
        suggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Server server = new Server(RSbyOwnerActivity.this);
                ArrayList<BasicNameValuePair> value = new ArrayList<>();
                value.add(new BasicNameValuePair("uuid", Preferences.getDeviceUuid(RSbyOwnerActivity.this)));
                value.add(new BasicNameValuePair("campus_id", campusId));
                value.add(new BasicNameValuePair("name", String.valueOf(restaurantName.getText())));
                value.add(new BasicNameValuePair("phone_number", String.valueOf(restaurantPhoneNumber.getText())));
                value.add(new BasicNameValuePair("is_suggested_by_restaurant", String.valueOf(1)));
                value.add(new BasicNameValuePair("office_hours", String.valueOf(officeHours.getText())));
                value.add(new BasicNameValuePair("files", String.valueOf(files)));
                server.sendRestaurantSuggestion(value);
                //문의해주셔서감사합니다.
                finish();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECTED_PICTURE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String[] projection = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(uri, projection, null
                            , null, null);

                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
                    Bitmap selectedImage = BitmapFactory.decodeFile(filePath);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                    if (count == 1) {
                        iv_1.setImageBitmap(selectedImage);
                        files[0] = encodedImage;
//                        iv_1.setImageURI(uri);
                    } else if (count == 2) {
                        iv_2.setImageBitmap(selectedImage);
                        files[1] = encodedImage;
                    } else if (count == 3) {
                        iv_3.setImageBitmap(selectedImage);
                        files[2] = encodedImage;
                    }

                }
                Log.d("RSbyOwnerActivity", files[0] + "," + files[1] + "," + files[2]);
                break;
            case SELECTED_CAMPUS:
                if (resultCode == RESULT_OK) {
                    campusId = data.getStringExtra("id");
                    campusName.setText(data.getStringExtra("name"));
                }
                break;
        }
    }


}
