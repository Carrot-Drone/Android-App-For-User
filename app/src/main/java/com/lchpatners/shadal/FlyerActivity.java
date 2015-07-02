package com.lchpatners.shadal;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Displays image files of leaflets(==flyers).
 */
public class FlyerActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flyer);
        //getSupportActionBar().hide();

        Intent intent = getIntent();
        ArrayList<String> urls = (ArrayList<String>)intent.getSerializableExtra("URLS");

        ViewPager viewPager = (ViewPager)findViewById(R.id.flyer_pager);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), urls));
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsHelper helper = new AnalyticsHelper(getApplication());
        helper.sendScreen("전단지 화면");
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

    public static class PageFragment extends Fragment  {

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
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  {
            final int page = getArguments().getInt("PAGE");
            ImageView image = new ImageView(context);
            PhotoViewAttacher mAttacher;
            stream = null;
            // TODO: use an AsyncTask instead, for this is quite weird
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Create a drawable from URL.
                        stream = (InputStream)new URL("http://www.shadal.kr" + urls.get(page)).getContent();
                        drawable = Drawable.createFromStream(stream, null);

                    } catch (Exception e) {
                        exceptionOccurred = true;
                        e.printStackTrace();
                    }
                }
            }).start();
            // Wait the task to be completed.
            while (drawable == null) {
                if (exceptionOccurred) {
                    Toast.makeText(context, "이미지를 불러올 수 없습니다.", Toast.LENGTH_LONG).show();
                    return null;
                }
            }
            image.setImageDrawable(drawable);
            //image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mAttacher = new PhotoViewAttacher(image);
//            mAttacher.setScaleType(ImageView.ScaleType.FIT_CENTER);

            return mAttacher.getImageView();
        }

    }

}
