package com.lchpatners.shadal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
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

        // If shown up by the RandomFragment, set up the dice button.
        if (intent.getStringExtra("REFERRER") != null &&
                intent.getStringExtra("REFERRER").equals("RandomFragment")) {
            View button = findViewById(R.id.random_button);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RotateAnimation anim =
                            new RotateAnimation(0, 360,
                                    Animation.RELATIVE_TO_SELF, 0.5f,
                                    Animation.RELATIVE_TO_SELF, 0.5f);
                    anim.setDuration(500);
                    v.startAnimation(anim);
                    DatabaseHelper helper = DatabaseHelper.getInstance(MenuListActivity.this);
                    restaurant = helper.getRandomRestaurant();
                    setView();

                    AnalyticsHelper aHelper = new AnalyticsHelper(getApplication());
                    aHelper.sendEvent("UX", "random_res_clicked", restaurant.getName());
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
        // The icon resource was kinda too big.
        // So it was resized programmatically.
        flyer.setIcon(resizeDrawable(flyer.getIcon(), 0.8f));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.bookmark) {
            DatabaseHelper helper = DatabaseHelper.getInstance(this);
            boolean bookmarked = helper.toggleFavoriteById(restaurant.getId());
            setMenuItemChecked(item, bookmarked);

            AnalyticsHelper aHelper = new AnalyticsHelper(getApplication());
            aHelper.sendEvent("UX", bookmarked ? "favorite_button_clicked" :
                    "favorite_button_disclicked", restaurant.getName());
            return true;
        } else if (id == R.id.see_flyer) {
            DatabaseHelper helper = DatabaseHelper.getInstance(this);
            ArrayList<String> urls = helper.getFlyerUrlsByRestaurantServerId(restaurant.getServerId());
            Intent intent = new Intent(this, FlyerActivity.class);
            intent.putExtra("URLS", urls);
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

            DatabaseHelper helper = DatabaseHelper.getInstance(this);
            restaurant = helper.getRestaurantFromId(restaurant.getId());

            toolbar.setTitle(restaurant.getName());
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


            final Server server = new Server(this);
            server.updateRestaurant(restaurant.getServerId(), restaurant.getUpdatedTime(), this);

            if (menu != null) {
                //setMenuItemChecked(menu.findItem(R.id.bookmark), restaurant.isFavorite());
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
//                    AnalyticsHelper helper = new AnalyticsHelper(getApplication());
//                    helper.sendEvent("UX", "phonenumber_clicked", restaurant.getName());

                    DatabaseHelper DBhelper = DatabaseHelper.getInstance(MenuListActivity.this);
                    DBhelper.insertRecentCalls(restaurant.getId());

                    server.sendCallLog(restaurant);
                    String number = "tel:" + restaurant.getPhoneNumber();
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                    startActivity(intent);
                }
            });

            TextView openTime = (TextView)findViewById(R.id.open_time);
            openTime.setText(hourFormatString(restaurant));

            TextView couponString = (TextView)findViewById(R.id.coupon_string);
            if (restaurant.getCouponString() != null && restaurant.getCouponString().length() > 0) {
                couponString.setText(restaurant.getCouponString());
            } else {
                couponString.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Check or uncheck {@link android.view.MenuItem MenuItem}.
     * @param item The "bookmark" {@link android.view.MenuItem MenuItem}.
     * @param checked Is the icon is to be checked?
     */
    public void setMenuItemChecked(MenuItem item, boolean checked) {
        Drawable drawable = getResources().getDrawable(R.drawable.ic_action_star);
        drawable = resizeDrawable(drawable, 0.8f);
        if (checked) {
            drawable.mutate().setColorFilter(Color.YELLOW, PorterDuff.Mode.MULTIPLY);
        }
        item.setIcon(drawable);
    }

    /**
     * Resize a {@link android.graphics.drawable.Drawable Drawable}.
     * @param drawable The {@link android.graphics.drawable.Drawable Drawable} to be resized.
     * @param ratio Size ratio.
     * @return The resized {@link android.graphics.drawable.Drawable Drawable}.
     */
    public Drawable resizeDrawable(Drawable drawable, final float ratio) {
        int x = drawable.getIntrinsicWidth(), y = drawable.getIntrinsicHeight();
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        drawable = new BitmapDrawable(getResources(),
                Bitmap.createScaledBitmap(bitmap, (int)(x * ratio), (int)(y * ratio), true));
        return drawable;
    }

    /**
     * Return a formatted {@link java.lang.String String}
     * which tells open hours of a restaurant.
     * @param restaurant {@link com.lchpatners.shadal.Restaurant Restaurant}
     * with {@link com.lchpatners.shadal.Restaurant#openingHour openingHour}
     * and {@link com.lchpatners.shadal.Restaurant#closingHour closingHour}.
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

        return getString(R.string.open_time) + ": " + open + " ~ " + close;
    }


}
