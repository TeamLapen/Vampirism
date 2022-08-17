package de.teamlapen.vampirism.world.gen;

import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VampireSpruceTree extends AbstractTreeGrower {

    @Nullable
    @Override
    protected Holder<ConfiguredFeature<TreeConfiguration, Feature<TreeConfiguration>>> getConfiguredFeature(@NotNull RandomSource randomIn, boolean largeHive) {
        return VampirismFeatures.DARK_SPRUCE_TREE.getHolder().orElseThrow();
    }
}
