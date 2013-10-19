package uq.deco7381.runspyrun.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uq.deco7381.runspyrun.R;
import uq.deco7381.runspyrun.model.Course;
import uq.deco7381.runspyrun.model.ListAdapter_current_mission;
import uq.deco7381.runspyrun.model.ParseDAO;
import uq.deco7381.runspyrun.model.SwipeDismissListViewTouchListener;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
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
import com.parse.PushService;
/**
 * This activity is the main page "Dash board" when user launch the app.
 * 1. Display user's current information:
 * 	a.Username
 * 	b.Level
 * 	c.Energy
 * 	d.Datasource number
 * 	e.Current location
 * 
 * 2. Display user's current mission list:
 * 	a.Mission name (not available)
 *  b.Mission took place
 *  c.Distance between user and course of the mission
 *  d.Direction of course of the mission.
 *  
 * 3. Other Activities link
 * 	a.Message (not available)
 * 	b.Equipment
 * 	c.Setting (not available)
 * @author Jafo
 *
 */
public class DashboardActivity extends Activity implements OnMyLocationChangeListener {
	
	private GoogleMap map;
	private LocationManager status;
	private Location currentLocation;
	private ListView missionListView;
	private ListAdapter_current_mission adapter;
	private ParseDAO dao;
	private boolean isCurrLocExist;

	@SuppressLint("ResourceAsColor")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		dao = new ParseDAO();
		isCurrLocExist = false;
		PushService.subscribe(this, ParseUser.getCurrentUser().getString("organization"), LoginActivity.class);
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

		/*
		 *  Get user info from Parse server
		 */
		
		dao.updateEnergyByTime(ParseUser.getCurrentUser());
		dao.updateEnergyByObstacle(ParseUser.getCurrentUser());
		setUserInfo();
		
		
		ArrayList<Course> missionList = getMissionList();
		missionListView = (ListView)findViewById(R.id.db_mission_list);
		missionListView.setScrollingCacheEnabled(false);
		adapter = new ListAdapter_current_mission(this, currentLocation, missionList);
		missionListView.setAdapter(adapter);
		/*
		 * Constrain the scroll view when list view is scrollable 
		 */
		/*
		missionListView.setOnTouchListener(new ListView.OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				int action = event.getAction();
	            switch (action) {
	            case MotionEvent.ACTION_DOWN:
	                // Disallow ScrollView to intercept touch events.
	                v.getParent().requestDisallowInterceptTouchEvent(true);
	                break;

	            case MotionEvent.ACTION_UP:
	                // Allow ScrollView to intercept touch events.
	                v.getParent().requestDisallowInterceptTouchEvent(false);
	                break;
	            }

	            // Handle ListView touch events.
	            v.onTouchEvent(event);
	            return true;
			}
			
		});
*/
		TextView noMission = (TextView)findViewById(R.id.textView2);
		if(missionList.size() == 0){
			noMission.setVisibility(View.VISIBLE);
			missionListView.setVisibility(View.GONE);
		}else{
			noMission.setVisibility(View.GONE);
			missionListView.setVisibility(View.VISIBLE);
		}
		
		
		
	}
	
	/**
	 * Get the mission list
	 * @return ArrayList: list of mission
	 */
	private ArrayList<Course> getMissionList(){
		ArrayList<Course> missionList = dao.getCourseByMissionFromNetwork(ParseUser.getCurrentUser());
		return missionList;
	}
	
	/**
	 * To display all user information on the dash board:
	 * 1. Get and display current user name from device's cache
	 * 2. Get and display current user level from device's cache
	 * 3. Get and display current user energy level from device's cache
	 * 4. Fetch user's equipment data base on Current user.
	 * 
	 */
	private void setUserInfo() {
		ParseUser currentUser = ParseUser.getCurrentUser();
		
		/*
		 *  Set the username
		 */
		String userString = currentUser.getUsername();
		TextView usernameTextView = (TextView)findViewById(R.id.basicInfo_name);
		usernameTextView.setText(userString);
		
		/*
		 *  Set the level
		 */
		int level = (Integer) currentUser.getInt("level");
		TextView levelTextView = (TextView)findViewById(R.id.basicInfo_level);
		levelTextView.setText(String.valueOf(level));
		
		/*
		 *  Set the energy level
		 */
		int energyLv = (Integer) currentUser.getNumber("energyLevel");
		TextView energyTextView = (TextView)findViewById(R.id.basicInfo_energy);
		energyTextView.setText(String.valueOf(energyLv)+" / "+String.valueOf(level*100));
		
		/*
		 *  Set the datasource number
		 */
		ParseQuery<ParseObject> equipmentQuery = ParseQuery.getQuery("equipment");
		equipmentQuery.whereEqualTo("username", currentUser);
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
					int numOfdata = objects.get(0).getInt("number");
					TextView dataTextView = (TextView)findViewById(R.id.basicInfo_dataSource);
					dataTextView.setText(String.valueOf(numOfdata));
				}else{
					System.out.println(e.getMessage());
				}
			}
		});
		
		/*
		 * Set the locality
		 */
		if(currentLocation != null){
			
			String locality = "";
			Geocoder geocoder = new Geocoder(this);
			try {
				List<Address> addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
				locality = addresses.get(0).getLocality();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			TextView localityTextView = (TextView)findViewById(R.id.basicInfo_location);
			localityTextView.setText(locality);
		}
		
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
	/**
	 * onClick method triggered by "Equipment"
	 * Direct user to Equipment page
	 * @param v
	 */
	public void goEquipment(View v){
		Intent intent = new Intent(this, EquipmentActivity.class);
		startActivity(intent);
	}

	/**
	 * 
	 */
	public void goExistingCourse(View v){
		Intent intent = new Intent(this, Existing_courseActivity.class);
		intent.putExtra("latitude", currentLocation.getLatitude());
		intent.putExtra("longtitude", currentLocation.getLongitude());
		startActivity(intent);
	}
	/**
	 * onClick method triggered by "Attack"
	 * Direct user to a list of course can be attacked
	 */
	public void goAttack(View v){
		Intent intent = new Intent(this, AttackCourseListActivity.class);
		intent.putExtra("latitude", currentLocation.getLatitude());
		intent.putExtra("longtitude", currentLocation.getLongitude());
		startActivity(intent);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		map.setMyLocationEnabled(false);
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		map.setMyLocationEnabled(true);
	}

	@Override
	public void onMyLocationChange(Location lastKnownLocation) {
		// TODO Auto-generated method stub
		currentLocation = lastKnownLocation;
		/*
		 *  Getting latitude of the current location
		 */
        double latitude = lastKnownLocation.getLatitude();
 
        /*
         *  Getting longitude of the current location
         */
        double longitude = lastKnownLocation.getLongitude();
 
        if(isCurrLocExist == false){
        	adapter.changeLocation(lastKnownLocation);
        	setUserInfo();
        	isCurrLocExist = true;
        }
        adapter.changeLocation(lastKnownLocation);
        /*
         *  Make camera on map keep tracking user.
         */
        LatLng latLng = new LatLng(latitude, longitude);
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		map.animateCamera(CameraUpdateFactory.zoomTo(15));
		map.setOnCameraChangeListener(null);
	}

}
