package de.teamlapen.vampirism.common.world.entity;

import de.teamlapen.vampirism.common.core.ModEntities;
import de.teamlapen.vampirism.common.core.ModItems;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.HitResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class ThrowableItemEntity extends ThrowableItemProjectile {

    private final static Logger LOGGER = LogManager.getLogger(ThrowableItemEntity.class);
    private static final EntityDataAccessor<ItemStack> ITEM = SynchedEntityData.defineId(ThrowableItemEntity.class, EntityDataSerializers.ITEM_STACK);

    public ThrowableItemEntity(@NotNull EntityType<? extends ThrowableItemEntity> type, @NotNull Level worldIn) {
        super(type, worldIn);
    }

    public ThrowableItemEntity(@NotNull ServerLevel level, @NotNull LivingEntity thrower, ItemStack source) {
        super(ModEntities.THROWABLE_ITEM.get(), thrower, level, source);
        this.setOwner(thrower);
    }

    public ThrowableItemEntity(@NotNull Level worldIn, double pX, double pY, double pZ, ItemStack source) {
        super(ModEntities.THROWABLE_ITEM.get(), pX, pY, pZ, worldIn, source);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.HOLY_WATER_BOTTLE_NORMAL.get();
    }

    @Override
    public void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        ItemStack stack = getItem();
        if (!stack.isEmpty()) {
            output.store("thrownItem", ItemStack.CODEC, stack);
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        input.read("thrownItem", ItemStack.CODEC).ifPresentOrElse(this::setItem, this::discard);
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
                ((IVampirismThrowableItem) item).onImpact(this, stack, result, this.level().isClientSide());
            } else {
                LOGGER.warn("Saved item ({}) is not an instance of IVampirismThrowableItem. This should not be able to happen", stack);
            }
        }
        if (!this.level().isClientSide()) this.discard();
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
