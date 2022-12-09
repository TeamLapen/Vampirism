package de.teamlapen.vampirism.world.gen;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFeatures;
import de.teamlapen.vampirism.mixin.VanillaRegistriesAccessor;
import de.teamlapen.vampirism.world.gen.feature.VampireDungeonFeature;
import de.teamlapen.vampirism.world.gen.feature.treedecorators.TrunkCursedVineDecorator;
import de.teamlapen.vampirism.world.gen.structure.huntercamp.HunterCampPieces;
import de.teamlapen.vampirism.world.gen.structure.templatesystem.BiomeTopBlockProcessor;
import de.teamlapen.vampirism.world.gen.structure.templatesystem.RandomStructureProcessor;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
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
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.OptionalInt;

import static net.minecraft.data.worldgen.placement.OrePlacements.commonOrePlacement;
import static net.minecraft.data.worldgen.placement.OrePlacements.rareOrePlacement;

public class VampirismFeatures {

    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Registries.PLACED_FEATURE, REFERENCE.MODID);
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(Registries.CONFIGURED_FEATURE, REFERENCE.MODID);
    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECES = DeferredRegister.create(Registries.STRUCTURE_PIECE, REFERENCE.MODID);
    public static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSOR_TYPES = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, REFERENCE.MODID);

    public static void register(IEventBus ctx) {
        PLACED_FEATURES.register(ctx);
        CONFIGURED_FEATURES.register(ctx);
        STRUCTURE_PIECES.register(ctx);
        STRUCTURE_PROCESSOR_TYPES.register(ctx);
    }

    //Configured features and their placement
    public static final RegistryObject<ConfiguredFeature<RandomPatchConfiguration, Feature<RandomPatchConfiguration>>> VAMPIRE_FLOWER = CONFIGURED_FEATURES.register("vampire_flower", () -> new ConfiguredFeature<>(Feature.FLOWER, FeatureUtils.simpleRandomPatchConfiguration(32, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.VAMPIRE_ORCHID.get()))))));
    public static final RegistryObject<ConfiguredFeature<RandomPatchConfiguration, Feature<RandomPatchConfiguration>>> CURSED_ROOT = CONFIGURED_FEATURES.register("cursed_root", () -> new ConfiguredFeature<>(Feature.RANDOM_PATCH, FeatureUtils.simpleRandomPatchConfiguration(16, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.CURSED_ROOTS.get()))))));
    public static final RegistryObject<PlacedFeature> VAMPIRE_FLOWER_PLACED = PLACED_FEATURES.register("vampire_flower", () -> new PlacedFeature(getHolder(VAMPIRE_FLOWER), List.of(RarityFilter.onAverageOnceEvery(4), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, InSquarePlacement.spread(), BiomeFilter.biome())));
    public static final RegistryObject<PlacedFeature> CURSED_ROOT_PLACED = PLACED_FEATURES.register("cursed_root", () -> new PlacedFeature(getHolder(CURSED_ROOT), List.of(RarityFilter.onAverageOnceEvery(1), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome())));

    public static final RegistryObject<ConfiguredFeature<TreeConfiguration, Feature<TreeConfiguration>>> DARK_SPRUCE_TREE = CONFIGURED_FEATURES.register("dark_spruce_tree", () -> new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(ModBlocks.DARK_SPRUCE_LOG.get()), new StraightTrunkPlacer(11, 2, 2), BlockStateProvider.simple(ModBlocks.DARK_SPRUCE_LEAVES.get().defaultBlockState()), new SpruceFoliagePlacer(UniformInt.of(2,3), UniformInt.of(0, 2), UniformInt.of(3, 7)), new ThreeLayersFeatureSize(5, 8, 0, 3, 3, OptionalInt.of(5))).ignoreVines().build()));
    public static final RegistryObject<PlacedFeature> DARK_SPRUCE_TREE_PLACED = PLACED_FEATURES.register("dark_spruce_tree", () -> new PlacedFeature(getHolder(DARK_SPRUCE_TREE), List.of(PlacementUtils.filteredByBlockSurvival((ModBlocks.DARK_SPRUCE_SAPLING.get())))));

    public static final RegistryObject<ConfiguredFeature<TreeConfiguration, Feature<TreeConfiguration>>> CURSED_SPRUCE_TREE = CONFIGURED_FEATURES.register("cursed_tree_red", () -> new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(ModBlocks.CURSED_SPRUCE_LOG.get()), new StraightTrunkPlacer(11, 2, 2), BlockStateProvider.simple(ModBlocks.DARK_SPRUCE_LEAVES.get().defaultBlockState()), new SpruceFoliagePlacer(UniformInt.of(2,3), UniformInt.of(0, 2), UniformInt.of(3, 7)), new ThreeLayersFeatureSize(5, 8, 0, 3, 3, OptionalInt.of(5))).decorators(ImmutableList.of(TrunkCursedVineDecorator.INSTANCE)).ignoreVines().build()));
    public static final RegistryObject<PlacedFeature> CURSED_SPRUCE_TREE_PLACED = PLACED_FEATURES.register("cursed_spruce_tree_placed", () -> new PlacedFeature(getHolder(CURSED_SPRUCE_TREE), List.of(PlacementUtils.filteredByBlockSurvival((ModBlocks.CURSED_SPRUCE_SAPLING.get())))));


    public static final RegistryObject<ConfiguredFeature<NoneFeatureConfiguration, VampireDungeonFeature>> VAMPIRE_DUNGEON = CONFIGURED_FEATURES.register("vampire_dungeon", () -> new ConfiguredFeature<>(ModFeatures.VAMPIRE_DUNGEON.get(), FeatureConfiguration.NONE));
    public static final RegistryObject<PlacedFeature> VAMPIRE_DUNGEON_PLACED = PLACED_FEATURES.register("vampire_dungeon", () -> new PlacedFeature(getHolder(VAMPIRE_DUNGEON), List.of(CountPlacement.of(3), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.top()), BiomeFilter.biome())));

    public static final RegistryObject<ConfiguredFeature<LakeFeature.Configuration, Feature<LakeFeature.Configuration>>> WATER_LAKE = CONFIGURED_FEATURES.register("mod_lake", () -> new ConfiguredFeature<>(Feature.LAKE, new LakeFeature.Configuration(BlockStateProvider.simple(Blocks.WATER), BlockStateProvider.simple(ModBlocks.CASTLE_BLOCK_DARK_STONE.get()))));
    public static final RegistryObject<PlacedFeature> WATER_LAKE_PLACED = PLACED_FEATURES.register("mod_lake_placed", () -> new PlacedFeature(getHolder(WATER_LAKE), List.of(RarityFilter.onAverageOnceEvery(200), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome())));


    public static final RegistryObject<ConfiguredFeature<RandomFeatureConfiguration, Feature<RandomFeatureConfiguration>>> VAMPIRE_TREES = CONFIGURED_FEATURES.register("vampire_trees_placed", () -> new ConfiguredFeature<>(Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of(new WeightedPlacedFeature(getHolder(CURSED_SPRUCE_TREE_PLACED), 0.3f)), getHolder(DARK_SPRUCE_TREE_PLACED))));
    public static final RegistryObject<PlacedFeature> VAMPIRE_TREES_PLACED = PLACED_FEATURES.register("vampire_trees", () -> new PlacedFeature(getHolder(VAMPIRE_TREES), VegetationPlacements.treePlacement(PlacementUtils.countExtra(10, 0.1f, 1))));

    public static final RegistryObject<PlacedFeature> FOREST_GRASS_PLACED = PLACED_FEATURES.register("forest_grass", () -> {
        var holder = VanillaRegistries.createLookup().lookupOrThrow(Registries.CONFIGURED_FEATURE).getOrThrow(VegetationFeatures.PATCH_GRASS);
        return new PlacedFeature(holder, VegetationPlacements.worldSurfaceSquaredWithCount(2));
    });

    public static final RegistryObject<ConfiguredFeature<?, ?>> ORE_DARK_STONE = CONFIGURED_FEATURES.register("ore_dark_stone", () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(new TagMatchTest(BlockTags.BASE_STONE_OVERWORLD), ModBlocks.CASTLE_BLOCK_DARK_STONE.get().defaultBlockState(), 64)));
    public static final RegistryObject<PlacedFeature> ORE_DARK_STONE_UPPER_PLACED = PLACED_FEATURES.register("ore_dark_stone_upper", () -> new PlacedFeature(getHolder(ORE_DARK_STONE), rareOrePlacement(6, HeightRangePlacement.uniform(VerticalAnchor.absolute(64), VerticalAnchor.absolute(128)))));
    public static final RegistryObject<PlacedFeature> ORE_DARK_STONE_LOWER_PLACED = PLACED_FEATURES.register("ore_dark_stone_lower", () -> new PlacedFeature(getHolder(ORE_DARK_STONE), commonOrePlacement(2, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(60)))));

    public static final RegistryObject<ConfiguredFeature<?, ?>> ORE_CURSED_DIRT = CONFIGURED_FEATURES.register("ore_cursed_dirt", () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(new TagMatchTest(BlockTags.BASE_STONE_OVERWORLD), ModBlocks.CURSED_EARTH.get().defaultBlockState(), 33)));
    public static final RegistryObject<PlacedFeature> ORE_CURSED_DIRT_PLACED = PLACED_FEATURES.register("ore_cursed_dirt", () -> new PlacedFeature(getHolder(ORE_CURSED_DIRT), commonOrePlacement(7, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(160)))));

    //structure pieces
    public static final RegistryObject<StructurePieceType> HUNTER_CAMP_FIREPLACE = STRUCTURE_PIECES.register("hunter_camp_fireplace", () -> (StructurePieceType.ContextlessType) HunterCampPieces.Fireplace::new);
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
    private static <T> Holder<T> getHolder(@NotNull RegistryObject<? extends T> object) {
        return (Holder<T>) object.getHolder().orElseThrow(() -> new IllegalStateException("Registry object " + object.getKey() + " does not have a holder. Something is wrong"));
    }

}
