package com.lchpatners.shadal.recommend;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by eunhyekim on 2015. 8. 26..
 */
public class RecommendedRestaurantList {
    @SerializedName("new")
    private List<RecommendedRestaurantInfo> newRestaurant;

    @SerializedName("trend")
    private List<RecommendedRestaurantInfo> trendRestaraurant;

    public List<RecommendedRestaurantInfo> getNewRestaurant() {
        return newRestaurant;
    }

    public void setNewRestaurant(List<RecommendedRestaurantInfo> newRestaurant) {
        this.newRestaurant = newRestaurant;
    }

    public List<RecommendedRestaurantInfo> getTrendRestaraurant() {
        return trendRestaraurant;
    }

    public void setTrendRestaraurant(List<RecommendedRestaurantInfo> trendRestaraurant) {
        this.trendRestaraurant = trendRestaraurant;
    }
}
