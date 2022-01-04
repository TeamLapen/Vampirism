package de.teamlapen.vampirism.world.gen;

import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VampireSpruceTree extends AbstractTreeGrower {

    @Nullable
    @Override
    protected Holder<ConfiguredFeature<TreeConfiguration, Feature<TreeConfiguration>>> getConfiguredFeature(@Nonnull RandomSource randomIn, boolean largeHive) {
        return VampirismFeatures.DARK_SPRUCE_TREE.getHolder().orElseThrow();
    }
}
