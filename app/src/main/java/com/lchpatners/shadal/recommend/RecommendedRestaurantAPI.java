package com.lchpatners.shadal.recommend;

import com.lchpatners.shadal.dao.RecommendedRestaurantList;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by eunhyekim on 2015. 8. 26..
 */
public interface RecommendedRestaurantAPI {
    @GET("/campus/{campus_id}/recommended_restaurants")
    void getRecommendedRestaurants(@Path("campus_id") int campus_id, Callback<RecommendedRestaurantList> callback);
}
