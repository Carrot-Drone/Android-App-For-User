package com.lchpatners.shadal.request;

import com.lchpatners.shadal.dao.Request;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by eunhyekim on 2015. 8. 30..
 */
public interface RequestAPI {
    @POST("/user_request")
    void sendUserRequest(@Body Request request, Callback<Request> callback);
}
