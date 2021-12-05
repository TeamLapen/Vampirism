package de.teamlapen.vampirism.world.gen;

import de.teamlapen.vampirism.world.biome.VampirismBiomeFeatures;
import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class CursedSpruceTree extends Tree {

    @Nullable
    @Override
    protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getConfiguredFeature(@Nonnull Random randomIn, boolean largeHive) {
        return VampirismBiomeFeatures.cursed_spruce_tree;
    }
}
