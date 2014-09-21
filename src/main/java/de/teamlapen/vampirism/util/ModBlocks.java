package de.teamlapen.vampirism.util;

import cpw.mods.fml.common.registry.GameRegistry;
import de.teamlapen.vampirism.block.BlockBloodAltar;
import de.teamlapen.vampirism.block.BlockContainerVampirism;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar;

public class ModBlocks {

	public final static BlockContainerVampirism bloodAltar = new BlockBloodAltar();

	public static void init() {
		GameRegistry.registerBlock(bloodAltar, "bloodAltar");
		GameRegistry.registerTileEntity(TileEntityBloodAltar.class, "TileEntityBloodAltar");
	}
}
