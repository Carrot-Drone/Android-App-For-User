package com.lchpatners.shadal.setting;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lchpatners.shadal.R;
import com.lchpatners.shadal.RootActivity;
import com.lchpatners.shadal.campus.Campus;
import com.lchpatners.shadal.campus.CampusAPI;
import com.lchpatners.shadal.campus.CampusAdapter;
import com.lchpatners.shadal.campus.CampusController;
import com.lchpatners.shadal.restaurant.RestaurantController;
import com.lchpatners.shadal.util.LogUtils;
import com.lchpatners.shadal.util.Preferences;
import com.lchpatners.shadal.util.RetrofitConverter;
import com.lchpatners.shadal.util.System.DeviceController;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;


public class CampusChangeController {
    private static final String TAG = LogUtils.makeTag(CampusChangeController.class);
    private static final String BASE_URL = "http://www.shadal.kr:3000";
    private CampusAPI mCampusAPI;
    private Activity mActivity;
    private ListView lvCampusListView;
    private Campus mSelectedCampus;
    private int mSelectedCampusId = -1;

    public CampusChangeController(Activity activity) {
        this.mActivity = activity;
        this.lvCampusListView = (ListView) activity.findViewById(R.id.campusList);

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
        Realm realm = Realm.getInstance(mActivity);
        try {
            realm.beginTransaction();

            RealmQuery<Campus> query = realm.where(Campus.class);
            RealmResults<Campus> currentCampus = query.findAll();
            currentCampus.clear();

            //insert campus to realm
            realm.copyToRealm(mSelectedCampus);

            realm.commitTransaction();
        } catch (Exception e) {
            realm.cancelTransaction();
        } finally {
            realm.close();
        }

        DeviceController.sendDeviceInfo(mSelectedCampusId, Preferences.getDeviceUuid(mActivity));
        CampusController.updateCampusMetaData(mActivity, mSelectedCampus);
        RestaurantController.insertOrUpdateAllRestaurantInfo(mActivity, mSelectedCampus, RootActivity.class);
    }

    private void markSelectedCampus(CampusAdapter adapter, int position) {
        adapter.setCheckedItem(position);
    }

    private void fillCampusListView(final List<Campus> campusList) {
        final CampusAdapter campusListAdapter =
                new CampusAdapter(mActivity, campusList);

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

    public Campus getCampus() {
        return mSelectedCampus;
    }
}
