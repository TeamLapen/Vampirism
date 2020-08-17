package de.teamlapen.vampirism.world.gen;

import de.teamlapen.vampirism.core.ModFeatures;
import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

import javax.annotation.Nullable;
import java.util.Random;

public class BloodySpruceTree extends Tree {
    @Nullable
    @Override
    protected ConfiguredFeature<TreeFeatureConfig, ?> getTreeFeature(Random random, boolean p_225546_2_) {
        return random.nextInt(10) < 7 ? Feature.NORMAL_TREE.withConfiguration(ModFeatures.vampire_tree) : Feature.NORMAL_TREE.withConfiguration(ModFeatures.vampire_tree_red);
    }
}
