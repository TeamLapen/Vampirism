package de.teamlapen.vampirism.util;

import cpw.mods.fml.common.registry.GameRegistry;
import de.teamlapen.vampirism.block.BlockBloodAltar;
import de.teamlapen.vampirism.block.BlockVampirism;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar;

public class ModBlocks {

	public final static BlockVampirism bloodAltar = new BlockBloodAltar();

	public static void init() {
		GameRegistry.registerBlock(bloodAltar, "bloodAltar");
		GameRegistry.registerTileEntity(TileEntityBloodAltar.class, REFERENCE.TE_BLOODALTAR_NBT_KEY);
	}
}
