package de.teamlapen.vampirism.entity;

import com.google.common.base.Optional;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * 1.10
 *
 * @author maxanier
 */
public class EntityThrowableItem extends EntityThrowable {

    private static final DataParameter<Optional<ItemStack>> ITEM = EntityDataManager.createKey(EntityThrowableItem.class, DataSerializers.OPTIONAL_ITEM_STACK);

    public EntityThrowableItem(World worldIn) {
        super(worldIn);
    }

    public EntityThrowableItem(World worldIn, EntityLivingBase thrower) {
        super(worldIn, thrower);
    }

    /**
     * @return Itemstack represented by this entity. Corresponding item is instance of {@link IVampirismThrowableItem}
     */
    public
    @Nullable
    ItemStack getItem() {
        return (ItemStack) ((Optional) this.getDataManager().get(ITEM)).orNull();
    }

    /**
     * Set's the representing item stack.
     * Only accepts stacks of {@link IVampirismThrowableItem} items
     *
     * @param stack Corresponding item has to be instance of {@link IVampirismThrowableItem}
     */
    public void setItem(@Nullable ItemStack stack) {
        if (stack != null && !(stack.getItem() instanceof IVampirismThrowableItem))
            throw new IllegalArgumentException("EntityThrowable only accepts IVampirismThrowableItem, but not " + stack);
        this.getDataManager().set(ITEM, Optional.fromNullable(stack));
        this.getDataManager().setDirty(ITEM);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        ItemStack stack = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("thrownItem"));
        if (stack == null) {
            this.setDead();
        } else {
            this.setItem(stack);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        ItemStack stack = getItem();
        if (stack != null) {
            compound.setTag("thrownItem", stack.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(ITEM, Optional.<ItemStack>absent());
    }

    protected float getGravityVelocity() {
        return 0.05F;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        ItemStack stack = getItem();
        if (stack != null) {
            Item item = stack.getItem();
            if (item instanceof IVampirismThrowableItem) {
                ((IVampirismThrowableItem) item).onImpact(this, stack, result, this.worldObj.isRemote);
            } else {
                VampirismMod.log.w("EntityThrowableItem", "Saved item (%s) is not an instance of IVampirismThrowableItem. This should not be able to happen", stack);
            }
        }
        if (!this.worldObj.isRemote) this.setDead();
    }

    /**
     * Has to be implemented by any item, that can be thrown using {@link EntityThrowableItem}
     */
    public interface IVampirismThrowableItem {
        /**
         * Is called when the throwable entity impacts.
         * Entity is set to dead afterwards
         *
         * @param entity The throwable entity
         * @param stack  The stack this entity is representing
         * @param impact The impact raytrace
         * @param remote If this is a remote world
         */
        void onImpact(EntityThrowableItem entity, ItemStack stack, RayTraceResult impact, boolean remote);
    }
}
