package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.item.ItemMetaBlock;
import de.teamlapen.vampirism.blocks.*;
import de.teamlapen.vampirism.tileentity.*;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Handles all block registrations and reference.
 */
@ObjectHolder(REFERENCE.MODID)
public class ModBlocks {
    public static final BlockCastleBlock castle_block_purple_brick = getNull();
    public static final BlockCastleBlock castle_block_dark_brick = getNull();
    public static final BlockCastleBlock castle_block_dark_brick_bloody = getNull();
    public static final BlockCastleBlock castle_block_dark_stone = getNull();
    public static final BlockCastleBlock castle_block_normal_brick = getNull();
    public static final BlockCursedEarth cursed_earth = getNull();
    public static final VampirismFlower vampirism_flower_orchid = getNull();
    public static final BlockTent tent = getNull();
    public static final BlockTentMain tent_main = getNull();
    public static final BlockCoffin block_coffin = getNull();
    public static final BlockAltarInfusion altar_infusion = getNull();
    public static final BlockAltarPillar altar_pillar = getNull();
    public static final BlockAltarTip altar_tip = getNull();
    public static final BlockHunterTable hunter_table = getNull();
    public static final BlockMedChair med_chair = getNull();
    public static final BlockGarlic garlic = getNull();
    public static final BlockChurchAltar church_altar = getNull();
    public static final BlockBloodContainer blood_container = getNull();
    public static final BlockAltarInspiration altar_inspiration = getNull();
    public static final BlockFirePlace fire_place = getNull();
    public static final BlockWeaponTable weapon_table = getNull();
    public static final BlockBloodPotionTable blood_potion_table = getNull();
    public static final BlockSunscreenBeacon sunscreen_beacon = getNull();
    public static final BlockAlchemicalFire alchemical_fire = getNull();
    public static final BlockAlchemicalCauldron alchemical_cauldron = getNull();
    public static final BlockGarlicBeacon garlic_beacon_normal = getNull();
    public static final BlockGarlicBeacon garlic_beacon_weak = getNull();
    public static final BlockGarlicBeacon garlic_beacon_improved = getNull();

    public static final BlockStairs castle_stairs_dark = getNull();
    public static final BlockStairs castle_stairs_dark_stone = getNull();
    public static final BlockStairs castle_stairs_purple = getNull();
    public static final BlockCastleSlab castle_slab_dark_stone = getNull();
    public static final BlockCastleSlab castle_slab_dark_brick = getNull();
    public static final BlockCastleSlab castle_slab_purple_brick = getNull();
    public static final BlockPedestal blood_pedestal = getNull();
    public static final BlockGrinder blood_grinder = getNull();
    public static final BlockSieve blood_sieve = getNull();
    public static final BlockTotemTop totem_top = getNull();
    public static final VampirismBlock totem_base = getNull();




    private static void registerTiles() {
        registerTileEntity(TileTent.class, "tent", "VampirismTent");
        registerTileEntity(TileCoffin.class, "coffin", "VampirismCoffin");
        registerTileEntity(TileAltarInfusion.class, "altar_infusion", "VampirismAltarInfusion");
        registerTileEntity(TileBloodContainer.class, "blood_container", "VampirismBloodContainer");
        registerTileEntity(TileAltarInspiration.class, "altar_inspiration", "VampirismAltarInspiration");
        registerTileEntity(TileSunscreenBeacon.class, "sunscreen_beacon", "VampirismSunscreenBeacon");
        registerTileEntity(TileAlchemicalCauldron.class, "alchemical_cauldron", "VampirismAlchemicalCauldron");
        registerTileEntity(TileGarlicBeacon.class, "garlic_beacon", "VampirismGarlicBeacon");
        registerTileEntity(TilePedestal.class, "blood_pedestal");
        registerTileEntity(TileGrinder.class, "grinder");
        registerTileEntity(TileSieve.class, "sieve");
        registerTileEntity(TileTotem.class, "totem");
    }

