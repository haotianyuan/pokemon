package Pokemon;

public class Growlithe extends Pokemon{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6279213918594812911L;

	public Growlithe(String name) {
		super(name, Pokedex.Growlithe);
	}

	@Override
	public String getIntro() {
		// TODO Auto-generated method stub
		return "Growlithe is a quadruped, canine Pok¨¦mon. It has orange fur with black stripes along its back and legs. The fur on its muzzle, chest, belly, and tail is cream-colored, as well as an additional tuft of fur on top of its head.";
	}

}
