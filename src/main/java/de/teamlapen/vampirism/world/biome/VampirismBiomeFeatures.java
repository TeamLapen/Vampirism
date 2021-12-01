package de.teamlapen.vampirism.world.biome;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFeatures;
import de.teamlapen.vampirism.mixin.FlatGenerationSettingsAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.StructureFeatures;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class VampirismBiomeFeatures {

    public static final RandomPatchConfiguration VAMPIRE_FLOWER_CONFIG = FeatureUtils.simpleRandomPatchConfiguration(64, Feature.SIMPLE_BLOCK.configured(new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(ModBlocks.vampire_orchid.defaultBlockState(), 2).add(ModBlocks.vampire_orchid.defaultBlockState(), 1)))).onlyWhenEmpty());
    public static final ConfiguredFeature<?, ?> vampire_flower = registerFeature("vampire_flower", Feature.FLOWER.configured(VAMPIRE_FLOWER_CONFIG));

    public static final ConfiguredFeature<TreeConfiguration, ?> vampire_tree = registerFeature("vampire_tree", Feature.TREE.configured((new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.SPRUCE_LOG.defaultBlockState()), new StraightTrunkPlacer(4, 2, 0), BlockStateProvider.simple(ModBlocks.vampire_spruce_leaves.defaultBlockState()), new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3), new TwoLayersFeatureSize(1, 0, 1))).ignoreVines().build())); //TODO 1.18 what happened to the saplings
    public static final ConfiguredFeature<TreeConfiguration, ?> vampire_tree_red = registerFeature("vampire_tree_red", Feature.TREE.configured((new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(ModBlocks.bloody_spruce_log.defaultBlockState()), new StraightTrunkPlacer(4, 2, 0), BlockStateProvider.simple(ModBlocks.bloody_spruce_leaves.defaultBlockState()), new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3), new TwoLayersFeatureSize(1, 0, 1))).ignoreVines().build()));

    public static final ConfiguredFeature<?, ?> vampire_trees = registerFeature("vampire_trees", Feature.RANDOM_SELECTOR.configured(new RandomFeatureConfiguration(ImmutableList.of(new WeightedPlacedFeature(vampire_tree_red.placed(), 0.3f)), vampire_tree.placed())));

    public static final ConfiguredFeature<?, ?> vampire_dungeon = registerFeature("vampire_dungeon", ModFeatures.vampire_dungeon.configured(FeatureConfiguration.NONE));
    public static final ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> hunter_camp = registerStructure("hunter_camp", ModFeatures.hunter_camp.configured(FeatureConfiguration.NONE));

    public static final PlacedFeature vampire_flower_placed = PlacementUtils.register("vampire_flower", vampire_flower.placed(RarityFilter.onAverageOnceEvery(5), PlacementUtils.HEIGHTMAP, InSquarePlacement.spread(), BiomeFilter.biome())); //TODO 1.18 verify correct placement
    public static final PlacedFeature vampire_trees_placed = PlacementUtils.register("vampire_trees", vampire_trees.placed(PlacementUtils.countExtra(0,0.05F,1), InSquarePlacement.spread(), SurfaceWaterDepthFilter.forMaxDepth(0), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(Blocks.OAK_SAPLING.defaultBlockState(), BlockPos.ZERO)), BiomeFilter.biome()));//TODO 1.18 verify correct placement

    public static final PlacedFeature vampire_dungeon_placed = PlacementUtils.register("vampire_dungeon", vampire_dungeon.placed(CountPlacement.of(3), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.top()), BiomeFilter.biome())); //TODO 1.18 verify correct placement

    private static <T extends FeatureConfiguration> ConfiguredFeature<T, ?> registerFeature(String name, ConfiguredFeature<T, ?> feature) {
        return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation(REFERENCE.MODID, name), feature);
    }

    private static <T extends FeatureConfiguration> ConfiguredStructureFeature<T, ?> registerStructure(String name, ConfiguredStructureFeature<T, ?> structure) {
        return Registry.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, new ResourceLocation(REFERENCE.MODID, name), structure);
    }

    public static void init() {
        if (VampirismConfig.COMMON.enforceTentGeneration.get()) {
            FlatGenerationSettingsAccessor.getStructures_vampirism().put(ModFeatures.hunter_camp, hunter_camp);
        }
    }

    private static void registerStructuresForBiomes(BiConsumer<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>> consumer) { //TODO 1.18 add structure to  StructureSettings#configuredStructures look at the constructor
        Set<ResourceKey<Biome>> set = ForgeRegistries.BIOMES.getValues().stream().filter(biome -> VampirismAPI.worldGenRegistry().canStructureBeGeneratedInBiome(ModFeatures.hunter_camp.getRegistryName(), biome.getRegistryName(), biome.getBiomeCategory())).flatMap(biome -> ForgeRegistries.BIOMES.getResourceKey(biome).stream()).collect(Collectors.toUnmodifiableSet());
        set.forEach(biome -> consumer.accept(hunter_camp, biome));
    }


    public static void addVampireFlower(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, vampire_flower_placed);
    }

    public static void addWaterSprings(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.FLUID_SPRINGS, MiscOverworldPlacements.SPRING_WATER);
    }

    public static void addModdedWaterLake(BiomeGenerationSettings.Builder builder) {
        // builder.addFeature(GenerationStep.Decoration.LAKES, mod_water_lake); TODO 1.18 is there a vanilla water lake ?
    }

    public static void addVampireTrees(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, vampire_trees_placed);
    }
}
