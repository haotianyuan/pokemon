package Inventory;

import Pokemon.Pokemon;
import Trainer.Trainer;

public class Rock extends Item{

	private static final long serialVersionUID = -4794400599819801264L;

	private double reducedCurHpAmount;
	
	// extra
	private double reducedRunChance;
	


	// constructor
	public Rock(){
		super("Rock", ItemType.ROCK);
	}

	@Override
	public String getInfo() {
		return "This is a rock";
	}

	@Override
	public boolean useItem(Object object) {
		if (checkItemUsable(object)){
			this.decrement(1);
			return true;
		}
		else{
			return false;
		}
	}
	
	@Override
	public boolean checkItemUsable(Object object) {
		if (Pokemon.class.isInstance(object)){
			return true;
		}
		else{
			System.out.println("You cannot use this item out of battle");
			return false;
		}
	}

}