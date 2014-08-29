package com.lchpartners.shadal;

import android.app.ActionBar;
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
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.lchpartners.fragments.CategoryFragment;

import java.io.IOException;

import info.android.sqlite.helper.DatabaseHelper;

public class MainActivity extends Activity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private final static String TAG = "MainActivity";

    private final static int PAGE_COUNT = 4;
    private final static int PAGE_MAIN = 0;
    private final static int PAGE_FAVORITE = 1;
    private final static int PAGE_RANDOM = 2;
    private final static int PAGE_MORE = 3;

    /**
     * Created by Gwangrae Kim on 2014-08-25.
     */
    public static class ShadalTabsAdapter extends FragmentPagerAdapter {

        //TODO - use it or remove it.
        private ActionBar mActionBar;

        public ShadalTabsAdapter(FragmentManager fm, ActionBar actionBar) {
            super(fm);
            this.mActionBar = actionBar;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == PAGE_MAIN) return new CategoryFragment();
            else if (position == PAGE_FAVORITE) return new CategoryFragment();
            else if (position == PAGE_RANDOM) return new CategoryFragment();
            else if (position == PAGE_MORE) return new CategoryFragment();
            else return null;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

    }

    //For handling fragment tabs.
    ShadalTabsAdapter mAdapter;
    ViewPager mPager;
    ImageButton mSelectedPageBtn, mMainBtn, mFavoriteBtn, mRandomBtn, mMoreBtn;

    //For handling data
	DatabaseHelper mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        mAdapter = new ShadalTabsAdapter(getFragmentManager(), getActionBar());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(this);

        //TODO : for long clicks - 'set default page'

        mMainBtn = (ImageButton) findViewById(R.id.button_tab_main);
        mFavoriteBtn = (ImageButton) findViewById(R.id.button_tab_favorite);
        mRandomBtn = (ImageButton) findViewById(R.id.button_tab_random);
        mMoreBtn = (ImageButton) findViewById(R.id.button_tab_more);

        mMainBtn.setOnClickListener(this);
        mFavoriteBtn.setOnClickListener(this);
        mRandomBtn.setOnClickListener(this);
        mMoreBtn.setOnClickListener(this);

        mSelectedPageBtn = mMainBtn;

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
		}
        catch(SQLException eSQL){
	        Log.e(TAG,"Cannot open database");
            Toast.makeText(this,"데이터베이스를 여는 데 실패했습니다.",Toast.LENGTH_SHORT).show();
            finish();
		}
		catch (IOException IOe) {
	        Log.e(TAG,"Cannot copy initial database");
            Toast.makeText(this,"초기 데이터베이스를 복사하는 데 실패했습니다.",Toast.LENGTH_SHORT).show();
            finish();
		}
	}

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        //TODO : implement search button - getMenuInflater().inflate(R.menu.search,menu);
        return true;
    }

    @Override
    public void onClick (View v) {
        int id = v.getId();
        //When the user pressed the same tab.
        if (id == mSelectedPageBtn.getId() && id != mRandomBtn.getId())
            return;

        mSelectedPageBtn.setSelected(false);
        if (id == R.id.button_tab_main) {
            mPager.setCurrentItem(PAGE_MAIN, true);
            mSelectedPageBtn = mMainBtn;
        }
        else if (id == R.id.button_tab_favorite) {
            mPager.setCurrentItem(PAGE_FAVORITE, true);
            mSelectedPageBtn = mFavoriteBtn;
        }
        else if (id == R.id.button_tab_random) {
            mPager.setCurrentItem(PAGE_RANDOM, true);
            //TODO : shake effect
            //TODO : refresh content on Scroll?

            mSelectedPageBtn = mRandomBtn;
        }
        else if (id == R.id.button_tab_more) {
            mPager.setCurrentItem(PAGE_MORE, true);
            mSelectedPageBtn = mMoreBtn;
        }
        mSelectedPageBtn.setSelected(true);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // Log.e(TAG, "SCROLLED");
    }

    @Override
    public void onPageSelected(int position) {
        Log.e(TAG, "SELECTED "+Integer.valueOf(position).toString());
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.e(TAG, "SCROLLSTATECHANGED");
    }
}
