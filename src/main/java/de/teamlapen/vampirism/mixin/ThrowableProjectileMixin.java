package de.teamlapen.vampirism.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.teamlapen.vampirism.core.ModEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.neoforged.fml.common.Mod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ThrowableProjectile.class)
public abstract class ThrowableProjectileMixin extends Projectile {

    protected ThrowableProjectileMixin(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void shootFromRotation(Entity pShooter, float pX, float pY, float pZ, float pVelocity, float pInaccuracy) {
        super.shootFromRotation(pShooter, pX, pY, pZ, pVelocity, pInaccuracy);
    }

    @SuppressWarnings("MixinAnnotationTarget")
    @WrapOperation(method = "shootFromRotation", at = @At(value = "INVOKE", target = "shootFromRotation"))
    public void modifyVelocity(ThrowableProjectile projectile, Entity pShooter, float pX, float pY, float pZ, float pVelocity, float pInaccuracy, Operation<Void> operation) {
        operation.call(projectile, pShooter, pX, pY, pZ, pShooter instanceof LivingEntity living && living.hasEffect(ModEffects.FREEZE) ? pVelocity / 3 : pVelocity, pInaccuracy);
    }
}
