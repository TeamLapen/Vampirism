package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.ModRegistryItems;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.blocks.CursedRootsBlock;
import de.teamlapen.vampirism.blocks.*;
import de.teamlapen.vampirism.blocks.candle.*;
import de.teamlapen.vampirism.blocks.diffuser.FogDiffuserBlock;
import de.teamlapen.vampirism.blocks.diffuser.GarlicDiffuserBlock;
import de.teamlapen.vampirism.blocks.mother.ActiveVulnerableRemainsBlock;
import de.teamlapen.vampirism.blocks.mother.MotherBlock;
import de.teamlapen.vampirism.blocks.mother.RemainsBlock;
import de.teamlapen.vampirism.items.PureLevelBlockItem;
import de.teamlapen.vampirism.items.component.PureLevel;
import de.teamlapen.vampirism.util.BlockVoxelshapes;
import de.teamlapen.vampirism.world.gen.ModTreeGrower;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.teamlapen.lib.lib.util.RegisterHelper.flammable;

/**
 * Handles all block registrations and reference.
 */
@SuppressWarnings("unused")
public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(REFERENCE.MODID);

    public static final DeferredBlock<AlchemicalCauldronBlock> ALCHEMICAL_CAULDRON = registerWithItem("alchemical_cauldron", (props) -> new AlchemicalCauldronBlock(props.mapColor(MapColor.METAL).strength(4f).lightLevel(litBlockEmission(13)).noOcclusion()));
    public static final DeferredBlock<AlchemicalFireBlock> ALCHEMICAL_FIRE = BLOCKS.registerBlock("alchemical_fire", AlchemicalFireBlock::new);
    public static final DeferredBlock<AltarInfusionBlock> ALTAR_INFUSION = registerWithItem("altar_infusion", (prop) -> new AltarInfusionBlock(prop.mapColor(MapColor.STONE).strength(5).noOcclusion()));
    public static final DeferredBlock<AltarInspirationBlock> ALTAR_INSPIRATION = registerWithItem("altar_inspiration", (prop) -> new AltarInspirationBlock(prop.mapColor(MapColor.METAL).strength(2f, 3f).noOcclusion()));
    public static final DeferredBlock<AltarPillarBlock> ALTAR_PILLAR = registerWithItem("altar_pillar", AltarPillarBlock::new);
    public static final DeferredBlock<AltarTipBlock> ALTAR_TIP = registerWithItem("altar_tip", AltarTipBlock::new);
    public static final DeferredBlock<BloodContainerBlock> BLOOD_CONTAINER = registerWithItem("blood_container", (prop) -> new BloodContainerBlock(prop.strength(1f).isViewBlocking(UtilLib::never).noOcclusion()), x -> x.stacksTo(1));
    public static final DeferredBlock<GrinderBlock> BLOOD_GRINDER = registerWithItem("blood_grinder", (prop) -> new GrinderBlock(prop.mapColor(MapColor.METAL).strength(5).sound(SoundType.METAL).noOcclusion()));
    public static final DeferredBlock<PedestalBlock> BLOOD_PEDESTAL = registerWithItem("blood_pedestal", (prop) -> new PedestalBlock(prop.mapColor(MapColor.STONE).strength(3f).noOcclusion()));
    public static final DeferredBlock<SieveBlock> BLOOD_SIEVE = registerWithItem("blood_sieve", (prop) -> new SieveBlock(prop.mapColor(MapColor.WOOD).ignitedByLava().strength(2.5f).sound(SoundType.WOOD).noOcclusion()));
    public static final DeferredBlock<AltarCleansingBlock> ALTAR_CLEANSING = registerWithItem("altar_cleansing", AltarCleansingBlock::new);
    public static final DeferredBlock<CursedEarthBlock> CURSED_EARTH = registerWithItem("cursed_earth", CursedEarthBlock::new);
    public static final DeferredBlock<FirePlaceBlock> FIRE_PLACE = registerWithItem("fire_place", FirePlaceBlock::new);
    public static final DeferredBlock<GarlicBlock> GARLIC = registerWithItem("garlic", GarlicBlock::new);
    public static final DeferredBlock<GarlicDiffuserBlock> GARLIC_DIFFUSER_IMPROVED = registerWithItem("garlic_diffuser_improved", (prop) -> new GarlicDiffuserBlock(prop, GarlicDiffuserBlock.Type.IMPROVED));
    public static final DeferredBlock<GarlicDiffuserBlock> GARLIC_DIFFUSER_NORMAL = registerWithItem("garlic_diffuser_normal", (prop) -> new GarlicDiffuserBlock(prop, GarlicDiffuserBlock.Type.NORMAL));
    public static final DeferredBlock<GarlicDiffuserBlock> GARLIC_DIFFUSER_WEAK = registerWithItem("garlic_diffuser_weak", (prop) -> new GarlicDiffuserBlock(prop, GarlicDiffuserBlock.Type.WEAK));
    public static final DeferredBlock<HunterTableBlock> HUNTER_TABLE = registerWithItem("hunter_table", HunterTableBlock::new);
    public static final DeferredBlock<MedChairBlock> MED_CHAIR = registerWithItem("med_chair", MedChairBlock::new);
    public static final DeferredBlock<SunscreenBeaconBlock> SUNSCREEN_BEACON = registerWithItem("sunscreen_beacon", (prop) -> new SunscreenBeaconBlock(prop.mapColor(MapColor.METAL).strength(-1, 3600000).noOcclusion()), x -> x.rarity(Rarity.RARE));
    public static final DeferredBlock<TentBlock> TENT = BLOCKS.registerBlock("tent", TentBlock::new);
    public static final DeferredBlock<TentMainBlock> TENT_MAIN = BLOCKS.registerBlock("tent_main", TentMainBlock::new);
    public static final DeferredBlock<TotemBaseBlock> TOTEM_BASE = registerWithItem("totem_base", TotemBaseBlock::new);
    public static final DeferredBlock<TotemTopBlock> TOTEM_TOP = registerWithItem("totem_top", (prop) -> new TotemTopBlock(prop, false, null));
    public static final DeferredBlock<TotemTopBlock> TOTEM_TOP_VAMPIRISM_VAMPIRE = BLOCKS.registerBlock("totem_top_vampirism_vampire", (prop) -> new TotemTopBlock(prop, false, ModFactions.VAMPIRE));
    public static final DeferredBlock<TotemTopBlock> TOTEM_TOP_VAMPIRISM_HUNTER = BLOCKS.registerBlock("totem_top_vampirism_hunter", (prop) -> new TotemTopBlock(prop, false, ModFactions.HUNTER));
    public static final DeferredBlock<TotemTopBlock> TOTEM_TOP_CRAFTED = registerWithItem("totem_top_crafted", (prop) -> new TotemTopBlock(prop, true, null));
    public static final DeferredBlock<TotemTopBlock> TOTEM_TOP_VAMPIRISM_VAMPIRE_CRAFTED = BLOCKS.registerBlock("totem_top_vampirism_vampire_crafted", (prop) -> new TotemTopBlock(prop, true, ModFactions.VAMPIRE));
    public static final DeferredBlock<TotemTopBlock> TOTEM_TOP_VAMPIRISM_HUNTER_CRAFTED = BLOCKS.registerBlock("totem_top_vampirism_hunter_crafted", (prop) -> new TotemTopBlock(prop, true, ModFactions.HUNTER));
    public static final DeferredBlock<VampirismFlowerBlock> VAMPIRE_ORCHID = registerWithItem("vampire_orchid", (prop) -> new VampirismFlowerBlock(prop, MobEffects.BLINDNESS, 7, true));
    public static final DeferredBlock<FlowerPotBlock> POTTED_VAMPIRE_ORCHID = registerPottedPlant("potted_vampire_orchid", VAMPIRE_ORCHID);
    public static final DeferredBlock<WeaponTableBlock> WEAPON_TABLE = registerWithItem("weapon_table", WeaponTableBlock::new);
    public static final DeferredBlock<PotionTableBlock> POTION_TABLE = registerWithItem("potion_table", (prop) -> new PotionTableBlock(prop.mapColor(MapColor.METAL).strength(1f).noOcclusion()));
    public static final DeferredBlock<DarkSpruceLeavesBlock> DARK_SPRUCE_LEAVES = registerWithItem("dark_spruce_leaves", DarkSpruceLeavesBlock::new);
    public static final DeferredBlock<VampirismBlock> CROSS = registerWithItem("cross", (prop) -> new VampirismSplitBlock(prop.pushReaction(PushReaction.DESTROY).mapColor(MapColor.WOOD).ignitedByLava().strength(2, 3), BlockVoxelshapes.crossBottom, BlockVoxelshapes.crossTop, true).markDecorativeBlock());
    public static final DeferredBlock<VampirismBlock> TOMBSTONE1 = registerWithItem("tombstone1", (prop) -> new VampirismHorizontalBlock(prop.mapColor(MapColor.STONE).strength(2, 6), BlockVoxelshapes.tomb1).markDecorativeBlock());
    public static final DeferredBlock<VampirismBlock> TOMBSTONE2 = registerWithItem("tombstone2", (prop) -> new VampirismHorizontalBlock(prop.mapColor(MapColor.STONE).strength(2, 6), BlockVoxelshapes.tomb2).markDecorativeBlock());
    public static final DeferredBlock<VampirismBlock> TOMBSTONE3 = registerWithItem("tombstone3", (prop) -> new VampirismSplitBlock(prop.mapColor(MapColor.STONE).pushReaction(PushReaction.DESTROY).strength(2, 6), BlockVoxelshapes.tomb3_base, BlockVoxelshapes.tomb3_top, true).markDecorativeBlock());
    public static final DeferredBlock<VampirismBlock> GRAVE_CAGE = registerWithItem("grave_cage", (prop) -> new VampirismHorizontalBlock(prop.mapColor(MapColor.METAL).strength(6, 8).requiresCorrectToolForDrops().sound(SoundType.METAL), BlockVoxelshapes.grave_cage).markDecorativeBlock());
    public static final DeferredBlock<CursedGrass> CURSED_GRASS = registerWithItem("cursed_grass", (prop) -> new CursedGrass(prop.mapColor(MapColor.COLOR_BLACK).randomTicks().strength(0.6F).sound(SoundType.GRASS)));
    public static final DeferredBlock<CursedRootsBlock> CURSED_ROOTS = registerWithItem("cursed_roots", (prop) -> flammable(new CursedRootsBlock(prop.mapColor(MapColor.COLOR_RED).isViewBlocking(UtilLib::never).pushReaction(PushReaction.DESTROY).ignitedByLava().replaceable().noCollission().instabreak().sound(SoundType.GRASS)), 60, 100));
    public static final DeferredBlock<FlowerPotBlock> POTTED_CURSED_ROOTS = registerPottedPlant("potted_cursed_roots", CURSED_ROOTS);
    public static final DeferredBlock<Block> DARK_SPRUCE_PLANKS = registerWithItem(ModRegistryItems.DARK_SPRUCE_PLANKS.getId().getPath(), (prop) -> new Block(prop.mapColor(MapColor.COLOR_GRAY).ignitedByLava().mapColor(MapColor.COLOR_GRAY).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> CURSED_SPRUCE_PLANKS = registerWithItem(ModRegistryItems.CURSED_SPRUCE_PLANKS.getId().getPath(), (prop) -> new Block(prop.ignitedByLava().mapColor(MapColor.CRIMSON_HYPHAE).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final DeferredBlock<LogBlock> STRIPPED_DARK_SPRUCE_LOG = registerWithItem("stripped_dark_spruce_log", (prop) -> new LogBlock(prop, MapColor.COLOR_BLACK, MapColor.COLOR_GRAY));
    public static final DeferredBlock<LogBlock> STRIPPED_CURSED_SPRUCE_LOG = registerWithItem("stripped_cursed_spruce_log", (prop) -> new LogBlock(prop, MapColor.COLOR_BLACK, MapColor.CRIMSON_HYPHAE));
    public static final DeferredBlock<LogBlock> DARK_SPRUCE_LOG = registerWithItem("dark_spruce_log", (prop) -> new StrippableLogBlock(prop, MapColor.COLOR_BLACK, MapColor.COLOR_BLACK, STRIPPED_DARK_SPRUCE_LOG));
    public static final DeferredBlock<CursedSpruceBlock> CURSED_SPRUCE_LOG_CURED = registerWithItem("cursed_spruce_log_cured", (prop) -> new CursedSpruceBlock(prop, STRIPPED_CURSED_SPRUCE_LOG));
    public static final DeferredBlock<LogBlock> CURSED_SPRUCE_LOG = registerWithItem("cursed_spruce_log", (prop) -> new CursedSpruceBlock(prop, STRIPPED_CURSED_SPRUCE_LOG, CURSED_SPRUCE_LOG_CURED));
    public static final DeferredBlock<SaplingBlock> DARK_SPRUCE_SAPLING = registerWithItem("dark_spruce_sapling", (prop) -> new DarkSpruceSaplingBlock(ModTreeGrower.DARK_SPRUCE, ModTreeGrower.CURSED_SPRUCE, prop.mapColor(MapColor.COLOR_BLACK).isViewBlocking(UtilLib::never).replaceable().pushReaction(PushReaction.DESTROY).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)));
    public static final DeferredBlock<SaplingBlock> CURSED_SPRUCE_SAPLING = registerWithItem("cursed_spruce_sapling", (prop) -> new SaplingBlock(ModTreeGrower.CURSED_SPRUCE, prop.mapColor(MapColor.COLOR_BLACK).isViewBlocking(UtilLib::never).replaceable().pushReaction(PushReaction.DESTROY).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)));
    public static final DeferredBlock<DirectCursedBarkBlock> DIRECT_CURSED_BARK = registerWithItem("direct_cursed_bark", DirectCursedBarkBlock::new);
    public static final DeferredBlock<DiagonalCursedBarkBlock> DIAGONAL_CURSED_BARK = BLOCKS.registerBlock("diagonal_cursed_bark", DiagonalCursedBarkBlock::new);
    public static final DeferredBlock<DoorBlock> DARK_SPRUCE_DOOR = registerWithItem("dark_spruce_door", (prop) -> new DoorBlock(BlockSetType.SPRUCE, prop.mapColor(MapColor.COLOR_GRAY).ignitedByLava().strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final DeferredBlock<DoorBlock> CURSED_SPRUCE_DOOR = registerWithItem("cursed_spruce_door", (prop) -> new DoorBlock(BlockSetType.SPRUCE, prop.mapColor(MapColor.CRIMSON_HYPHAE).ignitedByLava().strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final DeferredBlock<TrapDoorBlock> DARK_SPRUCE_TRAPDOOR = registerWithItem("dark_spruce_trapdoor", (prop) -> new TrapDoorBlock(BlockSetType.SPRUCE, prop.mapColor(MapColor.COLOR_GRAY).ignitedByLava().strength(3.0F).sound(SoundType.WOOD).noOcclusion().isValidSpawn((p_61031_, p_61032_, p_61033_, p_61034_) -> false)));
    public static final DeferredBlock<TrapDoorBlock> CURSED_SPRUCE_TRAPDOOR = registerWithItem("cursed_spruce_trapdoor", (prop) -> new TrapDoorBlock(BlockSetType.SPRUCE, prop.mapColor(MapColor.CRIMSON_HYPHAE).ignitedByLava().strength(3.0F).sound(SoundType.WOOD).noOcclusion().isValidSpawn((p_61031_, p_61032_, p_61033_, p_61034_) -> false)));
    public static final DeferredBlock<StairBlock> DARK_SPRUCE_STAIRS = registerWithItem("dark_spruce_stairs", (prop) -> new StairBlock(DARK_SPRUCE_PLANKS.get().defaultBlockState(), prop), () -> BlockBehaviour.Properties.ofFullCopy(DARK_SPRUCE_PLANKS.get()));
    public static final DeferredBlock<StairBlock> CURSED_SPRUCE_STAIRS = registerWithItem("cursed_spruce_stairs", (prop) -> new StairBlock(CURSED_SPRUCE_PLANKS.get().defaultBlockState(), prop), () -> BlockBehaviour.Properties.ofFullCopy(CURSED_SPRUCE_PLANKS.get()));
    public static final DeferredBlock<LogBlock> STRIPPED_DARK_SPRUCE_WOOD = registerWithItem("stripped_dark_spruce_wood", (prop) -> new LogBlock(prop, MapColor.COLOR_BLACK, MapColor.COLOR_GRAY));
    public static final DeferredBlock<LogBlock> STRIPPED_CURSED_SPRUCE_WOOD = registerWithItem("stripped_cursed_spruce_wood", (prop) -> new LogBlock(prop, MapColor.COLOR_BLACK, MapColor.CRIMSON_HYPHAE));
    public static final DeferredBlock<LogBlock> DARK_SPRUCE_WOOD = registerWithItem("dark_spruce_wood", (prop) -> new StrippableLogBlock(prop, MapColor.COLOR_BLACK, MapColor.COLOR_BLACK, STRIPPED_DARK_SPRUCE_WOOD));
    public static final DeferredBlock<CursedSpruceBlock> CURSED_SPRUCE_WOOD_CURED = registerWithItem("cursed_spruce_wood_cured", (prop) -> new CursedSpruceBlock(prop, STRIPPED_CURSED_SPRUCE_LOG));
    public static final DeferredBlock<LogBlock> CURSED_SPRUCE_WOOD = registerWithItem("cursed_spruce_wood", (prop) -> new CursedSpruceBlock(prop, STRIPPED_CURSED_SPRUCE_WOOD, CURSED_SPRUCE_WOOD_CURED));
    public static final DeferredBlock<StandingSignBlock> DARK_SPRUCE_SIGN = BLOCKS.registerBlock("dark_spruce_sign", (prop) -> new StandingSignBlock(LogBlock.DARK_SPRUCE, prop.mapColor(DARK_SPRUCE_LOG.get().defaultMapColor()).ignitedByLava().noCollission().strength(1.0F).sound(SoundType.WOOD)));
    public static final DeferredBlock<StandingSignBlock> CURSED_SPRUCE_SIGN = BLOCKS.registerBlock("cursed_spruce_sign", (prop) -> new StandingSignBlock(LogBlock.CURSED_SPRUCE, prop.mapColor(CURSED_SPRUCE_LOG.get().defaultMapColor()).ignitedByLava().noCollission().strength(1.0F).sound(SoundType.WOOD)));
    public static final DeferredBlock<WallSignBlock> DARK_SPRUCE_WALL_SIGN = BLOCKS.registerBlock("dark_spruce_wall_sign", (prop) -> new WallSignBlock(LogBlock.DARK_SPRUCE, prop.mapColor(DARK_SPRUCE_LOG.get().defaultMapColor()).ignitedByLava().noCollission().strength(1.0F).sound(SoundType.WOOD).overrideLootTable(DARK_SPRUCE_SIGN.get().getLootTable())));
    public static final DeferredBlock<WallSignBlock> CURSED_SPRUCE_WALL_SIGN = BLOCKS.registerBlock("cursed_spruce_wall_sign", (prop) -> new WallSignBlock(LogBlock.CURSED_SPRUCE, prop.mapColor(CURSED_SPRUCE_LOG.get().defaultMapColor()).ignitedByLava().noCollission().strength(1.0F).sound(SoundType.WOOD).overrideLootTable(CURSED_SPRUCE_SIGN.get().getLootTable())));
    public static final DeferredBlock<PressurePlateBlock> DARK_SPRUCE_PRESSURE_PLACE = registerWithItem("dark_spruce_pressure_plate", (prop) -> new PressurePlateBlock(BlockSetType.SPRUCE, prop.mapColor(DARK_SPRUCE_PLANKS.get().defaultMapColor()).ignitedByLava().noCollission().strength(0.5F).sound(SoundType.WOOD)));
    public static final DeferredBlock<PressurePlateBlock> CURSED_SPRUCE_PRESSURE_PLACE = registerWithItem("cursed_spruce_pressure_plate", (prop) -> new PressurePlateBlock(BlockSetType.SPRUCE, prop.mapColor(CURSED_SPRUCE_PLANKS.get().defaultMapColor()).ignitedByLava().noCollission().strength(0.5F).sound(SoundType.WOOD)));
    public static final DeferredBlock<ButtonBlock> DARK_SPRUCE_BUTTON = registerWithItem("dark_spruce_button", (prop) -> new ButtonBlock(BlockSetType.SPRUCE, 30, prop.noCollission().isViewBlocking(UtilLib::never).pushReaction(PushReaction.DESTROY).ignitedByLava().replaceable().strength(0.5F).sound(SoundType.WOOD)));
    public static final DeferredBlock<ButtonBlock> CURSED_SPRUCE_BUTTON = registerWithItem("cursed_spruce_button", (prop) -> new ButtonBlock(BlockSetType.SPRUCE, 30, prop.noCollission().isViewBlocking(UtilLib::never).pushReaction(PushReaction.DESTROY).ignitedByLava().replaceable().strength(0.5F).sound(SoundType.WOOD)));
    public static final DeferredBlock<SlabBlock> DARK_SPRUCE_SLAB = registerWithItem("dark_spruce_slab", (prop) -> new SlabBlock(prop.mapColor(MapColor.COLOR_GRAY).ignitedByLava().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final DeferredBlock<SlabBlock> CURSED_SPRUCE_SLAB = registerWithItem("cursed_spruce_slab", (prop) -> new SlabBlock(prop.mapColor(MapColor.CRIMSON_HYPHAE).ignitedByLava().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final DeferredBlock<FenceGateBlock> DARK_SPRUCE_FENCE_GATE = registerWithItem("dark_spruce_fence_gate", (prop) -> new FenceGateBlock(LogBlock.DARK_SPRUCE, prop.mapColor(DARK_SPRUCE_PLANKS.get().defaultMapColor()).ignitedByLava().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final DeferredBlock<FenceGateBlock> CURSED_SPRUCE_FENCE_GATE = registerWithItem("cursed_spruce_fence_gate", (prop) -> new FenceGateBlock(LogBlock.CURSED_SPRUCE, prop.mapColor(CURSED_SPRUCE_PLANKS.get().defaultMapColor()).ignitedByLava().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final DeferredBlock<FenceBlock> DARK_SPRUCE_FENCE = registerWithItem("dark_spruce_fence", (prop) -> new FenceBlock(prop.mapColor(DARK_SPRUCE_PLANKS.get().defaultMapColor()).ignitedByLava().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final DeferredBlock<FenceBlock> CURSED_SPRUCE_FENCE = registerWithItem("cursed_spruce_fence", (prop) -> new FenceBlock(prop.mapColor(CURSED_SPRUCE_PLANKS.get().defaultMapColor()).ignitedByLava().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final DeferredBlock<VampirismBlock> VAMPIRE_RACK = registerWithItem("vampire_rack", (prop) -> new VampirismHorizontalBlock(prop.ignitedByLava().strength(2, 3), BlockVoxelshapes.vampire_rack).markDecorativeBlock());
    public static final DeferredBlock<VampirismBlock> THRONE = registerWithItem("throne", ThroneBlock::new);
    public static final DeferredBlock<CoffinBlock> COFFIN_WHITE = registerWithItem("coffin_white", (prop) -> new CoffinBlock(prop, DyeColor.WHITE));
    public static final DeferredBlock<CoffinBlock> COFFIN_ORANGE = registerWithItem("coffin_orange", (prop) -> new CoffinBlock(prop, DyeColor.ORANGE));
    public static final DeferredBlock<CoffinBlock> COFFIN_MAGENTA = registerWithItem("coffin_magenta", (prop) -> new CoffinBlock(prop, DyeColor.MAGENTA));
    public static final DeferredBlock<CoffinBlock> COFFIN_LIGHT_BLUE = registerWithItem("coffin_light_blue", (prop) -> new CoffinBlock(prop, DyeColor.LIGHT_BLUE));
    public static final DeferredBlock<CoffinBlock> COFFIN_YELLOW = registerWithItem("coffin_yellow", (prop) -> new CoffinBlock(prop, DyeColor.YELLOW));
    public static final DeferredBlock<CoffinBlock> COFFIN_LIME = registerWithItem("coffin_lime", (prop) -> new CoffinBlock(prop, DyeColor.LIME));
    public static final DeferredBlock<CoffinBlock> COFFIN_PINK = registerWithItem("coffin_pink", (prop) -> new CoffinBlock(prop, DyeColor.PINK));
    public static final DeferredBlock<CoffinBlock> COFFIN_GRAY = registerWithItem("coffin_gray", (prop) -> new CoffinBlock(prop, DyeColor.GRAY));
    public static final DeferredBlock<CoffinBlock> COFFIN_LIGHT_GRAY = registerWithItem("coffin_light_gray", (prop) -> new CoffinBlock(prop, DyeColor.LIGHT_GRAY));
    public static final DeferredBlock<CoffinBlock> COFFIN_CYAN = registerWithItem("coffin_cyan", (prop) -> new CoffinBlock(prop, DyeColor.CYAN));
    public static final DeferredBlock<CoffinBlock> COFFIN_PURPLE = registerWithItem("coffin_purple", (prop) -> new CoffinBlock(prop, DyeColor.PURPLE));
    public static final DeferredBlock<CoffinBlock> COFFIN_BLUE = registerWithItem("coffin_blue", (prop) -> new CoffinBlock(prop, DyeColor.BLUE));
    public static final DeferredBlock<CoffinBlock> COFFIN_BROWN = registerWithItem("coffin_brown", (prop) -> new CoffinBlock(prop, DyeColor.BROWN));
    public static final DeferredBlock<CoffinBlock> COFFIN_GREEN = registerWithItem("coffin_green", (prop) -> new CoffinBlock(prop, DyeColor.GREEN));
    public static final DeferredBlock<CoffinBlock> COFFIN_RED = registerWithItem("coffin_red", (prop) -> new CoffinBlock(prop, DyeColor.RED));
    public static final DeferredBlock<CoffinBlock> COFFIN_BLACK = registerWithItem("coffin_black", (prop) -> new CoffinBlock(prop, DyeColor.BLACK));
    public static final DeferredBlock<AlchemyTableBlock> ALCHEMY_TABLE = registerWithItem("alchemy_table", AlchemyTableBlock::new);
    public static final DeferredBlock<CeilingHangingSignBlock> DARK_SPRUCE_HANGING_SIGN = BLOCKS.registerBlock("dark_spruce_hanging_sign", (prop) -> new CeilingHangingSignBlock(LogBlock.DARK_SPRUCE, prop.mapColor(DARK_SPRUCE_LOG.get().defaultMapColor()).ignitedByLava().noCollission().strength(1.0F).sound(SoundType.WOOD)));
    public static final DeferredBlock<CeilingHangingSignBlock> CURSED_SPRUCE_HANGING_SIGN = BLOCKS.registerBlock("cursed_spruce_hanging_sign", (prop) -> new CeilingHangingSignBlock(LogBlock.CURSED_SPRUCE, prop.mapColor(CURSED_SPRUCE_LOG.get().defaultMapColor()).ignitedByLava().noCollission().strength(1.0F).sound(SoundType.WOOD)));
    public static final DeferredBlock<WallHangingSignBlock> DARK_SPRUCE_WALL_HANGING_SIGN = BLOCKS.registerBlock("dark_spruce_wall_hanging_sign", (prop) -> new WallHangingSignBlock(LogBlock.DARK_SPRUCE, prop.mapColor(DARK_SPRUCE_LOG.get().defaultMapColor()).ignitedByLava().noCollission().strength(1.0F).sound(SoundType.WOOD).overrideLootTable(DARK_SPRUCE_HANGING_SIGN.get().getLootTable())));
    public static final DeferredBlock<WallHangingSignBlock> CURSED_SPRUCE_WALL_HANGING_SIGN = BLOCKS.registerBlock("cursed_spruce_wall_hanging_sign", (prop) -> new WallHangingSignBlock(LogBlock.CURSED_SPRUCE, prop.mapColor(CURSED_SPRUCE_LOG.get().defaultMapColor()).ignitedByLava().noCollission().strength(1.0F).sound(SoundType.WOOD).overrideLootTable(CURSED_SPRUCE_HANGING_SIGN.get().getLootTable())));
    public static final DeferredBlock<CursedEarthPathBlock> CURSED_EARTH_PATH = registerWithItem("cursed_earth_path", (prop) -> new CursedEarthPathBlock(prop.mapColor(MapColor.DIRT).strength(0.65F).sound(SoundType.GRASS).isViewBlocking(UtilLib::always).isSuffocating(UtilLib::always)));
    public static final DeferredBlock<DarkStoneBlock> DARK_STONE = registerWithItem("dark_stone", (prop) -> new DarkStoneBlock(prop.mapColor(MapColor.DEEPSLATE).requiresCorrectToolForDrops().strength(2f, 10f).sound(SoundType.STONE)));
    public static final DeferredBlock<DarkStoneStairsBlock> DARK_STONE_STAIRS = registerWithItem("dark_stone_stairs", (prop) -> new DarkStoneStairsBlock(DARK_STONE, prop), () -> BlockBehaviour.Properties.ofFullCopy(DARK_STONE.get()));
    public static final DeferredBlock<DarkStoneSlabBlock> DARK_STONE_SLAB = registerWithItem("dark_stone_slab", DarkStoneSlabBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(DARK_STONE.get()));
    public static final DeferredBlock<WallBlock> DARK_STONE_WALL = registerWithItem("dark_stone_wall", (prop) -> new WallBlock(prop.forceSolidOn()), () -> BlockBehaviour.Properties.ofFullCopy(DARK_STONE.get()));
    public static final DeferredBlock<DarkStoneBlock> DARK_STONE_BRICKS = registerWithItem("dark_stone_bricks", (prop) -> new DarkStoneBlock(prop.mapColor(MapColor.DEEPSLATE).requiresCorrectToolForDrops().strength(2f, 10f).sound(SoundType.STONE)));
    public static final DeferredBlock<DarkStoneStairsBlock> DARK_STONE_BRICK_STAIRS = registerWithItem("dark_stone_brick_stairs", (prop) -> new DarkStoneStairsBlock(DARK_STONE_BRICKS, prop), () -> BlockBehaviour.Properties.ofFullCopy(DARK_STONE_BRICKS.get()));
    public static final DeferredBlock<DarkStoneSlabBlock> DARK_STONE_BRICK_SLAB = registerWithItem("dark_stone_brick_slab", DarkStoneSlabBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(DARK_STONE_BRICKS.get()));
    public static final DeferredBlock<WallBlock> DARK_STONE_BRICK_WALL = registerWithItem("dark_stone_brick_wall", (prop) -> new WallBlock(prop.forceSolidOn()), () -> BlockBehaviour.Properties.ofFullCopy(DARK_STONE_BRICKS.get()));
    public static final DeferredBlock<Block> CRACKED_DARK_STONE_BRICKS = registerWithItem("cracked_dark_stone_bricks", DarkStoneBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(DARK_STONE_BRICKS.get()));
    public static final DeferredBlock<DarkStoneBlock> COBBLED_DARK_STONE = registerWithItem("cobbled_dark_stone", (prop) -> new DarkStoneBlock(prop.mapColor(MapColor.DEEPSLATE).strength(2.5f, 10f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<DarkStoneStairsBlock> COBBLED_DARK_STONE_STAIRS = registerWithItem("cobbled_dark_stone_stairs", (prop) -> new DarkStoneStairsBlock(ModBlocks.COBBLED_DARK_STONE, prop), () -> BlockBehaviour.Properties.ofFullCopy(COBBLED_DARK_STONE.get()));
    public static final DeferredBlock<DarkStoneSlabBlock> COBBLED_DARK_STONE_SLAB = registerWithItem("cobbled_dark_stone_slab", DarkStoneSlabBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(COBBLED_DARK_STONE.get()));
    public static final DeferredBlock<WallBlock> COBBLED_DARK_STONE_WALL = registerWithItem("cobbled_dark_stone_wall", (prop) -> new WallBlock(prop.forceSolidOn()), () -> BlockBehaviour.Properties.ofFullCopy(COBBLED_DARK_STONE.get()));
    public static final DeferredBlock<DarkStoneBlock> POLISHED_DARK_STONE = registerWithItem("polished_dark_stone", DarkStoneBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(COBBLED_DARK_STONE.get()));
    public static final DeferredBlock<DarkStoneStairsBlock> POLISHED_DARK_STONE_STAIRS = registerWithItem("polished_dark_stone_stairs", (prop) -> new DarkStoneStairsBlock(POLISHED_DARK_STONE, prop), () -> BlockBehaviour.Properties.ofFullCopy(POLISHED_DARK_STONE.get()));
    public static final DeferredBlock<DarkStoneSlabBlock> POLISHED_DARK_STONE_SLAB = registerWithItem("polished_dark_stone_slab", DarkStoneSlabBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(POLISHED_DARK_STONE.get()));
    public static final DeferredBlock<WallBlock> POLISHED_DARK_STONE_WALL = registerWithItem("polished_dark_stone_wall", (prop) -> new WallBlock(prop.forceSolidOn()), () -> BlockBehaviour.Properties.ofFullCopy(POLISHED_DARK_STONE.get()));
    public static final DeferredBlock<DarkStoneBlock> DARK_STONE_TILES = registerWithItem("dark_stone_tiles", DarkStoneBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(COBBLED_DARK_STONE.get()));
    public static final DeferredBlock<DarkStoneBlock> CRACKED_DARK_STONE_TILES = registerWithItem("cracked_dark_stone_tiles", DarkStoneBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(DARK_STONE_TILES.get()));
    public static final DeferredBlock<DarkStoneStairsBlock> DARK_STONE_TILES_STAIRS = registerWithItem("dark_stone_tiles_stairs", (prop) -> new DarkStoneStairsBlock(ModBlocks.DARK_STONE_TILES, prop), () -> BlockBehaviour.Properties.ofFullCopy(DARK_STONE_TILES.get()));
    public static final DeferredBlock<DarkStoneSlabBlock> DARK_STONE_TILES_SLAB = registerWithItem("dark_stone_tiles_slab", DarkStoneSlabBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(DARK_STONE_TILES.get()));
    public static final DeferredBlock<WallBlock> DARK_STONE_TILES_WALL = registerWithItem("dark_stone_tiles_wall", (prop) -> new WallBlock(prop.forceSolidOn()), () -> BlockBehaviour.Properties.ofFullCopy(DARK_STONE_TILES.get()));
    public static final DeferredBlock<DarkStoneBlock> CHISELED_DARK_STONE_BRICKS = registerWithItem("chiseled_dark_stone_bricks", DarkStoneBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(DARK_STONE_BRICKS.get()));
    public static final DeferredBlock<DarkStoneBlock> INFESTED_DARK_STONE = registerWithItem("infested_dark_stone", DarkStoneBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(DARK_STONE.get()));
    public static final DeferredBlock<DarkStoneBlock> BLOODY_DARK_STONE_BRICKS = registerWithItem("bloody_dark_stone_bricks", DarkStoneBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(DARK_STONE_BRICKS.get()));
    public static final DeferredBlock<Block> BAT_CAGE = registerWithItem("bat_cage", (prop) -> new BatCageBlock(prop.strength(5.0F, 6.0F).sound(SoundType.METAL).noOcclusion()));
    public static final DeferredBlock<RemainsBlock> REMAINS = BLOCKS.registerBlock("remains", (prop) -> new RemainsBlock(prop.mapColor(MapColor.TERRACOTTA_BROWN).strength(-1, 3600000.0F).sound(SoundType.ROOTED_DIRT).randomTicks().noLootTable(), false, false));
    public static final DeferredBlock<RemainsBlock> VULNERABLE_REMAINS = BLOCKS.registerBlock("vulnerable_remains", (prop) -> new RemainsBlock(prop.mapColor(MapColor.TERRACOTTA_BROWN).strength(-1, 3600000.0F).sound(SoundType.ROOTED_DIRT).randomTicks().noLootTable(), true, true));
    public static final DeferredBlock<RemainsBlock> INCAPACITATED_VULNERABLE_REMAINS = BLOCKS.registerBlock("incapacitated_vulnerable_remains", (prop) -> new RemainsBlock(prop.mapColor(MapColor.TERRACOTTA_BROWN).strength(-1.0F, 3600000.0F).sound(SoundType.ROOTED_DIRT).randomTicks().noLootTable(), false, true));
    public static final DeferredBlock<ActiveVulnerableRemainsBlock> ACTIVE_VULNERABLE_REMAINS = BLOCKS.registerBlock("active_vulnerable_remains", (prop) -> new ActiveVulnerableRemainsBlock(prop.mapColor(MapColor.TERRACOTTA_BROWN).strength(-1, 3600000.0F).randomTicks().sound(SoundType.ROOTED_DIRT).noLootTable()));
    public static final DeferredBlock<HangingRootsBlock> CURSED_HANGING_ROOTS = registerWithItem("cursed_hanging_roots", (prop) -> {
        var block = new HangingRootsBlock(prop.mapColor(MapColor.TERRACOTTA_BROWN).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).ignitedByLava().pushReaction(PushReaction.DESTROY));
        ((FireBlock) Blocks.FIRE).setFlammable(block, 30, 60);
        return block;
    });
    public static final DeferredBlock<MotherBlock> MOTHER = BLOCKS.registerBlock("mother", MotherBlock::new);
    public static final DeferredBlock<Block> MOTHER_TROPHY = registerWithItem("mother_trophy", (prop) -> new MotherTrophyBlock(prop.mapColor(MapColor.COLOR_GRAY).strength(3, 9).lightLevel(s -> 1).noOcclusion()),prop -> prop.rarity(Rarity.EPIC).stacksTo(1));
    public static final DeferredBlock<FogDiffuserBlock> FOG_DIFFUSER = registerWithItem("fog_diffuser", (prop) -> new FogDiffuserBlock(prop.noOcclusion().mapColor(MapColor.STONE).strength(40.0F, 1200.0F).sound(SoundType.STONE)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_DARK_SPRUCE_SAPLING = registerPottedPlant("potted_dark_spruce_sapling", DARK_SPRUCE_SAPLING);
    public static final DeferredBlock<FlowerPotBlock> POTTED_CURSED_SPRUCE_SAPLING = registerPottedPlant("potted_cursed_spruce_sapling", CURSED_SPRUCE_SAPLING);
    public static final DeferredBlock<Block> BLOOD_INFUSED_IRON_BLOCK = registerWithItem("blood_infused_iron_block", (prop) -> new PureBloodBlock(prop.mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(6.0F, 7.0F).sound(SoundType.METAL)), (block, prop) -> new PureLevelBlockItem(block, prop.component(ModDataComponents.PURE_LEVEL, PureLevel.LOW)));
    public static final DeferredBlock<Block> BLOOD_INFUSED_ENHANCED_IRON_BLOCK = registerWithItem("blood_infused_enhanced_iron_block", (prop) -> new PureBloodBlock(prop.mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(6.5F, 8.0F).sound(SoundType.METAL)), (block, prop) -> new PureLevelBlockItem(block, prop.component(ModDataComponents.PURE_LEVEL, new PureLevel(4))));
    public static final DeferredBlock<VampireBeaconBlock> VAMPIRE_BEACON = registerWithItem("vampire_beacon", (prop) -> new VampireBeaconBlock(prop.mapColor(MapColor.DIAMOND).instrument(NoteBlockInstrument.HAT).strength(3.0F).lightLevel((p_50828_) -> 15).noOcclusion().isRedstoneConductor(UtilLib::never)), x -> x.rarity(Rarity.RARE));
    public static final DeferredBlock<Block> PURPLE_STONE_BRICKS = registerWithItem("purple_stone_bricks", (prop) -> new Block(prop.mapColor(MapColor.COLOR_PURPLE).requiresCorrectToolForDrops().strength(2f, 10f).sound(SoundType.STONE)));
    public static final DeferredBlock<StairBlock> PURPLE_STONE_BRICK_STAIRS = registerWithItem("purple_stone_brick_stairs", (prop) -> new StairBlock(PURPLE_STONE_BRICKS.get().defaultBlockState(), prop), () -> BlockBehaviour.Properties.ofFullCopy(PURPLE_STONE_BRICKS.get()));
    public static final DeferredBlock<SlabBlock> PURPLE_STONE_BRICK_SLAB = registerWithItem("purple_stone_brick_slab", SlabBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(PURPLE_STONE_BRICKS.get()));
    public static final DeferredBlock<WallBlock> PURPLE_STONE_BRICK_WALL = registerWithItem("purple_stone_brick_wall", (prop) -> new WallBlock(prop.forceSolidOn()), () -> BlockBehaviour.Properties.ofFullCopy(PURPLE_STONE_BRICKS.get()));
    public static final DeferredBlock<Block> PURPLE_STONE_TILES = registerWithItem("purple_stone_tiles", (prop) -> new Block(prop.mapColor(MapColor.COLOR_PURPLE).requiresCorrectToolForDrops().strength(2f, 10f).sound(SoundType.STONE)));
    public static final DeferredBlock<StairBlock> PURPLE_STONE_TILES_STAIRS = registerWithItem("purple_stone_tiles_stairs", (prop) -> new StairBlock(PURPLE_STONE_TILES.get().defaultBlockState(), prop), () -> BlockBehaviour.Properties.ofFullCopy(PURPLE_STONE_TILES.get()));
    public static final DeferredBlock<SlabBlock> PURPLE_STONE_TILES_SLAB = registerWithItem("purple_stone_tiles_slab", SlabBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(PURPLE_STONE_TILES.get()));
    public static final DeferredBlock<WallBlock> PURPLE_STONE_TILES_WALL = registerWithItem("purple_stone_tiles_wall", (prop) -> new WallBlock(prop.forceSolidOn()), () -> BlockBehaviour.Properties.ofFullCopy(PURPLE_STONE_TILES.get()));
    public static final DeferredBlock<VampireSoulLanternBlock> VAMPIRE_SOUL_LANTERN = registerWithItem("vampire_soul_lantern", (prop) -> new VampireSoulLanternBlock(prop.mapColor(MapColor.METAL).forceSolidOn().requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.LANTERN).lightLevel(p_187431_ -> 12).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<BloodInfuserBlock> INFUSER = registerWithItem("blood_infuser", BloodInfuserBlock::new);

    public static final DeferredBlock<StandingCandleStickBlock> CANDLE_STICK = BLOCKS.registerBlock("candle_stick", (prop) -> new StandingCandleStickBlock(null, () -> null, prop.mapColor(MapColor.METAL).noOcclusion().strength(0.5f).sound(SoundType.METAL).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<WallCandleStickBlock> WALL_CANDLE_STICK = registerBlock("wall_candle_stick", (prop) -> new WallCandleStickBlock(null, () -> null, prop.overrideLootTable(CANDLE_STICK.get().getLootTable())), () -> BlockBehaviour.Properties.ofFullCopy(CANDLE_STICK.get()));
    
    public static final DeferredBlock<StandingCandleStickBlock> CANDLE_STICK_NORMAL = registerCandleStick("normal", Items.CANDLE);
    public static final DeferredBlock<WallCandleStickBlock> WALL_CANDLE_STICK_NORMAL = registerWallCandleStick("normal", Items.CANDLE, CANDLE_STICK_NORMAL);
    public static final DeferredBlock<StandingCandleStickBlock> CANDLE_STICK_WHITE = registerCandleStick("white", Items.WHITE_CANDLE);
    public static final DeferredBlock<WallCandleStickBlock> WALL_CANDLE_STICK_WHITE = registerWallCandleStick("white", Items.WHITE_CANDLE, CANDLE_STICK_WHITE);
    public static final DeferredBlock<StandingCandleStickBlock> CANDLE_STICK_ORANGE = registerCandleStick("orange", Items.ORANGE_CANDLE);
    public static final DeferredBlock<WallCandleStickBlock> WALL_CANDLE_STICK_ORANGE = registerWallCandleStick("orange", Items.ORANGE_CANDLE, CANDLE_STICK_ORANGE);
    public static final DeferredBlock<StandingCandleStickBlock> CANDLE_STICK_MAGENTA = registerCandleStick("magenta", Items.MAGENTA_CANDLE);
    public static final DeferredBlock<WallCandleStickBlock> WALL_CANDLE_STICK_MAGENTA = registerWallCandleStick("magenta", Items.MAGENTA_CANDLE, CANDLE_STICK_MAGENTA);
    public static final DeferredBlock<StandingCandleStickBlock> CANDLE_STICK_LIGHT_BLUE = registerCandleStick("light_blue", Items.LIGHT_BLUE_CANDLE);
    public static final DeferredBlock<WallCandleStickBlock> WALL_CANDLE_STICK_LIGHT_BLUE = registerWallCandleStick("light_blue", Items.LIGHT_BLUE_CANDLE, CANDLE_STICK_LIGHT_BLUE);
    public static final DeferredBlock<StandingCandleStickBlock> CANDLE_STICK_YELLOW = registerCandleStick("yellow", Items.YELLOW_CANDLE);
    public static final DeferredBlock<WallCandleStickBlock> WALL_CANDLE_STICK_YELLOW = registerWallCandleStick("yellow", Items.YELLOW_CANDLE, CANDLE_STICK_YELLOW);
    public static final DeferredBlock<StandingCandleStickBlock> CANDLE_STICK_LIME = registerCandleStick("lime", Items.LIME_CANDLE);
    public static final DeferredBlock<WallCandleStickBlock> WALL_CANDLE_STICK_LIME = registerWallCandleStick("lime", Items.LIME_CANDLE, CANDLE_STICK_LIME);
    public static final DeferredBlock<StandingCandleStickBlock> CANDLE_STICK_PINK = registerCandleStick("pink", Items.PINK_CANDLE);
    public static final DeferredBlock<WallCandleStickBlock> WALL_CANDLE_STICK_PINK = registerWallCandleStick("pink", Items.PINK_CANDLE, CANDLE_STICK_PINK);
    public static final DeferredBlock<StandingCandleStickBlock> CANDLE_STICK_GRAY = registerCandleStick("gray", Items.GRAY_CANDLE);
    public static final DeferredBlock<WallCandleStickBlock> WALL_CANDLE_STICK_GRAY = registerWallCandleStick("gray", Items.GRAY_CANDLE, CANDLE_STICK_GRAY);
    public static final DeferredBlock<StandingCandleStickBlock> CANDLE_STICK_LIGHT_GRAY = registerCandleStick("light_gray", Items.LIGHT_GRAY_CANDLE);
    public static final DeferredBlock<WallCandleStickBlock> WALL_CANDLE_STICK_LIGHT_GRAY = registerWallCandleStick("light_gray", Items.LIGHT_GRAY_CANDLE, CANDLE_STICK_LIGHT_GRAY);
    public static final DeferredBlock<StandingCandleStickBlock> CANDLE_STICK_CYAN = registerCandleStick("cyan", Items.CYAN_CANDLE);
    public static final DeferredBlock<WallCandleStickBlock> WALL_CANDLE_STICK_CYAN = registerWallCandleStick("cyan", Items.CYAN_CANDLE, CANDLE_STICK_CYAN);
    public static final DeferredBlock<StandingCandleStickBlock> CANDLE_STICK_PURPLE = registerCandleStick("purple", Items.PURPLE_CANDLE);
    public static final DeferredBlock<WallCandleStickBlock> WALL_CANDLE_STICK_PURPLE = registerWallCandleStick("purple", Items.PURPLE_CANDLE, CANDLE_STICK_PURPLE);
    public static final DeferredBlock<StandingCandleStickBlock> CANDLE_STICK_BLUE = registerCandleStick("blue", Items.BLUE_CANDLE);
    public static final DeferredBlock<WallCandleStickBlock> WALL_CANDLE_STICK_BLUE = registerWallCandleStick("blue", Items.BLUE_CANDLE, CANDLE_STICK_BLUE);
    public static final DeferredBlock<StandingCandleStickBlock> CANDLE_STICK_BROWN = registerCandleStick("brown", Items.BROWN_CANDLE);
    public static final DeferredBlock<WallCandleStickBlock> WALL_CANDLE_STICK_BROWN = registerWallCandleStick("brown", Items.BROWN_CANDLE, CANDLE_STICK_BROWN);
    public static final DeferredBlock<StandingCandleStickBlock> CANDLE_STICK_GREEN = registerCandleStick("green", Items.GREEN_CANDLE);
    public static final DeferredBlock<WallCandleStickBlock> WALL_CANDLE_STICK_GREEN = registerWallCandleStick("green", Items.GREEN_CANDLE, CANDLE_STICK_GREEN);
    public static final DeferredBlock<StandingCandleStickBlock> CANDLE_STICK_RED = registerCandleStick("red", Items.RED_CANDLE);
    public static final DeferredBlock<WallCandleStickBlock> WALL_CANDLE_STICK_RED = registerWallCandleStick("red", Items.RED_CANDLE, CANDLE_STICK_RED);
    public static final DeferredBlock<StandingCandleStickBlock> CANDLE_STICK_BLACK = registerCandleStick("black", Items.BLACK_CANDLE);
    public static final DeferredBlock<WallCandleStickBlock> WALL_CANDLE_STICK_BLACK = registerWallCandleStick("black", Items.BLACK_CANDLE, CANDLE_STICK_BLACK);

    public static final DeferredBlock<StandingCandelabraBlock> CANDELABRA = BLOCKS.registerBlock("candelabra", (prop) -> new StandingCandelabraBlock(null, () -> null, prop.mapColor(MapColor.METAL).noOcclusion().strength(1.0f).sound(SoundType.METAL).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<WallCandelabraBlock> WALL_CANDELABRA = registerBlock("wall_candelabra", (prop) -> new WallCandelabraBlock(null, () -> null, prop.strength(3.5f).overrideLootTable(CANDELABRA.get().getLootTable())), () -> BlockBehaviour.Properties.ofFullCopy(CANDELABRA.get()));

    public static final DeferredBlock<StandingCandelabraBlock> CANDELABRA_NORMAL = registerCandelabra("normal", Items.CANDLE);
    public static final DeferredBlock<WallCandelabraBlock> WALL_CANDELABRA_NORMAL = registerWallCandelabra("normal", Items.CANDLE, CANDELABRA_NORMAL);
    public static final DeferredBlock<StandingCandelabraBlock> CANDELABRA_WHITE = registerCandelabra("white", Items.WHITE_CANDLE);
    public static final DeferredBlock<WallCandelabraBlock> WALL_CANDELABRA_WHITE = registerWallCandelabra("white", Items.WHITE_CANDLE, CANDELABRA_WHITE);
    public static final DeferredBlock<StandingCandelabraBlock> CANDELABRA_ORANGE = registerCandelabra("orange", Items.ORANGE_CANDLE);
    public static final DeferredBlock<WallCandelabraBlock> WALL_CANDELABRA_ORANGE = registerWallCandelabra("orange", Items.ORANGE_CANDLE, CANDELABRA_ORANGE);
    public static final DeferredBlock<StandingCandelabraBlock> CANDELABRA_MAGENTA = registerCandelabra("magenta", Items.MAGENTA_CANDLE);
    public static final DeferredBlock<WallCandelabraBlock> WALL_CANDELABRA_MAGENTA = registerWallCandelabra("magenta", Items.MAGENTA_CANDLE, CANDELABRA_MAGENTA);
    public static final DeferredBlock<StandingCandelabraBlock> CANDELABRA_LIGHT_BLUE = registerCandelabra("light_blue", Items.LIGHT_BLUE_CANDLE);
    public static final DeferredBlock<WallCandelabraBlock> WALL_CANDELABRA_LIGHT_BLUE = registerWallCandelabra("light_blue", Items.LIGHT_BLUE_CANDLE, CANDELABRA_LIGHT_BLUE);
    public static final DeferredBlock<StandingCandelabraBlock> CANDELABRA_YELLOW = registerCandelabra("yellow", Items.YELLOW_CANDLE);
    public static final DeferredBlock<WallCandelabraBlock> WALL_CANDELABRA_YELLOW = registerWallCandelabra("yellow", Items.YELLOW_CANDLE, CANDELABRA_YELLOW);
    public static final DeferredBlock<StandingCandelabraBlock> CANDELABRA_LIME = registerCandelabra("lime", Items.LIME_CANDLE);
    public static final DeferredBlock<WallCandelabraBlock> WALL_CANDELABRA_LIME = registerWallCandelabra("lime", Items.LIME_CANDLE, CANDELABRA_LIME);
    public static final DeferredBlock<StandingCandelabraBlock> CANDELABRA_PINK = registerCandelabra("pink", Items.PINK_CANDLE);
    public static final DeferredBlock<WallCandelabraBlock> WALL_CANDELABRA_PINK = registerWallCandelabra("pink", Items.PINK_CANDLE, CANDELABRA_PINK);
    public static final DeferredBlock<StandingCandelabraBlock> CANDELABRA_GRAY = registerCandelabra("gray", Items.GRAY_CANDLE);
    public static final DeferredBlock<WallCandelabraBlock> WALL_CANDELABRA_GRAY = registerWallCandelabra("gray", Items.GRAY_CANDLE, CANDELABRA_GRAY);
    public static final DeferredBlock<StandingCandelabraBlock> CANDELABRA_LIGHT_GRAY = registerCandelabra("light_gray", Items.LIGHT_GRAY_CANDLE);
    public static final DeferredBlock<WallCandelabraBlock> WALL_CANDELABRA_LIGHT_GRAY = registerWallCandelabra("light_gray", Items.LIGHT_GRAY_CANDLE, CANDELABRA_LIGHT_GRAY);
    public static final DeferredBlock<StandingCandelabraBlock> CANDELABRA_CYAN = registerCandelabra("cyan", Items.CYAN_CANDLE);
    public static final DeferredBlock<WallCandelabraBlock> WALL_CANDELABRA_CYAN = registerWallCandelabra("cyan", Items.CYAN_CANDLE, CANDELABRA_CYAN);
    public static final DeferredBlock<StandingCandelabraBlock> CANDELABRA_PURPLE = registerCandelabra("purple", Items.PURPLE_CANDLE);
    public static final DeferredBlock<WallCandelabraBlock> WALL_CANDELABRA_PURPLE = registerWallCandelabra("purple", Items.PURPLE_CANDLE, CANDELABRA_PURPLE);
    public static final DeferredBlock<StandingCandelabraBlock> CANDELABRA_BLUE = registerCandelabra("blue", Items.BLUE_CANDLE);
    public static final DeferredBlock<WallCandelabraBlock> WALL_CANDELABRA_BLUE = registerWallCandelabra("blue", Items.BLUE_CANDLE, CANDELABRA_BLUE);
    public static final DeferredBlock<StandingCandelabraBlock> CANDELABRA_BROWN = registerCandelabra("brown", Items.BROWN_CANDLE);
    public static final DeferredBlock<WallCandelabraBlock> WALL_CANDELABRA_BROWN = registerWallCandelabra("brown", Items.BROWN_CANDLE, CANDELABRA_BROWN);
    public static final DeferredBlock<StandingCandelabraBlock> CANDELABRA_GREEN = registerCandelabra("green", Items.GREEN_CANDLE);
    public static final DeferredBlock<WallCandelabraBlock> WALL_CANDELABRA_GREEN = registerWallCandelabra("green", Items.GREEN_CANDLE, CANDELABRA_GREEN);
    public static final DeferredBlock<StandingCandelabraBlock> CANDELABRA_RED = registerCandelabra("red", Items.RED_CANDLE);
    public static final DeferredBlock<WallCandelabraBlock> WALL_CANDELABRA_RED = registerWallCandelabra("red", Items.RED_CANDLE, CANDELABRA_RED);
    public static final DeferredBlock<StandingCandelabraBlock> CANDELABRA_BLACK = registerCandelabra("black", Items.BLACK_CANDLE);
    public static final DeferredBlock<WallCandelabraBlock> WALL_CANDELABRA_BLACK = registerWallCandelabra("black", Items.BLACK_CANDLE, CANDELABRA_BLACK);

    public static final DeferredBlock<ChandelierBlock> CHANDELIER = BLOCKS.registerBlock("chandelier", (prop) -> new ChandelierBlock(null, () -> null, prop.mapColor(MapColor.METAL).noOcclusion().strength(4.5f, 5.5f).sound(SoundType.METAL).pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<ChandelierBlock> CHANDELIER_NORMAL = registerChandelier("normal", Items.CANDLE);
    public static final DeferredBlock<ChandelierBlock> CHANDELIER_WHITE = registerChandelier("white", Items.WHITE_CANDLE);
    public static final DeferredBlock<ChandelierBlock> CHANDELIER_ORANGE = registerChandelier("orange", Items.ORANGE_CANDLE);
    public static final DeferredBlock<ChandelierBlock> CHANDELIER_MAGENTA = registerChandelier("magenta", Items.MAGENTA_CANDLE);
    public static final DeferredBlock<ChandelierBlock> CHANDELIER_LIGHT_BLUE = registerChandelier("light_blue", Items.LIGHT_BLUE_CANDLE);
    public static final DeferredBlock<ChandelierBlock> CHANDELIER_YELLOW = registerChandelier("yellow", Items.YELLOW_CANDLE);
    public static final DeferredBlock<ChandelierBlock> CHANDELIER_LIME = registerChandelier("lime", Items.LIME_CANDLE);
    public static final DeferredBlock<ChandelierBlock> CHANDELIER_PINK = registerChandelier("pink", Items.PINK_CANDLE);
    public static final DeferredBlock<ChandelierBlock> CHANDELIER_GRAY = registerChandelier("gray", Items.GRAY_CANDLE);
    public static final DeferredBlock<ChandelierBlock> CHANDELIER_LIGHT_GRAY = registerChandelier("light_gray", Items.LIGHT_GRAY_CANDLE);
    public static final DeferredBlock<ChandelierBlock> CHANDELIER_CYAN = registerChandelier("cyan", Items.CYAN_CANDLE);
    public static final DeferredBlock<ChandelierBlock> CHANDELIER_PURPLE = registerChandelier("purple", Items.PURPLE_CANDLE);
    public static final DeferredBlock<ChandelierBlock> CHANDELIER_BLUE = registerChandelier("blue", Items.BLUE_CANDLE);
    public static final DeferredBlock<ChandelierBlock> CHANDELIER_BROWN = registerChandelier("brown", Items.BROWN_CANDLE);
    public static final DeferredBlock<ChandelierBlock> CHANDELIER_GREEN = registerChandelier("green", Items.GREEN_CANDLE);
    public static final DeferredBlock<ChandelierBlock> CHANDELIER_RED = registerChandelier("red", Items.RED_CANDLE);
    public static final DeferredBlock<ChandelierBlock> CHANDELIER_BLACK = registerChandelier("black", Items.BLACK_CANDLE);

    /**
     * TUTORIAL:
     * - Register blocks here.
     * - To register itemblock, use {@link ModBlocks#registerWithItem}
     * - Register blockstate in {@link de.teamlapen.vampirism.data.provider.BlockStateProvider#registerStatesAndModels()} (pass existent model if desired)
     * - Maybe set render layer in the json model or blockstate generator.
     * - Register itemrender in {@link de.teamlapen.vampirism.data.provider.ItemModelGenerator#registerModels()}
     * - Register loot table in {@link de.teamlapen.vampirism.data.provider.LootTablesProvider.ModBlockLootTables#addTables()}
     * - Add lang keys
     * - Consider adding tool type in {@link de.teamlapen.vampirism.data.provider.tags.ModBlockTagsProvider}
     * - Run genData (twice?)
     */
    @SuppressWarnings("JavadocReference")
    private static <T extends Block> DeferredBlock<T> registerWithItem(String name, Function<BlockBehaviour.Properties, T> supplier, Supplier<BlockBehaviour.Properties> blockProperties, Function<Item.@NotNull Properties, Item.Properties> properties) {
        DeferredBlock<T> block = BLOCKS.registerBlock(name, prop -> supplier.apply(blockProperties.get().setId(ResourceKey.create(Registries.BLOCK, VResourceLocation.mod(name)))));
        createItem(name, block, BlockItem::new, properties);
        return block;
    }

    private static <T extends Block> DeferredBlock<T> registerWithItem(String name, Function<BlockBehaviour.Properties, T> supplier, Function<Item.@NotNull Properties, Item.Properties> properties) {
        DeferredBlock<T> block = BLOCKS.registerBlock(name, supplier);
        createItem(name, block, BlockItem::new, properties);
        return block;
    }

    private static <T extends Block, R extends Item> DeferredBlock<T> registerWithItem(String name, Function<BlockBehaviour.Properties, T> supplier, @NotNull BiFunction<T, Item.Properties, R> itemCreator) {
        DeferredBlock<T> block = BLOCKS.registerBlock(name, supplier);
        createItem(name, block, itemCreator, prop -> prop);
        return block;
    }

    private static <T extends Block, R extends Item> DeferredBlock<T> registerWithItem(String name, Function<BlockBehaviour.Properties, T> supplier, @NotNull BiFunction<T, Item.Properties, R> itemCreator, Function<Item.@NotNull Properties, Item.Properties> properties) {
        DeferredBlock<T> block = BLOCKS.registerBlock(name, supplier);
        createItem(name, block, itemCreator, properties);
        return block;
    }

    private static <T extends Block, R extends Item> void createItem(String name, Supplier<T> block, BiFunction<T, Item.Properties, R> itemCreator, Function<Item.@NotNull Properties, Item.Properties> properties) {
        ModItems.register(name, prop -> itemCreator.apply(block.get(), properties.apply(prop).overrideDescription(block.get().getDescriptionId())));
    }

    private static <T extends Block> DeferredBlock<T> registerWithItem(String name, Function<BlockBehaviour.Properties,T> supplier) {
        return registerWithItem(name, supplier, x -> x);
    }

    private static <T extends Block> DeferredBlock<T> registerWithItem(String name, Function<BlockBehaviour.Properties,T> supplier, Supplier<BlockBehaviour.Properties> blockProperties) {
        return registerWithItem(name, supplier, blockProperties, x -> x);
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String item, Function<BlockBehaviour.Properties,T> supplier, Supplier<BlockBehaviour.Properties> blockProperties) {
        return BLOCKS.registerBlock(item, prop -> supplier.apply(blockProperties.get().setId(ResourceKey.create(Registries.BLOCK, VResourceLocation.mod(item)))));
    }

    private static DeferredBlock<FlowerPotBlock> registerPottedPlant(String name, DeferredBlock<?> plantBlock) {
        return registerBlock(name, (properties) -> {
            FlowerPotBlock block = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, plantBlock, properties);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(plantBlock.getId(), () -> block);
            return block;
        }, () -> Block.Properties.ofFullCopy(Blocks.FLOWER_POT));
    }

    private static DeferredBlock<StandingCandleStickBlock> registerCandleStick(String suffix, Item candle) {
        return registerBlock("candle_stick_" + suffix, (prop) -> {
            StandingCandleStickBlock block = new StandingCandleStickBlock(CANDLE_STICK, () -> candle, prop);
            CANDLE_STICK.get().addCandle(BuiltInRegistries.ITEM.getKey(candle), () -> block);
            return block;
        }, () -> BlockBehaviour.Properties.ofFullCopy(CANDLE_STICK.get()));
    }

    private static DeferredBlock<WallCandleStickBlock> registerWallCandleStick(String suffix, Item candle, DeferredBlock<StandingCandleStickBlock> standingBlock) {
        return registerBlock("wall_candle_stick_" + suffix, (prop) -> {
            WallCandleStickBlock block = new WallCandleStickBlock(WALL_CANDLE_STICK, () -> candle, prop.overrideLootTable(standingBlock.get().getLootTable()));
            WALL_CANDLE_STICK.get().addCandle(BuiltInRegistries.ITEM.getKey(candle), () -> block);
            return block;
        }, () -> BlockBehaviour.Properties.ofFullCopy(standingBlock.get()));
    }

    private static DeferredBlock<StandingCandelabraBlock> registerCandelabra(String suffix, Item candle) {
        return registerBlock("candelabra_" + suffix, (prop) -> {
            StandingCandelabraBlock block = new StandingCandelabraBlock(CANDELABRA, () -> candle, prop);
            CANDELABRA.get().addCandle(BuiltInRegistries.ITEM.getKey(candle), () -> block);
            return block;
        }, () -> BlockBehaviour.Properties.ofFullCopy(CANDELABRA.get()));
    }

    private static DeferredBlock<WallCandelabraBlock> registerWallCandelabra(String suffix, Item candle, DeferredBlock<StandingCandelabraBlock> standingBlock) {
        return registerBlock("wall_candelabra_" + suffix, (prop) -> {
            WallCandelabraBlock block = new WallCandelabraBlock(WALL_CANDELABRA, () -> candle, prop.strength(3.5f).overrideLootTable(standingBlock.get().getLootTable()));
            WALL_CANDELABRA.get().addCandle(BuiltInRegistries.ITEM.getKey(candle), () -> block);
            return block;
        }, () -> BlockBehaviour.Properties.ofFullCopy(standingBlock.get()));
    }

    private static DeferredBlock<ChandelierBlock> registerChandelier(String suffix, Item candle) {
        return registerBlock("chandelier_" + suffix, (prop) -> {
            ChandelierBlock block = new ChandelierBlock(CHANDELIER, () -> candle, prop);
            CHANDELIER.get().addCandle(BuiltInRegistries.ITEM.getKey(candle), () -> block);
            return block;
        }, () -> BlockBehaviour.Properties.ofFullCopy(CHANDELIER.get()));
    }

    private static ToIntFunction<BlockState> litBlockEmission(int lightValue) {
        return state -> state.getValue(BlockStateProperties.LIT) ? lightValue : 0;
    }

    public static @NotNull Set<Block> getAllBlocks() {
        return BLOCKS.getEntries().stream().map(DeferredHolder::get).collect(Collectors.toUnmodifiableSet());
    }

    @SuppressWarnings("unchecked")
    public static @NotNull Stream<Holder<Block>> listElements() {
        return ((Collection<Holder<Block>>)(Object)BLOCKS.getEntries()).stream();
    }

    static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
