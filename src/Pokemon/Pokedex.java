/*
 * Author: Mengtao Tang
 * Date: 4/20/2017
 * Course: CSC_335
 * Purpose: This is an enum class that define the basic information
 * 			of all sort of pokemon that will be encountered in the 
 * 			game.
 * 			The defined the data for each pokemon includes:
 * 				name
 * 				index
 * 				quality
 * 				base hp
 * 				TODO: voice path
 * 
 */

package Pokemon;

public enum Pokedex {
	Caterpie("Caterpie", 10, PokemonQuality.COMMON, 100), 
	Weedle("Weedle", 13, PokemonQuality.COMMON, 100), 
	Pidgey("Pidgey", 16, PokemonQuality.COMMON, 100), 
	Rattata("Rattata", 19, PokemonQuality.COMMON, 100), 
	Spearow("Spearow", 21, PokemonQuality.COMMON, 100),
	Ekans("Ekans", 23, PokemonQuality.COMMON, 100), 
	Pikachu("Pikachu", 25, PokemonQuality.UNCOMMON, 100), 
	Growlithe("Growlithe", 58, PokemonQuality.UNCOMMON, 100), 
	Abra("Abra", 63, PokemonQuality.RARE, 100),
	Dratini("Dratini", 147, PokemonQuality.EPIC, 100),
	Farfetchd("Farfetch'd", 83, PokemonQuality.UNCOMMON, 100), 
	MewTwo("MewTwo", 150, PokemonQuality.LEGENDARY, 100),
	Mew("Mew", 151, PokemonQuality.LEGENDARY, 100);
	
	
	private final String name;		// store the name of the pokemon
	private final int pokedexIndex;		// store the index of the pokemon in pokedex
	private final PokemonQuality quality;		// store the quality of the pokemon
	private final int basicHP;		// store the basic HP of this kind
		
	Pokedex(String name, int tag, PokemonQuality q, int hp){
		this.name = name;
		this.pokedexIndex = tag;
		this.quality = q;
		this.basicHP = hp;
	}
		
	public String getName(){
		return this.name;
	}
	
	public int getIndex(){
		return this.pokedexIndex;
	}
	
	public PokemonQuality getQuality(){
		return this.quality;
	}
	
	public int getBasicHP(){
		return this.basicHP;
	}
	
	
}
