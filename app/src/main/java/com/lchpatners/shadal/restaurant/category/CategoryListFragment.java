package com.lchpatners.shadal.restaurant.category;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lchpatners.shadal.R;
import com.lchpatners.shadal.restaurant.RestaurantListActivity;
import com.lchpatners.shadal.util.AnalyticsHelper;

/**
 * Created by youngkim on 2015. 8. 25..
 */
public class CategoryListFragment extends Fragment {
    private Activity mActivity;

    public static CategoryListFragment newInstance() {
        CategoryListFragment categoryListFragment = new CategoryListFragment();
        return categoryListFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            AnalyticsHelper analyticsHelper = new AnalyticsHelper(mActivity);
            analyticsHelper.sendScreen("카테고리 화면");
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
        final CategoryListAdapter adapter = new CategoryListAdapter(mActivity);

        ListView listView = (ListView) view.findViewById(R.id.list_view);
        FrameLayout listFooterView = (FrameLayout) inflater.inflate(
                R.layout.category_list_footer, null);
        listView.addFooterView(listFooterView);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Instantiate a RestaurantListFragment instance of the chosen category.
                String category = ((TextView) view.findViewById(R.id.category_text)).getText().toString();

//                RestaurantListFragment rlfInstance = RestaurantListFragment.newInstance(position);
//                ((RootActivity) mActivity).restaurantListFragmentCurrentlyOn = rlfInstance;
//
//                AnalyticsHelper helper = new AnalyticsHelper(getActivity().getApplication());
//                helper.sendEvent("UX", "category_clicked", category);
//
//                // Add the fragment with custom animations.
//                ((MainActivity)activity).getSupportFragmentManager()
//                        .beginTransaction()
//                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
//                        .add(R.id.fragment_list_frame, rlfInstance)
//                        .addToBackStack(null)
//                        .commit();

                Intent intent = new Intent(getActivity(), RestaurantListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("category", category);
                intent.putExtra("position", position);
                startActivity(intent);

                AnalyticsHelper analyticsHelper = new AnalyticsHelper(mActivity);
                analyticsHelper.sendEvent("UX", "category_in_categories_clicked", category);
            }
        });

        return view;
    }

}
