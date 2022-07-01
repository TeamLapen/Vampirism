package de.teamlapen.vampirism.world.biome;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFeatures;
import de.teamlapen.vampirism.mixin.FlatGenerationSettingsAccessor;
import de.teamlapen.vampirism.world.gen.treedecorator.TrunkCursedVineTreeDecorator;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.foliageplacer.SpruceFoliagePlacer;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.trunkplacer.StraightTrunkPlacer;

import java.util.OptionalInt;

public class VampirismBiomeFeatures {

    public static BlockClusterFeatureConfig BUSH_CONFIG = new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.CURSED_ROOTS.get().defaultBlockState()), SimpleBlockPlacer.INSTANCE).tries(7).build();
    public static BlockClusterFeatureConfig DEFAULT_GRASS_CONFIG = new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(Blocks.GRASS.defaultBlockState()), SimpleBlockPlacer.INSTANCE).tries(16).build();
    public static BlockClusterFeatureConfig VAMPIRE_FLOWER = new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.VAMPIRE_ORCHID.get().defaultBlockState()), SimpleBlockPlacer.INSTANCE).tries(6).build();

    public static ConfiguredFeature<?, ?> patch_bush;
    public static ConfiguredFeature<?, ?> patch_grass_forest;
    public static ConfiguredFeature<?, ?> ore_cursed_earth;
    public static ConfiguredFeature<?, ?> ore_dark_stone;

    public static ConfiguredFeature<?, ?> vampire_flower;
    public static ConfiguredFeature<?, ?> mod_water_lake;

    public static ConfiguredFeature<?, ?> vampire_dungeon;
    public static StructureFeature<?, ?> hunter_camp;

    public static ConfiguredFeature<BaseTreeFeatureConfig,?> dark_spruce_tree;

    public static ConfiguredFeature<BaseTreeFeatureConfig,?> cursed_spruce_tree;

    public static ConfiguredFeature<?, ?> vampire_trees;

    public static ConfiguredFeature<?,?> dark_stone;


    public static void registerBiomeFeatures() {
        patch_bush = registerFeature("patch_bush", Feature.RANDOM_PATCH.configured(BUSH_CONFIG).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE));
        patch_grass_forest = registerFeature("patch_grass_forest", Feature.RANDOM_PATCH.configured(DEFAULT_GRASS_CONFIG).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).count(2));
        ore_cursed_earth = registerFeature("ore_cursed_earth", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, ModBlocks.CURSED_EARTH.get().defaultBlockState(), 33)).range(256).squared().count(10));
        ore_dark_stone = registerFeature("ore_dark_stone", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, ModBlocks.CASTLE_BLOCK_DARK_STONE.get().defaultBlockState(), 33)).range(80).squared().count(10));
        vampire_flower = registerFeature("vampire_flower", Feature.FLOWER.configured(VAMPIRE_FLOWER).count(FeatureSpread.of(-1, 4)).decorated(Features.Placements.ADD_32).decorated(Features.Placements.HEIGHTMAP_SQUARE).count(5));
        mod_water_lake = registerFeature("mod_water_lake", ModFeatures.MOD_LAKE.get().configured(new BlockStateFeatureConfig(Blocks.WATER.defaultBlockState())).decorated(Placement.WATER_LAKE.configured(new ChanceConfig(4))));
        vampire_dungeon = registerFeature("vampire_dungeon", ModFeatures.VAMPIRE_DUNGEON.get().configured(IFeatureConfig.NONE).range(256).squared().count(2 /*not entirely sure, but higher is more frequent - vanilla is 8 */));
        hunter_camp = registerStructure("hunter_camp", ModFeatures.HUNTER_CAMP.get().configured/*withConfiguration*/(IFeatureConfig.NONE));
        dark_spruce_tree = registerFeature("dark_spruce_tree", Feature.TREE.configured(new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.DARK_SPRUCE_LOG.get().defaultBlockState()), new SimpleBlockStateProvider(ModBlocks.DARK_SPRUCE_LEAVES.get().defaultBlockState()),
                new SpruceFoliagePlacer(FeatureSpread.of(2, 1), FeatureSpread.of(0, 2), FeatureSpread.of(3, 7)),
                new StraightTrunkPlacer(11,2,2),
                new ThreeLayerFeature(5,8,0,3,3, OptionalInt.of(5)))
                .build())
        );
        cursed_spruce_tree = registerFeature("cursed_spruce_tree", Feature.TREE.configured(new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.CURSED_SPRUCE_LOG.get().defaultBlockState()), new SimpleBlockStateProvider(ModBlocks.DARK_SPRUCE_LEAVES.get().defaultBlockState()),
                new SpruceFoliagePlacer(FeatureSpread.of(2, 1), FeatureSpread.of(0, 2), FeatureSpread.of(3, 7)),
                new StraightTrunkPlacer(11,2,2),
                new ThreeLayerFeature(5,8,0,3,3, OptionalInt.of(5)))
                .decorators(ImmutableList.of(TrunkCursedVineTreeDecorator.INSTANCE))
                .build())
        );
        vampire_trees = registerFeature("vampire_trees", Feature.RANDOM_SELECTOR.configured(
                        new MultipleRandomFeatureConfig(ImmutableList.of(cursed_spruce_tree.weighted(0.3f)), dark_spruce_tree))
                .decorated(Features.Placements.HEIGHTMAP_SQUARE)
                .decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(10, 0.1F, 1))));
        dark_stone = registerFeature("dark_stone", Feature.DISK.configured(new SphereReplaceConfig(ModBlocks.CASTLE_BLOCK_DARK_STONE.get().defaultBlockState(), FeatureSpread.of(2,4),2, ImmutableList.of(Blocks.STONE.defaultBlockState()))).decorated(Features.Placements.TOP_SOLID_HEIGHTMAP_SQUARE));
    }

    private static <T extends IFeatureConfig> ConfiguredFeature<T, ?> registerFeature(String name, ConfiguredFeature<T, ?> feature) {
        return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(REFERENCE.MODID, name), feature);
    }

    private static <T extends IFeatureConfig> StructureFeature<T, ?> registerStructure(String name, StructureFeature<T, ?> structure) {
        return Registry.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, new ResourceLocation(REFERENCE.MODID, name), structure);
    }

    public static void init() {
        if (VampirismConfig.COMMON.enforceTentGeneration.get())
            FlatGenerationSettingsAccessor.getStructures_vampirism().put(ModFeatures.HUNTER_CAMP.get(), hunter_camp);
    }


    public static void addVampireFlower(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, vampire_flower);
    }

    public static void addWaterSprings(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SPRING_WATER);
    }

    public static void addModdedWaterLake(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStage.Decoration.LAKES.ordinal(), () -> mod_water_lake);
    }

    public static void addVampireTrees(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION.ordinal(), () -> vampire_trees);
    }

    public static void addDarkStoneSoftDisk(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES.ordinal(), () -> dark_stone);
    }

    public static void addBushPatch(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION.ordinal(), () -> patch_bush);
        builder.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION.ordinal(), () -> patch_grass_forest);
    }

    public static void addUndergroundVariety(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES.ordinal(), () -> Features.ORE_GRAVEL);
        builder.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES.ordinal(), () -> ore_cursed_earth);
        builder.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES.ordinal(), () -> ore_dark_stone);
    }


}
