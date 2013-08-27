package uq.deco7381.runspyrun.model;

import android.graphics.Color;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;

public class Course {

	private int radius;
	private int level;
	private String owner;
	private String organization;
	private ParseGeoPoint location;
	
	public Course(ParseGeoPoint location, String username, String org) {
		// TODO Auto-generated constructor stub
		this.location = location;
		this.radius = 400; //400 meter
		this.level = 1;
		this.owner = username;
		this.organization = org;
	}
	
	public CircleOptions getCourseZone(){
		return new CircleOptions()
					.center(new LatLng(getLatitude(), getLongitude()))
					.radius(this.radius)
					.fillColor(0x40ff0000)
					.strokeColor(Color.TRANSPARENT)
					.strokeWidth(2);
	}

	public void setLocation(ParseGeoPoint location){
		this.location = location;
	}
	public ParseGeoPoint getLocation(){
		return this.location;
	}
	public String getType(){
		return "Datasource";
	}
	public String getOnwer(){
		return this.owner;
	}
	public int getLevel(){
		return this.level;
	}
	public double getLatitude(){
		return this.location.getLatitude();
	}
	public double getLongitude(){
		return this.location.getLongitude();
	}
	public void setOrg(String org){
		this.organization = org;
	}
	public String getOrg(){
		return this.organization;
	}
}
