package uq.deco7381.runspyrun.activity;

import java.util.ArrayList;
import java.util.List;

import uq.deco7381.runspyrun.R;
import uq.deco7381.runspyrun.model.Course;
import uq.deco7381.runspyrun.model.Guard;
import uq.deco7381.runspyrun.model.Obstacle;
import uq.deco7381.runspyrun.model.ParseDAO;
import android.R.integer;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseObject;
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
	private View mContentView;	// The view contain the whole content.
	private View mLoadingView;	// The view contain the process animation.
	private int userEnergy;

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
		userEnergy = ParseUser.getCurrentUser().getInt("energyLevel");

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
		displayEnergy();
		
		mContentView = findViewById(R.id.content);
		mLoadingView = findViewById(R.id.loading);
		mLoadingView.setVisibility(View.GONE);
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
		showLoading();
		
		final Intent intent = new Intent(this, DashboardActivity.class);
		SaveCallback saveCallback = new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				// TODO Auto-generated method stub
				if(e == null){
					startActivity(intent);
				}else{
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		};
		
		if(isFrom.equals("newCourse")){
			newCourse(saveCallback);
		}else if(isFrom.equals("existMission")){
			existMission(saveCallback);
		}else if(isFrom.equals("existCourse")){
			existCourse(saveCallback);
		}
		
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
	private void displayEnergy(){
		String energyString = String.valueOf(userEnergy) + "/" + String.valueOf(ParseUser.getCurrentUser().getInt("level")*100);
		TextView energy = (TextView)findViewById(R.id.energy);
		energy.setText(energyString);
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
		int COST = 40; //Cost 40 energy to set a guard in lv 1
		int userLevel = ParseUser.getCurrentUser().getInt("level");
		int userSpend = COST * userLevel;
		if(userSpend > 1000){
			userSpend  = 1000; // Maximum cost of a guard is 1000 energy.
		}
		if(userSpend < this.userEnergy){
			ParseUser currentUser = ParseUser.getCurrentUser();
			Guard g1 = new Guard(currentLocation.getLatitude(),currentLocation.getLongitude(),currentLocation.getAltitude(), currentUser, currentUser.getInt("level"),null);
			map.addMarker(g1.getMarkerOptions());
			
			/*
			 *  Add to the list of new obstacle
			 */
			newObstaclesOnCourse.add(g1);
			this.userEnergy -= userSpend;
			displayEnergy();
		}else{
			Toast.makeText(getApplicationContext(), "You don't have enough energy.", Toast.LENGTH_LONG).show();
		}
		
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
	public void newCourse(SaveCallback saveCallback){
		
		List<ParseObject> objectList = new ArrayList<ParseObject>();
		/*
		 *  Transfer Course to parse object
		 */
		ParseObject courseParseObject = dao.createCourseParseObject(this.course);
		objectList.add(courseParseObject);
		
		/*
		 * Transfer Obstacle to parse object
		 */
		for(Obstacle obstacle: newObstaclesOnCourse){
			ParseObject obstacleParseObject = dao.createObstacleParseObject(obstacle, courseParseObject);
			objectList.add(obstacleParseObject);
		}
		/*
		 *  Transfer Mission to parse object
		 */
		
		//dao.insertMissionByNewCourse(ParseUser.getCurrentUser(), courseParseObject);
		ParseObject missionParseObject = dao.createMissionParseObject(ParseUser.getCurrentUser(), courseParseObject);
		objectList.add(missionParseObject);
		
		/*
		 *  Update equipment
		 */
		dao.updateEquipment(ParseUser.getCurrentUser(), newObstaclesOnCourse);
		dao.updateEnergyByEnergy(ParseUser.getCurrentUser(), this.userEnergy);
		/*
		 * save all all Course,Obstacle,Mission to server.
		 */
		dao.insertToServer(objectList,saveCallback);

	}
	/**
	 * This method will called by missionDecide() when user's state is "Existing Mission"
	 * 1. Save Obstacle
	 * 
	 * @see missionDecide(View v)
	 */
	public void existMission(SaveCallback saveCallback){
		List<ParseObject> objectList = new ArrayList<ParseObject>();
		/*
		 * Transfer Obstacle to parse object
		 */
		for(Obstacle obstacle: newObstaclesOnCourse){
			ParseObject obstacleParseObject = dao.createObstacleParseObject(obstacle, ParseObject.createWithoutData("Course", this.course.getObjectID()));
			objectList.add(obstacleParseObject);
		}
		/*
		 *  Update equipment
		 */
		dao.updateEquipment(ParseUser.getCurrentUser(), newObstaclesOnCourse);
		/*
		 * save all all Course,Obstacle,Mission to server.
		 */
		dao.insertToServer(objectList,saveCallback);
	}
	
	/**
	 * This method will called by missionDecide() when user's state is "New Mission with existing course"
	 * 1. Save Obstacle
	 * 2. Save Mission
	 * @see missionDecide(View v)
	 */
	public void existCourse(SaveCallback saveCallback){
		List<ParseObject> objectList = new ArrayList<ParseObject>();
		
		/*
		 * Transfer Obstacle to parse object
		 */
		for(Obstacle obstacle: newObstaclesOnCourse){
			ParseObject obstacleParseObject = dao.createObstacleParseObject(obstacle, ParseObject.createWithoutData("Course", this.course.getObjectID()));
			objectList.add(obstacleParseObject);
		}
		/*
		 *  Transfer Mission to parse object
		 */
		
		ParseObject missionParseObject = dao.createMissionParseObject(ParseUser.getCurrentUser(), ParseObject.createWithoutData("Course", this.course.getObjectID()));
		objectList.add(missionParseObject);
		/*
		 *  Update equipment
		 */
		dao.updateEquipment(ParseUser.getCurrentUser(), newObstaclesOnCourse);
		/*
		 * save all all Course,Obstacle,Mission to server.
		 */
		dao.insertToServer(objectList,saveCallback);
	}

	/**
	 * Set up the screen in loading condition
	 * 1. Set loading progress visible
	 * 2. Set content invisible
	 */
	private void showLoading(){
		mContentView.setVisibility(View.GONE);
		mLoadingView.setVisibility(View.VISIBLE);
		
		mContentView.invalidate();
		mLoadingView.invalidate();
			
	}

}
