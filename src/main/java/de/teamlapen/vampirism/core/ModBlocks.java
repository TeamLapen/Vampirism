package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.blocks.*;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
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


    private static @Nonnull
    ItemBlock itemBlock(@Nonnull Block block, @Nonnull Item.Properties props) {
        ItemBlock item = new ItemBlock(block, props);
        item.setRegistryName(block.getRegistryName());
        return item;
    }

    static void registerItemBlocks(IForgeRegistry<Item> registry) {
        registry.register(itemBlock(castle_block_dark_brick, new Item.Properties()));
        registry.register(itemBlock(castle_block_dark_brick_bloody, new Item.Properties()));
        registry.register(itemBlock(castle_block_dark_stone, new Item.Properties()));
        registry.register(itemBlock(castle_block_normal_brick, new Item.Properties()));
        registry.register(itemBlock(castle_block_purple_brick, new Item.Properties()));

        registry.register(itemBlock(blood_container, new Item.Properties().maxStackSize(1)));

        registry.register(itemBlock(cursed_earth, new Item.Properties()));
        registry.register(itemBlock(vampirism_flower_orchid, new Item.Properties()));
        registry.register(itemBlock(altar_infusion, new Item.Properties()));
        registry.register(itemBlock(altar_pillar, new Item.Properties()));
        registry.register(itemBlock(altar_tip, new Item.Properties()));
        registry.register(itemBlock(hunter_table, new Item.Properties()));
        registry.register(itemBlock(church_altar, new Item.Properties()));
        registry.register(itemBlock(altar_inspiration, new Item.Properties()));
        registry.register(itemBlock(fire_place, new Item.Properties()));
        registry.register(itemBlock(weapon_table, new Item.Properties()));
        registry.register(itemBlock(blood_potion_table, new Item.Properties()));
        registry.register(itemBlock(sunscreen_beacon, new Item.Properties()));
        registry.register(itemBlock(alchemical_cauldron, new Item.Properties()));
        registry.register(itemBlock(garlic_beacon_normal, new Item.Properties()));
        registry.register(itemBlock(garlic_beacon_improved, new Item.Properties()));
        registry.register(itemBlock(garlic_beacon_weak, new Item.Properties()));

        registry.register(itemBlock(castle_stairs_dark, new Item.Properties()));
        registry.register(itemBlock(castle_stairs_dark_stone, new Item.Properties()));
        registry.register(itemBlock(castle_stairs_purple, new Item.Properties()));
        registry.register(itemBlock(blood_pedestal, new Item.Properties()));
        registry.register(itemBlock(blood_grinder, new Item.Properties()));
        registry.register(itemBlock(blood_sieve, new Item.Properties()));
        registry.register(itemBlock(totem_base, new Item.Properties()));
        registry.register(itemBlock(totem_top, new Item.Properties()));
        registry.register(itemBlock(castle_slab_dark_brick, new Item.Properties()));
        registry.register(itemBlock(castle_slab_dark_stone, new Item.Properties()));
        registry.register(itemBlock(castle_slab_purple_brick, new Item.Properties()));
    }


    static void registerBlocks(IForgeRegistry<Block> registry) {
        registry.register(new BlockCastleBlock(BlockCastleBlock.EnumVariant.DARK_BRICK));
        registry.register(new BlockCastleBlock(BlockCastleBlock.EnumVariant.DARK_BRICK_BLOODY));
        registry.register(new BlockCastleBlock(BlockCastleBlock.EnumVariant.DARK_STONE));
        registry.register(new BlockCastleBlock(BlockCastleBlock.EnumVariant.NORMAL_BRICK));
        registry.register(new BlockCastleBlock(BlockCastleBlock.EnumVariant.PURPLE_BRICK));
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


        registry.register(new BlockCastleStairs(castle_block_dark_brick.getDefaultState(), "dark"));
        registry.register(new BlockCastleStairs(castle_block_dark_stone.getDefaultState(), "dark_stone"));
        registry.register(new BlockCastleStairs(castle_block_purple_brick.getDefaultState(), "purple"));
        registry.register(new BlockCastleSlab(BlockCastleSlab.EnumVariant.DARK_BRICK));
        registry.register(new BlockCastleSlab(BlockCastleSlab.EnumVariant.PURPLE_BRICK));
        registry.register(new BlockCastleSlab(BlockCastleSlab.EnumVariant.DARK_STONE));

        registry.register(new BlockPedestal());
        registry.register(new BlockGrinder());
        registry.register(new BlockSieve());
        registry.register(new BlockTotemTop());
        registry.register(new BlockTotemBase());
    }


}
