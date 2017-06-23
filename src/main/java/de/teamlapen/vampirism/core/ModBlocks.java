package de.teamlapen.vampirism.core;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import de.teamlapen.lib.lib.item.ItemMetaBlock;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.blocks.*;
import de.teamlapen.vampirism.tileentity.*;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.datafix.IFixableData;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Map;

/**
 * Handles all block registrations and reference.
 */
public class ModBlocks {
    private static final Map<String, String> OLD_TO_NEW_TILE_MAP = Maps.newHashMap();
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


    private static void registerTiles() {
        registerTileEntity(TileTent.class, "tent", "VampirismTent");
        registerTileEntity(TileCoffin.class, "coffin", "VampirismCoffin");
        registerTileEntity(TileAltarInfusion.class, "altar_infusion", "VampirismAltarInfusion");
        registerTileEntity(TileBloodContainer.class, "blood_container", "VampirismBloodContainer");
        registerTileEntity(TileAltarInspiration.class, "altar_inspiration", "VampirismAltarInspiration");
        registerTileEntity(TileSunscreenBeacon.class, "sunscreen_beacon", "VampirismSunscreenBeacon");
        registerTileEntity(TileAlchemicalCauldron.class, "alchemical_cauldron", "VampirismAlchemicalCauldron");
        registerTileEntity(TileGarlicBeacon.class, "garlic_beacon", "VampirismGarlicBeacon");
    }

    /**
     * Register the given tile entity and add pre 1.11 name to DATA FIXER
     *
     * @param clazz Tile class
     * @param id    Tile id. Is converted to resource location  MODID:<id>
     */
    private static void registerTileEntity(Class<? extends TileEntity> clazz, String id, String old) {
        registerTileEntity(clazz, id);
        OLD_TO_NEW_TILE_MAP.put(old, REFERENCE.MODID + ":" + id);
    }

