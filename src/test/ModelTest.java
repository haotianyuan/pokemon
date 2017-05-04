package test;

import static org.junit.Assert.*;

import java.awt.Point;
import java.util.Map;

import org.junit.Test;

import GameModel.Direction;
import GameModel.GameModel;
import Inventory.ItemType;
import Map.*;
import Mission.Mission;
import Mission.MissionType;
import Pokemon.Abra;
import Pokemon.Mew;
import Pokemon.Pokemon;
import Pokemon.WildPokemonGenerator;
import Trainer.Trainer;

public class ModelTest {
	@Test
	public void GameModelTest() {
		GameModel model = new GameModel("aa", MissionType.TWENTYPOKEMON);
		Trainer newTrn = new Trainer("lulu");
		model.setTrainer(newTrn);
		assertTrue(model.getTrainer().getID().equals("lulu"));
		Map_00 newMap = new Map_00();
		model.setCurMap(newMap);
		assertTrue(model.getCurMap() == newMap);
		
		Mission newMission = new Mission(MissionType.TWENTYPOKEMON);
		
		model.setMission(newMission);
		assertTrue(model.getMission() == newMission);
		assertTrue(model.getDir() == model.getTrainer().getFaceDir());
		Point tempPoint = new Point();
		tempPoint.setLocation(10, 10);
		tempPoint.setLocation(0, 0);
		model.update();
		
		
		assertTrue(!model.isLost());
		assertTrue(!model.isWin());
		assertTrue(!model.isOver());
		assertTrue(model.getCurMap().getBlock(0, 0).getGround() != GroundType.SOIL);
		assertTrue(model.getCurMap().getBlock(0, 0).getInteractType() == InteractType.NONE);
		assertFalse(model.getCurMap().getBlock(0, 0).isPassable());
		
		model.chooseMap("00");
		for (int i = 0; i < 41 ; i++){
			for (int j = 0; j < 41 ; j ++){
				model.chooseMap("00");
				model.teleportOnline(new Point(i, j));
				model.chooseMap("01");
				model.teleportOnline(new Point(i, j));
				model.chooseMap("02");
				model.teleportOnline(new Point(i, j));
				model.chooseMap("10");
				model.teleportOnline(new Point(i, j));
				model.chooseMap("11");
				model.teleportOnline(new Point(i, j));
				model.chooseMap("12");
				model.teleportOnline(new Point(i, j));
			}

		}
		
		model.isTeleporting();
		model.doneTeleporting();

		model.chooseMap("13");

		model.setEncounteredThisBlock(false);
		assertFalse(model.hasEncounteredThisBlock());
		
		model.setLocation(1, 1);
		model.setLocation(1, 1);
		assertTrue(model.getCurLocation().equals(model.getPrevLocation()));
		
		model.chooseMap("00");
		model.setLocation(5, 12);
		for (int i = 0; i < 100; i ++){
			model.pokemonEncounter();
		}
		Trainer trainer = new Trainer("gg");
		model.setTrainer(trainer);
		model.getTrainer().catchPokemon(new Mew("hi"));
		model.calculateCurCaughtChance(new Mew("hi"));
		model.calculateCurRunChance(new Mew("hi"));
		for (int i = 0; i < 100; i ++){
			model.checkIfCaughtPokemon(new Mew("hi"));
			model.checkIfRunPokemon(new Mew("hi"));
		}
		
		model.createTrainer("hi");
		Trainer ttt = model.getTrainer();
		WildPokemonGenerator gen = new WildPokemonGenerator();
		
		for (int l = 0; l < 50; l ++){
			Pokemon p = gen.generatePokemon();
			for (int i = 0; i < 12; i ++){
				ttt.addItem(ItemType.ROCK);
				ttt.useItem(0, p);
				model.checkIfRunPokemon(p);
			}
		}
		
		
		model.chooseMap("10");
		model.setLocation(30, 9);
		
		
		for (int i = 0; i < 8000; i ++){
			model.generateLoot(0.5);
			if (Math.random() < 0.25){
				model.moveTrainer(Direction.WEST);
			}
			else if (Math.random() > 0.75){
				model.moveTrainer(Direction.EAST);
			}
			else if (Math.random() > 0.5 && Math.random() < 0.75){
				model.moveTrainer(Direction.NORTH);
			}
			else{
				model.moveTrainer(Direction.SOUTH);
			}
		}

		model.getCurMap().printMapInSymbol();
		assertTrue(model.getCurMap().getMapSize_X() == 42);
		assertTrue(model.getCurMap().getMapSize_Y() == 42);
	}
}
