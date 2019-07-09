package de.teamlapen.vampirism.world.gen.features;

import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.FlowersFeature;

import java.util.Random;

public class VampireForestFlowerFeature extends FlowersFeature {
    @Override
    public BlockState getRandomFlower(Random p_202355_1_, BlockPos p_202355_2_) {
        return ModBlocks.vampirism_flower_vampire_orchid.getDefaultState();
    }
}
