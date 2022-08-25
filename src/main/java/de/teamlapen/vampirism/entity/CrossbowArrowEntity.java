package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.items.IEntityCrossbowArrow;
import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.CrossbowArrowItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;


public class CrossbowArrowEntity extends AbstractArrow implements IEntityCrossbowArrow {

    private
    @NotNull
    ItemStack arrowStack = new ItemStack(ModItems.CROSSBOW_ARROW_NORMAL.get());
    private boolean ignoreHurtTimer = false;

    public CrossbowArrowEntity(@NotNull EntityType<? extends CrossbowArrowEntity> type, @NotNull Level world) {
        super(type, world);
    }

    public CrossbowArrowEntity(Level level, LivingEntity entity, ItemStack stack) {
        super(ModEntities.CROSSBOW_ARROW.get(), entity, level);
        this.arrowStack = stack.copy();
        this.arrowStack.setCount(1);
    }


    /**
     * @param arrow ItemStack of the represented arrow. Is copied.
     */
    public CrossbowArrowEntity(@NotNull Level worldIn, double x, double y, double z, @NotNull ItemStack arrow) {
        this(ModEntities.CROSSBOW_ARROW.get(), worldIn);
        this.setPos(x, y, z);
        this.arrowStack = arrow.copy();
        arrowStack.setCount(1);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put("arrowStack", arrowStack.save(new CompoundTag()));
    }

    public CrossbowArrowItem.EnumArrowType getArrowType() {
        return arrowStack.getItem() instanceof CrossbowArrowItem ? ((CrossbowArrowItem) arrowStack.getItem()).getType() : CrossbowArrowItem.EnumArrowType.NORMAL;
    }

    @NotNull
    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public @NotNull RandomSource getRNG() {
        return this.random;
    }

    /**
     * Allows the arrow to ignore the hurt timer of the hit entity
     */
    public void setIgnoreHurtTimer() {
        this.ignoreHurtTimer = true;
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        arrowStack.deserializeNBT(compound.getCompound("arrowStack"));
    }

    @Override
    protected void doPostHurtEffects(@NotNull LivingEntity living) {
        super.doPostHurtEffects(living);
        Item item = arrowStack.getItem();
        if (item instanceof IVampirismCrossbowArrow) {
            if (ignoreHurtTimer && living.invulnerableTime > 0) {
                living.invulnerableTime = 0;
            }
            ((IVampirismCrossbowArrow<?>) item).onHitEntity(arrowStack, living, this, getOwner());
        }
    }

    @NotNull
    @Override
    protected ItemStack getPickupItem() {
        return arrowStack;
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockRayTraceResult) { //onHitBlock
        Item item = arrowStack.getItem();
        if (item instanceof IVampirismCrossbowArrow) {
            ((IVampirismCrossbowArrow<?>) item).onHitBlock(arrowStack, (blockRayTraceResult).getBlockPos(), this, getOwner());
        }
        super.onHitBlock(blockRayTraceResult);
    }

    public void setEffectsFromItem(ItemStack p_200887_2_) {
    }
}
