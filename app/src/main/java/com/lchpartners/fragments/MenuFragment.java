package com.lchpartners.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lchpartners.apphelper.server.Server;
import com.lchpartners.shadal.R;
import com.lchpartners.views.NamsanTextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import info.android.sqlite.helper.DatabaseHelper;
import info.android.sqlite.model.Menu_data;
import info.android.sqlite.model.Restaurant;

/**
 * Created by Gwangrae Kim on 2014-09-01.
 */
public class MenuFragment extends Fragment implements ActionBarUpdater, OnClickListener {
    public static class ExpandableMenuAdapter extends BaseExpandableListAdapter {
        //Data references
        private ArrayList<String> catList;
        private ArrayList<ArrayList<String>> menuList;
        private ArrayList<ArrayList<String>> priceList;
        private LayoutInflater inflater;
        private Context mContext;

        private static class MenuViewHolder {
            public TextView menuText;
            public TextView priceText;
        }

        public ExpandableMenuAdapter(Context c, ArrayList<String> catList,
                                     ArrayList<ArrayList<String>> menuList,
                                     ArrayList<ArrayList<String>> priceList) {
            super();
            this.mContext = c;
            inflater = LayoutInflater.from(c);
            this.catList = catList;
            this.menuList = menuList;
            this.priceList = priceList;
        }


        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return menuList.get(groupPosition).get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parent) {
            TextView menuText, priceText;
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.expandable_item, null);
                MenuViewHolder menuViewHolder = new MenuViewHolder();

