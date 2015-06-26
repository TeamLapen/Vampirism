package de.teamlapen.vampirism.item;

public class ItemHumanHeart extends BasicItemBloodFood {

	public static String name = "humanHeart";
	private static final int bloodAmount = 20;

	public ItemHumanHeart() {
		super(name, bloodAmount);
	}
}
