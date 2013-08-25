package uq.deco7381.runspyrun.model;

import uq.deco7381.runspyrun.R;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Guard extends Obstacle {
	// Get the icon id
	
	public Guard(LatLng latLng, String org, String username) {
		super(latLng, "Guard",org, username);
		// TODO Auto-generated constructor stub
	}
	public MarkerOptions getMarker(){
		MarkerOptions markerOptions = new MarkerOptions()
										.position(getLatLng())
										.title("Guard")
										.icon(BitmapDescriptorFactory.fromResource(R.drawable.obstacle_guard));
		
		return markerOptions;
										
	}
	public String getType(){
		return "Guard";
	}
	

}
