package uq.deco7381.runspyrun.activity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import uq.deco7381.runspyrun.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

public class DefenceActivity extends Activity {
	
	private GoogleMap map;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_defence);
		
		Intent intent = getIntent();
		
		double latitude = intent.getDoubleExtra("latitude", 0.0);
		double longtitude = intent.getDoubleExtra("longtitude", 0.0);
		/*
		String loc = "(" + String.valueOf(latitude) + ", " + String.valueOf(longtitude)+")"; 
		TextView testTextView = (TextView)findViewById(R.id.textView1);
		testTextView.setText(loc);
		*/
		// Map set up
		LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
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
		
		LatLng latLng = new LatLng(latitude, longtitude);
		
		map.addMarker(new MarkerOptions().position(latLng).title("Test"));
	}

	private void setUpMap(){
		map.setMyLocationEnabled(true);
		/*
		UiSettings uiSettings = map.getUiSettings();
		uiSettings.setAllGesturesEnabled(true);
		uiSettings.setMyLocationButtonEnabled(true);
		uiSettings.setZoomControlsEnabled(true)
		*/
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.defence, menu);
		return true;
	}




}
