package uq.deco7381.runspyrun.model;

import android.R.integer;

/**
 * This class would  be used to save the equipment
 * information from database.
 * 
 * @author Jafo
 * @version 1.0
 * @since 16/10/2013
 */
public class Equipment {

	private String type;
	private int number;
	private String objectId;
	private int levelLimit;
	
	public Equipment(String type, int number, String objectId, int levelLimit) {
		// TODO Auto-generated constructor stub
		this.type = type;
		this.number = number;
		this.objectId = objectId;
		this.levelLimit = levelLimit;
	}
	/**
	 * Get the number of this type of equipment.
	 * @return int
	 */
	public int getNumber(){
		return this.number;
	}
	/**
	 * Set the number of this type  of equipment.
	 * @param int
	 */
	public void setNumber(int number){
		this.number = number;
	}
	/**
	 * Get the type of this obstacle.
	 * @return String
	 */
	public String getType(){
		return this.type;
	}
	/**
	 * Get the base energy cost of equipment when user set down a obstacle in lv1
	 * 
	 * @return int: basic energy cost
	 */
	public int getBaseCost(){
		int basecost = 0;
		
		if(this.type.equals("Guard")){
			basecost = 40;
		}else if(this.type.equals("Dog")){
			basecost = 30;
		}else if(this.type.equals("MotionDetector")){
			basecost = 50;
		}else if(this.type.equals("DetectionPlate")){
			basecost = 30;
		}
		
		return basecost;
	}
	/**
	 * Get the maximum energy cost of equipment when user's level exceed a certain level
	 * 
	 * @return int: maximum energy cost
	 */
	public int getMaxCost(){
		int maxCost = 0;
		
		if(this.type.equals("Guard")){
			maxCost = 1000;
		}else if(this.type.equals("Dog")){
			maxCost = 900;
		}else if(this.type.equals("MotionDetector")){
			maxCost = 1500;
		}else if(this.type.equals("DetectionPlate")){
			maxCost = 800;
		}
		
		return maxCost;
	}
	/**
	 * Get the object id of this equipment from Parse
	 * 
	 * @return String: object id
	 */
	public String getObjectId(){
		return this.objectId;
	}
	
	/**
	 * Get the lowest level to use this equipment.
	 * User only allowed to use certain equipment when they attain certain level.
	 * 
	 * @return int: acceptable level to use this equipment.
	 */
	public int getLevelLimit() {
		return this.levelLimit;
	}
}
