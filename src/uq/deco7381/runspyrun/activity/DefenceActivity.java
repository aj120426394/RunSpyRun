package uq.deco7381.runspyrun.activity;

import java.util.ArrayList;
import java.util.List;

import uq.deco7381.runspyrun.R;
import uq.deco7381.runspyrun.model.Course;
import uq.deco7381.runspyrun.model.Guard;
import uq.deco7381.runspyrun.model.Obstacle;
import android.app.Activity;
import android.app.DownloadManager.Query;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class DefenceActivity extends Activity implements OnMyLocationChangeListener {
	
	private GoogleMap map;
	private LocationManager status;
	private Location currentLocation;
	private String isFrom;
	private ArrayList<Obstacle> obstaclesOnCourse;
	private Course course;
	private LatLng courseLatLng;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_defence);
		obstaclesOnCourse = new ArrayList<Obstacle>();
		Intent intent = getIntent();
		
		// Get the Course's center point (where to put data stream) from intent
		double latitude = intent.getDoubleExtra("latitude", 0.0);
		double longtitude = intent.getDoubleExtra("longtitude", 0.0);
		isFrom = intent.getStringExtra("isFrom");

		// Map set up
		status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
		if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			// Check the map is exist or not
			if (map == null){
				map = ((MapFragment) getFragmentManager().findFragmentById(R.id.defence_map)).getMap();
				if(map != null){
					setUpMap();
				}
			}
		}else{
			Toast.makeText(this, "Please open the GPS", Toast.LENGTH_LONG).show();
			startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		}
		
		//Set the course (make it visible on the map)
		courseLatLng = new LatLng(latitude, longtitude);
		setCourse(courseLatLng);
		if(isFrom.equals("exsitMission") || isFrom.equals("exsitCourse")){
			displayObstacle();
		}
	}

	public void missionDecide(View v){
		if(isFrom.equals("newCourse")){
			newCourse();
		}else if(isFrom.equals("existMission")){
			existMission();
		}else if(isFrom.equals("existCourse")){
			existCourse();
		}
		
		Intent intent = new Intent(this, DashboardActivity.class);
		startActivity(intent);
	}
	
	private void displayObstacle(){
		/*
		ParseQuery<ParseObject> course = ParseQuery.getQuery("Course");
		ParseGeoPoint courseCenter = new ParseGeoPoint(courseLatLng.latitude, courseLatLng.longitude);
		course.whereEqualTo("location", courseCenter);
		
		ParseQuery<ParseObject> obstacleQuery = ParseQuery.getQuery("Obstacle");
		obstacleQuery.whereMatchesQuery("course", course);
		obstacleQuery.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if(objects.size() != 0 && e == null){
					for(ParseObject obstacle: objects){
						double latitude = obstacle.getParseGeoPoint("location").getLatitude();
						double longitude = obstacle.getParseGeoPoint("location").getLongitude();
						final String org = obstacle.getString("organization");
						final LatLng location = new LatLng(latitude, longitude);
						
						obstacle.getParseUser("creator")
							.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
								@Override
								public void done(ParseObject object,
										ParseException e) {
									// TODO Auto-generated method stub
									if(e == null){
										String username = object.getString("username");
										Guard g1 = new Guard(location,org,username);
										map.addMarker(g1.getMarker());
									}
								}
							});
	
					}
				}else{
					System.out.println(e.getMessage());
				}
			}
		});*/
		ParseQuery<ParseObject> course = ParseQuery.getQuery("Course");
		ParseGeoPoint courseCenter = new ParseGeoPoint(courseLatLng.latitude, courseLatLng.longitude);
		course.whereEqualTo("location", courseCenter);
		
		ParseQuery<ParseObject> obstacleQuery = ParseQuery.getQuery("Obstacle");
		obstacleQuery.whereMatchesQuery("course", course);
		obstacleQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if(e == null){
					System.out.println(objects.get(0).getString("organization"));
				}else{
					System.out.println(e.getMessage());
				}
			}
		});
		
	}
	// Set up the map with
	private void setUpMap(){
		map.setMyLocationEnabled(true);
		map.setOnMyLocationChangeListener(this);
		UiSettings uiSettings = map.getUiSettings();
		uiSettings.setMyLocationButtonEnabled(true);
		uiSettings.setZoomControlsEnabled(true);
	}
	
	// Set the course on the location
	private void setCourse(LatLng latLng){
		ParseUser currentUser = ParseUser.getCurrentUser();
		course= new Course(latLng, currentUser.getUsername(),currentUser.getString("organization"));
		map.addCircle(course.getCourseZone());
	}
	// Setting the Guard on the map
	public void setGuard(View v){
		LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
		ParseUser currentUser = ParseUser.getCurrentUser();
		Guard g1 = new Guard(latLng,currentUser.getString("organization"),currentUser.getUsername());
		map.addMarker(g1.getMarker());
		obstaclesOnCourse.add(g1);
	}
	// Update the current location from OnMyLocationChange()
	private void setCurrentLocation(Location location){
		this.currentLocation = location;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.defence, menu);
		return true;
	}
	
	@Override
	public void onMyLocationChange(Location lastKnownLocation) {
		// TODO Auto-generated method stub
		setCurrentLocation(lastKnownLocation);
		// Getting latitude of the current location
        double latitude = lastKnownLocation.getLatitude();
 
        // Getting longitude of the current location
        double longitude = lastKnownLocation.getLongitude();
 
        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);
		
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		map.animateCamera(CameraUpdateFactory.zoomTo(15));
		map.setOnCameraChangeListener(null);
		
	}

	public void newCourse(){
		// Save new Course
		ParseObject parseCourse = new ParseObject("Course");
		parseCourse.put("owner",ParseUser.getCurrentUser());
		parseCourse.put("location", new ParseGeoPoint(course.getLatitude(),course.getLongitude()));
		parseCourse.put("level", course.getLevel());
		parseCourse.put("organization", course.getOrg());
		parseCourse.saveInBackground();
		
		// Save new Obstacle
		for(Obstacle obstacle: obstaclesOnCourse){
			ParseObject object = new ParseObject("Obstacle");
			object.put("creator", ParseUser.getCurrentUser());
			object.put("energy", obstacle.getEnergy());
			object.put("location", new ParseGeoPoint(obstacle.getLatitude(), obstacle.getLongitude()));
			object.put("type", obstacle.getType());
			object.put("organization", obstacle.getOrg());
			object.put("course", parseCourse);
			object.saveInBackground(new SaveCallback() {
				@Override
				public void done(ParseException e) {
					// TODO Auto-generated method stub
					if(e == null){
						System.out.println("SUCCESS");
					}else{
						Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
					}
				}
			});
		}
		// Save new mission to mission list
		ParseObject mission = new ParseObject("Mission");
		mission.put("course", parseCourse);
		mission.put("username", ParseUser.getCurrentUser());
		mission.saveInBackground();

	}
	public void existMission(){
		ParseQuery<ParseObject> course = ParseQuery.getQuery("Course");
		course.whereEqualTo("location", new ParseGeoPoint(courseLatLng.latitude,courseLatLng.longitude));
		course.getFirstInBackground(new GetCallback<ParseObject>() {
			@Override
			public void done(ParseObject object, ParseException e) {
				// TODO Auto-generated method stub
				if(e == null){
					for(Obstacle obstacle: obstaclesOnCourse){
						ParseObject test = new ParseObject("Obstacle");
						test.put("creator", ParseUser.getCurrentUser());
						test.put("energy", obstacle.getEnergy());
						test.put("type", obstacle.getType());
						test.put("organization", obstacle.getOrg());
						test.put("course", object);
						test.saveInBackground(new SaveCallback() {
							@Override
							public void done(ParseException e) {
								// TODO Auto-generated method stub
								if(e == null){
									System.out.println("SUCCESS");
								}else{
									Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
								}
							}
						});
					}
				}else{
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	public void existCourse(){
		ParseQuery<ParseObject> course = ParseQuery.getQuery("Course");
		course.whereEqualTo("location", new ParseGeoPoint(courseLatLng.latitude,courseLatLng.longitude));
		course.getFirstInBackground(new GetCallback<ParseObject>() {
			@Override
			public void done(ParseObject object, ParseException e) {
				// TODO Auto-generated method stub
				if(e == null){
					for(Obstacle obstacle: obstaclesOnCourse){
						ParseObject test = new ParseObject("Obstacle");
						test.put("creator", ParseUser.getCurrentUser());
						test.put("energy", obstacle.getEnergy());
						test.put("type", obstacle.getType());
						test.put("organization", obstacle.getOrg());
						test.put("course", object);
						test.saveInBackground(new SaveCallback() {
							@Override
							public void done(ParseException e) {
								// TODO Auto-generated method stub
								if(e == null){
									System.out.println("SUCCESS");
								}else{
									Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
								}
							}
						});
					}
					// Save new mission to mission list
					ParseObject mission = new ParseObject("Mission");
					mission.put("course",object);
					mission.put("username", ParseUser.getCurrentUser());
					mission.saveInBackground();
				}else{
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		});
		
		
	}


}
