package de.teamlapen.vampirism.world.gen;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModFeatures;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.world.gen.feature.treedecorators.TrunkCursedVineDecorator;
import de.teamlapen.vampirism.world.gen.modifier.ExtendedAddSpawnsBiomeModifier;
import de.teamlapen.vampirism.world.gen.structure.huntercamp.HunterCampPieces;
import de.teamlapen.vampirism.world.gen.structure.mother.MotherPiece;
import de.teamlapen.vampirism.world.gen.structure.templatesystem.BiomeTopBlockProcessor;
import de.teamlapen.vampirism.world.gen.structure.templatesystem.RandomStructureProcessor;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.ThreeLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.SpruceFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.OptionalInt;

import static net.minecraft.data.worldgen.placement.OrePlacements.commonOrePlacement;
import static net.minecraft.data.worldgen.placement.OrePlacements.rareOrePlacement;

public class VampirismFeatures {

    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECES = DeferredRegister.create(Registries.STRUCTURE_PIECE, REFERENCE.MODID);
    public static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSOR_TYPES = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, REFERENCE.MODID);


    public static final RegistryObject<StructurePieceType> HUNTER_CAMP_FIREPLACE = STRUCTURE_PIECES.register("hunter_camp_fireplace", () -> (StructurePieceType.ContextlessType) HunterCampPieces.Fireplace::new);
    public static final RegistryObject<StructurePieceType> HUNTER_CAMP_TENT = STRUCTURE_PIECES.register("hunter_camp_tent", () -> (StructurePieceType.ContextlessType) HunterCampPieces.Tent::new);
    public static final RegistryObject<StructurePieceType> HUNTER_CAMP_SPECIAL = STRUCTURE_PIECES.register("hunter_camp_craftingtable", () -> (StructurePieceType.ContextlessType) HunterCampPieces.SpecialBlock::new);
    public static final RegistryObject<StructurePieceType> MOTHER = STRUCTURE_PIECES.register("mother", () -> (StructurePieceType.ContextlessType) MotherPiece::new);

    public static final RegistryObject<StructureProcessorType<RandomStructureProcessor>> RANDOM_SELECTOR = STRUCTURE_PROCESSOR_TYPES.register("random_selector", () -> () -> RandomStructureProcessor.CODEC);
    public static final RegistryObject<StructureProcessorType<BiomeTopBlockProcessor>> BIOME_BASED = STRUCTURE_PROCESSOR_TYPES.register("biome_based", () -> () -> BiomeTopBlockProcessor.CODEC);

    public static final ResourceKey<ConfiguredFeature<?, ?>> VAMPIRE_FLOWER = createConfiguredKey("vampire_flower");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CURSED_ROOT = createConfiguredKey("cursed_root");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DARK_SPRUCE_TREE = createConfiguredKey("dark_spruce_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CURSED_SPRUCE_TREE = createConfiguredKey("cursed_tree_red");
    public static final ResourceKey<ConfiguredFeature<?, ?>> VAMPIRE_DUNGEON = createConfiguredKey("vampire_dungeon");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WATER_LAKE = createConfiguredKey("mod_lake");
    public static final ResourceKey<ConfiguredFeature<?, ?>> VAMPIRE_TREES = createConfiguredKey("vampire_trees_placed");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_DARK_STONE = createConfiguredKey("ore_dark_stone");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_CURSED_DIRT = createConfiguredKey("ore_cursed_dirt");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CURSED_ROOTS = createConfiguredKey("cursed_roots");

    public static final ResourceKey<PlacedFeature> VAMPIRE_FLOWER_PLACED = createPlacedKey("vampire_flower");
    public static final ResourceKey<PlacedFeature> CURSED_ROOT_PLACED = createPlacedKey("cursed_root");
    public static final ResourceKey<PlacedFeature> DARK_SPRUCE_TREE_PLACED = createPlacedKey("dark_spruce_tree");
    public static final ResourceKey<PlacedFeature> CURSED_SPRUCE_TREE_PLACED = createPlacedKey("cursed_spruce_tree_placed");
    public static final ResourceKey<PlacedFeature> VAMPIRE_DUNGEON_PLACED = createPlacedKey("vampire_dungeon");
    public static final ResourceKey<PlacedFeature> WATER_LAKE_PLACED = createPlacedKey("mod_lake_placed");
    public static final ResourceKey<PlacedFeature> VAMPIRE_TREES_PLACED = createPlacedKey("vampire_trees");
    public static final ResourceKey<PlacedFeature> FOREST_GRASS_PLACED = createPlacedKey("forest_grass");
    public static final ResourceKey<PlacedFeature> ORE_DARK_STONE_UPPER_PLACED = createPlacedKey("ore_dark_stone_upper");
    public static final ResourceKey<PlacedFeature> ORE_DARK_STONE_LOWER_PLACED = createPlacedKey("ore_dark_stone_lower");
    public static final ResourceKey<PlacedFeature> ORE_CURSED_DIRT_PLACED = createPlacedKey("ore_cursed_dirt");
    public static final ResourceKey<PlacedFeature> CURSED_ROOTS_PLACED = createPlacedKey("cursed_roots");

    public static final ResourceKey<BiomeModifier> VAMPIRE_SPAWN = createModifierKey("spawn/vampire_spawns");
    public static final ResourceKey<BiomeModifier> HUNTER_SPAWN = createModifierKey("spawn/hunter_spawns");
    public static final ResourceKey<BiomeModifier> ADVANCED_VAMPIRE_SPAWN = createModifierKey("spawn/advanced_vampire_spawns");
    public static final ResourceKey<BiomeModifier> ADVANCED_HUNTER_SPAWN = createModifierKey("spawn/advanced_hunter_spawns");
    public static final ResourceKey<BiomeModifier> VAMPIRE_DUNGEON_MODIFIER = createModifierKey("feature/vampire_dungeon");
    public static final ResourceKey<BiomeModifier> VAMPIRE_ROOTS = createModifierKey("feature/vampire_roots");
    public static final ResourceKey<StructureSet> HUNTER_CAMP = createStructureSetKey("hunter_camp");
    public static final ResourceKey<StructureSet> MOTHER_SET = createStructureSetKey("mother");


    public static void register(IEventBus ctx) {
        STRUCTURE_PIECES.register(ctx);
        STRUCTURE_PROCESSOR_TYPES.register(ctx);
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> createConfiguredKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(REFERENCE.MODID, name));
    }

    private static ResourceKey<PlacedFeature> createPlacedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(REFERENCE.MODID, name));
    }

    private static ResourceKey<BiomeModifier> createModifierKey(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(REFERENCE.MODID, name));
    }

    private static ResourceKey<StructureSet> createStructureSetKey(String name) {
        return ResourceKey.create(Registries.STRUCTURE_SET, new ResourceLocation(REFERENCE.MODID, name));
    }

    public static void createConfiguredFeatures(BootstapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        context.register(VAMPIRE_FLOWER, new ConfiguredFeature<>(Feature.FLOWER, FeatureUtils.simpleRandomPatchConfiguration(32, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.VAMPIRE_ORCHID.get()))))));
        context.register(CURSED_ROOT, new ConfiguredFeature<>(Feature.RANDOM_PATCH, FeatureUtils.simpleRandomPatchConfiguration(16, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.CURSED_ROOTS.get()))))));
        context.register(DARK_SPRUCE_TREE, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(ModBlocks.DARK_SPRUCE_LOG.get()), new StraightTrunkPlacer(11, 2, 2), BlockStateProvider.simple(ModBlocks.DARK_SPRUCE_LEAVES.get().defaultBlockState()), new SpruceFoliagePlacer(UniformInt.of(2, 3), UniformInt.of(0, 2), UniformInt.of(3, 7)), new ThreeLayersFeatureSize(5, 8, 0, 3, 3, OptionalInt.of(5))).ignoreVines().build()));
        context.register(CURSED_SPRUCE_TREE, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(ModBlocks.CURSED_SPRUCE_LOG.get()), new StraightTrunkPlacer(11, 2, 2), BlockStateProvider.simple(ModBlocks.DARK_SPRUCE_LEAVES.get().defaultBlockState()), new SpruceFoliagePlacer(UniformInt.of(2, 3), UniformInt.of(0, 2), UniformInt.of(3, 7)), new ThreeLayersFeatureSize(5, 8, 0, 3, 3, OptionalInt.of(5))).decorators(ImmutableList.of(TrunkCursedVineDecorator.INSTANCE)).ignoreVines().build()));
        context.register(VAMPIRE_DUNGEON, new ConfiguredFeature<>(ModFeatures.VAMPIRE_DUNGEON.get(), FeatureConfiguration.NONE));
        context.register(WATER_LAKE, new ConfiguredFeature<>(Feature.LAKE, new LakeFeature.Configuration(BlockStateProvider.simple(Blocks.WATER), BlockStateProvider.simple(ModBlocks.CASTLE_BLOCK_DARK_STONE.get()))));
        context.register(VAMPIRE_TREES, new ConfiguredFeature<>(Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of(new WeightedPlacedFeature(placedFeatures.getOrThrow(CURSED_SPRUCE_TREE_PLACED), 0.3f)), placedFeatures.getOrThrow(DARK_SPRUCE_TREE_PLACED))));
        context.register(ORE_DARK_STONE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(new TagMatchTest(BlockTags.BASE_STONE_OVERWORLD), ModBlocks.CASTLE_BLOCK_DARK_STONE.get().defaultBlockState(), 64)));
        context.register(ORE_CURSED_DIRT, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(new TagMatchTest(BlockTags.BASE_STONE_OVERWORLD), ModBlocks.CURSED_EARTH.get().defaultBlockState(), 33)));
        context.register(CURSED_ROOTS, new ConfiguredFeature<>(Feature.ROOT_SYSTEM, new RootSystemConfiguration(placedFeatures.getOrThrow(CURSED_SPRUCE_TREE_PLACED), 3,10, BlockTags.AZALEA_ROOT_REPLACEABLE, BlockStateProvider.simple(ModBlocks.CURSED_ROOTED_DIRT.get()), 100, 20, 3, 2, BlockStateProvider.simple(ModBlocks.CURSED_HANGING_ROOTS.get()), 20, 2, BlockPredicate.allOf(BlockPredicate.anyOf(BlockPredicate.matchesBlocks(List.of(Blocks.AIR, Blocks.CAVE_AIR, Blocks.VOID_AIR, Blocks.WATER)), BlockPredicate.matchesTag(BlockTags.LEAVES), BlockPredicate.matchesTag(BlockTags.REPLACEABLE_PLANTS)), BlockPredicate.matchesTag(Direction.DOWN.getNormal(), BlockTags.AZALEA_GROWS_ON)))));
    }

    public static void createPlacedFeatures(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> placedFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        context.register(VAMPIRE_FLOWER_PLACED, new PlacedFeature(placedFeatures.getOrThrow(VAMPIRE_FLOWER), List.of(RarityFilter.onAverageOnceEvery(4), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, InSquarePlacement.spread(), BiomeFilter.biome())));
        context.register(CURSED_ROOT_PLACED, new PlacedFeature(placedFeatures.getOrThrow(CURSED_ROOT), List.of(RarityFilter.onAverageOnceEvery(1), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome())));
        context.register(DARK_SPRUCE_TREE_PLACED, new PlacedFeature(placedFeatures.getOrThrow(DARK_SPRUCE_TREE), List.of(PlacementUtils.filteredByBlockSurvival((ModBlocks.DARK_SPRUCE_SAPLING.get())))));
        context.register(CURSED_SPRUCE_TREE_PLACED, new PlacedFeature(placedFeatures.getOrThrow(CURSED_SPRUCE_TREE), List.of(PlacementUtils.filteredByBlockSurvival((ModBlocks.CURSED_SPRUCE_SAPLING.get())))));
        context.register(VAMPIRE_DUNGEON_PLACED, new PlacedFeature(placedFeatures.getOrThrow(VAMPIRE_DUNGEON), List.of(CountPlacement.of(3), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.top()), BiomeFilter.biome())));
        context.register(WATER_LAKE_PLACED, new PlacedFeature(placedFeatures.getOrThrow(WATER_LAKE), List.of(RarityFilter.onAverageOnceEvery(200), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome())));
        context.register(VAMPIRE_TREES_PLACED, new PlacedFeature(placedFeatures.getOrThrow(VAMPIRE_TREES), VegetationPlacements.treePlacement(PlacementUtils.countExtra(10, 0.1f, 1))));
        context.register(FOREST_GRASS_PLACED, new PlacedFeature(placedFeatures.getOrThrow(VegetationFeatures.PATCH_GRASS), VegetationPlacements.worldSurfaceSquaredWithCount(2)));
        context.register(ORE_DARK_STONE_UPPER_PLACED, new PlacedFeature(placedFeatures.getOrThrow(ORE_DARK_STONE), rareOrePlacement(6, HeightRangePlacement.uniform(VerticalAnchor.absolute(64), VerticalAnchor.absolute(128)))));
        context.register(ORE_DARK_STONE_LOWER_PLACED, new PlacedFeature(placedFeatures.getOrThrow(ORE_DARK_STONE), commonOrePlacement(2, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(60)))));
        context.register(ORE_CURSED_DIRT_PLACED, new PlacedFeature(placedFeatures.getOrThrow(ORE_CURSED_DIRT), commonOrePlacement(7, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(160)))));
        context.register(CURSED_ROOTS_PLACED, new PlacedFeature(placedFeatures.getOrThrow(CURSED_ROOTS), List.of(CountPlacement.of(UniformInt.of(1, 10)), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.of(-1)), BiomeFilter.biome())));
    }

    public static void createBiomeModifier(BootstapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomeLookup = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> placedFeatureLookup = context.lookup(Registries.PLACED_FEATURE);
        context.register(VAMPIRE_SPAWN, ExtendedAddSpawnsBiomeModifier.singleSpawn(biomeLookup.getOrThrow(ModTags.Biomes.HasSpawn.VAMPIRE), biomeLookup.getOrThrow(ModTags.Biomes.NoSpawn.VAMPIRE), new ExtendedAddSpawnsBiomeModifier.ExtendedSpawnData(ModEntities.VAMPIRE.get(), 80, 1, 3, MobCategory.MONSTER)));
        context.register(HUNTER_SPAWN, ForgeBiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(biomeLookup.getOrThrow(ModTags.Biomes.HasSpawn.HUNTER), new MobSpawnSettings.SpawnerData(ModEntities.HUNTER.get(), 0, 1, 3)));
        context.register(ADVANCED_VAMPIRE_SPAWN,ExtendedAddSpawnsBiomeModifier.singleSpawn(biomeLookup.getOrThrow(ModTags.Biomes.HasSpawn.ADVANCED_VAMPIRE), biomeLookup.getOrThrow(ModTags.Biomes.NoSpawn.ADVANCED_VAMPIRE), new ExtendedAddSpawnsBiomeModifier.ExtendedSpawnData(ModEntities.ADVANCED_VAMPIRE.get(), 25, 1, 3, MobCategory.MONSTER)));
        context.register(ADVANCED_HUNTER_SPAWN,ForgeBiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(biomeLookup.getOrThrow(ModTags.Biomes.HasSpawn.ADVANCED_HUNTER), new MobSpawnSettings.SpawnerData(ModEntities.ADVANCED_HUNTER.get(), 0, 1, 1)));
        context.register(VAMPIRE_DUNGEON_MODIFIER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(biomeLookup.getOrThrow(ModTags.Biomes.HasStructure.VAMPIRE_DUNGEON), HolderSet.direct(placedFeatureLookup.getOrThrow(VampirismFeatures.VAMPIRE_DUNGEON_PLACED)), GenerationStep.Decoration.UNDERGROUND_STRUCTURES));
        context.register(VAMPIRE_ROOTS, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(biomeLookup.getOrThrow(BiomeTags.IS_OVERWORLD), HolderSet.direct(placedFeatureLookup.getOrThrow(VampirismFeatures.CURSED_ROOTS_PLACED)), GenerationStep.Decoration.VEGETAL_DECORATION));
    }

    public static void createStructureSets(BootstapContext<StructureSet> context) {
        HolderGetter<Structure> structureLookup = context.lookup(Registries.STRUCTURE);
        context.register(HUNTER_CAMP, new StructureSet(structureLookup.getOrThrow(ModFeatures.HUNTER_CAMP), new RandomSpreadStructurePlacement(9, 4, RandomSpreadType.LINEAR, 1724616580)));
        context.register(MOTHER_SET, new StructureSet(structureLookup.getOrThrow(ModFeatures.MOTHER_KEY), new RandomSpreadStructurePlacement(9, 4, RandomSpreadType.LINEAR, 1724616580)));
    }

}
