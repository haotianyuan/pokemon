package Trainer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

import GameModel.Direction;
import Inventory.ItemCollection;
import Inventory.ItemType;
import Pokemon.Pokemon;
import Pokemon.PokemonCollection;
import Inventory.Item;

public class Trainer implements Serializable{

	private static final long serialVersionUID = 3185483056462008814L;
	
	// loggin information
	private final String id;
	private final LocalDateTime birthDate;
	
	// character information
	private Direction faceDir;
	private PokemonCollection pokemonCollection;
	private ItemCollection inventory;
	
	// in-game information
	private int stepCount;
	private int totalStepCount;
	private int xCoords;
	private int yCoords;
	private Pokemon curEncounterPokemon = null;
	
	// bonus attribute
	private int bonusTurn;	// add bonus turn for capture the pokemon
	private double bonusCapture;
	private double reducedRun;
	
	// battle field bonus attribute
	private double battleBonusCapture;	// store the bonus capture rate during battle
	private double battleReducedRun;	// store the reduced run chance during battle
	
	public Trainer(String id){
		this.id = id;
		this.birthDate = LocalDateTime.now();
		
		this.faceDir = Direction.SOUTH;
		this.pokemonCollection = new PokemonCollection();
		this.inventory = new ItemCollection();
		
		this.bonusCapture = 0;
		this.reducedRun = 0;
		this.bonusTurn = 0;
	}
	
	public Direction getFaceDir(){
		return faceDir;
	}
	
	public void setFaceDir(Direction dir){
		this.faceDir = dir;
	}
	
	public void setCurEncounterPokemon(Pokemon p){
		this.curEncounterPokemon = p;
	}
	
	public Pokemon getCurEncounterPokemon(){
		return this.curEncounterPokemon;
	}
	
	public void setLocation(int x, int y){
		xCoords = x;
		yCoords = y;
	}
	
	public PokemonCollection getPokemonCollection(){
		return this.pokemonCollection;
	}
	
	public ItemCollection getInventory(){
		return this.inventory;
	}
	
	public void incrementStep(int num){
		this.stepCount += num;
		this.totalStepCount += num;
	}
	
	public void decrementStep(int num){
		this.stepCount -= num;
	}
		
	public void addItem(ItemType item){
		this.inventory.addItem(item);
	}
	
	public void catchPokemon(Pokemon newPokemon){
		this.pokemonCollection.addPokemon(newPokemon);
	}
	
	
	public void incrementBonusTurn(int num){
		this.bonusTurn += num;
	}
	
	public void decrementBonusTurn(int num){
		this.bonusTurn -= num;
	}	
	
	public void incrementBonusCapture(double num){
		this.bonusCapture += num;
	}
	
	public void decrementBonusCapture(double num){
		this.bonusCapture -= num;
	}
	
	
	public void incrementReducedRun(double num){
		this.reducedRun += num;
	}
	
	public void decrementReducedRun(double num){
		this.reducedRun -= num;
	}
	
	public int getStepCount(){
		return this.stepCount;
	}
	
	public int getTotalStepCount(){
		return this.totalStepCount;
	}
		
	public int getRow(){
		return this.yCoords;
	}
	
	public int getCol(){
		return this.xCoords;
	}
	
	public int getBonusTurn(){
		return this.bonusTurn;
	}
	
	public double getBonusCapture(){
		return this.bonusCapture;
	}
	
	public double getReducedRun(){
		return this.reducedRun;
	}
	
	public String getID(){
		return this.id;
	}
	
	public boolean useItem(int index, Object object){
		return inventory.useItem(index, object);
	}
	
	public boolean checkItemUsable(int index, Object object){
		return inventory.checkItemUsable(index, object);
	}
}
