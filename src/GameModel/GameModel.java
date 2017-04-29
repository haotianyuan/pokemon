package GameModel;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;

import Inventory.Item;
import Inventory.ItemType;
import Map.*;
import Mission.*;
import Pokemon.*;
import Trainer.*;

public class GameModel extends Observable implements Serializable{

	private static final long serialVersionUID = -7756945750821100840L;
	
	// trainer information
	private Trainer curTrainer;
	private int xCoords;
	private int yCoords;
	private int xPrevCoords;
	private int yPrevCoords;
		
	// map information
	private Map_BottomLeft map_BL;
	private Map_BottomRight map_BR;
	private Map_TopLeft map_TL;
	private Map_TopRight map_TR;
	private Map curMap;
	
	// battle information
	private WildPokemonGenerator wildPokemonGenerator;
	private boolean encounteredThisBlock;	// flag to check if just encounter a pokemon before move
	
	// game information
	private Mission mission;
	private boolean isOver = false;
	private boolean isWin = false;
	private boolean isLost = false;
	
	
	public GameModel(){
		initiateMap();
		wildPokemonGenerator = WildPokemonGenerator.getInstance();
		encounteredThisBlock = false;
		curTrainer = new Trainer("T.M.T.");
		setLocation(65, 65);
		xPrevCoords = 65;
		yPrevCoords = 65;
		setCurMap(map_BL);
		setMission(new Mission(MissionType.TEST));
		curTrainer.catchPokemon(new Abra("A_1"));
		curTrainer.catchPokemon(new Abra("A_2"));
		curTrainer.catchPokemon(new Abra("A_3"));
		curTrainer.catchPokemon(new Mew("M"));
		curTrainer.addItem(ItemType.CAPTURE_POTION_MEDIUM);
		curTrainer.addItem(ItemType.STEP_POTION_LARGE);
		curTrainer.addItem(ItemType.BAIT);
		curTrainer.addItem(ItemType.BAIT);
		curTrainer.addItem(ItemType.BAIT);
		curTrainer.addItem(ItemType.BALL);
		curTrainer.addItem(ItemType.BALL);
		curTrainer.addItem(ItemType.BALL);
		curTrainer.addItem(ItemType.ROCK);
		curTrainer.addItem(ItemType.ROCK);
		curTrainer.addItem(ItemType.ROCK);
		curTrainer.addItem(ItemType.ROCK);
	}
		
	public void setTrainer(Trainer trainer){
		this.curTrainer = trainer;
	}
	
	public void setCurMap(Map map){
		this.curMap = map;
	}
		
	public void chooseMap(int i){
		if (i == 0){
			curMap = map_BL;
		}
		else if (i == 1){
			curMap = map_BR;
		}
		else if (i == 2){
			curMap = map_TL;
		}
		else{
			curMap = map_TR;
		}
	}
	
	public boolean hasEncounteredThisBlock(){
		return this.encounteredThisBlock;
	}
	
	public void setEncounteredThisBlock(boolean b){
		this.encounteredThisBlock = b;
	}
	
	public void setMission(Mission mission){
		this.mission = mission;
	}
	
	public Mission getMission(){
		return this.mission;
	}
		
	public Direction getDir(){
		return curTrainer.getFaceDir();
	}
	
	// get the location of the trainer
	public Point getLocation(){
		Point p = new Point();
		p.setLocation(xCoords, yCoords);
		return p;
	}
	
	public Point getPrevLocation(){
		Point p = new Point();
		p.setLocation(xPrevCoords, yPrevCoords);
		return p;
	}
	
	// get the current map of the game
	public Map getCurMap(){
		return curMap;
	}
	
	// initiate the map
	private void initiateMap(){
		map_BL = new Map_BottomLeft();
		map_BR = new Map_BottomRight();
		map_TL = new Map_TopLeft();
		map_TR = new Map_TopRight();
	}	
		
	// set the coordinate of the trainer
	public void setLocation(int x, int y){
		this.xPrevCoords = xCoords;
		this.yPrevCoords = yCoords;
		this.xCoords = x;
		this.yCoords = y;
	}
	
	private void changeDir(Direction dir){
		this.curTrainer.setFaceDir(dir);
	}
	
	public void update(){
		super.setChanged();
		super.notifyObservers();
	}
	
	public void updateBattleView(Item item){
		super.setChanged();
		super.notifyObservers(item);
	}
	
