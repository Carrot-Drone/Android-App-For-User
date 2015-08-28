package com.lchpatners.shadal.campus;

import android.app.Activity;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by YoungKim on 2015. 8. 28..
 */
public class CampusController {
    public static Campus getCurrentCampus(Activity activity) {
        Realm realm = Realm.getInstance(activity);
        RealmQuery<Campus> query = realm.where(Campus.class);
        Campus campus = query.findFirst();
        return campus;
    }
}
