package com.lchpatners.shadal;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
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
    private Menu menu;
    private String title;
    private Toolbar toolbar;
    private SlidingTabLayout tabs;
    private ViewPagerAdapter adapter;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private TextView drawerTitle;
    private SearchView searchView;

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


        viewPager = (ViewPager) findViewById(R.id.main_pager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), this, ViewPagerAdapter.MAINACTIVITY);
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

        if (getIntent().getStringExtra("setcurrentpage") != null) {
            viewPager.setCurrentItem(1);
            Log.d("setcurrentpage", "getintentnotnull");
        }

        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

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
        menu.findItem(R.id.action_restaurant_suggestion).setVisible(true);
        switch (item.getItemId()) {
            case R.id.action_search:
                menu.findItem(R.id.action_restaurant_suggestion).setVisible(false);
                return true;
            case R.id.action_restaurant_suggestion:
                Intent intent = new Intent(this, RestaurantSuggestionActivity.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                menu.findItem(R.id.action_restaurant_suggestion).setVisible(true);
            default:
                return super.onOptionsItemSelected(item);
        }
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
                        if (result.getString("id").equals(
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


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        menu.findItem(R.id.action_restaurant_suggestion).setVisible(true);
        searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("원하시는 음식점, 메뉴를 입력해주세요");

        TextView searchText = (TextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchText.setTextSize(15);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                return false;
            }
        });

     /*   getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setQueryHint("원하시는 음식점, 메뉴를 검색해주세요");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });*/
        return super.onCreateOptionsMenu(menu);
    }


}