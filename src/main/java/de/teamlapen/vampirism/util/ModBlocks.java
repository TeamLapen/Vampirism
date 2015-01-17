package de.teamlapen.vampirism.util;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLiquid;
import cpw.mods.fml.common.registry.GameRegistry;
import de.teamlapen.vampirism.block.BlockBloodAltar;
import de.teamlapen.vampirism.block.BlockBloodAltarTier2;
import de.teamlapen.vampirism.block.BasicBlockContainer;
import de.teamlapen.vampirism.block.MaterialLiquidBlood;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar;

public class ModBlocks {

	public final static BlockBloodAltar bloodAltar = new BlockBloodAltar();
	public final static MaterialLiquid blood = new MaterialLiquidBlood(MapColor.netherrackColor);
	public final static BlockBloodAltarTier2 bloodAltarTier2 = new BlockBloodAltarTier2();

	public static void init() {
		GameRegistry.registerBlock(bloodAltar, BlockBloodAltar.name);
		GameRegistry.registerBlock(bloodAltarTier2, BlockBloodAltarTier2.name);
		GameRegistry.registerTileEntity(TileEntityBloodAltar.class, "TileEntityBloodAltar");
	}
}
