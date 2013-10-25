package uq.deco7381.runspyrun.model;

import uq.deco7381.runspyrun.R;
import android.R.integer;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseUser;

public class DetectionPlate extends Obstacle {

	private static int triggerDistance = 20;
	private static int energyCost = 20;
	
	public DetectionPlate(double latitude,double longitude,double altitude, ParseUser creator, int level, String objectId) {
		super(latitude, longitude, altitude, "DetectionPlate", creator, level, objectId, triggerDistance,energyCost);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public MarkerOptions getMarkerOptions() {
		// TODO Auto-generated method stub
		MarkerOptions markerOptions = new MarkerOptions()
		.position(new LatLng(getLatitude(), getLongitude()))
		.title("DetectionPlate")
		.icon(BitmapDescriptorFactory.fromResource(R.drawable.obstacle_detectionplate));

		return markerOptions;
	}

}
