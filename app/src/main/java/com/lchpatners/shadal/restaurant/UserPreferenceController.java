package com.lchpatners.shadal.restaurant;

import com.lchpatners.shadal.util.RetrofitConverter;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by YoungKim on 2015. 8. 27..
 */
public class UserPreferenceController {
    private static final String BASE_URL = "http://www.shadal.kr";
    private static int GOOD = 1;
    private static int BAD = -1;

    public static void sendUserPreference(int restaurant_id, int preference, String uuid) {
        UserPreference userPreference = setUserPreference(preference, uuid);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(new RetrofitConverter().createBasicConverter()))
                .setEndpoint(BASE_URL) // The base API endpoint.
                .build();

        RestaurantAPI restaurantAPI = restAdapter.create(RestaurantAPI.class);

        restaurantAPI.sendUserEvaluation(restaurant_id, userPreference, new Callback<UserPreference>() {
            @Override
            public void success(UserPreference userPreference, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private static UserPreference setUserPreference(int preference, String uuid) {
        UserPreference userPreference = new UserPreference();
        if (preference == 1) {
            userPreference.setPreference(GOOD);
        } else if (preference == 0) {
            userPreference.setPreference(BAD);
        }
        userPreference.setUUID(uuid);

        return userPreference;
    }
}
