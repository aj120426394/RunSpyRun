package uq.deco7381.runspyrun.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
/**
 * This class is use for Database management.
 * The database is Parse, so all the command of database are using 
 * Parse API
 * 
 * @author Jafo
 * @version 1.5
 * @since 19/10/2013
 */
public class ParseDAO {

	public ParseDAO() {
		// TODO Auto-generated constructor stub
		
	}
	/**
	 * Convert course object to ParseOjbect
	 * 
	 * @param Course: The Course is going to be converted
	 * @return ParseObject: The Course with ParseObject type
	 */
	public ParseObject createCourseParseObject(Course course){
		ParseObject parseCourse = new ParseObject("Course");
		parseCourse.put("owner", course.getOnwer());
		parseCourse.put("location", course.getParseGeoPoint());
		parseCourse.put("level", course.getLevel());
		parseCourse.put("organization", course.getOrg());
		
		return parseCourse;
	}
	/**
	 * Convert Obstacle object to ParseObejct
	 * 
	 * @param Obstacle: The Obstacle is going to be converted
	 * @param ParseObject: The Course which this obstacle in.
	 * @return ParseObject: The Obstacle with ParseObject type
	 */
	public ParseObject createObstacleParseObject(Obstacle obstacle, ParseObject course){
		ParseObject object = new ParseObject("Obstacle");
		object.put("creator", obstacle.getCreator());
		object.put("energy", 0);
		object.put("location", obstacle.getParseGeoPoint());
		object.put("altitude", obstacle.getAltitude());
		object.put("type", obstacle.getType());
		object.put("course", course);
		object.put("level", obstacle.getLevel());
		return object;
	}
	/**
	 * Create a Mission with ParseObject type.
	 * 
	 * @param ParseUser: Current user who create this mission
	 * @param ParseObject: The course which the mission is.
	 * @return ParseObject: The Mission with ParseObject type.
	 */
	public ParseObject createMissionParseObject(ParseUser currentUser, ParseObject course){
		ParseObject mission = new ParseObject("Mission");
		mission.put("course", course);
		mission.put("username", currentUser);
		return mission;
	}
	/**
	 * Save a list of Parse Object to Parse server.
	 * 
	 * @param List<ParseObject>: A list of ParseObject
	 * @param SaveCallback: the callback after save command execute.
	 */
	public void insertToServer(List<ParseObject> list,SaveCallback saveCallback){
		ParseObject.saveAllInBackground(list, saveCallback);
	}
	/**
	 * 
	 * @param user
	 */
	public void updateUserLevel(ParseUser user) {
		int currentLV = user.getInt("level");
		int courseDone = user.getInt("courseDone");
		courseDone += 1;
		if(courseDone == currentLV){
			currentLV += 1;
			courseDone = 0;
			user.put("level", currentLV);
			user.put("courseDone", courseDone);
		}else{
			user.put("courseDone", courseDone);
		}
		
		user.saveEventually();
	}
	/**
	 * Update the current user's equipment
	 * 
	 * SQL:
	 * UPDATE "equipment"
	 * SET "number" = current equipment number
	 * WHERE "username" = current user AND "type" = equipment type
	 * 
	 * @param ParseUser: Current user.
	 * @param ArrayList<Obstacle>: the list of obstacle need to be updated.
	 */
	public List<ParseObject> updateEquipment(ArrayList<Equipment> equipments){
		List<ParseObject> list = new ArrayList<ParseObject>();
		
		for(Equipment e: equipments){
			ParseObject equipment = ParseObject.createWithoutData("equipment", e.getObjectId());
			equipment.put("number", e.getNumber());
			list.add(equipment);
		}
		return list;
		
		/*
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
		/*
					}
				}
			}
		});
		*/
	}
	public ParseObject updateDatasource(ParseUser user){
		ParseQuery<ParseObject> equipment = ParseQuery.getQuery("equipment");
		equipment.whereEqualTo("username", user);
		equipment.whereEqualTo("eq_name", "Datasource");
		try {
			ParseObject datasource = equipment.getFirst();
			int currentNum = datasource.getInt("number");
			datasource.put("number", currentNum-1);
			return datasource;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	/**
	 * Update user's energy by time
	 * 
	 * SQL:
	 * UPDATE "User"
	 * SET "energyLevel" = current energy
	 * WHERE "user" = current user
	 * 
	 * @param ParseUser: Current user
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
		user.saveEventually();
	}
	/**
	 * User can regenerate energy from people who trigger the obstacle he set.
	 * 
	 * SQL:
	 * UPDATE "User"
	 * SET "energyLevel" = current energy
	 * WHERE "user" = current user
	 * 
	 * @param ParseUser: Current user
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
		user.saveEventually();
	}
	/**
	 * Update the current user's energy.
	 * 
	 * SQL:
	 * UPDATE "User"
	 * SET "energyLevel" = current energy
	 * WHERE "user" = current user
	 * 
	 * @param ParseUser: Current user.
	 * @param int: Current energy of current user.
	 */
	public void updateEnergyByEnergy(ParseUser user, int energy){
		user.put("energyLevel", energy);
		user.saveEventually();
	}
	/**
	 * Update the stolen energy to obstacle.
	 * 
	 * @param Obstacle
	 * @param int
	 */
	public void updateObstacleEnergy(final Obstacle obstacle,final int stolenEnergy){
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Obstacle");
		query.getInBackground(obstacle.getObjectId(), new GetCallback<ParseObject>() {

			@Override
			public void done(ParseObject object, ParseException e) {
				// TODO Auto-generated method stub
				if(e == null){
					object.increment("energy", stolenEnergy);
					object.saveEventually();
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
	 * Get user's equipment.
	 * SQL:
	 * SELECT *
	 * FROM equipment
	 * WHERE username = current user
	 * 
	 * @param ParseUser
	 * @return  ArrayList<Equipment>
	 */
	public ArrayList<Equipment> getEquipments(ParseUser user){
		ArrayList<Equipment> equipments = new ArrayList<Equipment>();
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("equipment");
		query.whereEqualTo("username", user);
		try {
			List<ParseObject> pList = query.find();
			for(ParseObject object: pList){
				String type = object.getString("eq_name");
				int number = object.getInt("number");
				String objectId = object.getObjectId();
				if(!type.equals("Datasource")){
					Equipment equipment = null;
					if(type.equals("MotionDetector")){
						equipment = new Equipment(type, number, objectId,3);
					}else if(type.equals("Dog")){
						equipment = new Equipment(type, number, objectId,2);
					}else{
						equipment = new Equipment(type, number, objectId,1);
					}
					equipments.add(equipment);
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return equipments;
	}
	/**
	 * Delete Course
	 * 
	 * SQL: 
	 * DELETE 
	 * FROM "Course" 
	 * WHERE ""
	 * @param course - Course
	 */
	
	public void deleteCourse(Course course){
		ParseObject courseObject = ParseObject.createWithoutData("Course", course.getObjectID());
		courseObject.deleteEventually();
		
		ParseQuery<ParseObject> missionQuery = ParseQuery.getQuery("Mission");
		missionQuery.whereEqualTo("course", ParseObject.createWithoutData("Course", course.getObjectID()));
		
		ParseQuery<ParseObject> obstaclesQuery = ParseQuery.getQuery("Obstacle");
		obstaclesQuery.whereEqualTo("course", courseObject);
		try {
			List<ParseObject> missionList = missionQuery.find();
			List<ParseObject> obstaclesList = obstaclesQuery.find();
			
			missionList.addAll(obstaclesList);
			ParseObject.deleteAll(missionList);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Delete the Mission 
	 * 
	 * SQL:
	 * DELETE
	 * FROM "Mission"
	 * WHERE "course" = Course AND "username" = Current user
	 * 
	 * @param course
	 * @param user
	 */
	public void deleteMission(Course course, ParseUser user){
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Mission");
		query.whereEqualTo("course", ParseObject.createWithoutData("Course", course.getObjectID()));
		query.whereEqualTo("username", user);
		query.getFirstInBackground(new GetCallback<ParseObject>() {

			@Override
			public void done(ParseObject object, ParseException e) {
				// TODO Auto-generated method stub
				if(e == null){
					object.deleteEventually();
				}else{
					System.out.println(e.getMessage());
				}
			}
		});
	}
	/**
	 * Delete all obstacle which created by user in a course.
	 * 
	 * SQL:
	 * DELETE
	 * FROM "Obstacle"
	 * WHERE "course" = Course AND "username" = Current user
	 * 
	 * @param course
	 * @param user
	 */
	public void deleteObstacleByUser(Course course, ParseUser user){
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Obstacle");
		query.whereEqualTo("course", ParseObject.createWithoutData("Course", course.getObjectID()));
		query.whereEqualTo("username", user);
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if(e == null){
					ParseObject.deleteAllInBackground(objects, new DeleteCallback() {
						
						@Override
						public void done(ParseException e) {
							// TODO Auto-generated method stub
							if(e != null){
								System.out.println(e.getMessage());
							}
						}
					});
				}
				
			}
		});
	}
	/**
	 * Increase equipment when user hack the course.
	 * 
	 * @param ParseUser: Current user
	 * @param ArrayList<Obstacle>: List of obstacle in th course.
	 */
	public void updateGetEquipment(ParseUser user, ArrayList<Obstacle> obstacles){
		ParseQuery<ParseObject> query = ParseQuery.getQuery("equipment");
		query.whereEqualTo("username", user);
		try {
			List<ParseObject> equipments = query.find();
			for(ParseObject e: equipments){
				for(Obstacle o: obstacles){
					if(o.getType().equals(e.getString("eq_name"))){
						e.increment("number");
					}
				}
				if(e.getString("eq_name").equals("Datasource")){
					e.increment("number");
				}
			}
			ParseObject.saveAll(equipments);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void pushNotification(ParseUser user) {
		ParsePush push = new ParsePush();
		if(user.getString("organization").equals("iCorp")){
			push.setChannel("mCorp");
		}else{
			push.setChannel("iCorp");
		}
		push.setMessage("Your course has been hacked");
		push.sendInBackground();
	}
}
