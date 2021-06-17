package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.tileentity.TotemHelper;
import net.minecraft.block.BellBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BellBlock.class)
public class MixinBellBlock {

    @Inject(method = "attemptRing", at = @At(value = "RETURN", ordinal = 0))
    public void ringTotem(World world, BlockState state, BlockRayTraceResult result, PlayerEntity player, boolean canRingBell, CallbackInfoReturnable<Boolean> cir) {
        if (player != null) TotemHelper.ringBell(world, player);
    }
}
