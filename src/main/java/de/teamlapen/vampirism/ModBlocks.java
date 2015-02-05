package de.teamlapen.vampirism;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import de.teamlapen.vampirism.block.BlockBloodAltar;
import de.teamlapen.vampirism.block.BlockBloodAltarTier2;
import de.teamlapen.vampirism.block.BasicBlockContainer;
import de.teamlapen.vampirism.block.MaterialLiquidBlood;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltarTier2;

public class ModBlocks {

	public final static BlockBloodAltar bloodAltar = new BlockBloodAltar();
	public final static MaterialLiquid blood = new MaterialLiquidBlood(MapColor.netherrackColor);
	public final static BlockBloodAltarTier2 bloodAltarTier2 = new BlockBloodAltarTier2();

	public static void init() {
		GameRegistry.registerBlock(bloodAltar, BlockBloodAltar.name);
		GameRegistry.registerBlock(bloodAltarTier2, ItemBlock.class,BlockBloodAltarTier2.name);
		GameRegistry.registerTileEntity(TileEntityBloodAltar.class, "TileEntityBloodAltar");
		GameRegistry.registerTileEntity(TileEntityBloodAltarTier2.class, "TileEntityBloodAltarTier2");
		
		GameRegistry.addRecipe(new ItemStack(bloodAltarTier2,1), " X ","XYX","ZZZ",'X',Blocks.glass,'Y',Items.gold_ingot,'Z',Items.iron_ingot);
	}
}
