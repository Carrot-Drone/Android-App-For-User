package com.lchpatners.shadal.restaurant;

import com.lchpatners.shadal.restaurant.category.Category;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by YoungKim on 2015. 8. 24..
 */
public interface RestaurantAPI {
    @GET("/campus/{campus_id}/restaurants")
    void getAllRestaurantList(@Path("campus_id") int campus_id, Callback<List<Category>> callback);

    @GET("/restaurant/{restaurant_id}")
    void getRestaurant(@Path("restaurant_id") int restaurant_id, Callback<Restaurant> callback);

    @POST("/restaurant/{restaurant_id}/preference")
    void sendUserEvaluation(@Path("restaurant_id") int restaurant_id, @Body UserPreference userPreference, Callback<UserPreference> callback);

//    @GET("/restaurant/{restaurant_id}")
//    Restaurant getRestaurant(@Path("restaurant_id") int restaurant_id);
}
