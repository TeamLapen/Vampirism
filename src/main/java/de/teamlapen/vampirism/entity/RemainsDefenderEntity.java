package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.blockentity.MotherBlockEntity;
import de.teamlapen.vampirism.blockentity.VulnerableRemainsBlockEntity;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;

public class RemainsDefenderEntity extends Mob implements IRemainsEntity {

    protected static final EntityDataAccessor<Direction> DATA_ATTACH_FACE_ID = SynchedEntityData.defineId(RemainsDefenderEntity.class, EntityDataSerializers.DIRECTION);
    private static final EntityDataAccessor<Integer> DATA_LIGHT_TICKS_REMAINING = SynchedEntityData.defineId(RemainsDefenderEntity.class, EntityDataSerializers.INT);


    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.ARMOR, 15).add(Attributes.ATTACK_DAMAGE, 5).add(Attributes.ARMOR_TOUGHNESS, 6);
    }

    public RemainsDefenderEntity(EntityType<RemainsDefenderEntity> type, Level pLevel) {
        super(type, pLevel);
        this.xpReward = 5;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 10f, 0.5f, false));
        this.goalSelector.addGoal(2, new RemainsDefenderAttackGoal());
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new RemainsDefenderAttackTargetGoal(this));

    }

    @Override
    protected Entity.@NotNull MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    public @NotNull SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    public void tick() {
        super.tick();
        // remove in case the there is no remains block, but the entity was not removed or was added otherwise
        if (level().getGameTime() % 512 == 32 && getVehicle() == null && !level().getBlockState(blockPosition().relative(getAttachFace())).is(ModTags.Blocks.ACTIVE_REMAINS)) {
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        int i = this.getLightTicksRemaining();
        if (i > 0) {
            this.setLightTicksRemaining(i - 1);
        }
    }

    @Override
    public boolean isInvulnerableTo(@NotNull DamageSource pSource) {
        return pSource.is(ModTags.DamageTypes.MOTHER_RESISTANT_TO) || pSource.is(DamageTypes.IN_WALL) || pSource.is(DamageTypes.DROWN) || super.isInvulnerableTo(pSource);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.REMAINS_DEFENDER_AMBIENT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.REMAINS_DEFENDER_DEATH.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
        return ModSounds.REMAINS_DEFENDER_HURT.get();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ATTACH_FACE_ID, Direction.DOWN);
        this.entityData.define(DATA_LIGHT_TICKS_REMAINING, 0);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setAttachFace(Direction.from3DDataValue(pCompound.getByte("AttachFace")));
        this.setLightTicksRemaining(pCompound.getInt("LightTicks"));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putByte("AttachFace", (byte) this.getAttachFace().get3DDataValue());
        pCompound.putInt("LightTicks", this.getLightTicksRemaining());
    }

    @Override
    protected boolean canRide(@NotNull Entity pVehicle) {
        return pVehicle instanceof VulnerableRemainsDummyEntity;
    }

    @Override
    public void rideTick() {
        this.tick();
    }

    @Override
    public void move(@NotNull MoverType pType, @NotNull Vec3 pPos) {
    }

    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        if(super.hurt(pSource, pAmount)) {
            this.getDummy().ifPresent(vehicle -> vehicle.childrenIsHurt(pSource, this.dead, getAttachFace()));
            return true;
        }
        return false;
    }

    @Override
    protected void actuallyHurt(@NotNull DamageSource pDamageSource, float pDamageAmount) {
        super.actuallyHurt(pDamageSource, pDamageAmount);
        this.invulnerableTime *= 2;
    }

    @Override
    public @NotNull Vec3 getDeltaMovement() {
        return Vec3.ZERO;
    }

    @Override
    public void setDeltaMovement(@NotNull Vec3 pDeltaMovement) {
    }

    public void setAttachFace(Direction face) {
        this.entityData.set(DATA_ATTACH_FACE_ID, face);
    }

    public Direction getAttachFace() {
        return this.entityData.get(DATA_ATTACH_FACE_ID);
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> pKey) {
        if (DATA_ATTACH_FACE_ID.equals(pKey)) {
            this.setBoundingBox(this.makeBoundingBox());
            refreshDimensions();
        }
        super.onSyncedDataUpdated(pKey);
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    protected float getStandingEyeHeight(@NotNull Pose pPose, @NotNull EntityDimensions pSize) {
        return switch (getAttachFace()) {
            case UP -> 0.8f;
            case DOWN -> 0.2f;
            default -> 0.5f;
        };
    }

    @Override
    public void push(@NotNull Entity pEntity) {
    }

    @Override
    public boolean startRiding(Entity pEntity, boolean pForce) {
        if(super.startRiding(pEntity, pForce)) {
            this.reapplyPosition();
            return true;
        }
        return false;
    }

    @Override
    protected @NotNull AABB makeBoundingBox() {
        Direction attachFace = getAttachFace().getOpposite();
        AABB box = new AABB(BlockPos.ZERO);
        return (switch (attachFace.getAxis()) {
            case X -> box.contract(0, 4d / 16, 4d / 16).contract(0, (-4d / 16), -(4d / 16));
            case Y -> box.contract(4d / 16, 0, 4d / 16).contract(-(4d / 16), 0, -(4d / 16));
            case Z -> box.contract(4d / 16, 4d / 16, 0).contract(-(4d / 16), -(4d / 16), 0);
        }).contract(attachFace.getStepX() * (12d/16d),attachFace.getStepY() * (12d/16d),attachFace.getStepZ() * (12d/16d)).move(blockPosition());
    }

    public Optional<VulnerableRemainsDummyEntity> getDummy() {
        return Optional.ofNullable(this.getVehicle()).filter(VulnerableRemainsDummyEntity.class::isInstance).map(VulnerableRemainsDummyEntity.class::cast);
    }

    private void setLightTicksRemaining(int ticks) {
        this.entityData.set(DATA_LIGHT_TICKS_REMAINING, ticks);
    }

    public int getLightTicksRemaining() {
        return this.entityData.get(DATA_LIGHT_TICKS_REMAINING);
    }

    class RemainsDefenderAttackGoal extends Goal {
        private int attackTime;

        public RemainsDefenderAttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = RemainsDefenderEntity.this.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                return RemainsDefenderEntity.this.level().getDifficulty() != Difficulty.PEACEFUL;
            } else {
                return false;
            }
        }

        @Override
        public void start() {
            this.attackTime = 40;
        }

        @Override
        public void stop() {
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (RemainsDefenderEntity.this.level().getDifficulty() != Difficulty.PEACEFUL) {
                --this.attackTime;
                LivingEntity livingentity = RemainsDefenderEntity.this.getTarget();
                if (livingentity != null) {
                    RemainsDefenderEntity.this.getLookControl().setLookAt(livingentity, 180.0F, 180.0F);
                    double d0 = RemainsDefenderEntity.this.distanceToSqr(livingentity);
                    if (d0 < 400) {
                        if (this.attackTime <= 0) {
                            this.attackTime = 20 + RemainsDefenderEntity.this.random.nextInt(10) * 20 / 2;
                            Vec3 position = RemainsDefenderEntity.this.position();
                            Vec3 direction = RemainsDefenderEntity.this.getViewVector(1.0f);
                            var projectile = new DarkBloodProjectileEntity(RemainsDefenderEntity.this.level(), position.x(), position.y(), position.z(), direction.x(), direction.y(), direction.z());
                            projectile.setOwner(RemainsDefenderEntity.this);
                            projectile.setDamage((float) RemainsDefenderEntity.this.getAttributeValue(Attributes.ATTACK_DAMAGE), 0);
                            projectile.excludeShooter();
                            RemainsDefenderEntity.this.level().addFreshEntity(projectile);
                            RemainsDefenderEntity.this.setLightTicksRemaining(40);
                        }
                    } else {
                        RemainsDefenderEntity.this.setTarget(null);
                    }

                    super.tick();
                }
            }
        }
    }

    static class RemainsDefenderAttackTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {

        public RemainsDefenderAttackTargetGoal(RemainsDefenderEntity entity) {
            super(entity, LivingEntity.class, 10, true, false, target -> {
                if (target instanceof ServerPlayer player && entity.getDummy().flatMap(VulnerableRemainsDummyEntity::getTile).flatMap(VulnerableRemainsBlockEntity::getMother).map(MotherBlockEntity::involvedPlayers).stream().anyMatch(s -> s.contains(player))) {
                    return true;
                } else {
                    return !(target instanceof  IRemainsEntity) && !Helper.isVampire(target);
                }
            });
        }
    }

    @Override
    protected void reapplyPosition() {
        if (this.getVehicle() != null) {
            Direction attachFace = getAttachFace();
            this.setPos(Vec3.atBottomCenterOf(this.getVehicle().blockPosition().above()).subtract(attachFace.getStepX(), attachFace.getStepY(), attachFace.getStepZ()));
        } else {
            super.reapplyPosition();
        }
    }
}
