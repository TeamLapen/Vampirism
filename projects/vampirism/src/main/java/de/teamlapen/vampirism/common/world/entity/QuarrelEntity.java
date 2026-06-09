package de.teamlapen.vampirism.common.world.entity;

import de.teamlapen.vampirism.api.world.items.IEntityQuarrel;
import de.teamlapen.vampirism.api.world.items.IVampirismQuarrel;
import de.teamlapen.vampirism.common.core.ModEntities;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.world.items.crossbow.QuarrelItem;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuarrelEntity extends AbstractArrow implements IEntityQuarrel {

    public static final String TAG_GRAVITY_FACTOR = "GravityFactor";

    private static final double MIN_PRECISION_HORIZONTAL_SPEED = 1.0;

    private static final EntityDataAccessor<Float> GRAVITY_FACTOR = SynchedEntityData.defineId(QuarrelEntity.class, EntityDataSerializers.FLOAT);

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
        this.arrowStack.setCount(1);
    }

    @Nullable
    public IVampirismQuarrel.IQuarrelBehavior getArrowType() {
        return getPickupItem().getItem() instanceof QuarrelItem ? ((QuarrelItem) getPickupItem().getItem()).getBehavior() : null;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(GRAVITY_FACTOR, 1.0f);
    }

    /**
     * @param factor multiplier applied to the arrow's gravity (1 = normal, 0 = none)
     */
    public void setGravityFactor(float factor) {
        this.entityData.set(GRAVITY_FACTOR, factor);
    }

    public float getGravityFactor() {
        return this.entityData.get(GRAVITY_FACTOR);
    }

    @Override
    protected double getDefaultGravity() {
        return super.getDefaultGravity() * getEffectiveGravityFactor();
    }

    private float getEffectiveGravityFactor() {
        float factor = getGravityFactor();
        if (factor >= 1.0f) {
            return factor;
        }
        Vec3 movement = getDeltaMovement();
        double horizontalSpeed = Math.sqrt(movement.x * movement.x + movement.z * movement.z);
        return horizontalSpeed >= MIN_PRECISION_HORIZONTAL_SPEED ? factor : 1.0f;
    }

    @Override
    protected void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putFloat(TAG_GRAVITY_FACTOR, getGravityFactor());
    }

    @Override
    protected void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        setGravityFactor(input.getFloatOr(TAG_GRAVITY_FACTOR, 1.0f));
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
