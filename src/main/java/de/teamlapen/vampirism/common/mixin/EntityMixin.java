package de.teamlapen.vampirism.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.entity.player.IVampirismPlayer;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.util.MixinHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "isCurrentlyGlowing", at = @At("RETURN"), cancellable = true)
    private void handleIsGlowing(@NotNull CallbackInfoReturnable<Boolean> cir) {
        if (MixinHooks.enforcingGlowing_bloodVision) {
            Entity p = VampirismMod.proxy.getClientPlayer();
            Entity e = (Entity) (Object) this;
            if (p != null && p.distanceToSqr(e) < ModConfig.BALANCE.vsBloodVisionDistanceSq.get()) {
                cir.setReturnValue(true);
            }
        }
    }

    @ModifyReturnValue(method = "getTicksRequiredToFreeze", at = @At("RETURN"))
    private int ticks(int ticks) {
        if (Helper.isVampire((Entity) (Object) this)) {
            return ticks * 10;
        }
        return ticks;
    }

    @Inject(method = "vibrationAndSoundEffectsFromBlock", at = @At("HEAD"), cancellable = true)
    private void test(BlockPos pPos, BlockState pState, boolean pPlayStepSound, boolean pBroadcastGameEvent, Vec3 pEntityPos, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof IVampirismPlayer player && player.vampirism$getVampAtts().getVampSpecial().darkStalker) {
            cir.setReturnValue(false);
        }
    }
}
