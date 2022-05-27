package de.teamlapen.vampirism.world.gen;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BloodySpruceTree extends AbstractTreeGrower {

    @Nullable
    @Override
    protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(@Nonnull Random randomIn, boolean largeHive) {
        return VampirismFeatures.VAMPIRE_TREE_RED.getHolder().orElseThrow();
    }
}
