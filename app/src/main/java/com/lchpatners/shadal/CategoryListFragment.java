package com.lchpatners.shadal;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Guanadah on 2015-01-23.
 */
public class CategoryListFragment extends Fragment {

    private Activity activity;

    public static CategoryListFragment newInstance() {
        return new CategoryListFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            AnalyticsHelper helper = new AnalyticsHelper(activity.getApplication());
            helper.sendScreen(((MainActivity)getActivity()).restaurantListFragmentCurrentlyOn == null ?
                    "메인 화면" : "음식점 리스트 화면");
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
        View view = inflater.inflate(R.layout.list_view, container, false);
        ListView listView = (ListView)view.findViewById(R.id.list_view);
        final CategoryListAdapter adapter = new CategoryListAdapter(activity);
        listView.setAdapter(adapter);
        // known issue : duplicated onItemClick when double tapping
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String category = ((TextView)view.findViewById(R.id.category_text)).getText().toString();
                RestaurantListFragment rlfInstance = RestaurantListFragment.newInstance(category);
                ((MainActivity)getActivity()).restaurantListFragmentCurrentlyOn = rlfInstance;

                AnalyticsHelper helper = new AnalyticsHelper(getActivity().getApplication());
                helper.sendEvent("UX", "category_clicked", category);

                ((FragmentActivity)activity).getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .add(R.id.fragment_list_frame, rlfInstance)
                        .addToBackStack(null)
                        .commit();
            }
        });
        return view;
    }

}