	// move the trainer
	public void moveTrainer(Direction dir){
		// change direction first
		this.changeDir(dir);
				
		int nextX = xCoords;
		int nextY = yCoords;
		
		// move direction to the east
		if (dir == Direction.EAST){
			//System.out.println("Move to EAST");
			// set the next coords
			nextX = xCoords + 1;
			nextY = yCoords;
		}
		// move to west
		else if (dir == Direction.WEST){
			//System.out.println("Move to WEST");
			// set the next coords
			nextX = xCoords - 1;
			nextY = yCoords;
		}
		// move to south
		else if (dir == Direction.SOUTH){
			//System.out.println("Move to SOUTH");
			// set the next coords
			nextX = xCoords;
			nextY = yCoords + 1;
		}
		// move to north
		else{
			//System.out.println("Move to NORTH");
			// set the next coords
			nextX = xCoords;
			nextY = yCoords - 1;
		}
		
		// block the path if is an obstacle
		if (curMap.getBlock(nextX, nextY).getObstacle() != ObstacleType.NONE){
			// TODO: notify the user that its not passable
			// 		Check if it is an item
			setLocation(xCoords, yCoords);
		}
		/*
		// trigger the portal
		else if (curMap.getBlock(nextX, nextY).getInteractType() == InteractType.PORTAL){
			// count step
			curTrainer.incrementStep(1);
			// TODO: call the change map function of the map
			Point p = new Point();
			p.setLocation(nextX, nextY);						
			curMap = curMap.changeMap(p);
		}
		*/		
		else{
			setLocation(nextX, nextY);
			curTrainer.incrementStep(1);
			update();
			//pokemonEncounter();
		}
	}
	
	// Algorithm to encounter a pokemon when moving upon area that could met a pokemon
	public void pokemonEncounter(){
		// Check if the current map block is interactType
		if (curMap.getBlock(xCoords, yCoords).getInteractType() != InteractType.NONE){
			// roll the dice to see if encounter a pokemon
			// if not encounter return with nothing
			if (Math.random() > curMap.getBlock(xCoords, yCoords).getInteractType().getBasicEncounterRate()){
				return;
			}
			// encounter the pokemon
			else{
				curTrainer.setCurEncounterPokemon(wildPokemonGenerator.generatePokemon());
				this.setEncounteredThisBlock(true);
				update();
			}
		}
	}
	
	/************ algorithm to check if the pokemon is caught ************/
	public boolean checkIfCaughtPokemon(Pokemon p){
		// calculate the dynamic catch rate
		if (Math.random() < calculateCurCaughtChance(p)){
			return true;
		}
		else{
			return false;
		}
		
	}
	
	public double calculateCurCaughtChance(Pokemon p){
		double chance = (3 * p.getMaxHP() - 2 * p.getCurHP()) * p.getBasicCapRate() * (1 + curTrainer.getBonusCapture()) / (3 * p.getMaxHP());
		return chance;
	}
	
	/************ algorithm to check if the pokemon will run ************/
	public boolean checkIfRunPokemon(Pokemon p){
		// check if below the alert hp line first
		if (p.getCurHP() < p.getCapHpLimit()){
			return true;
		}
		else if (Math.random() < calculateCurRunChance(p)){
			return true;
		}
		else{
			return false;
		}
	}
	
	public double calculateCurRunChance(Pokemon p){
		double chance = (1 - p.getCurHP() / p.getMaxHP()) * (1 + p.getBasicRunChance()) * (1 - curTrainer.getReducedRun());
		return chance;
	}
		
	public void checkLost(){
		this.isLost = mission.checkMissionFailed(curTrainer);
	}
	
	public void checkWin(){
		this.isWin = mission.checkMissionComplete(curTrainer);
	}
	
	public boolean isLost(){
		checkLost();
		return this.isLost;
	}
	
	public boolean isWin(){
		checkWin();
		return this.isWin;
	}
	
	public boolean isOver(){
		if (isWin || isLost){
			return true;
		}
		else{
			return false;
		}
	}
	
	public int getStepCount(){
		return curTrainer.getStepCount();
	}
	
	public Trainer getTrainer(){
		return this.curTrainer;
	}
	
	/*
	// call by the user to use an item
	public void useItem(int index, Object object){
		ItemType type = curTrainer.getInventory().getItemType(index);
		System.out.println(type.getClass());
		this.curTrainer.useItem(index, object);
		setChanged();
		notifyObservers(type);
	}
	*/
}
