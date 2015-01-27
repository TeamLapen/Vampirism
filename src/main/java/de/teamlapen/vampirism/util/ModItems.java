package de.teamlapen.vampirism.util;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import cpw.mods.fml.common.registry.GameRegistry;
import de.teamlapen.vampirism.item.ItemBloodBottle;
import de.teamlapen.vampirism.item.ItemVampireFang;
import de.teamlapen.vampirism.item.ItemVampiresFear;
import de.teamlapen.vampirism.item.SpawnBloodAltar;
import de.teamlapen.vampirism.item.SpawnBloodAltarTier2;

public class ModItems {

	public static ItemSword vampiresFear = new ItemVampiresFear();
	public static ItemBloodBottle bloodBottle = new ItemBloodBottle();
	public static ItemVampireFang vampireFang = new ItemVampireFang();
	public static SpawnBloodAltar spawnBloodAltar = new SpawnBloodAltar("spawnBloodAltar");
	public static SpawnBloodAltarTier2 spawnBloodAltarTier2 = new SpawnBloodAltarTier2("spawnBloodAltarTier2");

	public static void init() {
		GameRegistry.registerItem(vampiresFear, ItemVampiresFear.name);
		GameRegistry.registerItem(bloodBottle, ItemBloodBottle.name);
		GameRegistry.registerItem(vampireFang, ItemVampireFang.NAME);
		GameRegistry.registerItem(spawnBloodAltar, SpawnBloodAltar.name);
		GameRegistry.registerItem(spawnBloodAltarTier2, SpawnBloodAltarTier2.name);
		
		// Recipe for empty blood bottle
		GameRegistry.addRecipe(new ItemStack(bloodBottle, 1, 0), 
			"   ",
			"XYX",
			" X ",
			'X', Blocks.glass, 'Y', Items.rotten_flesh);
		GameRegistry.addRecipe(new ItemStack(vampiresFear,1), "XYX","XYX"," Z ",'X',vampireFang,'Y',Items.iron_ingot,'Z',Items.stick);
	}
}
