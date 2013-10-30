package uq.deco7381.runspyrun.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uq.deco7381.runspyrun.R;
import uq.deco7381.runspyrun.activity.DashboardActivity;
import uq.deco7381.runspyrun.activity.DefenceActivity;
import uq.deco7381.runspyrun.activity.Existing_courseActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
/**
 * This class is a list view adapter use in DashboardActivity.
 * It will use async task to calculate the location, bearing and distance.  
 * 
 * @author Jafo
 * @version 1.3
 * @since 15/10/2013
 * @see uq.deco7381.runspyrun.activity.DashboardActivity.java
 */
public class ListAdapter_current_mission extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<Course> mAppList;
	private Context mContext;
	private Location currentLocation;
	private Bitmap bitmap;
	private ParseDAO dao;
	//private boolean triggable;
	
	/**
	 * Constructor 
	 * @param c: the main Activity which contain the list view.
	 * @param location: user's current loction.
	 * @param course: the list of course load from Parse.
	 */
	public ListAdapter_current_mission(Context c, Location location, ArrayList<Course> course) {
		// TODO Auto-generated constructor stub
		mAppList = course;
		mContext = c;
		mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		bitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.arrow);
		currentLocation = location;
		dao = new ParseDAO();
		//triggable = false;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mAppList.size();
	}

	@Override
	public Course getItem(int position) {
		// TODO Auto-generated method stub
		return mAppList.get(position);
	}
	/**
     * Override all the data set in this adapter.
     * @param course: the list of course get from Parse
     */
	public void overwriteDataset(ArrayList<Course> course){
		this.mAppList = course;
		notifyDataSetChanged();
	}
	/**
	 * Remove the Item in the data set.
	 * @param object: the object user want to remove.
	 */
	public void removeItem(Object object){
		this.mAppList.remove(object);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	/**
	 * Set the new current location to list view.
	 * @param loc: user's current location.
	 */
	public void changeLocation(Location loc){
		this.currentLocation = loc;
		this.notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		/*
		 * Set up view holder
		 */
		ViewHolder holder = null;
		if (convertView != null){
			holder = (ViewHolder) convertView.getTag();
		}else {
			convertView = mInflater.inflate(R.layout.list_tag_current_mission, null);
			
			holder = new ViewHolder();
			holder.type = (TextView) convertView.findViewById(R.id.energy);
			holder.compass = (ImageView) convertView.findViewById(R.id.imageView1);
			holder.locality = (TextView) convertView.findViewById(R.id.textView4);
			holder.distance = (TextView) convertView.findViewById(R.id.textView2);
			holder.level = (TextView) convertView.findViewById(R.id.textView5);
			holder.position = position;
			holder.triggable = false;
			convertView.setTag(holder);
		}
		
		/*
		 * Get the course from the list
		 */
		final Course course = mAppList.get(position);
		if(course.getOrg().equals(ParseUser.getCurrentUser().getString("organization"))){
			holder.type.setText(course.getObjectID());
		}else{
			holder.type.setText("Attack");
		}
		

		/*
		 * Computing the "locality", "bearing", "distance" in an Asynchrony Task
		 */
		final ParseGeoPoint courseLoc = course.getParseGeoPoint();
		if(currentLocation !=  null){
			new locationComputing().execute(new Object[]{holder,position,courseLoc});
		}
    	/*
    	 * Get level of the course
    	 */
		String levelString = String.valueOf(course.getLevel());
		holder.level.setText(levelString);
		
		final RelativeLayout viewGroup = (RelativeLayout)parent.getParent();
		TextView addNew = (TextView)viewGroup.findViewById(R.id.db_mission_new);
		/*
		 * Check if "Add new" is clickable
		 */
		if(0 < getCount() && getCount() < 6 && this.currentLocation != null){
			addNew.setTextColor(mContext.getResources().getColor(R.color.orangeText));
			addNew.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(mContext, Existing_courseActivity.class);
					intent.putExtra("latitude", currentLocation.getLatitude());
					intent.putExtra("longtitude", currentLocation.getLongitude());
					mContext.startActivity(intent);
				}
			});
		}
		/*
		 * Swipe gesture to delete the mission in Dash board
		 */
		final SwipeDismissTouchListener swipeDismissTouchListener;
	    swipeDismissTouchListener = new SwipeDismissTouchListener(convertView, null, new SwipeDismissTouchListener.DismissCallbacks() {
			
			@Override
			public void onDismiss(View view, Object token) {
				// TODO Auto-generated method stub
				System.out.println(position);
				dao.deleteMission(getItem(position), ParseUser.getCurrentUser());
				dao.deleteObstacleByUser(getItem(position), ParseUser.getCurrentUser());
				removeItem(getItem(position));
				/*
				 * Set the number of current mission
				 */
				TextView missionNum = (TextView)viewGroup.findViewById(R.id.missionNum);
				String missionNumString = "( " + getCount() + " / 6 )";
				missionNum.setText(missionNumString);
				notifyDataSetChanged();
			}
			
			@Override
			public boolean canDismiss(Object token) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		
		convertView.setOnTouchListener(swipeDismissTouchListener);
		
		if(holder.triggable){
    		holder.type.setTextColor(mContext.getResources().getColor(R.color.orangeText));
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(mContext, DefenceActivity.class);
					intent.putExtra("latitude", courseLoc.getLatitude());
					intent.putExtra("longtitude", courseLoc.getLongitude());
					intent.putExtra("isFrom", "existMission");
					mContext.startActivity(intent);
				}
			});
		}
		
		
		
		return convertView;
	}

	/*
	 * private view holder class
	 */
    private class ViewHolder {
        ImageView compass;
        TextView type;
        TextView distance;
        TextView locality;
        TextView level;
        int position;
        boolean triggable;
    }
    /**
	 * This class use another thread to calculate the location, bearing and distance between user current loaction and course location.
	 * Improving user experience.
	 * @author Jafo
	 *
	 */
    private class locationComputing extends AsyncTask<Object,Void,Bitmap>{
		private ViewHolder holder;
		private int position;
		private String distanceString;
		private String locality;
	
		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if(holder.position == position){
				this.holder.compass.setImageBitmap(result);
				this.holder.distance.setText(distanceString);
				this.holder.locality.setText(locality);
			}
		}
		@Override
		protected Bitmap doInBackground(Object... params) {
			// TODO Auto-generated method stub
			holder = (ViewHolder)params[0];
			position = (Integer) params[1];
			ParseGeoPoint courseLoc = (ParseGeoPoint)params[2];
			
			/*
			 * Computing the distance between current location to each course
			 */
			ParseGeoPoint currentGeoPoint = new ParseGeoPoint(currentLocation.getLatitude(),currentLocation.getLongitude());
			double distanceDouble = currentGeoPoint.distanceInKilometersTo(courseLoc);
	    	distanceString = "";
	    	if(distanceDouble*1000 < 500){
	    		holder.triggable = true;
	    	}
	    	if(distanceDouble*1000 < 400){
	    		distanceString = "you are in the course";
	    	}else{
	    		distanceString = String.valueOf((int)(distanceDouble * 1000 - 400)) + " m";
	    		holder.triggable = false;
	    	}
			
			/*
			 * Get the locality of each course
			 */
			locality = "";
			Geocoder geocoder = new Geocoder(mContext);
			try {
				List<Address> addresses = geocoder.getFromLocation(courseLoc.getLatitude(), courseLoc.getLongitude(), 1);
				if(addresses.size() > 0){
					locality = addresses.get(0).getLocality();
					locality += ", " + addresses.get(0).getAdminArea();
				}
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			/*
			 * Get the compass direction of each course
			 */
	    	Location tempLoc = new Location(LocationManager.GPS_PROVIDER);
	    	tempLoc.setLatitude(courseLoc.getLatitude());
	    	tempLoc.setLongitude(courseLoc.getLongitude());
	    	
			Matrix matrix = new Matrix();
			matrix.postRotate(currentLocation.bearingTo(tempLoc));
			Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			return bmp;
		}
		
	}
}
