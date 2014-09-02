package com.lchpartners.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lchpartners.shadal.R;

import info.android.sqlite.helper.DatabaseHelper;

/**
 * Created by Gwangrae Kim on 2014-09-02.
 */
public class FavoriteFragment extends Fragment implements ActionBarUpdater {
    private boolean updateActionBarOnCreateView = false;
    private Activity mActivity;
    private DatabaseHelper mDBHelper;

    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = getActivity();
        mDBHelper = new DatabaseHelper(mActivity);
        if (updateActionBarOnCreateView)
            updateActionBar();
        ListView resultView = (ListView) inflater.inflate(R.layout.fragment_more, container, false);
        //mDBHelper.getFavoriteRestaurant();
        return resultView;
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