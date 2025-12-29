package de.teamlapen.vampirism.misc.mixin.accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SpreadingSnowyDirtBlock.class)
public interface SpreadingSnowyDirtBlockAccessor {

    @Invoker("canPropagate")
    static boolean invokeCanPropagate(BlockState state, LevelReader level, BlockPos pos) {
        throw new IllegalStateException("Mixin failed to apply");
    }

    @Invoker("canBeGrass")
    static boolean invokeCanBeGrass(BlockState state, LevelReader level, BlockPos pos) {
        throw new IllegalStateException("Mixin failed to apply");
    }
}
