package de.teamlapen.vampirism.common.world.entity;

import de.teamlapen.vampirism.api.world.items.IEntityQuarrel;
import de.teamlapen.vampirism.api.world.items.IVampirismQuarrel;
import de.teamlapen.vampirism.api.world.items.QuarrelProperties;
import de.teamlapen.vampirism.common.core.ModAttachments;
import de.teamlapen.vampirism.common.core.ModEntities;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.world.items.crossbow.QuarrelItem;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class QuarrelEntity extends AbstractArrow implements IEntityQuarrel {

    public static final String TAG_GRAVITY_FACTOR = "GravityFactor";
    public static final String TAG_ARROW_STACK = "ArrowStack";

    private static final double MIN_PRECISION_HORIZONTAL_SPEED = 1.0;

    private static final int MAX_RAPID_HITS = 3;
    private static final int RAPID_HIT_WINDOW = 10;

    private static final int CHUNK_LOADING_RADIUS = 2;

    private static final EntityDataAccessor<Float> GRAVITY_FACTOR = SynchedEntityData.defineId(QuarrelEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<ItemStack> ARROW_STACK = SynchedEntityData.defineId(QuarrelEntity.class, EntityDataSerializers.ITEM_STACK);

    private boolean ignoreHurtTimer = false;

    public QuarrelEntity(EntityType<? extends QuarrelEntity> type, Level level) {
        super(type, level);
    }

    public QuarrelEntity(Level level, LivingEntity entity, ItemStack stack, ItemStack weapon) {
        super(ModEntities.QUARREL.get(), entity, level, stack, weapon);
        setArrowStack(stack);
    }

    /**
     * @param arrow ItemStack of the represented arrow. Is copied.
     */
    public QuarrelEntity(Level level, double x, double y, double z, ItemStack arrow, ItemStack weapon) {
        super(ModEntities.QUARREL.get(), x, y, z, level, arrow, weapon);
        this.setPos(x, y, z);
        setArrowStack(arrow);
    }

    private void setArrowStack(ItemStack stack) {
        this.entityData.set(ARROW_STACK, stack.copyWithCount(1));
    }

    public Optional<IVampirismQuarrel.IQuarrelBehavior> getBehavior() {
        return Optional.ofNullable(getPickupItem().getItem() instanceof QuarrelItem quarrel ? quarrel.getBehavior() : null);
    }

    public QuarrelProperties properties() {
        return getBehavior().map(IVampirismQuarrel.IQuarrelBehavior::properties).orElse(QuarrelProperties.DEFAULT);
    }

    @Override
    public void tick() {
        super.tick();
        if (isAlive() && level() instanceof ServerLevel serverLevel && properties().forcesChunkLoading()) {
            serverLevel.getChunkSource().addTicketWithRadius(TicketType.ENDER_PEARL, chunkPosition(), CHUNK_LOADING_RADIUS);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(GRAVITY_FACTOR, 1.0f);
        builder.define(ARROW_STACK, ModItems.QUARREL_NORMAL.toStack());
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
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putFloat(TAG_GRAVITY_FACTOR, getGravityFactor());
        output.store(TAG_ARROW_STACK, ItemStack.CODEC, getPickupItem());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        setGravityFactor(input.getFloatOr(TAG_GRAVITY_FACTOR, 1.0f));
        setArrowStack(input.read(TAG_ARROW_STACK, ItemStack.CODEC).orElse(ModItems.QUARREL_NORMAL.toStack()));
    }

    /**
     * Allows the arrow to ignore the hurt timer of the hit entity
     */
    public void setIgnoreHurtTimer() {
        this.ignoreHurtTimer = true;
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        Entity hit = hitResult.getEntity();
        if (properties().damageMultiplier() <= 0f) {
            if (!level().isClientSide() && hit instanceof LivingEntity target) {
                ItemStack arrow = getPickupItem();
                if (arrow.getItem() instanceof IVampirismQuarrel<?> quarrel) {
                    quarrel.onHitEntity(arrow, target, this, getOwner());
                }
            }
            return;
        }

        if (this.ignoreHurtTimer && !level().isClientSide() && hit instanceof LivingEntity living && living.getData(ModAttachments.QUARREL_HURT_BYPASS).registerHit(living.level().getGameTime())) {
            living.invulnerableTime = 0;
        }

        float knockbackFactor = properties().knockbackMultiplier();
        Vec3 preHitVelocity = knockbackFactor != 1f && hit instanceof LivingEntity ? hit.getDeltaMovement() : null;

        super.onHitEntity(hitResult);

        if (preHitVelocity != null && hit.isAlive()) {
            Vec3 imparted = hit.getDeltaMovement().subtract(preHitVelocity);
            hit.setDeltaMovement(preHitVelocity.add(imparted.scale(knockbackFactor)));
            hit.hurtMarked = true; // resync the corrected velocity to clients
        }
    }

    @Override
    protected void doPostHurtEffects(LivingEntity mob) {
        super.doPostHurtEffects(mob);
        ItemStack arrow = getPickupItem();
        if (arrow.getItem() instanceof IVampirismQuarrel<?> quarrel) {
            quarrel.onHitEntity(arrow, mob, this, getOwner());
        }
    }

    @Override
    public ItemStack getPickupItem() {
        return this.entityData.get(ARROW_STACK).copy();
    }

    @Override
    public ItemStack getDefaultPickupItem() {
        return ModItems.QUARREL_NORMAL.toStack();
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        ItemStack arrow = getPickupItem();
        if (arrow.getItem() instanceof IVampirismQuarrel<?> quarrel) {
            quarrel.onHitBlock(arrow, hitResult.getBlockPos(), this, getOwner(), hitResult.getDirection());
        }
        super.onHitBlock(hitResult);
    }

    public static final class HurtBypassTracker {

        private int count;
        private long lastTick;

        boolean registerHit(long gameTime) {
            if (gameTime - this.lastTick > RAPID_HIT_WINDOW) {
                this.count = 0;
            }
            this.lastTick = gameTime;
            if (this.count >= MAX_RAPID_HITS) {
                return false;
            }
            this.count++;
            return true;
        }
    }
}
