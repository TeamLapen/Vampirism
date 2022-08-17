package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.config.VampirismConfig;
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
//    Does not help for Optifine Shaders because they appear to implement the color themselves (if at all)
//    @Inject(method = "getTeamColor", at = @At("RETURN"), cancellable = true)
//    private void handleGetTeamColor(CallbackInfoReturnable<Integer> cir) {
//        if(ASMHooks.enforcingGlowing_bloodVision){
//            PlayerEntity p = Minecraft.getInstance().player;
//            Entity e = (Entity) (Object)this;
//            if(p!=null){
//                int color;
//                LazyOptional<IExtendedCreatureVampirism> opt = e instanceof CreatureEntity && e.isAlive() ? ExtendedCreature.getSafe(e) : LazyOptional.empty();
//                if (opt.map(creature -> creature.getBlood() > 0 && !creature.hasPoisonousBlood()).orElse(false)) {
//                    color = 0xFF0000;
//                } else if (VampirePlayer.getOpt(p).map(VampirePlayer::getSpecialAttributes).map(s -> s.blood_vision_garlic).orElse(false) && ((opt.map(IExtendedCreatureVampirism::hasPoisonousBlood).orElse(false)) || Helper.isHunter(e))) {
//                    color = 0x07FF07;
//                } else {
//                    color = 0xA0A0A0;
//                }
//                cir.setReturnValue(color);
//            }
//        }
//
//    }
}
