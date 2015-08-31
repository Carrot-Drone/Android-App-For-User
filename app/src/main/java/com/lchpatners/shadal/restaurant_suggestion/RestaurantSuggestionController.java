package com.lchpatners.shadal.restaurant_suggestion;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.lchpatners.shadal.util.RetrofitConverter;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by eunhyekim on 2015. 8. 31..
 */
public class RestaurantSuggestionController {

    private static final String BASE_URL = "http://www.shadal.kr:3000";

    public static void sendRestaurantSuggestion(final Activity activity, final RestaurantSuggestion restaurantSuggestion) {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(new RetrofitConverter().createBasicConverter()))
                .setEndpoint(BASE_URL) // The base API endpoint.
                .build();

        RestaurantSuggestionAPI restaurantSuggestionAPI = restAdapter.create(RestaurantSuggestionAPI.class);

        restaurantSuggestionAPI.sendRestaurantSuggestion(restaurantSuggestion, new Callback<RestaurantSuggestion>() {
            @Override
            public void success(RestaurantSuggestion rs, Response response) {

                if (restaurantSuggestion.getIs_suggested_by_restaurant() == 1) {
                    Intent intent = new Intent(activity, RSbyOwnerPopUpActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                } else {
                    Intent intent = new Intent(activity, RSbyUserPopUpActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(activity, "실패했습니다. 다시 시도해주세요!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
