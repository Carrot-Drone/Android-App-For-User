package com.lchpatners.shadal.login;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lchpatners.shadal.R;
import com.lchpatners.shadal.campus.Campus;
import com.lchpatners.shadal.campus.CampusAPI;
import com.lchpatners.shadal.campus.CampusAdapter;
import com.lchpatners.shadal.util.LogUtils;
import com.lchpatners.shadal.util.RetrofitConverter;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by youngkim on 2015. 8. 20..
 */
public class LoginController {
    private static final String TAG = LogUtils.makeTag(LoginController.class);
    private static final String BASE_URL = "http://www.shadal.kr:3000";
    private CampusAPI mCampusAPI;
    private Activity activity;
    private ListView lvCampusListView;
    private Campus mSelectedCampus;
    private int mSelectedCampusId = -1;

    public LoginController(Activity activity) {
        this.activity = activity;
        this.lvCampusListView = (ListView) activity.findViewById(R.id.LoginCampusSelect_campusList);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(new RetrofitConverter().createBasicConverter()))
                .setEndpoint(BASE_URL) // The base API endpoint.
                .build();

        mCampusAPI = restAdapter.create(CampusAPI.class);
    }

    public void drawCampusList() {
        getCampusListFromServer();
    }

    public boolean isCampusSelected() {
        if (mSelectedCampusId == -1) {
            return false;
        } else {
            return true;
        }
    }

    public void setCampus() {
        Realm realm = Realm.getInstance(activity);

        try {
            realm.beginTransaction();

            RealmQuery<Campus> query = realm.where(Campus.class);
            RealmResults<Campus> currentCampus = query.findAll();
            currentCampus.clear();

            //insert campus to realm
            realm.copyToRealm(mSelectedCampus);

            realm.commitTransaction();
        } finally {
            realm.close();
        }
    }

    private void markSelectedCampus(CampusAdapter adapter, int position) {
        adapter.setCheckedItem(position);
    }

    private void fillCampusListView(final List<Campus> campusList) {
        final CampusAdapter campusListAdapter =
                new CampusAdapter(activity, campusList);

        lvCampusListView.setAdapter(campusListAdapter);
        lvCampusListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                mSelectedCampus = campusList.get(position);
                mSelectedCampusId = mSelectedCampus.getId();

                markSelectedCampus(campusListAdapter, position);
                campusListAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getCampusListFromServer() {
        mCampusAPI.getCampusList(new Callback<List<Campus>>() {
            @Override
            public void success(List<Campus> campuses, Response response) {
                fillCampusListView(campuses);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, error.toString());
                error.printStackTrace();
            }
        });
    }
}
