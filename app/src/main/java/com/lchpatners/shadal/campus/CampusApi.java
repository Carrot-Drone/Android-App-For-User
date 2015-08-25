package com.lchpatners.shadal.campus;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by recover39 on 2015. 8. 20..
 */
public interface CampusAPI {
    @GET("/campuses")
    void getCampusList(Callback<List<Campus>> callback);

    @GET("/campus/{campus_id}")
    void getCampusInfo(@Path("campus_id") int campus_id, Callback<Campus> callback);
}
