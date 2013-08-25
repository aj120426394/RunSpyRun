package uq.deco7381.runspyrun.activity;

import uq.deco7381.runspyrun.R;
import uq.deco7381.runspyrun.model.Guard;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

public class DefenceActivity extends Activity implements OnMyLocationChangeListener {
	
	private GoogleMap map;
	private LocationManager status;
	private Location currentLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_defence);
		
		Intent intent = getIntent();
		
		// Get the Course's center point (where to put data stream) from intent
		double latitude = intent.getDoubleExtra("latitude", 0.0);
		double longtitude = intent.getDoubleExtra("longtitude", 0.0);

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
		LatLng latLng = new LatLng(latitude, longtitude);
		setCourse(latLng);
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
		CircleOptions circleOptions = new CircleOptions()
			.center(latLng)
			.radius(500)
			.fillColor(0x40ff0000)
			.strokeColor(Color.TRANSPARENT)
			.strokeWidth(2);
		map.addCircle(circleOptions);
	}
	// Setting the Guard on the map
	public void setGuard(View v){
		LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
		Guard g1 = new Guard(latLng);
		map.addMarker(g1.getMarker());
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




}
