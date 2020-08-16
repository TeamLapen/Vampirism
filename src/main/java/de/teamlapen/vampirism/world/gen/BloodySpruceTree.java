package de.teamlapen.vampirism.world.gen;

import de.teamlapen.vampirism.core.ModFeatures;
import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BloodySpruceTree extends Tree {
    @Nullable
    @Override
    protected AbstractTreeFeature<NoFeatureConfig> getTreeFeature(@Nonnull Random random) {
        return random.nextInt(10) < 7 ? ModFeatures.vampire_tree : ModFeatures.vampire_tree_red;
    }
}
