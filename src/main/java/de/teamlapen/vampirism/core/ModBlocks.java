package de.teamlapen.vampirism.core;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.blocks.*;
import de.teamlapen.vampirism.items.CoffinItem;
import de.teamlapen.vampirism.util.BlockVoxelshapes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import java.util.Set;

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
    public static final AltarCleansingBlock altar_cleansing = getNull();
    public static final CoffinBlock coffin = getNull();
    public static final CursedEarthBlock cursed_earth = getNull();
    public static final FirePlaceBlock fire_place = getNull();
    public static final GarlicBlock garlic = getNull();
    public static final GarlicDiffuserBlock garlic_diffuser_improved = getNull();
    public static final GarlicDiffuserBlock garlic_diffuser_normal = getNull();
    public static final GarlicDiffuserBlock garlic_diffuser_weak = getNull();
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
    public static final TotemTopBlock totem_top_crafted = getNull();
    public static final TotemTopBlock totem_top_vampirism_vampire_crafted = getNull();
    public static final TotemTopBlock totem_top_vampirism_hunter_crafted = getNull();
    public static final VampirismFlowerBlock vampire_orchid = getNull();
    public static final WeaponTableBlock weapon_table = getNull();
    public static final PotionTableBlock potion_table = getNull();
    public static final RotatedPillarBlock bloody_spruce_log = getNull();
    public static final BloodySpruceLeavesBlock vampire_spruce_leaves = getNull();
    public static final BloodySpruceLeavesBlock bloody_spruce_leaves = getNull();
    public static final BloodySpruceSaplingBlock bloody_spruce_sapling = getNull();
    public static final VampireSpruceSaplingBlock vampire_spruce_sapling = getNull();
    public static final VampirismBlock chandelier = getNull();
    public static final VampirismBlock candelabra = getNull();
    public static final VampirismBlock candelabra_wall = getNull();
    public static final VampirismBlock cross = getNull();
    public static final VampirismBlock tombstone1 = getNull();
    public static final VampirismBlock tombstone2 = getNull();
    public static final VampirismBlock tombstone3 = getNull();
    public static final VampirismBlock grave_cage = getNull();

    /**
     * empty unless in datagen
     */
    private static final Set<Block> ALL_BLOCKS = Sets.newHashSet();

    static void registerItemBlocks(IForgeRegistry<Item> registry) {
        registry.register(itemBlock(alchemical_cauldron));
        registry.register(itemBlock(altar_infusion));
        registry.register(itemBlock(altar_inspiration));
        registry.register(itemBlock(altar_pillar));
        registry.register(itemBlock(altar_tip));
        registry.register(itemBlock(blood_container, new Item.Properties().tab(VampirismMod.creativeTab).stacksTo(1)));
        registry.register(itemBlock(blood_grinder));
        registry.register(itemBlock(blood_pedestal));
        registry.register(itemBlock(blood_sieve));
        registry.register(itemBlock(altar_cleansing));
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
        registry.register(new CoffinItem(coffin));
        registry.register(itemBlock(cursed_earth));
        registry.register(itemBlock(fire_place));
        registry.register(itemBlock(garlic_diffuser_improved));
        registry.register(itemBlock(garlic_diffuser_normal));
        registry.register(itemBlock(garlic_diffuser_weak));
        registry.register(itemBlock(hunter_table));
        registry.register(itemBlock(sunscreen_beacon, new Item.Properties().tab(VampirismMod.creativeTab).rarity(Rarity.EPIC)));
        registry.register(itemBlock(totem_base));
        registry.register(itemBlock(totem_top));
        registry.register(itemBlock(totem_top_crafted));
        registry.register(itemBlock(totem_top_vampirism_hunter, new Item.Properties()));
        registry.register(itemBlock(totem_top_vampirism_vampire, new Item.Properties()));
        registry.register(itemBlock(vampire_orchid));
        registry.register(itemBlock(weapon_table));
        registry.register(itemBlock(potion_table));
        registry.register(itemBlock(bloody_spruce_log));
        registry.register(itemBlock(vampire_spruce_leaves));
        registry.register(itemBlock(bloody_spruce_leaves));
        registry.register(itemBlock(bloody_spruce_sapling));
        registry.register(itemBlock(vampire_spruce_sapling));
        registry.register(itemBlock(chandelier));
        registry.register(itemBlock(cross));
        registry.register(itemBlock(tombstone1));
        registry.register(itemBlock(tombstone2));
        registry.register(itemBlock(tombstone3));
        registry.register(itemBlock(grave_cage));
    }

    static void registerBlocks(IForgeRegistry<Block> registry) {
        CastleBricksBlock castle_block_dark_brick = new CastleBricksBlock(CastleBricksBlock.EnumVariant.DARK_BRICK);
        CastleBricksBlock castle_block_dark_stone = new CastleBricksBlock(CastleBricksBlock.EnumVariant.DARK_STONE);
        CastleBricksBlock castle_block_purple_brick = new CastleBricksBlock(CastleBricksBlock.EnumVariant.PURPLE_BRICK);
        VampirismFlowerBlock vampire_orchid = new VampirismFlowerBlock(VampirismFlowerBlock.TYPE.ORCHID);

        registry.register(prepareRegister(new AlchemicalCauldronBlock()));
        registry.register(prepareRegister(new AlchemicalFireBlock()));
        registry.register(prepareRegister(new AltarInfusionBlock()));
        registry.register(prepareRegister(new AltarInspirationBlock()));
        registry.register(prepareRegister(new AltarPillarBlock()));
        registry.register(prepareRegister(new AltarTipBlock()));
        registry.register(prepareRegister(new BloodContainerBlock()));
        registry.register(prepareRegister(new GrinderBlock()));
        registry.register(prepareRegister(new PedestalBlock()));
        registry.register(prepareRegister(new SieveBlock()));
        registry.register(prepareRegister(castle_block_dark_brick));
        registry.register(prepareRegister(new CastleBricksBlock(CastleBricksBlock.EnumVariant.DARK_BRICK_BLOODY)));
        registry.register(prepareRegister(castle_block_dark_stone));
        registry.register(prepareRegister(new CastleBricksBlock(CastleBricksBlock.EnumVariant.NORMAL_BRICK)));
        registry.register(prepareRegister(castle_block_purple_brick));
        registry.register(prepareRegister(new CastleSlabBlock(CastleBricksBlock.EnumVariant.DARK_BRICK)));
        registry.register(prepareRegister(new CastleSlabBlock(CastleBricksBlock.EnumVariant.DARK_STONE)));
        registry.register(prepareRegister(new CastleSlabBlock(CastleBricksBlock.EnumVariant.PURPLE_BRICK)));
        registry.register(prepareRegister(new CastleStairsBlock(castle_block_dark_brick.defaultBlockState(), CastleBricksBlock.EnumVariant.DARK_BRICK)));
        registry.register(prepareRegister(new CastleStairsBlock(castle_block_dark_stone.defaultBlockState(), CastleBricksBlock.EnumVariant.DARK_STONE)));
        registry.register(prepareRegister(new CastleStairsBlock(castle_block_purple_brick.defaultBlockState(), CastleBricksBlock.EnumVariant.PURPLE_BRICK)));
        registry.register(prepareRegister(new AltarCleansingBlock()));
        registry.register(prepareRegister(new CoffinBlock()));
        registry.register(prepareRegister(new CursedEarthBlock()));
        registry.register(prepareRegister(new FirePlaceBlock()));
        registry.register(prepareRegister(new GarlicBlock()));
        registry.register(prepareRegister(new GarlicDiffuserBlock(GarlicDiffuserBlock.Type.IMPROVED)));
        registry.register(prepareRegister(new GarlicDiffuserBlock(GarlicDiffuserBlock.Type.NORMAL)));
        registry.register(prepareRegister(new GarlicDiffuserBlock(GarlicDiffuserBlock.Type.WEAK)));
        registry.register(prepareRegister(new HunterTableBlock()));
        registry.register(prepareRegister(new MedChairBlock()));
        registry.register(prepareRegister(new FlowerPotBlock(vampire_orchid, Block.Properties.of(Material.DECORATION).instabreak().noOcclusion()).setRegistryName(REFERENCE.MODID, "potted_vampire_orchid")));
        registry.register(prepareRegister(new SunscreenBeaconBlock()));
        registry.register(prepareRegister(new TentBlock()));
        registry.register(prepareRegister(new TentMainBlock()));
        registry.register(prepareRegister(new TotemBaseBlock()));
        registry.register(prepareRegister(new TotemTopBlock(false, new ResourceLocation("none")).setRegistryName(REFERENCE.MODID, "totem_top")));
        registry.register(prepareRegister(new TotemTopBlock(false, REFERENCE.HUNTER_PLAYER_KEY).setRegistryName(REFERENCE.MODID, "totem_top_vampirism_hunter")));
        registry.register(prepareRegister(new TotemTopBlock(false, REFERENCE.VAMPIRE_PLAYER_KEY).setRegistryName(REFERENCE.MODID, "totem_top_vampirism_vampire")));
        registry.register(prepareRegister(new TotemTopBlock(true, new ResourceLocation("none")).setRegistryName(REFERENCE.MODID, "totem_top_crafted")));
        registry.register(prepareRegister(new TotemTopBlock(true, REFERENCE.HUNTER_PLAYER_KEY).setRegistryName(REFERENCE.MODID, "totem_top_vampirism_hunter_crafted")));
        registry.register(prepareRegister(new TotemTopBlock(true, REFERENCE.VAMPIRE_PLAYER_KEY).setRegistryName(REFERENCE.MODID, "totem_top_vampirism_vampire_crafted")));
        registry.register(prepareRegister(vampire_orchid));
        registry.register(prepareRegister(new WeaponTableBlock()));
        registry.register(prepareRegister(new PotionTableBlock()));
        Block log = Blocks.log(MaterialColor.PODZOL, MaterialColor.COLOR_BROWN).setRegistryName(REFERENCE.MODID, "bloody_spruce_log");
        ((FireBlock) Blocks.FIRE).setFlammable(log, 5, 5);
        registry.register(prepareRegister(log));
        registry.register(prepareRegister(new BloodySpruceLeavesBlock("vampire_spruce_leaves")));
        registry.register(prepareRegister(new BloodySpruceLeavesBlock("bloody_spruce_leaves")));
        registry.register(prepareRegister(new VampireSpruceSaplingBlock().setRegistryName(REFERENCE.MODID, "vampire_spruce_sapling")));
        registry.register(prepareRegister(new BloodySpruceSaplingBlock().setRegistryName(REFERENCE.MODID, "bloody_spruce_sapling")));
        registry.register(prepareRegister(new ChandelierBlock()));
        registry.register(prepareRegister(new CandelabraWallBlock()));
        registry.register(prepareRegister(new CandelabraBlock()));
        registry.register(prepareRegister(new CrossBlock()));
        registry.register(prepareRegister(new VampirismHorizontalBlock("tombstone1", BlockBehaviour.Properties.of(Material.STONE).strength(2, 6), BlockVoxelshapes.tomb1).markDecorativeBlock()));
        registry.register(prepareRegister(new VampirismHorizontalBlock("tombstone2", BlockBehaviour.Properties.of(Material.STONE).strength(2, 6), BlockVoxelshapes.tomb2).markDecorativeBlock()));
        registry.register(prepareRegister(new VampirismHorizontalBlock("tombstone3", BlockBehaviour.Properties.of(Material.STONE).strength(2, 6), BlockVoxelshapes.tomb3).markDecorativeBlock()));
        registry.register(prepareRegister(new VampirismHorizontalBlock("grave_cage", BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).strength(6, 8).requiresCorrectToolForDrops().sound(SoundType.METAL), BlockVoxelshapes.grave_cage).markDecorativeBlock()));


        /*
         * TUTORIAL:
         * - Register block here.
         * - Register itemblock in {@link ModBlocks#registerItemBlocks(IForgeRegistry)}
         * - Maybe set render layer in {@link ModBlocksRender#registerRenderType()}
         * - Register blockstate in {@link BlockStateGenerator#registerStatesAndModels()} (pass existent model if desired)
         * - Register itemrender in {@link ItemModelGenerator#registerModels()}
         * - Register loot table in {@link LootTablesGenerator.ModBlockLootTables#addTables()}
         * - Add lang keys
         * - Consider adding tool type in {@link de.teamlapen.vampirism.data.TagGenerator.ModBlockTagsProvider}
         * - Run genData (twice?)
         */
    }

    private static Block prepareRegister(Block block) {
        if (VampirismMod.inDataGen) {
            ALL_BLOCKS.add(block);
        }
        return block;
    }

    @Nonnull
    private static BlockItem itemBlock(@Nonnull Block block, @Nonnull Item.Properties props) {
        BlockItem item = new BlockItem(block, props);
        item.setRegistryName(block.getRegistryName());
        return item;
    }

    @Nonnull
    private static BlockItem itemBlock(@Nonnull Block block) {
        return itemBlock(block, new Item.Properties().tab(VampirismMod.creativeTab));
    }

    public static void fixMappings(RegistryEvent.MissingMappings<Block> event) {
        event.getAllMappings().forEach(missingMapping -> {
            if ("vampirism:blood_potion_table".equals(missingMapping.key.toString())) {
                missingMapping.remap(ModBlocks.potion_table);
            } else if ("vampirism:garlic_beacon_normal".equals(missingMapping.key.toString())) {
                missingMapping.remap(ModBlocks.garlic_diffuser_normal);
            } else if ("vampirism:garlic_beacon_weak".equals(missingMapping.key.toString())) {
                missingMapping.remap(ModBlocks.garlic_diffuser_weak);
            } else if ("vampirism:garlic_beacon_improved".equals(missingMapping.key.toString())) {
                missingMapping.remap(ModBlocks.garlic_diffuser_improved);
            } else if("vampirism:church_altar".equals(missingMapping.key.toString())){
                missingMapping.remap(ModBlocks.altar_cleansing);
            }
        });
    }

    public static Set<Block> getAllBlocks() {
        return ImmutableSet.copyOf(ALL_BLOCKS);
    }
}
