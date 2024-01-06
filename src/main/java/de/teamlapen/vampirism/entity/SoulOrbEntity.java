package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Similar to EntityXPOrb
 */
public class SoulOrbEntity extends Entity implements ItemSupplier {

    public static final EntityDataAccessor<String> TYPE_PARAMETER = SynchedEntityData.defineId(SoulOrbEntity.class, EntityDataSerializers.STRING);
    private int delayBeforePickup;
    private @Nullable Player player;
    private int age;
    @Nullable
    private ItemStack soulItemStack;

    public SoulOrbEntity(@NotNull Level worldIn, double x, double y, double z, @NotNull VARIANT type) {
        super(ModEntities.SOUL_ORB.get(), worldIn);
        this.setVariant(type);
        delayBeforePickup = 10;
        this.setPos(x, y, z);
        this.setYRot((float) (Math.random() * 360.0D));
        this.setDeltaMovement((this.random.nextDouble() * (double) 0.2F - (double) 0.1F) * 2.0D, this.random.nextDouble() * 0.2D * 2.0D, (this.random.nextDouble() * (double) 0.2F - (double) 0.1F) * 2.0D);
    }

    public SoulOrbEntity(@NotNull EntityType<? extends SoulOrbEntity> type, @NotNull Level worldIn) {
        super(type, worldIn);
    }


    public @NotNull VARIANT getVariant() {
        return VARIANT.valueOf(getEntityData().get(TYPE_PARAMETER));
    }

    private void setVariant(@NotNull VARIANT type) {
        getEntityData().set(TYPE_PARAMETER, type.name());
    }


    @NotNull
    @Override
    public ItemStack getItem() {
        return getSoulItemStack();
    }

    @NotNull
    public ItemStack getSoulItemStack() {
        if (soulItemStack == null) {
            soulItemStack = createSoulItemStack();
        }
        return soulItemStack;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean isInvisibleTo(@NotNull Player player) {
        if (getVariant() == VARIANT.VAMPIRE) {
            return !Helper.isHunter(player) || player.isSpectator();
        }
        return true;
    }

    @Override
    public void playerTouch(@NotNull Player entityIn) {
        if (!this.level().isClientSide) {
            if (delayBeforePickup == 0) {
                if (Helper.isHunter(entityIn)) {
                    if (entityIn.getInventory().add(getSoulItemStack())) {
                        entityIn.take(this, 1);
                        this.discard();
                    }
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (delayBeforePickup > 0) {
            delayBeforePickup--;
        }

        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();

        if (this.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value())) {
            Vec3 vec3d = this.getDeltaMovement();
            this.setDeltaMovement(vec3d.x * (double) 0.99F, Math.min(vec3d.y + (double) 5.0E-4F, 0.06F), vec3d.z * (double) 0.99F);
        } else if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));
        }

        if (this.level().getFluidState(blockPosition()).is(FluidTags.LAVA)) {
            this.setDeltaMovement((this.random.nextFloat() - this.random.nextFloat()) * 0.2F, 0.2F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            this.playSound(SoundEvents.GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
        }
        if (!this.level().noCollision(this.getBoundingBox())) { //areCollisionShapesEmpty
            this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.getZ());
        }


        if (this.age % 10 == 5 & (this.player == null || !this.player.isAlive() || this.player.distanceToSqr(this) > 64)) {
            this.player = this.level().getNearestPlayer(this.getX(), this.getY(), this.getZ(), 8, EntitySelector.NO_SPECTATORS.and(Helper::isHunter));
        }

        if (this.player != null) {
            Vec3 vec3d = new Vec3(this.player.getX() - this.getX(), this.player.getY() + (double) this.player.getEyeHeight() / 2.0D - this.getY(), this.player.getZ() - this.getZ());
            double d1 = vec3d.lengthSqr();
            if (d1 < 64.0D) {
                double d2 = 1.0D - Math.sqrt(d1) / 8.0D;
                this.setDeltaMovement(this.getDeltaMovement().add(vec3d.normalize().scale(d2 * d2 * 0.1D)));
            }
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
        float f = 0.98F;

        if (this.onGround()) {
            BlockPos underPos = new BlockPos(Mth.floor(this.getX()), Mth.floor(this.getBoundingBox().minY) - 1, Mth.floor(this.getZ()));
            BlockState underState = this.level().getBlockState(underPos);
            f = underState.getBlock().getFriction(underState, this.level(), underPos, this) * 0.98F;
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(f, 0.9800000190734863D, f));

        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1D, -0.8999999761581421D, 1D));
        }


        this.age++;

        if (this.age >= 6000) {
            this.discard();
        }
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
        compound.putString("type", this.getVariant().name());
        compound.putInt("age", age);
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(TYPE_PARAMETER, VARIANT.NONE.name());
    }

    @NotNull
    @Override
    protected MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }


    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
        this.setVariant(VARIANT.valueOf(compound.getString("type")));
        this.age = compound.getInt("age");
        soulItemStack = null;//Reset item just in case an item of a different type has been created beforehand
    }

    private @NotNull ItemStack createSoulItemStack() {
        //noinspection IfStatementWithIdenticalBranches
        if (getVariant() == VARIANT.VAMPIRE) {
            return new ItemStack(ModItems.SOUL_ORB_VAMPIRE.get());
        }
        return new ItemStack(ModItems.SOUL_ORB_VAMPIRE.get());
    }


    public enum VARIANT {
        NONE, VAMPIRE
    }
}
