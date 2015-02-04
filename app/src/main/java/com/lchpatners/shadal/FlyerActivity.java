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


public class FlyerActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flyer);

        getSupportActionBar().hide();

        Intent intent = getIntent();
        ArrayList<String> urls = (ArrayList<String>)intent.getSerializableExtra("URLS");

        ViewPager viewPager = (ViewPager)findViewById(R.id.flyer_pager);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), urls));
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
            ImageView image = new ImageView(context);
            stream = null;
            // TODO use an AsyncTask instead for this is quite weird
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        stream = (InputStream)new URL("http://www.shadal.kr" + urls.get(page)).getContent();
                        drawable = Drawable.createFromStream(stream, null);
                    } catch (Exception e) {
                        exceptionOccurred = true;
                        e.printStackTrace();
                    }
                }
            }).start();
            while (drawable == null) {
                if (exceptionOccurred) {
                    Toast.makeText(context, "이미지를 불러올 수 없습니다.", Toast.LENGTH_LONG).show();
                    return null;
                }
            }
            image.setImageDrawable(drawable);
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            return image;
        }
    }
}
