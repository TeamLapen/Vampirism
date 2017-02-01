package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.item.ItemMetaBlock;
import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.blocks.*;
import de.teamlapen.vampirism.tileentity.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Handles all block registrations and reference.
 */
public class ModBlocks {
    public static BlockFluidBlood fluidBlood;
    public static BlockCastleBlock castleBlock;
    public static BlockCursedEarth cursedEarth;
    public static VampirismFlower vampirismFlower;
    public static BlockTent tent;
    public static BlockTentMain tentMain;
    public static BlockCoffin coffin;
    public static BlockAltarInfusion altarInfusion;
    public static BlockAltarPillar altarPillar;
    public static BlockAltarTip altarTip;
    public static BlockHunterTable hunterTable;
    public static BlockMedChair medChair;
    public static BlockGarlic garlic;
    public static BlockChurchAltar churchAltar;
    public static BlockBloodContainer bloodContainer;
    public static BlockAltarInspiration altarInspiration;
    public static BlockFirePlace firePlace;
    public static BlockWeaponTable weaponTable;
    public static BlockBloodPotionTable bloodPotionTable;
    public static BlockSunscreenBeacon sunscreenBeacon;
    public static BlockAlchemicalFire alchemicalFire;
    public static BlockAlchemicalCauldron alchemicalCauldron;
    public static BlockGarlicBeacon garlicBeacon;

    public static void onInitStep(IInitListener.Step step, FMLStateEvent event) {
        switch (step) {
            case PRE_INIT:
                registerBlocks();
                registerTiles();
                break;
            case INIT:
                registerCraftingRecipes();
                break;
            default://Do nothing
        }

    }

    private static void registerTiles() {
        GameRegistry.registerTileEntity(TileTent.class, "VampirismTent");
        GameRegistry.registerTileEntity(TileCoffin.class, "VampirismCoffin");
        GameRegistry.registerTileEntity(TileAltarInfusion.class, "VampirismAltarInfusion");
        GameRegistry.registerTileEntity(TileBloodContainer.class, "VampirismBloodContainer");
        GameRegistry.registerTileEntity(TileAltarInspiration.class, "VampirismAltarInspiration");
        GameRegistry.registerTileEntity(TileSunscreenBeacon.class, "VampirismSunscreenBeacon");
        GameRegistry.registerTileEntity(TileAlchemicalCauldron.class, "VampirismAlchemicalCauldron");
        GameRegistry.registerTileEntity(TileGarlicBeacon.class, "VampirismGarlicBeacon");
    }

    private static void registerBlocks() {
        fluidBlood = registerBlock(new BlockFluidBlood());//TODO Maybe remove blood block later
        castleBlock = new BlockCastleBlock();
        registerBlock(castleBlock, new ItemMetaBlock(castleBlock));
        cursedEarth = registerBlock(new BlockCursedEarth());
        vampirismFlower = new VampirismFlower();
        registerBlock(vampirismFlower, new ItemMetaBlock(vampirismFlower));
        tent = registerBlock(new BlockTent(), null);
        tentMain = registerBlock(new BlockTentMain(), null);
        coffin = registerBlock(new BlockCoffin(), null);
        altarInfusion = registerBlock(new BlockAltarInfusion());
        altarPillar = registerBlock(new BlockAltarPillar());
        altarTip = registerBlock(new BlockAltarTip());
        hunterTable = registerBlock(new BlockHunterTable());
        medChair = registerBlock(new BlockMedChair(), null);
        garlic = registerBlock(new BlockGarlic(), null);
        churchAltar = registerBlock(new BlockChurchAltar());
        bloodContainer = new BlockBloodContainer();
        Item itemBloodContainer = new ItemBlock(bloodContainer);
        itemBloodContainer.setRegistryName(bloodContainer.getRegistryName());
        itemBloodContainer.setMaxStackSize(1);
        registerBlock(bloodContainer, itemBloodContainer);
        altarInspiration = registerBlock(new BlockAltarInspiration());
        firePlace = registerBlock(new BlockFirePlace());
        weaponTable = registerBlock(new BlockWeaponTable());
        bloodPotionTable = registerBlock(new BlockBloodPotionTable());
        sunscreenBeacon = registerBlock(new BlockSunscreenBeacon());
        alchemicalFire = registerBlock(new BlockAlchemicalFire(), null);
        alchemicalCauldron = registerBlock(new BlockAlchemicalCauldron());
        garlicBeacon = registerBlock(new BlockGarlicBeacon());

    }

