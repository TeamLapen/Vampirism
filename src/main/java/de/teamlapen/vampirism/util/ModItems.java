package de.teamlapen.vampirism.util;

import cpw.mods.fml.common.registry.GameRegistry;
import item.ItemVampiresFear;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;

public class ModItems {
	
	public static ItemSword vampiresFear = new ItemVampiresFear(Item.ToolMaterial.EMERALD);
	
	public static void init() {
		GameRegistry.registerItem(vampiresFear, REFERENCE.vampiresFearName);
	}

}
