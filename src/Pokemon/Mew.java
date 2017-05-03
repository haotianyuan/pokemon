package Pokemon;

public class Mew extends Pokemon{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -966333615015409371L;

	public Mew(String name) {
		super(name, Pokedex.Mew);
	}

	@Override
	public String getIntro() {
		// TODO Auto-generated method stub
		return "Mew is a pink, bipedal Pok¨¦mon with mammalian features. Its snout is short and wide, and it has triangular ears and large, blue eyes. It has short arms with three-fingered paws, large hind legs and feet with oval markings on the soles, and a long, thin tail ending in an ovoid tip.";
	}
	
}
