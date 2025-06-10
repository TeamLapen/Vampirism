package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HopperBlock.class)
public class HopperBlockMixin {

    @Inject(method = "checkPoweredState", at = @At("HEAD"), cancellable = true)
    private void skipDisableWhenConnectedToBloodGrinder(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
        Direction facing = state.getValue(HopperBlock.FACING);
        BlockState targetState = level.getBlockState(pos.relative(facing));

        // TODO: This was added because you couldn't power blood grinders with levers and use hoppers to put the meat inside as a consequence of how redstone works. Maybe adding a config to turn this feature off just in case?
        if (targetState.getBlock() == ModBlocks.BLOOD_GRINDER.get()) {
            if (!state.getValue(HopperBlock.ENABLED)) {
                level.setBlock(pos, state.setValue(HopperBlock.ENABLED, true), Block.UPDATE_CLIENTS);
            }
            ci.cancel();
        }
    }
}
