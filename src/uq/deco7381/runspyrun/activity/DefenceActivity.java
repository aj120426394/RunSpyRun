package uq.deco7381.runspyrun.activity;

import java.util.ArrayList;
import java.util.List;

import uq.deco7381.runspyrun.R;
import uq.deco7381.runspyrun.model.Course;
import uq.deco7381.runspyrun.model.Guard;
import uq.deco7381.runspyrun.model.Obstacle;
import uq.deco7381.runspyrun.model.ParseDAO;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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
/**
 * This is an Activity with Defense mode, it inherit from OnMyLocationChangeListener
 * which is going to track user's current location.
 *  
 * There are three state for user to get into this Activity:
 * 1. Create a new course
 * 2. Create new mission from a existing course
 * 3. Existing mission
 * 
 * Functionality:
 * 1. Display user current location
 * 2. Display zone of the course
 * 3. Display obstacle in the zone
 * 
 * @author Jafo
 *
 */
public class DefenceActivity extends Activity implements OnMyLocationChangeListener {
	
	private GoogleMap map;
	private LocationManager status;
	private Location currentLocation;
	private String isFrom; 				// Determine which Activity is user coming from
	private ArrayList<Obstacle> newObstaclesOnCourse;	// Save the obstacle when user create new.
	private Course course;				// Course of this mode
	private ParseDAO dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_defence);
		newObstaclesOnCourse = new ArrayList<Obstacle>();
		Intent intent = getIntent();
		dao = new ParseDAO(); 
		
		/*
		 * Get the Course's center point (where to put data stream) from intent
		 */
		double latitude = intent.getDoubleExtra("latitude", 0.0);
		double longitude = intent.getDoubleExtra("longtitude", 0.0);
		isFrom = intent.getStringExtra("isFrom");

		/*
		 *  Map set up
		 */
		status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
		if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			/*
			 *  Check the map is exist or not
			 */
			if (map == null){
				map = ((MapFragment) getFragmentManager().findFragmentById(R.id.defence_map)).getMap();
			}
			setUpMap();
		}else{
			Toast.makeText(this, "Please open the GPS", Toast.LENGTH_LONG).show();
			startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		}
		
		/*
		 * Set the course (make it visible on the map)
		 */
		if(isFrom.equals("exsitMission") || isFrom.equals("exsitCourse")){
			this.course = dao.getCourseByLoc(latitude, longitude);
			ArrayList<Obstacle> obstacles  = dao.getObstaclesByCourse(this.course);
			displayObstacle(obstacles);
		}else{
			this.course= new Course(latitude,longitude, ParseUser.getCurrentUser(), ParseUser.getCurrentUser().getInt("level"), null, ParseUser.getCurrentUser().getString("organization"));
		}
		displayCourse(this.course);
	}

	/**
	 * onClick method triggered by "Create"
	 * 1. Determine which method to execute base on user's state
	 * 
	 * @see newCourse()
	 * @see existMission()
	 * @see existCourse()
	 * 
	 * @param v   
	 * 
	 */
	public void missionDecide(View v){
		if(isFrom.equals("newCourse")){
			newCourse();
		}else if(isFrom.equals("existMission")){
			existMission();
		}else if(isFrom.equals("existCourse")){
			existCourse();
		}
		
		Intent intent = new Intent(this, DashboardActivity.class);
		/*
		 * Because some database connection time issue.
		 * Force program sleep for 5 second.
		 */
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startActivity(intent);
	}
	/**
	 * This function would be called when user come from "New mission" and "Existing mission" which the course is already developed.
	 * 
	 * 1. Add the obstacle into Map.
	 * 
	 */
	private void displayObstacle(ArrayList<Obstacle> obstacles){
		for(Obstacle obstacle: obstacles){
			map.addMarker(obstacle.getMarkerOptions());
		}
	}
	/**
	 * Set up a Google map.
	 * Remove the button: zoom
	 * Remove the button: find my location
	 * Initiate the zoom of camera to 15
	 */
	private void setUpMap(){
		map.setMyLocationEnabled(true);
		map.setOnMyLocationChangeListener(this);
		map.animateCamera(CameraUpdateFactory.zoomTo(15));
		UiSettings uiSettings = map.getUiSettings();
		uiSettings.setMyLocationButtonEnabled(false);
		uiSettings.setZoomControlsEnabled(false);
	}
	
	
	/**
	 * Set up a course:
	 * 1. Display the zone
	 * 2. Set the course object
	 * 
	 * @param latLng
	 */
	private void displayCourse(Course course){
		map.addCircle(course.getCourseZone());
	}
	/**
	 * Set up a new obstacle: Guard:
	 * Base on your current location
	 * 
	 * @param v
	 */
	public void setGuard(View v){;
		ParseUser currentUser = ParseUser.getCurrentUser();
		Guard g1 = new Guard(currentLocation.getLatitude(),currentLocation.getLongitude(),currentLocation.getAltitude(), currentUser, currentUser.getInt("level"),null);
		map.addMarker(g1.getMarkerOptions());
		
		/*
		 *  Add to the list of new obstacle
		 */
		newObstaclesOnCourse.add(g1);
	}
	
	/**
	 * Set "currentLocation" to the actual current location
	 * 1. Transform the type of location to ParseGeoPoint
	 * 
	 * @param location: Actual current location from OnMyLocationChange
	 * @see onMyLocationChange(Location lastKnownLocation)
	 */
	private void setCurrentLocation(Location location){
		this.currentLocation = location;
	}
	
	@Override
	public void onMyLocationChange(Location lastKnownLocation) {
		// TODO Auto-generated method stub
		setCurrentLocation(lastKnownLocation);
		/*
		 *  Getting latitude of the current location
		 */
        double latitude = lastKnownLocation.getLatitude();
 
        /*
         *  Getting longitude of the current location
         */
        double longitude = lastKnownLocation.getLongitude();
 
        /*
         *  Creating a LatLng object for the current location
         */
        LatLng latLng = new LatLng(latitude, longitude);
		
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
		
		map.setOnCameraChangeListener(null);
		
	}

	/**
	 * This method will called by missionDecide() when user's state is "Create a new course"
	 * 1. Save new course.
	 * 2. Save new obstacle
	 * 3. Save new mission
	 * 4. Update the number of equipment user have (only available for datasourse in the moment)
	 * 
	 * @see missionDecide(View v)
	 */
	public void newCourse(){
		/*
		 *  Save new Course
		 */
		
		ParseObject courseParseObject = dao.insertCourse(this.course);
		
		/*
		 *  Save new Obstacle
		 */
		for(Obstacle obstacle: newObstaclesOnCourse){
			dao.insertObstaclesByNewCourse(obstacle, courseParseObject);
		}
		/*
		 *  Save new mission to mission list
		 */
		
		dao.insertMissionByNewCourse(ParseUser.getCurrentUser(), courseParseObject);
		
		/*
		 *  Update equipment
		 */
		dao.updateEquipment(ParseUser.getCurrentUser(), newObstaclesOnCourse);

	}
	/**
	 * This method will called by missionDecide() when user's state is "Existing Mission"
	 * 1. Save Obstacle
	 * 
	 * @see missionDecide(View v)
	 */
	public void existMission(){
		/*
		 * Get the current course data from database
		 */
		for(Obstacle obstacle: newObstaclesOnCourse){
			dao.insertObstacle(this.course, obstacle);
		}
		dao.updateEquipment(ParseUser.getCurrentUser(), newObstaclesOnCourse);
	}
	
	/**
	 * This method will called by missionDecide() when user's state is "New Mission with existing course"
	 * 1. Save Obstacle
	 * 2. Save Mission
	 * @see missionDecide(View v)
	 */
	public void existCourse(){
		for(Obstacle obstacle: newObstaclesOnCourse){
			dao.insertObstacle(this.course, obstacle);
		}
		dao.insertMission(ParseUser.getCurrentUser(), this.course);
		dao.updateEquipment(ParseUser.getCurrentUser(), newObstaclesOnCourse);
	}


}
