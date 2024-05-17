package de.teamlapen.vampirism.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.MixinHooks;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Entity.class)
public class MixinEntity {

    @Inject(method = "isCurrentlyGlowing", at = @At("RETURN"), cancellable = true)
    private void handleIsGlowing(@NotNull CallbackInfoReturnable<Boolean> cir) {
        if (MixinHooks.enforcingGlowing_bloodVision) {
            Entity p = VampirismMod.proxy.getClientPlayer();
            Entity e = (Entity) (Object) this;
            if (p != null && p.distanceToSqr(e) < VampirismConfig.BALANCE.vsBloodVisionDistanceSq.get()) {
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
}
