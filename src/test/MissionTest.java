package test;

import static org.junit.Assert.*;

import org.junit.Test;

import Mission.*;
import Pokemon.Ekans;
import Trainer.Trainer;

public class MissionTest {

	@Test
	public void test() {
		Mission a = new Mission(MissionType.FIFTYPOKEMON);
		Mission b = new Mission(MissionType.FINDLENGEND);
		Mission c = new Mission(MissionType.FIVEEPIC);
		Mission d = new Mission(MissionType.STANDARDLADDER);
		Mission e = new Mission(MissionType.TEST);
		Mission f = new Mission(MissionType.THIRTYPOKEMON);
		Mission g = new Mission(MissionType.TWENTYPOKEMON);
		
		assertTrue(e.getStepCap() == 50);
		assertTrue(a.getMissionType() == MissionType.FIFTYPOKEMON);
		assertTrue(a.getInitBall() == 30);
		assertTrue(a.getRareRequirement() == 0);
		assertTrue(a.getLegendRequirement() == 0);
		assertTrue(a.getTotalRequirement() == 50);
		a.incrementStepCap(10);
		assertTrue(a.getStepCap() == 510);
		a.decrementStepCap(10);
		assertTrue(a.getStepCap() == 500);
		
		Trainer trn = new Trainer("tmt");
		assertFalse(g.checkMissionComplete(trn));
		assertFalse(f.checkMissionComplete(trn));
		assertFalse(e.checkMissionComplete(trn));
		
		assertFalse(e.checkMissionFailed(trn));
		trn.incrementStep(500);
		assertTrue(d.checkMissionComplete(trn));
		assertTrue(f.checkMissionFailed(trn));
		
		for(int i = 0; i < 60; i ++ ){
			trn.catchPokemon(new Ekans("e"));
		}
		assertTrue(g.checkMissionComplete(trn));
		assertTrue(f.checkMissionComplete(trn));
		assertTrue(e.checkMissionComplete(trn));
		
	}

}
