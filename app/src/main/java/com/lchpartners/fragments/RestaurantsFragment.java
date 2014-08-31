package com.lchpartners.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lchpartners.android.adaptor.RestaurantsAdapter;
import com.lchpartners.apphelper.server.Server;
import com.lchpartners.shadal.R;
import com.lchpartners.views.NamsanTextView;

import java.util.ArrayList;

import info.android.sqlite.helper.DatabaseHelper;
import info.android.sqlite.model.Restaurant;

/**
 * Created by Gwangrae Kim on 2014-08-30.
 */
public class RestaurantsFragment extends Fragment implements ActionBarUpdater {
    private final static String EXTRA_CATEGORY_IDX = "catIdx";
    private final static String TAG = "CategoryFragment";

    public static RestaurantsFragment newInstance(int categoryIndex) {
        RestaurantsFragment rf = new RestaurantsFragment();
        Bundle bdl = new Bundle(1);
        bdl.putInt(EXTRA_CATEGORY_IDX, categoryIndex);
        rf.setArguments(bdl);
        return rf;
    }

    private RestaurantsAdapter adapter;
    private ArrayList<Restaurant> mResults = new ArrayList<Restaurant>();
    private DatabaseHelper db;
    private String mCategoryName;
    private Activity mActivity;

    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int categoryIndex = getArguments().getInt(EXTRA_CATEGORY_IDX);
        this.mCategoryName = getResources().getStringArray(R.array.categories)[categoryIndex];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mActivity = getActivity();
        ListView resultView = (ListView) inflater.inflate(R.layout.activity_restaurant, container, false);

        //Query database
        db = new DatabaseHelper(mActivity);
        mResults = db.getAllRestaurantsWithCategory(mCategoryName);

        adapter = new RestaurantsAdapter(mActivity, mResults);
        resultView.setAdapter(adapter);
        db.closeDB();


        // check for update
        Server server = new Server(this.getActivity());
        server.updateRestaurantInCategory(mCategoryName, adapter);

        return resultView;
    }

    public void updateActionBar () {
        ActionBar actionBar = mActivity.getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        ViewGroup titleBar = (ViewGroup) mActivity.getLayoutInflater().inflate(R.layout.action_bar_restaurant, null);
        titleBar.setLayoutParams(actionBar.getCustomView().getLayoutParams());
        NamsanTextView title = (NamsanTextView) titleBar.findViewById(R.id.textview_restaurant_title);
        title.setText(mCategoryName);
        actionBar.setCustomView(titleBar);
        mActivity.invalidateOptionsMenu();
    }

}
