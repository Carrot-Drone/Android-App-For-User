package com.lchpatners.shadal.restaurant.category;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.lchpatners.shadal.R;
import com.lchpatners.shadal.restaurant.RestaurantActivity;

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

//        if (getUserVisibleHint()) {
//            AnalyticsHelper helper = new AnalyticsHelper(activity.getApplication());
//            helper.sendScreen(((MainActivity) getActivity()).restaurantListFragmentCurrentlyOn == null ?
//                    "메인 화면" : "음식점 리스트 화면");
//        }
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

        ListView listView = (ListView) view.findViewById(R.id.list_view);
        final CategoryListAdapter adapter = new CategoryListAdapter(mActivity);

//        emptyView.setText("문제가 생겼어요! \n 더 보기에 가서 캠퍼스를 다시 설정해주세요");
        listView.setAdapter(adapter);

        // TODO: KNOWN ISSUE: duplicated onItemClick() calls when double-tapping
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

                Intent intent = new Intent(getActivity(), RestaurantActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("category", category);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

        return view;
    }

}
