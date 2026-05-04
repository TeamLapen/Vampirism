package de.teamlapen.vampirism.misc.mixin;

import de.teamlapen.vampirism.api.world.entity.hunter.IHunterMob;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.server.level.ServerLevel;
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
public class EntityPredicateMixin {

    @Final
    @Shadow
    private boolean isCombat;

    @Inject(method = "test", at = @At("RETURN"), cancellable = true)
    private void ignoreIfDown(ServerLevel level, LivingEntity targeter, LivingEntity target, @NotNull CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && isCombat) {
            if (target instanceof Player player && !(targeter instanceof IHunterMob)) {
                if (VampirePlayer.get(player).getSkillProperties().isDBNO) {
                    cir.setReturnValue(false);
                    cir.cancel();
                }
            }
        }
    }
}
