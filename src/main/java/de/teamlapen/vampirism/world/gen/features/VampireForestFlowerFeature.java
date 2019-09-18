package de.teamlapen.vampirism.world.gen.features;

import com.mojang.datafixers.Dynamic;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.FlowersFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;
import java.util.function.Function;

public class VampireForestFlowerFeature extends FlowersFeature {
    public VampireForestFlowerFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> fnc) {
        super(fnc);
    }

    @Override
    public BlockState getRandomFlower(Random p_202355_1_, BlockPos p_202355_2_) {
        return ModBlocks.vampire_orchid.getDefaultState();
    }
}
