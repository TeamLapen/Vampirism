package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EntityPredicate.class)
public class MixinEntityPredicate {

    @Shadow
    private boolean allowInvulnerable;

    @Inject(method = "canTarget", at = @At("RETURN"), cancellable = true)
    private void handleCanTarget_vampirism( LivingEntity attacker, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if(cir.getReturnValue()&&!allowInvulnerable){
            if(target instanceof PlayerEntity){
                if(VampirePlayer.getOpt((PlayerEntity) target).map(VampirePlayer::isDBNO).orElse(false)){
                    cir.setReturnValue(false);
                    cir.cancel();
                }
            }
        }
    }
}
