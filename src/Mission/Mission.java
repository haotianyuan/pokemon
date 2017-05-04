package Mission;

import java.io.Serializable;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import Trainer.Trainer;

public class Mission implements TableModel, Serializable{

	private static final long serialVersionUID = 5722314330503884060L;
	
	private int stepCap;
	private final int initBall;
	private final int epicRequirement;
	private final int totalRequirement;
	private final int legendRequirement;
	private final int commonRequirement;
	private final int uncommonRequirement;
	private final int rareRequirement;
	private final MissionType type;
	private Trainer curTrainer;
	
	
	// constructor
	public Mission(MissionType mission){
		this.type = mission;
		// record when 500 step down
		if (mission == MissionType.STANDARDLADDER){
			this.stepCap = 1000;
			this.initBall = 100;
			this.commonRequirement = 0;
			this.uncommonRequirement = 0;
			this.rareRequirement = 0;
			this.epicRequirement = 0;
			this.totalRequirement = 0;
			this.legendRequirement = 0;
		}
		else if (mission == MissionType.TWENTYPOKEMON){
			this.stepCap = 500;
			this.initBall = 30;
			this.commonRequirement = 0;
			this.uncommonRequirement = 0;
			this.rareRequirement = 0;
			this.epicRequirement = 0;
			this.totalRequirement = 20;
			this.legendRequirement = 0;
		}
		else if (mission == MissionType.THIRTYPOKEMON){
			this.stepCap = 500;
			this.initBall = 30;
			this.commonRequirement = 0;
			this.uncommonRequirement = 0;
			this.rareRequirement = 0;
			this.epicRequirement = 0;
			this.totalRequirement = 30;
			this.legendRequirement = 0;
		}
		else if (mission == MissionType.FIFTYPOKEMON){
			this.stepCap = 500;
			this.initBall = 30;
			this.commonRequirement = 0;
			this.uncommonRequirement = 0;
			this.rareRequirement = 0;
			this.epicRequirement = 0;
			this.totalRequirement = 50;
			this.legendRequirement = 0;
		}
		else if (mission == MissionType.FIVEEPIC){
			this.stepCap = 500;
			this.initBall = 30;
			this.commonRequirement = 0;
			this.uncommonRequirement = 0;
			this.rareRequirement = 0;
			this.epicRequirement = 5;
			this.totalRequirement = 0;
			this.legendRequirement = 0;
		}
		else if (mission == MissionType.FINDLENGEND){
			this.stepCap = 1000;
			this.initBall = 30;
			this.commonRequirement = 0;
			this.uncommonRequirement = 0;
			this.rareRequirement = 0;
			this.epicRequirement = 0;
			this.totalRequirement = 0;
			this.legendRequirement = 1;
		}
		else{
			this.stepCap = 25;
			this.initBall = 30;
			this.commonRequirement = 0;
			this.uncommonRequirement = 0;
			this.rareRequirement = 0;
			this.epicRequirement = 0;
			this.totalRequirement = 15;
			this.legendRequirement = 0;
		}
	}
	
	public void setTrainer(Trainer trainer){
		curTrainer = trainer;
	}
	
	public int getStepCap(){
		return this.stepCap;
	}
	
	public MissionType getMissionType(){
		return this.type;
	}
	
	public int getInitBall(){
		return this.initBall;
	}
	
	public int getRareRequirement(){
		return this.epicRequirement;
	}
	
	public int getTotalRequirement(){
		return this.totalRequirement;
	}
	
	public int getLegendRequirement(){
		return this.legendRequirement;
	}
	
	public void incrementStepCap(int num){
		this.stepCap += num;
	}
	
	public void decrementStepCap(int num){
		this.stepCap -= num;
	}
	
	// Check if the mission is failed
	public boolean checkMissionFailed(Trainer curTrainer){
		if (curTrainer.getStepCount() >= this.getStepCap() && !this.checkMissionComplete(curTrainer)){
			return true;
		}
		else{
			return false;
		}
	}
	
	// check if the mission is complete
	public boolean checkMissionComplete(Trainer curTrainer){
		if (type == MissionType.TWENTYPOKEMON){
			if (curTrainer.getPokemonCollection().getSize() >= this.getTotalRequirement()){
				return true;
			}
			else{
				return false;
			}
		}
		else if (type == MissionType.FIFTYPOKEMON){
			if (curTrainer.getPokemonCollection().getSize() >= this.getTotalRequirement()){
				return true;
			}
			else{
				return false;
			}
		}
		else if (type == MissionType.THIRTYPOKEMON){
			if (curTrainer.getPokemonCollection().getSize() >= this.getTotalRequirement()){
				return true;
			}
			else{
				return false;
			}
		}
		else if (type == MissionType.FIVEEPIC){
			if (curTrainer.getPokemonCollection().getEpicNum() >= this.epicRequirement){
				return true;
			}
			else{
				return false;
			}
		}
		else if (type == MissionType.FINDLENGEND){
			if (curTrainer.getPokemonCollection().getLegendNum() >= this.legendRequirement){
				return true;
			}
			else{
				return false;
			}
		}
		else if (type == MissionType.TEST){
			if (curTrainer.getPokemonCollection().getSize() >= this.getTotalRequirement()){
				return true;
			}
			else{
				return false;
			}
		}
		// ladder
		else{
			if (curTrainer.getStepCount() >= this.getStepCap()){
				return true;
			}
			else{
				return false;
			}
		}
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public int getColumnCount() {

		return 2;
	}

	@Override
	public String getColumnName(int col) {
		if (col == 0){
			return "Requirement";
		}
		
		if (col == 1){
			return "Progressing";
		}

		return null;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return 7;
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (row == 0 && col == 0){
			return "Step Limit";
		}
		if (row == 0 && col == 1){
			return curTrainer.getStepCount() + " / " + this.getStepCap();
		}
		
		if (row == 1 && col == 0){
			return "Common Requirement";
		}
		if (row == 1 && col == 1){
			return curTrainer.getPokemonCollection().getCommonNum() + " / " + this.commonRequirement;
		}
		
		if (row == 2 && col == 0){
			return "Uncommon Requirement";
		}
		if (row == 2 && col == 1){
			return curTrainer.getPokemonCollection().getUncommonNum() + " / " + this.uncommonRequirement;
		}
		
		if (row == 3 && col == 0){
			return "Rare Requirement";
		}
		if (row == 3 && col == 1){
			return curTrainer.getPokemonCollection().getRareNum() + " / " + this.rareRequirement;
		}
		
		if (row == 4 && col == 0){
			return "Rare Requirement";
		}
		if (row == 4 && col == 1){
			return curTrainer.getPokemonCollection().getEpicNum() + " / " + this.epicRequirement;
		}
		
		if (row == 5 && col == 0){
			return "Rare Requirement";
		}
		if (row == 5 && col == 1){
			return curTrainer.getPokemonCollection().getLegendNum() + " / " + this.legendRequirement;
		}
		
		if (row == 6 && col == 0){
			return "Rare Requirement";
		}
		if (row == 6 && col == 1){
			return curTrainer.getPokemonCollection().getSize() + " / " + this.totalRequirement;
		}
		
		// TODO Auto-generated method stub
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
