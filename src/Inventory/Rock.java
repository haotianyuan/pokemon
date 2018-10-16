package Inventory;

import Pokemon.Pokemon;

public class Rock extends Item{

	private static final long serialVersionUID = -4794400599819801264L;

	private double reducedRate = 0.08;
	
	// extra
	//private double reducedRunChance;
	


	// constructor
	public Rock(){
		super("Rock", ItemType.ROCK);
	}

	@Override
	public String getInfo() {
		return  "A rock that seems to be really hurt if hit by it.";
	}

	@Override
	public boolean useItem(Object object) {
		if (checkItemUsable(object)){
			int reduced = (int) (((Pokemon) object).getMaxHP() * reducedRate);
			((Pokemon) object).decrementHP(reduced);
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

	@Override
	public String getEffectMessage() {
		return "WEAKENED AND FEAR";
	}

}