package com.lchpatners.shadal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * Created by Guanadah on 2015-01-23.
 */
public class BookmarkFragment extends Fragment {

    private static Context context;
    public static RestaurantListAdapter latestAdapter;

    public static BookmarkFragment newInstance(Context context) {
        BookmarkFragment.context = context;
        return new BookmarkFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_view, container, false);
        ListView listView = (ListView)view.findViewById(R.id.list_view);

        ImageView img = new ImageView(context);
        img.setImageResource(R.drawable.no_bookmarks);
        img.setVisibility(View.GONE);
        ((ViewGroup)listView.getParent()).addView(img);
        listView.setEmptyView(img);

        final RestaurantListAdapter adapter
                = new RestaurantListAdapter(BookmarkFragment.context, RestaurantListAdapter.BOOKMARK);
        latestAdapter = adapter;
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getItem(position) instanceof Restaurant) {
                    Intent intent = new Intent(context, MenuListActivity.class);
                    intent.putExtra("RESTAURANT", (Restaurant) adapter.getItem(position));
                    intent.putExtra("REFERRER", "BookmarkFragment");
                    startActivity(intent);
                }
            }
        });
        return view;
    }

}