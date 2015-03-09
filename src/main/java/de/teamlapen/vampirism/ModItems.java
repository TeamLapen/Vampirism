package de.teamlapen.vampirism;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import cpw.mods.fml.common.registry.GameRegistry;
import de.teamlapen.vampirism.item.ItemAltarTier4Bed;
import de.teamlapen.vampirism.item.ItemBloodBottle;
import de.teamlapen.vampirism.item.ItemHumanHearth;
import de.teamlapen.vampirism.item.ItemPitchfork;
import de.teamlapen.vampirism.item.ItemPureBlood;
import de.teamlapen.vampirism.item.ItemTorch;
import de.teamlapen.vampirism.item.ItemVampireFang;
import de.teamlapen.vampirism.item.ItemVampiresFear;

public class ModItems {

	public static void init() {
		GameRegistry.registerItem(vampiresFear, ItemVampiresFear.name);
		GameRegistry.registerItem(bloodBottle, ItemBloodBottle.name);
		GameRegistry.registerItem(vampireFang, ItemVampireFang.NAME);
		GameRegistry.registerItem(pitchfork, ItemPitchfork.name);
		GameRegistry.registerItem(torch, ItemTorch.name);
		GameRegistry.registerItem(humanHearth, ItemHumanHearth.name);
		GameRegistry.registerItem(pureBlood,ItemPureBlood.name);

		// Recipe for empty blood bottle
		GameRegistry.addRecipe(new ItemStack(bloodBottle, 1, 0), "   ", "XYX", " X ", 'X', Blocks.glass, 'Y', Items.rotten_flesh);
		GameRegistry.addRecipe(new ItemStack(vampiresFear, 1), "XYX", "XYX", " Z ", 'X', vampireFang, 'Y', Items.iron_ingot, 'Z', Items.stick);
	}
	public static ItemSword vampiresFear = new ItemVampiresFear();
	public static ItemBloodBottle bloodBottle = new ItemBloodBottle();

	public static ItemVampireFang vampireFang = new ItemVampireFang();
	public static ItemPitchfork pitchfork=new ItemPitchfork();
	public static ItemTorch torch = new ItemTorch();
	public static ItemHumanHearth humanHearth=new ItemHumanHearth();
	public static ItemPureBlood pureBlood=new ItemPureBlood();
}
