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
	private static int triggerDistance = 10;
	private static int energyCost = 50;
	public int MAX_ENERGY_SPEND = 1000;
	public int ENERGY_SPEND = 40;
	private boolean triggered;
	
	public Guard(double latitude,double longitude,double altitude, ParseUser creator, int level, String objectId) {
		super(latitude, longitude, altitude, "Guard", creator, level, objectId, triggerDistance,energyCost);
		// TODO Auto-generated constructor stub
		triggered = false;
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

	public void isTriggered(){
		if(triggered){
			
		}else{
			
		}
	}
	
	/**
	 * Converts userbearing to 360 degrees
	 * The checks to see if userbearing is within +/- 30 degrees of the guard's sight bearing
	 * 
	 * @param guardSightBearing2
	 * @param userbearing
	 * @return
	 */
	private Boolean checkIfSeenByGuard(Float guardSightBearing2,
			float userbearing) {
		// TODO Auto-generated method stub
		
		// convert userbearing to 360
		System.out.println("User bearing before conversion: "+userbearing);
		if (userbearing <0) {
			userbearing = 180 + (180 + userbearing);
		}
		System.out.println("User bearing after conversion: "+userbearing);
		
		// check if user is within sight of the guard
		if (userbearing >= (guardSightBearing2-30.0f) && userbearing <= (guardSightBearing2+30.0f)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Calulates a new bearing (based on 360 degrees) for where the guard is looking
	 * adds 60 degrees and then checks if the value is above 360 then adjusts
	 * 
	 * @author worthyp
	 * 
	 * @param guardSightBearing2
	 * @return
	 */
	public Float calcGuardSightbearing(Float guardSightBearing2) {
		guardSightBearing2 += 60;
		if (guardSightBearing2>360) {
			guardSightBearing2 -= 360;
		}
		return guardSightBearing2;
	}
	
}
