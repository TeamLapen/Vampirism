package de.teamlapen.vampirism.common.world.entity.dracula;

import de.teamlapen.vampirism.common.core.ModEntities;
import de.teamlapen.vampirism.common.util.DamageHandler;
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

public class FlyingSwordEntity extends Projectile {

    private int lifeTicks = 0;
    private static final int MAX_LIFE = 60;
    private float damage = 5.0f;

    public FlyingSwordEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noPhysics = true;
    }

    public FlyingSwordEntity(Level level, LivingEntity owner, LivingEntity target, float damage) {
        this(ModEntities.FLYING_SWORD.get(), level);
        this.setOwner(owner);
        this.damage = damage;
        this.setPos(owner.getX(), owner.getEyeY(), owner.getZ());

        Vec3 direction = target.getHitbox().getCenter().subtract(this.position()).normalize();
        this.setDeltaMovement(direction.scale(1.5));
        this.setRot((float) (Math.atan2(-direction.x, direction.z) * (180 / Math.PI)), (float) (-Math.asin(direction.y) * (180 / Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
    }

    @Override
    public void tick() {
        super.tick();
        this.lifeTicks++;
        if (this.lifeTicks > MAX_LIFE) {
            this.discard();
            return;
        }

        Vec3 movement = this.getDeltaMovement();
        Vec3 nextPos = this.position().add(movement);

        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(this.level(), this, this.position(), nextPos, this.getBoundingBox().expandTowards(movement).inflate(1.0D), e -> this.canHitEntity(e));
        if (entityHitResult != null) {
            onHit(entityHitResult);
        }

        this.setPos(nextPos);

        // Keep the rotation towards movement
        if (movement.lengthSqr() > 1.0E-7D) {
            this.setRot((float) (Math.atan2(-movement.x, movement.z) * (180 / Math.PI)), (float) (-Math.asin(movement.y / movement.length()) * (180 / Math.PI)));
        }
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
        return super.canHitEntity(pTarget) && pTarget != this.getOwner();
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.lifeTicks = input.getIntOr("LifeTicks", 0);
        this.damage = input.getFloatOr("Damage", 5.0f);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("LifeTicks", this.lifeTicks);
        output.putFloat("Damage", this.damage);
    }
}
