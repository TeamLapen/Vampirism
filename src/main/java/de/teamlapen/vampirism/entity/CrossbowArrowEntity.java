package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.items.IEntityCrossbowArrow;
import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Random;


public class CrossbowArrowEntity extends AbstractArrowEntity implements IEntityCrossbowArrow {

    /**
     * Create a entity arrow for a shooting entity (with offset)
     *
     * @param heightOffset An height offset for the position the entity is created
     * @param rightHanded  If the entiy is right handed
     * @param arrow        ItemStack of the represented arrow. Is copied.
     * @param centerOffset An offset from the center of the entity
     */
    public static CrossbowArrowEntity createWithShooter(World world, LivingEntity shooter, double heightOffset, double centerOffset, boolean rightHanded, ItemStack arrow) {
        double yaw = ((shooter.rotationYaw - 90)) / 180 * Math.PI;
        if (rightHanded) {
            yaw += Math.PI;
        }
        double posX = shooter.posX - Math.sin(yaw) * centerOffset;
        double posZ = shooter.posZ + Math.cos(yaw) * centerOffset;
        CrossbowArrowEntity entityArrow = new CrossbowArrowEntity(world, posX, shooter.posY + (double) shooter.getEyeHeight() - 0.10000000149011612D + heightOffset, posZ, arrow);
        entityArrow.shootingEntity = shooter.getUniqueID();
        if (shooter instanceof PlayerEntity) {
            entityArrow.pickupStatus = AbstractArrowEntity.PickupStatus.ALLOWED;
        }
        return entityArrow;
    }

    private
    @Nonnull
    ItemStack arrowStack = new ItemStack(ModItems.crossbow_arrow_normal);
    private boolean ignoreHurtTimer = false;

    public CrossbowArrowEntity(EntityType<? extends CrossbowArrowEntity> type, World world) {
        super(type, world);
    }


    /**
     * @param arrow ItemStack of the represented arrow. Is copied.
     */
    public CrossbowArrowEntity(World worldIn, double x, double y, double z, ItemStack arrow) {
        this(ModEntities.crossbow_arrow, worldIn);
        this.setPosition(x, y, z);
        this.arrowStack = arrow.copy();
        arrowStack.setCount(1);
    }

    public Random getRNG() {
        return this.rand;
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        arrowStack.deserializeNBT(compound.getCompound("arrowStack"));
    }

    /**
     * Allows the arrow to ignore the hurt timer of the hit entity
     */
    public void setIgnoreHurtTimer() {
        this.ignoreHurtTimer = true;
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.put("arrowStack", arrowStack.write(new CompoundNBT()));
    }

    @Override
    protected void arrowHit(LivingEntity living) {
        super.arrowHit(living);
        Item item = arrowStack.getItem();
        if (item instanceof IVampirismCrossbowArrow) {
            if (ignoreHurtTimer && living.hurtResistantTime > 0) {
                living.hurtResistantTime = 0;
            }
            ((IVampirismCrossbowArrow) item).onHitEntity(arrowStack, living, this, this.shootingEntity == null ? this : this.world instanceof ServerWorld ? ((ServerWorld) this.world).getEntityByUuid(this.shootingEntity) : null); //TODO nonnull server only
        }
    }

    @Override
    protected ItemStack getArrowStack() {
        return arrowStack;
    }

    @Override
    protected void onHit(RayTraceResult raytraceResultIn) {
        if (raytraceResultIn.getType() == RayTraceResult.Type.BLOCK) {
            Item item = arrowStack.getItem();
            if (item instanceof IVampirismCrossbowArrow) {
                ((IVampirismCrossbowArrow) item).onHitBlock(arrowStack, ((BlockRayTraceResult) raytraceResultIn).getPos(), this, this.shootingEntity == null ? this : this.world instanceof ServerWorld ? ((ServerWorld) this.world).getEntityByUuid(this.shootingEntity) : null);//TODO nonnull server only
            }
        }
        super.onHit(raytraceResultIn);
    }
}
