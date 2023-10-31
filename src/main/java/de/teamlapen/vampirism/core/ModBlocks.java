package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.blocks.BushBlock;
import de.teamlapen.vampirism.blocks.*;
import de.teamlapen.vampirism.blocks.mother.ActiveVulnerableRemainsBlock;
import de.teamlapen.vampirism.blocks.mother.MotherBlock;
import de.teamlapen.vampirism.blocks.mother.RemainsBlock;
import de.teamlapen.vampirism.items.MotherTrophyItem;
import de.teamlapen.vampirism.util.BlockVoxelshapes;
import de.teamlapen.vampirism.world.gen.CursedSpruceTree;
import de.teamlapen.vampirism.world.gen.DarkSpruceTreeGrower;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static de.teamlapen.lib.lib.util.RegisterHelper.*;

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
    public static final RegistryObject<BloodContainerBlock> BLOOD_CONTAINER = registerWithItem("blood_container", BloodContainerBlock::new, new Item.Properties().stacksTo(1));
    public static final RegistryObject<GrinderBlock> BLOOD_GRINDER = registerWithItem("blood_grinder", GrinderBlock::new);
    public static final RegistryObject<PedestalBlock> BLOOD_PEDESTAL = registerWithItem("blood_pedestal", PedestalBlock::new);
    public static final RegistryObject<SieveBlock> BLOOD_SIEVE = registerWithItem("blood_sieve", SieveBlock::new);
    public static final RegistryObject<DarkStoneBlock> CASTLE_BLOCK_PURPLE_BRICK = registerWithItem("castle_block_purple_brick", () -> new DarkStoneBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(2f, 10f).sound(SoundType.STONE)));
    public static final RegistryObject<DarkStoneSlabBlock> CASTLE_SLAB_PURPLE_BRICK = registerWithItem("castle_slab_purple_brick", () -> new DarkStoneSlabBlock(BlockBehaviour.Properties.copy(CASTLE_BLOCK_PURPLE_BRICK.get())));
    public static final RegistryObject<DarkStoneStairsBlock> CASTLE_STAIRS_PURPLE_BRICK = registerWithItem("castle_stairs_purple_brick", () -> new DarkStoneStairsBlock(CASTLE_BLOCK_PURPLE_BRICK.map(Block::defaultBlockState)::get, BlockBehaviour.Properties.copy(CASTLE_BLOCK_PURPLE_BRICK.get())));
    public static final RegistryObject<AltarCleansingBlock> ALTAR_CLEANSING = registerWithItem("altar_cleansing", AltarCleansingBlock::new);
    public static final RegistryObject<CursedEarthBlock> CURSED_EARTH = registerWithItem("cursed_earth", CursedEarthBlock::new);
    public static final RegistryObject<FirePlaceBlock> FIRE_PLACE = registerWithItem("fire_place", FirePlaceBlock::new);
    public static final RegistryObject<GarlicBlock> GARLIC = BLOCKS.register("garlic", GarlicBlock::new);
    public static final RegistryObject<GarlicDiffuserBlock> GARLIC_DIFFUSER_IMPROVED = registerWithItem("garlic_diffuser_improved", () -> new GarlicDiffuserBlock(GarlicDiffuserBlock.Type.IMPROVED));
    public static final RegistryObject<GarlicDiffuserBlock> GARLIC_DIFFUSER_NORMAL = registerWithItem("garlic_diffuser_normal", () -> new GarlicDiffuserBlock(GarlicDiffuserBlock.Type.NORMAL));
    public static final RegistryObject<GarlicDiffuserBlock> GARLIC_DIFFUSER_WEAK = registerWithItem("garlic_diffuser_weak", () -> new GarlicDiffuserBlock(GarlicDiffuserBlock.Type.WEAK));
    public static final RegistryObject<HunterTableBlock> HUNTER_TABLE = registerWithItem("hunter_table", HunterTableBlock::new);
    public static final RegistryObject<MedChairBlock> MED_CHAIR = registerWithItem("med_chair", MedChairBlock::new);
    public static final RegistryObject<SunscreenBeaconBlock> SUNSCREEN_BEACON = registerWithItem("sunscreen_beacon", SunscreenBeaconBlock::new, new Item.Properties().rarity(Rarity.RARE));
    public static final RegistryObject<TentBlock> TENT = BLOCKS.register("tent", TentBlock::new);
    public static final RegistryObject<TentMainBlock> TENT_MAIN = BLOCKS.register("tent_main", TentMainBlock::new);
    public static final RegistryObject<TotemBaseBlock> TOTEM_BASE = registerWithItem("totem_base", TotemBaseBlock::new);
    public static final RegistryObject<TotemTopBlock> TOTEM_TOP = registerWithItem("totem_top", () -> new TotemTopBlock(false, new ResourceLocation("none")));
    public static final RegistryObject<TotemTopBlock> TOTEM_TOP_VAMPIRISM_VAMPIRE = BLOCKS.register("totem_top_vampirism_vampire", () -> new TotemTopBlock(false, REFERENCE.VAMPIRE_PLAYER_KEY));
    public static final RegistryObject<TotemTopBlock> TOTEM_TOP_VAMPIRISM_HUNTER = BLOCKS.register("totem_top_vampirism_hunter", () -> new TotemTopBlock(false, REFERENCE.HUNTER_PLAYER_KEY));
    public static final RegistryObject<TotemTopBlock> TOTEM_TOP_CRAFTED = registerWithItem("totem_top_crafted", () -> new TotemTopBlock(true, new ResourceLocation("none")));
    public static final RegistryObject<TotemTopBlock> TOTEM_TOP_VAMPIRISM_VAMPIRE_CRAFTED = BLOCKS.register("totem_top_vampirism_vampire_crafted", () -> new TotemTopBlock(true, REFERENCE.VAMPIRE_PLAYER_KEY));
    public static final RegistryObject<TotemTopBlock> TOTEM_TOP_VAMPIRISM_HUNTER_CRAFTED = BLOCKS.register("totem_top_vampirism_hunter_crafted", () -> new TotemTopBlock(true, REFERENCE.HUNTER_PLAYER_KEY));
    public static final RegistryObject<VampirismFlowerBlock> VAMPIRE_ORCHID = registerWithItem("vampire_orchid", () -> compostable(new VampirismFlowerBlock(VampirismFlowerBlock.TYPE.ORCHID), 0.65f));
    public static final RegistryObject<FlowerPotBlock> POTTED_VAMPIRE_ORCHID = BLOCKS.register("potted_vampire_orchid", () -> potted(new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, VAMPIRE_ORCHID, Block.Properties.of().noCollission().isViewBlocking(UtilLib::never).pushReaction(PushReaction.DESTROY).instabreak()), VAMPIRE_ORCHID.getId()));
    public static final RegistryObject<WeaponTableBlock> WEAPON_TABLE = registerWithItem("weapon_table", WeaponTableBlock::new);
    public static final RegistryObject<PotionTableBlock> POTION_TABLE = registerWithItem("potion_table", PotionTableBlock::new);
    public static final RegistryObject<DarkSpruceLeavesBlock> DARK_SPRUCE_LEAVES = registerWithItem("dark_spruce_leaves", DarkSpruceLeavesBlock::new);
    public static final RegistryObject<VampirismBlock> CHANDELIER = registerWithItem("chandelier", ChandelierBlock::new);
    public static final RegistryObject<VampirismBlock> CANDELABRA = BLOCKS.register("candelabra", CandelabraBlock::new);
    public static final RegistryObject<VampirismBlock> CANDELABRA_WALL = BLOCKS.register("candelabra_wall", CandelabraWallBlock::new);
    public static final RegistryObject<VampirismBlock> CROSS = registerWithItem("cross", () -> new VampirismSplitBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY).mapColor(MapColor.WOOD).ignitedByLava().strength(2, 3), BlockVoxelshapes.crossBottom, BlockVoxelshapes.crossTop, true).markDecorativeBlock());
    public static final RegistryObject<VampirismBlock> TOMBSTONE1 = registerWithItem("tombstone1", () -> new VampirismHorizontalBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(2, 6), BlockVoxelshapes.tomb1).markDecorativeBlock());
    public static final RegistryObject<VampirismBlock> TOMBSTONE2 = registerWithItem("tombstone2", () -> new VampirismHorizontalBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(2, 6), BlockVoxelshapes.tomb2).markDecorativeBlock());
    public static final RegistryObject<VampirismBlock> TOMBSTONE3 = registerWithItem("tombstone3", () -> new VampirismSplitBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).pushReaction(PushReaction.DESTROY).strength(2, 6), BlockVoxelshapes.tomb3_base, BlockVoxelshapes.tomb3_top, true).markDecorativeBlock());
    public static final RegistryObject<VampirismBlock> GRAVE_CAGE = registerWithItem("grave_cage", () -> new VampirismHorizontalBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(6, 8).requiresCorrectToolForDrops().sound(SoundType.METAL), BlockVoxelshapes.grave_cage).markDecorativeBlock());
    public static final RegistryObject<CursedGrass> CURSED_GRASS = registerWithItem("cursed_grass", () -> new CursedGrass(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).randomTicks().strength(0.6F).sound(SoundType.GRASS)));
    public static final RegistryObject<BushBlock> CURSED_ROOTS = registerWithItem("cursed_roots", () -> flammable(new BushBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).isViewBlocking(UtilLib::never).pushReaction(PushReaction.DESTROY).ignitedByLava().replaceable().noCollission().instabreak().sound(SoundType.GRASS)),60, 100));
    public static final RegistryObject<Block> POTTED_CURSED_ROOTS = BLOCKS.register("potted_cursed_roots", () -> potted(new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, CURSED_ROOTS, Block.Properties.of().noCollission().isViewBlocking(UtilLib::never).pushReaction(PushReaction.DESTROY).ignitedByLava().replaceable().instabreak().noOcclusion()), CURSED_ROOTS.getId()));
    public static final RegistryObject<Block> DARK_SPRUCE_PLANKS = registerWithItem("dark_spruce_planks", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).ignitedByLava().mapColor(MapColor.COLOR_GRAY).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> CURSED_SPRUCE_PLANKS = registerWithItem("cursed_spruce_planks", () -> new Block(BlockBehaviour.Properties.of().ignitedByLava().mapColor(MapColor.CRIMSON_HYPHAE).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<LogBlock> STRIPPED_DARK_SPRUCE_LOG = registerWithItem("stripped_dark_spruce_log", () -> new LogBlock(MapColor.COLOR_BLACK, MapColor.COLOR_GRAY));
    public static final RegistryObject<LogBlock> STRIPPED_CURSED_SPRUCE_LOG = registerWithItem("stripped_cursed_spruce_log", () -> new LogBlock(MapColor.COLOR_BLACK, MapColor.CRIMSON_HYPHAE));
    public static final RegistryObject<LogBlock> DARK_SPRUCE_LOG = registerWithItem("dark_spruce_log", () -> new StrippableLogBlock(MapColor.COLOR_BLACK, MapColor.COLOR_BLACK, STRIPPED_DARK_SPRUCE_LOG));
    public static final RegistryObject<CursedSpruceBlock> CURSED_SPRUCE_LOG_CURED = registerWithItem("cursed_spruce_log_cured", () -> new CursedSpruceBlock(STRIPPED_CURSED_SPRUCE_LOG));
    public static final RegistryObject<LogBlock> CURSED_SPRUCE_LOG = registerWithItem("cursed_spruce_log", () -> new CursedSpruceBlock(STRIPPED_CURSED_SPRUCE_LOG, CURSED_SPRUCE_LOG_CURED));
    public static final RegistryObject<SaplingBlock> DARK_SPRUCE_SAPLING = registerWithItem("dark_spruce_sapling", () -> new DarkSpruceSaplingBlock(new DarkSpruceTreeGrower(), new CursedSpruceTree(), BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).isViewBlocking(UtilLib::never).replaceable().pushReaction(PushReaction.DESTROY).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)));
    public static final RegistryObject<SaplingBlock> CURSED_SPRUCE_SAPLING = registerWithItem("cursed_spruce_sapling", () -> new SaplingBlock(new CursedSpruceTree(), BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).isViewBlocking(UtilLib::never).replaceable().pushReaction(PushReaction.DESTROY).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)));
    public static final RegistryObject<DirectCursedBarkBlock> DIRECT_CURSED_BARK = registerWithItem("direct_cursed_bark", DirectCursedBarkBlock::new);
    public static final RegistryObject<DiagonalCursedBarkBlock> DIAGONAL_CURSED_BARK = BLOCKS.register("diagonal_cursed_bark", DiagonalCursedBarkBlock::new);
    public static final RegistryObject<DoorBlock> DARK_SPRUCE_DOOR = registerWithItem("dark_spruce_door", () -> new DoorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).ignitedByLava().strength(3.0F).sound(SoundType.WOOD).noOcclusion(), BlockSetType.SPRUCE));
    public static final RegistryObject<DoorBlock> CURSED_SPRUCE_DOOR = registerWithItem("cursed_spruce_door", () -> new DoorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.CRIMSON_HYPHAE).ignitedByLava().strength(3.0F).sound(SoundType.WOOD).noOcclusion(), BlockSetType.SPRUCE));
    public static final RegistryObject<TrapDoorBlock> DARK_SPRUCE_TRAPDOOR = registerWithItem("dark_spruce_trapdoor", () -> new TrapDoorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).ignitedByLava().strength(3.0F).sound(SoundType.WOOD).noOcclusion().isValidSpawn((p_61031_, p_61032_, p_61033_, p_61034_) -> false), BlockSetType.SPRUCE));
    public static final RegistryObject<TrapDoorBlock> CURSED_SPRUCE_TRAPDOOR = registerWithItem("cursed_spruce_trapdoor", () -> new TrapDoorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.CRIMSON_HYPHAE).ignitedByLava().strength(3.0F).sound(SoundType.WOOD).noOcclusion().isValidSpawn((p_61031_, p_61032_, p_61033_, p_61034_) -> false), BlockSetType.SPRUCE));
    public static final RegistryObject<StairBlock> DARK_SPRUCE_STAIRS = registerWithItem("dark_spruce_stairs", () -> new StairBlock(() -> DARK_SPRUCE_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.copy(DARK_SPRUCE_PLANKS.get())));
    public static final RegistryObject<StairBlock> CURSED_SPRUCE_STAIRS = registerWithItem("cursed_spruce_stairs", () -> new StairBlock(() -> CURSED_SPRUCE_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.copy(CURSED_SPRUCE_PLANKS.get())));
    public static final RegistryObject<LogBlock> STRIPPED_DARK_SPRUCE_WOOD = registerWithItem("stripped_dark_spruce_wood", () -> new LogBlock(MapColor.COLOR_BLACK, MapColor.COLOR_GRAY));
    public static final RegistryObject<LogBlock> STRIPPED_CURSED_SPRUCE_WOOD = registerWithItem("stripped_cursed_spruce_wood", () -> new LogBlock(MapColor.COLOR_BLACK, MapColor.CRIMSON_HYPHAE));
    public static final RegistryObject<LogBlock> DARK_SPRUCE_WOOD = registerWithItem("dark_spruce_wood", () -> new StrippableLogBlock(MapColor.COLOR_BLACK, MapColor.COLOR_BLACK, STRIPPED_DARK_SPRUCE_WOOD));
    public static final RegistryObject<CursedSpruceBlock> CURSED_SPRUCE_WOOD_CURED = registerWithItem("cursed_spruce_wood_cured", () -> new CursedSpruceBlock(STRIPPED_CURSED_SPRUCE_LOG));
    public static final RegistryObject<LogBlock> CURSED_SPRUCE_WOOD = registerWithItem("cursed_spruce_wood", () -> new CursedSpruceBlock(STRIPPED_CURSED_SPRUCE_WOOD, CURSED_SPRUCE_WOOD_CURED));
    public static final RegistryObject<StandingSignBlock> DARK_SPRUCE_SIGN = BLOCKS.register("dark_spruce_sign", () -> new StandingSignBlock(BlockBehaviour.Properties.of().mapColor(DARK_SPRUCE_LOG.get().defaultMapColor()).ignitedByLava().noCollission().strength(1.0F).sound(SoundType.WOOD), LogBlock.DARK_SPRUCE));
    public static final RegistryObject<StandingSignBlock> CURSED_SPRUCE_SIGN = BLOCKS.register("cursed_spruce_sign", () -> new StandingSignBlock(BlockBehaviour.Properties.of().mapColor(CURSED_SPRUCE_LOG.get().defaultMapColor()).ignitedByLava().noCollission().strength(1.0F).sound(SoundType.WOOD), LogBlock.CURSED_SPRUCE));
    public static final RegistryObject<WallSignBlock> DARK_SPRUCE_WALL_SIGN = BLOCKS.register("dark_spruce_wall_sign", () -> new WallSignBlock(BlockBehaviour.Properties.of().mapColor(DARK_SPRUCE_LOG.get().defaultMapColor()).ignitedByLava().noCollission().strength(1.0F).sound(SoundType.WOOD).lootFrom(DARK_SPRUCE_SIGN), LogBlock.DARK_SPRUCE));
    public static final RegistryObject<WallSignBlock> CURSED_SPRUCE_WALL_SIGN = BLOCKS.register("cursed_spruce_wall_sign", () -> new WallSignBlock(BlockBehaviour.Properties.of().mapColor(CURSED_SPRUCE_LOG.get().defaultMapColor()).ignitedByLava().noCollission().strength(1.0F).sound(SoundType.WOOD).lootFrom(CURSED_SPRUCE_SIGN), LogBlock.CURSED_SPRUCE));
    public static final RegistryObject<PressurePlateBlock> DARK_SPRUCE_PRESSURE_PLACE = registerWithItem("dark_spruce_pressure_place", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.of().mapColor(DARK_SPRUCE_PLANKS.get().defaultMapColor()).ignitedByLava().noCollission().strength(0.5F).sound(SoundType.WOOD), BlockSetType.SPRUCE));//TODO fix name
    public static final RegistryObject<PressurePlateBlock> CURSED_SPRUCE_PRESSURE_PLACE = registerWithItem("cursed_spruce_pressure_place", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.of().mapColor(CURSED_SPRUCE_PLANKS.get().defaultMapColor()).ignitedByLava().noCollission().strength(0.5F).sound(SoundType.WOOD), BlockSetType.SPRUCE));
    public static final RegistryObject<ButtonBlock> DARK_SPRUCE_BUTTON = registerWithItem("dark_spruce_button", () -> new ButtonBlock(BlockBehaviour.Properties.of().noCollission().isViewBlocking(UtilLib::never).pushReaction(PushReaction.DESTROY).ignitedByLava().replaceable().strength(0.5F).sound(SoundType.WOOD), BlockSetType.SPRUCE, 30, true));
    public static final RegistryObject<ButtonBlock> CURSED_SPRUCE_BUTTON = registerWithItem("cursed_spruce_button", () -> new ButtonBlock(BlockBehaviour.Properties.of().noCollission().isViewBlocking(UtilLib::never).pushReaction(PushReaction.DESTROY).ignitedByLava().replaceable().strength(0.5F).sound(SoundType.WOOD), BlockSetType.SPRUCE, 30, true));
    public static final RegistryObject<SlabBlock> DARK_SPRUCE_SLAB = registerWithItem("dark_spruce_slab", () -> new SlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).ignitedByLava().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<SlabBlock> CURSED_SPRUCE_SLAB = registerWithItem("cursed_spruce_slab", () -> new SlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.CRIMSON_HYPHAE).ignitedByLava().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<FenceGateBlock> DARK_SPRUCE_FENCE_GATE = registerWithItem("dark_spruce_fence_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.of().mapColor(DARK_SPRUCE_PLANKS.get().defaultMapColor()).ignitedByLava().strength(2.0F, 3.0F).sound(SoundType.WOOD), LogBlock.DARK_SPRUCE));
    public static final RegistryObject<FenceGateBlock> CURSED_SPRUCE_FENCE_GATE = registerWithItem("cursed_spruce_fence_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.of().mapColor(CURSED_SPRUCE_PLANKS.get().defaultMapColor()).ignitedByLava().strength(2.0F, 3.0F).sound(SoundType.WOOD), LogBlock.CURSED_SPRUCE));
    public static final RegistryObject<FenceBlock> DARK_SPRUCE_FENCE = registerWithItem("dark_spruce_fence", () -> new FenceBlock(BlockBehaviour.Properties.of().mapColor(DARK_SPRUCE_PLANKS.get().defaultMapColor()).ignitedByLava().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<FenceBlock> CURSED_SPRUCE_FENCE = registerWithItem("cursed_spruce_fence", () -> new FenceBlock(BlockBehaviour.Properties.of().mapColor(CURSED_SPRUCE_PLANKS.get().defaultMapColor()).ignitedByLava().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<VampirismBlock> VAMPIRE_RACK = registerWithItem("vampire_rack", () -> new VampirismHorizontalBlock(BlockBehaviour.Properties.of().ignitedByLava().strength(2, 3), BlockVoxelshapes.vampire_rack).markDecorativeBlock());
    public static final RegistryObject<VampirismBlock> THRONE = registerWithItem("throne", ThroneBlock::new);
    public static final RegistryObject<CoffinBlock> COFFIN_WHITE = registerWithItem("coffin_white", () -> new CoffinBlock(DyeColor.WHITE));
    public static final RegistryObject<CoffinBlock> COFFIN_ORANGE = registerWithItem("coffin_orange", () -> new CoffinBlock(DyeColor.ORANGE));
    public static final RegistryObject<CoffinBlock> COFFIN_MAGENTA = registerWithItem("coffin_magenta", () -> new CoffinBlock(DyeColor.MAGENTA));
    public static final RegistryObject<CoffinBlock> COFFIN_LIGHT_BLUE = registerWithItem("coffin_light_blue", () -> new CoffinBlock(DyeColor.LIGHT_BLUE));
    public static final RegistryObject<CoffinBlock> COFFIN_YELLOW = registerWithItem("coffin_yellow", () -> new CoffinBlock(DyeColor.YELLOW));
    public static final RegistryObject<CoffinBlock> COFFIN_LIME = registerWithItem("coffin_lime", () -> new CoffinBlock(DyeColor.LIME));
    public static final RegistryObject<CoffinBlock> COFFIN_PINK = registerWithItem("coffin_pink", () -> new CoffinBlock(DyeColor.PINK));
    public static final RegistryObject<CoffinBlock> COFFIN_GRAY = registerWithItem("coffin_gray", () -> new CoffinBlock(DyeColor.GRAY));
    public static final RegistryObject<CoffinBlock> COFFIN_LIGHT_GRAY = registerWithItem("coffin_light_gray", () -> new CoffinBlock(DyeColor.LIGHT_GRAY));
    public static final RegistryObject<CoffinBlock> COFFIN_CYAN = registerWithItem("coffin_cyan", () -> new CoffinBlock(DyeColor.CYAN));
    public static final RegistryObject<CoffinBlock> COFFIN_PURPLE = registerWithItem("coffin_purple", () -> new CoffinBlock(DyeColor.PURPLE));
    public static final RegistryObject<CoffinBlock> COFFIN_BLUE = registerWithItem("coffin_blue", () -> new CoffinBlock(DyeColor.BLUE));
    public static final RegistryObject<CoffinBlock> COFFIN_BROWN = registerWithItem("coffin_brown", () -> new CoffinBlock(DyeColor.BROWN));
    public static final RegistryObject<CoffinBlock> COFFIN_GREEN = registerWithItem("coffin_green", () -> new CoffinBlock(DyeColor.GREEN));
    public static final RegistryObject<CoffinBlock> COFFIN_RED = registerWithItem("coffin_red", () -> new CoffinBlock(DyeColor.RED));
    public static final RegistryObject<CoffinBlock> COFFIN_BLACK = registerWithItem("coffin_black", () -> new CoffinBlock(DyeColor.BLACK));
    public static final RegistryObject<AlchemyTableBlock> ALCHEMY_TABLE = registerWithItem("alchemy_table", AlchemyTableBlock::new);
    public static final RegistryObject<CeilingHangingSignBlock> DARK_SPRUCE_HANGING_SIGN = BLOCKS.register("dark_spruce_hanging_sign", () -> new CeilingHangingSignBlock(BlockBehaviour.Properties.of().mapColor(DARK_SPRUCE_LOG.get().defaultMapColor()).ignitedByLava().noCollission().strength(1.0F).sound(SoundType.WOOD), LogBlock.DARK_SPRUCE));
    public static final RegistryObject<CeilingHangingSignBlock> CURSED_SPRUCE_HANGING_SIGN = BLOCKS.register("cursed_spruce_hanging_sign", () -> new CeilingHangingSignBlock(BlockBehaviour.Properties.of().mapColor(CURSED_SPRUCE_LOG.get().defaultMapColor()).ignitedByLava().noCollission().strength(1.0F).sound(SoundType.WOOD), LogBlock.CURSED_SPRUCE));
    public static final RegistryObject<WallHangingSignBlock> DARK_SPRUCE_WALL_HANGING_SIGN = BLOCKS.register("dark_spruce_wall_hanging_sign", () -> new WallHangingSignBlock(BlockBehaviour.Properties.of().mapColor(DARK_SPRUCE_LOG.get().defaultMapColor()).ignitedByLava().noCollission().strength(1.0F).sound(SoundType.WOOD).lootFrom(DARK_SPRUCE_HANGING_SIGN), LogBlock.DARK_SPRUCE));
    public static final RegistryObject<WallHangingSignBlock> CURSED_SPRUCE_WALL_HANGING_SIGN = BLOCKS.register("cursed_spruce_wall_hanging_sign", () -> new WallHangingSignBlock(BlockBehaviour.Properties.of().mapColor(CURSED_SPRUCE_LOG.get().defaultMapColor()).ignitedByLava().noCollission().strength(1.0F).sound(SoundType.WOOD).lootFrom(CURSED_SPRUCE_HANGING_SIGN), LogBlock.CURSED_SPRUCE));
    public static final RegistryObject<CursedEarthPathBlock> CURSED_EARTH_PATH = registerWithItem("cursed_earth_path", () -> new CursedEarthPathBlock(BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).strength(0.65F).sound(SoundType.GRASS).isViewBlocking(UtilLib::always).isSuffocating(UtilLib::always)));
    public static final RegistryObject<WallBlock> CASTLE_BLOCK_PURPLE_BRICK_WALL = registerWithItem("castle_block_purple_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(ModBlocks.CASTLE_BLOCK_PURPLE_BRICK.get()).forceSolidOn()));
    public static final RegistryObject<DarkStoneBlock> DARK_STONE = registerWithItem("dark_stone", () -> new DarkStoneBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(2f, 10f).sound(SoundType.STONE)));
    public static final RegistryObject<DarkStoneStairsBlock> DARK_STONE_STAIRS = registerWithItem("dark_stone_stairs", () -> new DarkStoneStairsBlock(DARK_STONE.map(Block::defaultBlockState)::get, BlockBehaviour.Properties.copy(DARK_STONE.get())));
    public static final RegistryObject<DarkStoneSlabBlock> DARK_STONE_SLAB = registerWithItem("dark_stone_slab", () -> new DarkStoneSlabBlock(BlockBehaviour.Properties.copy(DARK_STONE.get())));
    public static final RegistryObject<WallBlock> DARK_STONE_WALL = registerWithItem("dark_stone_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(DARK_STONE.get()).forceSolidOn()));
    public static final RegistryObject<DarkStoneBlock> DARK_STONE_BRICKS = registerWithItem("dark_stone_bricks", () -> new DarkStoneBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(2f, 10f).sound(SoundType.STONE)));
    public static final RegistryObject<DarkStoneStairsBlock> DARK_STONE_BRICK_STAIRS = registerWithItem("dark_stone_brick_stairs", () -> new DarkStoneStairsBlock(DARK_STONE_BRICKS.map(Block::defaultBlockState)::get, BlockBehaviour.Properties.copy(DARK_STONE_BRICKS.get())));
    public static final RegistryObject<DarkStoneSlabBlock> DARK_STONE_BRICK_SLAB = registerWithItem("dark_stone_brick_slab", () -> new DarkStoneSlabBlock(BlockBehaviour.Properties.copy(DARK_STONE_BRICKS.get())));
    public static final RegistryObject<WallBlock> DARK_STONE_BRICK_WALL = registerWithItem("dark_stone_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(DARK_STONE_BRICKS.get()).forceSolidOn()));
    public static final RegistryObject<Block> CRACKED_DARK_STONE_BRICKS = registerWithItem("cracked_dark_stone_bricks", () -> new DarkStoneBlock(BlockBehaviour.Properties.copy(DARK_STONE_BRICKS.get())));
    public static final RegistryObject<DarkStoneBlock> COBBLED_DARK_STONE = registerWithItem("cobbled_dark_stone", () -> new DarkStoneBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(2.5f, 10f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final RegistryObject<DarkStoneStairsBlock> COBBLED_DARK_STONE_STAIRS = registerWithItem("cobbled_dark_stone_stairs", () -> new DarkStoneStairsBlock(ModBlocks.COBBLED_DARK_STONE.map(Block::defaultBlockState)::get, BlockBehaviour.Properties.copy(COBBLED_DARK_STONE.get())));
    public static final RegistryObject<DarkStoneSlabBlock> COBBLED_DARK_STONE_SLAB = registerWithItem("cobbled_dark_stone_slab", () -> new DarkStoneSlabBlock(BlockBehaviour.Properties.copy(COBBLED_DARK_STONE.get())));
    public static final RegistryObject<WallBlock> COBBLED_DARK_STONE_WALL = registerWithItem("cobbled_dark_stone_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(COBBLED_DARK_STONE.get()).forceSolidOn()));
    public static final RegistryObject<DarkStoneBlock> POLISHED_DARK_STONE = registerWithItem("polished_dark_stone", () -> new DarkStoneBlock(BlockBehaviour.Properties.copy(COBBLED_DARK_STONE.get())));
    public static final RegistryObject<DarkStoneStairsBlock> POLISHED_DARK_STONE_STAIRS = registerWithItem("polished_dark_stone_stairs", () -> new DarkStoneStairsBlock(POLISHED_DARK_STONE.map(Block::defaultBlockState)::get, BlockBehaviour.Properties.copy(POLISHED_DARK_STONE.get())));
    public static final RegistryObject<DarkStoneSlabBlock> POLISHED_DARK_STONE_SLAB = registerWithItem("polished_dark_stone_slab", () -> new DarkStoneSlabBlock(BlockBehaviour.Properties.copy(POLISHED_DARK_STONE.get())));
    public static final RegistryObject<WallBlock> POLISHED_DARK_STONE_WALL = registerWithItem("polished_dark_stone_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(POLISHED_DARK_STONE.get()).forceSolidOn()));
    public static final RegistryObject<DarkStoneBlock> DARK_STONE_TILES = registerWithItem("dark_stone_tiles", () -> new DarkStoneBlock(BlockBehaviour.Properties.copy(COBBLED_DARK_STONE.get())));
    public static final RegistryObject<DarkStoneBlock> CRACKED_DARK_STONE_TILES = registerWithItem("cracked_dark_stone_tiles", () -> new DarkStoneBlock(BlockBehaviour.Properties.copy(DARK_STONE_TILES.get())));
    public static final RegistryObject<DarkStoneStairsBlock> DARK_STONE_TILES_STAIRS = registerWithItem("dark_stone_tiles_stairs", () -> new DarkStoneStairsBlock(ModBlocks.DARK_STONE_TILES.map(Block::defaultBlockState)::get, BlockBehaviour.Properties.copy(DARK_STONE_TILES.get())));
    public static final RegistryObject<DarkStoneSlabBlock> DARK_STONE_TILES_SLAB = registerWithItem("dark_stone_tiles_slab", () -> new DarkStoneSlabBlock(BlockBehaviour.Properties.copy(DARK_STONE_TILES.get())));
    public static final RegistryObject<WallBlock> DARK_STONE_TILES_WALL = registerWithItem("dark_stone_tiles_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(DARK_STONE_TILES.get()).forceSolidOn()));
    public static final RegistryObject<DarkStoneBlock> CHISELED_DARK_STONE_BRICKS = registerWithItem("chiseled_dark_stone_bricks", () -> new DarkStoneBlock(BlockBehaviour.Properties.copy(DARK_STONE_BRICKS.get())));
    public static final RegistryObject<DarkStoneBlock> INFESTED_DARK_STONE = registerWithItem("infested_dark_stone", () -> new DarkStoneBlock(BlockBehaviour.Properties.copy(DARK_STONE.get())));
    public static final RegistryObject<DarkStoneBlock> BLOODY_DARK_STONE_BRICKS = registerWithItem("bloody_dark_stone_bricks", () -> new DarkStoneBlock(BlockBehaviour.Properties.copy(DARK_STONE_BRICKS.get())));
    public static final RegistryObject<Block> BAT_CAGE = registerWithItem("bat_cage", () -> new BatCageBlock(BlockBehaviour.Properties.of().strength(5.0F, 6.0F).sound(SoundType.METAL).noOcclusion()));
    public static final RegistryObject<RemainsBlock> REMAINS = BLOCKS.register("remains", () -> new RemainsBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(-1, 3600000.0F).sound(SoundType.ROOTED_DIRT).randomTicks().noLootTable(), false, false));
    public static final RegistryObject<RemainsBlock> VULNERABLE_REMAINS = BLOCKS.register("vulnerable_remains", () -> new RemainsBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(-1, 3600000.0F).sound(SoundType.ROOTED_DIRT).randomTicks().noLootTable(), true, true));
    public static final RegistryObject<RemainsBlock> INCAPACITATED_VULNERABLE_REMAINS = BLOCKS.register("incapacitated_vulnerable_remains", () -> new RemainsBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(-1.0F, 3600000.0F).sound(SoundType.ROOTED_DIRT).randomTicks().noLootTable(), false, true));
    public static final RegistryObject<ActiveVulnerableRemainsBlock> ACTIVE_VULNERABLE_REMAINS = BLOCKS.register("active_vulnerable_remains", () -> new ActiveVulnerableRemainsBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(-1, 3600000.0F).randomTicks().sound(SoundType.ROOTED_DIRT).noLootTable()));
    public static final RegistryObject<HangingRootsBlock> CURSED_HANGING_ROOTS = registerWithItem("cursed_hanging_roots", () -> {
        var block = new HangingRootsBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).ignitedByLava().pushReaction(PushReaction.DESTROY));
        ((FireBlock) Blocks.FIRE).setFlammable(block, 30, 60);
        return block;
    });
    public static final RegistryObject<MotherBlock> MOTHER = BLOCKS.register("mother", MotherBlock::new);
    public static final RegistryObject<Block> MOTHER_TROPHY = registerWithItem("mother_trophy", () -> new MotherTrophyBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3, 9).lightLevel(s -> 1).noOcclusion()), block -> new MotherTrophyItem(block, new Item.Properties().rarity(Rarity.EPIC).stacksTo(1)));
    public static final RegistryObject<FogDiffuserBlock> FOG_DIFFUSER = registerWithItem("fog_diffuser", () -> new FogDiffuserBlock(BlockBehaviour.Properties.of().noOcclusion().mapColor(MapColor.STONE).strength(40.0F, 1200.0F).sound(SoundType.STONE)));
    public static final RegistryObject<FlowerPotBlock> POTTED_DARK_SPRUCE_SAPLING = BLOCKS.register("potted_dark_spruce_sapling", () -> potted(new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, DARK_SPRUCE_SAPLING, Block.Properties.of().noCollission().isViewBlocking(UtilLib::never).pushReaction(PushReaction.DESTROY).instabreak()), DARK_SPRUCE_SAPLING.getId()));
    public static final RegistryObject<FlowerPotBlock> POTTED_CURSED_SPRUCE_SAPLING = BLOCKS.register("potted_cursed_spruce_sapling", () -> potted(new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, CURSED_SPRUCE_SAPLING, Block.Properties.of().noCollission().isViewBlocking(UtilLib::never).pushReaction(PushReaction.DESTROY).instabreak()), CURSED_SPRUCE_SAPLING.getId()));
    public static final RegistryObject<Block> BLOOD_INFUSED_IRON_BLOCK = registerWithItem("blood_infused_iron_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> BLOOD_INFUSED_ENHANCED_IRON_BLOCK = registerWithItem("blood_infused_enhanced_iron_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)));
    public static final RegistryObject<VampireBeaconBlock> VAMPIRE_BEACON = registerWithItem("vampire_beacon", () -> new VampireBeaconBlock(BlockBehaviour.Properties.of().mapColor(MapColor.DIAMOND).instrument(NoteBlockInstrument.HAT).strength(3.0F).lightLevel((p_50828_) -> 15).noOcclusion().isRedstoneConductor(UtilLib::never)), new Item.Properties().rarity(Rarity.RARE));


    /**
     * TUTORIAL:
     * - Register blocks here.
     * - To register itemblock, use {@link ModBlocks#registerWithItem}
     * - Register blockstate in {@link de.teamlapen.vampirism.data.BlockStateGenerator#registerStatesAndModels()} (pass existent model if desired)
     * - Maybe set render layer in the json model or blockstate generator.
     * - Register itemrender in {@link de.teamlapen.vampirism.data.ItemModelGenerator#registerModels()}
     * - Register loot table in {@link de.teamlapen.vampirism.data.LootTablesGenerator.ModBlockLootTables#addTables()}
     * - Add lang keys
     * - Consider adding tool type in {@link de.teamlapen.vampirism.data.TagGenerator.ModBlockTagsProvider}
     * - Run genData (twice?)
     */
    @SuppressWarnings("JavadocReference")
    private static <T extends Block> RegistryObject<T> registerWithItem(String name, Supplier<T> supplier, Item.@NotNull Properties properties) {
        RegistryObject<T> block = BLOCKS.register(name, supplier);
        ModItems.register(name, () -> new BlockItem(block.get(), properties));
        return block;
    }

    private static <T extends Block, R extends Item> RegistryObject<T> registerWithItem(String name, Supplier<T> supplier, @NotNull Function<T, R> itemMaker) {
        RegistryObject<T> block = BLOCKS.register(name, supplier);
        ModItems.register(name, () -> itemMaker.apply(block.get()));
        return block;
    }

    private static <T extends Block> RegistryObject<T> registerWithItem(String name, Supplier<T> supplier) {
        return registerWithItem(name, supplier, new Item.Properties());
    }

    public static void fixMappings(@NotNull MissingMappingsEvent event) {
        event.getAllMappings(ForgeRegistries.Keys.BLOCKS).forEach(missingMapping -> {
            switch (missingMapping.getKey().toString()) {
                case "vampirism:blood_potion_table" -> missingMapping.remap(ModBlocks.POTION_TABLE.get());
                case "vampirism:garlic_beacon_normal" -> missingMapping.remap(ModBlocks.GARLIC_DIFFUSER_NORMAL.get());
                case "vampirism:garlic_beacon_weak" -> missingMapping.remap(ModBlocks.GARLIC_DIFFUSER_WEAK.get());
                case "vampirism:garlic_beacon_improved" -> missingMapping.remap(ModBlocks.GARLIC_DIFFUSER_IMPROVED.get());
                case "vampirism:church_altar" -> missingMapping.remap(ModBlocks.ALTAR_CLEANSING.get());
                case "vampirism:vampire_spruce_leaves", "vampirism:bloody_spruce_leaves" -> missingMapping.remap(ModBlocks.DARK_SPRUCE_LEAVES.get());
                case "vampirism:bloody_spruce_log" -> missingMapping.remap(ModBlocks.CURSED_SPRUCE_LOG.get());
                case "vampirism:cursed_grass_block" -> missingMapping.remap(ModBlocks.CURSED_GRASS.get());
                case "cursed_bark" -> missingMapping.ignore();
                case "castle_block_dark_brick" -> missingMapping.remap(ModBlocks.DARK_STONE_BRICKS.get());
                case "castle_block_dark_brick_bloody" -> missingMapping.remap(ModBlocks.BLOODY_DARK_STONE_BRICKS.get());
                case "castle_block_dark_stone" -> missingMapping.remap(ModBlocks.DARK_STONE.get());
                case "castle_block_normal_brick" -> missingMapping.remap(Blocks.STONE_BRICKS);
                case "castle_slab_dark_brick" -> missingMapping.remap(ModBlocks.DARK_STONE_BRICK_SLAB.get());
                case "castle_slab_dark_stone" -> missingMapping.remap(ModBlocks.DARK_STONE_SLAB.get());
                case "castle_stairs_dark_brick" -> missingMapping.remap(ModBlocks.DARK_STONE_BRICK_STAIRS.get());
                case "castle_stairs_dark_stone" -> missingMapping.remap(ModBlocks.DARK_STONE_STAIRS.get());
                case "castle_block_dark_brick_cracked" -> missingMapping.remap(ModBlocks.CRACKED_DARK_STONE_BRICKS.get());
                case "castle_block_dark_brick_wall" -> missingMapping.remap(ModBlocks.DARK_STONE_BRICK_WALL.get());
            }
        });
    }

    public static @NotNull Set<Block> getAllBlocks() {
        return BLOCKS.getEntries().stream().map(RegistryObject::get).collect(Collectors.toUnmodifiableSet());
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
