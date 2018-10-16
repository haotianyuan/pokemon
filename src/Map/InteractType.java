package Map;

public enum InteractType {
	SHORTGRASS(20), 
	NONE(0), 
	SAND(50), 
	PORTAL(0),
	SWAMP(50);
	
	private final double basicEncounterRate;	// rate that user encounter pokemon upon these interact type
	
	private InteractType(double encounterRate){
		basicEncounterRate = encounterRate * 0.01;
	}
	
	public double getBasicEncounterRate(){
		return this.basicEncounterRate;
	}
}
