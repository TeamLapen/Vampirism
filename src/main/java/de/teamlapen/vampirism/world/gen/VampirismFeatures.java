package de.teamlapen.vampirism.world.gen;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFeatures;
import de.teamlapen.vampirism.world.gen.features.VampireDungeonFeature;
import de.teamlapen.vampirism.world.gen.structures.huntercamp.HunterCampPieces;
import de.teamlapen.vampirism.world.gen.util.BiomeTopBlockProcessor;
import de.teamlapen.vampirism.world.gen.util.RandomStructureProcessor;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class VampirismFeatures {

    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, REFERENCE.MODID);
    public static final DeferredRegister<ConfiguredFeature<?,?>> CONFIGURED_FEATURES = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, REFERENCE.MODID);
    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECES = DeferredRegister.create(Registry.STRUCTURE_PIECE_REGISTRY, REFERENCE.MODID);
    public static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSOR_TYPES = DeferredRegister.create(Registry.STRUCTURE_PROCESSOR_REGISTRY, REFERENCE.MODID);

    public static void register(IEventBus ctx){
        PLACED_FEATURES.register(ctx);
        CONFIGURED_FEATURES.register(ctx);
        STRUCTURE_PIECES.register(ctx);
        STRUCTURE_PROCESSOR_TYPES.register(ctx);
//        CONFIGURED_STRUCTURE_FEATURES.register(ctx);
    }

    //Configured features and their placement
    public static final RegistryObject<ConfiguredFeature<RandomPatchConfiguration, Feature<RandomPatchConfiguration>>>  VAMPIRE_FLOWER = CONFIGURED_FEATURES.register("vampire_flower", () -> new ConfiguredFeature<>( Feature.FLOWER, FeatureUtils.simpleRandomPatchConfiguration(64,  PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(ModBlocks.VAMPIRE_ORCHID.get().defaultBlockState(), 2).add(ModBlocks.VAMPIRE_ORCHID.get().defaultBlockState(), 1)))))));
    public static final RegistryObject<PlacedFeature> VAMPIRE_FLOWER_PLACED = PLACED_FEATURES.register("vampire_flower", () -> new PlacedFeature(getHolder(VAMPIRE_FLOWER), List.of(RarityFilter.onAverageOnceEvery(5), PlacementUtils.HEIGHTMAP, InSquarePlacement.spread(), BiomeFilter.biome())));

    public static final RegistryObject<ConfiguredFeature<TreeConfiguration, Feature<TreeConfiguration>>>  VAMPIRE_TREE = CONFIGURED_FEATURES.register("vampire_tree", () -> new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.SPRUCE_LOG.defaultBlockState()), new StraightTrunkPlacer(4, 2, 0), BlockStateProvider.simple(ModBlocks.VAMPIRE_SPRUCE_LEAVES.get().defaultBlockState()), new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3), new TwoLayersFeatureSize(1, 0, 1)).ignoreVines().build()));
    public static final RegistryObject<PlacedFeature> VAMPIRE_TREE_PLACED = PLACED_FEATURES.register("vampire_tree_placed", () -> new PlacedFeature(getHolder(VAMPIRE_TREE), List.of(PlacementUtils.filteredByBlockSurvival((ModBlocks.VAMPIRE_SPRUCE_SAPLING.get())))));

    public static final RegistryObject<ConfiguredFeature<TreeConfiguration, Feature<TreeConfiguration>>> VAMPIRE_TREE_RED =  CONFIGURED_FEATURES.register("vampire_tree_red", () -> new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(ModBlocks.BLOODY_SPRUCE_LOG.get().defaultBlockState()), new StraightTrunkPlacer(4, 2, 0), BlockStateProvider.simple(ModBlocks.BLOODY_SPRUCE_LEAVES.get().defaultBlockState()), new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3), new TwoLayersFeatureSize(1, 0, 1)).ignoreVines().build()));
    public static final RegistryObject<PlacedFeature> VAMPIRE_TREE_RED_PLACED = PLACED_FEATURES.register("vampire_tree_red_placed", () -> new PlacedFeature(getHolder(VAMPIRE_TREE_RED), List.of(PlacementUtils.filteredByBlockSurvival((ModBlocks.BLOODY_SPRUCE_SAPLING.get())))));


    public static final RegistryObject<ConfiguredFeature<NoneFeatureConfiguration, VampireDungeonFeature>> VAMPIRE_DUNGEON = CONFIGURED_FEATURES.register("vampire_dungeon", () -> new ConfiguredFeature<>(ModFeatures.VAMPIRE_DUNGEON.get(), FeatureConfiguration.NONE));
    public static final RegistryObject<PlacedFeature> VAMPIRE_DUNGEON_PLACED = PLACED_FEATURES.register("vampire_dungeon", () -> new PlacedFeature(getHolder(VAMPIRE_DUNGEON), List.of(CountPlacement.of(3), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.top()), BiomeFilter.biome())));

    public static final RegistryObject<ConfiguredFeature<LakeFeature.Configuration, Feature<LakeFeature.Configuration>>> WATER_LAKE = CONFIGURED_FEATURES.register("mod_lake", () -> new ConfiguredFeature<>(Feature.LAKE, new LakeFeature.Configuration(BlockStateProvider.simple(Blocks.WATER.defaultBlockState()), BlockStateProvider.simple(ModBlocks.CASTLE_BLOCK_DARK_STONE.get().defaultBlockState()))));
    public static final RegistryObject<PlacedFeature> WATER_LAKE_PLACED =PLACED_FEATURES.register("mod_lake_placed", () -> new PlacedFeature(getHolder(WATER_LAKE), List.of(RarityFilter.onAverageOnceEvery(200), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome())));


    public static final RegistryObject<ConfiguredFeature<RandomFeatureConfiguration, Feature<RandomFeatureConfiguration>>> VAMPIRE_TREES = CONFIGURED_FEATURES.register("vampire_trees_placed", () -> new ConfiguredFeature<>(Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of(new WeightedPlacedFeature(getHolder(VAMPIRE_TREE_RED_PLACED), 0.3f)), getHolder(VAMPIRE_TREE_PLACED))));
    public static final RegistryObject<PlacedFeature> VAMPIRE_TREES_PLACED = PLACED_FEATURES.register("vampire_trees", () -> new PlacedFeature(getHolder(VAMPIRE_TREES), VegetationPlacements.treePlacement(PlacementUtils.countExtra(6, 0.2F, 2))));

    public static final RegistryObject<PlacedFeature> FOREST_GRASS_PLACED = PLACED_FEATURES.register("forest_grass", () -> new PlacedFeature(Holder.hackyErase(VegetationFeatures.PATCH_GRASS), VegetationPlacements.worldSurfaceSquaredWithCount(2)));


    //Structure features
