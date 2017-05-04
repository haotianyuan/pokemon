package test;

import static org.junit.Assert.*;

import org.junit.Test;

import GameModel.Direction;
import Inventory.ItemCollection;
import Inventory.ItemType;
import Pokemon.Caterpie;
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
		
		Caterpie caterpie = new Caterpie("caterpie");
		trn.getPokemonCollection();
		trn.setCurEncounterPokemon(caterpie);
		trn.getCurEncounterPokemon();
		trn.incrementBonusTurn(1);
		trn.decrementBonusTurn(1);
		trn.getTotalStepCount();
		trn.getBonusTurn();
		trn.useItem(0, ItemType.BAIT);
		trn.checkItemUsable(0, ItemType.BAIT);
		trn.getInventory();
		
		for (int i  = 0; i < trn.getRowCount(); i ++){
			for (int j = 0; j < trn.getColumnCount(); j ++){
				System.out.println(trn.getValueAt(i, j));
			}
		}
		
		
	}

}
