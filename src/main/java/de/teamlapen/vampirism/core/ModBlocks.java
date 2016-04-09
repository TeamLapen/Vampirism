package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.item.ItemMetaBlock;
import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.blocks.*;
import de.teamlapen.vampirism.tileentity.TileAltarInfusion;
import de.teamlapen.vampirism.tileentity.TileBloodContainer;
import de.teamlapen.vampirism.tileentity.TileCoffin;
import de.teamlapen.vampirism.tileentity.TileTent;
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

    public static void onInitStep(IInitListener.Step step, FMLStateEvent event) {
        switch (step) {
            case PRE_INIT:
                registerBlocks();
                registerTiles();
                break;
            case INIT:
                registerCraftingRecipes();
        }

    }

    private static void registerTiles() {
        GameRegistry.registerTileEntity(TileTent.class, "VampirismTent");
        GameRegistry.registerTileEntity(TileCoffin.class, "VampirismCoffin");
        GameRegistry.registerTileEntity(TileAltarInfusion.class, "VampirismAltarInfusion");
        GameRegistry.registerTileEntity(TileBloodContainer.class, "VampirismBloodContainer");
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
        //coffin = registerBlock(new BlockCoffin(), null);
        altarInfusion = registerBlock(new BlockAltarInfusion());
        altarPillar = registerBlock(new BlockAltarPillar());
        altarTip = registerBlock(new BlockAltarTip());
        hunterTable = registerBlock(new BlockHunterTable());
        medChair = registerBlock(new BlockMedChair(), null);
        garlic = registerBlock(new BlockGarlic(), null);
        churchAltar = registerBlock(new BlockChurchAltar());
        bloodContainer = registerBlock(new BlockBloodContainer());

    }

    private static void registerCraftingRecipes() {
        GameRegistry.addRecipe(new ItemStack(altarInfusion, 1), "   ", "YZY", "ZZZ", 'Y', Items.gold_ingot, 'Z', Blocks.obsidian);
        GameRegistry.addRecipe(new ItemStack(altarPillar, 4), "X X", "   ", "XXX", 'X', Blocks.stonebrick);
        GameRegistry.addRecipe(new ItemStack(altarTip, 2), "   ", " X ", "XYX", 'X', Items.iron_ingot, 'Y', Blocks.iron_block);
        GameRegistry.addRecipe(new ItemStack(castleBlock, 1, 0), "XXX", "XYX", "XXX", 'X', Blocks.stonebrick, 'Y', new ItemStack(vampirismFlower, 1, VampirismFlower.EnumFlowerType.ORCHID.getMeta()));
        GameRegistry.addShapelessRecipe(new ItemStack(castleBlock, 8, 1), castleBlock, castleBlock, castleBlock, castleBlock, castleBlock, castleBlock, castleBlock, castleBlock, new ItemStack(Items.dye, 1, 0));
        GameRegistry.addRecipe(new ItemStack(hunterTable), "XY ", "ZZZ", "Z Z", 'X', ModItems.vampireFang, 'Y', Items.book, 'Z', Blocks.planks);//TODO maybe replace fangs with garlic
        GameRegistry.addRecipe(new ItemStack(medChair), "XYX", "XXX", "XZX", 'X', Items.iron_ingot, 'Y', Blocks.wool, 'Z', Items.glass_bottle);
        GameRegistry.addRecipe(new ItemStack(bloodContainer), "XYX", "YZY", "XYX", 'X', Blocks.planks, 'Y', Blocks.glass, 'Z', Items.iron_ingot);
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
