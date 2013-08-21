package uq.deco7381.runspyrun.model;

import com.google.android.gms.maps.model.LatLng;

public class Obstacle {
	private LatLng latLng;
	
	public Obstacle(LatLng latLng) {
		// TODO Auto-generated constructor stub
		this.latLng = latLng;
	}
	
	public void setLatLng(LatLng latLng){
		this.latLng = latLng;
	}
	
	public LatLng getLatLng(){
		return this.latLng;
	}

}
