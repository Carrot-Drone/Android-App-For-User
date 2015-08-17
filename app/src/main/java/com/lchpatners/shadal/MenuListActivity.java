package com.lchpatners.shadal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Displays a restaurant's detailed information.
 */
// This could have been named "RestaurantActivity".
// But as you can see, it looks better on the project structure this way.
// That's why. That's all. So you may just rename this class any time.
public class MenuListActivity extends ActionBarActivity {

    /**
     * {@link com.lchpatners.shadal.Restaurant Restaurant} of which information
     * is to be displayed.
     */
    private DatabaseHelper helper;
    private Restaurant restaurant;
    /**
     * {@link android.view.Menu} of this {@link android.support.v7.app.ActionBarActivity
     * ActionBarActivity}.
     */
    // ALERT: this is android.view.Menu, not com.lchpartners.shadal.Menu
    private Menu menu;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        Intent intent = getIntent();
        restaurant = intent.getParcelableExtra("RESTAURANT");


        setView();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu_list, menu);

        this.menu = menu;

        MenuItem flyer = menu.findItem(R.id.see_flyer);
        flyer.setVisible(restaurant.hasFlyer());
        // The icon resource was kinda too big.
        // So it was resized programmatically.
        flyer.setIcon(resizeDrawable(flyer.getIcon(), 0.8f));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.see_flyer) {
            DatabaseHelper helper = DatabaseHelper.getInstance(this);
            ArrayList<String> urls = helper.getFlyerUrlsByRestaurantServerId(restaurant.getRestaurantId());
            Intent intent = new Intent(this, FlyerActivity.class);
            intent.putExtra(RestaurantListAdapter.URLS, urls);
            intent.putExtra(RestaurantListAdapter.RESTAURANT, restaurant);
            startActivity(intent);
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Set view of this {@link android.app.Activity Activity}.
     */
    public void setView() {
        if (restaurant != null) {
            Intent intent = getIntent();
            if (intent.getStringExtra("REFERRER") != null &&
                    intent.getStringExtra("REFERRER").equals("RandomFragment")) {
//                AnalyticsHelper helper = new AnalyticsHelper(getApplication());
//                helper.sendScreen("아무거나 화면");
            } else {
//                AnalyticsHelper helper = new AnalyticsHelper(getApplication());
//                helper.sendScreen("음식점 화면");
            }

            helper = DatabaseHelper.getInstance(this);
            restaurant = helper.getRestaurantFromId(restaurant.getRestaurantId());

            toolbar.setTitle(restaurant.getName());
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


            final Server server = new Server(this);
            server.updateRestaurant(restaurant.getRestaurantId(), restaurant.getUpdatedTime(), restaurant.getCategoryId(), this);
//
//            if (menu != null) {
//                //setMenuItemChecked(menu.findItem(R.id.bookmark), restaurant.isFavorite());
//                menu.findItem(R.id.see_flyer).setVisible(restaurant.hasFlyer());
//            }
            ImageButton flyer = (ImageButton) findViewById(R.id.flyer);
            flyer.setVisibility((restaurant.hasFlyer()) ? View.VISIBLE : View.INVISIBLE);
            flyer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> urls = helper.getFlyerUrlsByRestaurantServerId(restaurant.getRestaurantId());
                    Intent intent = new Intent(MenuListActivity.this, FlyerActivity.class);
                    intent.putExtra(RestaurantListAdapter.URLS, urls);
                    intent.putExtra(RestaurantListAdapter.RESTAURANT, restaurant);
                    startActivity(intent);
                }
            });


            final ListView listView = (ListView) findViewById(R.id.menu_list);

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View header = inflater.inflate(
                    R.layout.menu_header, null, false);

            TextView retention = (TextView) header.findViewById(R.id.retention);
            TextView numberOfMyCalls = (TextView) header.findViewById(R.id.number_of_my_calls);
            TextView totalNumberOfCalls = (TextView) header.findViewById(R.id.total_number_of_calls);
            TextView notice = (TextView) header.findViewById(R.id.notice);
            if (restaurant.getNotice() != null && restaurant.getNotice().length() > 0) {
                notice.setText(restaurant.getNotice());
            } else {
                notice.setVisibility(View.GONE);
            }

            retention.setText(Math.round(restaurant.getRetention() * 100) + "%");
            numberOfMyCalls.setText(restaurant.getNumberOfCalls(MenuListActivity.this, restaurant.getRestaurantId()) + "회");
            totalNumberOfCalls.setText(restaurant.getTotalNumberOfCalls() + "회");

            if (listView.getHeaderViewsCount() == 0) {
                listView.addHeaderView(header, null, false);
            }

            MenuListAdapter adapter = new MenuListAdapter(this, restaurant);
            listView.setAdapter(adapter);


            TextView phoneNumber = (TextView) findViewById(R.id.phone_number);
            phoneNumber.setText(restaurant.getPhoneNumber());
            phoneNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    AnalyticsHelper helper = new AnalyticsHelper(getApplication());
//                    helper.sendEvent("UX", "phonenumber_clicked", restaurant.getName());

                    Call.updateCallLog(MenuListActivity.this, restaurant);
                    String number = "tel:" + restaurant.getPhoneNumber();
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                    startActivity(intent);
                }
            });

            TextView officeHours = (TextView) findViewById(R.id.office_hours);
            officeHours.setText(hourFormatString(restaurant));

            TextView minimum = (TextView) findViewById(R.id.minimum);
            TextView labelMinimum = (TextView) findViewById(R.id.label_minimum);
            if (restaurant.getMinimumPrice() != 0) {
                minimum.setText(restaurant.getMinimumPrice() + "원");
            } else {
                minimum.setVisibility(View.GONE);
                labelMinimum.setVisibility(View.GONE);

            }


        }
    }

    /**
     * Resize a {@link android.graphics.drawable.Drawable Drawable}.
     *
     * @param drawable The {@link android.graphics.drawable.Drawable Drawable} to be resized.
     * @param ratio    Size ratio.
     * @return The resized {@link android.graphics.drawable.Drawable Drawable}.
     */
    public Drawable resizeDrawable(Drawable drawable, final float ratio) {
        int x = drawable.getIntrinsicWidth(), y = drawable.getIntrinsicHeight();
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        drawable = new BitmapDrawable(getResources(),
                Bitmap.createScaledBitmap(bitmap, (int) (x * ratio), (int) (y * ratio), true));
        return drawable;
    }

    /**
     * Return a formatted {@link java.lang.String String}
     * which tells open hours of a restaurant.
     *
     * @param restaurant {@link com.lchpatners.shadal.Restaurant Restaurant}
     *                   with {@link com.lchpatners.shadal.Restaurant#openingHour openingHour}
     *                   and {@link com.lchpatners.shadal.Restaurant#closingHour closingHour}.
     * @return Well-formed {@link java.lang.String String}.
     */
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

        return open + " ~ " + close;
    }


}
