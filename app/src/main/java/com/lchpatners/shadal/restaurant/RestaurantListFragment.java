package com.lchpatners.shadal.restaurant;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.lchpatners.shadal.R;
import com.lchpatners.shadal.restaurant.category.Category;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by YoungKim on 2015. 8. 25..
 */
public class RestaurantListFragment extends Fragment {
    private static final int LIST_ALL = 0;
    ListView listView;
    ImageView onlyFlyer;
    ImageView onlyOpenRestaurant;
    private RestaurantListAdapter mRestaurantListAdapter;
    private int mCategoryNumber;
    private Activity mActivity;
    private boolean isChecked1 = false;
    private boolean isChecked2 = false;

    public RestaurantListFragment(int categoryNumber) {
        this.mCategoryNumber = categoryNumber;
    }

//    View.OnClickListener checkListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            if (view.getId() == R.id.check_is_open) {
//                isChecked1 = !isChecked1;
//            } else if (view.getId() == R.id.check_has_flyer) {
//                isChecked2 = !isChecked2;
//            }
//
//            if (isChecked1) { //checked isopen
//                onlyOpenRestaurant.setImageResource(R.drawable.icon_list_bar_check_box_selected);
//                if (isChecked2) { //checked isopen && checked hasflyer
//                    flag = 1;
//                    onlyFlyer.setImageResource(R.drawable.icon_list_bar_check_box_selected);
//                } else { //checked isopen && unchecked hasflyer
//                    flag = 2;
//                    onlyFlyer.setImageResource(R.drawable.icon_list_bar_check_box_normal);
//                }
//            } else {
//                onlyOpenRestaurant.setImageResource(R.drawable.icon_list_bar_check_box_normal);
//                if (isChecked2) { //unchecked isopen && checked hasflyer
//                    flag = 3;
//                    onlyFlyer.setImageResource(R.drawable.icon_list_bar_check_box_selected);
//                    RestaurantListAdapter adapter = new RestaurantListAdapter(mActivity, categoryId, flag);
//                    mRestaurantListAdapter = adapter;
//                    listView.setAdapter(adapter);
//                    Log.d("flag", "3");
//                } else { //unchecked isopen && unchecked hasflyer
//                    flag = 0;
//                    onlyFlyer.setImageResource(R.drawable.icon_list_bar_check_box_normal);
//                    RestaurantListAdapter adapter = new RestaurantListAdapter(mActivity, categoryId, flag);
//                    mRestaurantListAdapter = adapter;
//                    listView.setAdapter(adapter);
//                    Log.d("flag", "0");
//                }
//
//            }
//        }
//    };

    public static RestaurantListFragment newInstance(int categoryId) {
        RestaurantListFragment restaurantListFragment = new RestaurantListFragment(categoryId);

        // TODO : why I have to deliver this args like this?
//        Bundle args = new Bundle();
//        args.putInt("mCategoryNumber", categoryId);
//        rlf.setArguments(args);
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
//        //need when I use bundle
//        categoryId = getArguments().getInt("categoryId");
//        final String category = getArguments().getString("CATEGORY");

//        Server server = new Server(activity);
//        server.updateCategory(category);

        View view = inflater.inflate(R.layout.fragment_restaurant, container, false);

        onlyFlyer = (ImageView) view.findViewById(R.id.check_has_flyer);
        onlyOpenRestaurant = (ImageView) view.findViewById(R.id.check_is_open);
//        onlyFlyer.setOnClickListener(checkListener);
//        onlyOpenRestaurant.setOnClickListener(checkListener);

        listView = (ListView) view.findViewById(R.id.list_view);
        RelativeLayout empty = (RelativeLayout) view.findViewById(R.id.empty);
        listView.setEmptyView(empty);

        RestaurantListAdapter adapter = new RestaurantListAdapter(mActivity, getRestaurantList(mCategoryNumber, LIST_ALL));
        mRestaurantListAdapter = adapter;
        listView.setAdapter(adapter);

        return view;
    }

    private List<Restaurant> getRestaurantList(int categoryNumber, int flag) {
        List<Restaurant> restaurantList;

        Realm realm = Realm.getInstance(mActivity);
        realm.beginTransaction();

        RealmQuery<Category> categoryQuery = realm.where(Category.class);
        RealmResults<Category> categoryList = categoryQuery.findAll();
        switch (flag) {
            case LIST_ALL:
                restaurantList = categoryList.get(categoryNumber).getRestaurants();
                break;
            default:
                restaurantList = categoryList.get(categoryNumber).getRestaurants();
                break;
        }

        realm.commitTransaction();
        realm.close();

        return restaurantList;
    }
}
