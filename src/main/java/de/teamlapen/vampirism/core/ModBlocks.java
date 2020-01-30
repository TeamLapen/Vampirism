package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.blocks.*;
import de.teamlapen.vampirism.client.render.VampirismItemStackTESR;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Handles all block registrations and reference.
 */
@ObjectHolder(REFERENCE.MODID)
@SuppressWarnings("unused")
public class ModBlocks {
    public static final AlchemicalCauldronBlock alchemical_cauldron = getNull();
    public static final AlchemicalFireBlock alchemical_fire = getNull();
    public static final AltarInfusionBlock altar_infusion = getNull();
    public static final AltarInspirationBlock altar_inspiration = getNull();
    public static final AltarPillarBlock altar_pillar = getNull();
    public static final AltarTipBlock altar_tip = getNull();
    public static final BloodContainerBlock blood_container = getNull();
    public static final GrinderBlock blood_grinder = getNull();
    public static final PedestalBlock blood_pedestal = getNull();
    public static final BloodPotionTableBlock blood_potion_table = getNull();
    public static final SieveBlock blood_sieve = getNull();
    public static final CastleBricksBlock castle_block_dark_brick = getNull();
    public static final CastleBricksBlock castle_block_dark_brick_bloody = getNull();
    public static final CastleBricksBlock castle_block_dark_stone = getNull();
    public static final CastleBricksBlock castle_block_normal_brick = getNull();
    public static final CastleBricksBlock castle_block_purple_brick = getNull();
    public static final CastleSlabBlock castle_slab_dark_brick = getNull();
    public static final CastleSlabBlock castle_slab_dark_stone = getNull();
    public static final CastleSlabBlock castle_slab_purple_brick = getNull();
    public static final CastleStairsBlock castle_stairs_dark_brick = getNull();
    public static final CastleStairsBlock castle_stairs_dark_stone = getNull();
    public static final CastleStairsBlock castle_stairs_purple_brick = getNull();
    public static final ChurchAltarBlock church_altar = getNull();
    public static final CoffinBlock coffin = getNull();
    public static final CursedEarthBlock cursed_earth = getNull();
    public static final FirePlaceBlock fire_place = getNull();
    public static final GarlicBlock garlic = getNull();
    public static final GarlicBeaconBlock garlic_beacon_improved = getNull();
    public static final GarlicBeaconBlock garlic_beacon_normal = getNull();
    public static final GarlicBeaconBlock garlic_beacon_weak = getNull();
    public static final HunterTableBlock hunter_table = getNull();
    public static final MedChairBlock med_chair = getNull();
    public static final FlowerPotBlock potted_vampire_orchid = getNull();
    public static final SunscreenBeaconBlock sunscreen_beacon = getNull();
    public static final TentBlock tent = getNull();
    public static final TentMainBlock tent_main = getNull();
    public static final TotemBaseBlock totem_base = getNull();
    public static final TotemTopBlock totem_top = getNull();
    public static final TotemTopBlock totem_top_vampirism_vampire = getNull();
    public static final TotemTopBlock totem_top_vampirism_hunter = getNull();
    public static final VampirismFlowerBlock vampire_orchid = getNull();
    public static final WeaponTableBlock weapon_table = getNull();

    static void registerItemBlocks(IForgeRegistry<Item> registry) {
        registry.register(itemBlock(alchemical_cauldron));
        registry.register(itemBlock(altar_infusion));
        registry.register(itemBlock(altar_inspiration));
        registry.register(itemBlock(altar_pillar));
        registry.register(itemBlock(altar_tip));
        registry.register(itemBlock(blood_container, new Item.Properties().group(VampirismMod.creativeTab).maxStackSize(1)));
        registry.register(itemBlock(blood_grinder));
        registry.register(itemBlock(blood_potion_table));
        registry.register(itemBlock(blood_pedestal));
        registry.register(itemBlock(blood_sieve));
        registry.register(itemBlock(church_altar));
        registry.register(itemBlock(castle_block_dark_brick));
        registry.register(itemBlock(castle_block_dark_brick_bloody));
        registry.register(itemBlock(castle_block_dark_stone));
        registry.register(itemBlock(castle_block_normal_brick));
        registry.register(itemBlock(castle_block_purple_brick));
        registry.register(itemBlock(castle_stairs_dark_brick));
        registry.register(itemBlock(castle_stairs_dark_stone));
        registry.register(itemBlock(castle_stairs_purple_brick));
        registry.register(itemBlock(castle_slab_dark_brick));
        registry.register(itemBlock(castle_slab_dark_stone));
        registry.register(itemBlock(castle_slab_purple_brick));
        registry.register(itemBlock(coffin, new Item.Properties().group(VampirismMod.creativeTab).setISTER(() -> VampirismItemStackTESR::new)));
        registry.register(itemBlock(cursed_earth));
        registry.register(itemBlock(fire_place));
        registry.register(itemBlock(garlic_beacon_improved));
        registry.register(itemBlock(garlic_beacon_normal));
        registry.register(itemBlock(garlic_beacon_weak));
        registry.register(itemBlock(hunter_table));
        registry.register(itemBlock(sunscreen_beacon));
        registry.register(itemBlock(totem_base));
        registry.register(itemBlock(totem_top));
        registry.register(itemBlock(totem_top_vampirism_hunter, new Item.Properties()));
        registry.register(itemBlock(totem_top_vampirism_vampire, new Item.Properties()));
        registry.register(itemBlock(vampire_orchid));
        registry.register(itemBlock(weapon_table));
    }

