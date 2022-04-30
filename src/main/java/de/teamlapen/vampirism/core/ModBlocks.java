package de.teamlapen.vampirism.core;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.blocks.BushBlock;
import de.teamlapen.vampirism.blocks.*;
import de.teamlapen.vampirism.client.core.ModBlocksRender;
import de.teamlapen.vampirism.client.render.VampirismItemStackTESR;
import de.teamlapen.vampirism.data.BlockStateGenerator;
import de.teamlapen.vampirism.data.ItemModelGenerator;
import de.teamlapen.vampirism.data.LootTablesGenerator;
import de.teamlapen.vampirism.util.BlockVoxelshapes;
import de.teamlapen.vampirism.world.gen.CursedSpruceTree;
import de.teamlapen.vampirism.world.gen.DarkSpruceTree;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
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
    public static final TotemTopBlock totem_top_crafted = getNull();
    public static final TotemTopBlock totem_top_vampirism_vampire_crafted = getNull();
    public static final TotemTopBlock totem_top_vampirism_hunter_crafted = getNull();
    public static final VampirismFlowerBlock vampire_orchid = getNull();
    public static final WeaponTableBlock weapon_table = getNull();
    public static final PotionTableBlock potion_table = getNull();
    public static final DarkSpruceLeavesBlock dark_spruce_leaves = getNull();
    public static final VampirismBlock chandelier = getNull();
    public static final VampirismBlock candelabra = getNull();
    public static final VampirismBlock candelabra_wall = getNull();
    public static final VampirismBlock cross = getNull();
    public static final VampirismBlock tombstone1 = getNull();
    public static final VampirismBlock tombstone2 = getNull();
    public static final VampirismBlock tombstone3 = getNull();
    public static final VampirismBlock grave_cage = getNull();
    public static final CursedGrass cursed_grass = getNull();
    public static final RotatedPillarBlock dark_spruce_log = getNull();
    public static final Block cursed_roots = getNull();
    public static final Block potted_cursed_roots = getNull();
    public static final RotatedPillarBlock cursed_spruce_log = getNull();
    public static final SaplingBlock dark_spruce_sapling = getNull();
    public static final SaplingBlock cursed_spruce_sapling = getNull();
    public static final CursedBarkBlock cursed_bark = getNull();
    public static final RotatedPillarBlock stripped_dark_spruce_log = getNull();
    public static final RotatedPillarBlock stripped_cursed_spruce_log = getNull();
    public static final Block dark_spruce_planks = getNull();
    public static final Block cursed_spruce_planks = getNull();
    public static final DoorBlock dark_spruce_door = getNull();
    public static final DoorBlock cursed_spruce_door = getNull();
    public static final TrapDoorBlock dark_spruce_trapdoor = getNull();
    public static final TrapDoorBlock cursed_spruce_trapdoor = getNull();
    public static final StairsBlock dark_spruce_stairs = getNull();
    public static final StairsBlock cursed_spruce_stairs = getNull();
    public static final LogBlock dark_spruce_wood = getNull();
    public static final LogBlock cursed_spruce_wood = getNull();
    public static final LogBlock stripped_dark_spruce_wood = getNull();
    public static final LogBlock stripped_cursed_spruce_wood = getNull();
    public static final StandingSignBlock dark_spruce_sign = getNull();
    public static final StandingSignBlock cursed_spruce_sign = getNull();
    public static final WallSignBlock dark_spruce_wall_sign = getNull();
    public static final WallSignBlock cursed_spruce_wall_sign = getNull();
    public static final PressurePlateBlock dark_spruce_pressure_place = getNull();
    public static final PressurePlateBlock cursed_spruce_pressure_place = getNull();
    public static final WoodButtonBlock dark_spruce_button = getNull();
    public static final WoodButtonBlock cursed_spruce_button = getNull();
    public static final SlabBlock dark_spruce_slab = getNull();
    public static final SlabBlock cursed_spruce_slab = getNull();
    public static final FenceGateBlock dark_spruce_fence_gate = getNull();
    public static final FenceGateBlock cursed_spruce_fence_gate = getNull();
    public static final FenceBlock dark_spruce_fence = getNull();
    public static final FenceBlock cursed_spruce_fence = getNull();
    public static final VampirismBlock vampire_rack = getNull();
    public static final VampirismBlock throne = getNull();
    public static final AlchemyTableBlock alchemy_table = getNull();

    /**
     * empty unless in datagen
     */
    private static final Set<Block> ALL_BLOCKS = Sets.newHashSet();
    private static Set<Block> ITEM_BLOCKS = Sets.newHashSet();

    static void registerItemBlocks(IForgeRegistry<Item> registry) {
        registry.register(itemBlock(blood_container, new Item.Properties().tab(VampirismMod.creativeTab).stacksTo(1)));
        registry.register(itemBlock(coffin, new Item.Properties().tab(VampirismMod.creativeTab).setISTER(() -> VampirismItemStackTESR::new)));
        registry.register(itemBlock(sunscreen_beacon, new Item.Properties().tab(VampirismMod.creativeTab).rarity(Rarity.EPIC)));
        registry.register(itemBlock(totem_top_vampirism_hunter, new Item.Properties()));
        registry.register(itemBlock(totem_top_vampirism_vampire, new Item.Properties()));
        ITEM_BLOCKS.forEach(block -> registry.register(itemBlock(block)));
        ITEM_BLOCKS = null;
    }

    static void registerBlocks(IForgeRegistry<Block> registry) {
        CastleBricksBlock castle_block_dark_brick = new CastleBricksBlock(CastleBricksBlock.EnumVariant.DARK_BRICK);
        CastleBricksBlock castle_block_dark_stone = new CastleBricksBlock(CastleBricksBlock.EnumVariant.DARK_STONE);
        CastleBricksBlock castle_block_purple_brick = new CastleBricksBlock(CastleBricksBlock.EnumVariant.PURPLE_BRICK);
        VampirismFlowerBlock vampire_orchid = new VampirismFlowerBlock(VampirismFlowerBlock.TYPE.ORCHID);

        registry.register(prepareItemRegistry(new AlchemicalCauldronBlock()));
        registry.register(prepareRegister(new AlchemicalFireBlock()));
        registry.register(prepareItemRegistry(new AltarInfusionBlock()));
        registry.register(prepareItemRegistry(new AltarInspirationBlock()));
        registry.register(prepareItemRegistry(new AltarPillarBlock()));
        registry.register(prepareItemRegistry(new AltarTipBlock()));
        registry.register(prepareRegister(new BloodContainerBlock()));
        registry.register(prepareItemRegistry(new GrinderBlock()));
        registry.register(prepareItemRegistry(new PedestalBlock()));
        registry.register(prepareItemRegistry(new SieveBlock()));
        registry.register(prepareItemRegistry(castle_block_dark_brick));
        registry.register(prepareItemRegistry(new CastleBricksBlock(CastleBricksBlock.EnumVariant.DARK_BRICK_BLOODY)));
        registry.register(prepareItemRegistry(castle_block_dark_stone));
        registry.register(prepareItemRegistry(new CastleBricksBlock(CastleBricksBlock.EnumVariant.NORMAL_BRICK)));
        registry.register(prepareItemRegistry(castle_block_purple_brick));
        registry.register(prepareItemRegistry(new CastleSlabBlock(CastleBricksBlock.EnumVariant.DARK_BRICK)));
        registry.register(prepareItemRegistry(new CastleSlabBlock(CastleBricksBlock.EnumVariant.DARK_STONE)));
        registry.register(prepareItemRegistry(new CastleSlabBlock(CastleBricksBlock.EnumVariant.PURPLE_BRICK)));
        registry.register(prepareItemRegistry(new CastleStairsBlock(castle_block_dark_brick.defaultBlockState(), CastleBricksBlock.EnumVariant.DARK_BRICK)));
        registry.register(prepareItemRegistry(new CastleStairsBlock(castle_block_dark_stone.defaultBlockState(), CastleBricksBlock.EnumVariant.DARK_STONE)));
        registry.register(prepareItemRegistry(new CastleStairsBlock(castle_block_purple_brick.defaultBlockState(), CastleBricksBlock.EnumVariant.PURPLE_BRICK)));
        registry.register(prepareItemRegistry(new ChurchAltarBlock()));
        registry.register(prepareRegister(new CoffinBlock()));
        registry.register(prepareItemRegistry(new CursedEarthBlock()));
        registry.register(prepareItemRegistry(new FirePlaceBlock()));
        registry.register(prepareRegister(new GarlicBlock()));
        registry.register(prepareItemRegistry(new GarlicBeaconBlock(GarlicBeaconBlock.Type.IMPROVED)));
        registry.register(prepareItemRegistry(new GarlicBeaconBlock(GarlicBeaconBlock.Type.NORMAL)));
        registry.register(prepareItemRegistry(new GarlicBeaconBlock(GarlicBeaconBlock.Type.WEAK)));
        registry.register(prepareItemRegistry(new HunterTableBlock()));
        registry.register(prepareItemRegistry(new MedChairBlock()));
        registry.register(prepareRegister(new FlowerPotBlock(vampire_orchid, Block.Properties.of(Material.DECORATION).instabreak().noOcclusion()).setRegistryName(REFERENCE.MODID, "potted_vampire_orchid")));
        registry.register(prepareRegister(new SunscreenBeaconBlock()));
        registry.register(prepareRegister(new TentBlock()));
        registry.register(prepareRegister(new TentMainBlock()));
        registry.register(prepareItemRegistry(new TotemBaseBlock()));
        registry.register(prepareItemRegistry(new TotemTopBlock(false, new ResourceLocation("none")).setRegistryName(REFERENCE.MODID, "totem_top")));
        registry.register(prepareRegister(new TotemTopBlock(false, REFERENCE.HUNTER_PLAYER_KEY).setRegistryName(REFERENCE.MODID, "totem_top_vampirism_hunter")));
        registry.register(prepareRegister(new TotemTopBlock(false, REFERENCE.VAMPIRE_PLAYER_KEY).setRegistryName(REFERENCE.MODID, "totem_top_vampirism_vampire")));
        registry.register(prepareItemRegistry(new TotemTopBlock(true, new ResourceLocation("none")).setRegistryName(REFERENCE.MODID, "totem_top_crafted")));
        registry.register(prepareRegister(new TotemTopBlock(true, REFERENCE.HUNTER_PLAYER_KEY).setRegistryName(REFERENCE.MODID, "totem_top_vampirism_hunter_crafted")));
        registry.register(prepareRegister(new TotemTopBlock(true, REFERENCE.VAMPIRE_PLAYER_KEY).setRegistryName(REFERENCE.MODID, "totem_top_vampirism_vampire_crafted")));
        registry.register(prepareItemRegistry(vampire_orchid));
        registry.register(prepareItemRegistry(new WeaponTableBlock()));
        registry.register(prepareItemRegistry(new PotionTableBlock()));
        registry.register(prepareItemRegistry(new DarkSpruceLeavesBlock("dark_spruce_leaves")));
        registry.register(prepareItemRegistry(new ChandelierBlock()));
        registry.register(prepareRegister(new CandelabraWallBlock()));
        registry.register(prepareRegister(new CandelabraBlock()));
        registry.register(prepareItemRegistry(new VampirismSplitBlock("cross", AbstractBlock.Properties.of(Material.WOOD).strength(2, 3), BlockVoxelshapes.crossBottom, BlockVoxelshapes.crossTop, true).markDecorativeBlock()));
        registry.register(prepareItemRegistry(new VampirismHorizontalBlock("tombstone1", AbstractBlock.Properties.of(Material.STONE).strength(2, 6), BlockVoxelshapes.tomb1).markDecorativeBlock()));
        registry.register(prepareItemRegistry(new VampirismHorizontalBlock("tombstone2", AbstractBlock.Properties.of(Material.STONE).strength(2, 6), BlockVoxelshapes.tomb2).markDecorativeBlock()));
        registry.register(prepareItemRegistry(new VampirismHorizontalBlock("tombstone3", AbstractBlock.Properties.of(Material.STONE).strength(2, 6), BlockVoxelshapes.tomb3).markDecorativeBlock()));
        registry.register(prepareItemRegistry(new VampirismHorizontalBlock("grave_cage", AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(6, 8).requiresCorrectToolForDrops().sound(SoundType.METAL), BlockVoxelshapes.grave_cage).markDecorativeBlock()));
        registry.register(prepareItemRegistry(new CursedGrass(AbstractBlock.Properties.of(Material.GRASS, MaterialColor.COLOR_BLACK).randomTicks().strength(0.6F).sound(SoundType.GRASS))).setRegistryName(REFERENCE.MODID, "cursed_grass"));
        Block bush = new BushBlock(AbstractBlock.Properties.of(Material.REPLACEABLE_PLANT, MaterialColor.COLOR_RED).noCollission().instabreak().sound(SoundType.GRASS)).setRegistryName(REFERENCE.MODID, "cursed_roots");
        ((FireBlock) Blocks.FIRE).setFlammable(bush, 60, 100);
        registry.register(prepareItemRegistry(bush));
        registry.register(prepareRegister(new FlowerPotBlock(bush, Block.Properties.of(Material.DECORATION).instabreak().noOcclusion()).setRegistryName(REFERENCE.MODID, "potted_cursed_roots")));

        Block dark_spruce_planks = new Block(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY).strength(2.0F, 3.0F).sound(SoundType.WOOD)).setRegistryName(REFERENCE.MODID, "dark_spruce_planks");
        Block cursed_spruce_planks = new Block(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.CRIMSON_HYPHAE).strength(2.0F, 3.0F).sound(SoundType.WOOD)).setRegistryName(REFERENCE.MODID, "cursed_spruce_planks");
        Block dark_spruce_log = new LogBlock(MaterialColor.COLOR_BLACK, MaterialColor.COLOR_BLACK).setRegistryName(REFERENCE.MODID, "dark_spruce_log");
        Block cursed_spruce_log = new CursedSpruceBlock().setRegistryName(REFERENCE.MODID, "cursed_spruce_log");
        registry.register(prepareItemRegistry(dark_spruce_log));
        registry.register(prepareItemRegistry(cursed_spruce_log));
        registry.register(prepareItemRegistry(new SaplingBlock(new DarkSpruceTree(), AbstractBlock.Properties.of(Material.PLANT, MaterialColor.COLOR_BLACK).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)).setRegistryName(REFERENCE.MODID,"dark_spruce_sapling")));
        registry.register(prepareItemRegistry(new SaplingBlock(new CursedSpruceTree(), AbstractBlock.Properties.of(Material.PLANT, MaterialColor.COLOR_BLACK).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)).setRegistryName(REFERENCE.MODID,"cursed_spruce_sapling")));
        registry.register(prepareItemRegistry(new CursedBarkBlock().setRegistryName(REFERENCE.MODID, "cursed_bark")));
        registry.register(prepareItemRegistry(new LogBlock(MaterialColor.COLOR_BLACK, MaterialColor.COLOR_GRAY).setRegistryName(REFERENCE.MODID, "stripped_dark_spruce_log")));
        registry.register(prepareItemRegistry(new LogBlock(MaterialColor.COLOR_BLACK, MaterialColor.CRIMSON_HYPHAE).setRegistryName(REFERENCE.MODID, "stripped_cursed_spruce_log")));
        registry.register(prepareItemRegistry(dark_spruce_planks));
        registry.register(prepareItemRegistry(cursed_spruce_planks));
        registry.register(prepareItemRegistry(new DoorBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY).strength(3.0F).sound(SoundType.WOOD).noOcclusion()).setRegistryName(REFERENCE.MODID, "dark_spruce_door")));
        registry.register(prepareItemRegistry(new DoorBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.CRIMSON_HYPHAE).strength(3.0F).sound(SoundType.WOOD).noOcclusion()).setRegistryName(REFERENCE.MODID, "cursed_spruce_door")));
        registry.register(prepareItemRegistry(new TrapDoorBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY).strength(3.0F).sound(SoundType.WOOD).noOcclusion().isValidSpawn(ModBlocks::never)).setRegistryName(REFERENCE.MODID, "dark_spruce_trapdoor")));
        registry.register(prepareItemRegistry(new TrapDoorBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.CRIMSON_HYPHAE).strength(3.0F).sound(SoundType.WOOD).noOcclusion().isValidSpawn(ModBlocks::never)).setRegistryName(REFERENCE.MODID, "cursed_spruce_trapdoor")));
        registry.register(prepareItemRegistry(new StairsBlock(dark_spruce_planks.defaultBlockState(), AbstractBlock.Properties.copy(dark_spruce_planks)).setRegistryName(REFERENCE.MODID, "dark_spruce_stairs")));
        registry.register(prepareItemRegistry(new StairsBlock(cursed_spruce_planks.defaultBlockState(), AbstractBlock.Properties.copy(cursed_spruce_planks)).setRegistryName(REFERENCE.MODID, "cursed_spruce_stairs")));
        registry.register(prepareItemRegistry(new LogBlock(MaterialColor.COLOR_BLACK, MaterialColor.COLOR_BLACK).setRegistryName(REFERENCE.MODID, "dark_spruce_wood")));
        registry.register(prepareItemRegistry(new CursedSpruceBlock().setRegistryName(REFERENCE.MODID, "cursed_spruce_wood")));
        registry.register(prepareItemRegistry(new LogBlock(MaterialColor.COLOR_BLACK, MaterialColor.COLOR_GRAY).setRegistryName(REFERENCE.MODID, "stripped_dark_spruce_wood")));
        registry.register(prepareItemRegistry(new LogBlock(MaterialColor.COLOR_BLACK, MaterialColor.CRIMSON_HYPHAE).setRegistryName(REFERENCE.MODID, "stripped_cursed_spruce_wood")));
        registry.register(prepareRegister(new StandingSignBlock(AbstractBlock.Properties.of(Material.WOOD, dark_spruce_log.defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundType.WOOD), LogBlock.dark_spruce).setRegistryName(REFERENCE.MODID, "dark_spruce_sign")));
        registry.register(prepareRegister(new StandingSignBlock(AbstractBlock.Properties.of(Material.WOOD, cursed_spruce_log.defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundType.WOOD), LogBlock.cursed_spruce).setRegistryName(REFERENCE.MODID, "cursed_spruce_sign")));
        registry.register(prepareRegister(new WallSignBlock(AbstractBlock.Properties.of(Material.WOOD, dark_spruce_log.defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundType.WOOD).lootFrom(() -> dark_spruce_sign), LogBlock.dark_spruce).setRegistryName(REFERENCE.MODID, "dark_spruce_wall_sign")));
        registry.register(prepareRegister(new WallSignBlock(AbstractBlock.Properties.of(Material.WOOD, cursed_spruce_log.defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundType.WOOD).lootFrom(() -> cursed_spruce_sign), LogBlock.dark_spruce).setRegistryName(REFERENCE.MODID, "cursed_spruce_wall_sign")));
        registry.register(prepareItemRegistry(new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, AbstractBlock.Properties.of(Material.WOOD, dark_spruce_planks.defaultMaterialColor()).noCollission().strength(0.5F).sound(SoundType.WOOD)).setRegistryName(REFERENCE.MODID, "dark_spruce_pressure_place")));
        registry.register(prepareItemRegistry(new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, AbstractBlock.Properties.of(Material.WOOD, cursed_spruce_planks.defaultMaterialColor()).noCollission().strength(0.5F).sound(SoundType.WOOD)).setRegistryName(REFERENCE.MODID, "cursed_spruce_pressure_place")));
        registry.register(prepareItemRegistry(new WoodButtonBlock(AbstractBlock.Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.WOOD)).setRegistryName(REFERENCE.MODID, "dark_spruce_button")));
        registry.register(prepareItemRegistry(new WoodButtonBlock(AbstractBlock.Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.WOOD)).setRegistryName(REFERENCE.MODID, "cursed_spruce_button")));
        registry.register(prepareItemRegistry(new SlabBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY).strength(2.0F, 3.0F).sound(SoundType.WOOD)).setRegistryName(REFERENCE.MODID, "dark_spruce_slab")));
        registry.register(prepareItemRegistry(new SlabBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.CRIMSON_HYPHAE).strength(2.0F, 3.0F).sound(SoundType.WOOD)).setRegistryName(REFERENCE.MODID, "cursed_spruce_slab")));
        registry.register(prepareItemRegistry(new FenceGateBlock(AbstractBlock.Properties.of(Material.WOOD, dark_spruce_planks.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)).setRegistryName(REFERENCE.MODID, "dark_spruce_fence_gate")));
        registry.register(prepareItemRegistry(new FenceGateBlock(AbstractBlock.Properties.of(Material.WOOD, cursed_spruce_planks.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)).setRegistryName(REFERENCE.MODID, "cursed_spruce_fence_gate")));
        registry.register(prepareItemRegistry(new FenceBlock(AbstractBlock.Properties.of(Material.WOOD, dark_spruce_planks.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)).setRegistryName(REFERENCE.MODID, "dark_spruce_fence")));
        registry.register(prepareItemRegistry(new FenceBlock(AbstractBlock.Properties.of(Material.WOOD, cursed_spruce_planks.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)).setRegistryName(REFERENCE.MODID, "cursed_spruce_fence")));
        registry.register(prepareItemRegistry(new VampirismHorizontalBlock("vampire_rack", AbstractBlock.Properties.of(Material.WOOD).strength(2, 3), BlockVoxelshapes.vampire_rack).markDecorativeBlock()));
        registry.register(prepareItemRegistry(new ThroneBlock()));
        registry.register(prepareItemRegistry(new AlchemyTableBlock()));

        /**
         * TUTORIAL:
         * - Register block here.
         * - Register itemblock in {@link ModBlocks#registerItemBlocks(IForgeRegistry)}
         * - Maybe set render layer in {@link ModBlocksRender#registerRenderType()}
         * - Register blockstate in {@link BlockStateGenerator#registerStatesAndModels()} (pass existent model if desired)
         * - Register itemrender in {@link ItemModelGenerator#registerModels()}
         * - Register loot table in {@link LootTablesGenerator.ModBlockLootTables#addTables()}
         * - Add lang keys
         * - Run genData (twice?)
         */
    }

    private static <T extends Block> T prepareRegister(T block) {
        if (VampirismMod.inDataGen) {
            ALL_BLOCKS.add(block);
        }
        return block;
    }

    private static <T extends Block> T prepareItemRegistry(T block) {
        ITEM_BLOCKS.add(block);
        return prepareRegister(block);
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

    private static Boolean never(BlockState p_235427_0_, IBlockReader p_235427_1_, BlockPos p_235427_2_, EntityType<?> p_235427_3_) {
        return false;
    }

    public static void fixMappings(RegistryEvent.MissingMappings<Block> event) {
        event.getAllMappings().forEach(missingMapping -> {
            if ("vampirism:blood_potion_table".equals(missingMapping.key.toString())) {
                missingMapping.remap(ModBlocks.potion_table);
            }else if ("vampirism:vampire_spruce_leaves".equals(missingMapping.key.toString()))  {
                missingMapping.remap(ModBlocks.dark_spruce_leaves);
            } else if ("vampirism:bloody_spruce_leaves".equals(missingMapping.key.toString())) {
                missingMapping.remap(ModBlocks.dark_spruce_leaves);
            } else if ("vampirism:bloody_spruce_log".equals(missingMapping.key.toString())) {
                missingMapping.remap(ModBlocks.cursed_spruce_log);
            }
        });
    }

    public static Set<Block> getAllBlocks() {
        return ImmutableSet.copyOf(ALL_BLOCKS);
    }
}
