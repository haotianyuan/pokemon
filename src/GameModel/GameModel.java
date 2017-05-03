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
	private int xNext;
	private int yNext;
		
	// map information
	private Map_Test map_Test;
	private Map_00 map_00;
	private Map_01 map_01;
	private Map_02 map_02;
	private Map_10 map_10;
	private Map_11 map_11;
	private Map_12 map_12;
	
	private MapPokemon curMap;
	private boolean teleporting = false;
	
	// battle information
	private WildPokemonGenerator wildPokemonGenerator = WildPokemonGenerator.getInstance();
	private boolean encounteredThisBlock = false;	// flag to check if just encounter a pokemon before move
	
	// game information
	private Mission mission;
	private boolean isOver = false;
	private boolean isWin = false;
	private boolean isLost = false;
	
	
	public GameModel(){
		initiateMap();
		// test part
		curTrainer = new Trainer("T.M.T.");
		setLocation(22, 31);
		xPrevCoords = 22;
		yPrevCoords = 31;
		setCurMap(map_01);
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
	
	/*
	 * *************************************** *
	 *  	 GameMainPanel Related Below 	   *
	 * *************************************** *
	 */
	
	/**************** Map Related ***************/
	public void setCurMap(MapPokemon map){
		this.curMap = map;
	}
		
	public void chooseMap(String tag){
		if (tag == "00"){
			curMap = map_00;
		}
		else if (tag == "01"){
			curMap = map_01;
		}
		else if (tag == "02"){
			curMap = map_02;
		}
		else if (tag == "10"){
			curMap = map_10;
		}
		else if (tag == "11"){
			curMap = map_11;
		}
		else if (tag == "12"){
			curMap = map_12;
		}
		else{
			curMap = map_Test;
		}
	}
	
	public boolean hasEncounteredThisBlock(){
		return this.encounteredThisBlock;
	}
	
	public void setEncounteredThisBlock(boolean b){
		this.encounteredThisBlock = b;
	}
	
	/**************** Mission Related ***************/
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
	public Point getCurLocation(){
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
	public MapPokemon getCurMap(){
		return curMap;
	}
	
	// initiate the map
	private void initiateMap(){
		map_Test = new Map_Test();
		map_00 = new Map_00();
		map_01 = new Map_01();
		map_02 = new Map_02();
		map_10 = new Map_10();
		map_11 = new Map_11();
		map_12 = new Map_12();
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
	
	/************ algorithm to move the character ************/
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
			System.out.println("Encounter Obstacle: " + curMap.getBlock(nextX, nextY).getObstacle().name());
			setLocation(xCoords, yCoords);
			update();
			return;
		}
		// trigger the portal
		else if (curMap.getBlock(nextX, nextY).getInteractType() == InteractType.PORTAL){
			// count step
			curTrainer.incrementStep(1);
			System.out.println("Encounter Interactable: " + curMap.getBlock(nextX, nextY).getInteractType().name());
			xNext = nextX;
			yNext = nextY;
			//teleportOnline(new Point(nextX, nextY));
			teleporting = true;
			update();
			return;
		}	
		else{
			setLocation(nextX, nextY);
			curTrainer.incrementStep(1);
			update();
			return;
		}
	}
	
	// call the teleport
	public void teleportOnline(Point p){
		Point nextPoint = curMap.getTeleportPoint(p);
		String nextMapTag = curMap.getTeleportMap(p);
		if (nextMapTag == "00"){
			curMap = map_00;
		}
		else if (nextMapTag == "01"){
			curMap = map_01;
		}
		else if (nextMapTag == "02"){
			curMap = map_02;
		}
		else if (nextMapTag == "10"){
			curMap = map_10;
		}
		else if (nextMapTag == "11"){
			curMap = map_11;
		}
		else if (nextMapTag == "12"){
			curMap = map_12;
		}
		else{
			return;
		}
		setLocation(nextPoint.x, nextPoint.y);
		setLocation(nextPoint.x, nextPoint.y);
		
		System.out.println("Next map: " + curMap.getMapName());
		System.out.println("Next start point: " + xCoords + ", " + yCoords);
	}
	
	// check if the character is on portal
	public boolean isTeleporting(){
		return teleporting;
	}
	
	public void doneTeleporting(){
		teleporting = false;
		teleportOnline(new Point(xNext, yNext));
		update();
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
	
	/*
	 * *************************************** *
	 *  	 BattleViewPanel Related Below 	   *
	 * *************************************** *
	 */
	
	/************ algorithm to check if the pokemon is caught ************/
	public boolean checkIfCaughtPokemon(Pokemon p){
		// calculate the dynamic catch rate
		if (Math.random() < calculateCurCaughtChance(p) + 1){
			return true;
		}
		else{
			return false;
		}
		
	}
	
	public double calculateCurCaughtChance(Pokemon p){
		double chance = (3 * p.getMaxHP() - 2 * p.getCurHP()) * p.getCurCapRate() * (1 + curTrainer.getBonusCapture()) / (3 * p.getMaxHP());
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
		else if (p.getCapTurn() > p.getQuality().getMaxTurn()){
			return true;
		}
		else{
			return false;
		}
	}
	
	public double calculateCurRunChance(Pokemon p){
		double chance = (1 - p.getCurHP() / p.getMaxHP()) * (1 + p.getCurRunChance()) * (1 - curTrainer.getReducedRun());
		return chance;
	}
		
	/*
	 * *************************************** *
	 *  	 Trigger/Notify Related Below 	   *
	 * *************************************** *
	 */
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
		
	public Trainer getTrainer(){
		return this.curTrainer;
	}
	
	public void createTrainer(String name){
		curTrainer = new Trainer(name);
	}
	
	public void update(){
		super.setChanged();
		super.notifyObservers();
	}
	
	public void updateBattleView(Item item){
		super.setChanged();
		super.notifyObservers(item);
	}
}
