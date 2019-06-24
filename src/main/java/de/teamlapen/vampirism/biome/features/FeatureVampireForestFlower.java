package de.teamlapen.vampirism.biome.features;

import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.AbstractFlowersFeature;

import java.util.Random;

public class FeatureVampireForestFlower extends AbstractFlowersFeature {
    @Override
    public IBlockState getRandomFlower(Random p_202355_1_, BlockPos p_202355_2_) {
        return ModBlocks.vampirism_flower_vampire_orchid.getDefaultState();
    }
}
