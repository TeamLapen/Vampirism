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
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.trunkplacer.StraightTrunkPlacer;

public class VampirismBiomeFeatures {

    private final static BlockClusterFeatureConfig flowerConfig = new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.vampire_orchid.getDefaultState()), new SimpleBlockPlacer()).tries(64).build();
    private final static BaseTreeFeatureConfig treeConfigSmall = new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(Blocks.SPRUCE_LOG.getDefaultState()), new SimpleBlockStateProvider(Blocks.OAK_LEAVES.getDefaultState()), new BlobFoliagePlacer(2, 0, 0, 0, 3), new StraightTrunkPlacer(4, 2, 0), new TwoLayerFeature(1, 0, 1)).func_236700_a_/*ignoreVines*/().build();
    private final static BaseTreeFeatureConfig treeConfigBig = new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(Blocks.SPRUCE_LOG.getDefaultState()), new SimpleBlockStateProvider(Blocks.OAK_LEAVES.getDefaultState()), new BlobFoliagePlacer(3, 0, 0, 0, 5), new StraightTrunkPlacer(6, 2, 0), new TwoLayerFeature(1, 0, 1)).func_236700_a_/*ignoreVines*/().build();

    public static void addVampirismFlowers(Biome biomeIn) {
        biomeIn.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.FLOWER.withConfiguration(flowerConfig).withPlacement(Placement.COUNT_HEIGHTMAP_32.configure(new FrequencyConfig(2)))); //defaultFlower
    }

    public static void addVampireTrees(Biome biomeIn) {
        biomeIn.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.withConfiguration(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.field_236291_c_/*NORMAL_TREE*/.withConfiguration(treeConfigSmall).withChance(0.2f), Feature.field_236291_c_/*NORMAL_TREE*/.withConfiguration(treeConfigBig).withChance(0.1f)), Feature.field_236291_c_/*NORMAL_TREE*/.withConfiguration(treeConfigSmall).withPlacement(Placement.COUNT_EXTRA_HEIGHTMAP.configure(new AtSurfaceWithExtraConfig(5, 0.05f, 1))))));
    }

    public static void addHunterTent(Biome biomeIn) {
        if (!VampirismConfig.SERVER.disableHunterTentGen.get()) {
            biomeIn.func_235063_a_/*addStructure*/(ModFeatures.hunter_camp.func_236391_a_/*withConfiguration*/(IFeatureConfig.NO_FEATURE_CONFIG));
        }
    }

    public static void addVampireDungeon(Biome biome) {
        biome.addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, ModFeatures.vampire_dungeon.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.DUNGEONS.configure(new ChanceConfig(VampirismConfig.BALANCE.vampireDungeonWeight.get()))));
    }
}
