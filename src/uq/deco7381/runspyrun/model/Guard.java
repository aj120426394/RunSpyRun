package uq.deco7381.runspyrun.model;

import uq.deco7381.runspyrun.R;
import android.location.LocationManager;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;

/**
 * 
 * @author Jafo
 *
 */
public class Guard extends Obstacle {
	// Get the icon id
	
	public Guard(ParseGeoPoint location, String org, String username) {
		super(location, "Guard", org, username);
		// TODO Auto-generated constructor stub
	}
	public MarkerOptions getMarker(){
		MarkerOptions markerOptions = new MarkerOptions()
										.position(new LatLng(getLatitude(), getLongitude()))
										.title("Guard")
										.icon(BitmapDescriptorFactory.fromResource(R.drawable.obstacle_guard));
		
		return markerOptions;
										
	}
	public String getType(){
		return "Guard";
	}
	

}
