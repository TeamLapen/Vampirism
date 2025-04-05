package de.teamlapen.vampirism.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.jetbrains.annotations.NotNull;

public class HunterNodeEvaluator extends WalkNodeEvaluator {
    @Override
    public @NotNull PathType getPathTypeOfMob(@NotNull PathfindingContext context, int x, int y, int z, @NotNull Mob mob) {
        PathType pathType = super.getPathTypeOfMob(context, x, y, z, mob);

        BlockState blockState = context.level().getBlockState(new BlockPos(x, y, z));
        if (blockState.getBlock() instanceof FenceGateBlock) {
            boolean isOpen = blockState.getValue(FenceGateBlock.OPEN);
            if (isOpen) {
                return PathType.WALKABLE_DOOR;
            } else {
                return this.canOpenDoors() ? PathType.WALKABLE_DOOR : PathType.DOOR_WOOD_CLOSED;
            }
        }

        return pathType;
    }

    @Override
    public @NotNull PathType getPathType(@NotNull PathfindingContext context, int x, int y, int z) {
        PathType pathType = super.getPathType(context, x, y, z);

        BlockState blockState = context.level().getBlockState(new BlockPos(x, y, z));
        if (blockState.getBlock() instanceof FenceGateBlock) {
            boolean isOpen = blockState.getValue(FenceGateBlock.OPEN);
            return isOpen ? PathType.WALKABLE_DOOR : PathType.DOOR_WOOD_CLOSED;
        }

        return pathType;
    }
}
