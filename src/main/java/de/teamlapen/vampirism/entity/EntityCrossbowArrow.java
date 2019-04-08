package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.items.IVampirismCrossbowArror;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import javax.annotation.Nonnull;
import java.util.Random;


public class EntityCrossbowArrow extends EntityArrow {

    /**
     * Create a entity arrow for a shooting entity (with offset)
     *
     * @param heightOffset An height offset for the position the entity is created
     * @param rightHanded  If the entiy is right handed
     * @param arrow        ItemStack of the represented arrow. Is copied.
     * @param centerOffset An offset from the center of the entity
     */
    public static EntityCrossbowArrow createWithShooter(World world, EntityLivingBase shooter, double heightOffset, double centerOffset, boolean rightHanded, ItemStack arrow) {
        double yaw = ((shooter.rotationYaw - 90)) / 180 * Math.PI;
        if (rightHanded) {
            yaw += Math.PI;
        }
        double posX = shooter.posX - Math.sin(yaw) * centerOffset;
        double posZ = shooter.posZ + Math.cos(yaw) * centerOffset;
        EntityCrossbowArrow entityArrow = new EntityCrossbowArrow(world, posX, shooter.posY + (double) shooter.getEyeHeight() - 0.10000000149011612D + heightOffset, posZ, arrow);
        entityArrow.shootingEntity = shooter;
        if (shooter instanceof EntityPlayer) {
            entityArrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
        }
        return entityArrow;
    }

    private
    @Nonnull
    ItemStack arrowStack = new ItemStack(ModItems.crossbow_arrow);
    private boolean ignoreHurtTimer = false;

    public EntityCrossbowArrow(World world) {
        super(world);
    }


    /**
     * @param arrow ItemStack of the represented arrow. Is copied.
     */
    public EntityCrossbowArrow(World worldIn, double x, double y, double z, ItemStack arrow) {
        this(worldIn);
        this.setPosition(x, y, z);
        this.arrowStack = arrow.copy();
        arrowStack.setCount(1);
    }

    public Random getRNG() {
        return this.rand;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        arrowStack.deserializeNBT(compound.getCompoundTag("arrowStack"));
    }

    /**
     * Allows the arrow to ignore the hurt timer of the hit entity
     */
    public void setIgnoreHurtTimer() {
        this.ignoreHurtTimer = true;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setTag("arrowStack", arrowStack.writeToNBT(new NBTTagCompound()));
    }

    @Override
    protected void arrowHit(EntityLivingBase living) {
        super.arrowHit(living);
        Item item = arrowStack.getItem();
        if (item instanceof IVampirismCrossbowArror) {
            if (ignoreHurtTimer && living.hurtResistantTime > 0) {
                living.hurtResistantTime = 0;
            }
            ((IVampirismCrossbowArror) item).onHitEntity(arrowStack, living, this, this.shootingEntity == null ? this : this.shootingEntity);
        }
    }

    @Override
    protected ItemStack getArrowStack() {
        return arrowStack;
    }

    @Override
    protected void onHit(RayTraceResult raytraceResultIn) {
        if (raytraceResultIn.entityHit == null) {
            Item item = arrowStack.getItem();
            if (item instanceof IVampirismCrossbowArror) {
                ((IVampirismCrossbowArror) item).onHitBlock(arrowStack, raytraceResultIn.getBlockPos(), this, this.shootingEntity == null ? this : this.shootingEntity);
            }
        }
        super.onHit(raytraceResultIn);
    }
}
