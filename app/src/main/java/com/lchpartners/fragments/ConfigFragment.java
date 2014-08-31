package com.lchpartners.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lchpartners.android.adaptor.RestaurantsAdapter;
import com.lchpartners.shadal.R;

/**
 * Created by swchoi06 on 9/1/14.
 */
public class ConfigFragment extends Fragment {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView resultView = (ListView) inflater.inflate(R.layout.user_configs, container, false);

        return resultView;
    }
}