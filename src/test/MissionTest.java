package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import Mission.*;
import Pokemon.Ekans;
import Pokemon.WildPokemonGenerator;
import Trainer.Trainer;

public class MissionTest {

	@Test
	public void test() {
		Trainer trn = new Trainer("aaa");
		ArrayList<Mission> arr = new ArrayList<Mission>();
		Mission a = new Mission(MissionType.FIFTYPOKEMON);
		Mission b = new Mission(MissionType.FINDLENGEND);
		Mission c = new Mission(MissionType.FIVEEPIC);
		Mission d = new Mission(MissionType.STANDARDLADDER);
		Mission e = new Mission(MissionType.TEST);
		Mission f = new Mission(MissionType.THIRTYPOKEMON);
		Mission g = new Mission(MissionType.TWENTYPOKEMON);
		a.setTrainer(trn);
		b.setTrainer(trn);
		c.setTrainer(trn);
		d.setTrainer(trn);
		e.setTrainer(trn);
		f.setTrainer(trn);
		g.setTrainer(trn);
		arr.add(a);
		arr.add(b);
		arr.add(c);
		arr.add(d);
		arr.add(e);
		arr.add(f);
		arr.add(g);
		
		for (Mission x : arr){
			for (int i = 0; i <= x.getRowCount(); i ++){
				for (int j = 0; j <= x.getColumnCount(); j ++){
					System.out.println(x.getColumnName(i));
					System.out.println(x.getInitBall());
					System.out.println(x.getLegendRequirement());
					System.out.println(x.getRareRequirement());
					System.out.println(x.getTotalRequirement());
					System.out.println(x.getStepCap());
					System.out.println(x.getValueAt(i, j));
					System.out.println(x.getColumnClass(j));
					System.out.println(x.isCellEditable(i, j));
					System.out.println(x.getMissionType());
				}
			}
		}
		
		WildPokemonGenerator gen = new WildPokemonGenerator();
		
		for (int i = 0; i < 501; i ++){
			trn.incrementStep(1);
			for (int j = 0; j < 10; j ++){
				trn.catchPokemon(gen.generatePokemon());
				a.checkMissionComplete(trn);
				a.checkMissionFailed(trn);
				b.checkMissionComplete(trn);
				b.checkMissionFailed(trn);
				c.checkMissionComplete(trn);
				c.checkMissionFailed(trn);
				d.checkMissionComplete(trn);
				d.checkMissionFailed(trn);
				e.checkMissionComplete(trn);
				e.checkMissionFailed(trn);
				f.checkMissionComplete(trn);
				f.checkMissionFailed(trn);
				g.checkMissionComplete(trn);
				g.checkMissionFailed(trn);
			}
		}
		
		
	}

}
