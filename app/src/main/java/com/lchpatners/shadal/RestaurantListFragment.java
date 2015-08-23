package com.lchpatners.shadal;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
    boolean isChecked1 = false;
    boolean isChecked2 = false;
    int flag = 0;
    int categoryId;
    ListView listView;
    ImageView checkBoxFlyer;
    ImageView checkBoxOpen;
    TextView textViewFlyer;
    TextView textViewOpen;
    /**
     * The {@link android.app.Activity Activity} to which this attaches.
     */
    private Activity activity;
    View.OnClickListener checkListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.check_is_open || view.getId() == R.id.tv_open) {
                isChecked1 = !isChecked1;
            } else if (view.getId() == R.id.check_has_flyer || view.getId() == R.id.tv_flyer) {
                isChecked2 = !isChecked2;
            }

            if (isChecked1) { //checked isopen
                checkBoxOpen.setImageResource(R.drawable.icon_list_bar_check_box_selected);
                if (isChecked2) { //checked isopen && checked hasflyer
                    flag = 1;
                    checkBoxFlyer.setImageResource(R.drawable.icon_list_bar_check_box_selected);
                } else { //checked isopen && unchecked hasflyer
                    flag = 2;
                    checkBoxFlyer.setImageResource(R.drawable.icon_list_bar_check_box_normal);
                }
            } else {
                checkBoxOpen.setImageResource(R.drawable.icon_list_bar_check_box_normal);
                if (isChecked2) { //unchecked isopen && checked hasflyer
                    flag = 3;
                    checkBoxFlyer.setImageResource(R.drawable.icon_list_bar_check_box_selected);
                    RestaurantListAdapter adapter = new RestaurantListAdapter(activity, categoryId, flag);
                    latestAdapter = adapter;
                    listView.setAdapter(adapter);
                    Log.d("flag", "3");
                } else { //unchecked isopen && unchecked hasflyer
                    flag = 0;
                    checkBoxFlyer.setImageResource(R.drawable.icon_list_bar_check_box_normal);
                    RestaurantListAdapter adapter = new RestaurantListAdapter(activity, categoryId, flag);
                    latestAdapter = adapter;
                    listView.setAdapter(adapter);
                    Log.d("flag", "0");
                }

            }
        }
    };

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
        categoryId = getArguments().getInt("categoryId");
//        final String category = getArguments().getString("CATEGORY");

//        Server server = new Server(activity);
//        server.updateCategory(category);

        View view = inflater.inflate(R.layout.fragment_restaurant, container, false);

        checkBoxFlyer = (ImageView) view.findViewById(R.id.check_has_flyer);
        checkBoxOpen = (ImageView) view.findViewById(R.id.check_is_open);
        textViewFlyer = (TextView) view.findViewById(R.id.tv_flyer);
        textViewOpen = (TextView) view.findViewById(R.id.tv_open);

        checkBoxFlyer.setOnClickListener(checkListener);
        checkBoxOpen.setOnClickListener(checkListener);
        textViewOpen.setOnClickListener(checkListener);
        textViewFlyer.setOnClickListener(checkListener);

        listView = (ListView) view.findViewById(R.id.list_view);
        TextView empty = (TextView) view.findViewById(R.id.empty);
        listView.setEmptyView(empty);
        RestaurantListAdapter adapter = new RestaurantListAdapter(activity, categoryId, flag);
        latestAdapter = adapter;
        listView.setAdapter(adapter);
        return view;
    }

}
