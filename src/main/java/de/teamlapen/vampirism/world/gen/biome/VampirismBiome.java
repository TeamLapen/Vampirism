package de.teamlapen.vampirism.world.gen.biome;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.world.gen.features.HunterCampFeature;
import de.teamlapen.vampirism.world.gen.features.VampireDungeonFeature;
import de.teamlapen.vampirism.world.gen.features.VampireForestFlowerFeature;
import de.teamlapen.vampirism.world.gen.features.config.HunterTentConfig;
import de.teamlapen.vampirism.world.gen.features.config.VampireDungeonConfig;
import net.minecraft.block.Blocks;
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
    public static final FlowersFeature VAMPIREFOREST_FLOWER_FEATURE = new VampireForestFlowerFeature();
    public static final AbstractTreeFeature<NoFeatureConfig> VAMPIRE_TREE_FEATURE = new TreeFeature(false, 4, Blocks.SPRUCE_LOG.getDefaultState(), Blocks.OAK_LEAVES.getDefaultState(), false);
    public static final Feature<HunterTentConfig> HUNTER_TENT = new HunterCampFeature();//TODO possible a structure
    public static final Feature<VampireDungeonConfig> VAMPIRE_DUNGEON = new VampireDungeonFeature();//TODO possible a structure
    public static final CompositeFeature<HunterTentConfig, FrequencyConfig> HUNTER_TENT_FEATURE = Biome.createCompositeFeature(HUNTER_TENT, new HunterTentConfig(), AT_SURFACE, new FrequencyConfig(10));
    public static final CompositeFeature<VampireDungeonConfig, NoPlacementConfig> VAMPIRE_DUNGEON_FEATURE = Biome.createCompositeFeature(VAMPIRE_DUNGEON, new VampireDungeonConfig(), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);

    protected VampirismBiome(Biome.Builder biomeBuilder) {
        super(biomeBuilder);
    }
}
