package com.lchpatners.shadal;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The main {@link android.app.Activity Activity}.
 * Displays the {@link android.support.v4.app.Fragment Fragments}.
 */
public class MainActivity extends ActionBarActivity {

    public static final String MAIN = "main";
    /**
     * The main {@link android.support.v4.view.ViewPager ViewPager}.
     */
    protected ViewPager viewPager;
    /**
     * Indicates the last instance of {@link com.lchpatners.shadal.RestaurantListFragment
     * RestaurantListFragment}.
     */
    RestaurantListFragment restaurantListFragmentCurrentlyOn;
    private String title;
    private Toolbar toolbar;
    private SlidingTabLayout tabs;
    private ViewPagerAdapter adapter;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private TextView drawerTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        drawerTitle = (TextView) findViewById(R.id.drawer_title);

        new Server(this).checkAppMinimumVersion();


        // If no campus is selected, have the user select one.
        if (Preferences.getCampusKoreanShortName(this) == null) {
            startActivity(new Intent(this, InitializationActivity.class));
            finish();
        } else {
            drawerTitle.setText(Preferences.getCampusKoreanName(this));
           // new Server(this).getPopupList();
        }


        // If no database, get data from the server and update.
        if (!DatabaseHelper.getInstance(this).checkDatabase(Preferences.getCampusEnglishName(this))) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                new Server(this).updateAll();
            }
        }


        updateCampusMetaData();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        title = getString(R.string.drawer_order);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.main_pager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), MAIN);
        viewPager.setAdapter(adapter);

        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.primary);
            }
        });

        tabs.setViewPager(viewPager, MAIN);

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }

                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {
                    case R.id.drawer_item_1:
                        return true;
                    case R.id.drawer_item_2:
                        return true;
                    case R.id.drawer_item_3:
                        Intent intent = new Intent(MainActivity.this, SeeMoreActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        return true;

                }
                return false;
            }
        });

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close) {
            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                toolbar.setTitle(title);
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActionBar().setTitle(mDrawerTitle);
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);



     /*   viewPager.setOffscreenPageLimit(ViewPagerAdapter.MAX_PAGE);
        //viewPager.setCurrentItem(PagerAdapter.MAIN);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(adapter.getPageTitle(viewPager.getCurrentItem()));
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // Do nothing.
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // Do nothing.
            }
        };

        for (int page = 0; page < ViewPagerAdapter.MAX_PAGE; page++) {
            ActionBar.Tab tab =
                    actionBar.newTab()
                            .setIcon(adapter.getPageIcon(page, page == viewPager.getCurrentItem()))
                            .setText(adapter.getPageTitle(page))
                            .setTabListener(tabListener);
            actionBar.addTab(tab);


        }

        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
                viewPager.setCurrentItem(position);
                for (int i = 0; i < actionBar.getTabCount(); i++) {
                    actionBar.getTabAt(i).setIcon(adapter.getPageIcon(i, false))
                            .setText(adapter.getPageTitle(i));
                }
                actionBar.getTabAt(position)
                        .setIcon(adapter.getPageIcon(position, true));

                actionBar.setTitle("주문하기");
            }
        });

        viewPager.setCurrentItem(ViewPagerAdapter.MAIN);
        */
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();

        } else {
            super.onBackPressed();
        }
    }


    /**
     * Update campus meta data of currently selected campus.
     *
     * @see com.lchpatners.shadal.Server.CampusesLoadingTask CampusesLoadingTask
     */
    public void updateCampusMetaData() {
        new Server.CampusesLoadingTask() {
            @Override
            public void onPostExecute(Void aVoid) {
                if (results == null) return;
                for (int i = 0; i < results.length(); i++) {
                    try {
                        JSONObject result = results.getJSONObject(i);
                        if (result.getString("name_eng").equals(
                                Preferences.getCampusEnglishName(MainActivity.this))) {
                            Preferences.setCampus(MainActivity.this, result);
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            protected Void doInBackground(Void... params) {

                try {
                    serviceCall = Server.makeServiceCall(
                            Server.BASE_URL + Server.CAMPUSES, Server.GET, null);
                    if (serviceCall == null) {
                        return null;
                    }
                    results = new JSONArray(serviceCall);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }


}