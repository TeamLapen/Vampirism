package de.teamlapen.vampirism.world.gen.biome;

import de.teamlapen.vampirism.core.ModEntities;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.MultipleRandomFeatureConfig;
import net.minecraft.world.gen.feature.TallGrassConfig;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.NoiseDependant;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

public class VampireForestBiome extends VampirismBiome {
    public final static String name = "vampireForest";

    public VampireForestBiome(Biome.Builder biomeBuilder) {
        super(biomeBuilder.surfaceBuilder(SurfaceBuilder.DEFAULT, VAMPIRE_SURFACE).category(Category.FOREST).depth(0.1F).scale(0.025F).waterColor(0xEE2505).waterFogColor(0xEE2505).precipitation(RainType.NONE).parent(null).downfall(0).temperature(0.3f));
        //TODO extra count and chance & NO_FEATURE_CONFIG?
        this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, createCompositeFeature(Feature.RANDOM_FEATURE_LIST, new MultipleRandomFeatureConfig(new Feature[]{VAMPIRE_TREE_FEATURE}, new IFeatureConfig[]{IFeatureConfig.NO_FEATURE_CONFIG}, new float[]{0.33333334F}, VAMPIRE_TREE_FEATURE, IFeatureConfig.NO_FEATURE_CONFIG), AT_SURFACE_WITH_EXTRA, new AtSurfaceWithExtraConfig(5, 0.05F, 1)));
        //TODO check if enough flowers are generated
        this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, createCompositeFlowerFeature(VAMPIREFOREST_FLOWER_FEATURE, SURFACE_PLUS_32_WITH_NOISE, new NoiseDependant(-0.8D, 15, 4)));
        this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, createCompositeFeature(Feature.TALL_GRASS, new TallGrassConfig(Blocks.GRASS.getDefaultState()), TWICE_SURFACE, new FrequencyConfig(4)));
        //TODO NO_FEATURE_CONFIG & TWICE_SURFACE
        this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, createCompositeFeature(Feature.DEAD_BUSH, IFeatureConfig.NO_FEATURE_CONFIG, TWICE_SURFACE, new FrequencyConfig(3)));
        this.addSpawn(EntityClassification.MONSTER, new SpawnListEntry(ModEntities.ghost, 3, 1, 1));
        this.addSpawn(EntityClassification.MONSTER, new SpawnListEntry(ModEntities.vampire, 7, 1, 3));
        this.addSpawn(EntityClassification.MONSTER, new SpawnListEntry(ModEntities.vampire_baron, 2, 1, 1));
        this.addSpawn(EntityClassification.AMBIENT, new SpawnListEntry(ModEntities.blinding_bat, 8, 2, 4));
        this.addSpawn(EntityClassification.CREATURE, new SpawnListEntry(ModEntities.dummy_creature, 15, 3, 6));
    }

    @Override
    public int getFoliageColor(BlockPos pos) {
        return 0x1E1F1F;
    }


    @Override
    public int getGrassColor(BlockPos pos) {
        // 0x7A317A; dark purple
        return 0x1E1F1F;
    }

    @Override
    public int getSkyColorByTemp(float p_76731_1_) {
        return 0xA33641;
    }
}