                menuText = (TextView)convertView.findViewById(R.id.text_view_menu_name);
                priceText = (TextView)convertView.findViewById(R.id.text_view_menu_price);
                menuViewHolder.menuText = menuText;
                menuViewHolder.priceText = priceText;
                convertView.setTag(menuViewHolder);
            }
            else {
                MenuViewHolder menuViewHolder = (MenuViewHolder) convertView.getTag();
                menuText = menuViewHolder.menuText;
                priceText = menuViewHolder.priceText;
            }
            menuText.setText(menuList.get(groupPosition).get(childPosition));
            priceText.setText(new StringBuffer(priceList.get(groupPosition).get(childPosition))
                    .append(mContext.getString(R.string.currency)));

            if(childPosition %2 == 0) convertView.setBackgroundColor(0xfffcfcfc);
            else convertView.setBackgroundColor(0xfff9f9f9);

            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return menuList.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return menuList.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return catList.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView ==  null)
                convertView = inflater.inflate(R.layout.expandable_category, null);
            ((TextView) convertView).setText(catList.get(groupPosition));
            convertView.setBackgroundColor(0x00000000);
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }
    private final static String TAG = "MenuFragment";
    protected final static String EXTRA_RESTAURANT_ID = "resId";

    protected int restaurantId = -1;
    protected DatabaseHelper db;
    protected Restaurant restaurant;
    protected ArrayList<String> sectionList;
    protected ArrayList<ArrayList<String>> menuList;
    protected ArrayList<ArrayList<String>> priceList;
    protected ExpandableListView elView;

    protected Activity mActivity;
    protected boolean updateActionBarOnCreateView = false;

    public void setupView (int restaurantId, View rootView, boolean updateActionBarOnCreateView) {
        setRestaurantFromDatabase(restaurantId);
        if(updateActionBarOnCreateView)
            updateActionBar();

        // check for update
        if(this.isConnected()){
            Server server = new Server(mActivity);
            server.updateRestaurant(restaurant.server_id, restaurant.updated_at);
        }

        TextView numberView = (TextView) rootView.findViewById(R.id.editText_phonenumber);
        numberView.setText(restaurant.phoneNumber);
        numberView.setOnClickListener(this);

        TextView timeView = (TextView) rootView.findViewById(R.id.text_view_time);

        StringBuffer timeString = new StringBuffer("영업 시간");

        boolean hasTimeInfo = false;
        if(!restaurant.openingHours.equals("0")) {
            hasTimeInfo = true;
            timeString.append(": ");
            int otime = (int)Double.parseDouble(restaurant.openingHours)*100;
            if(otime%100>=10)
                timeString.append(otime/100).append(':').append(otime%100);
            else
                timeString.append(otime/100).append(":0").append(otime%100);
            timeString.append(" ~ ");
        }

        if(!restaurant.closingHours.equals("0")) {
            if (!hasTimeInfo) // If the data only contains closing time
                timeString.append(": ~ ");
            hasTimeInfo = true;

            int ctime = (int)Double.parseDouble(restaurant.closingHours)*100;
            if(ctime%100>=10)
                timeString.append(ctime/100).append(':').append(ctime%100);
            else
                timeString.append(ctime/100).append(":0").append(ctime%100);
        }
        //TODO : make this work proplerly. (don't show 0:00 ~ 0:00) - 24h?
        if(!hasTimeInfo)
            timeString.append(" 정보가 없습니다.");

        timeView.setText(timeString);

        elView = (ExpandableListView) rootView.findViewById(R.id.expandableListView_menus);
        elView.setIndicatorBounds(0, 30);

        elView.setAdapter(new ExpandableMenuAdapter(mActivity,sectionList,menuList,priceList));
        TextView couponString = (TextView) rootView.findViewById(R.id.textview_couponString);

        if(restaurant.has_coupon == true)
            couponString.setText(restaurant.coupon_string);
        else
            couponString.setVisibility(View.GONE);
    }
    public void random() {
       setupView(db.getRandomRestaurant().id, this.getView(), true);
    }

    protected void setRestaurantFromDatabase(int res_id){
        db = new DatabaseHelper(mActivity);

        restaurant = db.getRestaurant((long)res_id);

        ArrayList<Menu_data> menus = db.getAllMenusByRestaurant((long)restaurant.id);
        int menu_size = menus.size();

        sectionList = new ArrayList<String>();
        menuList = new ArrayList<ArrayList<String>>();
        priceList = new ArrayList<ArrayList<String>>();

        String current_sec = null;
        if(menu_size != 0){
            current_sec = menus.get(0).getSection();
        }
        ArrayList<String> menuList_i = new ArrayList<String>();
        ArrayList<String> priceList_i = new ArrayList<String>();

        for(int i=0; i<menu_size; i++){
            Menu_data menu = menus.get(i);
            if(!menu.getSection().equals(current_sec)){
                sectionList.add(current_sec);
                menuList.add(menuList_i);
                priceList.add(priceList_i);

                menuList_i = new ArrayList<String>();
                priceList_i = new ArrayList<String>();
            }
            menuList_i.add(menu.getMenu());
            priceList_i.add(String.valueOf(menu.getPrice()));

            current_sec = menu.getSection();
        }
        // Add last section.
        sectionList.add(current_sec);
        menuList.add(menuList_i);
        priceList.add(priceList_i);

        // release memory
        menuList_i = null;
        priceList_i = null;
    }

    public boolean isConnected(){
        ConnectivityManager connMgr
           = (ConnectivityManager) mActivity.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public static MenuFragment newInstance(int restaurantId) {
        MenuFragment result = new MenuFragment();
        Bundle bdl = new Bundle(1);
        bdl.putInt(EXTRA_RESTAURANT_ID, restaurantId);
        result.setArguments(bdl);
        return result;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mActivity = getActivity();
        this.restaurantId = getArguments().getInt(EXTRA_RESTAURANT_ID);
        this.setRestaurantFromDatabase(restaurantId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View resultView = inflater.inflate(R.layout.activity_menu,null);
        setupView(this.restaurantId, resultView, updateActionBarOnCreateView);
        return resultView;
    }

    public void updateActionBarOnCreateView() {
        this.updateActionBarOnCreateView = true;
    }
    public void updateActionBar () {
        ActionBar actionBar = mActivity.getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        ViewGroup titleBar = (ViewGroup) mActivity.getLayoutInflater().inflate(R.layout.action_bar_menus, null);
        titleBar.setLayoutParams(actionBar.getCustomView().getLayoutParams());

        NamsanTextView title = (NamsanTextView) titleBar.findViewById(R.id.text_view_menus_title);
        ImageButton starBtn = (ImageButton) titleBar.findViewById(R.id.btn_star);
        title.setText(restaurant.name);
        starBtn.setSelected(restaurant.is_favorite);

        actionBar.setCustomView(titleBar);
        mActivity.invalidateOptionsMenu();
    }

    //Functions for call logging
    @Override
    public void onClick(View callBtn) {
        switch(callBtn.getId()){
            case R.id.editText_phonenumber : sendLog(restaurant.id);break;
            default : break;
        }
    }

    protected void sendLog(int restaurantId){
        //Log.d("sendLog", "sendLog");
        CallLogSender sender = new CallLogSender(mActivity);
        sender.execute(String.valueOf(restaurantId));
        try {
            if(sender.get() != null)
                Log.d("sendLog",sender.get().toString());
            else
                Log.e("sendLog","Null response returned");
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static class CallLogSender extends AsyncTask<String, Void, HttpResponse> {
        private Context context;
        public CallLogSender(Context context) {
            this.context = context;
        }

        @Override
        protected HttpResponse doInBackground(String... arg0) {
            int res_id = Integer.parseInt(arg0[0]);

            DatabaseHelper db = new DatabaseHelper(context);
            Restaurant res = db.getRestaurant((long)res_id);

            try{
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://services.snu.ac.kr:3111/new_call");
                ArrayList<BasicNameValuePair> value = new ArrayList<BasicNameValuePair>();
                value.add(new BasicNameValuePair("phoneNumber", res.getPhoneNumber()));
                value.add(new BasicNameValuePair("name", res.getName()));
                value.add(new BasicNameValuePair("device", "android"));
                value.add(new BasicNameValuePair("campus", Server.CAMPUS));

                try {
                    httppost.setEntity(new UrlEncodedFormEntity(value, "UTF-8"));
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                return httpclient.execute(httppost);

            }
            catch(Exception e){
                return null;
            }
        }

    }
}
