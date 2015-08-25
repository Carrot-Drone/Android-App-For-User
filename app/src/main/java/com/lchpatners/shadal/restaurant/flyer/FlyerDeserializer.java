package com.lchpatners.shadal.restaurant.flyer;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import io.realm.RealmList;

/**
 * Created by YoungKim on 2015. 8. 24..
 */
public class FlyerDeserializer implements JsonDeserializer<RealmList<Flyer>> {
    @Override
    public RealmList<Flyer> deserialize(
            JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        RealmList<Flyer> realmStrings = new RealmList<>();
        JsonArray stringList = json.getAsJsonArray();

        for (JsonElement stringElement : stringList) {
            realmStrings.add(new Flyer(stringElement.getAsString()));
        }

        return realmStrings;
    }
}
