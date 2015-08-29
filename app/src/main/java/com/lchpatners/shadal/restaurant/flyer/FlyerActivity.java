package com.lchpatners.shadal.restaurant.flyer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lchpatners.shadal.R;
import com.lchpatners.shadal.TouchImageView;
import com.lchpatners.shadal.call.CallLog.CallLogController;
import com.lchpatners.shadal.call.RecentCallController;
import com.lchpatners.shadal.restaurant.RestaurantController;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Displays image files of leaflets(==flyers).
 */
public class FlyerActivity extends ActionBarActivity {

    public static final String FLYER_URLS = "flyer_urls";
    public static final String RESTAURANT_ID = "restaurant_id";
    public static final String RESTAURANT_PHONE_NUMBER = "restaurant_phone_number";
    private static ImageView watingView;
    private LinearLayout mPageMark;
    private int mPrePosition;
    private Toolbar toolbar;
    private int mRestaurantId;
    private String mRestaurantPhoneNumber;
    private ArrayList<String> mURLS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flyer);

        watingView = (ImageView) findViewById(R.id.wating_view);

        mPageMark = (LinearLayout) findViewById(R.id.pager);
        Intent intent = getIntent();
        mRestaurantId = intent.getIntExtra(RESTAURANT_ID, 0);
        mRestaurantPhoneNumber = intent.getStringExtra(RESTAURANT_PHONE_NUMBER);
        mURLS = intent.getStringArrayListExtra(FLYER_URLS);

//        restaurant = intent.getParcelableExtra(RestaurantListAdapter.RESTAURANT);
//
//        ArrayList<String> urls = (ArrayList<String>) intent.getSerializableExtra(RestaurantListAdapter.URLS);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        TextView toolbarText = (TextView) findViewById(R.id.tool_bar_text);
        toolbarText.setText(mRestaurantPhoneNumber);
        toolbarText.setTextSize(20);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = "tel:" + RestaurantController
                        .getRestaurant(FlyerActivity.this, mRestaurantId).getPhone_number();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                startActivity(intent);
                RecentCallController.stackRecentCall(FlyerActivity.this, mRestaurantId);
                CallLogController.sendCallLog(FlyerActivity.this, mRestaurantId);
            }
        });

        ViewPager viewPager = (ViewPager) findViewById(R.id.flyer_pager);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), mURLS));

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

        initPageMark(mURLS.size());
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
//        AnalyticsHelper helper = new AnalyticsHelper(getApplication());
//        helper.sendScreen("전단지 화면");
    }

    public static class PageFragment extends Fragment {

        private static Context context;
        private static ArrayList<String> urls;

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
        public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
            final int page = getArguments().getInt("PAGE");
            TouchImageView img = new TouchImageView(context);

            Picasso.Builder builder = new Picasso.Builder(context);
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    Toast.makeText(context, "이미지를 불러올 수 없습니다.", Toast.LENGTH_LONG).show();
                    exception.printStackTrace();
                }
            });

            Picasso.with(context).load("http://www.shadal.kr" + urls.get(page)).into(img, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    watingView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onError() {
                    Toast.makeText(context, "이미지를 불러올 수 없습니다.", Toast.LENGTH_LONG).show();
                }
            });
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
