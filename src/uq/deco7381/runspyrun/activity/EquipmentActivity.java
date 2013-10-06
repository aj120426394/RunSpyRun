package uq.deco7381.runspyrun.activity;

import uq.deco7381.runspyrun.R;
import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
/**
 * 
 * This activity is to show how many and what kind of equipment that player have.
 * Also when user select Datasource it would direct to Defence mode to create a new course.
 * (User would be set as "newCourse" state to DefenceActivity)
 * 
 * Using Location Client:
 * Using the Google play service: Location Client to catch the current location.
 * It would get the location faster and more precise.
 * But it would need to check if user's device have already install Google play service to use this functionality
 * 
 * Founded Issue of Android build-in method: Location Manager
 * need LocationChange method to help to get the latest location, and that's not proper for this class.
 * 
 * @author Jafo
 *
 */
public class EquipmentActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {
	private LocationClient lClient;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_equipment);
		/*
		 * Set up a Location client
		 */
		lClient = new LocationClient(this, this, this);

		setNumOfData();
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		/*
		 * make the location detection start
		 */
		lClient.connect();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		lClient.disconnect();
		super.onStop();
	}

	/**
	 * onClick method triggered by click on "Datasource"
	 * 1. Get the user's current location
	 * 2. Store it into intent to pass it to next Activity
	 * 3. Store the user's state to pass it to next Activity
	 * 4. Direct to DefenceActivity
	 * 
	 * @param v
	 */
	public void setCourse(View v){
		Intent intent = new Intent(this, DefenceActivity.class);
		Location location = lClient.getLastLocation();
		intent.putExtra("latitude", location.getLatitude());
		intent.putExtra("longtitude", location.getLongitude());
		intent.putExtra("isFrom", "newCourse");
		startActivity(intent);
	}

	/**
	 * (unfinished)
	 * Display the number of equipment
	 * 1. Fetch the equipment list base on username
	 * 2. Get the number of each equipment
	 * 3. Set the number of each equipment on the TextView
	 */
	private void setNumOfData(){
		ParseQuery<ParseObject> query = ParseQuery.getQuery("equipment");
		query.whereEqualTo("username", ParseUser.getCurrentUser());
		query.whereEqualTo("eq_name", "Datasource");
		query.getFirstInBackground(new GetCallback<ParseObject>() {
			@Override
			public void done(ParseObject object, ParseException e) {
				// TODO Auto-generated method stub
				if(e == null){
					int numOfdata = object.getInt("number");
					TextView numOfdataTextView = (TextView)findViewById(R.id.equipment_numOfData);
					numOfdataTextView.setText(String.valueOf(numOfdata));
				}
			}
		});
	}
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
		
	}


}
