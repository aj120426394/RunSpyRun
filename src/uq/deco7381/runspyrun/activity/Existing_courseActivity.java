package uq.deco7381.runspyrun.activity;

import java.util.ArrayList;
import java.util.List;

import uq.deco7381.runspyrun.R;
import uq.deco7381.runspyrun.model.Course;
import uq.deco7381.runspyrun.model.ListAdapter_newmission;
import uq.deco7381.runspyrun.model.ParseDAO;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class Existing_courseActivity extends Activity implements OnMyLocationChangeListener{

	private GoogleMap map;
	private LocationManager status;
	private ListView existingCourseListView;
	private ListAdapter_newmission adapter;
	private ParseDAO dao;
	private Location currentLocation;
	private TextView numOfData;
	private int numOfdata;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_existing_course);
		dao = new ParseDAO();
		
		Intent intent = getIntent();
		double latitude = intent.getDoubleExtra("latitude", 0.0);
		double longitude = intent.getDoubleExtra("longtitude", 0.0);
		
		currentLocation = new Location(LocationManager.GPS_PROVIDER);
		currentLocation.setLatitude(latitude);
		currentLocation.setLongitude(longitude);

		status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
		/*
		 *  Check is GPS available
		 */
		if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			/*
			 *  Check the map is exist or not
			 */
			if (map == null){
				map = ((MapFragment) getFragmentManager().findFragmentById(R.id.db_map)).getMap();
				if(map != null){
					setUpMap();
				}
			}
		}else{
			/*
			 *  If GPS is no available, direct user to Setting  
			 */
			Toast.makeText(this, "Please open the GPS", Toast.LENGTH_LONG).show();
			startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		}
		numOfData = (TextView)findViewById(R.id.textView2);
		
		ArrayList<Course> courseList = new ArrayList<Course>();
		new GetCourseList().execute(new Double[]{latitude,longitude});
		
		existingCourseListView = (ListView)findViewById(R.id.listView);
		existingCourseListView.setScrollingCacheEnabled(false);
		
		adapter = new ListAdapter_newmission(this,currentLocation,courseList);
		existingCourseListView.setAdapter(adapter);
		
		/*
		 *  Set the datasource number
		 */
		ParseQuery<ParseObject> equipmentQuery = ParseQuery.getQuery("equipment");
		equipmentQuery.whereEqualTo("username", ParseUser.getCurrentUser());
		equipmentQuery.whereEqualTo("eq_name", "Datasource");
		if(equipmentQuery.hasCachedResult()){
			equipmentQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
		}else{
			equipmentQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		}
		equipmentQuery.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if(e == null){
					numOfdata = objects.get(0).getInt("number");
					numOfData.setText(String.valueOf(numOfdata));
					newCourseCreatable();
				}else{
					System.out.println(e.getMessage());
				}
			}
		});
		
		
		
		
	}

	private void newCourseCreatable(){
		RelativeLayout create = (RelativeLayout)findViewById(R.id.RelativeLayout1);
		TextView createTitle = (TextView)findViewById(R.id.missionNum);
		System.out.println(numOfdata);
		if(numOfdata > 0){
			numOfData.setTextColor(getResources().getColor(R.color.orangeText));
			createTitle.setTextColor(getResources().getColor(R.color.orangeText));
			create.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(currentLocation != null){
						Intent intent = new Intent(Existing_courseActivity.this, DefenceActivity.class);
						intent.putExtra("latitude", currentLocation.getLatitude());
						intent.putExtra("longtitude", currentLocation.getLongitude());
						intent.putExtra("isFrom", "newCourse");
						startActivity(intent);
					}
				}
			});
		}
	}
	private ArrayList<Course> getCourseList(double latitude, double longitude){
		ArrayList<Course> courseList  = new ArrayList<Course>();
		ArrayList<Course> missionList = dao.getCourseByMissionFromNetwork(ParseUser.getCurrentUser());
		ArrayList<Course> orgList = dao.getCourseByOrgInDistance(latitude, longitude, ParseUser.getCurrentUser().getString("organization"), 2);
		
		for(Course course:  orgList){
			boolean flag = false;
			for(Course missionCourse: missionList){
				if(course.getObjectID().equals(missionCourse.getObjectID())){
					flag = true;
				}
			}
			if(flag == false){
				courseList.add(course);
			}
		}
		
		return courseList;
	}
	
	/**
	 * Basic map set up
	 * 1. Set map track user's current location
	 * 2. Set map get location change listener.
	 * 3. Disable all operation on map (The map is the background)
	 */
	private void setUpMap(){
		map.setMyLocationEnabled(true);
		map.setOnMyLocationChangeListener(this);
		UiSettings uiSettings = map.getUiSettings();
		uiSettings.setAllGesturesEnabled(false);
		uiSettings.setMyLocationButtonEnabled(false);
		uiSettings.setZoomControlsEnabled(false);
	}

	@Override
	public void onMyLocationChange(Location lastKnowLocation) {
		// TODO Auto-generated method stub
	    /*
         *  Make camera on map keep tracking user.
         */
		this.currentLocation = lastKnowLocation;
		
        LatLng latLng = new LatLng(lastKnowLocation.getLatitude(), lastKnowLocation.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		map.animateCamera(CameraUpdateFactory.zoomTo(15));
		map.setOnCameraChangeListener(null);
		
	}
	
	public void createNewCourse() {
		if(this.currentLocation != null){
			Intent intent = new Intent(this, DefenceActivity.class);
			intent.putExtra("latitude", this.currentLocation.getLatitude());
			intent.putExtra("longtitude", this.currentLocation.getLongitude());
			intent.putExtra("isFrom", "newCourse");
			startActivity(intent);
		}
	}
	
	private class GetCourseList extends AsyncTask<Double, Void, ArrayList<Course>>{

		@Override
		protected ArrayList doInBackground(Double... params) {
			// TODO Auto-generated method stub
			double latitude = params[0];
			double longitude = params[1];
			ArrayList<Course> courseList  = new ArrayList<Course>();
			ArrayList<Course> missionList = dao.getCourseByMissionFromNetwork(ParseUser.getCurrentUser());
			ArrayList<Course> orgList = dao.getCourseByOrgInDistance(latitude, longitude, ParseUser.getCurrentUser().getString("organization"), 2);
			
			for(Course course:  orgList){
				boolean flag = false;
				for(Course missionCourse: missionList){
					if(course.getObjectID().equals(missionCourse.getObjectID())){
						flag = true;
					}
				}
				if(flag == false){
					courseList.add(course);
				}
			}
			
			return courseList;
		}

		@Override
		protected void onPostExecute(ArrayList<Course> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			adapter.overrideDataset(result);
		}
		
	}
}
