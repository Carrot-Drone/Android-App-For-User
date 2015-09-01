package com.lchpatners.shadal.call;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lchpatners.shadal.R;
import com.lchpatners.shadal.util.AnalyticsHelper;

/**
 * Created by eunhyekim on 2015. 8. 22..
 */
public class RecentCallFragment extends Fragment {
    private Activity activity;
    private ListView listView;
    private RecentCallAdapter mAdapter;
    private String orderBy;

    private ImageView iv_call;
    private ImageView iv_name;
    View.OnClickListener orderListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.title_name) {
                mAdapter.loadData(RecentCallController.ORDER_BY_NAME);
                iv_name.setVisibility(View.VISIBLE);
                iv_call.setVisibility(View.INVISIBLE);

                AnalyticsHelper analyticsHelper = new AnalyticsHelper(activity);
                analyticsHelper.sendEvent("UX", "sort_by_name", "");
            } else if (view.getId() == R.id.title_call) {
                mAdapter.loadData(RecentCallController.ORDER_BY_CALL_RECENT);
                iv_name.setVisibility(View.INVISIBLE);
                iv_call.setVisibility(View.VISIBLE);

                AnalyticsHelper analyticsHelper = new AnalyticsHelper(activity);
                analyticsHelper.sendEvent("UX", "sort_by_recent_order", "");
            }
        }
    };

    public RecentCallFragment() {
        this.orderBy = RecentCallController.ORDER_BY_CALL_RECENT;
    }

    public static RecentCallFragment newInstance() {
        return new RecentCallFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        //default is ORDER_BY_RECENT
        this.mAdapter = new RecentCallAdapter(activity, RecentCallController.ORDER_BY_CALL_RECENT);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.loadData(RecentCallController.ORDER_BY_CALL_RECENT);
        if (getUserVisibleHint()) {
            AnalyticsHelper analyticsHelper = new AnalyticsHelper(activity);
            analyticsHelper.sendScreen("최근주문 화면");
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
        View view = inflater.inflate(R.layout.fragment_recent_call, container, false);

        listView = (ListView) view.findViewById(R.id.call_list_view);
        TextView textView = (TextView) view.findViewById(android.R.id.empty);
        listView.setEmptyView(textView);

        TextView name = (TextView) view.findViewById(R.id.title_name);
        TextView call = (TextView) view.findViewById(R.id.title_call);

        iv_name = (ImageView) view.findViewById(R.id.iv_name_order);
        iv_call = (ImageView) view.findViewById(R.id.iv_order);

        name.setOnClickListener(orderListener);
        call.setOnClickListener(orderListener);

        listView.setAdapter(mAdapter);
        return view;
    }
}
