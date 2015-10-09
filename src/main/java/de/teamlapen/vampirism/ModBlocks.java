package de.teamlapen.vampirism;

import de.teamlapen.vampirism.block.*;
import de.teamlapen.vampirism.block.BlockBloodAltar4Tip.TileEntityBloodAltar4Tip;
import de.teamlapen.vampirism.block.BlockChurchAltar.TileEntityChurchAltar;
import de.teamlapen.vampirism.item.ItemCastleSlab;
import de.teamlapen.vampirism.item.ItemMetaBlock;
import de.teamlapen.vampirism.tileEntity.*;
import de.teamlapen.vampirism.util.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ModBlocks {

	public final static BlockBloodAltar1 bloodAltar1 = new BlockBloodAltar1();

	public final static MaterialLiquid blood = new MaterialLiquidBlood(MapColor.netherrackColor);

	public final static BlockBloodAltar2 bloodAltar2 = new BlockBloodAltar2();

	public final static BlockBloodAltar4 bloodAltar4 = new BlockBloodAltar4();
	public final static BlockBloodAltar4Tip bloodAltar4Tip = new BlockBloodAltar4Tip();
	public final static BlockCursedEarth cursedEarth = new BlockCursedEarth();
	public final static BlockChurchAltar churchAltar = new BlockChurchAltar();
	public final static BlockCoffin coffin = new BlockCoffin();
	public final static BlockTemplateGenerator templateGenerator = new BlockTemplateGenerator();
	public final static BlockCastle castleBlock = new BlockCastle();
	public final static BlockCastleSlab doubleCastleSlab = new BlockCastleSlabDouble();
	public final static BlockCastleSlab castleSlab = new BlockCastleSlabHalf();
	public final static BlockStairs castleStairsPurple = new BlockCastleStairs(castleBlock.getStateFromMeta(0));
	public final static BlockStairs castleStairsDark = new BlockCastleStairs(castleBlock.getStateFromMeta(1));
	public final static BlockCastlePortal castlePortal = new BlockCastlePortal();
	public final static BlockDraculaButton blockDraculaButton = new BlockDraculaButton();
	public final static BlockMainTent blockMainTent = new BlockMainTent();
	public final static BlockTent blockTent = new BlockTent();

	// Flowers
	public final static VampireFlower vampireFlower = new VampireFlower();
	public static void preInit() {
		GameRegistry.registerBlock(bloodAltar1, BlockBloodAltar1.name);
		GameRegistry.registerBlock(bloodAltar2, ItemBlock.class, BlockBloodAltar2.name);
		// GameRegistry.registerBlock(bloodAltarTier3, ItemBlock.class,BlockBloodAltarTier3.name);
		GameRegistry.registerBlock(bloodAltar4, ItemBlock.class, BlockBloodAltar4.name);
		GameRegistry.registerBlock(bloodAltar4Tip, ItemBlock.class, BlockBloodAltar4Tip.name);
		GameRegistry.registerBlock(cursedEarth, BlockCursedEarth.name);
		GameRegistry.registerBlock(churchAltar, BlockChurchAltar.name);
		GameRegistry.registerBlock(coffin, BlockCoffin.name);
		GameRegistry.registerBlock(castleBlock, ItemMetaBlock.class,BlockCastle.name);
		GameRegistry.registerBlock(castleSlab, ItemCastleSlab.class,BlockCastleSlab.name,castleSlab,doubleCastleSlab,false);
		GameRegistry.registerBlock(doubleCastleSlab,ItemCastleSlab.class,BlockCastleSlab.doubleName,castleSlab,doubleCastleSlab,true);
		GameRegistry.registerBlock(castleStairsDark,BlockCastleStairs.name+"_dark");
		GameRegistry.registerBlock(castleStairsPurple,BlockCastleStairs.name+"_purple");
		GameRegistry.registerBlock(castlePortal,null,BlockCastlePortal.name);
		GameRegistry.registerBlock(blockDraculaButton, BlockDraculaButton.name);
		GameRegistry.registerBlock(blockMainTent, BlockMainTent.name);
		GameRegistry.registerBlock(blockTent, BlockTent.name);
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
		GameRegistry.addRecipe(new ItemStack(castleStairsDark, 1), "  X", " XX", "XXX", 'X', new ItemStack(castleBlock, 1, 1));
		GameRegistry.addRecipe(new ItemStack(castleStairsPurple, 1), "  X", " XX", "XXX", 'X', new ItemStack(castleBlock, 1, 0));
	}

	@SideOnly(Side.CLIENT)
	private static List<Block> blocksToItemRegister;
	@SideOnly(Side.CLIENT)
	public static void preInitClient(){
		blocksToItemRegister=new ArrayList<Block>();
		for(Field f:ModBlocks.class.getDeclaredFields()){
			//Logger.t("%s (%s)",f,f.getType());
			if(Block.class.isAssignableFrom(f.getType())){
				//Logger.t("Checking %s (%s) for ignored properties",f,f.getType());
				try {
					Block b= (Block) f.get(null);
					if(b instanceof IIgnorePropsForRender){
						Logger.t("Adding ignored properties for %s",b);
						ModelLoader.setCustomStateMapper(b,new StateMap.Builder().addPropertiesToIgnore(((IIgnorePropsForRender)b).getRenderIgnoredProperties()).build());
					}
					if(b instanceof IBlockRegistrable){
						blocksToItemRegister.add(b);
							String[] variants=((IBlockRegistrable) b).getVariantsToRegister();
							if(variants!=null){
								ModelBakery.addVariantName(Item.getItemFromBlock(b), Helper.prefix("vampirism:",variants));
							}

					}

				} catch (IllegalAccessException e) {
					Logger.e("ModBlocks","Failed to retrieve block for %s",f);
				}
			}
		}
		ModelBakery.addVariantName(Item.getItemFromBlock(coffin),"vampirism:coffin_foot","vampirism:coffin_head");
	}



	@SideOnly(Side.CLIENT)
	public static void initClient(){
		for(Block b:blocksToItemRegister){
			if(((IBlockRegistrable)b).shouldRegisterSimpleItem()){
				Logger.t("Simple reg %s",b);
				reg(b);
			}
			else{
				String[] variants=((IBlockRegistrable)b).getVariantsToRegister();
				if(variants!=null){
					reg(b,variants);
					Logger.t("Variant reg %s %s",b,variants);
				}
			}
		}
		blocksToItemRegister.clear();
		blocksToItemRegister=null;
		reg(coffin,"coffin_foot");
		reg(castleStairsDark,"castleStairs_dark");
		reg(castleStairsPurple,"castleStairs_purple");
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(blockDraculaButton), 0, new ModelResourceLocation("stone_button", "inventory"));

	}

	@SideOnly(Side.CLIENT)
	private static void reg(Block block){
		String file=block.getUnlocalizedName().replace("tile." + REFERENCE.MODID + ".", "");
		reg(block,0,file);
	}

	@SideOnly(Side.CLIENT)
	private static void reg(Block block,String... filesForMeta){
		for(int i=0;i<filesForMeta.length;i++){
			reg(block,i,filesForMeta[i]);
		}
	}
	@SideOnly(Side.CLIENT)
	private static void reg(Block block,int meta,String file){
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
				.register(Item.getItemFromBlock(block), meta, new ModelResourceLocation(REFERENCE.MODID+ ":" + file, "inventory"));
	}
}