    public static IFixableData getTileEntityIDFixer() {
        return new IFixableData() {
            @Override
            public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
                String s = OLD_TO_NEW_TILE_MAP.get(compound.getString("id"));

                if (s != null) {
                    compound.setString("id", s);
                }

                return compound;
            }

            @Override
            public int getFixVersion() {
                return 1;
            }
        };
    }

    /**
     * Register the given tile entity
     *
     * @param clazz Tile class
     * @param id    Tile id. Is converted to resource location  MODID:<id>
     */
    private static void registerTileEntity(Class<? extends TileEntity> clazz, String id) {
        GameRegistry.registerTileEntity(clazz, REFERENCE.MODID + ":" + id);
    }

    static void registerBlocks() {
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

        registerTiles();
    }

    static void registerCraftingRecipes() {
        GameRegistry.addRecipe(new ItemStack(altarInfusion, 1), "   ", "YZY", "ZZZ", 'Y', Items.GOLD_INGOT, 'Z', Blocks.OBSIDIAN);
        GameRegistry.addRecipe(new ItemStack(altarPillar, 4), "X X", "   ", "XXX", 'X', Blocks.STONEBRICK);
        GameRegistry.addRecipe(new ItemStack(altarTip, 2), "   ", " X ", "XYX", 'X', Items.IRON_INGOT, 'Y', Blocks.IRON_BLOCK);
        GameRegistry.addShapelessRecipe(new ItemStack(castleBlock, 8, 3), Blocks.STONEBRICK, Blocks.STONEBRICK, Blocks.STONEBRICK, Blocks.STONEBRICK, Blocks.STONEBRICK, Blocks.STONEBRICK, Blocks.STONEBRICK, Blocks.STONEBRICK, new ItemStack(vampirismFlower, 1, VampirismFlower.EnumFlowerType.ORCHID.getMeta()));
        GameRegistry.addShapelessRecipe(new ItemStack(castleBlock, 8, 0), new ItemStack(castleBlock, 1, 3), new ItemStack(castleBlock, 1, 3), new ItemStack(castleBlock, 1, 3), new ItemStack(castleBlock, 1, 3), new ItemStack(castleBlock, 1, 3), new ItemStack(castleBlock, 1, 3), new ItemStack(castleBlock, 1, 3), new ItemStack(castleBlock, 1, 3), new ItemStack(Items.DYE, 1, 0));
        GameRegistry.addShapelessRecipe(new ItemStack(castleBlock, 8, 1), new ItemStack(castleBlock, 1, 3), new ItemStack(castleBlock, 1, 3), new ItemStack(castleBlock, 1, 3), new ItemStack(castleBlock, 1, 3), new ItemStack(castleBlock, 1, 3), new ItemStack(castleBlock, 1, 3), new ItemStack(castleBlock, 1, 3), new ItemStack(castleBlock, 1, 3), new ItemStack(vampirismFlower, 1, VampirismFlower.EnumFlowerType.ORCHID.getMeta()));
        GameRegistry.addShapelessRecipe(new ItemStack(castleBlock, 7, 0), Blocks.STONEBRICK, Blocks.STONEBRICK, Blocks.STONEBRICK, Blocks.STONEBRICK, Blocks.STONEBRICK, Blocks.STONEBRICK, Blocks.STONEBRICK, new ItemStack(Items.DYE, 1, 0), new ItemStack(vampirismFlower, 1, VampirismFlower.EnumFlowerType.ORCHID.getMeta()));
        GameRegistry.addShapelessRecipe(new ItemStack(castleBlock, 7, 4), Blocks.STONE, Blocks.STONE, Blocks.STONE, Blocks.STONE, Blocks.STONE, Blocks.STONE, Blocks.STONE, new ItemStack(Items.DYE, 1, 0), new ItemStack(vampirismFlower, 1, VampirismFlower.EnumFlowerType.ORCHID.getMeta()));
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

    /**
     * Fix block mappings
     *
     * @return if it was fixed
     */
    public static boolean fixMapping(FMLMissingMappingsEvent.MissingMapping mapping) {

        //Check for mappings changed for 1.11 CamelCase to lower underscore
        return checkMapping(mapping, mapping.resourceLocation.getResourcePath(), alchemicalCauldron, alchemicalFire, altarInfusion, altarInspiration, altarPillar, altarTip, bloodContainer, bloodPotionTable, castleBlock, churchAltar, coffin, cursedEarth, firePlace, fluidBlood, garlicBeacon, hunterTable, medChair, sunscreenBeacon, tentMain, vampirismFlower, weaponTable);
    }

    private static boolean checkMapping(FMLMissingMappingsEvent.MissingMapping mapping, String name, Block... blocks) {
        for (Block b : blocks) {
            String newRegisteredName = b instanceof VampirismBlock ? ((VampirismBlock) b).getRegisteredName() : (b instanceof VampirismBlockContainer ? ((VampirismBlockContainer) b).getRegisteredName() : (b instanceof VampirismFlower ? ((VampirismFlower) b).getRegisteredName() : (b instanceof BlockFluidBlood ? ((BlockFluidBlood) b).getRegisteredName() : null)));
            if (newRegisteredName == null) {
                VampirismMod.log.w("ModBlocks", "Unknown block class %s. Unable to determine new registered name during mapping fix", b.getClass());
                continue;
            }
            String oldRegisteredName = newRegisteredName.replaceAll("_", "");

            if (oldRegisteredName.equals(name)){
                if (mapping.type == GameRegistry.Type.ITEM) {
                    mapping.remap(Item.getItemFromBlock(b));
                } else {
                    mapping.remap(b);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Fix item block mappings
     *
     * @return if it was fixed
     */
    public static boolean fixMappingItemBlock(FMLMissingMappingsEvent.MissingMapping mapping) {
        //Check for mappings changed for 1.11 CamelCase to lower underscore
        String converted = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, mapping.resourceLocation.getResourcePath());
        return checkMapping(mapping, converted, alchemicalCauldron, altarInfusion, altarInspiration, altarPillar, altarTip, bloodContainer, bloodPotionTable, castleBlock, churchAltar, cursedEarth, firePlace, fluidBlood, garlicBeacon, hunterTable, sunscreenBeacon, vampirismFlower, weaponTable);
    }


}
