package de.teamlapen.vampirism.world.gen;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class VampireSpruceTree extends AbstractTreeGrower {

    @Nullable
    @Override
    protected Holder<ConfiguredFeature<TreeConfiguration, Feature<TreeConfiguration>>> getConfiguredFeature(@Nonnull Random randomIn, boolean largeHive) {
        return VampirismFeatures.VAMPIRE_TREE.getHolder().orElseThrow();
    }
}
