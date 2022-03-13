package de.teamlapen.vampirism.world.gen;

import de.teamlapen.vampirism.world.biome.VampirismBiomeFeatures;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BloodySpruceTree extends AbstractTreeGrower {

    @Nullable
    @Override
    protected Holder<? extends ConfiguredFeature<TreeConfiguration, ?>> getConfiguredFeature(@Nonnull Random randomIn, boolean largeHive) {
        return VampirismBiomeFeatures.vampire_tree_red;
    }
}
