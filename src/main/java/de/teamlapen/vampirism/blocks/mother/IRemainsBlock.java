package de.teamlapen.vampirism.blocks.mother;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IRemainsBlock {

    boolean isVulnerable(BlockState state);

    boolean isVulnerability(BlockState state);

    boolean isMother(BlockState state);

    default void freeze(Level level, BlockPos pos, BlockState state) {
    }

    default void unFreeze(Level level, BlockPos pos, BlockState state) {
    }

}
