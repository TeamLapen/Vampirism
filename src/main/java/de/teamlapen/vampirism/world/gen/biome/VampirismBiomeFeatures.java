package de.teamlapen.vampirism.world.gen.biome;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.world.gen.features.HunterCampPieces;
import de.teamlapen.vampirism.world.gen.features.HunterCampStructure;
import de.teamlapen.vampirism.world.gen.features.VampireDungeonFeature;
import de.teamlapen.vampirism.world.gen.features.VampireForestFlowerFeature;
import de.teamlapen.vampirism.world.gen.features.config.HunterTentConfig;
import de.teamlapen.vampirism.world.gen.features.config.VampireDungeonConfig;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.placement.*;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class VampirismBiomeFeatures {
    public static final SurfaceBuilderConfig VAMPIRE_SURFACE = new SurfaceBuilderConfig(ModBlocks.cursed_earth.getDefaultState(), ModBlocks.cursed_earth.getDefaultState(), ModBlocks.cursed_earth.getDefaultState());

    public static final Feature<HunterTentConfig> HUNTER_TENT = new HunterCampStructure();//TODO possible a structure
    public static final Feature<VampireDungeonConfig> VAMPIRE_DUNGEON = new VampireDungeonFeature();//TODO possible a structure
    public static final CompositeFeature<HunterTentConfig, FrequencyConfig> HUNTER_TENT_FEATURE = Biome.createCompositeFeature(HUNTER_TENT, new HunterTentConfig(), AT_SURFACE, new FrequencyConfig(10));
    public static final CompositeFeature<VampireDungeonConfig, NoPlacementConfig> VAMPIRE_DUNGEON_FEATURE = Biome.createCompositeFeature(VAMPIRE_DUNGEON, new VampireDungeonConfig(), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);

    public static void addVampirismFlowers(Biome biomeIn) {
        biomeIn.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Features.VAMPIRE_FLOWER, IFeatureConfig.NO_FEATURE_CONFIG, Placement.COUNT_HEIGHTMAP_32, new FrequencyConfig(2)));
    }

    public static void addVampireTrees(Biome biomeIn) {
        biomeIn.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.RANDOM_SELECTOR, new MultipleRandomFeatureConfig(new Feature[]{Features.VAMPIRE_TREE}, new IFeatureConfig[]{IFeatureConfig.NO_FEATURE_CONFIG, IFeatureConfig.NO_FEATURE_CONFIG}, new float[]{0.2F, 0.1F}, Features.VAMPIRE_TREE, IFeatureConfig.NO_FEATURE_CONFIG), Placement.COUNT_EXTRA_HEIGHTMAP, new AtSurfaceWithExtraConfig(5, 0.05F, 1)));
    }

    public static class Features {
        public static final FlowersFeature VAMPIRE_FLOWER = new VampireForestFlowerFeature();
        public static final AbstractTreeFeature<NoFeatureConfig> VAMPIRE_TREE = new TreeFeature(NoFeatureConfig::deserialize, false, 4, Blocks.SPRUCE_LOG.getDefaultState(), Blocks.OAK_LEAVES.getDefaultState(), false);
    }

    public static class StructurePieceTypes {
        public static final IStructurePieceType HUNTERCAMP = IStructurePieceType.register(HunterCampPieces.Piece::new, REFERENCE.MODID + ":huntercamp");
    }

}
