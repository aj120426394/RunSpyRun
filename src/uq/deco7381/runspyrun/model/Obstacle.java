package uq.deco7381.runspyrun.model;

import com.google.android.gms.maps.model.LatLng;

public class Obstacle {
	private LatLng location;
	private int energy;
	private String creator;
	private String organization;
	private String type;
	
	public Obstacle(LatLng latLng, String type, String org, String username) {
		// TODO Auto-generated constructor stub
		this.location = latLng;
		this.type = type;
		this.energy = 0;
		this.organization = org;
		this.creator = username;
	}
	
	public void setLatLng(LatLng latLng){
		this.location = latLng;
	}
	
	public LatLng getLatLng(){
		return this.location;
	}
	public double getLatitude(){
		return this.location.latitude;
	}
	public double getLongitude(){
		return this.location.longitude;
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
