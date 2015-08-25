package com.lchpatners.shadal.restaurant;

import com.lchpatners.shadal.restaurant.category.Category;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by YoungKim on 2015. 8. 24..
 */
public interface RestaurantAPI {
    @GET("/campus/{campus_id}/restaurants")
    void getAllRestaurantList(@Path("campus_id") int campus_id, Callback<List<Category>> callback);
}
