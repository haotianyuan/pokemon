package Inventory;

import Pokemon.Pokemon;
import Trainer.Trainer;

public class CapturePotion extends Item{

	private static final long serialVersionUID = -558710047797124501L;
	
	private double alteredChance;

	public CapturePotion(String name, double alterChance, ItemType type) {
		super(name, type);
	}

	@Override
	public boolean useItem(Object object) {
		if (checkItemUsable(object)){
			this.decrement(1);
			((Trainer) object).incrementBonusCapture(this.alteredChance);
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
			System.out.println("You cannot use this item during a battle");
			return false;
		}
	}

	@Override
	public String getInfo() {
		return "Permanently increase the chance of capture by " + this.alteredChance * 100 + "%";
	}

	@Override
	public String getEffectMessage() {
		return "You drink a Capture Poition";
	}

}
