package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.hunter.HunterPlayerSpecialAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {
    private final static Logger LOGGER = LogManager.getLogger();

    @Deprecated
    protected MixinLivingEntity(EntityType<? extends LivingEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Inject(method = "getVisibilityMultiplier", at = @At("RETURN"), cancellable = true)
    private void handleVisibilityMod(@Nullable Entity lookingEntity, CallbackInfoReturnable<Double> cir) {
        if (((Entity) this) instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) (Entity) this;
            if (HunterPlayer.getOpt(player).map(HunterPlayer::getSpecialAttributes).map(HunterPlayerSpecialAttribute::isDisguised).orElse(false)) {
                cir.setReturnValue(cir.getReturnValueD() * VampirismConfig.BALANCE.haDisguiseVisibilityMod.get());
            }
        }

    }
}
