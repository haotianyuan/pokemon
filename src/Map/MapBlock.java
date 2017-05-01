/*
 * Author: Mengtao Tang
 * Date: 4/20/2017
 * Course: CSC_335
 * Purpose: This is an class define the information of each
 * 			map block in the map
 * 
 */

package Map;

import java.io.Serializable;

public class MapBlock implements Serializable{
	

	private static final long serialVersionUID = -4085092152859549598L;
	
	private GroundType groundType;
	private ObstacleType obstacleType;
	private InteractType interactType;
	//private final boolean passable;
	
	// constructor
	public MapBlock(GroundType groundType){
		this.groundType = groundType;
		this.obstacleType = ObstacleType.NONE;
		this.interactType = InteractType.NONE;
	}
	
	// getter
	public GroundType getGround(){
		return this.groundType;
	}
	
	public ObstacleType getObstacle(){
		return this.obstacleType;
	}
	
	public InteractType getInteractType(){
		return this.interactType;
	}
	
	protected void setBlockFromIndex(int i){
		switch (i){
			case 0:
				setObstacle(ObstacleType.DECORATION);
				break;
			
			case 1:
				setInteract(InteractType.SHORTGRASS);
				break;
				
			case 2:
				setObstacle(ObstacleType.WATER);
				break;
				
			case 3:
				setObstacle(ObstacleType.ROCK);
				break;
				
			case 4:
				setInteract(InteractType.SWAMP);
				break;
				
			case 5:
				setGround(GroundType.SOIL);
				setInteract(InteractType.SAND);
				break;
				
			case 6:
				setInteract(InteractType.PORTAL);
				break;
				
			case 7:
				setObstacle(ObstacleType.TREE);
				break;
			
			case 8:
				setGround(GroundType.ROAD);
				break;
				
			default:
				setGround(GroundType.GRASSLAND);
				break;				
		}
		
	}
	
	public void setGround(GroundType gnd){
		this.groundType = gnd;
	}
	
	public void setObstacle(ObstacleType ob){
		this.obstacleType = ob;
		this.interactType = InteractType.NONE;
	}
	
	public void setInteract(InteractType it){
		this.interactType = it;
		this.obstacleType = ObstacleType.NONE;
	}
	
	/*	
	public boolean canMeetPokemon(){
		if (this.passType != PassableType.AIR && this.obstacle == ObstacleType.NONE){
			return true;
		}
		else{
			return false;
		}
	}
	*/
	
	// get passable status
	public boolean isPassable(){
		if (this.obstacleType == ObstacleType.NONE){
			return true;
		}
		else{
			return false;
		}
	}
}
