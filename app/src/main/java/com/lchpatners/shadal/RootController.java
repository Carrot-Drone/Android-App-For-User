package com.lchpatners.shadal;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.lchpatners.shadal.campus.Campus;
import com.lchpatners.shadal.campus.CampusAPI;
import com.lchpatners.shadal.util.LogUtils;
import com.lchpatners.shadal.util.RetrofitConverter;
import com.lchpatners.shadal.util.System.*;
import com.lchpatners.shadal.util.System.System;

import io.realm.Realm;
import io.realm.RealmQuery;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by YoungKim on 2015. 8. 24..
 */
public class RootController {
    private static final String TAG = LogUtils.makeTag(RootController.class);
    private static final String BASE_URL = "http://www.shadal.kr:3000";

    private Activity mActivity;
    private Campus mCampus;

    public RootController(Activity activity) {
        mActivity = activity;
        setCampus();
    }

    public static void showDownloadCompleteToast(Context context) {
        Toast.makeText(context, "음식점 정보 저장 완료!", Toast.LENGTH_SHORT).show();
    }

    private void setCampus() {
        Realm realm = Realm.getInstance(mActivity);
        RealmQuery<Campus> query = realm.where(Campus.class);
        Campus campus = query.findFirst();
        mCampus = campus;
    }

    public Campus getCampus() {
        return mCampus;
    }

    public void isRecentVersion() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(new RetrofitConverter().createBasicConverter()))
                .setEndpoint(BASE_URL) // The base API endpoint.
                .build();

        SystemAPI systemAPI = restAdapter.create(SystemAPI.class);

        systemAPI.getMinimunAppVersion(new Callback<com.lchpatners.shadal.util.System.System>() {
            @Override
            public void success(System system, Response response) {
                if (Integer.parseInt(system.getMinimum_android_version()) > getCurrentVersion()) {
                    updateDialog();
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private int getCurrentVersion() {
        int appVersion = 100000;
        try {
            appVersion = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appVersion;
    }

    private void updateDialog() {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(mActivity);
        alt_bld.setMessage("새 버전이 나왔어요!\n더 좋아진 캠달로 업데이트해주세요!")
                .setCancelable(false).setPositiveButton("업데이트하기", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                final String appPackageName = mActivity.getPackageName();
                try {
                    mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException e) {
                    e.printStackTrace();
                    mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        }).setNegativeButton("그냥쓸래요", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                Toast.makeText(mActivity, "다음엔 꼭 업데이트 해주세요!", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }
}
