package uq.deco7381.runspyrun.model;

import java.util.ArrayList;
import java.util.HashMap;

import uq.deco7381.runspyrun.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter_newmission extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<HashMap<String, Object>> mAppList;
	private Context mContext;
	private String[] keyString;
	private int[] valueViewID;
	
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

	/*private view holder class*/
    private class ViewHolder {
        ImageView compass;
        TextView type;
        TextView distance;
        TextView locality;
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
		ViewHolder holder = null;
		if (convertView != null){
			holder = (ViewHolder) convertView.getTag();
		}else {
			convertView = mInflater.inflate(R.layout.list_tag_new_mission, null);
			
			holder = new ViewHolder();
			holder.type = (TextView) convertView.findViewById(R.id.textView1);
			holder.compass = (ImageView) convertView.findViewById(R.id.imageView1);
			holder.locality = (TextView) convertView.findViewById(R.id.textView4);
			holder.distance = (TextView) convertView.findViewById(R.id.textView2);
		}
		return convertView;
	}

}
