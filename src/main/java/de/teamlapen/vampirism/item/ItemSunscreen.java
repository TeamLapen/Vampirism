package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.ModPotion;
import net.minecraft.potion.Potion;

public class ItemSunscreen extends BasicItemBloodFood{
	public static final String name="sunscreen";
	
	public ItemSunscreen(){
		super(name,5);
		this.setPotionEffect(ModPotion.sunscreen.id,120, 1, 1F);
	}
	
}