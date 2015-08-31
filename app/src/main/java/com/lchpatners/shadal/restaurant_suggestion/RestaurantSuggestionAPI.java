package com.lchpatners.shadal.restaurant_suggestion;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by eunhyekim on 2015. 8. 31..
 */
public interface RestaurantSuggestionAPI {

    @POST("/restaurant_suggestion")
    void sendRestaurantSuggestion(@Body RestaurantSuggestion restaurantSuggestion, Callback<RestaurantSuggestion> callback);
}
