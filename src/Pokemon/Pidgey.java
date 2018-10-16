package Pokemon;

public class Pidgey extends Pokemon{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6322188861515990725L;

	public Pidgey(String name) {
		super(name, Pokedex.Pidgey);
	}

	@Override
	public String getIntro() {
		// TODO Auto-generated method stub
		return "Pidgey is a small, plump-bodied avian Pok¨¦mon. It is primarily brown with a cream-colored face, underside, and flight feathers. On top of its head is a short crest of three tufts. The center crest feathers are brown and the outer two tufts are cream-colored. Just under its crest are its narrow, brown eyes.";
	}

}
