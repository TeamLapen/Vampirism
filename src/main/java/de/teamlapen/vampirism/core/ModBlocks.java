package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.blocks.BushBlock;
import de.teamlapen.vampirism.blocks.*;
import de.teamlapen.vampirism.client.core.ModBlocksRender;
import de.teamlapen.vampirism.items.CoffinBlockItem;
import de.teamlapen.vampirism.util.BlockVoxelshapes;
import de.teamlapen.vampirism.world.gen.CursedSpruceTree;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Handles all block registrations and reference.
 */
@SuppressWarnings("unused")
public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, REFERENCE.MODID);


    public static final RegistryObject<AlchemicalCauldronBlock> ALCHEMICAL_CAULDRON = registerWithItem("alchemical_cauldron", AlchemicalCauldronBlock::new);
    public static final RegistryObject<AlchemicalFireBlock> ALCHEMICAL_FIRE = BLOCKS.register("alchemical_fire", AlchemicalFireBlock::new);
    public static final RegistryObject<AltarInfusionBlock> ALTAR_INFUSION = registerWithItem("altar_infusion", AltarInfusionBlock::new);
    public static final RegistryObject<AltarInspirationBlock> ALTAR_INSPIRATION = registerWithItem("altar_inspiration", AltarInspirationBlock::new);
    public static final RegistryObject<AltarPillarBlock> ALTAR_PILLAR = registerWithItem("altar_pillar", AltarPillarBlock::new);
    public static final RegistryObject<AltarTipBlock> ALTAR_TIP = registerWithItem("altar_tip", AltarTipBlock::new);
    public static final RegistryObject<BloodContainerBlock> BLOOD_CONTAINER = registerWithItem("blood_container", BloodContainerBlock::new, block -> itemBlock(block, new Item.Properties().tab(VampirismMod.creativeTab).stacksTo(1)));
    public static final RegistryObject<GrinderBlock> BLOOD_GRINDER = registerWithItem("blood_grinder", GrinderBlock::new);
    public static final RegistryObject<PedestalBlock> BLOOD_PEDESTAL = registerWithItem("blood_pedestal", PedestalBlock::new);
    public static final RegistryObject<SieveBlock> BLOOD_SIEVE = registerWithItem("blood_sieve", SieveBlock::new);
    public static final RegistryObject<CastleBricksBlock> CASTLE_BLOCK_DARK_BRICK = registerWithItem("castle_block_dark_brick", () -> new CastleBricksBlock(CastleBricksBlock.EnumVariant.DARK_BRICK));
    public static final RegistryObject<CastleBricksBlock> CASTLE_BLOCK_DARK_BRICK_BLOODY = registerWithItem("castle_block_dark_brick_bloody", () -> new CastleBricksBlock(CastleBricksBlock.EnumVariant.DARK_BRICK_BLOODY));
    public static final RegistryObject<CastleBricksBlock> CASTLE_BLOCK_DARK_STONE = registerWithItem("castle_block_dark_stone", () -> new CastleBricksBlock(CastleBricksBlock.EnumVariant.DARK_STONE));
    public static final RegistryObject<CastleBricksBlock> CASTLE_BLOCK_NORMAL_BRICK = registerWithItem("castle_block_normal_brick", () -> new CastleBricksBlock(CastleBricksBlock.EnumVariant.NORMAL_BRICK));
    public static final RegistryObject<CastleBricksBlock> CASTLE_BLOCK_PURPLE_BRICK = registerWithItem("castle_block_purple_brick", () -> new CastleBricksBlock(CastleBricksBlock.EnumVariant.PURPLE_BRICK));
    public static final RegistryObject<CastleSlabBlock> CASTLE_SLAB_DARK_BRICK = registerWithItem("castle_slab_dark_brick", () -> new CastleSlabBlock(CastleBricksBlock.EnumVariant.DARK_BRICK));
    public static final RegistryObject<CastleSlabBlock> CASTLE_SLAB_DARK_STONE = registerWithItem("castle_slab_dark_stone", () -> new CastleSlabBlock(CastleBricksBlock.EnumVariant.DARK_STONE));
    public static final RegistryObject<CastleSlabBlock> CASTLE_SLAB_PURPLE_BRICK = registerWithItem("castle_slab_purple_brick", () -> new CastleSlabBlock(CastleBricksBlock.EnumVariant.PURPLE_BRICK));
    public static final RegistryObject<CastleStairsBlock> CASTLE_STAIRS_DARK_BRICK = registerWithItem("castle_stairs_dark_brick", () -> new CastleStairsBlock(()->CASTLE_BLOCK_DARK_BRICK.get().defaultBlockState(), CastleBricksBlock.EnumVariant.DARK_BRICK));
    public static final RegistryObject<CastleStairsBlock> CASTLE_STAIRS_DARK_STONE = registerWithItem("castle_stairs_dark_stone", () -> new CastleStairsBlock(()->CASTLE_BLOCK_DARK_STONE.get().defaultBlockState(), CastleBricksBlock.EnumVariant.DARK_STONE));
    public static final RegistryObject<CastleStairsBlock> CASTLE_STAIRS_PURPLE_BRICK = registerWithItem("castle_stairs_purple_brick", () -> new CastleStairsBlock(()->CASTLE_BLOCK_PURPLE_BRICK.get().defaultBlockState(), CastleBricksBlock.EnumVariant.PURPLE_BRICK));
    public static final RegistryObject<ChurchAltarBlock> CHURCH_ALTAR = registerWithItem("church_altar", ChurchAltarBlock::new);
    public static final RegistryObject<CoffinBlock> COFFIN_WHITE = registerWithItem("coffin_white", () -> new CoffinBlock(DyeColor.WHITE), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_ORANGE = registerWithItem("coffin_orange", () -> new CoffinBlock(DyeColor.ORANGE), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_MAGENTA = registerWithItem("coffin_magenta", () -> new CoffinBlock(DyeColor.MAGENTA), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_LIGHT_BLUE = registerWithItem("coffin_light_blue", () -> new CoffinBlock(DyeColor.LIGHT_BLUE), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_YELLOW = registerWithItem("coffin_yellow", () -> new CoffinBlock(DyeColor.YELLOW), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_LIME = registerWithItem("coffin_lime", () -> new CoffinBlock(DyeColor.LIME), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_PINK = registerWithItem("coffin_pink", () -> new CoffinBlock(DyeColor.PINK), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_GRAY = registerWithItem("coffin_gray", () -> new CoffinBlock(DyeColor.GRAY), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_LIGHT_GRAY = registerWithItem("coffin_light_gray", () -> new CoffinBlock(DyeColor.LIGHT_GRAY), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_CYAN = registerWithItem("coffin_cyan", () -> new CoffinBlock(DyeColor.CYAN), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_PURPLE = registerWithItem("coffin_purple", () -> new CoffinBlock(DyeColor.PURPLE), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_BLUE = registerWithItem("coffin_blue", () -> new CoffinBlock(DyeColor.BLUE), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_BROWN = registerWithItem("coffin_brown", () -> new CoffinBlock(DyeColor.BROWN), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_GREEN = registerWithItem("coffin_green", () -> new CoffinBlock(DyeColor.GREEN), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_RED = registerWithItem("coffin_red", () -> new CoffinBlock(DyeColor.RED), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_BLACK = registerWithItem("coffin_black", () -> new CoffinBlock(DyeColor.BLACK), CoffinBlockItem::new);
    public static final RegistryObject<CursedEarthBlock> CURSED_EARTH = registerWithItem("cursed_earth", CursedEarthBlock::new);
    public static final RegistryObject<FirePlaceBlock> FIRE_PLACE = registerWithItem("fire_place", FirePlaceBlock::new);
    public static final RegistryObject<GarlicBlock> GARLIC = BLOCKS.register("garlic", GarlicBlock::new);
    public static final RegistryObject<GarlicBeaconBlock> GARLIC_BEACON_IMPROVED = registerWithItem("garlic_beacon_improved", () -> new GarlicBeaconBlock(GarlicBeaconBlock.Type.IMPROVED));
    public static final RegistryObject<GarlicBeaconBlock> GARLIC_BEACON_NORMAL = registerWithItem("garlic_beacon_normal", () -> new GarlicBeaconBlock(GarlicBeaconBlock.Type.NORMAL));
    public static final RegistryObject<GarlicBeaconBlock> GARLIC_BEACON_WEAK = registerWithItem("garlic_beacon_weak", () -> new GarlicBeaconBlock(GarlicBeaconBlock.Type.WEAK));
    public static final RegistryObject<HunterTableBlock> HUNTER_TABLE = registerWithItem("hunter_table", HunterTableBlock::new);
    public static final RegistryObject<MedChairBlock> MED_CHAIR = registerWithItem("med_chair", MedChairBlock::new);
    public static final RegistryObject<VampirismFlowerBlock> VAMPIRE_ORCHID = registerWithItem("vampire_orchid", () -> new VampirismFlowerBlock(VampirismFlowerBlock.TYPE.ORCHID));
    public static final RegistryObject<FlowerPotBlock> POTTED_VAMPIRE_ORCHID = BLOCKS.register("potted_vampire_orchid", () -> new FlowerPotBlock(()-> (FlowerPotBlock) Blocks.FLOWER_POT, VAMPIRE_ORCHID, Block.Properties.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final RegistryObject<SunscreenBeaconBlock> SUNSCREEN_BEACON = registerWithItem("sunscreen_beacon", SunscreenBeaconBlock::new, block -> itemBlock(block, new Item.Properties().tab(VampirismMod.creativeTab).rarity(Rarity.EPIC)));
    public static final RegistryObject<TentBlock> TENT = BLOCKS.register("tent", TentBlock::new);
    public static final RegistryObject<TentMainBlock> TENT_MAIN = BLOCKS.register("tent_main", TentMainBlock::new);
    public static final RegistryObject<TotemBaseBlock> TOTEM_BASE = registerWithItem("totem_base", TotemBaseBlock::new);
    public static final RegistryObject<TotemTopBlock> TOTEM_TOP = registerWithItem("totem_top", () -> new TotemTopBlock(false, new ResourceLocation("none")));
    public static final RegistryObject<TotemTopBlock> TOTEM_TOP_VAMPIRISM_VAMPIRE = registerWithItem("totem_top_vampirism_vampire", () -> new TotemTopBlock(false, REFERENCE.VAMPIRE_PLAYER_KEY), block -> itemBlock(block, new Item.Properties()));
    public static final RegistryObject<TotemTopBlock> TOTEM_TOP_VAMPIRISM_HUNTER = registerWithItem("totem_top_vampirism_hunter", () -> new TotemTopBlock(false, REFERENCE.HUNTER_PLAYER_KEY), block -> itemBlock(block, new Item.Properties()));
    public static final RegistryObject<TotemTopBlock> TOTEM_TOP_CRAFTED = registerWithItem("totem_top_crafted", () -> new TotemTopBlock(true, new ResourceLocation("none")));
    public static final RegistryObject<TotemTopBlock> TOTEM_TOP_VAMPIRISM_VAMPIRE_CRAFTED = BLOCKS.register("totem_top_vampirism_vampire_crafted", () -> new TotemTopBlock(true, REFERENCE.VAMPIRE_PLAYER_KEY));
    public static final RegistryObject<TotemTopBlock> TOTEM_TOP_VAMPIRISM_HUNTER_CRAFTED = BLOCKS.register("totem_top_vampirism_hunter_crafted", () -> new TotemTopBlock(true, REFERENCE.HUNTER_PLAYER_KEY));
    public static final RegistryObject<WeaponTableBlock> WEAPON_TABLE = registerWithItem("weapon_table", WeaponTableBlock::new);
    public static final RegistryObject<PotionTableBlock> POTION_TABLE = registerWithItem("potion_table", PotionTableBlock::new);
    public static final RegistryObject<DarkSpruceLeavesBlock> DARK_SPRUCE_LEAVES = registerWithItem("dark_spruce_leaves", DarkSpruceLeavesBlock::new);
    public static final RegistryObject<VampirismBlock> CHANDELIER = registerWithItem("chandelier", ChandelierBlock::new);
    public static final RegistryObject<VampirismBlock> CANDELABRA = BLOCKS.register("candelabra", CandelabraBlock::new);
    public static final RegistryObject<VampirismBlock> CANDELABRA_WALL = BLOCKS.register("candelabra_wall", CandelabraWallBlock::new);
    public static final RegistryObject<VampirismBlock> CROSS = registerWithItem("cross", () -> new VampirismSplitBlock(AbstractBlock.Properties.of(Material.WOOD).strength(2, 3), BlockVoxelshapes.crossBottom, BlockVoxelshapes.crossTop, true).markDecorativeBlock());
    public static final RegistryObject<VampirismBlock> TOMBSTONE1 = registerWithItem("tombstone1", () -> new VampirismHorizontalBlock(AbstractBlock.Properties.of(Material.STONE).strength(2, 6), BlockVoxelshapes.tomb1).markDecorativeBlock());
    public static final RegistryObject<VampirismBlock> TOMBSTONE2 = registerWithItem("tombstone2", () -> new VampirismHorizontalBlock(AbstractBlock.Properties.of(Material.STONE).strength(2, 6), BlockVoxelshapes.tomb2).markDecorativeBlock());
    public static final RegistryObject<VampirismBlock> TOMBSTONE3 = registerWithItem("tombstone3", () -> new VampirismHorizontalBlock(AbstractBlock.Properties.of(Material.STONE).strength(2, 6), BlockVoxelshapes.tomb3).markDecorativeBlock());
    public static final RegistryObject<VampirismBlock> GRAVE_CAGE = registerWithItem("grave_cage", () -> new VampirismHorizontalBlock(AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(6, 8).requiresCorrectToolForDrops().sound(SoundType.METAL), BlockVoxelshapes.grave_cage).markDecorativeBlock());
    public static final RegistryObject<CursedGrass> CURSED_GRASS = registerWithItem("cursed_grass", () -> new CursedGrass(AbstractBlock.Properties.of(Material.GRASS, MaterialColor.COLOR_BLACK).randomTicks().strength(0.6F).sound(SoundType.GRASS)));
    public static final RegistryObject<LogBlock> STRIPPED_DARK_SPRUCE_LOG = registerWithItem("stripped_dark_spruce_log", () -> new LogBlock(MaterialColor.COLOR_BLACK, MaterialColor.COLOR_GRAY));
    public static final RegistryObject<RotatedPillarBlock> DARK_SPRUCE_LOG = registerWithItem("dark_spruce_log", () -> new StrippableLogBlock(MaterialColor.COLOR_BLACK, MaterialColor.COLOR_BLACK, STRIPPED_DARK_SPRUCE_LOG));
    public static final RegistryObject<Block> CURSED_ROOTS = registerWithItem("cursed_roots", flammable(()->new BushBlock(AbstractBlock.Properties.of(Material.REPLACEABLE_PLANT, MaterialColor.COLOR_RED).noCollission().instabreak().sound(SoundType.GRASS)), 60, 100));
    public static final RegistryObject<Block> POTTED_CURSED_ROOTS = BLOCKS.register("potted_cursed_roots", () -> new FlowerPotBlock(()-> (FlowerPotBlock) Blocks.FLOWER_POT, CURSED_ROOTS, Block.Properties.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final RegistryObject<LogBlock> STRIPPED_CURSED_SPRUCE_LOG = registerWithItem("stripped_cursed_spruce_log", () -> new LogBlock(MaterialColor.COLOR_BLACK, MaterialColor.CRIMSON_HYPHAE));
    public static final RegistryObject<RotatedPillarBlock> CURSED_SPRUCE_LOG = registerWithItem("cursed_spruce_log", () -> new CursedSpruceBlock(STRIPPED_CURSED_SPRUCE_LOG));
    public static final RegistryObject<SaplingBlock> DARK_SPRUCE_SAPLING = registerWithItem("dark_spruce_sapling", DarkSpruceSaplingBlock::new);
    public static final RegistryObject<SaplingBlock> CURSED_SPRUCE_SAPLING = registerWithItem("cursed_spruce_sapling", () -> new SaplingBlock(new CursedSpruceTree(), AbstractBlock.Properties.of(Material.PLANT, MaterialColor.COLOR_BLACK).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)));
    public static final RegistryObject<CursedBarkBlock> CURSED_BARK = registerWithItem("cursed_bark", CursedBarkBlock::new);
    public static final RegistryObject<Block> DARK_SPRUCE_PLANKS = registerWithItem("dark_spruce_planks", () -> new Block(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> CURSED_SPRUCE_PLANKS = registerWithItem("cursed_spruce_planks", () -> new Block(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.CRIMSON_HYPHAE).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<DoorBlock> DARK_SPRUCE_DOOR = registerWithItem("dark_spruce_door", () -> new DoorBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final RegistryObject<DoorBlock> CURSED_SPRUCE_DOOR = registerWithItem("cursed_spruce_door", () -> new DoorBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.CRIMSON_HYPHAE).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final RegistryObject<TrapDoorBlock> DARK_SPRUCE_TRAPDOOR = registerWithItem("dark_spruce_trapdoor", () -> new TrapDoorBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final RegistryObject<TrapDoorBlock> CURSED_SPRUCE_TRAPDOOR = registerWithItem("cursed_spruce_trapdoor", () -> new TrapDoorBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.CRIMSON_HYPHAE).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final RegistryObject<StairsBlock> DARK_SPRUCE_STAIRS = registerWithItem("dark_spruce_stairs", () -> new StairsBlock(()->DARK_SPRUCE_PLANKS.get().defaultBlockState(), AbstractBlock.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final RegistryObject<StairsBlock> CURSED_SPRUCE_STAIRS = registerWithItem("cursed_spruce_stairs", () -> new StairsBlock(()->CURSED_SPRUCE_PLANKS.get().defaultBlockState(), AbstractBlock.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final RegistryObject<LogBlock> STRIPPED_DARK_SPRUCE_WOOD = registerWithItem("stripped_dark_spruce_wood", () -> new LogBlock(MaterialColor.COLOR_BLACK, MaterialColor.COLOR_GRAY));
    public static final RegistryObject<LogBlock> DARK_SPRUCE_WOOD = registerWithItem("dark_spruce_wood", () -> new StrippableLogBlock(MaterialColor.COLOR_BLACK, MaterialColor.COLOR_BLACK, STRIPPED_DARK_SPRUCE_WOOD));
    public static final RegistryObject<LogBlock> STRIPPED_CURSED_SPRUCE_WOOD = registerWithItem("stripped_cursed_spruce_wood", () -> new LogBlock(MaterialColor.COLOR_BLACK, MaterialColor.CRIMSON_HYPHAE));
    public static final RegistryObject<LogBlock> CURSED_SPRUCE_WOOD = registerWithItem("cursed_spruce_wood", () -> new CursedSpruceBlock(STRIPPED_CURSED_SPRUCE_WOOD));
    public static final RegistryObject<StandingSignBlock> DARK_SPRUCE_SIGN = BLOCKS.register("dark_spruce_sign", () -> new StandingSignBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK).noCollission().strength(1.0F).sound(SoundType.WOOD), LogBlock.dark_spruce));
    public static final RegistryObject<StandingSignBlock> CURSED_SPRUCE_SIGN = BLOCKS.register("cursed_spruce_sign", () -> new StandingSignBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.CRIMSON_HYPHAE).noCollission().strength(1.0F).sound(SoundType.WOOD), LogBlock.cursed_spruce));
    public static final RegistryObject<WallSignBlock> DARK_SPRUCE_WALL_SIGN = BLOCKS.register("dark_spruce_wall_sign", () -> new WallSignBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK).noCollission().strength(1.0F).sound(SoundType.WOOD).lootFrom(DARK_SPRUCE_SIGN), LogBlock.dark_spruce));
    public static final RegistryObject<WallSignBlock> CURSED_SPRUCE_WALL_SIGN = BLOCKS.register("cursed_spruce_wall_sign", () -> new WallSignBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.CRIMSON_HYPHAE).noCollission().strength(1.0F).sound(SoundType.WOOD).lootFrom(CURSED_SPRUCE_SIGN), LogBlock.cursed_spruce));
    public static final RegistryObject<PressurePlateBlock> DARK_SPRUCE_PRESSURE_PLACE = registerWithItem("dark_spruce_pressure_place", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, AbstractBlock.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK).noCollission().strength(0.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<PressurePlateBlock> CURSED_SPRUCE_PRESSURE_PLACE = registerWithItem("cursed_spruce_pressure_place", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, AbstractBlock.Properties.of(Material.WOOD, MaterialColor.CRIMSON_HYPHAE).noCollission().strength(0.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<WoodButtonBlock> DARK_SPRUCE_BUTTON = registerWithItem("dark_spruce_button", () -> new WoodButtonBlock(AbstractBlock.Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<WoodButtonBlock> CURSED_SPRUCE_BUTTON = registerWithItem("cursed_spruce_button", () -> new WoodButtonBlock(AbstractBlock.Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<SlabBlock> DARK_SPRUCE_SLAB = registerWithItem("dark_spruce_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<SlabBlock> CURSED_SPRUCE_SLAB = registerWithItem("cursed_spruce_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.CRIMSON_HYPHAE).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<FenceGateBlock> DARK_SPRUCE_FENCE_GATE = registerWithItem("dark_spruce_fence_gate", () -> new FenceGateBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<FenceGateBlock> CURSED_SPRUCE_FENCE_GATE = registerWithItem("cursed_spruce_fence_gate", () -> new FenceGateBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.CRIMSON_HYPHAE).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<FenceBlock> DARK_SPRUCE_FENCE = registerWithItem("dark_spruce_fence", () -> new FenceBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<FenceBlock> CURSED_SPRUCE_FENCE = registerWithItem("cursed_spruce_fence", () -> new FenceBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.CRIMSON_HYPHAE).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<VampirismBlock> VAMPIRE_RACK = registerWithItem("vampire_rack", () -> new VampirismHorizontalBlock(AbstractBlock.Properties.of(Material.WOOD).strength(2, 3), BlockVoxelshapes.vampire_rack).markDecorativeBlock());
    public static final RegistryObject<VampirismBlock> THRONE = registerWithItem("throne", ThroneBlock::new);
    public static final RegistryObject<AlchemyTableBlock> ALCHEMY_TABLE = registerWithItem("alchemy_table", AlchemyTableBlock::new);

    /**
     * TUTORIAL:
     * - Register blocks here.
     * - To register itemblock, use {@link ModBlocks#registerWithItem}
     * - Maybe set render layer in {@link ModBlocksRender#registerRenderType()}
     * - Register blockstate in {@link de.teamlapen.vampirism.data.BlockStateGenerator#registerStatesAndModels()} (pass existent model if desired)
     * - Register itemrender in {@link de.teamlapen.vampirism.data.ItemModelGenerator#registerModels()}
     * - Register loot table in {@link de.teamlapen.vampirism.data.LootTablesGenerator.ModBlockLootTables#addTables()}
     * - Add lang keys
     * - Consider adding tool type in {@link de.teamlapen.vampirism.data.TagGenerator.ModBlockTagsProvider}
     * - Run genData (twice?)
     */

    static void registerBlocks(IEventBus bus) {
        BLOCKS.register(bus);
    }

    @Nonnull
    private static BlockItem itemBlock(@Nonnull Block block, @Nonnull Item.Properties props) {
        return new BlockItem(block, props);
    }

    @Nonnull
    private static BlockItem itemBlock(@Nonnull Block block) {
        return itemBlock(block, new Item.Properties().tab(VampirismMod.creativeTab));
    }

    public static void fixMappings(RegistryEvent.MissingMappings<Block> event) {
        event.getAllMappings().forEach(missingMapping -> {
            if ("vampirism:blood_potion_table".equals(missingMapping.key.toString())) {
                missingMapping.remap(ModBlocks.POTION_TABLE.get());
            }else if ("vampirism:vampire_spruce_leaves".equals(missingMapping.key.toString()))  {
                missingMapping.remap(ModBlocks.DARK_SPRUCE_LEAVES.get());
            } else if ("vampirism:bloody_spruce_leaves".equals(missingMapping.key.toString())) {
                missingMapping.remap(ModBlocks.DARK_SPRUCE_LEAVES.get());
            } else if ("vampirism:bloody_spruce_log".equals(missingMapping.key.toString())) {
                missingMapping.remap(ModBlocks.CURSED_SPRUCE_LOG.get());
            }
        });
    }

    private static <T extends Block> RegistryObject<T> registerWithItem(String name, Supplier<T> supplier, Item.Properties properties) {
        RegistryObject<T> block = BLOCKS.register(name, supplier);
        ModItems.ITEMS.register(name, ()->new BlockItem(block.get(), properties));
        return block;
    }

    private static <T extends Block, R extends Item> RegistryObject<T> registerWithItem(String name, Supplier<T> supplier, Function<T, R> itemMaker) {
        RegistryObject<T> block = BLOCKS.register(name, supplier);
        ModItems.ITEMS.register(name, ()->itemMaker.apply(block.get()));
        return block;
    }

    private static <T extends Block> RegistryObject<T> registerWithItem(String name, Supplier<T> supplier) {
        return registerWithItem(name, supplier, new Item.Properties().tab(VampirismMod.creativeTab));
    }

    private static <T extends Block> Supplier<T> flammable(Supplier<T> supplier, int i1, int i2) {
        return () -> {
            T block = supplier.get();
            ((FireBlock) Blocks.FIRE).setFlammable(block, i1,i2);
            return block;
        };
    }
    public static Set<Block> getAllBlocks() {
        return BLOCKS.getEntries().stream().map(RegistryObject::get).collect(Collectors.toSet());
    }
}
