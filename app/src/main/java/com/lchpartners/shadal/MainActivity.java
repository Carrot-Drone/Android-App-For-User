package com.lchpartners.shadal;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;

import com.lchpartners.fragments.CategoryFragment;

import java.io.IOException;

import info.android.sqlite.helper.DatabaseHelper;

public class MainActivity extends Activity {

    /**
     * Created by Gwangrae Kim on 2014-08-25.
     */
    public static class ShadalTabsAdapter extends FragmentPagerAdapter {
        public ShadalTabsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new CategoryFragment();
        }

        @Override
        public int getCount() {
            return 4;
        }

    }

    private final static String TAG = "MainActivity";
    //For handling fragment tabs.
    ShadalTabsAdapter mAdapter;
    ViewPager mPager;

    //For handling data
	DatabaseHelper mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        mAdapter = new ShadalTabsAdapter(getFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
		
		// 처음 설치시 assets/databases/Shadal 파일로 디비 설정
		try{
	        mDbHelper = new DatabaseHelper(getApplicationContext());
	        boolean dbExists = mDbHelper.doesDatabaseExist();

	        SQLiteDatabase db;
	        if(!dbExists){
	            //get database, we will override it in next steep
	            //but folders containing the database are created
	            db = mDbHelper.getWritableDatabase();
	            db.close();
	            //copy database
	            mDbHelper.copyDataBase();                        
	        }                                              
	        db = mDbHelper.getWritableDatabase();
		}catch(SQLException eSQL){
	        Log.e(TAG,"Cannot open database");
		}
		catch (IOException IOe) {
	        Log.e(TAG,"Cannot copy initial database");
		}
	}

    /**
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent moveToRestaurant = new Intent(MainActivity.this, RestaurantActivity.class);
		moveToRestaurant.putExtra("category", position);
		startActivity(moveToRestaurant);
		onPause();
	}*/

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.search,menu);
        return true;
    }
}
