package uq.deco7381.runspyrun.activity;

import java.util.List;

import org.w3c.dom.Text;

import uq.deco7381.runspyrun.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class DashboardActivity extends Activity implements OnMyLocationChangeListener {
	
	private GoogleMap map;
	private boolean firstStart = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		
		LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
		
		// Check is GPS available
		if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			// Check the map is exist or not
			if (map == null){
				map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
				
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
		Intent intent = getIntent();
		String username = intent.getStringExtra("username");
		TextView usernameTextView = (TextView)findViewById(R.id.basicInfo_name);
		usernameTextView.setText(username);
	}

	
	// Map set up
	private void setUpMap(){
		map.setMyLocationEnabled(true);
		map.setOnMyLocationChangeListener(this);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dashboard, menu);
		return true;
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
        
		if(firstStart == false){
			map.animateCamera(CameraUpdateFactory.zoomTo(15));
			firstStart = true;
		}
		
		ParseGeoPoint userLocation = new ParseGeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
		ParseQuery<ParseObject> query = ParseQuery.getQuery("testMarker");
		query.whereNear("location", userLocation);
		query.setLimit(10);
		query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null){
					for(int i = 0; i < objects.size(); i++){
						ParseObject parseObject = objects.get(i);
						ParseGeoPoint location = parseObject.getParseGeoPoint("location");
						LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
						map.addMarker(new MarkerOptions()
						.position(latLng)
						.title(parseObject.getString("title")));
					}
				}
			}
		});
		
		
		
		map.setOnCameraChangeListener(null);
	}

}
