package Inventory;

import Pokemon.Pokemon;
import Trainer.Trainer;

public class SafariBall extends Item{
	
	private static final long serialVersionUID = -6527050440324754144L;

	// constructor
	public SafariBall(){
		super("Safari Ball", ItemType.BALL);
	}

	@Override
	public String getInfo() {
		return "This is a regular safari ball";
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

	@Override
	public String getUsageMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
