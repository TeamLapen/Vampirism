package de.teamlapen.vampirism.util;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import cpw.mods.fml.common.registry.GameRegistry;
import de.teamlapen.vampirism.item.ItemBloodAltarTier2;
import de.teamlapen.vampirism.item.ItemBloodBottle;
import de.teamlapen.vampirism.item.ItemVampiresFear;

public class ModItems {

	public static ItemSword vampiresFear = new ItemVampiresFear();
	public static ItemBloodBottle bloodBottle = new ItemBloodBottle();
	public static ItemBloodAltarTier2 bloodAltarTier2Item=new ItemBloodAltarTier2();

	public static void init() {
		GameRegistry.registerItem(vampiresFear, ItemVampiresFear.name);
		GameRegistry.registerItem(bloodBottle, ItemBloodBottle.name);
		GameRegistry.registerItem(bloodAltarTier2Item, ItemBloodAltarTier2.name);
		
		// Recipe for empty blood bottle
		GameRegistry.addRecipe(new ItemStack(bloodBottle, 1, 0), 
			"   ",
			"XYX",
			" X ",
			'X', Blocks.glass, 'Y', Items.rotten_flesh);
	}

}
