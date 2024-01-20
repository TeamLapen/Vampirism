package de.teamlapen.vampirism.mixin.accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(net.minecraft.world.level.block.SpreadingSnowyDirtBlock.class)
public interface SpreadingSnowyDirtBlockAccessor {

    @Invoker("canPropagate(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z")
    static boolean canPropagate(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        throw new IllegalStateException("Mixin failed to apply");
    }

    @Invoker("canBeGrass(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z")
    static boolean canBeGrass(BlockState pState, LevelReader pLevelReader, BlockPos pPos) {
        throw new IllegalStateException("Mixin failed to apply");
    }
}
