package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.ItemCrossbowArrow;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nonnull;


public class EntityCrossbowArrow extends EntityArrow {

    private
    @Nonnull
    ItemStack arrowStack = new ItemStack(ModItems.crossbowArrow);

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
        this.arrowStack.stackSize = 1;
    }

    /**
     * @param heightOffset An height offset for the position the entity is created
     * @param arrow        ItemStack of the represented arrow. Is copied.
     */
    public EntityCrossbowArrow(World worldIn, EntityLivingBase shooter, double heightOffset, ItemStack arrow) {
        this(worldIn, shooter.posX, shooter.posY + (double) shooter.getEyeHeight() - 0.10000000149011612D + heightOffset, shooter.posZ, arrow);
        this.shootingEntity = shooter;

        if (shooter instanceof EntityPlayer) {
            this.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        arrowStack.readFromNBT(compound.getCompoundTag("arrowStack"));
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
        if (item instanceof ItemCrossbowArrow) {
            if (ignoreHurtTimer && living.hurtResistantTime > 0) {
                living.hurtResistantTime = 0;
            }
            ((ItemCrossbowArrow) item).onHitEntity(arrowStack, living, this, this.shootingEntity == null ? this : this.shootingEntity);
        }
    }

    @Override
    protected ItemStack getArrowStack() {
        return arrowStack;
    }
}
