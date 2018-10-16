package Pokemon;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Random;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

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


public abstract class Pokemon implements TableModel, Serializable{

	private static final long serialVersionUID = 1847973297212434777L;
	
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
	
	private double increasedCapRate;
	private double reducedRunChance;
	
	private int capHpLimit;	// the maximum hp allowed to be captured
	//private int curMaxTurn;	// individual max turn
	private int captureTurn;	// store the turns spent on capture this pokemon
	
	
	// constructor
	public Pokemon(String name, Pokedex specy){
		this.metDate = LocalDateTime.now();
		this.pokemonSpecy = specy;
		this.quality = specy.getQuality();
		this.randomSeed = specy.getIndex();
		
		this.name = specy.getName();
		this.nickName = name;
		
		// randomly generate hp
		this.maxHP = randomHP(specy.getBasicHP());
		this.curHP = this.maxHP;
		
		// randomly generate capture rate
		this.basicCapRate = randomCapRate(specy.getQuality().getCapRate());
		this.increasedCapRate = 0;
		
		// randomly generate run chance
		this.basicRunChance = randomRunChance(specy.getQuality().getRunChance());
		this.reducedRunChance = 0;
		
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
		return originCapRate * ((Math.random() - 0.5) * 0.075 + 1 );
	}
	
	
	// TODO: need a algorithm to randomly set up the run chance
	// 		run chance should be reversely propagate to the
	//		capture rate
	private double randomRunChance(double originRunChance){
		return originRunChance * ((Math.random() - 0.5) * 0.075 + 1 );
	}
	
	// TODO: need a algorithm to randomly set up the max hp for
	//		capture
	private int randomCapHP(int maxHP){
		return (int)(0.2 * maxHP);
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
	

	public double getCurCapRate(){
		return (double)basicCapRate * (1 + increasedCapRate);
	}
	
	public double getCurRunChance(){
		return (double)basicRunChance * (1 + reducedRunChance);
	}
		
	public void incrementAlteredCapRate(double incre){
		this.increasedCapRate = incre + this.increasedCapRate;
		if(this.increasedCapRate > 1){
			this.increasedCapRate = 1.0;
		}
	}
	
	public void decrementAlteredCapRate(double decre){
		this.increasedCapRate =  - decre + this.increasedCapRate;
		if(this.increasedCapRate < 0){
			this.increasedCapRate = 0;
		}
	}
	
	public void incrementAlteredRunChance(double incre){
		this.reducedRunChance += incre;
		if (this.reducedRunChance > 1){
			this.reducedRunChance = 1;
		}
	}
	
	public void decrementAlteredRunChance(double decre){
		this.reducedRunChance -= decre;
		/*
		if (this.reducedRunChance < 0){
			this.reducedRunChance = 0;
		}
		*/
	}
	
	/*
	public int getCurMaxTurn()	{
		return this.curMaxTurn;
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
	
	
	
	@Override
	public void addTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Class<?> getColumnClass(int col) {
		return String.class;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(int col) {
		if (col == 0){
			return "Name";
		}
		
		if (col == 1){
			return "Value";
		}
		
		return null;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return 13;
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (row == 0 && col == 0){
			return "Specy";
		}
		if (row == 0 && col == 1){
			return this.getSpecy().getName();
		}
		
		if (row == 1 && col == 0){
			return "Nickname";
		}
		if (row == 1 && col == 1){
			return this.getNickName();
		}
		
		if (row == 2 && col == 0){
			return "Meet Date";
		}
		if (row == 2 && col == 1){
			return this.recordMetDate();
		}
		
		if (row == 3 && col == 0){
			return "Current HP";
		}
		if (row == 3 && col == 1){
			return this.getCurHP();
		}
		
		if (row == 4 && col == 0){
			return "Max HP";
		}
		if (row == 4 && col == 1){
			return this.getMaxHP();
		}
		
		if (row == 5 && col == 0){
			return "Quality";
		}
		if (row == 5 && col == 1){
			return this.getSpecy().getQuality();
		}
		
		if (row == 6 && col == 0){
			return "Basic Capture Chance";
		}
		if (row == 6 && col == 1){
			return this.getBasicCapRate() * 100 + "%";
		}
		
		if (row == 7 && col == 0){
			return "Basic Run Chance";
		}
		if (row == 7 && col == 1){
			return this.getBasicRunChance() * 100 + "%";
		}
		
		if (row == 8 && col == 0){
			return "Max Turn Before Run";
		}
		if (row == 8 && col == 1){
			return this.getBasicMaxTurn();
		}
		
		if (row == 9 && col == 0){
			return "Current Capture Chance";
		}
		if (row == 9 && col == 1){
			return this.getCurCapRate() * 100 + "%";
		}
		
		if (row == 10 && col == 0){
			return "Current Run Chance";
		}
		if (row == 10 && col == 1){
			return this.getCurRunChance() * 100 + "%";
		}
		
		if (row == 11 && col == 0){
			return "Turn Used";
		}
		if (row == 11 && col == 1){
			return this.getCapTurn();
		}
		
		return null;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		
	}
	
	public abstract String getIntro();
	
	
}
