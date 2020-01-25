package de.teamlapen.vampirism.world.gen.features;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFeatures;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.IPlantable;

public class VampirismBiomeFeatures {

    private final static BlockClusterFeatureConfig flowerConfig = new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.vampire_orchid.getDefaultState()), new SimpleBlockPlacer()).func_227315_a_(64).func_227322_d_();
    private final static TreeFeatureConfig treeConfigSmall = new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(Blocks.SPRUCE_LOG.getDefaultState()), new SimpleBlockStateProvider(Blocks.SPRUCE_LEAVES.getDefaultState()), new BlobFoliagePlacer(2, 0)).baseHeight(4).func_227354_b_(2).func_227360_i_(3).func_227352_a_().setSapling((IPlantable) Blocks.SPRUCE_SAPLING).build();
    private final static TreeFeatureConfig treeConfigBig = new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(Blocks.SPRUCE_LOG.getDefaultState()), new SimpleBlockStateProvider(Blocks.SPRUCE_LEAVES.getDefaultState()), new BlobFoliagePlacer(3, 0)).baseHeight(6).func_227354_b_(2).func_227360_i_(5).func_227352_a_().setSapling((IPlantable) Blocks.SPRUCE_SAPLING).build();

    //public static final CompositeFeature<VampireDungeonConfig, NoPlacementConfig> VAMPIRE_DUNGEON_FEATURE = Biome.createCompositeFeature(VAMPIRE_DUNGEON, new VampireDungeonConfig(), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);

    public static void addVampirismFlowers(Biome biomeIn) {
        biomeIn.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.field_227247_y_.withConfiguration(flowerConfig).func_227228_a_(Placement.COUNT_HEIGHTMAP_32.func_227446_a_(new FrequencyConfig(2)))); //defaultFlower
    }

    public static void addVampireTrees(Biome biomeIn) {
        biomeIn.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.withConfiguration(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.NORMAL_TREE.withConfiguration(treeConfigSmall).func_227227_a_(0.2f), Feature.NORMAL_TREE.withConfiguration(treeConfigBig).func_227227_a_(0.1f)), Feature.NORMAL_TREE.withConfiguration(treeConfigSmall).func_227228_a_(Placement.COUNT_EXTRA_HEIGHTMAP.func_227446_a_(new AtSurfaceWithExtraConfig(5, 0.05f, 1))))));
    }

    public static void addHunterTent(Biome biomeIn) {
        biomeIn.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, ModFeatures.hunter_camp.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        biomeIn.addStructure(ModFeatures.hunter_camp.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        if (!VampirismConfig.SERVER.disableHunterTentGen.get()) {
            biomeIn.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, ModFeatures.hunter_camp.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).func_227228_a_(Placement.NOPE.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG)));
        }
    }
}
