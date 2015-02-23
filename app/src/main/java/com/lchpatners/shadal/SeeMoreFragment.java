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
 * Created by Guanadah on 2015-01-24.
 */
public class SeeMoreFragment extends Fragment {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout layout = new RelativeLayout(activity);
        View view = inflater.inflate(R.layout.list_view, null, false);
        ListView listView = (ListView)view.findViewById(R.id.list_view);
        listView.setAdapter(new SeeMoreListAdapter(activity));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view.findViewById(R.id.item) != null) {
                    TextView item = (TextView)view.findViewById(R.id.item);
                    CharSequence content = item.getText();
                    if (content.equals(getString(R.string.facebook_page))) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        try {
                            activity.getPackageManager().getPackageInfo("com.facebook.katana", 0);
                            intent.setData(Uri.parse("fb://page/424146807687590"));
                        } catch (Exception e) {
                            intent.setData(Uri.parse("https://www.facebook.com/snushadal"));
                        }
                        startActivity(intent);
                    } else if (content.equals(getString(R.string.report_restaurant))) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
                                Preferences.getCampusEmail(activity)
                        , null));
                        intent.putExtra(Intent.EXTRA_SUBJECT,
                                "[" + Preferences.getCampusKoreanShortName(activity) + "]" +
                                activity.getString(R.string.report_restaurant_title));
                        intent.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.report_restaurant_content));
                        startActivity(Intent.createChooser(intent, activity.getString(R.string.select_app)));
                    } else if (content.equals(getString(R.string.report_to_camdal))) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
                                "campusdal@gmail.com"
                                , null));
                        intent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.report_camdal_title));
                        intent.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.report_camdal_content));
                        startActivity(Intent.createChooser(intent, activity.getString(R.string.select_app)));
                    } if (content.equals(getString(R.string.change_campus))) {
                        Intent intent = new Intent(activity, CampusSelectionActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
        layout.addView(view);

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
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://talkparty.net/company/board_view.asp?ctype=1&idx=102"));
                startActivity(intent);
            }
        });
        layout.addView(banner);

        return layout;
    }

}
