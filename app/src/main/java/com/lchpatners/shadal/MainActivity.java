package com.lchpatners.shadal;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity {

    RestaurantListFragment restaurantListFragmentCurrentlyOn;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Server(this).checkAppMinimumVersion();

        if (Preferences.getCampusKoreanShortName(this) == null) {
            startActivity(new Intent(this, InitializationActivity.class));
            finish();
        }

        if (!DatabaseHelper.getInstance(this).checkDatabase()) {
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                new Server(this).updateAll();
            }
        }

        new AsyncTask<Void, Void, Void>() {
            JSONArray campuses;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    String serviceCall = Server.makeServiceCall(
                            Server.BASE_URL + Server.CAMPUSES, Server.GET, null);
                    if (serviceCall == null) {
                        return null;
                    }
                    campuses = new JSONArray(serviceCall);
                    for (int i = 0; i < campuses.length(); i++) {
                        JSONObject campus = campuses.getJSONObject(i);
                        if (campus.getString("name_kor_short").equals(
                                Preferences.getCampusKoreanShortName(MainActivity.this))) {
                            Preferences.setCampus(MainActivity.this, campus);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();

        viewPager = (ViewPager)findViewById(R.id.main_pager);
        final PagerAdapter adapter = new PagerAdapter(MainActivity.this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(PagerAdapter.MAX_PAGE);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(adapter.getPageTitle(viewPager.getCurrentItem()));
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // do nothing
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // do nothing
            }
        };
        for (int page = 0; page < PagerAdapter.MAX_PAGE; page++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setIcon(adapter.getPageIcon(page, page == viewPager.getCurrentItem()))
                            .setTabListener(tabListener));
        }

        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {@Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
                for (int i = 0; i < actionBar.getTabCount(); i++) {
                    actionBar.getTabAt(i).setIcon(adapter.getPageIcon(i, false));
                }
                actionBar.getTabAt(position).setIcon(adapter.getPageIcon(position, true));
                actionBar.setTitle(adapter.getPageTitle(position));
            }
        });
        viewPager.setCurrentItem(0);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == PagerAdapter.MAIN) {
            if (restaurantListFragmentCurrentlyOn != null) {
                AnalyticsHelper helper = new AnalyticsHelper(getApplication());
                helper.sendScreen("메인 화면");
            }
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(PagerAdapter.MAIN, true);
        }
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        public static final int MAIN = 0;
        public static final int BOOKMARK = 1;
        public static final int RANDOM = 2;
        public static final int SEE_MORE = 3;
        public static final int MAX_PAGE = 4;

        private Context context;

        public PagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case MAIN:
                    fragment = CategoryListFragment.newInstance();
                    break;
                case BOOKMARK:
                    fragment = BookmarkFragment.newInstance();
                    break;
                case RANDOM:
                    fragment = RandomFragment.newInstance();
                    break;
                case SEE_MORE:
                    fragment = SeeMoreFragment.newInstance();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return MAX_PAGE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            switch (position) {
                case MAIN:
                    title = getString(R.string.app_name);
                    break;
                case BOOKMARK:
                    title = getString(R.string.bookmark);
                    break;
                case RANDOM:
                    title = getString(R.string.random);
                    break;
                case SEE_MORE:
                    title = getString(R.string.see_more);
                    break;
            }
            return title;
        }

        public Drawable getPageIcon(int page, boolean selected) {
            Drawable icon = null;
            switch (page) {
                case MAIN:
                    icon = getResources().getDrawable(
                            selected ? R.drawable.tab_main_selected : R.drawable.tab_main_unselected
                    );
                    break;
                case BOOKMARK:
                    icon = getResources().getDrawable(
                            selected ? R.drawable.tab_star_selected : R.drawable.tab_star_unselected
                    );
                    break;
                case RANDOM:
                    icon = getResources().getDrawable(
                            selected ? R.drawable.tab_dice_selected : R.drawable.tab_dice_unselected
                    );
                    break;
                case SEE_MORE:
                    icon = getResources().getDrawable(
                            selected ? R.drawable.tab_more_selected : R.drawable.tab_more_unselected
                    );
                    break;
            }
            return icon;
        }
    }
}
