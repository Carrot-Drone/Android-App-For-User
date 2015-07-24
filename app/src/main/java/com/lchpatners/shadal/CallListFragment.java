package com.lchpatners.shadal;

import android.app.Activity;
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
 * Displays bookmarks.
 */
public class CallListFragment extends Fragment {

    /**
     * The {@link Activity Activity} to which this attaches.
     */
    private Activity activity;
    /**
     * The lastly instantiated instance's {@link RestaurantListAdapter
     * RestaurantListAdapter}. Used to reload the view from another {@link Activity Activity}.
     */
    public static RestaurantListAdapter latestAdapter;

    public static CallListFragment newInstance() {
        return new CallListFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onResume() {
        super.onResume();

        /*
         * Without this condition check, there would be multiple onResume() calls of
         * all the Fragments in the parent ViewPager this and they belong. This is because
         * of the relationship of Fragment life cycle and Activity life cycle.
         */
//        if (getUserVisibleHint()) {
//            AnalyticsHelper helper = new AnalyticsHelper(activity.getApplication());
//            helper.sendScreen("즐겨찾기 화면");
//        }
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

        View view = inflater.inflate(R.layout.fragment_call, container, false);
        final ListView listView = (ListView)view.findViewById(R.id.call_list_view);

        // Create and set the empty view to show up when the list is empty.
        ImageView img = new ImageView(activity);
        img.setImageResource(R.drawable.bg_bookmarks_empty);
        img.setVisibility(View.GONE);
        ((ViewGroup)listView.getParent()).addView(img);
        listView.setEmptyView(img);

        final CallListAdapter adapter
                = new CallListAdapter(activity);

        adapter.notifyDataSetChanged();
        //latestAdapter = adapter;
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getItem(position) instanceof Restaurant) {
                    Restaurant restaurant = (Restaurant) adapter.getItem(position);

//                    AnalyticsHelper helper = new AnalyticsHelper(getActivity().getApplication());
//                    helper.sendEvent("UX", "res_in_favorite_clicked", restaurant.getName());

                    Intent intent = new Intent(activity, MenuListActivity.class);
                    intent.putExtra("RESTAURANT", restaurant);
                    intent.putExtra("REFERRER", "CallListFragment");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    adapter.notifyDataSetChanged();
                    listView.deferNotifyDataSetChanged();
                }
            }
        });
        return view;
    }

}
