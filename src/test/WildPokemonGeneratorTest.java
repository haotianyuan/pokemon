package test;

import static org.junit.Assert.*;

import org.junit.Test;

import Pokemon.*;

public class WildPokemonGeneratorTest {

	@Test
	public void test() {
		WildPokemonGenerator pGenerator = new WildPokemonGenerator();
		
		
		//////////////// Common ////////////////
		System.out.println("Common:");
		for (int i = 0; i < 10; i++){
			System.out.println( "   " + i + " " + ((Pokemon) pGenerator.getCommon()).getClass().getName());
		}
		System.out.println();
		
		//////////////// Uncommon ////////////////
		System.out.println("Uncommon:");
		for (int i = 0; i < 10; i++){
			System.out.println( "   " + i + " " + ((Pokemon) pGenerator.getUncommon()).getClass().getName());
		}
		System.out.println();
		
		//////////////// Rare ////////////////
		System.out.println("Rare:");
		for (int i = 0; i < 5; i++){
			System.out.println( "   " + i + " " + ((Pokemon) pGenerator.getRare()).getClass().getName());
		}
		System.out.println();
		
		//////////////// Epic ////////////////
		System.out.println("Epic:");
		for (int i = 0; i < 5; i++){
			System.out.println( "   " + i + " " + ((Pokemon) pGenerator.getEpic()).getClass().getName());
		}
		System.out.println();
		
		//////////////// Legend ////////////////
		System.out.println("Legend:");
		for (int i = 0; i < 5; i++){
			System.out.println( "   " + i + " " + ((Pokemon) pGenerator.getLegend()).getClass().getName());
		}
		
		
		
		///////////// Random /////////////////
		System.out.println("Random:");
		for (int i = 0; i < 1000; i++){
			Pokemon p = pGenerator.generatePokemon();
			System.out.println( "   " + i + ". " + p.getSpecy().getName());
			System.out.println(p.getIntro());
			if (p.getSpecy() == Pokedex.MewTwo){
				System.out.println("------> I FIND IT <------");
			}
		}
	}

}
