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
import android.widget.TextView;

/**
 * Displays bookmarks.
 */
public class CallListFragment extends Fragment {

    /**
     * The {@link Activity Activity} to which this attaches.
     */
    private Activity activity;
    private ListView listView;
    private CallListAdapter adapter;

    private ImageView iv_call;
    private ImageView iv_name;
    View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (view.getId() == R.id.title_name) {
                CallListAdapter adapter = new CallListAdapter(activity, DatabaseHelper.NAME);
                listView.setAdapter(adapter);

                iv_name.setVisibility(View.VISIBLE);
                iv_call.setVisibility(View.INVISIBLE);

            } else if (view.getId() == R.id.title_call) {
                CallListAdapter adapter = new CallListAdapter(activity, DatabaseHelper.CALL);
                listView.setAdapter(adapter);

                iv_name.setVisibility(View.INVISIBLE);
                iv_call.setVisibility(View.VISIBLE);

            }
        }
    };

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
        listView = (ListView) view.findViewById(R.id.call_list_view);
        TextView textView = (TextView) view.findViewById(android.R.id.empty);
        listView.setEmptyView(textView);

        TextView name = (TextView) view.findViewById(R.id.title_name);
        TextView call = (TextView) view.findViewById(R.id.title_call);

        iv_name = (ImageView) view.findViewById(R.id.iv_name_order);
        iv_call = (ImageView) view.findViewById(R.id.iv_order);

        name.setOnClickListener(btnListener);
        call.setOnClickListener(btnListener);

        adapter = new CallListAdapter(activity, DatabaseHelper.CALL);

        adapter.notifyDataSetChanged();

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
                    startActivity(intent);
                    adapter.notifyDataSetChanged();
                    listView.deferNotifyDataSetChanged();
                }
            }
        });
        return view;
    }
}
