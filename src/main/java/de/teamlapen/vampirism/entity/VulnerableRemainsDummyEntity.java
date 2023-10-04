package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.entity.IEntityLeader;
import de.teamlapen.vampirism.blockentity.VulnerableRemainsBlockEntity;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Optional;

public class VulnerableRemainsDummyEntity extends LivingEntity implements IEntityLeader {

    private BlockPos ownerPos = null;

    public VulnerableRemainsDummyEntity(EntityType<VulnerableRemainsDummyEntity> type, Level pLevel) {
        super(type, pLevel);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return LivingEntity.createLivingAttributes();
    }

    @Override
    public float getHealth() {
        return getTile().map(VulnerableRemainsBlockEntity::getHealth).orElse(1);
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    @Override
    public @NotNull Vec3 getDeltaMovement() {
        return Vec3.ZERO;
    }

    @Override
    public void setDeltaMovement(@NotNull Vec3 pMotion) {
    }

    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        getTile().ifPresent(vr -> {
            vr.onDamageDealt(pSource, pAmount);
        });
        return false;
    }

    @Override
    public boolean isInvulnerableTo(@NotNull DamageSource pSource) {
        return this.isRemoved() || pSource.is(DamageTypes.ON_FIRE) || pSource.is(ModDamageTypes.HOLY_WATER) || pSource.is(DamageTypes.FREEZE);
    }

    @Override
    public @NotNull Iterable<ItemStack> getArmorSlots() {
        return Collections.emptyList();
    }

    @Override
    public @NotNull ItemStack getItemBySlot(@NotNull EquipmentSlot pSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(@NotNull EquipmentSlot pSlot, @NotNull ItemStack pStack) {

    }

    @Override
    public boolean isPickable() {
        return true; //This ensures the entity can be targeted by client which is required to be able to hit it
    }

    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.LEFT;
    }


    @Override
    public void push(@NotNull Entity pEntity) {
    }

    public void setOwnerLocation(BlockPos pos) {
        this.ownerPos = pos;
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide() && (this.ownerPos == null || this.level().getBlockState(ownerPos).getBlock() != ModBlocks.ACTIVE_VULNERABLE_REMAINS.get())) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    private Optional<VulnerableRemainsBlockEntity> getTile() {
        if (ownerPos != null) {
            if (this.level().getBlockEntity(ownerPos) instanceof VulnerableRemainsBlockEntity vr) {
                return Optional.of(vr);
            }
        }
        return Optional.empty();
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {

    }

    @Override
    protected Entity.@NotNull MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {

    }

    private int followerCount = 0;

    @Override
    public void decreaseFollowerCount() {
        followerCount--;
    }

    @Override
    public int getFollowingCount() {
        return this.followerCount;
    }

    @Override
    public int getMaxFollowerCount() {
        return isAlive() ? Integer.MAX_VALUE : 0;
    }

    @Override
    public LivingEntity getRepresentingEntity() {
        return this;
    }

    @Override
    public boolean increaseFollowerCount() {
        followerCount++;
        return true;
    }
}
