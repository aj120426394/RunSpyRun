package uq.deco7381.runspyrun.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uq.deco7381.runspyrun.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
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
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class DashboardActivity extends Activity implements OnMyLocationChangeListener {
	
	private GoogleMap map;
	private LocationManager status;

	@SuppressLint("ResourceAsColor")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		
		Parse.initialize(this, "2XLuNz2w0M4iTL5VwXY2w6ICc7aYPZfnr7xyB4EF", "6ZHEiV500losBP4oHmX4f1qVuct1VyRgOlByTVQB");
		ParseAnalytics.trackAppOpened(getIntent());
		
		status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
		// Check is GPS available
		if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			// Check the map is exist or not
			if (map == null){
				map = ((MapFragment) getFragmentManager().findFragmentById(R.id.db_map)).getMap();
				if(map != null){
					setUpMap();
				}
			}
		}else{
			// If GPS is no available, direct user to Setting  
			Toast.makeText(this, "Please open the GPS", Toast.LENGTH_LONG).show();
			startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		}
		
		
		
		// Get user info from Parse server
		setUserInfo();

		displayMissionList();
		
	}
	
	/**
	 * This method is to display the list of mission that user have started and haven't finished.
	 * 
	 * 1. Save the textview which display the distance between user and the course to a ArrayList
	 * 2. Save the course geolocation to a ArrayList.
	 * @see onMyLocationChange()   Both of the ArrayList would be use to measure and dipaly the distance
	 * 								between user and course.
	 * 
	 * Fetch the mission list and the course info from Parse server.
	 * 
	 */
	ArrayList<TextView> distance = new ArrayList<TextView>();
	ArrayList<ParseGeoPoint> course = new ArrayList<ParseGeoPoint>();
	private void displayMissionList(){
		final Intent intent = new Intent(this, DefenceActivity.class);
		
		// Get user's mission list
		ParseQuery<ParseObject> missionList = ParseQuery.getQuery("Mission");
		missionList.whereEqualTo("username", ParseUser.getCurrentUser());
		missionList.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if(objects.size() != 0 && e == null){
					for(ParseObject mission: objects){
						// Get the course of each mission
						mission.getParseObject("course").fetchIfNeededInBackground(new GetCallback<ParseObject>() {
							@Override
							public void done(final ParseObject object, ParseException e) {
								// TODO Auto-generated method stub
								if(e == null){
									LinearLayout linearLayout = (LinearLayout)findViewById(R.id.db_mission_list);
									RelativeLayout missionLayout = new RelativeLayout(uq.deco7381.runspyrun.activity.DashboardActivity.this);
									RelativeLayout.LayoutParams missiLayoutParams = new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT,70);
									missionLayout.setLayoutParams(missiLayoutParams);
									// Display mission name
									TextView missionName = new TextView(uq.deco7381.runspyrun.activity.DashboardActivity.this);
									missionName.setId(R.id.textView1);
									missionName.setText("Mission - ");
									missionName.setId(R.id.button1);
									missionName.setTextColor(Color.parseColor("#EF802E"));
									missionName.setTextSize(18);
									missionName.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
									missionLayout.addView(missionName);
									
									
									// Display arrow of each mission
									TextView missionArrow = new TextView(uq.deco7381.runspyrun.activity.DashboardActivity.this);
									missionLayout.addView(missionArrow);
									missionArrow.setText(">");
									missionArrow.setTextColor(Color.parseColor("#EF802E"));
									missionArrow.setTextSize(18);
									RelativeLayout.LayoutParams missionArrowParams = (RelativeLayout.LayoutParams)missionArrow.getLayoutParams();
									missionArrowParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
									missionArrowParams.addRule(RelativeLayout.CENTER_IN_PARENT);
									missionArrow.setLayoutParams(missionArrowParams);
									
									// Display place name of the course
									final ParseGeoPoint location = object.getParseGeoPoint("location");
									String locality = "";
									Geocoder geocoder = new Geocoder(DashboardActivity.this);
									try {
										List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
										locality = addresses.get(0).getLocality();
										locality += ", " + addresses.get(0).getAdminArea();
										locality += ", " + addresses.get(0).getCountryCode();
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									
									TextView missionLoc = new TextView(uq.deco7381.runspyrun.activity.DashboardActivity.this);
									missionLayout.addView(missionLoc);
									missionLoc.setText(locality);
									missionLoc.setTextColor(Color.parseColor("#EF802E"));
									missionLoc.setTextSize(15);
									RelativeLayout.LayoutParams missionLocParams = (RelativeLayout.LayoutParams)missionLoc.getLayoutParams();
									missionLocParams.addRule(RelativeLayout.RIGHT_OF,missionName.getId());
									missionLocParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
									missionLocParams.setMargins(0, 5, 0, 0);
									missionLoc.setLayoutParams(missionLocParams);
									
									// Display distance between user and the course.
									TextView missionDis = new TextView(uq.deco7381.runspyrun.activity.DashboardActivity.this);
									missionLayout.addView(missionDis);
									missionDis.setText("Counting distance...");
									missionDis.setTextColor(Color.BLACK);
									missionDis.setTextSize(12);
									RelativeLayout.LayoutParams missionDisParams = (RelativeLayout.LayoutParams)missionDis.getLayoutParams();
									missionDisParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
									missionDisParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
									missionDis.setLayoutParams(missionDisParams);
									
									distance.add(missionDis);
									course.add(location);
									
									//Set up onclick listener if click on the mission
									missionLayout.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											
											intent.putExtra("latitude", location.getLatitude());
											intent.putExtra("longtitude", location.getLongitude());
											intent.putExtra("isFrom", "exsitMission");
											startActivity(intent);
										}
									});
									linearLayout.addView(missionLayout);
									
								}else{
									System.out.println(e.getMessage());
								}
							}
						});
					}
				}
			}
		});
	}
	private void setUserInfo() {
		ParseUser currentUser = ParseUser.getCurrentUser();
		
		// Set the username
		String userString = currentUser.getUsername();
		TextView usernameTextView = (TextView)findViewById(R.id.basicInfo_name);
		usernameTextView.setText(userString);
		
		// Set the level
		int level = (Integer) currentUser.getInt("level");
		TextView levelTextView = (TextView)findViewById(R.id.basicInfo_level);
		levelTextView.setText(String.valueOf(level));
		
		// Set the energy level
		int energyLv = (Integer) currentUser.getNumber("energyLevel");
		TextView energyTextView = (TextView)findViewById(R.id.basicInfo_energy);
		energyTextView.setText(String.valueOf(energyLv)+" / "+String.valueOf(level*100));
		
		// Set the datasource number
		ParseQuery<ParseObject> query = ParseQuery.getQuery("equipment");
		query.whereEqualTo("username", currentUser);
		query.whereEqualTo("eq_name", "Datasource");
		query.findInBackground(new FindCallback<ParseObject>() {
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
	}
	// Map set up
	private void setUpMap(){
		map.setMyLocationEnabled(true);
		map.setOnMyLocationChangeListener(this);
		UiSettings uiSettings = map.getUiSettings();
		uiSettings.setAllGesturesEnabled(false);
		uiSettings.setMyLocationButtonEnabled(false);
		uiSettings.setZoomControlsEnabled(false);
	}
	
	public void goEquipment(View v){
		Intent intent = new Intent(this, EquipmentActivity.class);
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
		// Getting latitude of the current location
        double latitude = lastKnownLocation.getLatitude();
 
        // Getting longitude of the current location
        double longitude = lastKnownLocation.getLongitude();
 
        Geocoder geocoder = new Geocoder(DashboardActivity.this);
        String locality = "";
		try {
			List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
			locality = addresses.get(0).getLocality();
			locality += ", " + addresses.get(0).getAdminArea();
			locality += ", " + addresses.get(0).getCountryCode();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		TextView currentLocTextView = (TextView)findViewById(R.id.basicInfo_location);
		currentLocTextView.setText(locality);
		
        ParseGeoPoint currentLoc = new ParseGeoPoint(latitude,longitude);
        for(int i = 0; i < course.size(); i++){
        	TextView tempTextView = distance.get(i);
        	double distanceDouble = currentLoc.distanceInKilometersTo(course.get(i));
        	String distanceString = "";
        	if(distanceDouble*1000 < 400){
        		distanceString = "you are in the course";
        	}else{
        		distanceString = String.valueOf((int)(distanceDouble * 1000 - 400)) + " m";
        	}
        	tempTextView.setText(distanceString);
        }
        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);
		
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        
		map.animateCamera(CameraUpdateFactory.zoomTo(15));

		map.setOnCameraChangeListener(null);
	}

}
