package de.teamlapen.vampirism.world.gen;

import de.teamlapen.vampirism.world.biome.VampirismBiomeFeatures;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import javax.annotation.Nullable;
import java.util.Random;

public class BloodySpruceTree extends AbstractTreeGrower {

    @Nullable
    @Override
    protected ConfiguredFeature<TreeConfiguration, ?> getConfiguredFeature(Random randomIn, boolean largeHive) {
        return randomIn.nextInt(10) < 7 ? VampirismBiomeFeatures.vampire_tree : VampirismBiomeFeatures.vampire_tree_red;
    }
}
