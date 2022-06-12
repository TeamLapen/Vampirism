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
    private static final DataParameter<ItemStack> ITEM = EntityDataManager.defineId(ThrowableItemEntity.class, DataSerializers.ITEM_STACK);

    public ThrowableItemEntity(EntityType<? extends ThrowableItemEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public ThrowableItemEntity(World worldIn, LivingEntity thrower) {
        super(ModEntities.THROWABLE_ITEM.get(), thrower, worldIn);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        ItemStack stack = getItem();
        if (!stack.isEmpty()) {
            compound.put("thrownItem", stack.save(new CompoundNBT()));
        }
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    /**
     * @return Itemstack represented by this entity. Corresponding item is instance of {@link IVampirismThrowableItem}
     */
    public
    @Nonnull
    ItemStack getItem() {
        return this.getEntityData().get(ITEM);
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
        this.getEntityData().set(ITEM, stack);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        ItemStack stack = ItemStack.of(compound.getCompound("thrownItem"));
        if (stack.isEmpty()) {
            this.remove();
        } else {
            this.setItem(stack);
        }
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(ITEM, ItemStack.EMPTY);
    }

    protected float getGravity() {
        return 0.05F;
    }

    @Override
    protected void onHit(RayTraceResult result) {
        ItemStack stack = getItem();
        if (!stack.isEmpty()) {
            Item item = stack.getItem();
            if (item instanceof IVampirismThrowableItem) {
                ((IVampirismThrowableItem) item).onImpact(this, stack, result, this.level.isClientSide);
            } else {
                LOGGER.warn("Saved item ({}) is not an instance of IVampirismThrowableItem. This should not be able to happen", stack);
            }
        }
        if (!this.level.isClientSide) this.remove();
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
}
