package de.teamlapen.vampirism;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import de.teamlapen.vampirism.block.BlockBloodAltar;
import de.teamlapen.vampirism.block.BlockBloodAltarTier2;
import de.teamlapen.vampirism.block.BlockBloodAltarTier3;
import de.teamlapen.vampirism.block.BlockBloodAltarTier4;
import de.teamlapen.vampirism.block.BlockBloodAltarTier4Bed;
import de.teamlapen.vampirism.block.BlockBloodAltarTier4Tip;
import de.teamlapen.vampirism.block.BlockChurchAltar;
import de.teamlapen.vampirism.block.BlockCursedEarth;
import de.teamlapen.vampirism.block.MaterialLiquidBlood;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltarTier2;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltarTier3;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltarTier4;

public class ModBlocks {

	public static void init() {
		GameRegistry.registerBlock(bloodAltar, BlockBloodAltar.name);
		GameRegistry.registerBlock(bloodAltarTier2, ItemBlock.class, BlockBloodAltarTier2.name);
		GameRegistry.registerBlock(bloodAltarTier3, ItemBlock.class,BlockBloodAltarTier3.name);
		GameRegistry.registerBlock(bloodAltarTier4, ItemBlock.class,BlockBloodAltarTier4.name);
		GameRegistry.registerBlock(bloodAltarTier4Tip, ItemBlock.class,BlockBloodAltarTier4Tip.name);
		GameRegistry.registerBlock(cursedEarth, BlockCursedEarth.name);
		GameRegistry.registerBlock(churchAltar, BlockChurchAltar.name);
		GameRegistry.registerTileEntity(TileEntityBloodAltar.class, "TileEntityBloodAltar");
		GameRegistry.registerTileEntity(TileEntityBloodAltarTier2.class, "TileEntityBloodAltarTier2");
		GameRegistry.registerTileEntity(TileEntityBloodAltarTier3.class, "TileEntityBloodAltarTier3");
		GameRegistry.registerTileEntity(TileEntityBloodAltarTier4.class, "TileEntityBloodAltarTier4");

		GameRegistry.addRecipe(new ItemStack(bloodAltarTier2, 1), " X ", "XYX", "ZZZ", 'X', Blocks.glass, 'Y', Items.gold_ingot, 'Z',
				Items.iron_ingot);
	}
	public final static BlockBloodAltar bloodAltar = new BlockBloodAltar();
	public final static MaterialLiquid blood = new MaterialLiquidBlood(MapColor.netherrackColor);

	public final static BlockBloodAltarTier2 bloodAltarTier2 = new BlockBloodAltarTier2();
	public final static BlockBloodAltarTier3 bloodAltarTier3= new BlockBloodAltarTier3();
	public final static BlockBloodAltarTier4 bloodAltarTier4=new BlockBloodAltarTier4();
	public final static BlockBloodAltarTier4Tip bloodAltarTier4Tip=new BlockBloodAltarTier4Tip();
	public final static BlockCursedEarth cursedEarth = new BlockCursedEarth();
	public final static BlockChurchAltar churchAltar= new BlockChurchAltar();
}
