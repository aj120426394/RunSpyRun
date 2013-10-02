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
import com.parse.ParseUser;

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
			String username = courseParseObject.getParseUser("creator").getUsername();
			String org = courseParseObject.getParseUser("creator").getString("organization");
			int level = courseParseObject.getInt("level");
			String objectId = courseParseObject.getObjectId();
			course = new Course(latitude, longitude, username, level, objectId, org);
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

	/**
	 * Get obstacles by course from Parse
	 * SQL: SELECT obstacle FROM Course WHERE course = "course"
	 * 
	 * 
	 * @param course
	 * @return ArrayList of obstacles in different type
	 */
	
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
	/**
	 * Get course by mission from cache
	 * SQL: SELECT course FROM Mission WHERE course = mission.couse
	 * 
	 * 
	 * 
	 * @param user
	 * @return ArrayList of Course
	 */
	public ArrayList<Course> getCourseByMissionFromCache(ParseUser user){
		ArrayList<Course> courses = new ArrayList<Course>();
		ParseQuery<ParseObject> missionListQuery = ParseQuery.getQuery("Mission"); 
		missionListQuery.whereEqualTo("username", user);
		missionListQuery.include("course");
		missionListQuery.include("course.owner");
		if(missionListQuery.hasCachedResult()){
			missionListQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ONLY);
			try {
				List<ParseObject> pList = missionListQuery.find();
				for(ParseObject object: pList){
					double latitude = object.getParseObject("course").getParseGeoPoint("location").getLatitude();
					double longitude = object.getParseObject("course").getParseGeoPoint("location").getLongitude();
					String username = object.getParseObject("course").getParseUser("owner").getUsername();
					String org = object.getParseObject("course").getParseUser("owner").getString("organization");
					int level = object.getParseObject("course").getInt("level");
					String objectId = object.getParseObject("course").getObjectId();
					Course course = new Course(latitude, longitude, username, level, objectId, org);
					courses.add(course);
				}
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return courses;
	}
	/**
	 * Get course by mission from Parse
	 * SQL: SELECT course FROM Mission WHERE course = mission.couse
	 * 
	 * @param user
	 * @return ArrayList of Course
	 */
	public ArrayList<Course> getCourseByMissionFromNetwork(ParseUser user){
		ArrayList<Course> courses = new ArrayList<Course>();
		ParseQuery<ParseObject> missionListQuery = ParseQuery.getQuery("Mission"); 
		missionListQuery.whereEqualTo("username", user);
		missionListQuery.include("course");
		missionListQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
		try {
			List<ParseObject> pList = missionListQuery.find();
			for(ParseObject object: pList){
				double latitude = object.getParseObject("course").getParseGeoPoint("location").getLatitude();
				double longitude = object.getParseObject("course").getParseGeoPoint("location").getLongitude();
				String username = object.getParseObject("course").getParseUser("creator").getUsername();
				String org = object.getParseObject("course").getParseUser("creator").getString("organization");
				int level = object.getParseObject("course").getInt("level");
				String objectId = object.getParseObject("course").getObjectId();
				Course course = new Course(latitude, longitude, username, level, objectId, org);
				courses.add(course);
			}
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return courses;
	}
	/**
	 * Get Course By different organization in 500 meter
	 * 
	 * @param latitude
	 * @param longitude
	 * @param org
	 * @return ArrayList of Course
	 */
	public ArrayList<Course> getCourseByDiffOrgIn500M(double latitude, double longitude, String org){
		ArrayList<Course> courses = new ArrayList<Course>();
		
		ParseGeoPoint loc = new ParseGeoPoint(latitude,longitude);
		
		ParseQuery<ParseObject> courseList = ParseQuery.getQuery("Course");
		courseList.whereWithinKilometers("location", loc, 0.5);
		courseList.whereNotEqualTo("organization", ParseUser.getCurrentUser().getString("organization"));
		try {
			List<ParseObject> objects =  courseList.find();
			for(ParseObject object: objects){
				double cLatitude = object.getParseGeoPoint("location").getLatitude();
				double cLongitude = object.getParseGeoPoint("location").getLongitude();
				String username = object.getParseUser("creator").getUsername();
				String cOrg = object.getParseUser("creator").getString("organization");
				int level = object.getInt("level");
				String objectId = object.getObjectId();
				Course course = new Course(cLatitude, cLongitude, username, level, objectId, cOrg);
				courses.add(course);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return courses;
	}

}
