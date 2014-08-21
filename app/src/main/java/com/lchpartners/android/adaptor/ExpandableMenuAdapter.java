package com.lchpartners.android.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.lchpartners.shadal.R;

import java.util.ArrayList;

public class ExpandableMenuAdapter extends BaseExpandableListAdapter {

    //Data references
	private ArrayList<String> catList;
	private ArrayList<ArrayList<String>> menuList;
	private ArrayList<ArrayList<String>> priceList;
	private LayoutInflater inflater;

    private static class MenuViewHolder {
        public TextView menuText;
        public TextView priceText;
    }
	
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
		View resultView = convertView;
        TextView menuText, priceText;
		if(resultView == null) {
            resultView = inflater.inflate(R.layout.expandable_item, null);
            MenuViewHolder menuViewHolder = new MenuViewHolder();

            menuText = (TextView)resultView.findViewById(R.id.textView_time);
            priceText = (TextView)resultView.findViewById(R.id.textView_menuprice);
            menuViewHolder.menuText = menuText;
            menuViewHolder.priceText = priceText;
            resultView.setTag(menuViewHolder);
        }
        else {
            MenuViewHolder menuViewHolder = (MenuViewHolder) resultView.getTag();
            menuText = menuViewHolder.menuText;
            priceText = menuViewHolder.priceText;
        }
		menuText.setText(menuList.get(groupPosition).get(childPosition));
		priceText.setText(priceList.get(groupPosition).get(childPosition));

		return resultView;
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
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView
            , ViewGroup parent) {
		View resultView = convertView;
		if(resultView ==  null)
			resultView = inflater.inflate(R.layout.expandable_category, null);
		
		TextView catText = (TextView)resultView.findViewById(R.id.textView_categoryName);
		catText.setText(catList.get(groupPosition));
		return resultView;
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
