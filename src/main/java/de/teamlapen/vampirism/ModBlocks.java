package de.teamlapen.vampirism;

import cpw.mods.fml.common.registry.GameRegistry;
import de.teamlapen.vampirism.block.*;
import de.teamlapen.vampirism.block.BlockBloodAltar4Tip.TileEntityBloodAltar4Tip;
import de.teamlapen.vampirism.block.BlockChurchAltar.TileEntityChurchAltar;
import de.teamlapen.vampirism.item.ItemMetaBlock;
import de.teamlapen.vampirism.tileEntity.*;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ModBlocks {

	public final static BlockBloodAltar1 bloodAltar1 = new BlockBloodAltar1();

	public final static MaterialLiquid blood = new MaterialLiquidBlood(MapColor.netherrackColor);

	public final static BlockBloodAltar2 bloodAltar2 = new BlockBloodAltar2();
	public final static BlockBloodAltar3 bloodAltar3 = new BlockBloodAltar3();

	public final static BlockBloodAltar4 bloodAltar4 = new BlockBloodAltar4();
	public final static BlockBloodAltar4Tip bloodAltar4Tip = new BlockBloodAltar4Tip();
	public final static BlockCursedEarth cursedEarth = new BlockCursedEarth();
	public final static BlockChurchAltar churchAltar = new BlockChurchAltar();
	public final static BlockCoffin coffin = new BlockCoffin();
	public final static BlockTemplateGenerator templateGenerator = new BlockTemplateGenerator();
	public final static BlockCastle castleBlock = new BlockCastle();
	public final static BlockCastleSlab doubleCastleSlab = new BlockCastleSlab(true);
	public final static BlockCastleSlab castleSlab = new BlockCastleSlab(false);
	public final static BlockStairs castleStairsPurple = new BlockCastleStairs(castleBlock,0);
	public final static BlockStairs castleStairsDark = new BlockCastleStairs(castleBlock,1);
	public final static BlockCastlePortal castlePortal = new BlockCastlePortal();
	public final static BlockDraculaButton blockDraculaButton = new BlockDraculaButton();
	public final static BlockMainTent blockMainTent = new BlockMainTent();
	public final static BlockTent blockTent = new BlockTent();
	public final static BlockGildedIron gildedIron = new BlockGildedIron();
	public final static BlockGarlic garlicPlant = new BlockGarlic();
	public final static BlockGarlicGas garlicGasWeak = new BlockGarlicGas(true);
	public final static BlockGarlicGas garlicGasStrong = new BlockGarlicGas(false);
	public final static BlockGarlicTorch garlicTorchWeak = (BlockGarlicTorch) new BlockGarlicTorch(true).setBlockName(REFERENCE.MODID + ".garlicTorchWeak").setBlockTextureName(REFERENCE.MODID + ":" + "garlicTorchWeak");
	public final static BlockGarlicTorch garlicTorchStrong = (BlockGarlicTorch) new BlockGarlicTorch(false).setBlockName(REFERENCE.MODID + ".garlicTorchStrong").setBlockTextureName(REFERENCE.MODID + ":" + "garlicTorchStrong");

	// Flowers
	public final static VampireFlower vampireFlower = new VampireFlower();
	public static void init() {
		GameRegistry.registerBlock(bloodAltar1, BlockBloodAltar1.name);
		GameRegistry.registerBlock(bloodAltar2, ItemBlock.class, BlockBloodAltar2.name);
		// GameRegistry.registerBlock(bloodAltarTier3, ItemBlock.class,BlockBloodAltarTier3.name);
		GameRegistry.registerBlock(bloodAltar4, ItemBlock.class, BlockBloodAltar4.name);
		GameRegistry.registerBlock(bloodAltar4Tip, ItemBlock.class, BlockBloodAltar4Tip.name);
		GameRegistry.registerBlock(cursedEarth, BlockCursedEarth.name);
		GameRegistry.registerBlock(churchAltar, BlockChurchAltar.name);
		GameRegistry.registerBlock(coffin, BlockCoffin.name);
		GameRegistry.registerBlock(castleBlock, ItemMetaBlock.class,BlockCastle.name);
		GameRegistry.registerBlock(castleSlab,null,BlockCastleSlab.name);
		GameRegistry.registerBlock(doubleCastleSlab,null,BlockCastleSlab.doubleName);
		GameRegistry.registerBlock(castleStairsDark,BlockCastleStairs.name+"_dark");
		GameRegistry.registerBlock(castleStairsPurple,BlockCastleStairs.name+"_purple");
		GameRegistry.registerBlock(castlePortal,BlockCastlePortal.name);
		GameRegistry.registerBlock(blockDraculaButton, BlockDraculaButton.name);
		GameRegistry.registerBlock(blockMainTent, BlockMainTent.name);
		GameRegistry.registerBlock(blockTent, BlockTent.name);
		GameRegistry.registerBlock(gildedIron, BlockGildedIron.name);
		GameRegistry.registerBlock(garlicPlant, null, BlockGarlic.name);
		GameRegistry.registerBlock(garlicGasWeak, null, BlockGarlicGas.name + "_weak");
		GameRegistry.registerBlock(garlicGasStrong, null, BlockGarlicGas.name + "_strong");
		GameRegistry.registerBlock(garlicTorchWeak, "garlicTorchWeak");
		GameRegistry.registerBlock(garlicTorchStrong, "garlicTorchStrong");
		GameRegistry.registerTileEntity(TileEntityBloodAltar1.class, "TileEntityBloodAltar");
		GameRegistry.registerTileEntity(TileEntityBloodAltar2.class, "TileEntityBloodAltarTier2");
		// GameRegistry.registerTileEntity(TileEntityBloodAltarTier3.class, "TileEntityBloodAltarTier3");
		GameRegistry.registerTileEntity(TileEntityBloodAltar4.class, "TileEntityBloodAltarTier4");
		GameRegistry.registerTileEntity(TileEntityChurchAltar.class, "TileEntityChurchAltar");
		GameRegistry.registerTileEntity(TileEntityBloodAltar4Tip.class, "TileEntityBloodAltarTier4Tip");
		GameRegistry.registerTileEntity(TileEntityCoffin.class, "TileEntityCoffin");
		GameRegistry.registerTileEntity(TileEntityTent.class, "TileEntityTent");

		if(VampirismMod.inDev){
			GameRegistry.registerBlock(templateGenerator,ItemBlock.class,BlockTemplateGenerator.name);
			GameRegistry.registerTileEntity(TileEntityTemplateGenerator.class, "TileEntityTemplateGenerator");
			templateGenerator.setCreativeTab(VampirismMod.tabVampirism);
		}

		// Flowers
		GameRegistry.registerBlock(vampireFlower, VampireFlower.name);

	}

	public static void registerRecipes() {
		GameRegistry.addRecipe(new ItemStack(bloodAltar2, 1), " X ", "XYX", "ZZZ", 'X', Blocks.glass, 'Y', Items.gold_ingot, 'Z', Items.iron_ingot);
		GameRegistry.addRecipe(new ItemStack(bloodAltar4, 1), "   ", "YZY", "ZZZ", 'Y', Items.gold_ingot, 'Z', Blocks.obsidian);
		GameRegistry.addRecipe(new ItemStack(bloodAltar4Tip, 1), "   ", " X ", "XYX", 'X', Items.iron_ingot, 'Y', Blocks.iron_block);
		GameRegistry.addRecipe(new ItemStack(castleBlock,1,0),"XXX","XYX","XXX",'X',Blocks.stonebrick,'Y',ModBlocks.vampireFlower);
		GameRegistry.addShapelessRecipe(new ItemStack(castleBlock, 8, 1), castleBlock, castleBlock, castleBlock, castleBlock, castleBlock, castleBlock, castleBlock, castleBlock, new ItemStack(Items.dye, 1, 0));
		GameRegistry.addRecipe(new ItemStack(castleSlab, 6, 0), "XXX", 'X', new ItemStack(castleBlock, 1, 0));
		GameRegistry.addRecipe(new ItemStack(castleSlab, 6, 1), "XXX", 'X', new ItemStack(castleBlock, 1, 1));
		GameRegistry.addRecipe(new ItemStack(castleStairsDark, 4), "  X", " XX", "XXX", 'X', new ItemStack(castleBlock, 1, 1));
		GameRegistry.addRecipe(new ItemStack(castleStairsPurple, 4), "  X", " XX", "XXX", 'X', new ItemStack(castleBlock, 1, 0));
		GameRegistry.addRecipe(new ItemStack(gildedIron, 1), "XYX", "YYY", "XYX", 'X', Items.gold_ingot, 'Y', Items.iron_ingot);
		GameRegistry.addRecipe(new ItemStack(gildedIron, 1), "YXY", "XYX", "YXY", 'X', Items.gold_ingot, 'Y', Items.iron_ingot);
		GameRegistry.addRecipe(new ItemStack(garlicTorchWeak), "X", "Y", 'X', ModItems.garlic, 'Y', Items.stick);
		GameRegistry.addRecipe(new ItemStack(garlicTorchStrong), "X", "Y", 'X', ModItems.concentratedGarlic, 'Y', Items.stick);
	}
}
