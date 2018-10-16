package Pokemon;

public class MewTwo extends Pokemon{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2419893703804480709L;

	public MewTwo(String name) {
		super(name, Pokedex.MewTwo);
	}

	@Override
	public String getIntro() {
		// TODO Auto-generated method stub
		return "Mewtwo is a Pok¨¦mon created by science. It is a bipedal, humanoid creature with some feline features. It is primarily gray with a long, purple tail. On top of its head are two short, blunt horns, and it has purple eyes.";
	}

}
