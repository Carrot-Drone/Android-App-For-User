package com.lchpatners.shadal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;


public class RestaurantActivity extends ActionBarActivity {


    public static final String RESTAURANT = "Restaurant";
    private CharSequence category;
    private Toolbar toolbar;
    private int position;
    private SlidingTabLayout tabs;
    private ViewPagerAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);


        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        position = intent.getIntExtra("position", 1);

        toolbar.setTitle(category);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.main_pager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), RESTAURANT);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);

        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setBackgroundColor(getResources().getColor(R.color.primary));

        tabs.setDistributeEvenly(true);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.white100);
            }
        });

        tabs.setViewPager(viewPager, RESTAURANT);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                category = ViewPagerAdapter.categories[position];
                toolbar.setTitle(category);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
