package Inventory;

import Pokemon.Pokemon;
import Trainer.Trainer;

public class Bait extends Item{

	private static final long serialVersionUID = 5729623957892624516L;
	
	private double reducedCaptureRate;
	private double reducedRunChance;
	
	// extra
	private int extendTurn;

	// constructor
	public Bait(){
		super("Bait", ItemType.BAIT);
	}

	@Override
	public String getInfo() {
		return "This is a bait";
	}

	@Override
	public boolean useItem(Object object) {
		if (checkItemUsable(object)){
			this.decrement(1);
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
}