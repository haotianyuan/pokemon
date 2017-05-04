package test;

import static org.junit.Assert.*;

import org.junit.Test;

import GameModel.GameModel;
import Inventory.*;
import Pokemon.Mew;
import Pokemon.Pokemon;
import Trainer.Trainer;

public class InventoryTest {
	@Test
	public void test(){
		Item a = new SafariBall();
		Item b = new CapturePotion_Large();
		Item c = new StepPotion_Large();
		a.useItem(null);
		c.getInfo();
		b.getInfo();
		ItemCollection collection = new ItemCollection();
		a.increment(1);
		b.decrement(1);
		assertEquals(b.getCount(), 0);
		assertTrue(a.getName().equals("Safari Ball"));
		assertTrue(a.getType() == ItemType.BALL);
		
		collection.addItem(ItemType.CAPTURE_POTION_LARGE);
		collection.addItem(ItemType.BALL);
		collection.addItem(ItemType.CAPTURE_POTION_MEDIUM);
		collection.addItem(ItemType.CAPTURE_POTION_SMALL);
		collection.addItem(ItemType.STEP_POTION_LARGE);
		collection.addItem(ItemType.STEP_POTION_MEDIUM);
		collection.addItem(ItemType.STEP_POTION_SMALL);
		
		assertTrue(collection.getColumnClass(0) == String.class);
		assertTrue(collection.getColumnClass(1) == Integer.class);
		assertTrue(collection.getColumnCount() == 2);
		assertTrue(collection.getColumnName(0).equals("Item Type"));
		assertTrue(collection.getColumnName(1).equals("Quantity"));
		
		assertTrue(collection.getRowCount() == 7);
		collection.getValueAt(0, 0);
		assertTrue(collection.getItem(0) != null);
		collection.addItem(ItemType.ROCK);
		collection.addItem(ItemType.BAIT);
		collection.addItem(ItemType.BAIT);
		
		System.out.println(collection.getItemType(0));
		System.out.println(collection.getValueAt(0, 1));
		
		GameModel model = new GameModel("tmt", null);
		Pokemon p = new Mew("hi");
		Trainer t = new Trainer("lol");
		
		for (int i = 0; i < 1000; i ++){
			ItemType type = model.generateLoot(0.5);
			if (type != null){
				collection.addItem(type);
				System.out.println(collection.getItem(0).getInfo());
				System.out.println(collection.getItem(0).getEffectMessage());
				if (Math.random() > 0.5){
					collection.useItem(0, p);
				}
				else{
					collection.useItem(0, t);
				}
			}

		}
	}
}
 