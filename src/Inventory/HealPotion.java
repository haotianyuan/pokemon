package Inventory;

import Pokemon.Pokemon;

public class HealPotion extends Item{

	private static final long serialVersionUID = -6731421041395451866L;
	
	private int restoreAmount;	// store the amount of health restored for the pokemon
	
	public HealPotion(int amount, String name, ItemType type){
		super(name, type);
		this.restoreAmount = amount;
	}
	
	@Override
	public String getInfo() {
		return "This healing potion will recover " + restoreAmount + " for the pokemon. It can be only used during battle.";
	}

	@Override
	public boolean useItem(Object object) {
		if (checkItemUsable(object)){
			this.decrement(1);
			((Pokemon) object).incrementHP(this.restoreAmount);		
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
		return "COMFORTABLE";
	}


}
