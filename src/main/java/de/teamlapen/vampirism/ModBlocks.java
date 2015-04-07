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
import de.teamlapen.vampirism.block.BlockBloodAltarTier4Tip;
import de.teamlapen.vampirism.block.BlockChurchAltar;
import de.teamlapen.vampirism.block.BlockCoffin;
import de.teamlapen.vampirism.block.BlockCursedEarth;
import de.teamlapen.vampirism.block.MaterialLiquidBlood;
import de.teamlapen.vampirism.block.BlockBloodAltarTier4Tip.TileEntityBloodAltarTier4Tip;
import de.teamlapen.vampirism.block.BlockChurchAltar.TileEntityChurchAltar;
import de.teamlapen.vampirism.block.BlockFlower.VampireFlower;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltarTier2;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltarTier3;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltarTier4;
import de.teamlapen.vampirism.tileEntity.TileEntityCoffin;

public class ModBlocks {

	public static void init() {
		GameRegistry.registerBlock(bloodAltar, BlockBloodAltar.name);
		GameRegistry.registerBlock(bloodAltarTier2, ItemBlock.class, BlockBloodAltarTier2.name);
		//GameRegistry.registerBlock(bloodAltarTier3, ItemBlock.class,BlockBloodAltarTier3.name);
		GameRegistry.registerBlock(bloodAltarTier4, ItemBlock.class,BlockBloodAltarTier4.name);
		GameRegistry.registerBlock(bloodAltarTier4Tip, ItemBlock.class,BlockBloodAltarTier4Tip.name);
		GameRegistry.registerBlock(cursedEarth, BlockCursedEarth.name);
		GameRegistry.registerBlock(churchAltar, BlockChurchAltar.name);
		GameRegistry.registerBlock(coffin, BlockCoffin.name);
		GameRegistry.registerTileEntity(TileEntityBloodAltar.class, "TileEntityBloodAltar");
		GameRegistry.registerTileEntity(TileEntityBloodAltarTier2.class, "TileEntityBloodAltarTier2");
		//GameRegistry.registerTileEntity(TileEntityBloodAltarTier3.class, "TileEntityBloodAltarTier3");
		GameRegistry.registerTileEntity(TileEntityBloodAltarTier4.class, "TileEntityBloodAltarTier4");
		GameRegistry.registerTileEntity(TileEntityChurchAltar.class, "TileEntityChurchAltar");
		GameRegistry.registerTileEntity(TileEntityBloodAltarTier4Tip.class, "TileEntityBloodAltarTier4Tip");
		GameRegistry.registerTileEntity(TileEntityCoffin.class, "TileEntityCoffin");

		// Flowers
		GameRegistry.registerBlock(vampireFlower, VampireFlower.name);
		

	}
	
	public static void registerRecipes(){
		GameRegistry.addRecipe(new ItemStack(bloodAltarTier2, 1), " X ", "XYX", "ZZZ", 'X', Blocks.glass, 'Y', Items.gold_ingot, 'Z',
				Items.iron_ingot);
		GameRegistry.addRecipe(new ItemStack(bloodAltarTier4,1), "   ","YZY","ZZZ",'Y',Items.gold_ingot,'Z',Blocks.obsidian);
		GameRegistry.addRecipe(new ItemStack(bloodAltarTier4Tip,1),"   "," X ","XYX",'X',Items.iron_ingot,'Y',Blocks.iron_block);
	}
	public final static BlockBloodAltar bloodAltar = new BlockBloodAltar();
	public final static MaterialLiquid blood = new MaterialLiquidBlood(MapColor.netherrackColor);

	public final static BlockBloodAltarTier2 bloodAltarTier2 = new BlockBloodAltarTier2();
	public final static BlockBloodAltarTier3 bloodAltarTier3= new BlockBloodAltarTier3();
	public final static BlockBloodAltarTier4 bloodAltarTier4=new BlockBloodAltarTier4();
	public final static BlockBloodAltarTier4Tip bloodAltarTier4Tip=new BlockBloodAltarTier4Tip();
	public final static BlockCursedEarth cursedEarth = new BlockCursedEarth();
	public final static BlockChurchAltar churchAltar= new BlockChurchAltar();
	public final static BlockCoffin coffin = new BlockCoffin();
	
	// Flowers
	public final static VampireFlower vampireFlower = new VampireFlower();
}