//    public static final RegistryObject<ConfiguredStructureFeature<?,?>> HUNTER_CAMP = CONFIGURED_STRUCTURE_FEATURES.register("hunter_camp", () -> ModFeatures.HUNTER_CAMP_KEY.get().configured(FeatureConfiguration.NONE, ModTags.Biomes.HAS_HUNTER_TENT));
    //Hunter camp structure set is added in data pack
    //public static final Holder<StructureSet> hunter_camp_set = registerStructureSet(createStructureSetKey("hunter_camp"), new StructureSet(hunter_camp, new RandomSpreadStructurePlacement(10, 4, RandomSpreadType.LINEAR, 14387363)));


    //structure pieces
    public static final RegistryObject<StructurePieceType> HUNTER_CAMP_FIREPLACE = STRUCTURE_PIECES.register("hunter_camp_fireplace", () -> (StructurePieceType.ContextlessType)  HunterCampPieces.Fireplace::new);
    public static final RegistryObject<StructurePieceType> HUNTER_CAMP_TENT = STRUCTURE_PIECES.register("hunter_camp_tent", () -> (StructurePieceType.ContextlessType) HunterCampPieces.Tent::new);
    public static final RegistryObject<StructurePieceType> HUNTER_CAMP_SPECIAL = STRUCTURE_PIECES.register("hunter_camp_craftingtable", () -> (StructurePieceType.ContextlessType) HunterCampPieces.SpecialBlock::new);

    //structure processor
    public static final RegistryObject<StructureProcessorType<RandomStructureProcessor>> RANDOM_SELECTOR = STRUCTURE_PROCESSOR_TYPES.register("random_selector", () -> () -> RandomStructureProcessor.CODEC);
    public static final RegistryObject<StructureProcessorType<BiomeTopBlockProcessor>> BIOME_BASED = STRUCTURE_PROCESSOR_TYPES.register("biome_based", () -> () -> BiomeTopBlockProcessor.CODEC);


    /**
     * Get the holder for the given registry object and strip it of generic
     * MUST only be used for objects that belong to registries that are guaranteed to be present
     */
    @SuppressWarnings({"unchecked"})
    private static <T> Holder<T> getHolder(RegistryObject<? extends T> object) {
        return (Holder<T>)object.getHolder().orElseThrow(() -> new IllegalStateException("Registry object "+object.getKey()+" does not have a holder. Something is wrong"));
    }

}
