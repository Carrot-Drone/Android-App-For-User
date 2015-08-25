package com.lchpatners.shadal.util.System;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by youngKim on 2015. 8. 21..
 */
public interface SystemApi {
    @GET("/minimum_app_version")
    void getMinimunAppVersion(Callback<System> callback);
}

