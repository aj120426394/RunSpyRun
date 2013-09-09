package uq.deco7381.runspyrun.model;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter_newmission extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<HashMap<String, Object>> mAppList;
	private Context mContext;
	private String[] keyString;
	private int[] valueViewID;
	
	private ItemView itemView;
	 
	private class ItemView {
	   ImageView compass;
	   TextView ItemName;
	   TextView ItemInfo;
	}
	
	public ListAdapter_newmission(Context c, ArrayList<HashMap<String, Object>> appList, int resource, String[] from, int[] to) {
		// TODO Auto-generated constructor stub
		mAppList = appList;
		mContext = c;
		mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		keyString = new String[from.length];
		valueViewID = new int[to.length];
		System.arraycopy(from, 0, keyString, 0, from.length);
		System.arraycopy(to, 0, valueViewID, 0, to.length);
	}

	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mAppList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mAppList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView != null){
			itemView = (ItemView) convertView.getTag();
		}else {
			
		}
		return convertView;
	}

}
