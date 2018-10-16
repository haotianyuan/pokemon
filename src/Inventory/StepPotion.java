package Inventory;

import Pokemon.Pokemon;
import Trainer.Trainer;

public class StepPotion extends Item{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6305062532400306948L;
	private int restoreAmount;
	
	public StepPotion(int amount, String name, ItemType type){
		super(name, type);
		this.restoreAmount = amount;
	}
	
	@Override
	public String getInfo() {
		return "This step potion will recharge " + this.restoreAmount + " step count for the user";
	}

	@Override
	public boolean useItem(Object object) {
		if (checkItemUsable(object)){
			this.decrement(1);
			((Trainer) object).decrementStep(this.restoreAmount);
			return true;
		}
		else{
			return false;
		}
	}
	
	@Override
	public boolean checkItemUsable(Object object) {
		if (Trainer.class.isInstance(object)){
			return true;
		}
		else{
			System.out.println("You cannot use this item on pokemon");
			return false;
		}
	}

	@Override
	public String getEffectMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
