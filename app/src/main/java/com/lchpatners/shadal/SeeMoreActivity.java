package com.lchpatners.shadal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;


public class SeeMoreActivity extends ActionBarActivity {
    Toolbar toolbar;
    String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_more);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        mTitle = getString(R.string.see_more);
        toolbar.setTitle(mTitle);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        TextView campus = (TextView) findViewById(R.id.campus);
        campus.setText(Preferences.getCampusKoreanName(SeeMoreActivity.this));
        TextView changeCampus = (TextView) findViewById(R.id.change_campus);
        changeCampus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SeeMoreActivity.this, CampusSelectionActivity.class);
                startActivity(intent);

            }
        });

/*
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
   */
    }

}
