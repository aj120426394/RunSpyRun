package uq.deco7381.runspyrun.model;

import com.google.android.gms.maps.model.LatLng;

public class Obstacle {
	private LatLng location;
	
	public Obstacle(LatLng latLng) {
		// TODO Auto-generated constructor stub
		this.location = latLng;
	}
	
	public void setLatLng(LatLng latLng){
		this.location = latLng;
	}
	
	public LatLng getLatLng(){
		return this.location;
	}
	

}
