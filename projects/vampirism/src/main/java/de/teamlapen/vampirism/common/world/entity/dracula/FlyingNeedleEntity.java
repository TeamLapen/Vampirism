package de.teamlapen.vampirism.common.world.entity.dracula;

import de.teamlapen.vampirism.common.core.ModEntities;
import de.teamlapen.vampirism.common.util.DamageHandler;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class FlyingNeedleEntity extends Projectile {

    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(FlyingNeedleEntity.class, EntityDataSerializers.BOOLEAN);

    private int lifeTicks = 0;
    private static final int MAX_LIFE = 100;
    private float damage = 4.0f;
    private int indexOffset = 0;
    private int maxCount = 1;
    private @Nullable UUID draculaUUID;
    private @Nullable Dracula dracula;

    public FlyingNeedleEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noPhysics = true;
    }

    public FlyingNeedleEntity(Level level, Dracula owner, float damage, int indexOffset, int maxCount) {
        this(ModEntities.FLYING_NEEDLE.get(), level);
        this.setOwner(owner);
        this.dracula = owner;
        this.draculaUUID = owner.getUUID();
        this.indexOffset = indexOffset;
        this.maxCount = maxCount;
        this.damage = damage;
        this.setPos(owner.getX(), owner.getEyeY(), owner.getZ());
    }

    //<editor-fold desc="Tick">

    @Override
    public void tick() {
        super.tick();
        if (this.isFlying()) {
            tickFlying();
        } else {
            tickOrbiting();
        }
    }

    private void tickFlying() {
        this.lifeTicks++;
        if (this.lifeTicks > MAX_LIFE) {
            this.discard();
            return;
        }

        Vec3 movement = this.getDeltaMovement();
        Vec3 nextPos = this.position().add(movement);

        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(this.level(), this, this.position(), nextPos, this.getBoundingBox().expandTowards(movement).inflate(1.0D), this::canHitEntity);
        if (entityHitResult != null) {
            onHit(entityHitResult);
        }

        this.setPos(nextPos);
        if (movement.lengthSqr() > 1.0E-7D) {
            this.setYRot((float) (Math.atan2(-movement.x, movement.z) * (180 / Math.PI)));
            this.setXRot((float) (-Math.asin(movement.y / movement.length()) * (180 / Math.PI)));
        }
    }

    private void tickOrbiting() {
        // But we might want it to follow the owner if not fired yet.
        if (this.dracula == null && this.draculaUUID != null && this.level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(this.draculaUUID);
            if (entity instanceof Dracula d) {
                this.dracula = d;
            }
        }

        // orbit Dracula
        if (this.dracula != null && this.dracula.isAlive()) {
            double angle = (this.level().getGameTime() * 0.1) + (this.indexOffset * (Math.PI * 2 / this.maxCount));
            double x = this.dracula.getX() + Math.cos(angle) * 1.5;
            double z = this.dracula.getZ() + Math.sin(angle) * 1.5;
            double y = this.dracula.getY() + 1.5 + Math.sin(this.level().getGameTime() * 0.05 + this.indexOffset) * 0.5;

            this.setPos(x, y, z);
            this.setYRot((float) (angle * (180 / Math.PI)));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        } else if (!this.level().isClientSide()) {
            this.discard();
        }
    }

    //</editor-fold>

    //<editor-fold desc="Shooting / Hitting">

    public void shoot(LivingEntity target) {
        this.setFlying(true);
        Vec3 direction = target.getHitbox().getCenter().subtract(this.position()).normalize();
        this.setDeltaMovement(direction.scale(3));
        this.setYRot((float) (Math.atan2(-direction.x, direction.z) * (180 / Math.PI)));
        this.setXRot((float) (-Math.asin(direction.y) * (180 / Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        Entity entity = pResult.getEntity();
        if (entity instanceof LivingEntity living && !this.level().isClientSide()) {
            if (living != this.getOwner()) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    DamageHandler.hurtVanilla(serverLevel, living, damageSources -> damageSources.mobAttack((LivingEntity) this.getOwner()), damage);
                    this.discard();
                }
            }
        }
    }

    @Override
    protected boolean canHitEntity(Entity pTarget) {
        return super.canHitEntity(pTarget) && pTarget != this.getOwner() && this.isFlying();
    }

    //</editor-fold>

    //<editor-fold desc="Data">

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(FLYING, false);
    }

    public void setFlying(boolean flying) {
        this.entityData.set(FLYING, flying);
    }

    public boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    //</editor-fold>

    //<editor-fold desc="Serialization">

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.lifeTicks = input.getIntOr("life_ticks", 0);
        this.damage = input.getFloatOr("damage", 4.0f);
        this.indexOffset = input.getIntOr("index_offset", 0);
        this.draculaUUID = input.read("dracula_uuid", UUIDUtil.CODEC).orElse(null);
        this.setFlying(input.getBooleanOr("flying", false));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("life_ticks", this.lifeTicks);
        output.putFloat("damage", this.damage);
        output.putInt("index_offset", this.indexOffset);
        if (this.draculaUUID != null) {
            output.store("dracula_uuid", UUIDUtil.CODEC, this.draculaUUID);
        }
        output.putBoolean("flying", this.isFlying());
    }
}
