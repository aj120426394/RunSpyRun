package uq.deco7381.runspyrun.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uq.deco7381.runspyrun.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

public class ListAdapter_newmission extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<ParseObject> mAppList;
	private Context mContext;
	private Location currentLocation;
	private Location courseLocation;
	private Bitmap bitmap;
	
	public ListAdapter_newmission(Context c, Location location) {
		// TODO Auto-generated constructor stub
		mAppList = new ArrayList<ParseObject>();
		mContext = c;
		mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		bitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.arrow);
		currentLocation = location;
	}

	/*private view holder class*/
    private class ViewHolder {
        ImageView compass;
        TextView type;
        TextView distance;
        TextView locality;
        TextView level;
        Bitmap bitmap;
    }
    private class locationComputing extends AsyncTask<ViewHolder, Void, ViewHolder>{

		@Override
		protected ViewHolder doInBackground(ViewHolder... params) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = params[0];
			if(currentLocation != null){
		    	Matrix matrix = new Matrix();
				matrix.postRotate(currentLocation.bearingTo(courseLocation));
				viewHolder.bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			}else{
				viewHolder.bitmap = bitmap;
			}
			return null;
		}

		@Override
		protected void onPostExecute(ViewHolder result) {
			// TODO Auto-generated method stub
			if(result.bitmap == null){
				result.compass.setImageResource(R.drawable.arrow);
			}else{
				result.compass.setImageBitmap(result.bitmap);

			}
		}
    }
	public void setCurrentLocation(Location currenLocation){
		this.currentLocation = currenLocation;
		this.notifyDataSetChanged();
	}
    public void addCourse(ParseObject course){
    	mAppList.add(course);
    	this.notifyDataSetChanged();
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
		/*
		 * Set up view holder
		 */
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
			holder.level = (TextView) convertView.findViewById(R.id.textView5);
			convertView.setTag(holder);
		}
		/*
		 * Get the course from the list
		 */
		ParseObject course = mAppList.get(position);
		holder.type.setText("DEFENCE");
		/*
		 * Get the locality
		 */
		ParseGeoPoint courseLoc = course.getParseGeoPoint("location");
		String locality = "";
		Geocoder geocoder = new Geocoder(mContext);
		try {
			List<Address> addresses = geocoder.getFromLocation(courseLoc.getLatitude(), courseLoc.getLongitude(), 1);
			locality = addresses.get(0).getLocality();
			locality += ", " + addresses.get(0).getAdminArea();
			holder.locality.setText(locality);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		/*
		 * Get the distance 
		 */
		if(currentLocation != null){
			ParseGeoPoint currentGeoPoint = new ParseGeoPoint(currentLocation.getLatitude(),currentLocation.getLongitude());
			double distanceDouble = currentGeoPoint.distanceInKilometersTo(courseLoc);
	    	String distanceString = "";
	    	if(distanceDouble*1000 < 400){
	    		distanceString = "you are in the course";
	    	}else{
	    		distanceString = String.valueOf((int)(distanceDouble * 1000 - 400)) + " m";
	    	}
	    	holder.distance.setText(distanceString);
		}else{
			holder.distance.setText("Computing...");
		}
		
		/*
		 * Rotate the compass (Image)
		 */
		/*
		courseLocation.setLatitude(courseLoc.getLatitude());
		courseLocation.setLongitude(courseLoc.getLongitude());
		new locationComputing().execute(holder);
		*/
		
		if(currentLocation != null){
			Location tempLoc = new Location(LocationManager.GPS_PROVIDER);
	    	tempLoc.setLatitude(courseLoc.getLatitude());
	    	tempLoc.setLongitude(courseLoc.getLongitude());
	    	Matrix matrix = new Matrix();
			matrix.postRotate(currentLocation.bearingTo(tempLoc));
			Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			holder.compass.setImageBitmap(bmp);
		}else{
			holder.compass.setImageBitmap(bitmap);
		}
		
    	/*
    	 * Get level of the course
    	 */
		String levelString = String.valueOf(course.getInt("level"));
		holder.level.setText(levelString);
		
		return convertView;
	}

}
