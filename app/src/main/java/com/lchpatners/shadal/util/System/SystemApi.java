package com.lchpatners.shadal.util.System;

import com.lchpatners.shadal.dao.*;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by youngKim on 2015. 8. 21..
 */
public interface SystemAPI {
    @GET("/minimum_app_version")
    void getMinimunAppVersion(Callback<com.lchpatners.shadal.dao.System> callback);

    @POST("/device")
    void sendDeviceInfo(@Body Device device, Callback<Device> callback);
}

