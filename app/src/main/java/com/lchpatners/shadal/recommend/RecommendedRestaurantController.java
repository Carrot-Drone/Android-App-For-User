package com.lchpatners.shadal.recommend;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.lchpatners.shadal.R;
import com.lchpatners.shadal.campus.Campus;
import com.lchpatners.shadal.campus.CampusController;
import com.lchpatners.shadal.restaurant.Restaurant;
import com.lchpatners.shadal.restaurant.RestaurantController;
import com.lchpatners.shadal.restaurant.category.Category;
import com.lchpatners.shadal.util.LogUtils;
import com.lchpatners.shadal.util.RetrofitConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import io.realm.Realm;
import io.realm.RealmQuery;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by eunhyekim on 2015. 8. 26..
 */
public class RecommendedRestaurantController {
    private static final String TAG = LogUtils.makeTag(RecommendedRestaurantController.class);
    private static final String BASE_URL = "http://www.shadal.kr:3000";
    private static VerticalViewPager verticalViewPager;
    private RecommendedRestaurantPagerAdapter adapter;
    private Activity mActivity;
    private RecommendedRestaurantAPI recommendedRestaurantAPI;
    private FragmentManager mFragmentManager;
    private Campus mCampus;

    public RecommendedRestaurantController(Activity activity, FragmentManager fm) {
        mActivity = activity;
        mFragmentManager = fm;
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(new RetrofitConverter().createBasicConverter()))
                .setEndpoint(BASE_URL) // The base API endpoint.
                .build();

        recommendedRestaurantAPI = restAdapter.create(RecommendedRestaurantAPI.class);
        mCampus = CampusController.getCurrentCampus(activity);
    }

    public static String getCategoryTitle(Context context, int restaurant_id) {
        String category_title = "";
        Realm realm = Realm.getInstance(context);
        try {
            RealmQuery<Category> query = realm.where(Category.class).equalTo("restaurants.id", restaurant_id);
            Category category = query.findFirst();
            category_title = category.getTitle();
            realm.commitTransaction();
        } catch (Exception e) {
            realm.cancelTransaction();
        } finally {
            realm.close();
        }
        return category_title;
    }

    public static int getCategoryPosition(String category_title) {
        int position;
        switch (category_title) {
            case "치킨":
                position = 0;
                break;
            case "피자":
                position = 1;
                break;
            case "중국집":
                position = 2;
                break;
            case "한식/분식":
                position = 3;
                break;
            case "도시락/돈까스":
                position = 4;
                break;
            case "족발/보쌈":
                position = 5;
                break;
            case "냉면":
                position = 6;
                break;
            default:
                position = 7;
                break;
        }
        return position;
    }

    public static void changeCurrentPage(int i) {
        verticalViewPager.setCurrentItem(verticalViewPager.getCurrentItem() + i, true);
    }

    public void getRecommendedRestaurantListFromServer() {
        recommendedRestaurantAPI.getRecommendedRestaurants(mCampus.getId(), new Callback<RecommendedRestaurantList>() {
            @Override
            public void success(RecommendedRestaurantList recommendedRestaurantList, Response response) {
                fillRecommendedRestaurantView(recommendedRestaurantList);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                Log.e(TAG, error.toString());
            }
        });
    }

    private void fillRecommendedRestaurantView(RecommendedRestaurantList recommendedRestaurantList) {
        final List<RecommendedRestaurant> restaurants = getRecommendedRestaurants(recommendedRestaurantList);

        verticalViewPager = (VerticalViewPager) mActivity.findViewById(R.id.recommend_view_pager);
        adapter = new RecommendedRestaurantPagerAdapter(mFragmentManager, mActivity, restaurants);
        verticalViewPager.setAdapter(adapter);

        verticalViewPager.setOffscreenPageLimit(0);
        verticalViewPager.setCurrentItem(restaurants.size() / 2);
        verticalViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                if (position == 0) {
//                    RecommendedRestaurantFragment.setUpButtonInvisible();
//                } else if (position == count) {
//                    RecommendedRestaurantFragment.setDownButtonInvisible();
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private List<RecommendedRestaurant> getRecommendedRestaurants(RecommendedRestaurantList recommendedRestaurantList) {
        List<RecommendedRestaurant> recommendedRestaurants = new ArrayList<>();

        List<RecommendedRestaurantInfo> newRestaurant = recommendedRestaurantList.getNewRestaurant();
        List<RecommendedRestaurantInfo> trendRestaurant = recommendedRestaurantList.getTrendRestaurant();

        for (int i = 0; i < newRestaurant.size(); i++) {
            Restaurant restaurant;
            String reason;

            RecommendedRestaurant recommendedRestaurant1 = new RecommendedRestaurant();
            reason = newRestaurant.get(i).getReason();
            restaurant = RestaurantController.getRestaurant(mActivity, newRestaurant.get(i).getId());

            recommendedRestaurant1.setReason(reason);
            recommendedRestaurant1.setRestaurant(restaurant);
            recommendedRestaurants.add(recommendedRestaurant1);
        }
        for (int i = 0; i < trendRestaurant.size(); i++) {
            Restaurant restaurant;
            String reason;

            RecommendedRestaurant recommendedRestaurant2 = new RecommendedRestaurant();
            reason = trendRestaurant.get(i).getReason();
            restaurant = RestaurantController.getRestaurant(mActivity, trendRestaurant.get(i).getId());

            recommendedRestaurant2.setReason(reason);
            recommendedRestaurant2.setRestaurant(restaurant);
            recommendedRestaurants.add(recommendedRestaurant2);
        }

        long seed = System.nanoTime();
        Collections.shuffle(recommendedRestaurants, new Random(seed));

        return recommendedRestaurants;
    }

    public Campus getCampus() {
        return mCampus;
    }
}
