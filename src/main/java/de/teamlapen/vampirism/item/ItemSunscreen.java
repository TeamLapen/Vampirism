package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.ModPotion;

public class ItemSunscreen extends BasicItemBloodFood {
	public static final String name = "sunscreen";

	public ItemSunscreen() {
		super(name, 5);
		this.setPotionEffect(ModPotion.sunscreen.id, 120, 0, 1F);
	}

}