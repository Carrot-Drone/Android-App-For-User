package com.lchpartners.shadal;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.lchpartners.apphelper.preference.PrefUtil;
import com.lchpartners.apphelper.server.Server;
import com.lchpartners.fragments.ActionBarUpdater;
import com.lchpartners.fragments.CategoryFragment;
import com.lchpartners.fragments.MoreFragment;
import com.lchpartners.fragments.MenuFragment;
import com.lchpartners.fragments.RestaurantsFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import info.android.sqlite.helper.DatabaseHelper;
import info.android.sqlite.model.Restaurant;


public class MainActivity extends Activity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private final static String TAG = "MainActivity";

    private final static int TAB_COUNT = 4;

    public final static int TAB_MAIN = 0;
    public final static int TAB_FAVORITE = 1;
    public final static int TAB_RANDOM = 2;
    public final static int TAB_MORE = 3;

    /**
     * Created by Gwangrae Kim on 2014-08-25.
     */
    public static class ShadalTabsAdapter extends FragmentPagerAdapter {
        /**
         * Simple record for generating a new fragment instance.
         */
        public static class FragmentRecord {
            public String className;
            public int param0;

            public FragmentRecord(Class<?> fragmentClass) {
                this.className = fragmentClass.getSimpleName();
            }

            public FragmentRecord(Class<?> fragmentClass, int param0) {
                this.className = fragmentClass.getSimpleName();
                this.param0 = param0;
            }
        }

        /**
         * You should add some code here if you want to construct more kinds of custom fragments
         * @param record
         * @return
         */
        private Fragment getFragmentFromRecord(FragmentRecord record) {
            if(record.className.equals(CategoryFragment.class.getSimpleName())) {
//                Log.e(TAG,"1");
                return CategoryFragment.newInstance();
            }
            else if(record.className.equals(RestaurantsFragment.class.getSimpleName())) {
//                Log.e(TAG,"2");
                return RestaurantsFragment.newInstance(record.param0);
            }
            else if(record.className.equals(MenuFragment.class.getSimpleName())) {
                return MenuFragment.newInstance(record.param0);
            }else if(record.className.equals(MoreFragment.class.getSimpleName())){
                return MoreFragment.newInstance(record.param0);
            }
            else return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //Prevent items being destroyed for faster scroll performance.
            //May cause memory issue if used with fragments with large images
        }

        private SparseArray<Fragment> mCurrentFragments = new SparseArray<Fragment>();
        public ShadalTabsAdapter(FragmentManager fm, MainActivity activity) {
            super(fm);
            this.mActivity = activity;
            this.mDbHelper = mActivity.mDbHelper;
            this.mFragementManager = fm;
            for (int i = 0; i < isRootLoad.length ; i++) {
                isRootLoad[i] = true;
            }
            //Generate Fragments
            mFirstPageStack.push(new FragmentRecord(CategoryFragment.class));
            mSecondPageStack.push(new FragmentRecord(RestaurantsFragment.class, 0));
            mThirdPageStack.push(new FragmentRecord(MenuFragment.class, mDbHelper.getRandomRestaurant().id));
            mFourthPageStack.push(new FragmentRecord(MoreFragment.class, 0));
        }

        private FragmentManager mFragementManager;
        private MainActivity mActivity;
        private DatabaseHelper mDbHelper;

        //Back stacks for each page
        private Stack<FragmentRecord> mFirstPageStack = new Stack<FragmentRecord>();
        private Stack<FragmentRecord> mSecondPageStack = new Stack<FragmentRecord>();
        private Stack<FragmentRecord> mThirdPageStack = new Stack<FragmentRecord>();
        private Stack<FragmentRecord> mFourthPageStack = new Stack<FragmentRecord>();

        //Validity check value for each page
        private boolean isFirstPageValid = false;
        private boolean isSecondPageValid = false;
        private boolean isThirdPageValid = false;
        private boolean isFourthPageValid = false;

        //TODO - convert above declarations (too much..) to array like below.
        private boolean[] isRootLoad = new boolean[4]; //Used to suppress updateActionBar() for the first launch.

        private Stack<FragmentRecord> getStack(int tab) {
//            Log.e(TAG, "getStack "+Integer.valueOf(tab).toString());
            if (tab == TAB_MAIN) return mFirstPageStack;
            else if (tab == TAB_FAVORITE) return mSecondPageStack;
            else if (tab == TAB_RANDOM) return mThirdPageStack;
            else if (tab == TAB_MORE) return mFourthPageStack;
            else return null;
        }

        private boolean isValid(int tab) {
//            Log.e(TAG, "isValid "+Integer.valueOf(tab).toString());
            if (tab == TAB_MAIN) return isFirstPageValid;
            else if (tab == TAB_FAVORITE) return isSecondPageValid;
            else if (tab == TAB_RANDOM) return isThirdPageValid;
            else if (tab == TAB_MORE) return isFourthPageValid;
            else return false;
        }

        private boolean isAllTabsValid() {
//            Log.e(TAG, "isAllTabsValid");
            return isFirstPageValid && isSecondPageValid && isThirdPageValid && isFourthPageValid;
        }

        private void setValidity(int tab, boolean isValid) {
//            Log.e(TAG, "setValid "+Integer.valueOf(tab).toString()+" "+Boolean.valueOf(isValid).toString());
            if (tab == TAB_MAIN) isFirstPageValid = isValid;
            else if (tab == TAB_FAVORITE) isSecondPageValid = isValid;
            else if (tab == TAB_RANDOM) isThirdPageValid = isValid;
            else if (tab == TAB_MORE) isFourthPageValid = isValid;
        }

        @Override
        public Fragment getItem(int tab) {
//            Log.e(TAG, "getItem "+Integer.valueOf(tab).toString());
            Stack<FragmentRecord> currStack = getStack(tab);
            Fragment result = getFragmentFromRecord(currStack.peek());
            if (isRootLoad[tab]) {
                isRootLoad[tab] = false;
            }
            else ((ActionBarUpdater) result).updateActionBarOnCreateView();

            setValidity(tab, true);
            mCurrentFragments.put(tab, result);
            return result;
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Override
        public int getItemPosition(Object object) {
 //           Log.e(TAG, "getItemPosition");
            if (isAllTabsValid()) {
 //               Log.e(TAG, "No change");
                return POSITION_UNCHANGED;
            }
            else {
 //               Log.e(TAG, "refresh screens");
                return POSITION_NONE;
            }
        }

        public void push(int tab, FragmentRecord newFragmentRecord) {
 //           Log.e(TAG, "push "+Integer.valueOf(tab).toString()+" "+newFragmentRecord.className);

            Fragment previousTop = getCurrentFragment(tab);
            mFragementManager.beginTransaction().remove(previousTop).commit();
            mFragementManager.executePendingTransactions();
            mCurrentFragments.put(tab, null);

            Stack<FragmentRecord> currStack = getStack(tab);
            currStack.push(newFragmentRecord);
            setValidity(tab, false);
            notifyDataSetChanged();
        }

        //TODO : notify user before finishing!
        public void pop(int tab) {
//            Log.e(TAG, "pop "+Integer.valueOf(tab).toString());
            Fragment previousTop = getCurrentFragment(tab);
            mFragementManager.beginTransaction().remove(previousTop).commit();
            mFragementManager.executePendingTransactions();
            mCurrentFragments.put(tab, null);
            Stack<FragmentRecord> currStack = getStack(tab);
            currStack.pop(); //Remove current Fragment's record from the stack.

            if (currStack.size() == 0) {
                mActivity.finish();
            }
            else {
                setValidity(tab, false);
                notifyDataSetChanged();
            }
        }

        public Fragment getCurrentFragment(int tab) {
//            Log.e(TAG, "getCurrentFragment "+Integer.valueOf(tab).toString());
            return mCurrentFragments.get(tab);
        }
    }

    /**
     * TODO : track user's navigation and store it in one LARGE stack.
     * and pop it if the user presses back button.
     */
    @Override
    public void onBackPressed() {
        mTabsAdapter.pop(mCurrTab);
    }

    public ShadalTabsAdapter getAdapter() {
        return mTabsAdapter;
    }

    //For handling fragment tabs.
    private ShadalTabsAdapter mTabsAdapter;
    private ViewPager mPager;
    private ImageButton mSelectedTabBtn, mMainBtn, mFavoriteBtn, mRandomBtn, mMoreBtn;
    private int mCurrTab = 0, mNextTab = 0;

    //For handling data
    DatabaseHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context2 = getApplicationContext();
        mDbHelper = new DatabaseHelper(context2);
        mDbHelper.getFavoriteRestaurant();

        // 처음 설치시 assets/databases/Shadal 파일로 디비 설정
        try{
            Context context = getApplicationContext();
            mDbHelper = new DatabaseHelper(context);
            boolean dbExists = mDbHelper.doesDatabaseExist();

            SQLiteDatabase db;

            // Database file이 없거나, version이 맞지 않으면..
            String version = PrefUtil.getVersion(getApplicationContext());
//            Log.d("tag", "original version :"+version + " latest Version : " + PrefUtil.VERSION);

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
//            Log.e(TAG,"Cannot open database");
            Toast.makeText(this,"데이터베이스를 여는 데 실패했습니다.",Toast.LENGTH_SHORT).show();
            finish();
        }
        catch (IOException IOe) {
//            Log.e(TAG,"Cannot copy initial database");
            Toast.makeText(this,"초기 데이터베이스를 복사하는 데 실패했습니다.",Toast.LENGTH_SHORT).show();
            finish();
        }


        mTabsAdapter = new ShadalTabsAdapter(getFragmentManager(), this);
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

        mSelectedTabBtn = mMainBtn;


    }

    @Override
    public void onDestroy() {
        mDbHelper.closeDB();
        super.onDestroy();
    }

    /**
     * NOTE : Each fragment is responsible for refreshing the options menu on scroll events.
     */
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
       // if(mNextTab == 0)
       //    getMenuInflater().inflate(R.menu.search,menu);
        return true;
    }

    @Override
    public void onClick (View v) {
        int id = v.getId();
        //When the user pressed the same tab.
        if (id == mSelectedTabBtn.getId() && id != mRandomBtn.getId())
            return;

        mSelectedTabBtn.setSelected(false);
        if (id == R.id.button_tab_main) {
            mPager.setCurrentItem(TAB_MAIN, true);
            mSelectedTabBtn = mMainBtn;
        }
        else if (id == R.id.button_tab_favorite) {
            mPager.setCurrentItem(TAB_FAVORITE, true);
            mSelectedTabBtn = mFavoriteBtn;
        }
        else if (id == R.id.button_tab_random) {
            mPager.setCurrentItem(TAB_RANDOM, true);
            //TODO : shake effect
            //TODO : refresh content on Scroll?

            mSelectedTabBtn = mRandomBtn;
        }
        else if (id == R.id.button_tab_more) {
            mPager.setCurrentItem(TAB_MORE, true);
            mSelectedTabBtn = mMoreBtn;
        }
        mSelectedTabBtn.setSelected(true);
    }

    @Override
    public void onPageSelected(int position) {
        mCurrTab = position;
        mNextTab = position;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        /**Determine scroll direction and check whether the user scrolled enough.
         * Note that positionOffset is always positive, and position value is given quite weirdly.
         * Carefully take care of behavior of the OnPageChangedListener using Logcat
         * before you change code here.
         **/

        if (position < mCurrTab) {
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

        if (mNextTab != position) {
            //When mNextTab is changed, it means user changed final destination of the current scroll motion.
            //Re-drawing of the action bar and bottom buttons only should happen in this case.

            mNextTab = position;
            //Change action bar content for the next page.
            ActionBarUpdater nextPageFragment = (ActionBarUpdater) mTabsAdapter.getCurrentFragment(mNextTab);
            nextPageFragment.updateActionBar();

            mSelectedTabBtn.setSelected(false);
            switch (mNextTab) {
                case TAB_MAIN:
                    mSelectedTabBtn = mMainBtn;
                    break;
                case TAB_FAVORITE:
                    mSelectedTabBtn = mFavoriteBtn;
                    break;
                case TAB_RANDOM:
                    mSelectedTabBtn = mRandomBtn;
                    break;
                case TAB_MORE:
                    mSelectedTabBtn = mMoreBtn;
                    break;
            }
            mSelectedTabBtn.setSelected(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {

            //Double-check action bar and bottom button update (to deal with fast scroll)
            ActionBarUpdater nextPageFragment = (ActionBarUpdater) mTabsAdapter.getCurrentFragment(mCurrTab);
            nextPageFragment.updateActionBar();

            mSelectedTabBtn.setSelected(false);
            switch (mCurrTab) {
                case TAB_MAIN:
                    mSelectedTabBtn = mMainBtn;
                    break;
                case TAB_FAVORITE:
                    mSelectedTabBtn = mFavoriteBtn;
                    break;
                case TAB_RANDOM:
                    mSelectedTabBtn = mRandomBtn;
                    break;
                case TAB_MORE:
                    mSelectedTabBtn = mMoreBtn;
                    break;
            }
            mSelectedTabBtn.setSelected(true);
        }
    }
}
