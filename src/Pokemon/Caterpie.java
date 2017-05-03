package Pokemon;

public class Caterpie extends Pokemon{

	/**
	 * 
	 */
	private static final long serialVersionUID = 978119450393382099L;

	public Caterpie(String name) {
		super(name, Pokedex.Caterpie);
	}

	@Override
	public String getIntro() {
		// TODO Auto-generated method stub
		return "Caterpie is a Pok¨¦mon that resembles a green caterpillar. There are yellow ring-shaped markings down the sides of its body, which resemble its large yellow eyes. Its most notable characteristic is the bright red antenna (osmeterium) on its head, which releases a stench to repel predators.";
	}

}
