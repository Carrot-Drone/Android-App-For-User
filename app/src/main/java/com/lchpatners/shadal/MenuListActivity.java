package com.lchpatners.shadal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;


public class MenuListActivity extends ActionBarActivity {

    private Restaurant restaurant;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);

        Intent intent = getIntent();
        restaurant = intent.getParcelableExtra("RESTAURANT");

        setView();

        if (intent.getStringExtra("REFERRER") != null &&
                intent.getStringExtra("REFERRER").equals("RandomFragment")) {
            View button = findViewById(R.id.random_button);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseHelper helper = DatabaseHelper.getInstance(MenuListActivity.this);
                    restaurant = helper.getRandomRestaurant();
                    setView();
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu_list, menu);

        this.menu = menu;

        DatabaseHelper helper = DatabaseHelper.getInstance(this);
        setMenuItemChecked(menu.findItem(R.id.bookmark),
                helper.getRestaurantFromId(restaurant.getId()).isFavorite());

        MenuItem flyer = menu.findItem(R.id.see_flyer);
        flyer.setVisible(restaurant.hasFlyer());
        flyer.setIcon(resizeDrawable(flyer.getIcon(), 0.8f));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.bookmark) {
            DatabaseHelper helper = DatabaseHelper.getInstance(this);
            helper.toggleFavoriteById(restaurant.getId());
            boolean bookmarked = helper.getRestaurantFromId(restaurant.getId()).isFavorite();
            setMenuItemChecked(item, bookmarked);
            return true;
        } else if (id == R.id.see_flyer) {
            DatabaseHelper helper = DatabaseHelper.getInstance(this);
            ArrayList<String> urls = helper.getFlyerUrlsByRestaurantServerId(restaurant.getServerId());
            Intent intent = new Intent(this, FlyerActivity.class);
            intent.putExtra("URLS", urls);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setView() {
        if (restaurant != null) {
            DatabaseHelper helper = DatabaseHelper.getInstance(this);
            restaurant = helper.getRestaurantFromId(restaurant.getId());

            getSupportActionBar().setTitle(restaurant.getName());

            Server server = new Server((this), Server.GWANAK);
            server.updateRestaurant(restaurant.getServerId(), restaurant.getUpdatedTime(), this);

            if (menu != null) {
                setMenuItemChecked(menu.findItem(R.id.bookmark), restaurant.isFavorite());
                menu.findItem(R.id.see_flyer).setVisible(restaurant.hasFlyer());
            }

            ListView listView = (ListView)findViewById(R.id.menu_list);
            MenuListAdapter adapter = new MenuListAdapter(this, restaurant);
            listView.setAdapter(adapter);

            TextView phoneNumber = (TextView)findViewById(R.id.phone_number);
            phoneNumber.setText(restaurant.getPhoneNumber());
            phoneNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new CallLoggingTask().execute();
                    String number = "tel:" + restaurant.getPhoneNumber();
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(number));
                    startActivity(intent);
                }
            });

            TextView openTime = (TextView)findViewById(R.id.open_time);
            openTime.setText(hourFormatString(restaurant));

            TextView couponString = (TextView)findViewById(R.id.coupon_string);
            if (restaurant.hasCoupon()) {
                couponString.setText(restaurant.getCouponString());
            } else {
                couponString.setVisibility(View.GONE);
            }
        }
    }

    public void setMenuItemChecked(MenuItem item, boolean checked) {
        Drawable drawable = getResources().getDrawable(R.drawable.actionbar_star);
        drawable = resizeDrawable(drawable, 0.8f);
        if (checked) {
            drawable.mutate().setColorFilter(Color.YELLOW, PorterDuff.Mode.MULTIPLY);
        }
        item.setIcon(drawable);
    }

    public Drawable resizeDrawable(Drawable drawable, final float ratio) {
        int x = drawable.getIntrinsicWidth(), y = drawable.getIntrinsicHeight();
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        drawable = new BitmapDrawable(getResources(),
                Bitmap.createScaledBitmap(bitmap, (int)(x * ratio), (int)(y * ratio), true));
        return drawable;
    }

    public String hourFormatString(Restaurant restaurant) {
        String open = restaurant.getOpeningHour();
        int hour = Integer.parseInt(open.split("[.]")[0]);
        try {
            hour += "0".equals(open.split("[.]")[1]) ? 0 : 1;
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e("MLA", e.getClass().getName());
        }
        open = String.format("%02d:%02d", hour, 0);

        String close = restaurant.getClosingHour();
        hour = Integer.parseInt(close.split("[.]")[0]);
        try {
            hour += "0".equals(close.split("[.]")[1]) ? 0 : 1;
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e("MLA", e.getClass().getName());
        }
        close = String.format("%02d:%02d", hour, 0);

        return getString(R.string.open_time) + ": " + open + " ~ " + close;
    }

    private class CallLoggingTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://shadal.kr/new_call");
                ArrayList<BasicNameValuePair> value = new ArrayList<>();
                value.add(new BasicNameValuePair("phoneNumber", restaurant.getPhoneNumber()));
                value.add(new BasicNameValuePair("name", restaurant.getName()));
                value.add(new BasicNameValuePair("device", "android"));
                value.add(new BasicNameValuePair("campus", Server.GWANAK));
                value.add(new BasicNameValuePair("server_id", Integer.toString(restaurant.getServerId())));
                post.setEntity(new UrlEncodedFormEntity(value, "UTF-8"));
                client.execute(post);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
