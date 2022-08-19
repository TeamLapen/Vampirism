package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.util.TotemHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BellBlock.class)
public class MixinBellBlock {

    @Inject(method = "onHit", at = @At(value = "RETURN", ordinal = 0))
    public void ringTotem(@NotNull Level world, BlockState state, BlockHitResult result, @Nullable Player player, boolean canRingBell, CallbackInfoReturnable<Boolean> cir) {
        if (player != null) TotemHelper.ringBell(world, player);
    }
}
