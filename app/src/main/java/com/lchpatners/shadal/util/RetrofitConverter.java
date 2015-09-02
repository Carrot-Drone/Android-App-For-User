package com.lchpatners.shadal.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lchpatners.shadal.dao.Flyer;
import com.lchpatners.shadal.restaurant.flyer.FlyerDeserializer;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by youngkim on 2015. 8. 21..
 */
public class RetrofitConverter {

    public Gson createBasicConverter() {
        Gson converter = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();
        return converter;
    }

    public Gson createRestaurantConverter() {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                });

        // register the deserializer
        gsonBuilder.registerTypeAdapter(
                new TypeToken<RealmList<Flyer>>() {
                }.getType(),
                new FlyerDeserializer());

        Gson gson = gsonBuilder.create();

        return gson;
    }
}
