package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Similar to EntityXPOrb
 */
@OnlyIn(
        value = Dist.CLIENT,
        _interface = IRendersAsItem.class
)
public class SoulOrbEntity extends Entity implements IRendersAsItem {

    public static final DataParameter<String> TYPE_PARAMETER = EntityDataManager.defineId(SoulOrbEntity.class, DataSerializers.STRING);
    private int delayBeforePickup;
    private PlayerEntity player;
    private int age;
    @Nullable
    private ItemStack soulItemStack;

    public SoulOrbEntity(World worldIn, double x, double y, double z, VARIANT type) {
        super(ModEntities.SOUL_ORB.get(), worldIn);
        this.setVariant(type);
        delayBeforePickup = 10;
        this.setPos(x, y, z);
        this.yRot = (float) (Math.random() * 360.0D);
        this.setDeltaMovement((this.random.nextDouble() * (double) 0.2F - (double) 0.1F) * 2.0D, this.random.nextDouble() * 0.2D * 2.0D, (this.random.nextDouble() * (double) 0.2F - (double) 0.1F) * 2.0D);
    }

    public SoulOrbEntity(EntityType<? extends SoulOrbEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public VARIANT getVariant() {
        return VARIANT.valueOf(getEntityData().get(TYPE_PARAMETER));
    }

    private void setVariant(VARIANT type) {
        getEntityData().set(TYPE_PARAMETER, type.name());
    }


    @Override
    public ItemStack getItem() {
        return getSoulItemStack();
    }

    @Nonnull
    public ItemStack getSoulItemStack() {
        if (soulItemStack == null) {
            soulItemStack = createSoulItemStack();
        }
        return soulItemStack;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean isInvisibleTo(@Nonnull PlayerEntity player) {
        if (getVariant() == VARIANT.VAMPIRE) {
            return !Helper.isHunter(player) || player.isSpectator();
        }
        return true;
    }

    @Override
    public void playerTouch(PlayerEntity entityIn) {
        if (!this.level.isClientSide) {
            if (delayBeforePickup == 0) {
                if (Helper.isHunter(entityIn)) {
                    if (entityIn.inventory.add(getSoulItemStack())) {
                        entityIn.take(this, 1);
                        this.remove();
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

        if (this.isEyeInFluid(FluidTags.WATER)) {
            Vector3d vec3d = this.getDeltaMovement();
            this.setDeltaMovement(vec3d.x * (double) 0.99F, Math.min(vec3d.y + (double) 5.0E-4F, 0.06F), vec3d.z * (double) 0.99F);
        } else if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));
        }

        if (this.level.getFluidState(blockPosition()).is(FluidTags.LAVA)) {
            this.setDeltaMovement((this.random.nextFloat() - this.random.nextFloat()) * 0.2F, 0.2F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            this.playSound(SoundEvents.GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
        }
        if (!this.level.noCollision(this.getBoundingBox())) { //areCollisionShapesEmpty
            this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.getZ());
        }


        if (this.age % 10 == 5 & (this.player == null || !this.player.isAlive() || this.player.distanceToSqr(this) > 64)) {
            this.player = this.level.getNearestPlayer(this.getX(), this.getY(), this.getZ(), 8, EntityPredicates.NO_SPECTATORS.and(Helper::isHunter));
        }

        if (this.player != null) {
            Vector3d vec3d = new Vector3d(this.player.getX() - this.getX(), this.player.getY() + (double) this.player.getEyeHeight() / 2.0D - this.getY(), this.player.getZ() - this.getZ());
            double d1 = vec3d.lengthSqr();
            if (d1 < 64.0D) {
                double d2 = 1.0D - Math.sqrt(d1) / 8.0D;
                this.setDeltaMovement(this.getDeltaMovement().add(vec3d.normalize().scale(d2 * d2 * 0.1D)));
            }
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
        float f = 0.98F;

        if (this.onGround) {
            BlockPos underPos = new BlockPos(MathHelper.floor(this.getX()), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.getZ()));
            BlockState underState = this.level.getBlockState(underPos);
            f = underState.getBlock().getSlipperiness(underState, this.level, underPos, this) * 0.98F;
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(f, 0.9800000190734863D, f));

        if (this.onGround) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1D, -0.8999999761581421D, 1D));
        }


        this.age++;

        if (this.age >= 6000) {
            this.remove();
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound) {
        compound.putString("type", this.getVariant().name());
        compound.putInt("age", age);
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(TYPE_PARAMETER, VARIANT.NONE.name());
    }

    @Override
    protected boolean isMovementNoisy() {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {
        this.setVariant(VARIANT.valueOf(compound.getString("type")));
        this.age = compound.getInt("age");
        soulItemStack = null;//Reset item just in case a item of a different type has been created beforehand
    }

    private ItemStack createSoulItemStack() {
        if (getVariant() == VARIANT.VAMPIRE) {
            return new ItemStack(ModItems.SOUL_ORB_VAMPIRE.get());
        }
        return new ItemStack(ModItems.SOUL_ORB_VAMPIRE.get());
    }


    public enum VARIANT {
        NONE, VAMPIRE
    }
}
