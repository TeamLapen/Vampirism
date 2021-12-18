package de.teamlapen.vampirism.world.biome;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFeatures;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class VampirismBiomeFeatures {

    public static final RandomPatchConfiguration VAMPIRE_FLOWER_CONFIG = FeatureUtils.simpleRandomPatchConfiguration(64, Feature.SIMPLE_BLOCK.configured(new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(ModBlocks.vampire_orchid.defaultBlockState(), 2).add(ModBlocks.vampire_orchid.defaultBlockState(), 1)))).onlyWhenEmpty());
    public static final ConfiguredFeature<?, ?> vampire_flower = registerConfiguredFeature("vampire_flower", Feature.FLOWER.configured(VAMPIRE_FLOWER_CONFIG));

    public static final ConfiguredFeature<TreeConfiguration, ?> vampire_tree = registerConfiguredFeature("vampire_tree", Feature.TREE.configured((new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.SPRUCE_LOG.defaultBlockState()), new StraightTrunkPlacer(4, 2, 0), BlockStateProvider.simple(ModBlocks.vampire_spruce_leaves.defaultBlockState()), new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3), new TwoLayersFeatureSize(1, 0, 1))).ignoreVines().build())); //TODO 1.18 what happened to the saplings
    public static final ConfiguredFeature<TreeConfiguration, ?> vampire_tree_red = registerConfiguredFeature("vampire_tree_red", Feature.TREE.configured((new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(ModBlocks.bloody_spruce_log.defaultBlockState()), new StraightTrunkPlacer(4, 2, 0), BlockStateProvider.simple(ModBlocks.bloody_spruce_leaves.defaultBlockState()), new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3), new TwoLayersFeatureSize(1, 0, 1))).ignoreVines().build()));

    public static final ConfiguredFeature<?, ?> vampire_dungeon = registerConfiguredFeature("vampire_dungeon", ModFeatures.vampire_dungeon.configured(FeatureConfiguration.NONE));
    public static final ConfiguredFeature<LakeFeature.Configuration, ?> water_lake = FeatureUtils.register("vampirism:mod_lake", Feature.LAKE.configured(new LakeFeature.Configuration(BlockStateProvider.simple(Blocks.WATER.defaultBlockState()), BlockStateProvider.simple(ModBlocks.castle_block_dark_stone.defaultBlockState()))));
    public static final ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> hunter_camp = registerConfiguredStructure("hunter_camp", ModFeatures.hunter_camp.configured(FeatureConfiguration.NONE));

    public static final PlacedFeature vampire_tree_placed = PlacementUtils.register("vampire_tree_placed", vampire_tree.filteredByBlockSurvival(ModBlocks.vampire_spruce_sapling));
    public static final PlacedFeature vampire_tree_red_placed = PlacementUtils.register("vampire_tree_red_placed", vampire_tree_red.filteredByBlockSurvival(ModBlocks.bloody_spruce_sapling));
    public static final ConfiguredFeature<RandomFeatureConfiguration, ?> vampire_trees = FeatureUtils.register("vampire_trees_placed", Feature.RANDOM_SELECTOR.configured(new RandomFeatureConfiguration(List.of(new WeightedPlacedFeature(vampire_tree_red_placed, 0.3f)), vampire_tree_placed)));

    public static final PlacedFeature water_lake_placed = PlacementUtils.register("vampirism:mod_lake_placed", water_lake.placed(RarityFilter.onAverageOnceEvery(200), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
    public static final PlacedFeature vampire_trees_placed = PlacementUtils.register("vampirism:vampire_trees", vampire_trees.placed(VegetationPlacements.treePlacement(PlacementUtils.countExtra(6, 0.2F, 2))));
    public static final PlacedFeature vampire_dungeon_placed = PlacementUtils.register("vampirism:vampire_dungeon", vampire_dungeon.placed(CountPlacement.of(3), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.top()), BiomeFilter.biome())); //TODO 1.18 verify correct placement
    public static final PlacedFeature vampire_flower_placed = PlacementUtils.register("vampirism:vampire_flower", vampire_flower.placed(RarityFilter.onAverageOnceEvery(5), PlacementUtils.HEIGHTMAP, InSquarePlacement.spread(), BiomeFilter.biome())); //TODO 1.18 verify correct placement
    public static final PlacedFeature forest_grass_placed = PlacementUtils.register("vampirism:forest_grass", VegetationFeatures.PATCH_GRASS.placed(VegetationPlacements.worldSurfaceSquaredWithCount(2)));

    private static <T extends FeatureConfiguration> ConfiguredFeature<T, ?> registerConfiguredFeature(String name, ConfiguredFeature<T, ?> feature) {
        return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation(REFERENCE.MODID, name), feature);
    }

    private static <T extends FeatureConfiguration> ConfiguredStructureFeature<T, ?> registerConfiguredStructure(String name, ConfiguredStructureFeature<T, ?> structure) {
        return Registry.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, new ResourceLocation(REFERENCE.MODID, name), structure);
    }

    public static void addVampireFlower(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, vampire_flower_placed);
    }

    public static void addWaterSprings(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.FLUID_SPRINGS, MiscOverworldPlacements.SPRING_WATER);
    }

    public static void addModdedWaterLake(BiomeGenerationSettings.Builder builder) {
         builder.addFeature(GenerationStep.Decoration.LAKES, water_lake_placed);
    }

    public static void addVampireTrees(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, vampire_trees_placed);
    }

    public static void addDefaultCarversWithoutLakes(BiomeGenerationSettings.Builder builder) {
        builder.addCarver(GenerationStep.Carving.AIR, Carvers.CAVE);
        builder.addCarver(GenerationStep.Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND);
        builder.addCarver(GenerationStep.Carving.AIR, Carvers.CANYON);
    }

    public static void addStructuresToBiomes(Set<Map.Entry<ResourceKey<Biome>, Biome>> allBiomes, BiConsumer<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>> consumer) {
        Set<ResourceKey<Biome>> set = allBiomes.stream().filter(biomeEntry -> VampirismAPI.worldGenRegistry().canStructureBeGeneratedInBiome(ModFeatures.hunter_camp.getRegistryName(), biomeEntry.getValue().getRegistryName(), biomeEntry.getValue().getBiomeCategory())).map(Map.Entry::getKey).collect(Collectors.toUnmodifiableSet());
        set.forEach(biome -> consumer.accept(hunter_camp, biome));
    }
}
