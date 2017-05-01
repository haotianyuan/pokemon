package test;

import static org.junit.Assert.*;

import org.junit.Test;

import GameModel.Direction;
import Inventory.ItemCollection;
import Inventory.ItemType;
import Trainer.Trainer;

public class TrainerTest {

	@Test
	public void test() {
		Trainer trn = new Trainer("lulu");
		
		trn.setFaceDir(Direction.NORTH);
		assertTrue(trn.getFaceDir() == Direction.NORTH);
		assertTrue(trn.getID().equals("lulu"));
		trn.incrementStep(1);
		trn.decrementStep(1);
		assertTrue(trn.getStepCount() == 0);
		trn.incrementBonusCapture(1.0);
		trn.decrementBonusCapture(1.0);
		trn.incrementReducedRun(1.0);
		trn.decrementReducedRun(1.0);
		assertTrue(trn.getBonusCapture() == 0);
		assertTrue(trn.getReducedRun() == 0);
		trn.setLocation(1, 2);
		assertTrue(trn.getRow() == 2);
		assertTrue(trn.getCol() == 1);
		trn.addItem(ItemType.BAIT);
		ItemCollection ic = trn.getInventory();
		assertTrue(ic.getRowCount() == 2);
	}

}
