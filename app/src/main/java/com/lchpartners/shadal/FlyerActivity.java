package com.lchpartners.shadal;

import java.io.IOException;
import java.io.InputStream;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.lchpartners.shadal.ScreenSlidePageFragment;

public class FlyerActivity extends FragmentActivity {
	private String phoneNumber;
	
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
    public int getImageCount(){
        int count = 0;
        boolean exist = true;
        try{
        	InputStream istream = getAssets().open(phoneNumber + ".jpg");
        }catch(IOException ex){
        	exist = false;
 	   	}
 	   	if(exist) {
 	   		count ++;    	   
 	   	}else{
 	   		exist = true;
 	   	}
 	   
 	   	try{
 	   		InputStream istream = getAssets().open(phoneNumber + ".png");
 	   	}catch(IOException ex){
 	   		exist = false;
 	   	}
 	   	if(exist) {
 	   		count ++;
 	   	}else{
 	   		exist = true;
 	   	}
 	   
 	   	if(count == 0){
 	   		count = 2;
 	   	}else{
 	   		count =1;
 	   	}
 	   	return count;
    }
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_flyer);

	    // set PhoneNumber from Restaurant Activity
		Intent caller = getIntent();
		phoneNumber = caller.getStringExtra("phoneNumber");    	
		imgCount = getImageCount();
		
        mPager = (ViewPager)findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        
        // set ScreenSlidePageFragment Class members
    	ScreenSlidePageFragment.context = getApplicationContext();
    	ScreenSlidePageFragment.imgCount = imgCount;
    	ScreenSlidePageFragment.phoneNumber = phoneNumber;
	}

}