    /**
     * Register the given tile entity and add pre 1.11 name to DATA FIXER
     *
     * @param clazz Tile class
     * @param id    Tile id. Is converted to resource location  MODID:<id>
     */
    private static void registerTileEntity(Class<? extends TileEntity> clazz, String id, String old) {
        registerTileEntity(clazz, id);
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

    static void registerItemBlocks(IForgeRegistry<Item> registry) {
        registry.register(new ItemMetaBlock(castle_block));
        Item itemBloodContainer = new ItemBlock(blood_container);
        itemBloodContainer.setRegistryName(blood_container.getRegistryName());
        itemBloodContainer.setMaxStackSize(1);
        registry.register(itemBloodContainer);
        registry.register(itemBlock(cursed_earth));
        registry.register(new ItemMetaBlock(vampirism_flower));
        registry.register(itemBlock(altar_infusion));
        registry.register(itemBlock(altar_pillar));
        registry.register(itemBlock(altar_tip));
        registry.register(itemBlock(hunter_table));
        registry.register(itemBlock(church_altar));
        registry.register(itemBlock(altar_inspiration));
        registry.register(itemBlock(fire_place));
        registry.register(itemBlock(weapon_table));
        registry.register(itemBlock(blood_potion_table));
        registry.register(itemBlock(sunscreen_beacon));
        registry.register(itemBlock(alchemical_cauldron));
        registry.register(itemBlock(garlic_beacon));
        registry.register(itemBlock(castle_stairs_dark));
        registry.register(itemBlock(castle_stairs_dark_stone));
        registry.register(itemBlock(castle_stairs_purple));
        registry.register(itemBlock(blood_pedestal));
        registry.register(itemBlock(blood_grinder));
        registry.register(itemBlock(blood_sieve));
        registry.register(itemBlock(totem_base));
        registry.register(itemBlock(totem_top));
        registry.register(new ItemSlab(castle_slab, castle_slab, castle_slab_double).setRegistryName(castle_slab.getRegistryName()));
    }

    private static @Nonnull
    ItemBlock itemBlock(@Nonnull Block b) {
        ItemBlock item = new ItemBlock(b);
        //noinspection ConstantConditions
        item.setRegistryName(b.getRegistryName());
        return item;
    }

    static void registerBlocks(IForgeRegistry<Block> registry) {
        BlockCastleBlock castleBlock_dark_brick = new BlockCastleBlock(BlockCastleBlock.EnumVariant.DARK_BRICK);
        registry.register(castleBlock_dark_brick);
        registry.register(new BlockCastleBlock(BlockCastleBlock.EnumVariant.DARK_BRICK_BLOODY));
        BlockCastleBlock castleBlock_dark_stone = new BlockCastleBlock(BlockCastleBlock.EnumVariant.DARK_STONE);
        registry.register(castleBlock_dark_stone);
        registry.register(new BlockCastleBlock(BlockCastleBlock.EnumVariant.NORMAL_BRICK));
        BlockCastleBlock castleBlock_purple = new BlockCastleBlock(BlockCastleBlock.EnumVariant.PURPLE_BRICK);
        registry.register(castleBlock_purple);
        registry.register(new VampirismFlower(VampirismFlower.TYPE.ORCHID));
        registry.register(new BlockCursedEarth());
        registry.register(new BlockTent());
        registry.register(new BlockTentMain());
        registry.register(new BlockCoffin());
        registry.register(new BlockAltarInfusion());
        registry.register(new BlockAltarPillar());
        registry.register(new BlockAltarTip());
        registry.register(new BlockHunterTable());
        registry.register(new BlockMedChair());
        registry.register(new BlockGarlic());
        registry.register(new BlockChurchAltar());
        registry.register(new BlockBloodContainer());
        registry.register(new BlockAltarInspiration());
        registry.register(new BlockFirePlace());
        registry.register(new BlockWeaponTable());
        registry.register(new BlockBloodPotionTable());
        registry.register(new BlockSunscreenBeacon());
        registry.register(new BlockAlchemicalFire());
        registry.register(new BlockAlchemicalCauldron());
        registry.register(new BlockGarlicBeacon(BlockGarlicBeacon.Type.NORMAL));
        registry.register(new BlockGarlicBeacon(BlockGarlicBeacon.Type.WEAK));
        registry.register(new BlockGarlicBeacon(BlockGarlicBeacon.Type.IMPROVED));


        registry.register(new BlockCastleStairs(castleBlock_dark_brick.getDefaultState(), "dark"));
        registry.register(new BlockCastleStairs(castleBlock_dark_stone.getDefaultState(), "dark_stone"));
        registry.register(new BlockCastleStairs(castleBlock_purple.getDefaultState(), "purple"));
        registry.register(new BlockCastleSlab(BlockCastleSlab.EnumVariant.DARK_BRICK));
        registry.register(new BlockCastleSlab(BlockCastleSlab.EnumVariant.PURPLE_BRICK));
        registry.register(new BlockCastleSlab(BlockCastleSlab.EnumVariant.DARK_STONE));

        registry.register(new BlockPedestal());
        registry.register(new BlockGrinder());
        registry.register(new BlockSieve());
        registry.register(new BlockTotemTop());
        registry.register(new BlockTotemBase());
        registerTiles();
    }

    static void registerCraftingRecipes() {


    }

}
