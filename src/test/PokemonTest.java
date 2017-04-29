package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


import Pokemon.Abra;
import Pokemon.Caterpie;
import Pokemon.Dratini;
import Pokemon.Ekans;
import Pokemon.Farfetchd;
import Pokemon.Growlithe;
import Pokemon.Mew;
import Pokemon.MewTwo;
import Pokemon.Pidgey;
import Pokemon.Pokedex;
import Pokemon.PokemonCollection;
import Pokemon.PokemonQuality;
import Pokemon.Rattata;
import Pokemon.Spearow;
import Pokemon.Weedle;


public class PokemonTest {
	
	PokemonCollection collection = new PokemonCollection();
	
	Caterpie caterpie = new Caterpie("caterpie");
	Abra abra = new Abra("abra");
	Dratini dratini = new Dratini("dratini");
	Ekans ekans = new Ekans("ekans");
	Farfetchd farfetchd = new Farfetchd("farfetchd");
	Growlithe growlithe = new Growlithe("growlithe");
	Mew mew = new Mew("mew");
	MewTwo mewtwo = new MewTwo("mewtwo");
	Pidgey pidgey = new Pidgey("pidgey");
	Rattata rattata = new Rattata("rattata");
	Spearow spearow = new Spearow("spearow");
	Weedle weedle = new Weedle("weedle");

	
	@Test

	public void testPokemonCollectionandPokemon() {
		collection.addPokemon(caterpie);
		collection.addPokemon(abra);
		collection.addPokemon(dratini);
		collection.addPokemon(ekans);
		collection.addPokemon(farfetchd);
		collection.addPokemon(growlithe);
		collection.addPokemon(mew);
		collection.addPokemon(mewtwo);
		collection.addPokemon(pidgey);
		collection.addPokemon(rattata);
		collection.addPokemon(spearow);
		collection.addPokemon(weedle);
		
		
		assertTrue(caterpie.getBasicRunChance() < 1);
		assertTrue(caterpie.getBasicCapRate() < 1);
		assertTrue(caterpie.getCapHpLimit() <100);
		assertTrue(caterpie.getCapTurn() <20);
		assertTrue(caterpie.getCurHP() < 1000);
		assertTrue(caterpie.getMaxHP() <2000);
		assertTrue(caterpie.getBasicMaxTurn() < 50);
		caterpie.setName("mike");
		
		caterpie.incrementHP(10);
		caterpie.decrementHP(10);
		caterpie.recordCapTurn(10);
		assertTrue(caterpie.getBasicRunChance() < 1);
		assertTrue(caterpie.getBasicCapRate() < 1);
		assertTrue(caterpie.getCapHpLimit() <100);
		assertTrue(caterpie.getCapTurn() <20);
		assertTrue(caterpie.getCurHP() < 1000);
		assertTrue(caterpie.getMaxHP() <2000);
		assertTrue(caterpie.getBasicMaxTurn() < 50);		

		System.out.println(caterpie.recordMetDate());
		assertEquals(caterpie.getSpecy(), Pokedex.Caterpie);
		assertEquals(caterpie.getQuality(), PokemonQuality.COMMON);
		assertEquals(caterpie.getNickName(), "mike");
		assertEquals(caterpie.getName(), "caterpie");
		
		for (int i = 0 ;i < 20; i ++){
			caterpie.decrementHP(10);
			/*
			caterpie.decrementCapRate(10.0);
			caterpie.decrementRunChance(10.0);
			caterpie.decrementMaxTurn(10);
			*/
		}

		
		PokemonCollection a=new PokemonCollection();
		a.addPokemon(mew);
		a.addTableModelListener(null);
		a.getColumnClass(0);
		a.getColumnClass(1);
		a.getColumnClass(2);
		a.getColumnClass(3);
		assertEquals(4,a.getColumnCount());
		assertEquals("Pokedex Index",a.getColumnName(0));
		assertEquals("Type",a.getColumnName(1));
		assertEquals("Nickname",a.getColumnName(2));
		assertEquals("Captured Time",a.getColumnName(3));
		System.out.println(a.getRowCount());
		System.out.println(a.getValueAt(0, 0));
		System.out.println(a.getValueAt(0, 1));
		System.out.println(a.getValueAt(0, 2));
		System.out.println(a.getValueAt(0, 3));
		assertFalse(a.isCellEditable(0, 1));
		assertEquals(null,a.getColumnName(5));
		assertEquals(null,a.getColumnName(9));
		assertEquals(null,a.getColumnClass(99));


}
}