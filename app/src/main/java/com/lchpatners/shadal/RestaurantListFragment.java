package com.lchpatners.shadal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by Guanadah on 2015-01-23.
 */
public class RestaurantListFragment extends Fragment {

    public static RestaurantListAdapter latestAdapter;

    private static Context context;

    public static RestaurantListFragment newInstance(Context context, String category) {
        RestaurantListFragment.context = context;
        RestaurantListFragment rlf = new RestaurantListFragment();
        Bundle args = new Bundle();
        args.putString("CATEGORY", category);
        rlf.setArguments(args);
        return rlf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final String category = getArguments().getString("CATEGORY");

        RestaurantListAdapter adapter = new RestaurantListAdapter(RestaurantListFragment.context, category);
        latestAdapter = adapter;

        Server server = new Server(context, Server.GWANAK);
        server.updateCategory(category, adapter);

        View view = inflater.inflate(R.layout.list_view, container, false);
        ListView listView = (ListView)view.findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (latestAdapter.getItem(position) instanceof Restaurant) {
                    Intent intent = new Intent(context, MenuListActivity.class);
                    intent.putExtra("RESTAURANT", (Restaurant)latestAdapter.getItem(position));
                    intent.putExtra("REFERRER", "RestaurantListFragment");
                    startActivity(intent);
                }
            }
        });

        return view;
    }


}
