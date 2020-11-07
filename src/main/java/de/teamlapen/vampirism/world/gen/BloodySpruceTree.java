package de.teamlapen.vampirism.world.gen;

import de.teamlapen.vampirism.world.biome.VampirismBiomeFeatures;
import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;

import javax.annotation.Nullable;
import java.util.Random;

public class BloodySpruceTree extends Tree {

    @Nullable
    @Override
    protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getTreeFeature(Random randomIn, boolean largeHive) {
        return randomIn.nextInt(10) < 7 ? VampirismBiomeFeatures.vampire_tree : VampirismBiomeFeatures.vampire_tree_red;
    }
}
