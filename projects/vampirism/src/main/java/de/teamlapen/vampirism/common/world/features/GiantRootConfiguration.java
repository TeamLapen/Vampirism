package de.teamlapen.vampirism.common.world.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record GiantRootConfiguration(BlockState block, int minTopRadius, int maxTopRadius, int topOffset, int maxLowestReachY, int minLowestReachY, double tipRadius, double taperExponent, double maxHorizontalOffset) implements FeatureConfiguration {

    public static final Codec<GiantRootConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockState.CODEC.fieldOf("block").forGetter(GiantRootConfiguration::block),
            Codec.INT.fieldOf("min_top_radius").forGetter(GiantRootConfiguration::minTopRadius),
            Codec.INT.fieldOf("max_top_radius").forGetter(GiantRootConfiguration::maxTopRadius),
            Codec.INT.fieldOf("top_offset").forGetter(GiantRootConfiguration::topOffset),
            Codec.INT.fieldOf("max_lowest_reach_y").forGetter(GiantRootConfiguration::maxLowestReachY),
            Codec.INT.fieldOf("min_lowest_reach_y").forGetter(GiantRootConfiguration::minLowestReachY),
            Codec.DOUBLE.fieldOf("tip_radius").forGetter(GiantRootConfiguration::tipRadius),
            Codec.DOUBLE.fieldOf("taper_exponent").forGetter(GiantRootConfiguration::taperExponent),
            Codec.DOUBLE.fieldOf("max_horizontal_offset").forGetter(GiantRootConfiguration::maxHorizontalOffset)
    ).apply(instance, GiantRootConfiguration::new));
}
