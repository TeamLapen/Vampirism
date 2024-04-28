package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.core.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class ThrowableItemEntity extends ThrowableProjectile implements ItemSupplier {

    private final static Logger LOGGER = LogManager.getLogger(ThrowableItemEntity.class);
    private static final EntityDataAccessor<ItemStack> ITEM = SynchedEntityData.defineId(ThrowableItemEntity.class, EntityDataSerializers.ITEM_STACK);

    public ThrowableItemEntity(@NotNull EntityType<? extends ThrowableItemEntity> type, @NotNull Level worldIn) {
        super(type, worldIn);
    }

    public ThrowableItemEntity(@NotNull Level worldIn, @NotNull LivingEntity thrower) {
        super(ModEntities.THROWABLE_ITEM.get(), thrower, worldIn);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        ItemStack stack = getItem();
        if (!stack.isEmpty()) {
            compound.put("thrownItem", stack.save(this.registryAccess(), new CompoundTag()));
        }
    }

    /**
     * @return Itemstack represented by this entity. Corresponding item is instance of {@link IVampirismThrowableItem}
     */
    public
    @NotNull
    ItemStack getItem() {
        return this.getEntityData().get(ITEM);
    }

    /**
     * Set's the representing item stack.
     * Only accepts stacks of {@link IVampirismThrowableItem} tileInventory
     *
     * @param stack Corresponding item has to be instance of {@link IVampirismThrowableItem}
     */
    public void setItem(@NotNull ItemStack stack) {
        if (!stack.isEmpty() && !(stack.getItem() instanceof IVampirismThrowableItem)) {
            throw new IllegalArgumentException("EntityThrowable only accepts IVampirismThrowableItem, but not " + stack);
        }
        this.getEntityData().set(ITEM, stack);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        ItemStack stack = ItemStack.parseOptional(this.registryAccess(), compound.getCompound("thrownItem"));
        if (stack.isEmpty()) {
            this.discard();
        } else {
            this.setItem(stack);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ITEM, ItemStack.EMPTY);
    }

    @Override
    protected double getDefaultGravity() {
        return 0.05;
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        ItemStack stack = getItem();
        if (!stack.isEmpty()) {
            Item item = stack.getItem();
            if (item instanceof IVampirismThrowableItem) {
                ((IVampirismThrowableItem) item).onImpact(this, stack, result, this.level().isClientSide);
            } else {
                LOGGER.warn("Saved item ({}) is not an instance of IVampirismThrowableItem. This should not be able to happen", stack);
            }
        }
        if (!this.level().isClientSide) this.discard();
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
        void onImpact(ThrowableItemEntity entity, ItemStack stack, HitResult impact, boolean remote);
    }
}
