package com.lchpatners.shadal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Guanadah on 2015-01-24.
 */
public class SeeMoreFragment extends Fragment {

    private static Context context;

    public static SeeMoreFragment newInstance(Context context) {
        SeeMoreFragment.context = context;
        return new SeeMoreFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_view, container, false);
        ListView listView = (ListView)view.findViewById(R.id.list_view);
        listView.setAdapter(new SeeMoreListAdapter(context));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view.findViewById(R.id.item) != null) {
                    TextView item = (TextView)view.findViewById(R.id.item);
                    if (item.getText().equals(context.getString(R.string.facebook_page))) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        try {
                            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
                            intent.setData(Uri.parse("fb://page/424146807687590"));
                        } catch (Exception e) {
                            intent.setData(Uri.parse("https://www.facebook.com/snushadal"));
                        }
                        startActivity(intent);
                    } else if (item.getText().equals(context.getString(R.string.report))) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","campusdal@gmail.com", null));
                        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.report_title));
                        intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.report_content));
                        startActivity(Intent.createChooser(intent, context.getString(R.string.select_app)));
                    } else if (item.getText().equals(context.getString(R.string.yongon))) {
                        final String PACKAGE_NAME = "com.lchpatners.yongon";
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        try {
                            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
                            intent.setData(Uri.parse("market://details?id=" + PACKAGE_NAME));
                        } catch (Exception e) {
                            intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=" + PACKAGE_NAME));
                        }
                        startActivity(intent);
                    }
                }
            }
        });
        return view;
    }

}
