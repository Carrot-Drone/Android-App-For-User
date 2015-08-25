package com.lchpatners.shadal.call;

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

import com.lchpatners.shadal.CallListAdapter;
import com.lchpatners.shadal.DatabaseHelper;
import com.lchpatners.shadal.restaurant.menu.MenuListActivity;
import com.lchpatners.shadal.R;
import com.lchpatners.shadal.Restaurant;

/**
 * Created by eunhyekim on 2015. 8. 22..
 */
public class RecentCallFragment extends Fragment {
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

    public static RecentCallFragment newInstance() {
        return new RecentCallFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onResume() {
        super.onResume();
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

        View view = inflater.inflate(R.layout.fragment_recent_call, container, false);

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
