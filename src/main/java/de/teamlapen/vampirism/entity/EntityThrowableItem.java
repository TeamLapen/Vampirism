package de.teamlapen.vampirism.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

/**
 * 1.10
 *
 * @author maxanier
 */
public class EntityThrowableItem extends ThrowableEntity {

    private final static Logger LOGGER = LogManager.getLogger(EntityThrowableItem.class);
    private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(EntityThrowableItem.class, DataSerializers.ITEM_STACK);

    public EntityThrowableItem(EntityType<? extends EntityThrowableItem> type, World worldIn) {
        super(type, worldIn);
    }

    public EntityThrowableItem(EntityType<? extends EntityThrowableItem> type, World worldIn, LivingEntity thrower) {
        super(type, thrower, worldIn);
    }

    /**
     * @return Itemstack represented by this entity. Corresponding item is instance of {@link IVampirismThrowableItem}
     */
    public
    @Nonnull
    ItemStack getItem() {
        return this.getDataManager().get(ITEM);
    }

    /**
     * Set's the representing item stack.
     * Only accepts stacks of {@link IVampirismThrowableItem} items
     *
     * @param stack Corresponding item has to be instance of {@link IVampirismThrowableItem}
     */
    public void setItem(@Nonnull ItemStack stack) {
        if (!stack.isEmpty() && !(stack.getItem() instanceof IVampirismThrowableItem))
            throw new IllegalArgumentException("EntityThrowable only accepts IVampirismThrowableItem, but not " + stack);
        this.getDataManager().set(ITEM, stack);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        ItemStack stack = ItemStack.read(compound.getCompound("thrownItem"));
        if (stack.isEmpty()) {
            this.remove();
        } else {
            this.setItem(stack);
        }
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        ItemStack stack = getItem();
        if (!stack.isEmpty()) {
            compound.put("thrownItem", stack.write(new CompoundNBT()));
        }
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.getDataManager().register(ITEM, ItemStack.EMPTY);
    }

    protected float getGravityVelocity() {
        return 0.05F;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        ItemStack stack = getItem();
        if (!stack.isEmpty()) {
            Item item = stack.getItem();
            if (item instanceof IVampirismThrowableItem) {
                ((IVampirismThrowableItem) item).onImpact(this, stack, result, this.world.isRemote);
            } else {
                LOGGER.warn("Saved item (%s) is not an instance of IVampirismThrowableItem. This should not be able to happen", stack);
            }
        }
        if (!this.world.isRemote) this.remove();
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
