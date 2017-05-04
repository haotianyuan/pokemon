package test;

import static org.junit.Assert.*;

import org.junit.Test;

import Inventory.*;
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
		
	}
}
 