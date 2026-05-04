package de.teamlapen.vampirism.misc.mixin;

import de.teamlapen.faction.common.util.TotemHelper;
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
public class BellBlockMixin {

    @Inject(method = "onHit", at = @At(value = "RETURN", ordinal = 0))
    public void ringTotem(@NotNull Level level, BlockState state, BlockHitResult hitResult, @Nullable Player player, boolean requireHitFromCorrectSide, CallbackInfoReturnable<Boolean> cir) {
        if (player != null) TotemHelper.ringBell(level, player);
    }
}
