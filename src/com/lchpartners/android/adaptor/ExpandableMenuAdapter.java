package com.lchpartners.android.adaptor;

import java.util.ArrayList;

import com.lchpartners.shadal.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ExpandableMenuAdapter extends BaseExpandableListAdapter {

	private ArrayList<String> catList;
	private ArrayList<ArrayList<String>> menuList;
	private ArrayList<ArrayList<String>> priceList;
	private LayoutInflater inflater;
	
	
	public ExpandableMenuAdapter(Context c, ArrayList<String> catList,
			ArrayList<ArrayList<String>> menuList,
			ArrayList<ArrayList<String>> priceList) {
		super();
		inflater = LayoutInflater.from(c);
		this.catList = catList;
		this.menuList = menuList;
		this.priceList = priceList;
	}

	@Override
	public Object getChild(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
		return menuList.get(arg0).get(arg1);
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getChildView(int arg0, int arg1, boolean arg2, View arg3,
			ViewGroup arg4) {
		// TODO Auto-generated method stub
		View v = arg3;
		if(v == null)
			v = inflater.inflate(R.layout.expandable_item, null);
		
		TextView menuText = (TextView)v.findViewById(R.id.textView_time);
		TextView priceText = (TextView)v.findViewById(R.id.textView_menuprice);
		menuText.setText(menuList.get(arg0).get(arg1));
		priceText.setText(priceList.get(arg0).get(arg1));

		return v;
	}

	@Override
	public int getChildrenCount(int arg0) {
		// TODO Auto-generated method stub
		return menuList.get(arg0).size();
	}

	@Override
	public Object getGroup(int arg0) {
		// TODO Auto-generated method stub
		return menuList.get(arg0);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return catList.size();
	}

	@Override
	public long getGroupId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getGroupView(int arg0, boolean arg1, View arg2, ViewGroup arg3) {
		// TODO Auto-generated method stub
		View v = arg2;
		if(v ==  null)
			v = inflater.inflate(R.layout.expandable_category, null);
		
		TextView catText = (TextView)v.findViewById(R.id.textView_categoryName);
		catText.setText(catList.get(arg0));
		return v;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return true;
	}

}
