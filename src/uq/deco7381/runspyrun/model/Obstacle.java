package uq.deco7381.runspyrun.model;

import com.parse.ParseGeoPoint;
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
	private String creator;		// Obstacle's creator
	private int energy;			// The energy get from player who trigger the obstacle
	private String objectID;

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
	public Obstacle(double latitude,double longitude,double altitude, String type, String username, int level,String objectId) {
		// TODO Auto-generated constructor stub
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.level = level;
		this.type = type;
		this.energy = 0;
		this.creator = username;
		this.objectID = objectId;
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
	 * @return String: creator of obstacle
	 */
	public String getCreator(){
		return this.creator;
	}
	/**
	 * Get the type of this obstacle
	 * @return	String: type of this obstacle
	 */
	public String getType(){
		return this.type;
	}
	/**
	 * Set the energy get from the player who trigger this obstacle
	 * @param energy
	 */
	public void setEnergy(int energy){
		this.energy = energy;
	}
	/**
	 * Get the energy get from the player who trigger this obstacle
	 * @return
	 */
	public int getEnergy(){
		return this.energy;
	}
	
	public void setObjectId(String objectId){
		this.objectID = objectId;
	}
	public String getObjectId(){
		return this.objectID;
	}
}
