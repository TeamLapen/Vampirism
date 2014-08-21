package de.teamlapen.vampirism.util;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import de.teamlapen.vampirism.block.BlockBloodAltar;

public class ModBlocks {

public static Block bloodAltar = new BlockBloodAltar(Material.rock);
	
	public static void init() {
		GameRegistry.registerBlock(bloodAltar, "bloodAltar");
	}
}
