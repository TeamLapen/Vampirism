package de.teamlapen.vampirism.world.gen.features;

import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModFeatures;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.MultipleRandomFeatureConfig;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

public class VampirismBiomeFeatures {

    //public static final CompositeFeature<VampireDungeonConfig, NoPlacementConfig> VAMPIRE_DUNGEON_FEATURE = Biome.createCompositeFeature(VAMPIRE_DUNGEON, new VampireDungeonConfig(), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);

    public static void addVampirismFlowers(Biome biomeIn) {
        biomeIn.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(ModFeatures.vampire_flower, IFeatureConfig.NO_FEATURE_CONFIG, Placement.COUNT_HEIGHTMAP_32, new FrequencyConfig(2)));
    }

    public static void addVampireTrees(Biome biomeIn) {
        biomeIn.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.RANDOM_SELECTOR, new MultipleRandomFeatureConfig(new Feature[]{ModFeatures.vampire_tree}, new IFeatureConfig[]{IFeatureConfig.NO_FEATURE_CONFIG, IFeatureConfig.NO_FEATURE_CONFIG}, new float[]{0.2F, 0.1F}, ModFeatures.vampire_tree, IFeatureConfig.NO_FEATURE_CONFIG), Placement.COUNT_EXTRA_HEIGHTMAP, new AtSurfaceWithExtraConfig(5, 0.05F, 1)));
    }

    public static void addHunterTent(Biome biomeIn) {
        biomeIn.addStructure(ModFeatures.hunter_camp, IFeatureConfig.NO_FEATURE_CONFIG);
        if (!VampirismConfig.SERVER.disableHunterTentGen.get()) {
            biomeIn.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, Biome.createDecoratedFeature(ModFeatures.hunter_camp, IFeatureConfig.NO_FEATURE_CONFIG, Placement.NOPE, IPlacementConfig.NO_PLACEMENT_CONFIG));
        }
    }
}
