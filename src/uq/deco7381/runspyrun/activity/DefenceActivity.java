package uq.deco7381.runspyrun.activity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import uq.deco7381.runspyrun.R;
import uq.deco7381.runspyrun.model.Guard;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
		
		map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		map.animateCamera(CameraUpdateFactory.zoomTo(15));
		
		Guard g1 = new Guard(latLng);
		
		map.addMarker(g1.getIcon());
		
		setCourse(latLng);
	}

	private void setUpMap(){
		map.setMyLocationEnabled(true);
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.defence, menu);
		return true;
	}




}
