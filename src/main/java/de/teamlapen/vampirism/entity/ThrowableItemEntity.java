package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.core.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

@OnlyIn(
        value = Dist.CLIENT,
        _interface = IRendersAsItem.class
)
public class ThrowableItemEntity extends ThrowableEntity implements IRendersAsItem {

    private final static Logger LOGGER = LogManager.getLogger(ThrowableItemEntity.class);
    private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(ThrowableItemEntity.class, DataSerializers.ITEMSTACK);

    public ThrowableItemEntity(EntityType<? extends ThrowableItemEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public ThrowableItemEntity(World worldIn, LivingEntity thrower) {
        super(ModEntities.throwable_item, thrower, worldIn);
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
     * Only accepts stacks of {@link IVampirismThrowableItem} tileInventory
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

    protected float getGravityVelocity() {
        return 0.05F;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerData() {
        this.getDataManager().register(ITEM, ItemStack.EMPTY);
    }

    /**
     * Has to be implemented by any item, that can be thrown using {@link ThrowableItemEntity}
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
        void onImpact(ThrowableItemEntity entity, ItemStack stack, RayTraceResult impact, boolean remote);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        ItemStack stack = getItem();
        if (!stack.isEmpty()) {
            Item item = stack.getItem();
            if (item instanceof IVampirismThrowableItem) {
                ((IVampirismThrowableItem) item).onImpact(this, stack, result, this.world.isRemote);
            } else {
                LOGGER.warn("Saved item ({}) is not an instance of IVampirismThrowableItem. This should not be able to happen", stack);
            }
        }
        if (!this.world.isRemote) this.remove();
    }
}
