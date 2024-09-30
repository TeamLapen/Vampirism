package de.teamlapen.vampirism.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class HomingProjectile extends AbstractHurtingProjectile {

    @Nullable
    private UUID targetUUID;
    @Nullable
    private LivingEntity cachedTarget;

    private Vec3 direction;

    protected HomingProjectile(EntityType<? extends AbstractHurtingProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public HomingProjectile(EntityType<? extends AbstractHurtingProjectile> pEntityType, double pX, double pY, double pZ, Vec3 direction, Level pLevel) {
        super(pEntityType, pX, pY, pZ, direction, pLevel);
        this.direction = direction;
    }

    public HomingProjectile(EntityType<? extends AbstractHurtingProjectile> pEntityType, LivingEntity pShooter, Vec3 offset, Level pLevel) {
        super(pEntityType, pShooter, offset, pLevel);
    }

    public void setTarget(@Nullable LivingEntity cachedTarget) {
        if (cachedTarget != null) {
            this.targetUUID = cachedTarget.getUUID();
            this.cachedTarget = cachedTarget;
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (this.targetUUID != null) {
            pCompound.putUUID("target", this.targetUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.hasUUID("target")) {
            this.targetUUID = pCompound.getUUID("target");
        }
    }

    @Nullable
    protected LivingEntity getTarget() {
        if (targetUUID != null) {
            if (this.cachedTarget != null) {
                if (this.cachedTarget.isRemoved()) {
                    this.cachedTarget = null;
                } else {
                    return this.cachedTarget;
                }
            } else if (this.level() instanceof ServerLevel serverLevel) {
                this.cachedTarget = (LivingEntity) serverLevel.getEntity(this.targetUUID);
                return this.cachedTarget;
            }
        }
        return null;
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity target = getTarget();
        if (target != null) {
            Vec3 updatedTarget = target.getEyePosition();
            Vec3 position = position();
            Vec3 idealDirection = updatedTarget.subtract(position);
            Vec3 currentDirection = this.direction;

            double angleBetween = Math.acos(idealDirection.dot(currentDirection));
            double maxAngle = Math.toRadians(20);

            if (angleBetween > maxAngle) {
                Vec3 rotationAxis = currentDirection.cross(idealDirection).normalize();

                // Create a quaternion for the rotation
                Quaterniond rotation = new Quaterniond().rotationAxis(maxAngle, rotationAxis.x, rotationAxis.y, rotationAxis.z);

                // Apply the rotation to the current direction
                Vector3d newDir = rotation.transform(new Vector3d(currentDirection.x, currentDirection.y, currentDirection.z));

                this.direction = new Vec3(newDir.x, newDir.y, newDir.z);
            } else {
                this.direction = idealDirection;
            }

            // use the acceleration from the superclass
            double length = getDeltaMovement().length();

            this.setDeltaMovement(this.direction.normalize().scale(length));
        }
    }
}
