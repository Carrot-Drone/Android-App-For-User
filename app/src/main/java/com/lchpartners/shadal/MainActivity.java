package com.lchpartners.shadal;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.lchpartners.apphelper.preference.PrefUtil;
import com.lchpartners.apphelper.server.Server;
import com.lchpartners.fragments.ActionBarUpdater;
import com.lchpartners.fragments.CategoryFragment;
import com.lchpartners.fragments.RestaurantsFragment;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import info.android.sqlite.helper.DatabaseHelper;
import info.android.sqlite.model.Restaurant;

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
        private SparseArray<Fragment> mCachedFragments = new SparseArray<Fragment>();
        public ShadalTabsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment result = null;
            if (position == PAGE_MAIN) result = CategoryFragment.newInstance();
            else if (position == PAGE_FAVORITE) result = RestaurantsFragment.newInstance(0);
            else if (position == PAGE_RANDOM) result = new CategoryFragment();
            else if (position == PAGE_MORE) result = new CategoryFragment();
            mCachedFragments.put(position, result);
            return result;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        public Fragment getCachedItem (int position) {
            return mCachedFragments.get(position);
        }

    }

    //For handling fragment tabs.
    private ShadalTabsAdapter mTabsAdapter;
    private ViewPager mPager;
    private ImageButton mSelectedPageBtn, mMainBtn, mFavoriteBtn, mRandomBtn, mMoreBtn;
    private int mCurrPage = 0, mNextPage = 0;

    //For handling data
    DatabaseHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTabsAdapter = new ShadalTabsAdapter(getFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mTabsAdapter);
        mPager.setOnPageChangeListener(this);

        //TODO : for long clicks - 'set default page'

        mMainBtn = (ImageButton) findViewById(R.id.button_tab_main);
        mFavoriteBtn = (ImageButton) findViewById(R.id.button_tab_favorite);
        mRandomBtn = (ImageButton) findViewById(R.id.button_tab_random);
        mMoreBtn = (ImageButton) findViewById(R.id.button_tab_more);
        mMainBtn.setOnClickListener(this);
        mMainBtn.setSelected(true);
        mFavoriteBtn.setOnClickListener(this);
        mRandomBtn.setOnClickListener(this);
        mMoreBtn.setOnClickListener(this);

        mSelectedPageBtn = mMainBtn;

        // 처음 설치시 assets/databases/Shadal 파일로 디비 설정
        try{

            Context context = getApplicationContext();
            mDbHelper = new DatabaseHelper(context);
            boolean dbExists = mDbHelper.doesDatabaseExist();

            SQLiteDatabase db;

            // Database file이 없거나, version이 맞지 않으면..
            String version = PrefUtil.getVersion(getApplicationContext());
            Log.d("tag", "original version :"+version + " latest Version : " + PrefUtil.VERSION);

            if(!dbExists || !version.equals(PrefUtil.VERSION)){
                PrefUtil.setVersion(getApplicationContext());
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
        mDbHelper.closeDB();
    }

    /**
     * NOTE : Each fragment is responsible for refreshing the options menu on scroll events.
     */
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        if(mNextPage == 0)
            getMenuInflater().inflate(R.menu.search,menu);
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
    public void onPageSelected(int position) {
        mCurrPage = position;
        mNextPage = position;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        /**Determine scroll direction and check whether the user scrolled enough.
         * Note that positionOffset is always positive, and position value is given quite weirdly.
         * Carefully take care of behavior of the OnPageChangedListener using Logcat
         * before you change code here.
         **/

        if (position < mCurrPage) {
            //LEFT scroll : In this case, position value itself points the new page the user is navigating to.
            if (positionOffset > 0.5) {
                position++; //Retain currentPageIndex.
            }
        }
        else {
            //RIGHT scroll : In this case, position value points the previous page.
            if (positionOffset < 0.5) {
                //Retain curentPageIndex.
            }
            else position++; // If the user scrolled enough, Make this value to point the new page.
        }

        if (mNextPage != position) {
            //When mNextPage is changed, it means user changed final destination of the current scroll motion.
            mNextPage = position;
            //Change action bar content for the next page.
            ActionBarUpdater nextPageFragment = (ActionBarUpdater) mTabsAdapter.getCachedItem(mNextPage);
            nextPageFragment.updateActionBar();
        }

        mSelectedPageBtn.setSelected(false);
        switch (mNextPage) {
            case PAGE_MAIN :
                mSelectedPageBtn = mMainBtn;
                break;
            case PAGE_FAVORITE :
                mSelectedPageBtn = mFavoriteBtn;
                break;
            case PAGE_RANDOM :
                mSelectedPageBtn = mRandomBtn;
                break;
            case PAGE_MORE :
                mSelectedPageBtn = mMoreBtn;
                break;
        }
        mSelectedPageBtn.setSelected(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //Do nothing
    }
}
