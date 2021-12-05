package de.teamlapen.vampirism.world.biome;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFeatures;
import de.teamlapen.vampirism.mixin.FlatGenerationSettingsAccessor;
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
import net.minecraft.world.gen.foliageplacer.SpruceFoliagePlacer;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.trunkplacer.StraightTrunkPlacer;

import java.util.OptionalInt;

public class VampirismBiomeFeatures {

    public static final BlockClusterFeatureConfig BUSH_CONFIG = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.cursed_roots.defaultBlockState()), SimpleBlockPlacer.INSTANCE)).tries(4).build();
    public static final ConfiguredFeature<?, ?> patch_bush = registerFeature("patch_bush", Feature.RANDOM_PATCH.configured(BUSH_CONFIG).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE));
    public static final ConfiguredFeature<?, ?> ore_cursed_earth = registerFeature("ore_cursed_earth", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, ModBlocks.cursed_earth.defaultBlockState(), 33)).range(256).squared().count(10));
    public static final ConfiguredFeature<?, ?> ore_dark_stone = registerFeature("ore_dark_stone", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, ModBlocks.castle_block_dark_stone.defaultBlockState(), 33)).range(80).squared().count(10));

    public static final ConfiguredFeature<?, ?> vampire_flower = registerFeature("vampire_flower", Feature.FLOWER.configured(new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.vampire_orchid.defaultBlockState()), SimpleBlockPlacer.INSTANCE).tries(20).build()).count(FeatureSpread.of(-1, 4)).decorated(Features.Placements.ADD_32).decorated(Features.Placements.HEIGHTMAP_SQUARE).count(5));
    public static final ConfiguredFeature<?, ?> mod_water_lake = registerFeature("mod_water_lake", ModFeatures.mod_lake.configured(new BlockStateFeatureConfig(Blocks.WATER.defaultBlockState())).decorated(Placement.WATER_LAKE.configured(new ChanceConfig(4))));

    public static final ConfiguredFeature<?, ?> vampire_dungeon = registerFeature("vampire_dungeon", ModFeatures.vampire_dungeon.configured(IFeatureConfig.NONE).range(256).squared().count(2 /*not entirely sure, but higher is more frequent - vanilla is 8 */));
    public static final StructureFeature<?, ?> hunter_camp = registerStructure("hunter_camp", ModFeatures.hunter_camp.configured/*withConfiguration*/(IFeatureConfig.NONE));

    public static final ConfiguredFeature<BaseTreeFeatureConfig,?> dark_spruce_tree = registerFeature("dark_spruce_tree", Feature.TREE.configured(new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.dark_spruce_log.defaultBlockState()), new SimpleBlockStateProvider(ModBlocks.dark_spruce_leaves.defaultBlockState()),
            new SpruceFoliagePlacer(FeatureSpread.of(2, 1), FeatureSpread.of(0, 2), FeatureSpread.of(3, 7)),
            new StraightTrunkPlacer(11,2,2),
            new ThreeLayerFeature(5,8,0,3,3, OptionalInt.of(5)))
            .build())
    );

    public static final ConfiguredFeature<BaseTreeFeatureConfig,?> cursed_spruce_tree = registerFeature("cursed_spruce_tree", Feature.TREE.configured(new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.cursed_spruce_log.defaultBlockState()), new SimpleBlockStateProvider(ModBlocks.dark_spruce_leaves.defaultBlockState()),
            new SpruceFoliagePlacer(FeatureSpread.of(2, 1), FeatureSpread.of(0, 2), FeatureSpread.of(3, 7)),
            new StraightTrunkPlacer(11,2,2),
            new ThreeLayerFeature(5,8,0,3,3, OptionalInt.of(5)))
            .build())
    );

    public static final ConfiguredFeature<?, ?> vampire_trees = registerFeature("vampire_trees", Feature.RANDOM_SELECTOR.configured(
            new MultipleRandomFeatureConfig(ImmutableList.of(cursed_spruce_tree.weighted(0.3f)), dark_spruce_tree))
            .decorated(Features.Placements.HEIGHTMAP_SQUARE)
            .decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(10, 0.1F, 1))));

    public static final ConfiguredFeature<?,?> dark_stone = registerFeature("dark_stone", Feature.DISK.configured(new SphereReplaceConfig(ModBlocks.castle_block_dark_stone.defaultBlockState(), FeatureSpread.of(2,4),2, ImmutableList.of(Blocks.STONE.defaultBlockState()))).decorated(Features.Placements.TOP_SOLID_HEIGHTMAP_SQUARE));

    private static <T extends IFeatureConfig> ConfiguredFeature<T, ?> registerFeature(String name, ConfiguredFeature<T, ?> feature) {
        return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(REFERENCE.MODID, name), feature);
    }

    private static <T extends IFeatureConfig> StructureFeature<T, ?> registerStructure(String name, StructureFeature<T, ?> structure) {
        return Registry.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, new ResourceLocation(REFERENCE.MODID, name), structure);
    }

    public static void init() {
        if (VampirismConfig.COMMON.enforceTentGeneration.get())
            FlatGenerationSettingsAccessor.getStructures_vampirism().put(ModFeatures.hunter_camp, hunter_camp);
    }


    public static void addVampireFlower(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, vampire_flower);
    }

    public static void addWaterSprings(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SPRING_WATER);
    }

    public static void addModdedWaterLake(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStage.Decoration.LAKES, mod_water_lake);
    }

    public static void addVampireTrees(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, vampire_trees);
    }

    public static void addDarkStoneSoftDisk(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, dark_stone);
    }

    public static void addBushPatch(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, patch_bush);
    }

    public static void addUndergroundVariety(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_GRAVEL);
        builder.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, ore_cursed_earth);
        builder.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, ore_dark_stone);
    }


}
