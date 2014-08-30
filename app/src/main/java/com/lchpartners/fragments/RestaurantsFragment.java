package com.lchpartners.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lchpartners.android.adaptor.RestaurantsAdapter;
import com.lchpartners.shadal.R;
import com.lchpartners.views.NamsanTextView;

import java.util.ArrayList;

import info.android.sqlite.helper.DatabaseHelper;
import info.android.sqlite.model.Restaurant;

/**
 * Created by Gwangrae Kim on 2014-08-30.
 */
public class RestaurantsFragment extends Fragment {
    private RestaurantsAdapter adapter;
    private ArrayList<Restaurant> mResults = new ArrayList<Restaurant>();
    private DatabaseHelper db;
    private String mCategoryName;
    private Activity mActivity;

    public RestaurantsFragment () {
        super();
    }

    public RestaurantsFragment (int categoryIndex) {
        super();
        mActivity = getActivity();
        mCategoryName = getResources().getStringArray(R.array.categories)[categoryIndex];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView resultView = (ListView) inflater.inflate(R.layout.activity_restaurant, container, false);

        //Setting up the action bar
        ActionBar actionBar = mActivity.getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        ViewGroup titleBar = (ViewGroup) inflater.inflate(R.layout.action_bar_restaurant, null);
        titleBar.setLayoutParams(actionBar.getCustomView().getLayoutParams());
        NamsanTextView title = (NamsanTextView) titleBar.findViewById(R.id.textview_restaurant_title);
        title.setText(mCategoryName);
        actionBar.setCustomView(titleBar);

        //Query database
        db = new DatabaseHelper(mActivity);
        mResults = db.getAllRestaurantsWithCategory(mCategoryName);

        adapter = new RestaurantsAdapter(mActivity, mResults);
        resultView.setAdapter(adapter);

        return resultView;
    }



}
