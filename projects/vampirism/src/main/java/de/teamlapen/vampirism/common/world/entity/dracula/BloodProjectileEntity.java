package de.teamlapen.vampirism.common.world.entity.dracula;

import de.teamlapen.vampirism.common.core.ModEntities;
import de.teamlapen.vampirism.common.world.entity.DarkBloodProjectileEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class BloodProjectileEntity extends DarkBloodProjectileEntity {

    public BloodProjectileEntity(EntityType<? extends BloodProjectileEntity> type, Level world) {
        super(type, world);
    }

    public BloodProjectileEntity(Level world, LivingEntity shooter, Vec3 accel) {
        super(ModEntities.BLOOD_PROJECTILE.get(), shooter, accel, world);
    }

    @Override
    protected void onHit(@NotNull net.minecraft.world.phys.HitResult result) {
        if (!this.level().isClientSide() && result.getType() == net.minecraft.world.phys.HitResult.Type.ENTITY) {
            Entity entity = ((net.minecraft.world.phys.EntityHitResult) result).getEntity();
            if (entity instanceof LivingEntity && entity != getOwner()) {
                Entity owner = getOwner();
                if (owner instanceof Dracula dracula) {
                    dracula.heal(dracula.getMaxHealth() * 0.005f);
                }
            }
        }
        super.onHit(result);
    }
}
