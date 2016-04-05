package de.teamlapen.vampirism.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Created by Max on 23.02.2016.
 */
public interface IGarlicBlock {
    EnumGarlicStrength getGarlicStrength(IBlockAccess world, BlockPos pos);
}