    static void registerBlocks(IForgeRegistry<Block> registry) {
        CastleBricksBlock castle_block_dark_brick = new CastleBricksBlock(CastleBricksBlock.EnumVariant.DARK_BRICK);
        CastleBricksBlock castle_block_dark_stone = new CastleBricksBlock(CastleBricksBlock.EnumVariant.DARK_STONE);
        CastleBricksBlock castle_block_purple_brick = new CastleBricksBlock(CastleBricksBlock.EnumVariant.PURPLE_BRICK);
        VampirismFlowerBlock vampire_orchid = new VampirismFlowerBlock(VampirismFlowerBlock.TYPE.ORCHID);

        registry.register(new AlchemicalCauldronBlock());
        registry.register(new AlchemicalFireBlock());
        registry.register(new AltarInfusionBlock());
        registry.register(new AltarInspirationBlock());
        registry.register(new AltarPillarBlock());
        registry.register(new AltarTipBlock());
        registry.register(new BloodContainerBlock());
        registry.register(new GrinderBlock());
        registry.register(new PedestalBlock());
        registry.register(new BloodPotionTableBlock());
        registry.register(new SieveBlock());
        registry.register(castle_block_dark_brick);
        registry.register(new CastleBricksBlock(CastleBricksBlock.EnumVariant.DARK_BRICK_BLOODY));
        registry.register(castle_block_dark_stone);
        registry.register(new CastleBricksBlock(CastleBricksBlock.EnumVariant.NORMAL_BRICK));
        registry.register(castle_block_purple_brick);
        registry.register(new CastleSlabBlock(CastleBricksBlock.EnumVariant.DARK_BRICK));
        registry.register(new CastleSlabBlock(CastleBricksBlock.EnumVariant.DARK_STONE));
        registry.register(new CastleSlabBlock(CastleBricksBlock.EnumVariant.PURPLE_BRICK));
        registry.register(new CastleStairsBlock(castle_block_dark_brick.getDefaultState(), CastleBricksBlock.EnumVariant.DARK_BRICK));
        registry.register(new CastleStairsBlock(castle_block_dark_stone.getDefaultState(), CastleBricksBlock.EnumVariant.DARK_STONE));
        registry.register(new CastleStairsBlock(castle_block_purple_brick.getDefaultState(), CastleBricksBlock.EnumVariant.PURPLE_BRICK));
        registry.register(new ChurchAltarBlock());
        registry.register(new CoffinBlock());
        registry.register(new CursedEarthBlock());
        registry.register(new FirePlaceBlock());
        registry.register(new GarlicBlock());
        registry.register(new GarlicBeaconBlock(GarlicBeaconBlock.Type.IMPROVED));
        registry.register(new GarlicBeaconBlock(GarlicBeaconBlock.Type.NORMAL));
        registry.register(new GarlicBeaconBlock(GarlicBeaconBlock.Type.WEAK));
        registry.register(new HunterTableBlock());
        registry.register(new MedChairBlock());
        registry.register(new FlowerPotBlock(vampire_orchid, Block.Properties.create(Material.MISCELLANEOUS).zeroHardnessAndResistance()).setRegistryName(REFERENCE.MODID, "potted_vampire_orchid"));
        registry.register(new SunscreenBeaconBlock());
        registry.register(new TentBlock());
        registry.register(new TentMainBlock());
        registry.register(new TotemBaseBlock());
        registry.register(new TotemTopBlock());
        registry.register(new TotemTopBlock(REFERENCE.HUNTER_PLAYER_KEY));
        registry.register(new TotemTopBlock(REFERENCE.VAMPIRE_PLAYER_KEY));
        registry.register(vampire_orchid);
        registry.register(new WeaponTableBlock());
    }

    @Nonnull
    private static BlockItem itemBlock(@Nonnull Block block, @Nonnull Item.Properties props) {
        assert block != null;
        BlockItem item = new BlockItem(block, props);
        item.setRegistryName(block.getRegistryName());
        return item;
    }

    @Nonnull
    private static BlockItem itemBlock(@Nonnull Block block) {
        return itemBlock(block, new Item.Properties().group(VampirismMod.creativeTab));
    }
}
