package Pokemon;

public class Weedle extends Pokemon{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Weedle(String name) {
		super(name, Pokedex.Weedle);
	}

	@Override
	public String getIntro() {
		// TODO Auto-generated method stub
		return "Weedle is a small larva Pok¨¦mon with a segmented body ranging in color from yellow to reddish-brown. Combined with its red nose and feet, Weedle's bright coloration wards off its enemies. ";
	}

}
