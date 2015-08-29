package com.lchpatners.shadal.recommend;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.lchpatners.shadal.R;
import com.lchpatners.shadal.campus.Campus;
import com.lchpatners.shadal.restaurant.Restaurant;
import com.lchpatners.shadal.restaurant.category.Category;
import com.lchpatners.shadal.util.LogUtils;
import com.lchpatners.shadal.util.RetrofitConverter;

import java.util.ArrayList;
import java.util.List;

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
        setCampus();
    }

    public static void changeCurrentPage(int i) {
        verticalViewPager.setCurrentItem(verticalViewPager.getCurrentItem() + i, true);
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
        final ArrayList<RecommendedRestaurant> restaurants = getRecommendedRestaurants(recommendedRestaurantList);
        verticalViewPager = (VerticalViewPager) mActivity.findViewById(R.id.recommend_view_pager);

//        viewPager = (ViewPager) mActivity.findViewById(R.id.recommend_view_pager);
        adapter = new RecommendedRestaurantPagerAdapter(mFragmentManager, mActivity, restaurants);
        verticalViewPager.setAdapter(adapter);
        final int count = restaurants.size();
        verticalViewPager.setCurrentItem(count / 2);
        verticalViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    RecommendedRestaurantFragment.setUpButtonInvisible();
                } else if (position == count - 1) {
                    RecommendedRestaurantFragment.setDownButtonInvisible();
                    Log.d(TAG, "onPageselected ");
                }
            /*
                if (position < count)
                    verticalViewPager.setCurrentItem(position + count, false);
                else if (position >= count * 2) {
                    verticalViewPager.setCurrentItem(position - count, false);
                }*/
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private ArrayList<RecommendedRestaurant> getRecommendedRestaurants(RecommendedRestaurantList recommendedRestaurantList) {
        ArrayList<RecommendedRestaurant> recommendedRestaurants = new ArrayList<>();
        RecommendedRestaurant recommendedRestaurant;

        List<RecommendedRestaurantInfo> newRestaurant = recommendedRestaurantList.getNewRestaurant();
        List<RecommendedRestaurantInfo> trendRestaurant = recommendedRestaurantList.getTrendRestaraurant();

        Restaurant restaurant;
        String reason;

        for (int i = 0; i < newRestaurant.size(); i++) {

            Log.i(TAG, getRecommendedRestaurant(newRestaurant.get(i).getId()).getName());
            recommendedRestaurant = new RecommendedRestaurant();
            reason = newRestaurant.get(i).getReason();
            restaurant = getRecommendedRestaurant(newRestaurant.get(i).getId());

            recommendedRestaurant.setReason(reason);
            recommendedRestaurant.setRestaurant(restaurant);
            recommendedRestaurants.add(recommendedRestaurant);

            Log.i(TAG, getRecommendedRestaurant(trendRestaurant.get(i).getId()).getName());
            recommendedRestaurant = new RecommendedRestaurant();
            reason = trendRestaurant.get(i).getReason();
            restaurant = getRecommendedRestaurant(trendRestaurant.get(i).getId());

            recommendedRestaurant.setReason(reason);
            recommendedRestaurant.setRestaurant(restaurant);
            recommendedRestaurants.add(recommendedRestaurant);
        }
        for (int i = 0; i < recommendedRestaurants.size(); i++) {
            Log.d(TAG, recommendedRestaurants.get(i).getRestaurant().getName());
            Log.d(TAG, recommendedRestaurants.get(i).getReason());
        }
        return recommendedRestaurants;
    }

    public void setCampus() {
        Campus currentCampus = new Campus();
        Realm realm = Realm.getInstance(mActivity);
        try {
            realm.beginTransaction();
            RealmQuery<Campus> query = realm.where(Campus.class);
            currentCampus = query.findFirst();
            realm.commitTransaction();
        } catch (Exception e) {
            realm.cancelTransaction();
        } finally {
            realm.close();
        }
        mCampus = currentCampus;
    }

    public Campus getCampus() {
        return mCampus;
    }

    public Restaurant getRecommendedRestaurant(int id) {
        Realm realm = Realm.getInstance(mActivity);
        Restaurant restaurant = new Restaurant();
        try {
            realm.beginTransaction();
            RealmQuery<Restaurant> query = realm.where(Restaurant.class).equalTo("id", id);
            restaurant = query.findFirst();
            realm.commitTransaction();
        } catch (Exception e) {
            realm.cancelTransaction();
        } finally {
            realm.close();
        }
        return restaurant;
    }

}
