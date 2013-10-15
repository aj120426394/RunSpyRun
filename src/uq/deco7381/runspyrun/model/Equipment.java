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
	
	public Equipment(String type, int number) {
		// TODO Auto-generated constructor stub
		this.type = type;
		this.number = number;
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

	public int getBaseCost(){
		int basecost = 0;
		
		if(this.type.equals("Guard")){
			basecost = 40;
		}else if(this.type.equals("Dog")){
			basecost = 30;
		}
		
		return basecost;
	}
	
	public int getMaxCost(){
		int maxCost = 0;
		
		if(this.type.equals("Guard")){
			maxCost = 1000;
		}else if(this.type.equals("Dog")){
			maxCost = 900;
		}
		
		return maxCost;
	}
}
