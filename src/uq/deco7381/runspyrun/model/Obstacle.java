package uq.deco7381.runspyrun.model;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;

public class Obstacle {
	//private LatLng location;
	private int energy;
	private String creator;
	private String organization;
	private String type;
	private ParseGeoPoint location;
	
	public Obstacle(ParseGeoPoint location, String type, String org, String username) {
		// TODO Auto-generated constructor stub
		this.location = location;
		this.type = type;
		this.energy = 0;
		this.organization = org;
		this.creator = username;
	}
	
	public void setLocation(ParseGeoPoint location){
		this.location = location;
	}
	
	public ParseGeoPoint getLocation(){
		return this.location;
	}
	public double getLatitude(){
		return this.location.getLatitude();
	}
	public double getLongitude(){
		return this.location.getLongitude();
	}
	public void setCreator(String creator){
		this.creator = creator;
	}
	public String getCreator(){
		return this.creator;
	}
	public void setOrg(String org){
		this.organization = org;
	}
	public String getOrg(){
		return this.organization;
	}
	public String getType(){
		return this.type;
	}
	public void setEnergy(int energy){
		this.energy = energy;
	}
	public int getEnergy(){
		return this.energy;
	}
}
