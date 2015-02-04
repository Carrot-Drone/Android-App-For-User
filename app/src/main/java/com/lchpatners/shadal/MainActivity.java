package com.lchpatners.shadal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    ViewPager viewPager;
    boolean hasNoDatabase = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hasNoDatabase = !DatabaseHelper.getInstance(this).checkDatabase();
        if (hasNoDatabase) {
            //helper.loadFromAssets();
            tryLoadingFromServer();
        }

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
                if (tab.getPosition() == PagerAdapter.MAIN) {
                    // TODO remove an RLF instance when pressed MAIN on the MAIN page
                }
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

        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
                for (int i = 0; i < actionBar.getTabCount(); i++) {
                    actionBar.getTabAt(i).setIcon(adapter.getPageIcon(i, false));
                }
                actionBar.getTabAt(position).setIcon(adapter.getPageIcon(position, true));
                actionBar.setTitle(adapter.getPageTitle(position));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == PagerAdapter.MAIN) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(PagerAdapter.MAIN, true);
        }
    }

    @Override
    public void onDestroy() {
        if (hasNoDatabase) {
            cancelInitialDownload();
        }
        super.onDestroy();
    }

    public void tryLoadingFromServer() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new Server(this, Server.GWANAK).updateAll();
            hasNoDatabase = false;
            Toast.makeText(this, getString(R.string.initial_download_guide), Toast.LENGTH_LONG).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.please_check_connectivity)
                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            tryLoadingFromServer();
                        }
                    })
                    .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            cancelInitialDownload();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancelInitialDownload();
                }
            });
            dialog.show();
        }
    }

    public void cancelInitialDownload() {
        deleteDatabase(DatabaseHelper.DATABASE_NAME);
        finish();
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
                    fragment = CategoryListFragment.newInstance(context);
                    break;
                case BOOKMARK:
                    fragment = BookmarkFragment.newInstance(context);
                    break;
                case RANDOM:
                    fragment = RandomFragment.newInstance(context);
                    break;
                case SEE_MORE:
                    fragment = SeeMoreFragment.newInstance(context);
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
