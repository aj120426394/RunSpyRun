package uq.deco7381.runspyrun.model;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ParseDAO {

	public ParseDAO() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * Get Course by location from Parse
	 * 
	 * 1.SQL: SELECT * FROM Course WHERE location = (latitude, longitude)
	 * 2.New a Course with the gotten data above.
	 * 
	 * @param latitude - double
	 * @param longitude - double
	 * @return course - Course
	 */
	public Course getCourseByLoc(double latitude, double longitude){
		Course course = null;
		ParseObject courseParseObject = null;
		ParseQuery<ParseObject> courseQuery = ParseQuery.getQuery("Course");
		courseQuery.whereNear("location", new ParseGeoPoint(latitude, longitude));
		courseQuery.include("owner");
		try {
			courseParseObject = courseQuery.getFirst();
			course = new Course(courseParseObject.getParseGeoPoint("location"), courseParseObject.getParseUser("owner").getUsername(),courseParseObject.getString("organization"));
			course.setObjectID(courseParseObject.getObjectId());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return course;
	}
	/**
	 * Delete Course 
	 * 
	 * 1. SQL: DELETE FROW Course WHERE
	 * @param course - Course
	 */
	public void deleteCourse(Course course){
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Course");
		query.whereNear("location", course.getParseGeoPoint());
		query.getFirstInBackground(new GetCallback<ParseObject>() {
			@Override
			public void done(ParseObject object, ParseException e) {
				object.deleteInBackground();
			}
		});
	}

	
	public ArrayList<Obstacle> getObstaclesByCourse(Course course){
		ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
		ParseQuery<ParseObject> courseQuery = ParseQuery.getQuery("Course");
		courseQuery.whereEqualTo("objectId", course.getObjectID());
		
		ParseQuery<ParseObject> obstaclesQuery = ParseQuery.getQuery("Obstacle");
		obstaclesQuery.whereMatchesQuery("course", courseQuery);
		obstaclesQuery.include("creator");
		try {
			List<ParseObject> pList =  obstaclesQuery.find();
			for(ParseObject object: pList){
				Obstacle obstacle = null;
				double latitude = object.getParseGeoPoint("location").getLatitude();
				double longitude = object.getParseGeoPoint("location").getLongitude();
				double altitude = object.getDouble("altitude");
				String username = object.getParseUser("creator").getUsername();
				int level = object.getInt("level");
				String objectId = object.getObjectId();
				
				if(object.getString("type").equals("Guard")){
					obstacle = new Guard(latitude, longitude, altitude, username, level, objectId);
				}
				
				obstacles.add(obstacle);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return obstacles;
		
	}
}
