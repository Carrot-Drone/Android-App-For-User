package com.lchpatners.shadal.restaurant_correction;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.lchpatners.shadal.dao.RestaurantCorrection;
import com.lchpatners.shadal.util.Preferences;
import com.lchpatners.shadal.util.RetrofitConverter;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by eunhyekim on 2015. 8. 30..
 */
public class RestaurantCorrectionController {
    private static final String BASE_URL = "http://www.shadal.kr";

    public static void sendRestaurantCorrection(final Activity activity, final String major, final String details, final int restaurant_id) {
        RestaurantCorrection correction = new RestaurantCorrection();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(new RetrofitConverter().createBasicConverter()))
                .setEndpoint(BASE_URL) // The base API endpoint.
                .build();
        RestaurantCorrectionAPI correctionAPI = restAdapter.create(RestaurantCorrectionAPI.class);

        correction.setMajor_correction(major);
        correction.setDetails(details);
        correction.setUuid(Preferences.getDeviceUuid(activity));

        correctionAPI.sendRestaurantCorrection(restaurant_id, correction, new Callback<RestaurantCorrection>() {
            @Override
            public void success(RestaurantCorrection restaurantCorrection, Response response) {
                activity.finish();
                Intent intent = new Intent(activity, RCPopUpActivity.class);
                activity.startActivity(intent);
            }

            @Override
            public void failure(RetrofitError error) {
//                if (error.toString().contains("java.io.EOFException")) {
                    sendRestaurantCorrection(activity, major, details, restaurant_id);
                error.printStackTrace();
//                } else {
//                    Toast.makeText(activity, "실패했어요! 다시 시도해 주세요", Toast.LENGTH_LONG).show();
//                }
            }
        });
    }
}
