package com.lchpatners.shadal.restaurant;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.lchpatners.shadal.R;

/**
 * Created by YoungKim on 2015. 8. 25..
 */
public class RestaurantListFragment extends Fragment {
    private static final int LIST_ALL = 0;
    ListView listView;

    ImageView onlyFlyer;
    ImageView onlyOpen;

    private RestaurantListAdapter mAdapter;
    private Activity mActivity;
    private boolean isCheckedOfficeHour = false;
    private boolean isCheckedHasFlyer = false;
    private String orderBy;

    public RestaurantListFragment() {
        this.isCheckedOfficeHour = RestaurantController.officeHour;
        this.orderBy = RestaurantController.LIST_ALL;
    }

    public static RestaurantListFragment newInstance(int categoryNumber) {
        RestaurantListFragment restaurantListFragment = new RestaurantListFragment();

        // TODO : why I have to deliver this args like this?
        Bundle args = new Bundle();
        args.putInt("mCategoryNumber", categoryNumber);
        restaurantListFragment.setArguments(args);

        return restaurantListFragment;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    @Override
    public void onResume() {
        super.onResume();
//        getCheckBox();
        mAdapter.loadData(orderBy);
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
        View view = inflater.inflate(R.layout.fragment_restaurant, container, false);

        listView = (ListView) view.findViewById(R.id.list_view);
        RelativeLayout empty = (RelativeLayout) view.findViewById(R.id.empty);
        listView.setEmptyView(empty);

        onlyFlyer = (ImageView) view.findViewById(R.id.check_has_flyer);
        onlyOpen = (ImageView) view.findViewById(R.id.check_is_open);
        onlyFlyer.setOnClickListener(checkListener);
        onlyOpen.setOnClickListener(checkListener);

        getCheckBox();

        //need when I use bundle
        int mCategoryNumber = getArguments().getInt("mCategoryNumber");
        this.mAdapter = new RestaurantListAdapter(mActivity, mCategoryNumber, orderBy);

        listView.setAdapter(mAdapter);

        return view;
    }

    public void getCheckBox() {
        isCheckedOfficeHour = RestaurantController.officeHour;
        if (isCheckedOfficeHour) {
            orderBy = RestaurantController.LIST_OFFICE_HOUR;
            onlyOpen.setImageResource(R.drawable.icon_list_bar_check_box_selected);
        } else {
            onlyOpen.setImageResource(R.drawable.icon_list_bar_check_box_normal);
        }
    }

    View.OnClickListener checkListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.check_is_open) {
                isCheckedOfficeHour = !isCheckedOfficeHour;
            } else if (view.getId() == R.id.check_has_flyer) {
                isCheckedHasFlyer = !isCheckedHasFlyer;
            }

            if (isCheckedOfficeHour && isCheckedHasFlyer) {
                RestaurantController.officeHour = true;
                orderBy = RestaurantController.LIST_FLYER_OFFICE;
                mAdapter.loadData(RestaurantController.LIST_FLYER_OFFICE);
                onlyOpen.setImageResource(R.drawable.icon_list_bar_check_box_selected);
                onlyFlyer.setImageResource(R.drawable.icon_list_bar_check_box_selected);
            } else if (isCheckedOfficeHour) {
                RestaurantController.officeHour = true;
                orderBy = RestaurantController.LIST_OFFICE_HOUR;
                mAdapter.loadData(RestaurantController.LIST_OFFICE_HOUR);
                onlyOpen.setImageResource(R.drawable.icon_list_bar_check_box_selected);
                onlyFlyer.setImageResource(R.drawable.icon_list_bar_check_box_normal);
            } else if (isCheckedHasFlyer) {
                RestaurantController.officeHour = false;
                orderBy = RestaurantController.LIST_HAS_FLYER;
                mAdapter.loadData(RestaurantController.LIST_HAS_FLYER);
                onlyOpen.setImageResource(R.drawable.icon_list_bar_check_box_normal);
                onlyFlyer.setImageResource(R.drawable.icon_list_bar_check_box_selected);
            } else {
                RestaurantController.officeHour = false;
                orderBy = RestaurantController.LIST_ALL;
                mAdapter.loadData(RestaurantController.LIST_ALL);
                onlyOpen.setImageResource(R.drawable.icon_list_bar_check_box_normal);
                onlyFlyer.setImageResource(R.drawable.icon_list_bar_check_box_normal);
            }
        }
    };
}
