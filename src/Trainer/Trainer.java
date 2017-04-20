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
	//private final String userName;
	//private final String passWord;
	private final String id;
	private final LocalDateTime birthDate;
	
	// character information
	private Direction faceDir;
	private PokemonCollection pokemonCollection;
	private ItemCollection inventory;
	
	// in-game information
	private int stepCount;
	private int xCoords;
	private int yCoords;
	private double bonusCapture;
	private double bonusRun;
	
	public Trainer(String id){
		this.id = id;
		this.birthDate = LocalDateTime.now();
		
		this.faceDir = Direction.SOUTH;
		this.pokemonCollection = new PokemonCollection();
		this.inventory = new ItemCollection();
		
		this.bonusCapture = 0;
		this.bonusRun = 0;
	}
	
	public Direction getFaceDir(){
		return faceDir;
	}
	
	public void setFaceDir(Direction dir){
		this.faceDir = dir;
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
	}
	
	public void decrementStep(int num){
		this.stepCount -= num;
	}
	
	public void setDirection(Direction dir){
		this.faceDir = dir;
	}
	
	public void addItem(ItemType item){
		this.inventory.addItem(item);
	}
	
	public void catchPokemon(Pokemon newPokemon){
		this.pokemonCollection.addPokemon(newPokemon);
	}
	
	public void incrementBonusCapture(double num){
		this.bonusCapture += num;
	}
	
	public void decrementBonusCapture(double num){
		this.bonusCapture -= num;
	}
	
	
	public void incrementBonusRun(double num){
		this.bonusRun += num;
	}
	
	public void decrementBonusRun(double num){
		this.bonusRun -= num;
	}
	
	public int getStepCount(){
		return this.stepCount;
	}
	
	public int getRow(){
		return this.xCoords;
	}
	
	public int getCol(){
		return this.yCoords;
	}
	
	public double getBonusCapture(){
		return this.bonusCapture;
	}
	
	public double getBonusRun(){
		return this.bonusRun;
	}
	
	public String getID(){
		return this.id;
	}
	
	/*
	public void useItem(int num){
		inventory.useItem(num, this);
	}
	*/
}
