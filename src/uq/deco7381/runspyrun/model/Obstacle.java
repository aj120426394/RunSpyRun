package uq.deco7381.runspyrun.model;

import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
/**
 * This class is built for parent of any obstacle object.
 * Every obstacle should include
 * 1. Latitude
 * 2. Longitude
 * 3. Altitude
 * 4. Type
 * 5. Level
 * 6. Creator
 * 7. Energy
 * 
 * @author Jafo
 *
 */
public class Obstacle {
	private double latitude;   	// Obstacle's latitude
	private double longitude;	// Obstacle's longitude
	private double altitude;	// Obstacle's altitude
	private String type;		// Obstacle's type
	private int level;			// Obstacle's level: base on creator's current level when user set it down
	private String objectID;	// Object id from parse.
	private ParseUser parseUser;// Creator who create this obstacle.
	private int triggerDistance;// The distance to trigger this obstacle.
	private int energyCost;		// The basic cost when people trigger this obstacle.
	public int MAX_ENERGY_SPEND;// Max cost to put down this obstacle
	public int ENERGY_SPEND;	// The basic cost to put down this obstacle.
	

	/**
	 * Constructor
	 * 
	 * @param latitude
	 * @param longitude
	 * @param altitude
	 * @param type
	 * @param username
	 * @param level
	 */
	public Obstacle(double latitude,double longitude,double altitude, String type, ParseUser creator, int level, String objectId,int triggerDistance, int energyCost) {
		// TODO Auto-generated constructor stub
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.level = level;
		this.type = type;
		this.objectID = objectId;
		this.parseUser = creator;
		this.triggerDistance = triggerDistance;
		this.energyCost = energyCost;
	}
	/**
	 * Get the latitude of this obstacle
	 * @return double: latitude of obstacle
	 */
	public double getLatitude(){
		return this.latitude;
	}
	/**
	 * Get the longitude of this obstacle
	 * @return double: longitude of obstacle
	 */
	public double getLongitude(){
		return this.longitude;
	}
	/**
	 * Get the altitude of this obstacle
	 * @return double: altitude of obstacle
	 */
	public double getAltitude(){
		return this.altitude;
	}
	/**
	 * Get the Geolocation of this obstacle in Parse format 
	 * @return ParseGeoPoint: location of obstacle
	 */
	public ParseGeoPoint getParseGeoPoint(){
		ParseGeoPoint pLoc = new ParseGeoPoint(this.latitude,this.longitude);
		return pLoc;
	}
	/**
	 * Get the Creator of this obstacle
	 * @return ParseUser: creator of obstacle
	 */
	public ParseUser getCreator(){
		return this.parseUser;
	}
	/**
	 * Get the type of this obstacle
	 * @return	String: type of this obstacle
	 */
	public String getType(){
		return this.type;
	}
	/**
	 * Set object id of this obstacle
	 * @param String: objectID 
	 */
	public void setObjectId(String objectId){
		this.objectID = objectId;
	}
	/**
	 * Get object id of this obstacle
	 * @return String: object id
	 */
	public String getObjectId(){
		return this.objectID;
	}
	/**
	 * Get the MarkerOption to place the icon on the Google map
	 * @return MarkerOption: the MarkerOption can set on the google map
	 */
	public MarkerOptions getMarkerOptions(){
		return null;								
	}
	/**
	 * Get level of this obstacle
	 * @return int: level of this obstacle
	 */
	public int getLevel(){
		return this.level;
	}
	/**
	 * Set level of this obstacle
	 * @param int: level of this obstacle
	 */
	public void setLevel(int level){
		this.level = level;
	}
	/**
	 * Get the distance to trigger this obstacle
	 * @return int: distance
	 */
	public int getTriggerDistance() {
		return this.triggerDistance;
	}
	/**
	 * Get the energy cost from people who trigger this obstacle
	 * @return int: energy cost
	 */
	public int getEnergyCost(){
		return this.energyCost * this.level;
	}
}
