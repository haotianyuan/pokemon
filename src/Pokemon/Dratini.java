package Pokemon;

public class Dratini extends Pokemon{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6875427829942238618L;

	public Dratini(String name) {
		super(name, Pokedex.Dratini);
	}

	@Override
	public String getIntro() {
		// TODO Auto-generated method stub
		return "Dratini is a serpentine Pok¨¦mon with a blue body and a white underside. It has white, three-pronged fins on the sides of its head and a white bump on its forehead. Above its large, round, white snout are oval, purple eyes.";
	}

}
