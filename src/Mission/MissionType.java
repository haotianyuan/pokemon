package Mission;

public enum MissionType {
	FINDLENGEND(Difficulty.HELL), 
	STANDARDLADDER(Difficulty.CASUAL), 
	TWENTYPOKEMON(Difficulty.EASY), 
	THIRTYPOKEMON(Difficulty.NORMAL), 
	FIFTYPOKEMON(Difficulty.HARD), 
	FIVEEPIC(Difficulty.VERYHARD),
	TEST(Difficulty.TEST);
	
	private final Difficulty difficulty;
	
	private MissionType(Difficulty difficulty){
		this.difficulty = difficulty;
	}
}
