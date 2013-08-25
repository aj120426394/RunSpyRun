package uq.deco7381.runspyrun.activity;

import java.util.List;

import uq.deco7381.runspyrun.R;
import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
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
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class DashboardActivity extends Activity implements OnMyLocationChangeListener {
	
	private GoogleMap map;
	private LocationManager status;

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
		
		System.out.println("DEBUGGING");
		
		
		// Get user info from Parse server
		setUserInfo();
		
		
		
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dashboard, menu);
		return true;
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
 
        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);
		
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        
		map.animateCamera(CameraUpdateFactory.zoomTo(15));

		map.setOnCameraChangeListener(null);
	}

}
