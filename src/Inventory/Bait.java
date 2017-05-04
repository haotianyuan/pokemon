package Inventory;

import Pokemon.Pokemon;

public class Bait extends Item{

	private static final long serialVersionUID = 5729623957892624516L;
	
	private double reducedCaptureRate = -0.05;
	private double reducedRunChance = 0.05;
	
	// extra
	//private int extendTurn;

	// constructor
	public Bait(){
		super("Bait", ItemType.BAIT);
	}

	@Override
	public String getInfo() {
		return "This is a bait which will reduce the run chance of the pokemon by: " + reducedRunChance + ", but will slightly decrease the capture chance.";
	}

	@Override
	public boolean useItem(Object object) {
		if (checkItemUsable(object)){
			((Pokemon) object).decrementAlteredCapRate(reducedCaptureRate);
			((Pokemon) object).decrementAlteredRunChance(reducedRunChance);;
			this.decrement(1);
			// take effect on the pokemon
			return true;
		}
		else{
			System.out.println("You cannot use this item out of battle");
			return false;
		}
	}

	@Override
	public boolean checkItemUsable(Object object) {
		if (Pokemon.class.isInstance(object)){
			return true;
		}
		else{
			System.out.println("You cannot use this item during a battle");
			return false;
		}
	}

	@Override
	public String getEffectMessage() {
		return "FOCUS AND STEADY";
	}

}