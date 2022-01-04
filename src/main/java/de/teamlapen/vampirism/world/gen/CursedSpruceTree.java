package de.teamlapen.vampirism.world.gen;

import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CursedSpruceTree extends AbstractTreeGrower {

    @Nullable
    @Override
    protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(@Nonnull RandomSource randomIn, boolean largeHive) {
        return VampirismFeatures.CURSED_SPRUCE_TREE.getHolder().orElseThrow();
    }
}
