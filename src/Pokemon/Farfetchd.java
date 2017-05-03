package Pokemon;

public class Farfetchd extends Pokemon {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6942747649208102743L;

	public Farfetchd(String name) {
		super(name, Pokedex.Farfetchd);
	}

	@Override
	public String getIntro() {
		// TODO Auto-generated method stub
		return "Farfetch'd resembles a brown duck with a white underbelly. It has a yellow beak and a V-shaped, black marking on its forehead. It has wings as big as its body, which appear to be prehensile enough to substitute for hands. It has yellow legs with webbed feet.";
	}

}
