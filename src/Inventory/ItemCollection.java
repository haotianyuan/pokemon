package Inventory;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import Pokemon.Pokemon;
import Trainer.Trainer;

public class ItemCollection implements TableModel, Serializable{

	private static final long serialVersionUID = 2624045140265552470L;
	
	
	private ArrayList<Item> itemList;
	
	public ItemCollection(){
		this.itemList = new ArrayList<Item>();		
	}
	
	// use the selected item
	public boolean useItem(int index, Object object){
		/*
		if (object.getClass() == Trainer.class){
			
			boolean result = itemList.get(index).useItem((Trainer) object);
			
			// check the number of the item, remove it if it is zero
			if (itemList.get(index).getCount() == 0){
				itemList.remove(index);
			}
			return result;
		}
		else if (Pokemon.class.isInstance(object)){
			//System.out.println("Using upon pokemon");
			
			boolean result = itemList.get(index).useItem((Pokemon) object);
			
			// check the number of the item, remove it if it is zero
			if (itemList.get(index).getCount() == 0){
				itemList.remove(index);
			}
			
			return result;
		}
		else{
			return false;
		}
		*/
		if (checkItemUsable(index, object)){
			itemList.get(index).useItem(object);
			
			// check the number of the item, remove it if it is zero
			if (itemList.get(index).getCount() == 0){
				itemList.remove(index);
			}
			return true;
		}
		else{
			return false;
		}
	}
	
	// check if the item is usable
	public boolean checkItemUsable(int index, Object object){
		return itemList.get(index).checkItemUsable(object);
	}
	
	public ItemType getItemType(int index){
		return itemList.get(index).getType();
	}
		
	public void addItem(ItemType type){
		// increase the count for the item
		for (Item tempItem : itemList) {
			if (tempItem.getType() == type){
				tempItem.increment(1);
				return;
			}
		}
		
		// create a new item and add into the list
		this.itemList.add(this.createItem(type));
		return;
		
	}
	
	private Item createItem(ItemType type){
		if (type == ItemType.BALL){
			return new SafariBall();
		}
		
		if (type == ItemType.ROCK){
			return new Rock();
		}
		
		if (type == ItemType.BAIT){
			return new Bait();
		}
		
		if (type == ItemType.CAPTURE_POTION_LARGE){
			return new CapturePotion_Large();
		}
		
		if (type == ItemType.CAPTURE_POTION_MEDIUM){
			return new CapturePotion_Medium();
		}
		
		if (type == ItemType.CAPTURE_POTION_SMALL){
			return new CapturePotion_Small();
		}
		
		if (type == ItemType.STEP_POTION_LARGE){
			return new StepPotion_Large();
		}
		
		if (type == ItemType.STEP_POTION_MEDIUM){
			return new StepPotion_Medium();
		}
		
		if (type == ItemType.STEP_POTION_SMALL){
			return new StepPotion_Small();
		}
		
		return null;
		
	}
	
	
	public Item getItem(int index){
		return itemList.get(index);
	}
	

	@Override
	public void addTableModelListener(TableModelListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Class<?> getColumnClass(int col) {
		if (col == 0){
			return String.class;
		}
		
		if (col == 1){
			return Integer.class;
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
			return "Item Type";
		}
		
		if (col == 1){
			return "Quantity";
		}
		
		return null;
	}

	@Override
	public int getRowCount() {
		return itemList.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (col == 0){
			return itemList.get(row).getName();
		}
		else{
			return itemList.get(row).getCount();
		}
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