    private static void registerCraftingRecipes() {
        GameRegistry.addRecipe(new ItemStack(altarInfusion, 1), "   ", "YZY", "ZZZ", 'Y', Items.GOLD_INGOT, 'Z', Blocks.OBSIDIAN);
        GameRegistry.addRecipe(new ItemStack(altarPillar, 4), "X X", "   ", "XXX", 'X', Blocks.STONEBRICK);
        GameRegistry.addRecipe(new ItemStack(altarTip, 2), "   ", " X ", "XYX", 'X', Items.IRON_INGOT, 'Y', Blocks.IRON_BLOCK);
        GameRegistry.addShapelessRecipe(new ItemStack(castleBlock, 8, 0), Blocks.STONEBRICK, Blocks.STONEBRICK, Blocks.STONEBRICK, Blocks.STONEBRICK, Blocks.STONEBRICK, Blocks.STONEBRICK, Blocks.STONEBRICK, Blocks.STONEBRICK, new ItemStack(vampirismFlower, 1, VampirismFlower.EnumFlowerType.ORCHID.getMeta()));
        GameRegistry.addShapelessRecipe(new ItemStack(castleBlock, 8, 1), castleBlock, castleBlock, castleBlock, castleBlock, castleBlock, castleBlock, castleBlock, castleBlock, new ItemStack(Items.DYE, 1, 0));
        GameRegistry.addRecipe(new ItemStack(hunterTable), "XYW", "ZZZ", "Z Z", 'X', ModItems.vampireFang, 'Y', Items.BOOK, 'Z', Blocks.PLANKS, 'W', ModItems.itemGarlic);
        GameRegistry.addRecipe(new ItemStack(bloodContainer), "XYX", "YZY", "XYX", 'X', Blocks.PLANKS, 'Y', Blocks.GLASS, 'Z', Items.IRON_INGOT);
        GameRegistry.addRecipe(new ItemStack(altarInspiration, 1), " X ", "XYX", "ZZZ", 'X', Blocks.GLASS, 'Y', Items.GLASS_BOTTLE, 'Z', Items.IRON_INGOT);
        GameRegistry.addRecipe(new ItemStack(firePlace, 1), "   ", " X ", "XYX", 'X', Blocks.LOG, 'Y', Items.COAL);
        GameRegistry.addRecipe(new ItemStack(weaponTable, 1), "X  ", "YYY", " Z ", 'X', Items.BUCKET, 'Y', Items.IRON_INGOT, 'Z', Blocks.IRON_BLOCK);
        GameRegistry.addRecipe(new ItemStack(bloodPotionTable, 1), "XXX", "Y Y", "ZZZ", 'X', Items.GLASS_BOTTLE, 'Y', Blocks.PLANKS, 'Z', Items.IRON_INGOT);
        GameRegistry.addRecipe(new ItemStack(churchAltar), " X ", "YYY", " Y ", 'X', ModItems.vampireBook, 'Y', Blocks.PLANKS);
        GameRegistry.addRecipe(new ItemStack(alchemicalCauldron), "XZX", "XXX", "Y Y", 'X', Items.IRON_INGOT, 'Y', Blocks.STONEBRICK, 'Z', ModItems.itemGarlic);
        GameRegistry.addRecipe(new ItemStack(garlicBeacon), "XYX", "YZY", "OOO", 'X', Blocks.PLANKS, 'Y', Items.DIAMOND, 'Z', ModItems.garlicBeaconCore, 'O', Blocks.OBSIDIAN);
        GameRegistry.addRecipe(new ItemStack(garlicBeacon, 1, 1), "XYX", "YZY", "OOO", 'X', Blocks.PLANKS, 'Y', Items.DIAMOND, 'Z', ModItems.garlicBeaconCoreImproved, 'O', Blocks.OBSIDIAN);

    }


    private static <T extends Block> T registerBlock(T block, Item item) {
        GameRegistry.register(block);
        if (item != null) GameRegistry.register(item);
        return block;
    }

    private static <T extends Block> T registerBlock(T block) {
        Item item = new ItemBlock(block);
        item.setRegistryName(block.getRegistryName());
        return registerBlock(block, item);
    }


}
