package uq.deco7381.runspyrun.Activity;

import uq.deco7381.runspyrun.R;
import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class SuccessActivity extends Activity  implements OnMyLocationChangeListener {

	static final LatLng NKUT = new LatLng(23.979548, 120.696745);
    private GoogleMap map;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_success);
		
		// Check the map is exist or not
		if (map == null){
			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			
			if(map != null){
				setUpMap();
			}
		}
		
	}
	
	// Map set up
	private void setUpMap(){
		map.setMyLocationEnabled(true);
		map.setOnMyLocationChangeListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.success, menu);
		return true;
	}

	// Check if current location change
	@Override
	public void onMyLocationChange(Location lastKnownLocation) {
		// TODO Auto-generated method stub
		CameraUpdate myLoc = CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
		.target(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
		.zoom(14)
		.build());
		
		map.moveCamera(myLoc);
		map.setOnCameraChangeListener(null);
	}

}
