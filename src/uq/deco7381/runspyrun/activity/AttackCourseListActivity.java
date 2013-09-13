package uq.deco7381.runspyrun.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import uq.deco7381.runspyrun.R;
import uq.deco7381.runspyrun.R.layout;
import uq.deco7381.runspyrun.R.menu;
import uq.deco7381.runspyrun.model.ListAdapter_attackcourse;
import uq.deco7381.runspyrun.model.ListAdapter_newmission;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;

public class AttackCourseListActivity extends Activity implements OnMyLocationChangeListener {
	private GoogleMap map;
	private LocationManager status;
	private ListView attackCourseListView;
	private ListAdapter_attackcourse adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attack_course_list);
		
		Intent intent = getIntent();
		double latitude = intent.getDoubleExtra("latitude", 0.0);
		double longitude = intent.getDoubleExtra("longtitude", 0.0);
		ParseGeoPoint pCurrentLocation = new ParseGeoPoint(latitude,longitude);
		
		Location currentLocation = new Location(LocationManager.GPS_PROVIDER);
		currentLocation.setLatitude(latitude);
		currentLocation.setLongitude(longitude);
		
		Parse.initialize(this, "2XLuNz2w0M4iTL5VwXY2w6ICc7aYPZfnr7xyB4EF", "6ZHEiV500losBP4oHmX4f1qVuct1VyRgOlByTVQB");
		ParseAnalytics.trackAppOpened(getIntent());
		
		status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
		/*
		 *  Check is GPS available
		 */
		if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			/*
			 *  Check the map is exist or not
			 */
			if (map == null){
				map = ((MapFragment) getFragmentManager().findFragmentById(R.id.db_map)).getMap();
				if(map != null){
					setUpMap();
				}
			}
		}else{
			/*
			 *  If GPS is no available, direct user to Setting  
			 */
			Toast.makeText(this, "Please open the GPS", Toast.LENGTH_LONG).show();
			startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		}
		ArrayList<ParseObject> courseList = getCourseList(pCurrentLocation);
		System.out.println(courseList.size());
		attackCourseListView = (ListView)findViewById(R.id.courseList);
		adapter = new ListAdapter_attackcourse(this,currentLocation,courseList);
		attackCourseListView.setAdapter(adapter);
	}


	private void setUpMap(){
		map.setMyLocationEnabled(true);
		map.setOnMyLocationChangeListener(this);
		UiSettings uiSettings = map.getUiSettings();
		uiSettings.setAllGesturesEnabled(false);
		uiSettings.setMyLocationButtonEnabled(false);
		uiSettings.setZoomControlsEnabled(false);
	}

	private ArrayList<ParseObject> getCourseList(ParseGeoPoint loc){
		final ArrayList<ParseObject> courseList = new ArrayList<ParseObject>();
		ParseQuery<ParseObject> course = ParseQuery.getQuery("Course");
		course.whereWithinKilometers("location", loc, 0.5);
		course.whereNotEqualTo("organization", ParseUser.getCurrentUser().getString("organization"));
		try {
			List<ParseObject> objects =  course.find();
			for(ParseObject courseObject: objects){
				courseList.add(courseObject);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		course.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if(e == null){
					for(ParseObject courseObject: objects){
						courseList.add(courseObject);
					}
				}else{
					System.out.println(e.getMessage());
				}
				
			}
		});
		*/
		System.out.println(courseList.size());
		return courseList;
	}

	@Override
	public void onMyLocationChange(Location lastKnowLocation) {
		// TODO Auto-generated method stub
		/*
         *  Make camera on map keep tracking user.
         */
        LatLng latLng = new LatLng(lastKnowLocation.getLatitude(), lastKnowLocation.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		map.animateCamera(CameraUpdateFactory.zoomTo(15));
		map.setOnCameraChangeListener(null);
	}
}
