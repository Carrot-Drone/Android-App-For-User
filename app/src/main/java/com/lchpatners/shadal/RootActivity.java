package com.lchpatners.shadal;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.lchpatners.shadal.recommend.RecommendedRestaurantActivity;
import com.lchpatners.shadal.setting.SeeMoreActivity;
import com.lchpatners.shadal.util.LogUtils;

/**
 * Created by youngkim on 2015. 8. 24..
 */
public class RootActivity extends ActionBarActivity {
    private static final String TAG = LogUtils.makeTag(RootActivity.class);
    private RootController mController;

    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private SlidingTabLayout mSlidingTabLayout;
    private RootViewPagerAdapter mViewPagerAdapter;

    private DrawerLayout mDrawerLayout;
    private TextView drawerTitle;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private Menu mMenu;
    private View.OnClickListener navigationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.drawer_ic_1 || view.getId() == R.id.drawer_1) {
                mDrawerLayout.closeDrawers();
            } else if (view.getId() == R.id.drawer_ic_2 || view.getId() == R.id.drawer_2) {
                Intent intent = new Intent(RootActivity.this, RecommendedRestaurantActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else if (view.getId() == R.id.drawer_ic_3 || view.getId() == R.id.drawer_3) {
                Intent intent = new Intent(RootActivity.this, SeeMoreActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        //TODO:checkAppMinimunVersion

        mController = new RootController(RootActivity.this);

        //TODO:check previous Database

        //TODO: set toast only one time

        //TODO : get popup list

        setNavigationDrawer();

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("주문하기");
        mToolbar.setNavigationIcon(R.drawable.icon_action_bar_menu);

        setSlidingTabBar();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close) {
            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                mToolbar.setTitle("주문하기");
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActionBar().setTitle(mDrawerTitle);
            }
        };

        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }

    private void setNavigationDrawer() {
        drawerTitle = (TextView) findViewById(R.id.drawer_title);
        drawerTitle.setText(mController.getCampus().getName());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.findViewById(R.id.drawer_ic_1).setOnClickListener(navigationClickListener);
        navigationView.findViewById(R.id.drawer_ic_2).setOnClickListener(navigationClickListener);
        navigationView.findViewById(R.id.drawer_ic_3).setOnClickListener(navigationClickListener);
        navigationView.findViewById(R.id.drawer_1).setOnClickListener(navigationClickListener); //주문하기
        navigationView.findViewById(R.id.drawer_2).setOnClickListener(navigationClickListener); //추천
        navigationView.findViewById(R.id.drawer_3).setOnClickListener(navigationClickListener); //더보기

        TextView lastDay = (TextView) navigationView.findViewById(R.id.last_day); //마지막 주문한날로부터
        TextView categoryName = (TextView) navigationView.findViewById(R.id.category); //가장 많이 주문한 음식
        TextView myCalls = (TextView) navigationView.findViewById(R.id.my_calls); //내 주문수
        TextView administrator = (TextView) navigationView.findViewById(R.id.administrator);

        //TODO: get recent call information
        administrator.setText(mController.getCampus().getAdministrator());
    }

    private void setSlidingTabBar() {
        mViewPager = (ViewPager) findViewById(R.id.main_pager);
        mViewPagerAdapter = new RootViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabs);
        mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.primary));
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.white100);
            }
        });
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();

        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_root, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mActionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        mMenu.findItem(R.id.action_restaurant_suggestion).setVisible(true);
        switch (item.getItemId()) {
            case R.id.action_search:
                mMenu.findItem(R.id.action_restaurant_suggestion).setVisible(false);
                return true;
            case R.id.action_restaurant_suggestion:
                Intent intent = new Intent(this, RestaurantSuggestionActivity.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                mMenu.findItem(R.id.action_restaurant_suggestion).setVisible(true);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
