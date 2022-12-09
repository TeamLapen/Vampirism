package de.teamlapen.vampirism.world.gen;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CursedSpruceTree extends AbstractTreeGrower {

    @Nullable
    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(@NotNull RandomSource randomIn, boolean largeHive) {
        return (ResourceKey<ConfiguredFeature<?, ?>>) (Object)VampirismFeatures.CURSED_SPRUCE_TREE.getKey();
    }
}
