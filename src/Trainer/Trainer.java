package Trainer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import GameModel.Direction;
import Inventory.ItemCollection;
import Inventory.ItemType;
import Pokemon.Pokemon;
import Pokemon.PokemonCollection;
import Inventory.Item;

public class Trainer implements TableModel, Serializable{

	private static final long serialVersionUID = 3185483056462008814L;
	
	// loggin information
	private final String id;
	private final LocalDateTime birthDate;
	
	// character information
	private Direction faceDir;
	private PokemonCollection pokemonCollection;
	private ItemCollection inventory;
	
	// in-game information
	private int stepCount = 0;
	private int totalStepCount = 0;
	private int xCoords;
	private int yCoords;
	private Pokemon curEncounterPokemon = null;
	
	// bonus attribute
	private int bonusTurn = 0;	// add bonus turn for capture the pokemon
	private double bonusCapture;
	private double reducedRun;
	public boolean justCaught = false;
	
	// battle field bonus attribute
	//private double battleBonusCapture;	// store the bonus capture rate during battle
	//private double battleReducedRun;	// store the reduced run chance during battle
	
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
	
	public void resetCurEncounterPokemon(){
		curEncounterPokemon = null;
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
		System.out.println(this.bonusCapture);
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

	
	/*
	 * *************************************** *
	 *  	      TableModel Below             *
	 * *************************************** *
	 */
	@Override
	public void addTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Class<?> getColumnClass(int col) {
		if (col == 0){
			return String.class;
		}
		if (col == 1){
			return String.class;
		}
		
		return null;
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
		return 14;
	}

	@Override
	public Object getValueAt(int row, int col) {
		// ID
		if (row == 0 && col == 0){
			return "Trainer Name";
		}
		if (row == 0 && col == 1){
			return this.id;
		}
		
		// B-DAY
		if (row == 1 && col == 0){
			return "Birth Date";
		}
		if (row == 1 && col == 1){
			return this.birthDate.toString();
		}
		
		// STEP
		if (row == 2 && col == 0){
			return "Current Step";
		}
		if (row == 2 && col == 1){
			return this.stepCount;
		}
		
		// STEP TOTAL
		if (row == 3 && col == 0){
			return "Total Step";
		}
		if (row == 3 && col == 1){
			return this.totalStepCount;
		}
		
		// POKEMON TOTAL
		if (row == 4 && col == 0){
			return "Total Pokemon";
		}
		if (row == 4 && col == 1){
			return this.pokemonCollection.getSize();
		}
		
		// COMMON POKEMON
		if (row == 5 && col == 0){
			return "Common Pokemon";
		}
		if (row == 5 && col == 1){
			return this.pokemonCollection.getCommonNum();
		}
		
		if (row == 6 && col == 0){
			return "Uncommon Pokemon";
		}
		if (row == 6 && col == 1){
			return this.pokemonCollection.getUncommonNum();
		}
		
		if (row == 7 && col == 0){
			return "Rare Pokemon";
		}
		if (row == 7 && col == 1){
			return this.pokemonCollection.getRareNum();
		}
		
		if (row == 8 && col == 0){
			return "Epic Pokemon";
		}
		if (row == 8 && col == 1){
			return this.pokemonCollection.getEpicNum();
		}
		
		if (row == 9 && col == 0){
			return "Legendary Pokemon";
		}
		if (row == 9 && col == 1){
			return this.pokemonCollection.getLegendNum();
		}
		
		// BONUS CAPTURE CHANCE
		if (row == 10 && col == 0){
			return "Bonus Capture";
		}
		if (row == 10 && col == 1){
			return this.getBonusCapture() * 100 + "%";
		}
		
		// REDUCED RUN CHANCE
		if (row == 11 && col == 0){
			return "Reduced Run";
		}
		if (row == 11 && col == 1){
			return this.getReducedRun() * 100 + "%";
		}
		
		// DIRECTION
		if (row == 12 && col == 0){
			return "Direction";
		}
		if (row == 12 && col == 1){
			return this.getFaceDir().name();
		}
		
		// Coordinates
		if (row == 13 && col == 0){
			return "Location";
		}
		if (row == 13 && col == 1){
			return "( " + this.xCoords + ", " + this.yCoords + " )";
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
}
