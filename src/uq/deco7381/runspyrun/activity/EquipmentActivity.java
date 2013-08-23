package uq.deco7381.runspyrun.activity;

import uq.deco7381.runspyrun.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class EquipmentActivity extends Activity implements LocationListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_equipment);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.equipment, menu);
		return true;
	}
	
	public void setDatastream(View v){
		Intent intent = new Intent(this, DefenceActivity.class);
		
		LocationManager locManager;
		
		locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f, this);
		Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		intent.putExtra("latitude", location.getLatitude());
		intent.putExtra("longtitude", location.getLongitude());
		
		startActivity(intent);
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
