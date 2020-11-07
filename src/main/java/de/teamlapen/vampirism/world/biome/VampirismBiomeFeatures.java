package de.teamlapen.vampirism.world.biome;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFeatures;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.trunkplacer.StraightTrunkPlacer;

public class VampirismBiomeFeatures {

    public static final ConfiguredFeature<?, ?> vampire_flower = registerFeature("vampire_flower", Feature.FLOWER.withConfiguration(new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.vampire_orchid.getDefaultState()), SimpleBlockPlacer.PLACER).tries(64).build()).func_242730_a(FeatureSpread.func_242253_a(-1, 4)).withPlacement(Features.Placements.VEGETATION_PLACEMENT).withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT).func_242731_b(5));
    public static final ConfiguredFeature<?, ?> mod_water_lake = registerFeature("mod_water_lake", ModFeatures.mod_lake.withConfiguration(new BlockStateFeatureConfig(Blocks.WATER.getDefaultState())).withPlacement(Placement.WATER_LAKE.configure(new ChanceConfig(4))));

    public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> vampire_tree = registerFeature("vampire_tree", Feature.TREE.withConfiguration((new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(Blocks.SPRUCE_LOG.getDefaultState()), new SimpleBlockStateProvider(ModBlocks.vampire_spruce_leaves.getDefaultState()), new BlobFoliagePlacer(FeatureSpread.func_242252_a(2), FeatureSpread.func_242252_a(0), 3), new StraightTrunkPlacer(4, 2, 0), new TwoLayerFeature(1, 0, 1))).setIgnoreVines().build()));
    public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> vampire_tree_red = registerFeature("vampire_tree_red", Feature.TREE.withConfiguration((new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.bloody_spruce_log.getDefaultState()), new SimpleBlockStateProvider(ModBlocks.bloody_spruce_leaves.getDefaultState()), new BlobFoliagePlacer(FeatureSpread.func_242252_a(2), FeatureSpread.func_242252_a(0), 3), new StraightTrunkPlacer(4, 2, 0), new TwoLayerFeature(1, 0, 1))).setIgnoreVines().build()));

    public static final ConfiguredFeature<?, ?> vampire_trees = registerFeature("vampire_trees", Feature.RANDOM_SELECTOR.withConfiguration(new MultipleRandomFeatureConfig(ImmutableList.of(vampire_tree_red.withChance(0.3f)), vampire_tree)).withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT).withPlacement(Placement.field_242902_f.configure(new AtSurfaceWithExtraConfig(6, 0.1F, 1))));

    public static final ConfiguredFeature<?, ?> vampire_dungeon = registerFeature("vampire_dungeon", ModFeatures.vampire_dungeon.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).func_242733_d(256).func_242728_a().func_242729_a(VampirismConfig.BALANCE.vampireDungeonWeight.get()));
    public static final StructureFeature<?, ?> hunter_camp = registerStructure("hunter_camp", ModFeatures.hunter_camp.func_236391_a_/*withConfiguration*/(IFeatureConfig.NO_FEATURE_CONFIG));

    private static <T extends IFeatureConfig> ConfiguredFeature<T, ?> registerFeature(String name, ConfiguredFeature<T, ?> feature) {
        return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(REFERENCE.MODID, name), feature);
    }

    private static <T extends IFeatureConfig> StructureFeature<T, ?> registerStructure(String name, StructureFeature<T, ?> structure){
        return Registry.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, new ResourceLocation(REFERENCE.MODID, name), structure);
    }

    public static void init() {
    }


    public static void addVampireFlower(BiomeGenerationSettings.Builder builder) {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, vampire_flower);
    }

    public static void addWaterSprings(BiomeGenerationSettings.Builder builder) {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SPRING_WATER);
    }

    public static void addModdedWaterLake(BiomeGenerationSettings.Builder builder) {
        builder.withFeature(GenerationStage.Decoration.LAKES, mod_water_lake);
    }

    public static void addVampireTrees(BiomeGenerationSettings.Builder builder) {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, vampire_trees);
    }
}
