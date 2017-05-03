package Pokemon;

public class Ekans extends Pokemon{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4223725585746816014L;

	public Ekans(String name) {
		super(name, Pokedex.Ekans);
	}

	@Override
	public String getIntro() {
		// TODO Auto-generated method stub
		return "Ekans is a purple, serpentine Pok¨¦mon. Its eyes, underbelly, the thick stripe around its neck, and rattle are yellow. Ekans has three pairs of black lines encircling its body, as well as another line that connects each slitted eye and curves toward its nose.";
	}

}
