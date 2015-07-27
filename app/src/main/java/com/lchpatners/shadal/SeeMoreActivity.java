package com.lchpatners.shadal;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class SeeMoreActivity extends ActionBarActivity {
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_more);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ListView navigation_list = (ListView) findViewById(R.id.navigation_list);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, MainActivity.drawer_str);
        navigation_list.setAdapter(arrayAdapter);
        navigation_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0) {
                    Intent intent = new Intent(SeeMoreActivity.this, MainActivity.class);
                    startActivity(intent);
                } else if (position == 1) {
                    mDrawerLayout.closeDrawer(navigation_list);
                } else if (position == 2) {
                    Intent intent = new Intent(SeeMoreActivity.this, BookmarkFragment.class);
                    startActivity(intent);
                }
            }
        });
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // getActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActionBar().setTitle(mDrawerTitle);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView listView = (ListView) findViewById(R.id.listView);

        // Set headers and items.
        SeeMoreListAdapter adapter = new SeeMoreListAdapter(this);
        adapter.addHeader(getString(R.string.participate_in));
        adapter.addItem(getString(R.string.facebook_page));
        adapter.addItem(getString(R.string.report_restaurant));
        adapter.addItem(getString(R.string.report_to_camdal));
        adapter.addHeader(getString(R.string.settings));
        adapter.addItem(getString(R.string.change_campus));
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view.findViewById(R.id.item) != null) {
                    TextView item = (TextView) view.findViewById(R.id.item);
                    CharSequence content = item.getText();

                    // To the facebook link
                    if (content.equals(getString(R.string.facebook_page))) {
//
//                        AnalyticsHelper helper = new AnalyticsHelper(getActivity().getApplication());
//                        helper.sendEvent("UX", "link_to_facebook_clicked", "facebook");

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        try {
                            getPackageManager().getPackageInfo("com.facebook.katana", 0);
                            intent.setData(Uri.parse("fb://page/424146807687590"));
                        } catch (Exception e) {
                            intent.setData(Uri.parse("https://www.facebook.com/snushadal"));
                        }
                        startActivity(intent);

                        // Request for a new restaurant, etc.
                    } else if (content.equals(getString(R.string.report_restaurant))) {
//
//                        AnalyticsHelper helper = new AnalyticsHelper(getApplication());
//                        helper.sendEvent("UX", "send_email", "individual");

                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
                                Preferences.getCampusEmail(SeeMoreActivity.this)
                                , null));
                        intent.putExtra(Intent.EXTRA_SUBJECT,
                                "[" + Preferences.getCampusKoreanShortName(SeeMoreActivity.this) + "]" +
                                        getString(R.string.report_restaurant_title));
                        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.report_restaurant_content));
                        startActivity(Intent.createChooser(intent, getString(R.string.select_app)));

                        // Report to Campusdal.
                    } else if (content.equals(getString(R.string.report_to_camdal))) {
//
//                        AnalyticsHelper helper = new AnalyticsHelper(getApplication());
//                        helper.sendEvent("UX", "send_email", "campusdal");

                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
                                "campusdal@gmail.com"
                                , null));
                        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.report_camdal_title));
                        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.report_camdal_content));
                        startActivity(Intent.createChooser(intent, getString(R.string.select_app)));

                        // Change the loaded campus.
                    }
                    if (content.equals(getString(R.string.change_campus))) {

                        Intent intent = new Intent(SeeMoreActivity.this, CampusSelectionActivity.class);
                        startActivity(intent);

                    }
                }
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

}
