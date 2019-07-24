package de.teamlapen.vampirism.world.gen.biome;

import de.teamlapen.vampirism.core.ModWorldFeatures;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.MultipleRandomFeatureConfig;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.Placement;

public class VampirismBiomeFeatures {

    //public static final CompositeFeature<HunterTentConfig, FrequencyConfig> HUNTER_TENT_FEATURE = Biome.createCompositeFeature(HUNTER_TENT, new HunterTentConfig(), AT_SURFACE, new FrequencyConfig(10));
    //public static final CompositeFeature<VampireDungeonConfig, NoPlacementConfig> VAMPIRE_DUNGEON_FEATURE = Biome.createCompositeFeature(VAMPIRE_DUNGEON, new VampireDungeonConfig(), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);

    public static void addVampirismFlowers(Biome biomeIn) {
        biomeIn.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(ModWorldFeatures.vampire_flower, IFeatureConfig.NO_FEATURE_CONFIG, Placement.COUNT_HEIGHTMAP_32, new FrequencyConfig(2)));
    }

    public static void addVampireTrees(Biome biomeIn) {
        biomeIn.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.RANDOM_SELECTOR, new MultipleRandomFeatureConfig(new Feature[]{ModWorldFeatures.vampire_tree}, new IFeatureConfig[]{IFeatureConfig.NO_FEATURE_CONFIG, IFeatureConfig.NO_FEATURE_CONFIG}, new float[]{0.2F, 0.1F}, ModWorldFeatures.vampire_tree, IFeatureConfig.NO_FEATURE_CONFIG), Placement.COUNT_EXTRA_HEIGHTMAP, new AtSurfaceWithExtraConfig(5, 0.05F, 1)));
    }

    public static void addHunterTent(Biome biomeIn) {
        //biomeIn.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES,);//TODO 1.14 world add huntertent structure also make sure generation of this can be deactivated. Maybe with a FrequencyConfig. Not sure how that works
    }
}
