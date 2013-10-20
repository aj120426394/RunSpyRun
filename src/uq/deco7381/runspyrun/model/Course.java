package uq.deco7381.runspyrun.model;

import android.graphics.Color;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
/**
 * This class is the object to store information of course.
 * 
 * 1. Give the CircleOption (make the zone on the map)
 * 2. Get the location of course in Parse format
 * 3. Get the type of equipment it use.
 * 4. Get the owner(creator) of this course
 * 5. Get the level of this course
 * 6. Get the latitude of this course
 * 7. Get the longitude of this course
 * 8. Get the organization of this course
 * 
 * @author Jafo
 *
 */
public class Course {

	private double latitude;   	// Course's latitude
	private double longitude;	// Course's longitude
	private int radius;
	private int level;
	private ParseUser creator;
	private String organization;
	private String objectID;
	/**
	 * Constructor
	 * 
	 * @param location	// Course location in Parse format
	 * @param username	// Owner(creator) of this course
	 * @param org		// Organization of this couse's owner
	 */
	public Course(double latitude, double longitude, ParseUser creator, int level, String objectId, String org) {
		// TODO Auto-generated constructor stub
		this.latitude = latitude;
		this.longitude = longitude;
		this.radius = 400; //400 meter
		this.level = level;
		this.creator = creator;
		this.organization = org;
		this.objectID = objectId;
	}
	/**
	 * Get the CircleOption to display a zone of this course
	 * 
	 * @return CircleOptions: to display a circle zone of this course.
	 */
	public CircleOptions getCourseZone(){
		return new CircleOptions()
					.center(new LatLng(getLatitude(), getLongitude()))
					.radius(this.radius)
					.fillColor(0x40ff0000)
					.strokeColor(Color.TRANSPARENT)
					.strokeWidth(2);
	}

	/**
	 * Get the geolocation of this course
	 * @return	ParseGeoPoing: geolocation of this course in Parse format
	 */
	public ParseGeoPoint getParseGeoPoint(){
		ParseGeoPoint location = new ParseGeoPoint(this.latitude,this.longitude);
		return location;
	}
	/**
	 * Because course is set up with a equipment "Datasourse", 
	 * this object would be considered as an Equipment
	 * 
	 * @return String: datasourse
	 */
	public String getType(){
		return "Datasource";
	}
	/**
	 * Get the owner(creator) of this course
	 * 
	 * @return String: username of owner(creator) of this course
	 * 
	 */
	public ParseUser getOnwer(){
		return this.creator;
	}
	/**
	 * Get the level of this course
	 * 
	 * @return int: level of this course
	 */
	public int getLevel(){
		return this.level;
	}
	/**
	 * Get the latitude of this course
	 * 
	 * @return  double: latitude
	 */
	public double getLatitude(){
		return latitude;
	}
	/**
	 * Get the longitude of this course
	 * 
	 * @return double: longitude
	 */
	public double getLongitude(){
		return longitude;
	}
	/**
	 * Get the organization of this course belong to
	 * 
	 * @return String: organization name
	 */
	public String getOrg(){
		return this.organization;
	}
	/**
	 * 
	 * @param String: objectID
	 */
	public void setObjectID(String objectID){
		this.objectID = objectID;
	}
	public String getObjectID(){
		return this.objectID;
	}
}
