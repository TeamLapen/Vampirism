package de.teamlapen.vampirism.biome;

import de.teamlapen.vampirism.biome.config.HunterTentConfig;
import de.teamlapen.vampirism.biome.config.VampireDungeonConfig;
import de.teamlapen.vampirism.biome.features.FeatureHunterCamp;
import de.teamlapen.vampirism.biome.features.FeatureVampireDungeon;
import de.teamlapen.vampirism.biome.features.FeatureVampireForestFlower;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

/**
 * base class fro storing all {@link Feature}
 */
public abstract class VampirismBiome extends Biome {
    public static boolean debug = false;

    public static final SurfaceBuilderConfig VAMPIRE_SURFACE = new SurfaceBuilderConfig(ModBlocks.cursed_earth.getDefaultState(), ModBlocks.cursed_earth.getDefaultState(), ModBlocks.cursed_earth.getDefaultState());
    public static final AbstractFlowersFeature VAMPIREFOREST_FLOWER_FEATURE = new FeatureVampireForestFlower();
    public static final AbstractTreeFeature<NoFeatureConfig> VAMPIRE_TREE_FEATURE = new TreeFeature(false, 4, Blocks.SPRUCE_LOG.getDefaultState(), Blocks.OAK_LEAVES.getDefaultState(), false);
    public static final Feature<HunterTentConfig> HUNTER_TENT = new FeatureHunterCamp();//TODO possible a structure
    public static final Feature<VampireDungeonConfig> VAMPIRE_DUNGEON = new FeatureVampireDungeon<>();//TODO possible a structure
    public static final CompositeFeature<HunterTentConfig, FrequencyConfig> HUNTER_TENT_FEATURE = Biome.createCompositeFeature(HUNTER_TENT, new HunterTentConfig(), AT_SURFACE, new FrequencyConfig(10));
    public static final CompositeFeature<VampireDungeonConfig, NoPlacementConfig> VAMPIRE_DUNGEON_FEATURE = Biome.createCompositeFeature(VAMPIRE_DUNGEON, new VampireDungeonConfig(), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);

    public VampirismBiome(BiomeBuilder biomeBuilder) {
        super(biomeBuilder);
    }
}
