package com.lchpatners.shadal.restaurant_correction;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by eunhyekim on 2015. 8. 30..
 */
public interface RestaurantCorrectionAPI {

    @POST("/restaurant/{restaurant_id}/restaurant_correction")
    void sendRestaurantCorrection(@Path("restaurant_id") int restaurant_id, @Body RestaurantCorrection restaurantCorrection, Callback<RestaurantCorrection> callback);
}
