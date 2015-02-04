package com.lchpatners.shadal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Guanadah on 2015-01-24.
 */
public class RandomFragment extends Fragment {

    private static Context context;

    public static RandomFragment newInstance(Context context) {
        RandomFragment.context = context;
        return new RandomFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_random, container, false);
        view.findViewById(R.id.random_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Restaurant restaurant = DatabaseHelper.getInstance(context).getRandomRestaurant();
                Intent intent = new Intent(context, MenuListActivity.class);
                intent.putExtra("RESTAURANT", restaurant);
                intent.putExtra("REFERRER", "RandomFragment");
                startActivity(intent);
            }
        });
        return view;
    }
}
