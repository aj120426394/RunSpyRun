package uq.deco7381.runspyrun.activity;

import uq.deco7381.runspyrun.R;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

public class EquipmentActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {
/*
 * Using the Google play service: Location Client to catch the current location.
 * It avoid the unnecessary method: Location Listener, and get the location faster and more precise.
 * 
 * Founded Issue of Android build-in method: Location Manager
 * need LocationChange method to help to get the latest location, and that's not proper for this class.
 * 
 */
	private LocationClient lClient;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_equipment);
		
		lClient = new LocationClient(this, this, this);

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.equipment, menu);
		return true;
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		lClient.connect();
	}
	
	

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		lClient.disconnect();
		super.onStop();
	}

	public void setDatastream(View v){
		Intent intent = new Intent(this, DefenceActivity.class);
		
		
		Location location = lClient.getLastLocation();
		
		intent.putExtra("latitude", location.getLatitude());
		intent.putExtra("longtitude", location.getLongitude());
		
		startActivity(intent);
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
		
	}


}
