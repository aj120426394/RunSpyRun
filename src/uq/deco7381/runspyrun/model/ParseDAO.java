package uq.deco7381.runspyrun.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.R.integer;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ParseDAO {

	public ParseDAO() {
		// TODO Auto-generated constructor stub
		
	}
	public ParseObject createCourseParseObject(Course course){
		ParseObject parseCourse = new ParseObject("Course");
		parseCourse.put("owner", course.getOnwer());
		parseCourse.put("location", course.getParseGeoPoint());
		parseCourse.put("level", course.getLevel());
		parseCourse.put("organization", course.getOrg());
		
		return parseCourse;
	}
	public ParseObject createObstacleParseObject(Obstacle obstacle, ParseObject course){
		ParseObject object = new ParseObject("Obstacle");
		object.put("creator", obstacle.getCreator());
		object.put("energy", 0);
		object.put("location", obstacle.getParseGeoPoint());
		object.put("altitude", obstacle.getAltitude());
		object.put("type", obstacle.getType());
		object.put("course", course);
		return object;
	}
	public ParseObject createMissionParseObject(ParseUser currentUser, ParseObject course){
		ParseObject mission = new ParseObject("Mission");
		mission.put("course", course);
		mission.put("username", currentUser);
		return mission;
	}
	public void insertToServer(List<ParseObject> list,SaveCallback saveCallback){
		ParseObject.saveAllInBackground(list, saveCallback);
	}
	public void updateEquipment(ParseUser user, ArrayList<Obstacle> obstacles){
		ParseQuery<ParseObject> equipment = ParseQuery.getQuery("equipment");
		equipment.whereEqualTo("username", user);
		equipment.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				for(ParseObject equipmentObject: objects){
					String eType = equipmentObject.getString("eq_name");
					if(eType.equals("Datasource")){
						equipmentObject.put("number", equipmentObject.getInt("number")-1);
						equipmentObject.saveInBackground();
					}else{
						/*
						for(Obstacle obstacle: obstacles){
							if(obstacle.getType().equals(eType)){
								equipmentObject.put("number", equipmentObject.getInt("number")-1);
							}
						}*/
					}
				}
			}
		});
	}
	/**
	 * Update user's energy by time
	 * @param user
	 */
	public void updateEnergyByTime(ParseUser user){
		Date lastLogin = user.getUpdatedAt();
		Date currentDate = new Date();
		long difference = currentDate.getTime() - lastLogin.getTime();
		long differenceBack = difference;
		differenceBack = difference / 1000;
		int mins = (int)differenceBack / 60;
		int extraEnergy = mins/10 * 500;
		int energy = user.getInt("energyLevel")+extraEnergy;
		
		if(energy >= user.getInt("level")*100){
			energy = user.getInt("level")*100;
		}
		user.put("energyLevel", energy);
		user.saveInBackground();
	}
	/**
	 * User can regenerate energy from people who trigger the obstacle he set.
	 * @param user
	 */
	public void updateEnergyByObstacle(ParseUser user){
		int energy = user.getInt("energyLevel");
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Obstacle");
		query.whereEqualTo("creator", user);
		try {
			List<ParseObject> obstaclesList = query.find();
			for(ParseObject obstacle: obstaclesList){
				int extraEnergy = obstacle.getInt("energy");
				energy += extraEnergy;
				obstacle.put("energy", 0);
			}
			ParseObject.saveAllInBackground(obstaclesList);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(energy >= user.getInt("level")*100){
			energy = user.getInt("level")*100;
		}
		user.put("energyLevel", energy);
		user.saveInBackground();
	}
	
	public void updateEnergyByEnergy(ParseUser user, int energy){
		user.put("energyLevel", energy);
		user.saveInBackground();
	}
	
	public void updateObstacleEnergy(final Obstacle obstacle,final int stolenEnergy){
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Obstacle");
		query.getInBackground(obstacle.getObjectId(), new GetCallback<ParseObject>() {

			@Override
			public void done(ParseObject object, ParseException e) {
				// TODO Auto-generated method stub
				if(e == null){
					object.increment("energy", stolenEnergy);
					object.saveInBackground();
				}else{
					System.out.println();
				}
			}
		});
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
			ParseUser username = courseParseObject.getParseUser("owner");
			String org = courseParseObject.getString("organization");
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
		if(missionListQuery.hasCachedResult()){
			missionListQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ONLY);
			try {
				List<ParseObject> pList = missionListQuery.find();
				for(ParseObject object: pList){
					double latitude = object.getParseObject("course").getParseGeoPoint("location").getLatitude();
					double longitude = object.getParseObject("course").getParseGeoPoint("location").getLongitude();
					ParseUser username = object.getParseObject("course").getParseUser("owner");
					String org = object.getParseObject("course").getString("organization");
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
		missionListQuery.include("course.owner");
		missionListQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
		try {
			List<ParseObject> pList = missionListQuery.find();
			for(ParseObject object: pList){
				double latitude = object.getParseObject("course").getParseGeoPoint("location").getLatitude();
				double longitude = object.getParseObject("course").getParseGeoPoint("location").getLongitude();
				ParseUser username = object.getParseObject("course").getParseUser("owner");
				String org = object.getParseObject("course").getString("organization");
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
	public ArrayList<Course> getCourseByDiffOrgInDistance(double latitude, double longitude, String org, double distance){
		ArrayList<Course> courses = new ArrayList<Course>();
		
		ParseGeoPoint loc = new ParseGeoPoint(latitude,longitude);
		
		ParseQuery<ParseObject> courseList = ParseQuery.getQuery("Course");
		courseList.whereWithinKilometers("location", loc, distance);
		courseList.whereNotEqualTo("organization", org);
		try {
			List<ParseObject> objects =  courseList.find();
			for(ParseObject object: objects){
				double cLatitude = object.getParseGeoPoint("location").getLatitude();
				double cLongitude = object.getParseGeoPoint("location").getLongitude();
				ParseUser username = object.getParseUser("owner");
				String cOrg = object.getString("organization");
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
	public ArrayList<Course> getCourseByOrgInDistance(double latitude, double longitude, String org, double distance){
		ArrayList<Course> courses = new ArrayList<Course>();
		
		ParseGeoPoint loc = new ParseGeoPoint(latitude,longitude);
		
		ParseQuery<ParseObject> courseList = ParseQuery.getQuery("Course");
		courseList.whereWithinKilometers("location", loc, distance);
		courseList.whereEqualTo("organization", org);
		try {
			List<ParseObject> objects =  courseList.find();
			for(ParseObject object: objects){
				double cLatitude = object.getParseGeoPoint("location").getLatitude();
				double cLongitude = object.getParseGeoPoint("location").getLongitude();
				ParseUser username = object.getParseUser("owner");
				String cOrg = object.getString("organization");
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
				ParseUser username = object.getParseUser("creator");
				int level = object.getInt("level");
				String objectId = object.getObjectId();
				
				if(object.getString("type").equals("Guard")){
					obstacle = new Guard(latitude, longitude, altitude, username, level, objectId);
				} else if(object.getString("type").equals("Dog")){
					obstacle = new Dog(latitude, longitude, altitude, username, level, objectId);
				} else if(object.getString("type").equals("MotionDetector")){
					obstacle = new MotionDetector(latitude, longitude, altitude, username, level, objectId);
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
	 * Delete Course
	 * 
	 * 1. SQL: DELETE FROM Course WHERE
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
	
}
