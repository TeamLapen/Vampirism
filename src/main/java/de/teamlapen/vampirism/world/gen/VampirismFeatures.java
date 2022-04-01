package de.teamlapen.vampirism.world.gen;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFeatures;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.world.gen.features.VampireDungeonFeature;
import de.teamlapen.vampirism.world.gen.structures.huntercamp.HunterCampPieces;
import de.teamlapen.vampirism.world.gen.util.BiomeTopBlockProcessor;
import de.teamlapen.vampirism.world.gen.util.RandomStructureProcessor;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

import java.util.List;

public class VampirismFeatures {

    //Feature configuration
    private static final RandomPatchConfiguration VAMPIRE_FLOWER_CONFIG = FeatureUtils.simpleRandomPatchConfiguration(64,  PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(ModBlocks.vampire_orchid.defaultBlockState(), 2).add(ModBlocks.vampire_orchid.defaultBlockState(), 1)))));
    private static final TreeConfiguration VAMPIRE_TREE_CONFIG = new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.SPRUCE_LOG.defaultBlockState()), new StraightTrunkPlacer(4, 2, 0), BlockStateProvider.simple(ModBlocks.vampire_spruce_leaves.defaultBlockState()), new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3), new TwoLayersFeatureSize(1, 0, 1)).ignoreVines().build();
    private static final TreeConfiguration VAMPIRE_TREE_RED_CONFIG = new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(ModBlocks.bloody_spruce_log.defaultBlockState()), new StraightTrunkPlacer(4, 2, 0), BlockStateProvider.simple(ModBlocks.bloody_spruce_leaves.defaultBlockState()), new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3), new TwoLayersFeatureSize(1, 0, 1)).ignoreVines().build();
    private static final LakeFeature.Configuration MOD_LAKE_CONFIG = new LakeFeature.Configuration(BlockStateProvider.simple(Blocks.WATER.defaultBlockState()), BlockStateProvider.simple(ModBlocks.castle_block_dark_stone.defaultBlockState()));

    //Configured features and their placement
    public static final Holder<ConfiguredFeature<RandomPatchConfiguration, Feature<RandomPatchConfiguration>>> vampire_flower = registerConfiguredFeature("vampire_flower", Feature.FLOWER, VAMPIRE_FLOWER_CONFIG);
    public static final Holder<PlacedFeature> vampire_flower_placed = PlacementUtils.register("vampirism:vampire_flower", vampire_flower, RarityFilter.onAverageOnceEvery(5), PlacementUtils.HEIGHTMAP, InSquarePlacement.spread(), BiomeFilter.biome());

    public static final Holder<ConfiguredFeature<TreeConfiguration, Feature<TreeConfiguration>>> vampire_tree = registerConfiguredFeature("vampire_tree", Feature.TREE, VAMPIRE_TREE_CONFIG);
    public static final Holder<PlacedFeature> vampire_tree_placed = PlacementUtils.register("vampire_tree_placed", vampire_tree, PlacementUtils.filteredByBlockSurvival((ModBlocks.vampire_spruce_sapling)));

    public static final Holder<ConfiguredFeature<TreeConfiguration, Feature<TreeConfiguration>>> vampire_tree_red = registerConfiguredFeature("vampire_tree_red", Feature.TREE, VAMPIRE_TREE_RED_CONFIG);
    public static final Holder<PlacedFeature> vampire_tree_red_placed = PlacementUtils.register("vampire_tree_red_placed", vampire_tree_red, PlacementUtils.filteredByBlockSurvival((ModBlocks.bloody_spruce_sapling)));

    public static final Holder<ConfiguredFeature<NoneFeatureConfiguration, VampireDungeonFeature>> vampire_dungeon = registerConfiguredFeature("vampire_dungeon", ModFeatures.vampire_dungeon, FeatureConfiguration.NONE);
    public static final Holder<PlacedFeature> vampire_dungeon_placed = PlacementUtils.register("vampirism:vampire_dungeon", vampire_dungeon, CountPlacement.of(3), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.top()), BiomeFilter.biome());

    public static final Holder<ConfiguredFeature<LakeFeature.Configuration, Feature<LakeFeature.Configuration>>> water_lake = registerConfiguredFeature("mod_lake", Feature.LAKE, MOD_LAKE_CONFIG);
    public static final Holder<PlacedFeature> water_lake_placed = PlacementUtils.register("vampirism:mod_lake_placed", water_lake, RarityFilter.onAverageOnceEvery(200), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());

    public static final Holder<ConfiguredFeature<RandomFeatureConfiguration, Feature<RandomFeatureConfiguration>>> vampire_trees = registerConfiguredFeature("vampire_trees_placed", Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of(new WeightedPlacedFeature(vampire_tree_red_placed, 0.3f)), vampire_tree_placed));
    public static final Holder<PlacedFeature> vampire_trees_placed = PlacementUtils.register("vampirism:vampire_trees", vampire_trees, VegetationPlacements.treePlacement(PlacementUtils.countExtra(6, 0.2F, 2)));

    public static final Holder<PlacedFeature> forest_grass_placed = PlacementUtils.register("vampirism:forest_grass", VegetationFeatures.PATCH_GRASS, VegetationPlacements.worldSurfaceSquaredWithCount(2));


    //Structure features
    public static final Holder<ConfiguredStructureFeature<?,?>> hunter_camp = registerConfiguredStructure("hunter_camp", ModFeatures.hunter_camp.configured(FeatureConfiguration.NONE, ModTags.Biomes.HAS_HUNTER_TENT));
    public static final Holder<StructureSet> hunter_camp_set = registerStructureSet(createStructureSetKey("hunter_camp"), new StructureSet(hunter_camp, new RandomSpreadStructurePlacement(10, 4, RandomSpreadType.LINEAR, 14387363)));


    //structure pieces
    public static final StructurePieceType hunter_camp_fireplace = setPieceId(HunterCampPieces.Fireplace::new, "hunter_camp_fireplace");
    public static final StructurePieceType hunter_camp_tent = setPieceId(HunterCampPieces.Tent::new, "hunter_camp_tent");
    public static final StructurePieceType hunter_camp_special = setPieceId(HunterCampPieces.SpecialBlock::new,  "hunter_camp_craftingtable");

    //structure processor
    public static final StructureProcessorType<RandomStructureProcessor> random_selector = StructureProcessorType.register(REFERENCE.MODID + ":random_selector", RandomStructureProcessor.CODEC);
    public static final StructureProcessorType<BiomeTopBlockProcessor> biome_based = StructureProcessorType.register(REFERENCE.MODID + ":biome_based", BiomeTopBlockProcessor.CODEC);



    private static ResourceKey<StructureSet> createStructureSetKey(String p_209839_) {
        return ResourceKey.create(Registry.STRUCTURE_SET_REGISTRY, new ResourceLocation(REFERENCE.MODID, p_209839_));
    }

    private static Holder<StructureSet> registerStructureSet(ResourceKey<StructureSet> p_211129_, StructureSet p_211130_) {
        return BuiltinRegistries.register(BuiltinRegistries.STRUCTURE_SETS, p_211129_, p_211130_);
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<ConfiguredFeature<FC, F>> registerConfiguredFeature(String name, F feature, FC config) {
        return BuiltinRegistries.registerExact(BuiltinRegistries.CONFIGURED_FEATURE, REFERENCE.MODID +":"+name,  new ConfiguredFeature<>(feature, config));
    }

    private static <FC extends FeatureConfiguration, F extends StructureFeature<FC>> Holder<ConfiguredStructureFeature<?, ?>> registerConfiguredStructure(String name, ConfiguredStructureFeature<FC, F> structure) {
        return BuiltinRegistries.registerExact(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, REFERENCE.MODID+":"+ name, structure);
    }

    private static StructurePieceType setPieceId(StructurePieceType.ContextlessType  p_210159_, String p_210160_) {
        return Registry.register(Registry.STRUCTURE_PIECE, new ResourceLocation(REFERENCE.MODID, p_210160_), p_210159_);
    }
}
