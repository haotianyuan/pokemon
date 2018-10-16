/*
 * Author: Mengtao Tang
 * Date: 4/20/2017
 * Course: CSC_335
 * Purpose: This is class stores pokemon list
 * 			TODO: need to implement JTable
 * 
 */

package Pokemon;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class PokemonCollection implements TableModel, Serializable{
	private static final long serialVersionUID = -1946656042672613799L;
	
	// an array list to store the pokemon
	private ArrayList<Pokemon> pokemonList;
	
	
	// constructor
	public PokemonCollection(){
		this.pokemonList = new ArrayList<Pokemon>();
	}
	
	public Pokemon getPokemon(int index){
		return this.pokemonList.get(index);
	}
	
	// add new pokemon into the collection
	public void addPokemon(Pokemon newPokemon){
		this.pokemonList.add(newPokemon);
	}
	
	// get the size of the collection
	public int getSize(){
		return this.pokemonList.size();
	}
	
	// get the number of common pokemon
	public int getCommonNum(){
		int counter = 0;
		for (Pokemon p : pokemonList){
			if (p.getSpecy().getQuality() == PokemonQuality.COMMON){
				counter++;
			}
		}
		
		return counter;
	}
	
	// get the number of uncommon pokemon
	public int getUncommonNum(){
		int counter = 0;
		for (Pokemon p : pokemonList){
			if (p.getSpecy().getQuality() == PokemonQuality.UNCOMMON){
				counter++;
			}
		}
		
		return counter;
	}
	
	// get the number of common pokemon
	public int getRareNum(){
		int counter = 0;
		for (Pokemon p : pokemonList){
			if (p.getSpecy().getQuality() == PokemonQuality.RARE){
				counter++;
			}
		}
		
		return counter;
	}
	
	// get the number of common pokemon
	public int getEpicNum(){
		int counter = 0;
		for (Pokemon p : pokemonList){
			if (p.getSpecy().getQuality() == PokemonQuality.EPIC){
				counter++;
			}
		}
		
		return counter;
	}
	
	// get the number of common pokemon
	public int getLegendNum(){
		int counter = 0;
		for (Pokemon p : pokemonList){
			if (p.getSpecy().getQuality() == PokemonQuality.LEGENDARY){
				counter++;
			}
		}
		
		return counter;
	}


	@Override
	public void addTableModelListener(TableModelListener arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Class<?> getColumnClass(int col) {
		if (col == 0){
			return Integer.class;
		}
		
		if (col == 1){
			return String.class;
		}
		
		if (col == 2){
			return String.class;
		}
		
		if (col == 3){
			return LocalDateTime.class;
		}
		
		return null;
	}


	@Override
	public int getColumnCount() {
		return 4;
	}


	@Override
	public String getColumnName(int col) {
		if (col == 0){
			return "Pokedex Index";
		}
		
		if (col == 1){
			return "Type";
		}
		
		if (col == 2){
			return "Nickname";
		}
		
		if (col == 3){
			return "Captured Time";
		}
		
		return null;
	}


	@Override
	public int getRowCount() {
		return this.pokemonList.size();
	}


	@Override
	public Object getValueAt(int row, int col) {
		if (col == 0){
			return this.pokemonList.get(row).getSpecy().getIndex();
		}
		if (col == 1){
			return this.pokemonList.get(row).getSpecy().getName();
		}		
		if (col == 2){
			return this.pokemonList.get(row).getNickName();
		}
		if (col == 3){
			return this.pokemonList.get(row).recordMetDate();
		}
		
		return null;
	}


	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void removeTableModelListener(TableModelListener arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setValueAt(Object arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	
}


