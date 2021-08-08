package de.teamlapen.vampirism.world.biome;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFeatures;
import de.teamlapen.vampirism.mixin.FlatGenerationSettingsAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.blockplacers.SimpleBlockPlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.placement.FrequencyWithExtraChanceDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.ChanceDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;

import net.minecraft.data.worldgen.Features;
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;

public class VampirismBiomeFeatures {

    public static final ConfiguredFeature<?, ?> vampire_flower = registerFeature("vampire_flower", Feature.FLOWER.configured(new RandomPatchConfiguration.GrassConfigurationBuilder(new SimpleStateProvider(ModBlocks.vampire_orchid.defaultBlockState()), SimpleBlockPlacer.INSTANCE).tries(64).build()).count(UniformInt.of(-1, 4)).decorated(Features.Decorators.ADD_32).decorated(Features.Decorators.HEIGHTMAP_SQUARE).count(5));
    public static final ConfiguredFeature<?, ?> mod_water_lake = registerFeature("mod_water_lake", ModFeatures.mod_lake.configured(new BlockStateConfiguration(Blocks.WATER.defaultBlockState())).decorated(FeatureDecorator.WATER_LAKE.configured(new ChanceDecoratorConfiguration(4))));

    public static final ConfiguredFeature<TreeConfiguration, ?> vampire_tree = registerFeature("vampire_tree", Feature.TREE.configured((new TreeConfiguration.TreeConfigurationBuilder(new SimpleStateProvider(Blocks.SPRUCE_LOG.defaultBlockState()), new SimpleStateProvider(ModBlocks.vampire_spruce_leaves.defaultBlockState()), new BlobFoliagePlacer(UniformInt.fixed(2), UniformInt.fixed(0), 3), new StraightTrunkPlacer(4, 2, 0), new TwoLayersFeatureSize(1, 0, 1))).ignoreVines().build()));
    public static final ConfiguredFeature<TreeConfiguration, ?> vampire_tree_red = registerFeature("vampire_tree_red", Feature.TREE.configured((new TreeConfiguration.TreeConfigurationBuilder(new SimpleStateProvider(ModBlocks.bloody_spruce_log.defaultBlockState()), new SimpleStateProvider(ModBlocks.bloody_spruce_leaves.defaultBlockState()), new BlobFoliagePlacer(UniformInt.fixed(2), UniformInt.fixed(0), 3), new StraightTrunkPlacer(4, 2, 0), new TwoLayersFeatureSize(1, 0, 1))).ignoreVines().build()));

    public static final ConfiguredFeature<?, ?> vampire_trees = registerFeature("vampire_trees", Feature.RANDOM_SELECTOR.configured(new RandomFeatureConfiguration(ImmutableList.of(vampire_tree_red.weighted(0.3f)), vampire_tree)).decorated(Features.Decorators.HEIGHTMAP_SQUARE).decorated(FeatureDecorator.COUNT_EXTRA.configured(new FrequencyWithExtraChanceDecoratorConfiguration(6, 0.1F, 1))));

    public static final ConfiguredFeature<?, ?> vampire_dungeon = registerFeature("vampire_dungeon", ModFeatures.vampire_dungeon.configured(FeatureConfiguration.NONE).range(256).squared().count(2 /*not entirely sure, but higher is more frequent - vanilla is 8 */));
    public static final ConfiguredStructureFeature<?, ?> hunter_camp = registerStructure("hunter_camp", ModFeatures.hunter_camp.configured/*withConfiguration*/(FeatureConfiguration.NONE));

    private static <T extends FeatureConfiguration> ConfiguredFeature<T, ?> registerFeature(String name, ConfiguredFeature<T, ?> feature) {
        return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation(REFERENCE.MODID, name), feature);
    }

    private static <T extends FeatureConfiguration> ConfiguredStructureFeature<T, ?> registerStructure(String name, ConfiguredStructureFeature<T, ?> structure) {
        return Registry.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, new ResourceLocation(REFERENCE.MODID, name), structure);
    }

    public static void init() {
        if (VampirismConfig.COMMON.enforceTentGeneration.get())
            FlatGenerationSettingsAccessor.getStructures_vampirism().put(ModFeatures.hunter_camp, hunter_camp);
    }


    public static void addVampireFlower(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, vampire_flower);
    }

    public static void addWaterSprings(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.SPRING_WATER);
    }

    public static void addModdedWaterLake(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.LAKES, mod_water_lake);
    }

    public static void addVampireTrees(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, vampire_trees);
    }
}
