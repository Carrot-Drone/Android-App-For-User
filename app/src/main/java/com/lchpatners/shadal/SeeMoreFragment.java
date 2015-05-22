package com.lchpatners.shadal;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Displays extra functions.
 */
public class SeeMoreFragment extends Fragment {

    /**
     * The {@link android.app.Activity Activity} to which this attaches.
     */
    private Activity activity;

    public static SeeMoreFragment newInstance() {
        return new SeeMoreFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            AnalyticsHelper helper = new AnalyticsHelper(activity.getApplication());
            helper.sendScreen("더보기 화면");
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            onResume();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // A wrapper instance.
        // Once the TalkParty banner gets of no use, it is okay to put it away.
        RelativeLayout wrapper = new RelativeLayout(activity);

        View view = inflater.inflate(R.layout.list_view, null, false);
        ListView listView = (ListView)view.findViewById(R.id.list_view);

        // Set headers and items.
        SeeMoreListAdapter adapter = new SeeMoreListAdapter(activity);
        adapter.addHeader(activity.getString(R.string.participate_in));
        adapter.addItem(activity.getString(R.string.facebook_page));
        adapter.addItem(activity.getString(R.string.report_restaurant));
        adapter.addItem(activity.getString(R.string.report_to_camdal));
        adapter.addHeader(activity.getString(R.string.settings));
        adapter.addItem(activity.getString(R.string.change_campus));
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view.findViewById(R.id.item) != null) {
                    TextView item = (TextView)view.findViewById(R.id.item);
                    CharSequence content = item.getText();

                    // To the facebook link
                    if (content.equals(getString(R.string.facebook_page))) {

                        AnalyticsHelper helper = new AnalyticsHelper(getActivity().getApplication());
                        helper.sendEvent("UX", "link_to_facebook_clicked", "facebook");

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        try {
                            activity.getPackageManager().getPackageInfo("com.facebook.katana", 0);
                            intent.setData(Uri.parse("fb://page/424146807687590"));
                        } catch (Exception e) {
                            intent.setData(Uri.parse("https://www.facebook.com/snushadal"));
                        }
                        startActivity(intent);

                    // Request for a new restaurant, etc.
                    } else if (content.equals(getString(R.string.report_restaurant))) {

                        AnalyticsHelper helper = new AnalyticsHelper(getActivity().getApplication());
                        helper.sendEvent("UX", "send_email", "individual");

                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
                                Preferences.getCampusEmail(activity)
                                , null));
                        intent.putExtra(Intent.EXTRA_SUBJECT,
                                "[" + Preferences.getCampusKoreanShortName(activity) + "]" +
                                activity.getString(R.string.report_restaurant_title));
                        intent.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.report_restaurant_content));
                        startActivity(Intent.createChooser(intent, activity.getString(R.string.select_app)));

                    // Report to Campusdal.
                    } else if (content.equals(getString(R.string.report_to_camdal))) {

                        AnalyticsHelper helper = new AnalyticsHelper(getActivity().getApplication());
                        helper.sendEvent("UX", "send_email", "campusdal");

                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
                                "campusdal@gmail.com"
                                , null));
                        intent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.report_camdal_title));
                        intent.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.report_camdal_content));
                        startActivity(Intent.createChooser(intent, activity.getString(R.string.select_app)));

                    // Change the loaded campus.
                    } if (content.equals(getString(R.string.change_campus))) {

                        Intent intent = new Intent(activity, CampusSelectionActivity.class);
                        startActivity(intent);

                    }
                }
            }
        });
        wrapper.addView(view);

        // TalkParty banner
        View banner = inflater.inflate(R.layout.banner_talkparty, null, false);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 72, getResources().getDisplayMetrics())
        );
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        banner.setLayoutParams(params);
        banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnalyticsHelper helper = new AnalyticsHelper(getActivity().getApplication());
                helper.sendEvent("UX", "talkparty", "talkparty");

                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://talkparty.net/company/board_view.asp?ctype=1&idx=102"));
                startActivity(intent);
            }
        });
        wrapper.addView(banner);

        return wrapper;
    }

}
