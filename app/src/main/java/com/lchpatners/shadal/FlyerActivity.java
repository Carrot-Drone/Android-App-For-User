package com.lchpatners.shadal;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import info.android.sqlite.helper.DatabaseHelper;
import info.android.sqlite.model.Restaurant;

public class FlyerActivity extends FragmentActivity {
	private long res_id;
    private Restaurant restaurant;
    private ArrayList<String> urls;
    
	private ViewPager mPager;

    private PagerAdapter mPagerAdapter;
    
    private int imgCount;

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
        	
            return ScreenSlidePageFragment.create(position);
        }

        @Override
        public int getCount() {
        	return imgCount; 
        }
    }
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_flyer);

	    // set PhoneNumber from Restaurant Activity
		Intent caller = getIntent();
		res_id = caller.getIntExtra("res_id", 0);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        restaurant = dbHelper.getRestaurant(res_id);
        urls = dbHelper.getALLURLsForRestaurant(res_id);
        dbHelper.closeDB();

        imgCount = urls.size();
		
        mPager = (ViewPager)findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        
        // set ScreenSlidePageFragment Class members
    	ScreenSlidePageFragment.context = getApplicationContext();
    	ScreenSlidePageFragment.imgCount = imgCount;
    	ScreenSlidePageFragment.restaurant = restaurant;
        ScreenSlidePageFragment.urls = urls;
	}

}
