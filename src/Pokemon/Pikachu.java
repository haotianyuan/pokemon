package Pokemon;

public class Pikachu extends Pokemon{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5071371292364131993L;

	public Pikachu(String name) {
		super(name, Pokedex.Pikachu);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getIntro() {
		// TODO Auto-generated method stub
		return "Pikachu is a short, chubby rodent Pok¨¦mon. It is covered in yellow fur with two horizontal brown stripes on its back. It has a small mouth, long, pointed ears with black tips, brown eyes, and two red circles on its cheeks. There are pouches inside its cheeks where it stores electricity.";
	}

}
