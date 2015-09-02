package com.lchpatners.shadal.call.CallLog;

import com.lchpatners.shadal.dao.CallLog;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by YoungKim on 2015. 8. 27..
 */
public interface CallLogAPI {
    @POST("/call_logs")
    void sendCallLog(@Body CallLog callLog, Callback<CallLog> callback);
}
