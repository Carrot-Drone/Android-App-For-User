package com.lchpartners.fragments;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lchpartners.fragments.RestaurantsFragment.RestaurantsAdapter;
import com.lchpartners.shadal.MainActivity;
import com.lchpartners.shadal.R;

import info.android.sqlite.helper.DatabaseHelper;

/**
 * Created by Gwangrae Kim on 2014-09-02.
 */
public class FavoriteFragment extends Fragment implements ActionBarUpdater {
    private boolean updateActionBarOnCreateView = false;
    private MainActivity mActivity;

    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (MainActivity) getActivity();
        DatabaseHelper dbHelper = new DatabaseHelper(mActivity);
        RestaurantsAdapter adapter = new RestaurantsAdapter(mActivity, dbHelper.getFavoriteRestaurant()
                                                          , MainActivity.TAB_FAVORITE);
        dbHelper.closeDB();

        if (updateActionBarOnCreateView)
            updateActionBar();
        ListView resultView = (ListView) inflater.inflate(R.layout.fragment_restaurant, container, false);
        resultView.setAdapter(adapter);
        return resultView;
    }

    public void invalidate() {
        DatabaseHelper dbHelper = new DatabaseHelper(mActivity);
        RestaurantsAdapter adapter = new RestaurantsAdapter(mActivity, dbHelper.getFavoriteRestaurant()
                , MainActivity.TAB_FAVORITE);
        dbHelper.closeDB();

        ListView resultView = (ListView) getView();
        resultView.setAdapter(adapter);
    }

    public void setUpdateActionBarOnCreateView() {
        this.updateActionBarOnCreateView = true;
    }
    public void updateActionBar () {
        ActionBar actionBar = mActivity.getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        ViewGroup titleBar = (ViewGroup) mActivity.getLayoutInflater().inflate(R.layout.action_bar_favorite, null);
        titleBar.setLayoutParams(actionBar.getCustomView().getLayoutParams());

        actionBar.setCustomView(titleBar);
        mActivity.invalidateOptionsMenu();
    }

}