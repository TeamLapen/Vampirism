package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.blockentity.VulnerableRemainsBlockEntity;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class VulnerableRemainsDummyEntity extends Entity {

    private BlockPos ownerPos = null;

    public VulnerableRemainsDummyEntity(EntityType<VulnerableRemainsDummyEntity> type, Level pLevel) {
        super(type, pLevel);
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    @Override
    public Vec3 getDeltaMovement() {
        return Vec3.ZERO;
    }

    @Override
    public void setDeltaMovement(Vec3 pMotion) {
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (ownerPos != null) {
            if (level.getBlockEntity(ownerPos) instanceof VulnerableRemainsBlockEntity vr) {
                vr.onDamageDealt(pSource, pAmount);
            }
        }
        return false;
    }

    @Override
    public boolean isPickable() {
        return true; //This ensures the entity can be targeted by client which is required to be able to hit it
    }


    @Override
    public void push(Entity pEntity) {
    }

    public void setOwnerLocation(BlockPos pos) {
        this.ownerPos = pos;
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide() && (this.ownerPos == null || this.level.getBlockState(ownerPos).getBlock() != ModBlocks.ACTIVE_VULNERABLE_REMAINS.get())) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }
}
