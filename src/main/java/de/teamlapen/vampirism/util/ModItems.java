package de.teamlapen.vampirism.util;

import net.minecraft.item.ItemSword;
import cpw.mods.fml.common.registry.GameRegistry;
import de.teamlapen.vampirism.item.ItemVampiresFear;

public class ModItems {

	public static ItemSword vampiresFear = new ItemVampiresFear();

	public static void init() {
		GameRegistry.registerItem(vampiresFear, ItemVampiresFear.name);
	}

}
