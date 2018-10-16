package Pokemon;

public class Rattata extends Pokemon{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1295066162093073285L;

	public Rattata(String name) {
		super(name, Pokedex.Rattata);
	}

	@Override
	public String getIntro() {
		// TODO Auto-generated method stub
		return "Rattata is a small, quadruped rodent Pok¨¦mon. Its most notable feature is its large teeth. Like most rodents, its teeth grow continuously throughout its life and must be worn down by gnawing. Rattata has purple fur on its back, and cream fur on its stomach.";
	}

}
