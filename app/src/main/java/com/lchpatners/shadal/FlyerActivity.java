package com.lchpatners.shadal;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Displays image files of leaflets(==flyers).
 */
public class FlyerActivity extends ActionBarActivity {
    private LinearLayout mPageMark;
    private int mPrePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flyer);
        mPageMark = (LinearLayout) findViewById(R.id.pager);


        //getSupportActionBar().hide();
        Intent intent = getIntent();
        ArrayList<String> urls = (ArrayList<String>) intent.getSerializableExtra("URLS");

        ViewPager viewPager = (ViewPager) findViewById(R.id.flyer_pager);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), urls));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPageMark.getChildAt(mPrePosition).setBackgroundResource(R.drawable.page_not);

                mPageMark.getChildAt(position).setBackgroundResource(R.drawable.page_select);
                mPrePosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        initPageMark(urls.size());
    }

    private void initPageMark(int count) {
        for (int i = 0; i < count; i++) {
            ImageView iv = new ImageView(getApplicationContext());

            if (i == 0)
                iv.setBackgroundResource(R.drawable.page_select);
            else
                iv.setBackgroundResource(R.drawable.page_not);

            mPageMark.addView(iv);
        }
        mPrePosition = 0;

    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsHelper helper = new AnalyticsHelper(getApplication());
        helper.sendScreen("전단지 화면");
    }

    public static class PageFragment extends Fragment {

        private static Context context;
        private static ArrayList<String> urls;

        InputStream stream;
        Drawable drawable;
        boolean exceptionOccurred = false;

        public static PageFragment create(Context context, ArrayList<String> urls, int page) {
            PageFragment.context = context;
            PageFragment.urls = urls;
            PageFragment fragment = new PageFragment();
            Bundle args = new Bundle();
            args.putInt("PAGE", page);
            fragment.setArguments(args);
            return fragment;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            final int page = getArguments().getInt("PAGE");
            TouchImageView img = new TouchImageView(context);

            Picasso.with(context).load("http://www.shadal.kr" + urls.get(page)).into(img);

            Picasso.Builder builder = new Picasso.Builder(context);
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    Toast.makeText(context, "이미지를 불러올 수 없습니다.", Toast.LENGTH_LONG).show();
                    Log.e("FlyerActivity", exception.getMessage());
                }
            });


            //image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            return img;
        }

    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<String> urls;

        private PagerAdapter(FragmentManager fm, ArrayList<String> urls) {
            super(fm);
            this.urls = urls;
        }

        @Override
        public Fragment getItem(int position) {
            return PageFragment.create(FlyerActivity.this, urls, position);
        }

        @Override
        public int getCount() {
            return urls.size();
        }


    }

}
