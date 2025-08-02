package de.teamlapen.vampirism.core;

import com.google.common.collect.ImmutableMap;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.EnumStrength;
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
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.data.provider.ModLootTableProvider;
import de.teamlapen.vampirism.items.BloodContainerItem;
import de.teamlapen.vampirism.items.PureLevelBlockItem;
import de.teamlapen.vampirism.items.component.PureLevel;
import de.teamlapen.vampirism.util.VampirismVoxelShapes;
import de.teamlapen.vampirism.world.gen.ModTreeGrower;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Handles all block registrations and reference.
 */
@SuppressWarnings("unused")
public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(REFERENCE.MODID);

    public static class WoodTypes {

        public static final WoodType DARK_SPRUCE = WoodType.register(new WoodType(REFERENCE.MODID + ":dark_spruce", BlockSetType.SPRUCE));
        public static final WoodType CURSED_SPRUCE = WoodType.register(new WoodType(REFERENCE.MODID + ":cursed_spruce", BlockSetType.SPRUCE));
    }

    // Functional Blocks
    public static final DeferredBlock<AltarInspirationBlock> ALTAR_INSPIRATION = registerWithItem("altar_inspiration", AltarInspirationBlock::new, () -> basicProperties().mapColor(MapColor.METAL).strength(2f, 3f).noOcclusion());
    public static final DeferredBlock<AltarInfusionBlock> ALTAR_INFUSION = registerWithItem("altar_infusion", AltarInfusionBlock::new, () -> basicProperties().mapColor(MapColor.STONE).strength(5).noOcclusion());
    public static final DeferredBlock<AltarPillarBlock> ALTAR_PILLAR = registerWithItem("altar_pillar", AltarPillarBlock::new, () -> basicProperties().mapColor(MapColor.STONE).strength(0.9f).noOcclusion());
    public static final DeferredBlock<AltarTipBlock> ALTAR_TIP = registerWithItem("altar_tip", AltarTipBlock::new, () -> basicProperties().mapColor(MapColor.METAL).strength(1f).noOcclusion());

    public static final DeferredBlock<PedestalBlock> BLOOD_PEDESTAL = registerWithItem("blood_pedestal", PedestalBlock::new, () -> basicProperties().mapColor(MapColor.STONE).strength(3f).noOcclusion());
    public static final DeferredBlock<BloodContainerBlock> BLOOD_CONTAINER = registerWithItem("blood_container", BloodContainerBlock::new, () -> copyProperties(Blocks.DARK_OAK_PLANKS).strength(1.0f), BloodContainerItem::new);
    public static final DeferredBlock<BloodGrinderBlock> BLOOD_GRINDER = registerWithItem("blood_grinder", BloodGrinderBlock::new, () -> copyProperties(Blocks.DARK_OAK_PLANKS).strength(3.0f));
    public static final DeferredBlock<BloodSieveBlock> BLOOD_SIEVE = registerWithItem("blood_sieve", BloodSieveBlock::new, () -> copyProperties(Blocks.DARK_OAK_PLANKS).strength(3.0f));
    public static final DeferredBlock<BloodInfuserBlock> INFUSER = registerWithItem("blood_infuser", BloodInfuserBlock::new);

    public static final DeferredBlock<LiquidBlock> BLOOD = registerBlock("blood", props -> new LiquidBlock(ModFluids.BLOOD.get(), props), () -> copyProperties(Blocks.WATER).mapColor(MapColor.CRIMSON_HYPHAE));

    public static final DeferredBlock<FogDiffuserBlock> FOG_DIFFUSER = registerWithItem("fog_diffuser", FogDiffuserBlock::new, () -> basicProperties().noOcclusion().mapColor(MapColor.STONE).strength(40.0F, 1200.0F).sound(SoundType.STONE));
    public static final DeferredBlock<SunscreenBeaconBlock> SUNSCREEN_BEACON = registerWithItem("sunscreen_beacon", SunscreenBeaconBlock::new, () -> basicProperties().mapColor(MapColor.METAL).strength(-1, 3600000).noOcclusion(), itemProps -> itemProps.rarity(Rarity.RARE));

    public static final DeferredBlock<HunterTableBlock> HUNTER_TABLE = registerWithItem("hunter_table", HunterTableBlock::new, () -> basicProperties().mapColor(MapColor.WOOD).strength(0.5f).ignitedByLava().noOcclusion());
    public static final DeferredBlock<WeaponTableBlock> WEAPON_TABLE = registerWithItem("weapon_table", WeaponTableBlock::new, () -> basicProperties().mapColor(MapColor.METAL).strength(3).noOcclusion());
    public static final DeferredBlock<AlchemicalCauldronBlock> ALCHEMICAL_CAULDRON = registerWithItem("alchemical_cauldron", (props) -> new AlchemicalCauldronBlock(props.mapColor(MapColor.METAL).strength(4f).lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? 13 : 0).noOcclusion()));
    public static final DeferredBlock<PotionTableBlock> POTION_TABLE = registerWithItem("potion_table", props -> new PotionTableBlock(props.mapColor(MapColor.METAL).strength(1f).noOcclusion()));
    public static final DeferredBlock<AlchemyTableBlock> ALCHEMY_TABLE = registerWithItem("alchemy_table", AlchemyTableBlock::new, () -> basicProperties().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(0.5F).lightLevel(state -> 1).noOcclusion());
    public static final DeferredBlock<MedChairBlock> MED_CHAIR = registerWithItem("med_chair", MedChairBlock::new);
    public static final DeferredBlock<AltarCleansingBlock> ALTAR_CLEANSING = registerWithItem("altar_cleansing", AltarCleansingBlock::new, () -> basicProperties().mapColor(MapColor.WOOD).ignitedByLava().strength(0.5f).noOcclusion());

    public static final DeferredBlock<GarlicDiffuserBlock> GARLIC_DIFFUSER_NORMAL = registerWithItem("garlic_diffuser_normal", props -> new GarlicDiffuserBlock(props, EnumStrength.MEDIUM, VampirismConfig.BALANCE.hsGarlicDiffuserNormalDist), () -> basicProperties().mapColor(MapColor.STONE).strength(40.0F, 1200.0F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<GarlicDiffuserBlock> GARLIC_DIFFUSER_WEAK = registerWithItem("garlic_diffuser_weak", props -> new GarlicDiffuserBlock(props, EnumStrength.WEAK, VampirismConfig.BALANCE.hsGarlicDiffuserWeakDist), () -> copyProperties(GARLIC_DIFFUSER_NORMAL));
    public static final DeferredBlock<GarlicDiffuserBlock> GARLIC_DIFFUSER_IMPROVED = registerWithItem("garlic_diffuser_improved", props -> new GarlicDiffuserBlock(props, EnumStrength.MEDIUM, VampirismConfig.BALANCE.hsGarlicDiffuserEnhancedDist), () -> copyProperties(GARLIC_DIFFUSER_NORMAL));

    public static final DeferredBlock<VampireBeaconBlock> VAMPIRE_BEACON = registerWithItem("vampire_beacon", VampireBeaconBlock::new, () -> copyProperties(Blocks.BEACON).mapColor(MapColor.CRIMSON_HYPHAE), itemProps -> itemProps.rarity(Rarity.RARE));

    public static final DeferredBlock<TotemBaseBlock> TOTEM_BASE = registerWithItem("totem_base", TotemBaseBlock::new, () -> basicProperties().mapColor(MapColor.STONE).strength(40, 2000).sound(SoundType.STONE).noOcclusion().pushReaction(PushReaction.BLOCK));
    public static final DeferredBlock<TotemTopBlock> TOTEM_TOP = registerWithItem("totem_top", props -> new TotemTopBlock(props, false, null), () -> basicProperties().mapColor(MapColor.STONE).strength(12, 2000).sound(SoundType.STONE).pushReaction(PushReaction.BLOCK));
    public static final DeferredBlock<TotemTopBlock> TOTEM_TOP_VAMPIRISM_VAMPIRE = registerBlock("totem_top_vampirism_vampire", props -> new TotemTopBlock(props, false, ModFactions.VAMPIRE), () -> copyProperties(TOTEM_TOP));
    public static final DeferredBlock<TotemTopBlock> TOTEM_TOP_VAMPIRISM_HUNTER = registerBlock("totem_top_vampirism_hunter", props -> new TotemTopBlock(props, false, ModFactions.HUNTER), () -> copyProperties(TOTEM_TOP));
    public static final DeferredBlock<TotemTopBlock> TOTEM_TOP_CRAFTED = registerWithItem("totem_top_crafted", props -> new TotemTopBlock(props, true, null), () -> copyProperties(TOTEM_TOP));
    public static final DeferredBlock<TotemTopBlock> TOTEM_TOP_VAMPIRISM_VAMPIRE_CRAFTED = registerBlock("totem_top_vampirism_vampire_crafted", props -> new TotemTopBlock(props, true, ModFactions.VAMPIRE), () -> copyProperties(TOTEM_TOP));
    public static final DeferredBlock<TotemTopBlock> TOTEM_TOP_VAMPIRISM_HUNTER_CRAFTED = registerBlock("totem_top_vampirism_hunter_crafted", props -> new TotemTopBlock(props, true, ModFactions.HUNTER), () -> copyProperties(TOTEM_TOP));

    // Nature
    public static final DeferredBlock<LeavesBlock> DARK_SPRUCE_LEAVES = registerWithItem("dark_spruce_leaves", LeavesBlock::new, () -> copyProperties(Blocks.SPRUCE_LEAVES).mapColor(MapColor.COLOR_BLACK));

    public static final DeferredBlock<SaplingBlock> DARK_SPRUCE_SAPLING = registerWithItem("dark_spruce_sapling", props -> new DarkSpruceSaplingBlock(ModTreeGrower.DARK_SPRUCE, ModTreeGrower.CURSED_SPRUCE, props), () -> copyProperties(Blocks.SPRUCE_SAPLING).mapColor(MapColor.COLOR_BLACK));
    public static final DeferredBlock<SaplingBlock> CURSED_SPRUCE_SAPLING = registerWithItem("cursed_spruce_sapling", props -> new SaplingBlock(ModTreeGrower.CURSED_SPRUCE, props), () -> copyProperties(DARK_SPRUCE_SAPLING));

    public static final DeferredBlock<VampirismFlowerBlock> VAMPIRE_ORCHID = registerWithItem("vampire_orchid", props -> new VampirismFlowerBlock(props, MobEffects.BLINDNESS, 7, true), () -> copyProperties(Blocks.BLUE_ORCHID).mapColor(MapColor.COLOR_MAGENTA));

    public static final DeferredBlock<CursedRootsBlock> CURSED_ROOTS = registerWithItem("cursed_roots", CursedRootsBlock::new, () -> copyProperties(Blocks.CRIMSON_ROOTS).mapColor(MapColor.CRIMSON_HYPHAE));
    public static final DeferredBlock<CursedHangingRootsBlock> CURSED_HANGING_ROOTS = registerWithItem("cursed_hanging_roots", CursedHangingRootsBlock::new, () -> copyProperties(Blocks.HANGING_ROOTS).mapColor(MapColor.CRIMSON_HYPHAE));
    
    public static final DeferredBlock<DirectCursedBarkBlock> DIRECT_CURSED_BARK = registerWithItem("direct_cursed_bark", DirectCursedBarkBlock::new, () -> basicProperties().sound(SoundType.WOOD));
    public static final DeferredBlock<DiagonalCursedBarkBlock> DIAGONAL_CURSED_BARK = registerBlock("diagonal_cursed_bark", DiagonalCursedBarkBlock::new, () -> basicProperties().sound(SoundType.EMPTY));

    public static final DeferredBlock<FlowerPotBlock> POTTED_DARK_SPRUCE_SAPLING = registerPottedPlant("potted_dark_spruce_sapling", DARK_SPRUCE_SAPLING);
    public static final DeferredBlock<FlowerPotBlock> POTTED_CURSED_SPRUCE_SAPLING = registerPottedPlant("potted_cursed_spruce_sapling", CURSED_SPRUCE_SAPLING);
    public static final DeferredBlock<FlowerPotBlock> POTTED_VAMPIRE_ORCHID = registerPottedPlant("potted_vampire_orchid", VAMPIRE_ORCHID);
    public static final DeferredBlock<FlowerPotBlock> POTTED_CURSED_ROOTS = registerPottedPlant("potted_cursed_roots", CURSED_ROOTS);

    public static final DeferredBlock<GarlicBlock> GARLIC = registerWithItem("garlic", GarlicBlock::new, () -> copyProperties(Blocks.CARROTS));

    // Building Blocks
    public static final DeferredBlock<CursedGrass> CURSED_GRASS = registerWithItem("cursed_grass", CursedGrass::new, () -> copyProperties(Blocks.GRASS_BLOCK).mapColor(MapColor.COLOR_BLACK));
    public static final DeferredBlock<CursedEarthBlock> CURSED_EARTH = registerWithItem("cursed_earth", CursedEarthBlock::new, () -> copyProperties(Blocks.DIRT).mapColor(MapColor.TERRACOTTA_BROWN));
    public static final DeferredBlock<CursedEarthPathBlock> CURSED_EARTH_PATH = registerWithItem("cursed_earth_path", CursedEarthPathBlock::new, () -> copyProperties(Blocks.DIRT_PATH).mapColor(MapColor.COLOR_GRAY));

    public static final DeferredBlock<RotatedPillarBlock> DARK_SPRUCE_LOG = registerWithItem("dark_spruce_log", RotatedPillarBlock::new, logProperties(MapColor.COLOR_BLACK, MapColor.COLOR_BLACK));
    public static final DeferredBlock<RotatedPillarBlock> DARK_SPRUCE_WOOD = registerWithItem("dark_spruce_wood", RotatedPillarBlock::new, logProperties(MapColor.COLOR_BLACK, MapColor.COLOR_BLACK));
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_DARK_SPRUCE_LOG = registerWithItem("stripped_dark_spruce_log", RotatedPillarBlock::new, logProperties(MapColor.COLOR_BLACK, MapColor.COLOR_GRAY));
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_DARK_SPRUCE_WOOD = registerWithItem("stripped_dark_spruce_wood", RotatedPillarBlock::new, logProperties(MapColor.COLOR_BLACK, MapColor.COLOR_GRAY));

    public static final DeferredBlock<Block> DARK_SPRUCE_PLANKS = registerWithItem(ModRegistryItems.DARK_SPRUCE_PLANKS.getId().getPath(), Block::new, () -> copyProperties(Blocks.SPRUCE_PLANKS).mapColor(MapColor.COLOR_GRAY));
    public static final DeferredBlock<StairBlock> DARK_SPRUCE_STAIRS = registerWithItem("dark_spruce_stairs", props -> new StairBlock(DARK_SPRUCE_PLANKS.get().defaultBlockState(), props), () -> copyProperties(Blocks.SPRUCE_STAIRS, DARK_SPRUCE_PLANKS));
    public static final DeferredBlock<SlabBlock> DARK_SPRUCE_SLAB = registerWithItem("dark_spruce_slab", SlabBlock::new, () -> copyProperties(Blocks.SPRUCE_SLAB, DARK_SPRUCE_PLANKS));
    public static final DeferredBlock<FenceBlock> DARK_SPRUCE_FENCE = registerWithItem("dark_spruce_fence", FenceBlock::new, () -> copyProperties(Blocks.SPRUCE_FENCE, DARK_SPRUCE_PLANKS));
    public static final DeferredBlock<FenceGateBlock> DARK_SPRUCE_FENCE_GATE = registerWithItem("dark_spruce_fence_gate", props -> new FenceGateBlock(WoodTypes.DARK_SPRUCE, props), () -> copyProperties(Blocks.SPRUCE_FENCE_GATE, DARK_SPRUCE_PLANKS));
    public static final DeferredBlock<DoorBlock> DARK_SPRUCE_DOOR = registerWithItem("dark_spruce_door", props -> new DoorBlock(BlockSetType.SPRUCE, props), () -> copyProperties(Blocks.SPRUCE_DOOR, DARK_SPRUCE_PLANKS));
    public static final DeferredBlock<TrapDoorBlock> DARK_SPRUCE_TRAPDOOR = registerWithItem("dark_spruce_trapdoor", props -> new TrapDoorBlock(BlockSetType.SPRUCE, props), () -> copyProperties(Blocks.SPRUCE_TRAPDOOR, DARK_SPRUCE_PLANKS));
    public static final DeferredBlock<PressurePlateBlock> DARK_SPRUCE_PRESSURE_PLACE = registerWithItem("dark_spruce_pressure_plate", props -> new PressurePlateBlock(BlockSetType.SPRUCE, props), () -> copyProperties(Blocks.SPRUCE_PRESSURE_PLATE, DARK_SPRUCE_PLANKS));
    public static final DeferredBlock<ButtonBlock> DARK_SPRUCE_BUTTON = registerWithItem("dark_spruce_button", props -> new ButtonBlock(BlockSetType.SPRUCE, 30, props), () -> copyProperties(Blocks.SPRUCE_BUTTON, DARK_SPRUCE_PLANKS));
    public static final DeferredBlock<StandingSignBlock> DARK_SPRUCE_SIGN = registerBlock("dark_spruce_sign", props -> new StandingSignBlock(WoodTypes.DARK_SPRUCE, props), () -> copyProperties(Blocks.SPRUCE_SIGN, DARK_SPRUCE_PLANKS));
    public static final DeferredBlock<WallSignBlock> DARK_SPRUCE_WALL_SIGN = registerBlock("dark_spruce_wall_sign", props -> new WallSignBlock(WoodTypes.DARK_SPRUCE, props), () -> copyProperties(Blocks.SPRUCE_WALL_SIGN, DARK_SPRUCE_PLANKS).overrideLootTable(DARK_SPRUCE_SIGN.get().getLootTable()));
    public static final DeferredBlock<CeilingHangingSignBlock> DARK_SPRUCE_HANGING_SIGN = registerBlock("dark_spruce_hanging_sign", props -> new CeilingHangingSignBlock(WoodTypes.DARK_SPRUCE, props), () -> copyProperties(Blocks.SPRUCE_HANGING_SIGN, DARK_SPRUCE_PLANKS));
    public static final DeferredBlock<WallHangingSignBlock> DARK_SPRUCE_WALL_HANGING_SIGN = registerBlock("dark_spruce_wall_hanging_sign", props -> new WallHangingSignBlock(WoodTypes.DARK_SPRUCE, props), () -> copyProperties(Blocks.SPRUCE_WALL_HANGING_SIGN, DARK_SPRUCE_PLANKS).overrideLootTable(DARK_SPRUCE_HANGING_SIGN.get().getLootTable()));

    public static final DeferredBlock<CursedSpruceBlock> CURSED_SPRUCE_LOG_CURED = registerWithItem("cursed_spruce_log_cured", CursedSpruceBlock::new, logProperties(MapColor.COLOR_BLACK, MapColor.CRIMSON_HYPHAE));
    public static final DeferredBlock<CursedSpruceBlock> CURSED_SPRUCE_WOOD_CURED = registerWithItem("cursed_spruce_wood_cured", CursedSpruceBlock::new, logProperties(MapColor.COLOR_BLACK, MapColor.CRIMSON_HYPHAE));
    
    public static final DeferredBlock<CursedSpruceBlock> CURSED_SPRUCE_LOG = registerWithItem("cursed_spruce_log", props -> new CursedSpruceBlock(props, CURSED_SPRUCE_LOG_CURED), logProperties(MapColor.COLOR_BLACK, MapColor.CRIMSON_HYPHAE));
    public static final DeferredBlock<CursedSpruceBlock> CURSED_SPRUCE_WOOD = registerWithItem("cursed_spruce_wood", props -> new CursedSpruceBlock(props, CURSED_SPRUCE_WOOD_CURED), logProperties(MapColor.COLOR_BLACK, MapColor.CRIMSON_HYPHAE));
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_CURSED_SPRUCE_LOG = registerWithItem("stripped_cursed_spruce_log", RotatedPillarBlock::new, logProperties(MapColor.COLOR_BLACK, MapColor.CRIMSON_HYPHAE));
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_CURSED_SPRUCE_WOOD = registerWithItem("stripped_cursed_spruce_wood", RotatedPillarBlock::new, logProperties(MapColor.COLOR_BLACK, MapColor.CRIMSON_HYPHAE));

    public static final DeferredBlock<Block> CURSED_SPRUCE_PLANKS = registerWithItem(ModRegistryItems.CURSED_SPRUCE_PLANKS.getId().getPath(), Block::new, () -> copyProperties(Blocks.SPRUCE_PLANKS).mapColor(MapColor.CRIMSON_HYPHAE));
    public static final DeferredBlock<StairBlock> CURSED_SPRUCE_STAIRS = registerWithItem("cursed_spruce_stairs", props -> new StairBlock(CURSED_SPRUCE_PLANKS.get().defaultBlockState(), props), () -> copyProperties(Blocks.SPRUCE_STAIRS, CURSED_SPRUCE_PLANKS));
    public static final DeferredBlock<SlabBlock> CURSED_SPRUCE_SLAB = registerWithItem("cursed_spruce_slab", SlabBlock::new, () -> copyProperties(Blocks.SPRUCE_SLAB, CURSED_SPRUCE_PLANKS));
    public static final DeferredBlock<FenceBlock> CURSED_SPRUCE_FENCE = registerWithItem("cursed_spruce_fence", FenceBlock::new, () -> copyProperties(Blocks.SPRUCE_FENCE, CURSED_SPRUCE_PLANKS));
    public static final DeferredBlock<FenceGateBlock> CURSED_SPRUCE_FENCE_GATE = registerWithItem("cursed_spruce_fence_gate", props -> new FenceGateBlock(WoodTypes.CURSED_SPRUCE, props), () -> copyProperties(Blocks.SPRUCE_FENCE_GATE, CURSED_SPRUCE_PLANKS));
    public static final DeferredBlock<DoorBlock> CURSED_SPRUCE_DOOR = registerWithItem("cursed_spruce_door", props -> new DoorBlock(BlockSetType.SPRUCE, props), () -> copyProperties(Blocks.SPRUCE_DOOR, CURSED_SPRUCE_PLANKS));
    public static final DeferredBlock<TrapDoorBlock> CURSED_SPRUCE_TRAPDOOR = registerWithItem("cursed_spruce_trapdoor", props -> new TrapDoorBlock(BlockSetType.SPRUCE, props), () -> copyProperties(Blocks.SPRUCE_TRAPDOOR, CURSED_SPRUCE_PLANKS));
    public static final DeferredBlock<PressurePlateBlock> CURSED_SPRUCE_PRESSURE_PLACE = registerWithItem("cursed_spruce_pressure_plate", props -> new PressurePlateBlock(BlockSetType.SPRUCE, props), () -> copyProperties(Blocks.SPRUCE_PRESSURE_PLATE, CURSED_SPRUCE_PLANKS));
    public static final DeferredBlock<ButtonBlock> CURSED_SPRUCE_BUTTON = registerWithItem("cursed_spruce_button", props -> new ButtonBlock(BlockSetType.SPRUCE, 30, props), () -> copyProperties(Blocks.SPRUCE_BUTTON, CURSED_SPRUCE_PLANKS));
    public static final DeferredBlock<StandingSignBlock> CURSED_SPRUCE_SIGN = registerBlock("cursed_spruce_sign", props -> new StandingSignBlock(WoodTypes.CURSED_SPRUCE, props), () -> copyProperties(Blocks.SPRUCE_SIGN, CURSED_SPRUCE_PLANKS));
    public static final DeferredBlock<WallSignBlock> CURSED_SPRUCE_WALL_SIGN = registerBlock("cursed_spruce_wall_sign", props -> new WallSignBlock(WoodTypes.CURSED_SPRUCE, props), () -> copyProperties(Blocks.SPRUCE_WALL_SIGN, CURSED_SPRUCE_PLANKS).overrideLootTable(CURSED_SPRUCE_SIGN.get().getLootTable()));
    public static final DeferredBlock<CeilingHangingSignBlock> CURSED_SPRUCE_HANGING_SIGN = registerBlock("cursed_spruce_hanging_sign", props -> new CeilingHangingSignBlock(WoodTypes.CURSED_SPRUCE, props), () -> copyProperties(Blocks.SPRUCE_HANGING_SIGN, CURSED_SPRUCE_PLANKS));
    public static final DeferredBlock<WallHangingSignBlock> CURSED_SPRUCE_WALL_HANGING_SIGN = registerBlock("cursed_spruce_wall_hanging_sign", props -> new WallHangingSignBlock(WoodTypes.CURSED_SPRUCE, props), () -> copyProperties(Blocks.SPRUCE_WALL_HANGING_SIGN, CURSED_SPRUCE_PLANKS).overrideLootTable(CURSED_SPRUCE_HANGING_SIGN.get().getLootTable()));

    public static final DeferredBlock<DarkStoneBlock> DARK_STONE = registerWithItem("dark_stone", props -> new DarkStoneBlock(props.mapColor(MapColor.DEEPSLATE).requiresCorrectToolForDrops().strength(2f, 10f).sound(SoundType.STONE)));
    public static final DeferredBlock<StairBlock> DARK_STONE_STAIRS = registerWithItem("dark_stone_stairs", props -> new StairBlock(DARK_STONE.get().defaultBlockState(), props), () -> copyProperties(DARK_STONE));
    public static final DeferredBlock<SlabBlock> DARK_STONE_SLAB = registerWithItem("dark_stone_slab", SlabBlock::new, () -> copyProperties(DARK_STONE));
    public static final DeferredBlock<WallBlock> DARK_STONE_WALL = registerWithItem("dark_stone_wall", props -> new WallBlock(props.forceSolidOn()), () -> copyProperties(DARK_STONE));
    public static final DeferredBlock<DarkStoneBlock> INFESTED_DARK_STONE = registerWithItem("infested_dark_stone", DarkStoneBlock::new, () -> copyProperties(DARK_STONE));

    public static final DeferredBlock<DarkStoneBlock> DARK_STONE_BRICKS = registerWithItem("dark_stone_bricks", props -> new DarkStoneBlock(props.mapColor(MapColor.DEEPSLATE).requiresCorrectToolForDrops().strength(2f, 10f).sound(SoundType.STONE)));
    public static final DeferredBlock<StairBlock> DARK_STONE_BRICK_STAIRS = registerWithItem("dark_stone_brick_stairs", props -> new StairBlock(DARK_STONE_BRICKS.get().defaultBlockState(), props), () -> copyProperties(DARK_STONE_BRICKS));
    public static final DeferredBlock<SlabBlock> DARK_STONE_BRICK_SLAB = registerWithItem("dark_stone_brick_slab", SlabBlock::new, () -> copyProperties(DARK_STONE_BRICKS));
    public static final DeferredBlock<WallBlock> DARK_STONE_BRICK_WALL = registerWithItem("dark_stone_brick_wall", props -> new WallBlock(props.forceSolidOn()), () -> copyProperties(DARK_STONE_BRICKS));
    public static final DeferredBlock<Block> CRACKED_DARK_STONE_BRICKS = registerWithItem("cracked_dark_stone_bricks", DarkStoneBlock::new, () -> copyProperties(DARK_STONE_BRICKS));
    public static final DeferredBlock<DarkStoneBlock> CHISELED_DARK_STONE_BRICKS = registerWithItem("chiseled_dark_stone_bricks", DarkStoneBlock::new, () -> copyProperties(DARK_STONE_BRICKS));
    public static final DeferredBlock<DarkStoneBlock> BLOODY_DARK_STONE_BRICKS = registerWithItem("bloody_dark_stone_bricks", DarkStoneBlock::new, () -> copyProperties(DARK_STONE_BRICKS));

    public static final DeferredBlock<DarkStoneBlock> COBBLED_DARK_STONE = registerWithItem("cobbled_dark_stone", props -> new DarkStoneBlock(props.mapColor(MapColor.DEEPSLATE).strength(2.5f, 10f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<StairBlock> COBBLED_DARK_STONE_STAIRS = registerWithItem("cobbled_dark_stone_stairs", props -> new StairBlock(ModBlocks.COBBLED_DARK_STONE.get().defaultBlockState(), props), () -> copyProperties(COBBLED_DARK_STONE));
    public static final DeferredBlock<SlabBlock> COBBLED_DARK_STONE_SLAB = registerWithItem("cobbled_dark_stone_slab", SlabBlock::new, () -> copyProperties(COBBLED_DARK_STONE));
    public static final DeferredBlock<WallBlock> COBBLED_DARK_STONE_WALL = registerWithItem("cobbled_dark_stone_wall", props -> new WallBlock(props.forceSolidOn()), () -> copyProperties(COBBLED_DARK_STONE));

    public static final DeferredBlock<DarkStoneBlock> POLISHED_DARK_STONE = registerWithItem("polished_dark_stone", DarkStoneBlock::new, () -> copyProperties(COBBLED_DARK_STONE));
    public static final DeferredBlock<StairBlock> POLISHED_DARK_STONE_STAIRS = registerWithItem("polished_dark_stone_stairs", props -> new StairBlock(POLISHED_DARK_STONE.get().defaultBlockState(), props), () -> copyProperties(POLISHED_DARK_STONE));
    public static final DeferredBlock<SlabBlock> POLISHED_DARK_STONE_SLAB = registerWithItem("polished_dark_stone_slab", SlabBlock::new, () -> copyProperties(POLISHED_DARK_STONE));
    public static final DeferredBlock<WallBlock> POLISHED_DARK_STONE_WALL = registerWithItem("polished_dark_stone_wall", props -> new WallBlock(props.forceSolidOn()), () -> copyProperties(POLISHED_DARK_STONE));

    public static final DeferredBlock<DarkStoneBlock> DARK_STONE_TILES = registerWithItem("dark_stone_tiles", DarkStoneBlock::new, () -> copyProperties(COBBLED_DARK_STONE));
    public static final DeferredBlock<StairBlock> DARK_STONE_TILES_STAIRS = registerWithItem("dark_stone_tiles_stairs", props -> new StairBlock(ModBlocks.DARK_STONE_TILES.get().defaultBlockState(), props), () -> copyProperties(DARK_STONE_TILES));
    public static final DeferredBlock<SlabBlock> DARK_STONE_TILES_SLAB = registerWithItem("dark_stone_tiles_slab", SlabBlock::new, () -> copyProperties(DARK_STONE_TILES));
    public static final DeferredBlock<WallBlock> DARK_STONE_TILES_WALL = registerWithItem("dark_stone_tiles_wall", props -> new WallBlock(props.forceSolidOn()), () -> copyProperties(DARK_STONE_TILES));
    public static final DeferredBlock<DarkStoneBlock> CRACKED_DARK_STONE_TILES = registerWithItem("cracked_dark_stone_tiles", DarkStoneBlock::new, () -> copyProperties(DARK_STONE_TILES));

    public static final DeferredBlock<Block> PURPLE_STONE_BRICKS = registerWithItem("purple_stone_bricks", props -> new Block(props.mapColor(MapColor.COLOR_PURPLE).requiresCorrectToolForDrops().strength(2f, 10f).sound(SoundType.STONE)));
    public static final DeferredBlock<StairBlock> PURPLE_STONE_BRICK_STAIRS = registerWithItem("purple_stone_brick_stairs", props -> new StairBlock(PURPLE_STONE_BRICKS.get().defaultBlockState(), props), () -> copyProperties(PURPLE_STONE_BRICKS));
    public static final DeferredBlock<SlabBlock> PURPLE_STONE_BRICK_SLAB = registerWithItem("purple_stone_brick_slab", SlabBlock::new, () -> copyProperties(PURPLE_STONE_BRICKS));
    public static final DeferredBlock<WallBlock> PURPLE_STONE_BRICK_WALL = registerWithItem("purple_stone_brick_wall", props -> new WallBlock(props.forceSolidOn()), () -> copyProperties(PURPLE_STONE_BRICKS));

    public static final DeferredBlock<Block> PURPLE_STONE_TILES = registerWithItem("purple_stone_tiles", props -> new Block(props.mapColor(MapColor.COLOR_PURPLE).requiresCorrectToolForDrops().strength(2f, 10f).sound(SoundType.STONE)));
    public static final DeferredBlock<StairBlock> PURPLE_STONE_TILES_STAIRS = registerWithItem("purple_stone_tiles_stairs", props -> new StairBlock(PURPLE_STONE_TILES.get().defaultBlockState(), props), () -> copyProperties(PURPLE_STONE_TILES));
    public static final DeferredBlock<SlabBlock> PURPLE_STONE_TILES_SLAB = registerWithItem("purple_stone_tiles_slab", SlabBlock::new, () -> copyProperties(PURPLE_STONE_TILES));
    public static final DeferredBlock<WallBlock> PURPLE_STONE_TILES_WALL = registerWithItem("purple_stone_tiles_wall", props -> new WallBlock(props.forceSolidOn()), () -> copyProperties(PURPLE_STONE_TILES));

    public static final DeferredBlock<Block> BLOOD_INFUSED_IRON_BLOCK = registerWithItem("blood_infused_iron_block", PureBloodBlock::new, () -> copyProperties(Blocks.IRON_BLOCK).mapColor(MapColor.CRIMSON_HYPHAE).strength(6.0F, 7.0F), (block, itemProps) -> new PureLevelBlockItem(block, itemProps.component(ModDataComponents.PURE_LEVEL, PureLevel.LOW)));
    public static final DeferredBlock<Block> BLOOD_INFUSED_ENHANCED_IRON_BLOCK = registerWithItem("blood_infused_enhanced_iron_block", PureBloodBlock::new, () -> copyProperties(Blocks.IRON_BLOCK).mapColor(MapColor.CRIMSON_HYPHAE).strength(6.5F, 8.0F), (block, itemProps) -> new PureLevelBlockItem(block, itemProps.component(ModDataComponents.PURE_LEVEL, new PureLevel(4))));

    // Decorative Blocks
    public static final DeferredBlock<FirePlaceBlock> FIRE_PLACE = registerWithItem("fire_place", FirePlaceBlock::new, () -> basicProperties().mapColor(MapColor.WOOD).lightLevel(state -> 15).strength(1).ignitedByLava().noOcclusion());
    public static final DeferredBlock<AlchemicalFireBlock> ALCHEMICAL_FIRE = registerBlock("alchemical_fire", AlchemicalFireBlock::new, () -> copyProperties(Blocks.FIRE).mapColor(MapColor.COLOR_PURPLE).noLootTable());

    public static final DeferredBlock<StandingCandleStickBlock> CANDLE_STICK = registerBlock("candle_stick", props -> new StandingCandleStickBlock(null, () -> null, props), () -> basicProperties().mapColor(MapColor.METAL).noOcclusion().strength(0.5f).sound(SoundType.METAL).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<WallCandleStickBlock> WALL_CANDLE_STICK = registerBlock("wall_candle_stick", props -> new WallCandleStickBlock(null, () -> null, props), () -> copyProperties(CANDLE_STICK).strength(1.5f).overrideLootTable(CANDLE_STICK.get().getLootTable()));

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

    public static final DeferredBlock<StandingCandelabraBlock> CANDELABRA = registerBlock("candelabra", props -> new StandingCandelabraBlock(null, () -> null, props), () -> basicProperties().mapColor(MapColor.METAL).noOcclusion().strength(1.0f).sound(SoundType.METAL).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<WallCandelabraBlock> WALL_CANDELABRA = registerBlock("wall_candelabra", props -> new WallCandelabraBlock(null, () -> null, props), () -> copyProperties(CANDELABRA).strength(3.5f).overrideLootTable(CANDELABRA.get().getLootTable()));

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

    public static final DeferredBlock<ChandelierBlock> CHANDELIER = registerWithItem("chandelier", props -> new ChandelierBlock(null, () -> null, props), () -> basicProperties().mapColor(MapColor.METAL).noOcclusion().strength(4.5f, 5.5f).sound(SoundType.METAL).pushReaction(PushReaction.DESTROY), (block, itemProps) -> new BlockItem(block, itemProps.useBlockDescriptionPrefix()));

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

    public static final DeferredBlock<VampireSoulLanternBlock> VAMPIRE_SOUL_LANTERN = registerWithItem("vampire_soul_lantern", VampireSoulLanternBlock::new, () -> copyProperties(Blocks.LANTERN).mapColor(MapColor.GOLD).lightLevel(state -> 12));

    public static final DeferredBlock<VampirismSplitBlock> CROSS = registerWithItem("cross", props -> new VampirismSplitBlock(props, VampirismVoxelShapes.CROSS_BOTTOM, VampirismVoxelShapes.CROSS_TOP, true), () -> basicProperties().pushReaction(PushReaction.DESTROY).mapColor(MapColor.WOOD).ignitedByLava().strength(2, 3));
    public static final DeferredBlock<VampirismHorizontalBlock> TOMBSTONE1 = registerWithItem("tombstone1", props -> new VampirismHorizontalBlock(props, VampirismVoxelShapes.TOMB_1), () -> basicProperties().mapColor(MapColor.STONE).strength(2, 6));
    public static final DeferredBlock<VampirismHorizontalBlock> TOMBSTONE2 = registerWithItem("tombstone2", props -> new VampirismHorizontalBlock(props, VampirismVoxelShapes.TOMB_2), () -> basicProperties().mapColor(MapColor.STONE).strength(2, 6));
    public static final DeferredBlock<VampirismSplitBlock> TOMBSTONE3 = registerWithItem("tombstone3", props -> new VampirismSplitBlock(props, VampirismVoxelShapes.TOMB_3_BASE, VampirismVoxelShapes.TOMB_3_TOP, true), () -> basicProperties().mapColor(MapColor.STONE).pushReaction(PushReaction.DESTROY).strength(2, 6));
    public static final DeferredBlock<VampirismHorizontalBlock> GRAVE_CAGE = registerWithItem("grave_cage", props -> new VampirismHorizontalBlock(props, VampirismVoxelShapes.GRAVE_CAGE), () -> basicProperties().mapColor(MapColor.METAL).strength(6, 8).requiresCorrectToolForDrops().sound(SoundType.METAL));

    public static final DeferredBlock<VampirismHorizontalBlock> VAMPIRE_RACK = registerWithItem("vampire_rack", props -> new VampirismHorizontalBlock(props.ignitedByLava().strength(2, 3), VampirismVoxelShapes.VAMPIRE_RACK));
    public static final DeferredBlock<ThroneBlock> THRONE = registerWithItem("throne", ThroneBlock::new, () -> basicProperties().mapColor(MapColor.WOOD).ignitedByLava().pushReaction(PushReaction.DESTROY).strength(2, 3));
    public static final DeferredBlock<BatCageBlock> BAT_CAGE = registerWithItem("bat_cage", BatCageBlock::new, () -> basicProperties().strength(5.0F, 6.0F).sound(SoundType.METAL).noOcclusion());
    public static final DeferredBlock<MotherTrophyBlock> MOTHER_TROPHY = registerWithItem("mother_trophy", MotherTrophyBlock::new, () -> basicProperties().mapColor(MapColor.COLOR_GRAY).strength(3, 9).lightLevel(s -> 1).noOcclusion(), itemProps -> itemProps.rarity(Rarity.EPIC).stacksTo(1));

    public static final DeferredBlock<TentBlock> TENT = registerBlock("tent", TentBlock::new, () -> basicProperties().mapColor(MapColor.WOOL).ignitedByLava().strength(0.6f).sound(SoundType.WOOL).noOcclusion());
    public static final DeferredBlock<TentMainBlock> TENT_MAIN = registerBlock("tent_main", TentMainBlock::new, () -> copyProperties(TENT));

    public static final DeferredBlock<CoffinBlock> COFFIN_WHITE = registerWithItem("coffin_white", props -> new CoffinBlock(props, DyeColor.WHITE), coffinProperties());
    public static final DeferredBlock<CoffinBlock> COFFIN_ORANGE = registerWithItem("coffin_orange", props -> new CoffinBlock(props, DyeColor.ORANGE), coffinProperties());
    public static final DeferredBlock<CoffinBlock> COFFIN_MAGENTA = registerWithItem("coffin_magenta", props -> new CoffinBlock(props, DyeColor.MAGENTA), coffinProperties());
    public static final DeferredBlock<CoffinBlock> COFFIN_LIGHT_BLUE = registerWithItem("coffin_light_blue", props -> new CoffinBlock(props, DyeColor.LIGHT_BLUE), coffinProperties());
    public static final DeferredBlock<CoffinBlock> COFFIN_YELLOW = registerWithItem("coffin_yellow", props -> new CoffinBlock(props, DyeColor.YELLOW), coffinProperties());
    public static final DeferredBlock<CoffinBlock> COFFIN_LIME = registerWithItem("coffin_lime", props -> new CoffinBlock(props, DyeColor.LIME), coffinProperties());
    public static final DeferredBlock<CoffinBlock> COFFIN_PINK = registerWithItem("coffin_pink", props -> new CoffinBlock(props, DyeColor.PINK), coffinProperties());
    public static final DeferredBlock<CoffinBlock> COFFIN_GRAY = registerWithItem("coffin_gray", props -> new CoffinBlock(props, DyeColor.GRAY), coffinProperties());
    public static final DeferredBlock<CoffinBlock> COFFIN_LIGHT_GRAY = registerWithItem("coffin_light_gray", props -> new CoffinBlock(props, DyeColor.LIGHT_GRAY), coffinProperties());
    public static final DeferredBlock<CoffinBlock> COFFIN_CYAN = registerWithItem("coffin_cyan", props -> new CoffinBlock(props, DyeColor.CYAN), coffinProperties());
    public static final DeferredBlock<CoffinBlock> COFFIN_PURPLE = registerWithItem("coffin_purple", props -> new CoffinBlock(props, DyeColor.PURPLE), coffinProperties());
    public static final DeferredBlock<CoffinBlock> COFFIN_BLUE = registerWithItem("coffin_blue", props -> new CoffinBlock(props, DyeColor.BLUE), coffinProperties());
    public static final DeferredBlock<CoffinBlock> COFFIN_BROWN = registerWithItem("coffin_brown", props -> new CoffinBlock(props, DyeColor.BROWN), coffinProperties());
    public static final DeferredBlock<CoffinBlock> COFFIN_GREEN = registerWithItem("coffin_green", props -> new CoffinBlock(props, DyeColor.GREEN), coffinProperties());
    public static final DeferredBlock<CoffinBlock> COFFIN_RED = registerWithItem("coffin_red", props -> new CoffinBlock(props, DyeColor.RED), coffinProperties());
    public static final DeferredBlock<CoffinBlock> COFFIN_BLACK = registerWithItem("coffin_black", props -> new CoffinBlock(props, DyeColor.BLACK), coffinProperties());

    public static final DeferredBlock<MotherBlock> MOTHER = registerBlock("mother", MotherBlock::new, () -> basicProperties().mapColor(MapColor.TERRACOTTA_BROWN).strength(5, 3600000.0F).sound(SoundType.CHAIN));
    public static final DeferredBlock<RemainsBlock> REMAINS = registerBlock("remains", props -> new RemainsBlock(props, false, false), () -> basicProperties().mapColor(MapColor.TERRACOTTA_BROWN).strength(-1, 3600000.0F).sound(SoundType.ROOTED_DIRT).randomTicks().noLootTable());
    public static final DeferredBlock<RemainsBlock> VULNERABLE_REMAINS = registerBlock("vulnerable_remains", props -> new RemainsBlock(props, true, true), () -> basicProperties().mapColor(MapColor.TERRACOTTA_BROWN).strength(-1, 3600000.0F).sound(SoundType.ROOTED_DIRT).randomTicks().noLootTable());
    public static final DeferredBlock<ActiveVulnerableRemainsBlock> ACTIVE_VULNERABLE_REMAINS = registerBlock("active_vulnerable_remains", ActiveVulnerableRemainsBlock::new, () -> basicProperties().mapColor(MapColor.TERRACOTTA_BROWN).strength(-1, 3600000.0F).randomTicks().sound(SoundType.ROOTED_DIRT).noLootTable());
    public static final DeferredBlock<RemainsBlock> INCAPACITATED_VULNERABLE_REMAINS = registerBlock("incapacitated_vulnerable_remains", props -> new RemainsBlock(props, false, true), () -> basicProperties().mapColor(MapColor.TERRACOTTA_BROWN).strength(-1.0F, 3600000.0F).sound(SoundType.ROOTED_DIRT).randomTicks().noLootTable());


    /**
     * TUTORIAL:
     * - Register blocks here.
     * - To register itemblock, use {@link ModBlocks#registerWithItem}
     * - Register blockstate in {@link de.teamlapen.vampirism.data.provider.BlockStateProvider#registerStatesAndModels()} (pass existent model if desired)
     * - Maybe set render layer in the json model or blockstate generator.
     * - Register itemrender in {@link de.teamlapen.vampirism.data.provider.ItemModelGenerator#registerModels()}
     * - Register loot table in {@link ModLootTableProvider.ModBlockLootTables#addTables()}
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

    private static <T extends Block> DeferredBlock<T> registerWithItem(String name, Function<BlockBehaviour.Properties, T> supplier) {
        DeferredBlock<T> block = BLOCKS.registerBlock(name, supplier);
        createItem(name, block, BlockItem::new, props -> props);
        return block;
    }

    private static <T extends Block, R extends Item> DeferredBlock<T> registerWithItem(String name, Function<BlockBehaviour.Properties, T> supplier, Supplier<BlockBehaviour.Properties> blockProperties, @NotNull BiFunction<T, Item.Properties, R> itemCreator) {
        DeferredBlock<T> block = BLOCKS.registerBlock(name, supplier);
        createItem(name, block, itemCreator, props -> props);
        return block;
    }

    private static <T extends Block, R extends Item> void createItem(String name, Supplier<T> block, BiFunction<T, Item.Properties, R> itemCreator, Function<Item.@NotNull Properties, Item.Properties> properties) {
        ModItems.ITEMS.registerItem(name, props -> itemCreator.apply(block.get(), properties.apply(props).overrideDescription(block.get().getDescriptionId())));
    }

    private static <T extends Block> DeferredBlock<T> registerWithItem(String name, Function<BlockBehaviour.Properties,T> supplier, Supplier<BlockBehaviour.Properties> blockProperties) {
        return registerWithItem(name, supplier, blockProperties, props -> props);
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties,T> supplier, Supplier<BlockBehaviour.Properties> blockProperties) {
        return BLOCKS.registerBlock(name, props -> supplier.apply(blockProperties.get().setId(ResourceKey.create(Registries.BLOCK, VResourceLocation.mod(name)))));
    }

    private static DeferredBlock<FlowerPotBlock> registerPottedPlant(String name, DeferredBlock<?> plantBlock) {
        return registerBlock(name, props -> {
            FlowerPotBlock block = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, plantBlock, props);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(plantBlock.getId(), () -> block);
            return block;
        }, () -> Block.Properties.ofFullCopy(Blocks.FLOWER_POT));
    }

    private static DeferredBlock<StandingCandleStickBlock> registerCandleStick(String suffix, Item candle) {
        return registerBlock("candle_stick_" + suffix, props -> {
            StandingCandleStickBlock block = new StandingCandleStickBlock(CANDLE_STICK, () -> candle, props);
            CANDLE_STICK.get().addCandle(BuiltInRegistries.ITEM.getKey(candle), () -> block);
            return block;
        }, () -> copyProperties(CANDLE_STICK));
    }

    private static DeferredBlock<WallCandleStickBlock> registerWallCandleStick(String suffix, Item candle, DeferredBlock<StandingCandleStickBlock> standingBlock) {
        return registerBlock("wall_candle_stick_" + suffix, props -> {
            WallCandleStickBlock block = new WallCandleStickBlock(WALL_CANDLE_STICK, () -> candle, props.overrideLootTable(standingBlock.get().getLootTable()));
            WALL_CANDLE_STICK.get().addCandle(BuiltInRegistries.ITEM.getKey(candle), () -> block);
            return block;
        }, () -> copyProperties(standingBlock));
    }

    private static DeferredBlock<StandingCandelabraBlock> registerCandelabra(String suffix, Item candle) {
        return registerBlock("candelabra_" + suffix, props -> {
            StandingCandelabraBlock block = new StandingCandelabraBlock(CANDELABRA, () -> candle, props);
            CANDELABRA.get().addCandle(BuiltInRegistries.ITEM.getKey(candle), () -> block);
            return block;
        }, () -> copyProperties(CANDELABRA));
    }

    private static DeferredBlock<WallCandelabraBlock> registerWallCandelabra(String suffix, Item candle, DeferredBlock<StandingCandelabraBlock> standingBlock) {
        return registerBlock("wall_candelabra_" + suffix, props -> {
            WallCandelabraBlock block = new WallCandelabraBlock(WALL_CANDELABRA, () -> candle, props.strength(3.5f).overrideLootTable(standingBlock.get().getLootTable()));
            WALL_CANDELABRA.get().addCandle(BuiltInRegistries.ITEM.getKey(candle), () -> block);
            return block;
        }, () -> copyProperties(standingBlock));
    }

    private static DeferredBlock<ChandelierBlock> registerChandelier(String suffix, Item candle) {
        return registerWithItem("chandelier_" + suffix, props -> {
            ChandelierBlock block = new ChandelierBlock(CHANDELIER, () -> candle, props);
            CHANDELIER.get().addCandle(BuiltInRegistries.ITEM.getKey(candle), () -> block);
            return block;
        }, () -> copyProperties(CHANDELIER), (block, itemProps) -> new BlockItem(block, itemProps.useBlockDescriptionPrefix()));
    }

    private static BlockBehaviour.Properties basicProperties() {
        return BlockBehaviour.Properties.of();
    }

    private static BlockBehaviour.Properties copyProperties(BlockBehaviour block) {
        return BlockBehaviour.Properties.ofFullCopy(block);
    }

    private static BlockBehaviour.Properties copyProperties(DeferredBlock<?> block) {
        return copyProperties(block.get());
    }

    private static BlockBehaviour.Properties copyProperties(BlockBehaviour block, DeferredBlock<?> copyMapColorBlock) {
        return copyProperties(block).mapColor(copyMapColorBlock.get().defaultMapColor());
    }

    private static Supplier<BlockBehaviour.Properties> logProperties(MapColor sideColor, MapColor topColor) {
        return () -> basicProperties().mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? sideColor : topColor).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava();
    }

    private static Supplier<BlockBehaviour.Properties> coffinProperties() {
        return () -> basicProperties().mapColor(MapColor.WOOD).strength(0.2f).noOcclusion().pushReaction(PushReaction.DESTROY).ignitedByLava();
    }

    public static @NotNull Set<Block> getAllBlocks() {
        return BLOCKS.getEntries().stream().map(DeferredHolder::get).collect(Collectors.toUnmodifiableSet());
    }

    @SuppressWarnings("unchecked")
    public static @NotNull Stream<Holder<Block>> listElements() {
        return ((Collection<Holder<Block>>)(Object) BLOCKS.getEntries()).stream();
    }

    static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }

    public static void registerStrippables() {
        AxeItem.STRIPPABLES = new ImmutableMap.Builder<Block, Block>().putAll(AxeItem.STRIPPABLES)
                .put(DARK_SPRUCE_LOG.get(), STRIPPED_DARK_SPRUCE_LOG.get())
                .put(DARK_SPRUCE_WOOD.get(), STRIPPED_DARK_SPRUCE_WOOD.get())
                .put(CURSED_SPRUCE_LOG.get(), STRIPPED_CURSED_SPRUCE_LOG.get())
                .put(CURSED_SPRUCE_WOOD.get(), STRIPPED_CURSED_SPRUCE_WOOD.get())
                .put(CURSED_SPRUCE_LOG_CURED.get(), STRIPPED_CURSED_SPRUCE_LOG.get())
                .put(CURSED_SPRUCE_WOOD_CURED.get(), STRIPPED_CURSED_SPRUCE_WOOD.get())
                .build();
    }

    public static void registerFlammables() {
        Stream.of(
                VAMPIRE_ORCHID,
                CURSED_ROOTS,
                CURSED_HANGING_ROOTS
        ).forEach(block -> setFlammable(block, 60, 120));

        Stream.of(
                DARK_SPRUCE_PLANKS,
                DARK_SPRUCE_STAIRS,
                DARK_SPRUCE_SLAB,
                DARK_SPRUCE_FENCE,
                DARK_SPRUCE_FENCE_GATE,
                CURSED_SPRUCE_PLANKS,
                CURSED_SPRUCE_STAIRS,
                CURSED_SPRUCE_SLAB,
                CURSED_SPRUCE_FENCE,
                CURSED_SPRUCE_FENCE_GATE
        ).forEach(block -> setFlammable(block, 15, 40));

        Stream.of(
                DARK_SPRUCE_LOG,
                DARK_SPRUCE_WOOD,
                STRIPPED_DARK_SPRUCE_LOG,
                STRIPPED_DARK_SPRUCE_WOOD,
                CURSED_SPRUCE_LOG_CURED,
                CURSED_SPRUCE_WOOD_CURED,
                CURSED_SPRUCE_LOG,
                CURSED_SPRUCE_WOOD,
                STRIPPED_CURSED_SPRUCE_LOG,
                STRIPPED_CURSED_SPRUCE_WOOD
        ).forEach(block -> setFlammable(block, 15, 10));

        setFlammable(CROSS, 20, 30);

        setFlammable(DARK_SPRUCE_LEAVES, 30, 60);
    }

    private static void setFlammable(DeferredBlock<?> block, int encouragement, int flammability) {
        ((FireBlock) Blocks.FIRE).setFlammable(block.get(), encouragement, flammability);
    }
}
