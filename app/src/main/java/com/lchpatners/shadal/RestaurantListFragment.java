package com.lchpatners.shadal;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Displays the restaurants of a category, or from the bookmarks.
 */
public class RestaurantListFragment extends Fragment {

    /**
     * The lastly instantiated instance of
     * {@link com.lchpatners.shadal.RestaurantListAdapter
     * RestaurantListAdapter}
     */
    public static RestaurantListAdapter latestAdapter;

    /**
     * The {@link android.app.Activity Activity} to which this attaches.
     */
    private Activity activity;

    public static RestaurantListFragment newInstance(int categoryId) {
        RestaurantListFragment rlf = new RestaurantListFragment();
        Bundle args = new Bundle();
        args.putInt("categoryId", categoryId);

        rlf.setArguments(args);
        return rlf;
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
//            AnalyticsHelper helper = new AnalyticsHelper(activity.getApplication());
//            helper.sendScreen("음식점 리스트 화면");
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
//            AnalyticsHelper helper = new AnalyticsHelper(activity.getApplication());
//            helper.sendScreen("음식점 리스트 화면");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("RestaurantFragment", "called");
        int categoryId = getArguments().getInt("categoryId");
//        final String category = getArguments().getString("CATEGORY");
        RestaurantListAdapter adapter = new RestaurantListAdapter(activity, categoryId);
        latestAdapter = adapter;
//        Server server = new Server(activity);
//        server.updateCategory(category);

        View view = inflater.inflate(R.layout.list_view, container, false);
        ListView listView = (ListView)view.findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        return view;
    }


}
