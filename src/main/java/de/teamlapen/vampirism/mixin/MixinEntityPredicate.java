package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.api.entity.hunter.IHunterMob;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(TargetingConditions.class)
public class MixinEntityPredicate {

    @Final
    @Shadow
    private boolean isCombat;

    @Inject(method = "test", at = @At("RETURN"), cancellable = true)
    private void handleCanTarget_vampirism(LivingEntity attacker, LivingEntity target, @NotNull CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && isCombat) {
            if (target instanceof Player && !(attacker instanceof IHunterMob)) {
                if (VampirismPlayerAttributes.get((Player) target).getVampSpecial().isDBNO) {
                    cir.setReturnValue(false);
                    cir.cancel();
                }
            }
        }
    }
}
