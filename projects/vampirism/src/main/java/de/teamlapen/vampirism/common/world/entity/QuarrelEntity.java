package de.teamlapen.vampirism.common.world.entity;

import de.teamlapen.vampirism.api.world.items.IEntityQuarrel;
import de.teamlapen.vampirism.api.world.items.IVampirismQuarrel;
import de.teamlapen.vampirism.common.core.ModEntities;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.world.items.QuarrelItem;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class QuarrelEntity extends AbstractArrow implements IEntityQuarrel {

    @NotNull
    private ItemStack arrowStack = new ItemStack(ModItems.QUARREL_NORMAL.get());
    private boolean ignoreHurtTimer = false;

    public QuarrelEntity(@NotNull EntityType<? extends QuarrelEntity> type, @NotNull Level world) {
        super(type, world);
    }

    public QuarrelEntity(Level level, LivingEntity entity, ItemStack stack, ItemStack weapon) {
        super(ModEntities.QUARREL.get(), entity, level, stack, weapon);
        this.arrowStack = stack.copy();
        this.arrowStack.setCount(1);
    }


    /**
     * @param arrow ItemStack of the represented arrow. Is copied.
     */
    public QuarrelEntity(@NotNull Level worldIn, double x, double y, double z, @NotNull ItemStack arrow, ItemStack weapon) {
        super(ModEntities.QUARREL.get(), x, y, z, worldIn, arrow, weapon);
        this.setPos(x, y, z);
        this.arrowStack = arrow.copy();
        arrowStack.setCount(1);
    }

    @Nullable
    public IVampirismQuarrel.IQuarrelBehavior getArrowType() {
        return getPickupItem().getItem() instanceof QuarrelItem ? ((QuarrelItem) getPickupItem().getItem()).getBehavior() : null;
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
    protected void doPostHurtEffects(@NotNull LivingEntity living) {
        super.doPostHurtEffects(living);
        Item item = arrowStack.getItem();
        if (item instanceof IVampirismQuarrel) {
            if (ignoreHurtTimer && living.invulnerableTime > 0) {
                living.invulnerableTime = 0;
            }
            ((IVampirismQuarrel<?>) item).onHitEntity(arrowStack, living, this, getOwner());
        }
    }

    @NotNull
    @Override
    protected ItemStack getPickupItem() {
        return arrowStack;
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return ModItems.QUARREL_NORMAL.toStack();
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockRayTraceResult) { //onHitBlock
        Item item = arrowStack.getItem();
        if (item instanceof IVampirismQuarrel) {
            ((IVampirismQuarrel<?>) item).onHitBlock(arrowStack, (blockRayTraceResult).getBlockPos(), this, getOwner(), blockRayTraceResult.getDirection());
        }
        super.onHitBlock(blockRayTraceResult);
    }

    @Override
    public void shoot(double pX, double pY, double pZ, float pVelocity, float pInaccuracy) {
        super.shoot(pX, pY, pZ, pVelocity, pInaccuracy);
    }
}
