
package uq.deco7381.runspyrun.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import uq.deco7381.runspyrun.R;
import uq.deco7381.runspyrun.model.Course;
import uq.deco7381.runspyrun.model.Obstacle;
import uq.deco7381.runspyrun.model.ParseDAO;
import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.wikitude.architect.ArchitectUrlListener;
import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.ArchitectView.ArchitectConfig;
import com.wikitude.architect.SensorAccuracyChangeListener;

public class AttackActivity extends Activity implements  OnMyLocationChangeListener, ArchitectUrlListener{

	private GoogleMap map;
	private MapFragment mMapFragment;
	protected ArchitectView architectView;
	protected SensorAccuracyChangeListener sensorAccuracyListener;
	protected Location lastKnownLocaton;
	protected LocationManager locationManager;
	protected JSONArray poiData = new JSONArray();
	private Course course;
	private ParseDAO dao;
	private int viewFlag = 1; //1 = Map View 2 = Architect View
	private boolean firstLoc;
	private boolean outsideZone; // Detec user start the game  out of the zone;
	private int counter1 = 1; // counter for checking distance to obstacles
	private HashMap<Obstacle, Boolean> obshash = new HashMap<Obstacle, Boolean>();
	private Boolean triggered = false; // for checking if defense already triggered
	private int dog_dist; // starting distance for guard dog
	private ArrayList<Obstacle> obstacles;
	private RelativeLayout viewGroup;
	private String alertmessage = "In mission - undetected";
	private int userEnergy;
	private String alertFlag = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attack);
		Intent intent = getIntent();
		dao = new ParseDAO();
		firstLoc = false;
		outsideZone = false;
		
		/*
		 * Get the Course's center point (where to put data stream) from intent
		 */
		double latitude = intent.getDoubleExtra("latitude", 0.0);
		double longitude = intent.getDoubleExtra("longtitude", 0.0);
		course = dao.getCourseByLoc(latitude, longitude);
		obstacles = dao.getObstaclesByCourse(course);
		userEnergy = ParseUser.getCurrentUser().getInt("energyLevel");
		
		locationManager = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
		/*
		 *  Check is GPS available
		 */
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			/*
			 *  Check the map is exist or not
			 */
			if (map == null){
				mMapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.db_map));
				map = mMapFragment.getMap();
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
		 * creates initial view using wikitude architectview
		 */
		viewGroup = (RelativeLayout)findViewById(R.id.RelativeLayout1);
		
		architectView = (ArchitectView)this.findViewById( R.id.architectView );
		final ArchitectConfig config = new ArchitectConfig("");
		architectView.onCreate( config );
		architectView.setVisibility(View.GONE);
			
		/*
		 * initializes a listener to check for accuracy of the compass - important for the positioning of the AR
		 * objects in the device view
		 * 
		 * advises user of accuracy level
		 */
		this.sensorAccuracyListener = new SensorAccuracyChangeListener() {
			@Override
			public void onCompassAccuracyChanged( int accuracy ) {
				/* UNRELIABLE = 0, LOW = 1, MEDIUM = 2, Height = 3 */
				if ( accuracy < SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM && AttackActivity.this != null && !AttackActivity.this.isFinishing() ) {
					Toast.makeText( AttackActivity.this, "Accuracy low", Toast.LENGTH_LONG ).show();
				}
			}
			
		};
		
		this.architectView.registerSensorAccuracyChangeListener( this.sensorAccuracyListener );
	}
	
	private void setUpMap(){
		map.setMyLocationEnabled(true);
		map.setOnMyLocationChangeListener(this);
		map.animateCamera(CameraUpdateFactory.zoomTo(15));
		UiSettings uiSettings = map.getUiSettings();
		uiSettings.setMyLocationButtonEnabled(false);
		uiSettings.setZoomControlsEnabled(false);
		
		map.addCircle(course.getCourseZone());
	}
	/**
	 * Set up the screen in normal condition
	 * 1. Set content visible
	 * 2. set loading progress invisible
	 */
	private void showMap(){
		viewFlag = 1;
		
		mMapFragment.getView().setVisibility(View.VISIBLE);
		architectView.setVisibility(View.INVISIBLE);
		
		this.viewGroup.removeView( this.architectView );
		
		
		// Force the View redraw
		architectView.invalidate();

		System.out.println("MAP MODE");
		System.out.println("MAP MODE");
		System.out.println("MAP MODE");
		System.out.println("MAP MODE");
		System.out.println("MAP MODE");
	}
	/**
	 * Set up the screen in loading condition
	 * 1. Set loading progress visible
	 * 2. Set content invisible
	 */
	private void showAR(){
		viewFlag = 2;
		
		if(this.viewGroup.getChildCount() < 3){
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
			this.viewGroup.addView(this.architectView, params);
		}

		architectView.setVisibility(View.VISIBLE);
		mMapFragment.getView().setVisibility(View.GONE);
		
		// Force the View redraw
		architectView.invalidate();

		System.out.println("AR MODE");
		System.out.println("AR MODE");
		System.out.println("AR MODE");
		System.out.println("AR MODE");
		System.out.println("AR MODE");
	}

	@Override
	public boolean urlWasInvoked(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}


	protected void onResume() {
		super.onResume();
		if ( this.architectView != null ) {
			this.architectView.onResume();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if ( this.architectView != null ) {
			this.architectView.onPause();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if ( this.architectView != null ) {
			if ( this.sensorAccuracyListener != null ) {
				this.architectView.unregisterSensorAccuracyChangeListener( this.sensorAccuracyListener );
			}
			this.architectView.onDestroy();
		}
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		if ( this.architectView != null ) {
			this.architectView.onLowMemory();
		}
	}
	
	/**
	 * Starts the Attack view in the device using wikitude architect view overlaying the html file with the AR objects
	 * 
	 * 
	 */
	
	@Override
	protected void onPostCreate( final Bundle savedInstanceState ) {
		super.onPostCreate( savedInstanceState );
		if ( this.architectView != null) {
			this.architectView.onPostCreate();
		}

		try {
			this.architectView.load("index.html");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if (!isLoading) {
			final Thread t = new Thread(loadData);
			t.start();
		}
		
		
	}
	
boolean isLoading = false;
	
	final Runnable loadData = new Runnable() {
		
		
		
		@Override
		public void run() {
			
			isLoading = true;
			
			final int WAIT_FOR_LOCATION_STEP_MS = 2000;
			
			/**
			 * Loop to ensure that location data is obtained before data from parse is loaded
			 */
			while (AttackActivity.this.lastKnownLocaton==null && !AttackActivity.this.isFinishing()) {
			
				AttackActivity.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(AttackActivity.this, "Scanning", Toast.LENGTH_SHORT).show();	
					}
				});

				try {
					Thread.sleep(WAIT_FOR_LOCATION_STEP_MS);
				} catch (InterruptedException e) {
					break;
				}
			}
			
			/**
			 * Gets data from parse database for course - preset course as this project is developed
			 * separately to the other elements of the application
			 * 
			 * To be changed when integrated into the main application
			 */
			HashMap<String, String> courseHashMap = new HashMap<String, String>();
			courseHashMap.put("id", course.getObjectID());
			courseHashMap.put("name", "Datastream");
			courseHashMap.put("description", "Objective");
			courseHashMap.put("latitude", String.valueOf(course.getLatitude()));
			courseHashMap.put("longitude", String.valueOf(course.getLongitude()));
			poiData.put(new JSONObject(courseHashMap));
			
			
			// System.out.println(obstacles.toString());
			// System.out.println(course.getObjectID());
			
			
			for(Obstacle obstacle: obstacles){
				HashMap<String, String> obstacleHashMap = new HashMap<String, String>();
				obstacleHashMap.put("id", obstacle.getObjectId());
				obstacleHashMap.put("name", obstacle.getType());
				obstacleHashMap.put("description", "Objective");
				obstacleHashMap.put("latitude",String.valueOf(obstacle.getLatitude()));
				obstacleHashMap.put("longitude", String.valueOf(obstacle.getLongitude()));
				//testHashMap.put("altitude", String.valueOf(70f));
				poiData.put(new JSONObject(obstacleHashMap));
			}
			
			/**
			 * If location is obtained calls javascrit file which displays the Attack View on the device
			 * Passes poiData into the javascript function
			 * 
			 * @see callJavaScript
			 */
			if (AttackActivity.this.lastKnownLocaton!=null && !AttackActivity.this.isFinishing()) {
				// TODO: you may replace this dummy implementation and instead load POI information e.g. from your database
				System.out.print(poiData.toString());
				AttackActivity.this.callJavaScript("World.loadPoisFromJsonData", new String[] { poiData.toString() });
				
			}
			isLoading = false;
		}
	};

	/**
	 * Adds data from to the javascript file name to enable the data to be passed to the javascript file
	 * 
	 * @param methodName - the name of the javascript file to be called
	 * @param arguments - the data to be passed to the javascript function in this case poiData - information about defences
	 */
	private void callJavaScript(final String methodName, final String[] arguments) {
		final StringBuilder argumentsString = new StringBuilder("");
		for (int i= 0; i<arguments.length; i++) {
			argumentsString.append(arguments[i]);
			if (i<arguments.length-1) {
				argumentsString.append(", ");
			}
		}
		
		if (this.architectView!=null) {
			final String js = ( methodName + "( " + argumentsString.toString() + " );" );
			this.architectView.callJavascript(js);
		}
	}

	@Override
	public void onMyLocationChange(Location location) {
		// TODO Auto-generated method stub
		/*
	        *  Make camera on map keep tracking user.
	        */
			LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
			map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
			if(firstLoc == false){
				map.animateCamera(CameraUpdateFactory.zoomTo(15));
				firstLoc = true;
			}
			map.setOnCameraChangeListener(null);
			
			ParseGeoPoint currentLoc = new ParseGeoPoint(location.getLatitude(),location.getLongitude());
			double distance = course.getParseGeoPoint().distanceInKilometersTo(currentLoc);
			System.out.println(distance);
			if(distance*1000 > 400){
				outsideZone = true;
				if(viewFlag == 2){
					showMap();
				}
			}else{
				if(viewFlag == 1){
					if(outsideZone == true){
						showAR();
					}else{
						Toast.makeText(this, "Please start from outside of the zone.", Toast.LENGTH_LONG).show();
					}
				}
			}
			
			if (location!=null) {
				this.lastKnownLocaton = location;
				if ( this.architectView != null ) {
					if ( location.hasAltitude() ) {
						this.architectView.setLocation( location.getLatitude(), location.getLongitude(), location.getAltitude(), location.hasAccuracy() ? location.getAccuracy() : 1000 );
						//System.out.println("altitude is "+ location.getAltitude());
					} else {
						this.architectView.setLocation( location.getLatitude(), location.getLongitude(), location.hasAccuracy() ? location.getAccuracy() : 1000 );
						
					}
				}
			}
			
			/*
			 * checking to see if obstacles have been triggered 
			 */
			
			if (viewFlag == 2) {
				
				System.out.println("Checking distance to obstacles");

				triggered = false;
				
				counter1 = 1;
				
				int obs_energycost = 0;
				
				// check whether obstacle has been triggered
				for(Obstacle obstacle: obstacles){
					//check distance between current location and all obstacles
				
					// get obstacle type
					String obs_type = obstacle.getType();
					double obs_triggerdistance = obstacle.getTriggerDistance();
					//System.out.println(obs_triggerdistance);
				
					// get distance from currentLoc to obstacle
					double obsdistance = (obstacle.getParseGeoPoint().distanceInKilometersTo(currentLoc) * 1000);
					System.out.println("Dis to obs"+counter1+" "+obs_type+" is "+obsdistance);
					System.out.println("Trigger distance is "+obs_triggerdistance);
					
					// check if obstacle already triggered
					triggered = obshash.containsKey(obstacle);
					System.out.println("triggered value is "+triggered);

					// check if user within distance to trigger obstacle
					// only do this if not already triggered
					// reduce users energy
					if ((obsdistance < obs_triggerdistance) && (triggered==false)) {
						// obstacle is triggered
						//
						
						
						if (obs_type=="Guard") {
							alertmessage = "Detection: Guard - energy reduction: "+(obstacle.getEnergyCost());
						}
						if (obs_type=="Dog") {
							alertmessage = "Detection: Dog - energy reduction: "+(obstacle.getEnergyCost());
							dog_dist = 30;
						}
						
						obshash.put(obstacle, true);
						/*
						 * When obstacle be triggered, it will reduce energy of user who trigger this obstacle.
						 * Also put half of stolen energy to player who create it.
						 */
						obs_energycost += (obstacle.getEnergyCost());
						dao.updateObstacleEnergy(obstacle, obstacle.getEnergyCost()/2);
						
						// vibrate phone
						Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
						vibrator.vibrate(500);
						
						// set alertFlag
						alertFlag = "ALERT!";
						System.out.println("First Triggered - "+alertFlag+" "+alertmessage);
					}
					
					// events when already detected still within trigger distance
					if ((obsdistance < obs_triggerdistance) && (triggered)) {
						//System.out.println("still within obs distance but not doing anything");
						if (obs_type=="Guard") {
							//System.out.println("The Guard can still see you!");
							alertmessage = "Guard approaching";
						}
						if (obs_type=="Dog") {
							dog_dist -= 1;
							alertmessage = "Dog chasing you - "+dog_dist+"m away";
						}
						alertFlag = "ALERT!";
						System.out.println("Second Trigger - "+alertFlag+" "+alertmessage);
					}
					
					// obstacle no longer triggered
					// reset obshash so that obstacle can be triggered again
					if (obsdistance > obs_triggerdistance && triggered){
						obshash.remove(obstacle);
						alertmessage ="In mission - undetected";
						alertFlag = "";
					}
					
					counter1 += 1;
				}
				/*
				 * If user have trigger any obstacle, call to update user's energy in Sever
				 */
				if(obs_energycost > 0){
					userEnergy -= obs_energycost;
					dao.updateEnergyByEnergy(ParseUser.getCurrentUser(), userEnergy);
				}
				
				
				/**
				 * Check to see if at data source
				 */
				if (distance*1000<10) {
					alertmessage = "You have reached the data stream";
					alertFlag = "ALERT!";
				}
				
				// update energy and alerts in AR view
				if (this.architectView!=null) {
					this.architectView.callJavascript("World.updateEnergyValue( '"+userEnergy+"' );");			
					final String alertRightText = ( "World.updateAlertElementRight( '"+alertmessage.toString()+"' );" );
					this.architectView.callJavascript(alertRightText);
					final String alertLeftText = ( "World.updateAlertElementLeft( '"+alertFlag.toString()+"' );" );
					this.architectView.callJavascript(alertLeftText);
				}
			}
	}

}
