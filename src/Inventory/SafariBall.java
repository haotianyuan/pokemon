package Inventory;

import Pokemon.Pokemon;

public class SafariBall extends Item{
	
	private static final long serialVersionUID = -6527050440324754144L;

	// constructor
	public SafariBall(){
		super("Safari Ball", ItemType.BALL);
	}

	@Override
	public String getInfo() {
		return "A special Ball that is used only in the Safari Zone. It is finished with a camouflage pattern.";
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
	public String getEffectMessage() {
		return "STRUGGLING";
	}

}
