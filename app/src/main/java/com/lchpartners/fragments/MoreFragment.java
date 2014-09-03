package com.lchpartners.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lchpartners.shadal.R;

/**
 * TODO - move all xml constants into dimens.xml
 * Created by Gwangrae Kim on 2014-09-02.
 */
public class MoreFragment extends Fragment implements ActionBarUpdater, Locatable {
    private static final String TAG = "MoreFragment";
    public String tag() {
        return TAG;
    }

    private boolean updateActionBarOnCreateView = false;
    private Activity mActivity;

    private int attachedPage = -1;
    public int getAttachedPage() {
        return attachedPage;
    }
    public void setAttachedPage(int page) {
        this.attachedPage = page;
    }
    public static MoreFragment newInstance() {
        return new MoreFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = getActivity();
        if (updateActionBarOnCreateView)
            updateActionBar();
        View resultView = inflater.inflate(R.layout.fragment_more, container, false);

        View versionView = resultView.findViewById(R.id.entry_more_version);
        View membersView = resultView.findViewById(R.id.entry_more_members);
        View facebookView = resultView.findViewById(R.id.entry_more_facebook);
        View reportView = resultView.findViewById(R.id.entry_more_report);
        View yeonGeonView = resultView.findViewById(R.id.entry_more_yeon_geon);

        versionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://shadal.kr/history"));
                startActivity(i);
            }
        });
        membersView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://shadal.kr/members"));
                startActivity(i);
            }
        });
        facebookView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                try {
                    mActivity.getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    i.setData(Uri.parse("fb://profile/424146807687590"));
                }
                catch (Exception e) {
                    i.setData(Uri.parse("https://www.facebook.com/snushadal"));
                }
                startActivity(i);
            }
        });
        reportView.setOnClickListener(new View.OnClickListener() {
            @Override
            //TODO : remove fixed notification String.
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","partnerslch@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "샤달 맛집 제보 : (음식점 이름)");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "(음식점 이름) 참 좋아하는데 샤달에 없어서 아쉽네요! 제보합니다~");
                startActivity(Intent.createChooser(emailIntent, "사용할 앱을 선택하세요"));
            }
        });
        yeonGeonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = "com.lchpatners.yongon";
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                }
                catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });


        return resultView;
    }

    public void setUpdateActionBarOnCreateView() {
        this.updateActionBarOnCreateView = true;
    }
    public void updateActionBar () {
        ActionBar actionBar = mActivity.getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        ViewGroup titleBar = (ViewGroup) mActivity.getLayoutInflater().inflate(R.layout.action_bar_more, null);
        titleBar.setLayoutParams(actionBar.getCustomView().getLayoutParams());

        actionBar.setCustomView(titleBar);
        mActivity.invalidateOptionsMenu();
    }

}