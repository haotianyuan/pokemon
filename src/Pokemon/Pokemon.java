package Pokemon;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Random;

/*
 * Author: Mengtao Tang
 * Date: 4/20/2017
 * Course: CSC_335
 * Purpose: This is class stores detailed information of a specific
 * 			pokemon with defined data. The data could be altered base
 * 			on the function.
 * 			The defined the data for each pokemon includes:
 * 				name
 * 				specy
 *  			quality
 * 				Heath Point (HP) max/cur
 * 				random seed
 * 
 * 				capture base rate
 * 				real capture rate
 * 				run chance		
 * 				real run chance		
 * 				maximum hp capable of being captured
 * 				maximum duration of battle before running
 * 
 * 				encounter date
 * 				captured turn
 * 
 * 				TODO: 
 * 				special ability
 * 
 */


public abstract class Pokemon implements Serializable{
	// fixed data for the pokemon
	private final LocalDateTime metDate;
	private final Pokedex pokemonSpecy;
	private final PokemonQuality quality;
	private final int randomSeed;
	private final int maxHP;
	private final double basicCapRate;
	private final double basicRunChance;
	private final int basicMaxTurn;	// store the max turn before the pokemon run away
	
	// dynamic information for the pokemon
	private String name;
	private String nickName;
	private int curHP;
	
	//private double curCapRate;
	//private double curRunChance;
	
	private int capHpLimit;	// the maximum hp allowed to be captured
	//private int curMaxTurn;	// individual max turn
	private int captureTurn;	// store the turns spent on capture this pokemon
	
	
	// constructor
	public Pokemon(String name, Pokedex specy){
		this.metDate = LocalDateTime.now();
		this.pokemonSpecy = specy;
		this.quality = specy.getQuality();
		this.randomSeed = specy.getIndex();
		
		this.name = name;
		this.nickName = name;
		
		// randomly generate hp
		this.maxHP = randomHP(specy.getBasicHP());
		this.curHP = this.maxHP;
		
		// randomly generate capture rate
		this.basicCapRate = randomCapRate(specy.getQuality().getCapRate());
		//this.curCapRate = this.basicCapRate;
		
		// randomly generate run chance
		this.basicRunChance = randomRunChance(specy.getQuality().getRunChance());
		//this.curRunChance = this.basicRunChance;
		
		// randomly generate the maximum hp that allowed to be captured
		this.capHpLimit = randomCapHP(this.maxHP);
		
		this.basicMaxTurn = specy.getQuality().getMaxTurn();
		
		this.captureTurn = 0;
		//this.curMaxTurn = 0;

	}
	
	
	// health point generator
	// this will generate the health point of the pokemon
	// in a range around its specy's basic health point
	private int randomHP(int hp){
		Random rand = new Random(this.randomSeed);
		double alterRate = rand.nextDouble() - 0.5;
		int newHP = (int) (hp*(1 + alterRate));
		return newHP;
	}
	
	// TODO: need a algorithm to randomly set up the capture 
	//		rate basing on the basic capture rate
	private double randomCapRate(double originCapRate){
		return (double)originCapRate;
	}
	
	
	// TODO: need a algorithm to randomly set up the run chance
	// 		run chance should be reversely propagate to the
	//		capture rate
	private double randomRunChance(double originRunChance){
		return originRunChance;
	}
	
	// TODO: need a algorithm to randomly set up the max hp for
	//		capture
	private int randomCapHP(int maxHP){
		return (int)(0.5 * maxHP);
	}
	
	// getter for the parameter
	public LocalDateTime recordMetDate(){
		return this.metDate;
	}
	
	public Pokedex getSpecy(){
		return this.pokemonSpecy;
	}
	
	public PokemonQuality getQuality(){
		return this.quality;
	}
	
	public int getMaxHP(){
		return this.maxHP;
	}
	
	public int getCurHP(){
		return this.curHP;
	}
	
	public String getName(){
		return this.name;
	}
	
	public double getBasicCapRate(){
		return this.basicCapRate;
	}
	
	public double getBasicRunChance(){
		return this.basicRunChance;
	}
		
	public int getCapHpLimit(){
		return this.capHpLimit;
	}
	
	public int getCapTurn(){
		return this.captureTurn;
	}
	
	public int getBasicMaxTurn(){
		return this.basicMaxTurn;
	}
	
	
	// setter for the parameter
	public void setName(String newName){
		this.nickName = newName;
	}
	
	public void incrementHP(int incre){
		this.curHP += incre;
		if (this.curHP > this.maxHP){
			this.curHP = this.maxHP;
		}
	}
	
	public void decrementHP(int decre){
		this.curHP -= decre;
		if (this.curHP < 0){
			this.curHP = 0;
		}
	}
	
	/**************** No longer using this algorithm *******************/
	/*
		public double getCurCapRate(){
		return this.curCapRate;
	}
	
	public double getCurRunChance(){
		return this.curRunChance;
	}
	
	public int getCurMaxTurn()	{
		return this.curMaxTurn;
	}
	
	public void incrementCapRate(double incre){
		this.curCapRate *= 1 + incre;
		if(this.curCapRate > 1){
			this.curCapRate = 1.0;
		}
	}
	
	public void decrementCapRate(double decre){
		this.curCapRate *= 1 - decre;
		if(this.curCapRate < 0){
			this.curCapRate = 0;
		}
	}
	
	public void incrementRunChance(double incre){
		this.curRunChance *= 1 + incre;
		if (this.curRunChance > 1){
			this.curRunChance = 1;
		}
	}
	
	public void decrementRunChance(double decre){
		this.curRunChance *= 1 - decre;
		if (this.curRunChance < 0){
			this.curRunChance = 0;
		}
	}
	
	
	public void incrementMaxTurn(int incre){
		this.curMaxTurn += incre;
	}
	
	public void decrementMaxTurn(int decre){
		this.curMaxTurn -= decre;
		if (this.curMaxTurn < 0){
			this.curMaxTurn = 0;
		}
	}
	*/
		
	public void recordCapTurn(int turn){
		this.captureTurn = turn;
	}
	
	public String getNickName(){
		return this.nickName;
	}
	
}
