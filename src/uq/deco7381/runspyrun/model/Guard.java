package uq.deco7381.runspyrun.model;

import uq.deco7381.runspyrun.R;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseUser;

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
	private static int triggerDistance = 30;
	
	public Guard(double latitude,double longitude,double altitude, ParseUser creator, int level, String objectId) {
		super(latitude, longitude, altitude, "Guard", creator, level, objectId, triggerDistance);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public MarkerOptions getMarkerOptions() {
		// TODO Auto-generated method stub
		MarkerOptions markerOptions = new MarkerOptions()
		.position(new LatLng(getLatitude(), getLongitude()))
		.title("Guard")
		.icon(BitmapDescriptorFactory.fromResource(R.drawable.obstacle_guard));

		return markerOptions;
	}

	
	
}
