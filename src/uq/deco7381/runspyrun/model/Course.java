package uq.deco7381.runspyrun.model;

import android.graphics.Color;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

public class Course {

	private LatLng centerLatLng;
	private int radius;
	private int level;
	private String owner;
	private String organization;
	
	public Course(LatLng latLng, String username, String org) {
		// TODO Auto-generated constructor stub
		this.centerLatLng = latLng;
		this.radius = 1000; //1000 meter
		this.level = 1;
		this.owner = username;
		this.organization = org;
	}
	
	public CircleOptions getCourseZone(){
		return new CircleOptions()
					.center(this.centerLatLng)
					.radius(this.radius)
					.fillColor(0x40ff0000)
					.strokeColor(Color.TRANSPARENT)
					.strokeWidth(2);
	}

	public void setLatLng(LatLng latLng){
		this.centerLatLng = latLng;
	}
	public LatLng getLatLng(){
		return this.centerLatLng;
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
		return this.centerLatLng.latitude;
	}
	public double getLongitude(){
		return this.centerLatLng.longitude;
	}
	public void setOrg(String org){
		this.organization = org;
	}
	public String getOrg(){
		return this.organization;
	}
}
