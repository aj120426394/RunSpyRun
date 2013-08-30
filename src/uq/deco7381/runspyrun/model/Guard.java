package uq.deco7381.runspyrun.model;

import uq.deco7381.runspyrun.R;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * This class is a type of obstacle, most of the function are inherited from Obstacle.class
 * extra method: Giving the MarkOption (put the icon on the map)
 * @author Jafo
 *
 */
public class Guard extends Obstacle {
	/**
	 * Constructor
	 * 
	 * @param latitude
	 * @param longitude
	 * @param altitude
	 * @param username
	 * @param level
	 */
	public Guard(double latitude,double longitude,double altitude, String username, int level) {
		super(latitude, longitude, altitude, "Guard", username, level);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Get the MarkerOption to place the icon on the Google map
	 * 
	 * @return MarkerOption: the MarkerOption can set on the google map
	 */
	public MarkerOptions getMarkerOptions(){
		MarkerOptions markerOptions = new MarkerOptions()
										.position(new LatLng(getLatitude(), getLongitude()))
										.title("Guard")
										.icon(BitmapDescriptorFactory.fromResource(R.drawable.obstacle_guard));
		
		return markerOptions;
										
	}
	

}
