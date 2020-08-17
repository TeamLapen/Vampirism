package de.teamlapen.vampirism.world.gen.features;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModFeatures;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.MultipleRandomFeatureConfig;
import net.minecraft.world.gen.placement.*;

public class VampirismBiomeFeatures {

    //public static final CompositeFeature<VampireDungeonConfig, NoPlacementConfig> VAMPIRE_DUNGEON_FEATURE = Biome.createCompositeFeature(VAMPIRE_DUNGEON, new VampireDungeonConfig(), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);

    public static void addVampirismFlowers(Biome biomeIn) {
        biomeIn.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.FLOWER.withConfiguration(ModFeatures.vampire_flower).withPlacement(Placement.COUNT_HEIGHTMAP_32.configure(new FrequencyConfig(2)))); //defaultFlower
    }

    public static void addVampireTrees(Biome biomeIn) {
        biomeIn.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.withConfiguration(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.NORMAL_TREE.withConfiguration(ModFeatures.vampire_tree_red).func_227227_a_(0.3f)), Feature.NORMAL_TREE.withConfiguration(ModFeatures.vampire_tree).withPlacement(Placement.COUNT_EXTRA_HEIGHTMAP.configure(new AtSurfaceWithExtraConfig(5, 0.05F, 1))))));
    }

    public static void addHunterTent(Biome biomeIn) {
        biomeIn.addStructure(ModFeatures.hunter_camp.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        if (!VampirismConfig.SERVER.disableHunterTentGen.get()) {
            biomeIn.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, ModFeatures.hunter_camp.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
        }
    }

    public static void addVampireDungeon(Biome biome) {
        biome.addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, ModFeatures.vampire_dungeon.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.DUNGEONS.configure(new ChanceConfig(VampirismConfig.BALANCE.vampireDungeonWeight.get()))));
    }
}
