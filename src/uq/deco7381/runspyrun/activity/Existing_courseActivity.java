package uq.deco7381.runspyrun.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import uq.deco7381.runspyrun.R;
import uq.deco7381.runspyrun.R.layout;
import uq.deco7381.runspyrun.R.menu;
import uq.deco7381.runspyrun.model.ListAdapter_newmission;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;

public class Existing_courseActivity extends Activity implements OnMyLocationChangeListener{

	private GoogleMap map;
	private LocationManager status;
	private ListView existingCourseListView;
	private ListAdapter_newmission adapter;
	private ArrayList<ParseObject> existingCourse;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_existing_course);
		
		Intent intent = getIntent();
		double latitude = intent.getDoubleExtra("latitude", 0.0);
		double longitude = intent.getDoubleExtra("longtitude", 0.0);
		Location currentLocation = new Location(LocationManager.GPS_PROVIDER);
		currentLocation.setLatitude(latitude);
		currentLocation.setLongitude(longitude);
		
		Parse.initialize(this, "2XLuNz2w0M4iTL5VwXY2w6ICc7aYPZfnr7xyB4EF", "6ZHEiV500losBP4oHmX4f1qVuct1VyRgOlByTVQB");
		ParseAnalytics.trackAppOpened(getIntent());
		
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
		
		existingCourse = new ArrayList<ParseObject>();
		existingCourseListView = (ListView) findViewById(R.id.ListOFCourse);
		adapter = new ListAdapter_newmission(this,currentLocation);
		existingCourseListView.setAdapter(adapter);
		getCourseList();
		
		
	}

	private void getCourseList(){
		ParseQuery<ParseObject> courseList = ParseQuery.getQuery("Course");
		courseList.whereEqualTo("organization", ParseUser.getCurrentUser().getString("organization"));
		if(courseList.hasCachedResult()){
			courseList.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
		}else{
			courseList.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		}
		courseList.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if(e == null){
					for(ParseObject mission: objects){
						boolean isNew = true;
						for(ParseObject inMission: existingCourse){
							if(mission.getObjectId().equals(inMission.getObjectId())){
								isNew = false;
							}
						}
						if(isNew){
							existingCourse.add(mission);
							adapter.addCourse(mission);
						}
					}
				}else{
					System.out.println(e.getMessage());
				}
			}
		});
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
        LatLng latLng = new LatLng(lastKnowLocation.getLatitude(), lastKnowLocation.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		map.animateCamera(CameraUpdateFactory.zoomTo(15));
		map.setOnCameraChangeListener(null);
		
	}

}
