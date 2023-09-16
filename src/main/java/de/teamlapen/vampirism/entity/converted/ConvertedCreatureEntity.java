package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.BiteableEntry;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.mixin.WalkAnimationStateAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Converted creature class.
 * Contains (stores and syncs) a normal Entity for rendering purpose
 */
public class ConvertedCreatureEntity<T extends PathfinderMob> extends VampireBaseEntity implements CurableConvertedCreature<T, ConvertedCreatureEntity<T>>, ISyncable {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final EntityDataAccessor<Boolean> CONVERTING = SynchedEntityData.defineId(ConvertedCreatureEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> OVERLAY_TEXTURE = SynchedEntityData.defineId(ConvertedCreatureEntity.class, EntityDataSerializers.STRING);

    public static boolean spawnPredicate(EntityType<? extends ConvertedCreatureEntity<?>> entityType, @NotNull LevelAccessor iWorld, MobSpawnType spawnReason, @NotNull BlockPos blockPos, RandomSource random) {
        return (iWorld.getBlockState(blockPos.below()).getBlock() == Blocks.GRASS_BLOCK || iWorld.getBlockState(blockPos.below()).is(ModTags.Blocks.CURSED_EARTH)) && iWorld.getRawBrightness(blockPos, 0) > 8;
    }

    private boolean entityChanged = false;
    public T entityCreature;
    private boolean canDespawn = false;
    private final Data<T> convertibleData = new Data<>();

    public ConvertedCreatureEntity(EntityType<? extends ConvertedCreatureEntity> type, Level world) {
        super(type, world, false);
        this.enableImobConversion();
        this.xpReward = 2;
    }

    @Override
    public Data<T> data() {
        return this.convertibleData;
    }

    @Override
    public void handleEntityEventSuper(byte id) {
        super.handleEntityEvent(id);
    }

    @Override
    public InteractionResult mobInteractSuper(@NotNull Player player, @NotNull InteractionHand hand) {
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean hurtSuper(DamageSource damageSource, float amount) {
        return super.hurt(damageSource, amount);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        writeOldEntityToNBT(pCompound);
        pCompound.putBoolean("converter_canDespawn", this.canDespawn);
        this.addAdditionalSaveDataC(pCompound);
    }

    @Override
    public void aiStep() {
        aiStepC((EntityType<T>) this.entityCreature.getType());
        super.aiStep();
    }

    @Override
    public void die(@NotNull DamageSource pDamageSource) {
        super.die(pDamageSource);
        this.dieC(pDamageSource);
    }

    @Override
    public @NotNull MobType getMobType() {
        return VReference.VAMPIRE_CREATURE_ATTRIBUTE;
    }

    @Override
    public @NotNull Component getName() {
        return this.getNameC(this.entityCreature.getType()::getDescription);
    }

    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        return this.hurtC(pSource, pAmount);
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        return mobInteractC(pPlayer, pHand);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.registerConvertingData(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.registerGoalsC();
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();
        this.tickDeathC();
    }

    @Override
    public void tick() {
        if (!level().isClientSide && this.entityCreature == null) {
            LOGGER.debug("Setting dead, since creature is null");
            this.discard();
        }
        super.tick();
        this.tickC();
    }

    @Override
    public T cureEntity(@NotNull ServerLevel world, @NotNull PathfinderMob entity, @NotNull EntityType<T> newType) {
        if (this.entityCreature == null) {
            return CurableConvertedCreature.super.cureEntity(world, entity, newType);
        }
        this.entityCreature.revive();
        return this.entityCreature;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (!nil()) {
            entityCreature.copyPosition(this);
            entityCreature.zo = this.zo;
            entityCreature.yo = this.yo;
            entityCreature.xo = this.xo;
            entityCreature.yHeadRot = this.yHeadRot;
            entityCreature.xRotO = this.xRotO;
            entityCreature.yRotO = this.yRotO;
            entityCreature.yHeadRotO = this.yHeadRotO;
            entityCreature.setDeltaMovement(this.getDeltaMovement());
            entityCreature.xOld = this.xOld;
            entityCreature.yOld = this.yOld;
            entityCreature.zOld = this.zOld;
            entityCreature.hurtTime = this.hurtTime;
            entityCreature.hurtDuration = this.hurtDuration;
            entityCreature.attackAnim = this.attackAnim;
            entityCreature.oAttackAnim = this.oAttackAnim;
            ((WalkAnimationStateAccessor) entityCreature.walkAnimation).setSpeed(((WalkAnimationStateAccessor)this.walkAnimation).getSpeed());
            ((WalkAnimationStateAccessor) entityCreature.walkAnimation).setSpeedOld(((WalkAnimationStateAccessor)this.walkAnimation).getSpeedOld());
            ((WalkAnimationStateAccessor) entityCreature.walkAnimation).setPosition(((WalkAnimationStateAccessor)this.walkAnimation).getPosition());
            entityCreature.yBodyRot = this.yBodyRot;
            entityCreature.yBodyRotO = this.yBodyRotO;
            entityCreature.deathTime = this.deathTime;
        }
        if (entityChanged) {
            this.updateEntityAttributes();
            entityChanged = false;
        }
    }

    @Override
    public @NotNull EntityDataAccessor<Boolean> getConvertingDataParam() {
        return CONVERTING;
    }

    @Override
    public @NotNull EntityDataAccessor<String> getSourceEntityDataParam() {
        return OVERLAY_TEXTURE;
    }


    public T getOldCreature() {
        return this.entityCreature;
    }

    @Override
    public void loadUpdateFromNBT(@NotNull CompoundTag nbt) {
        if (nbt.contains("entity_old")) {
            //noinspection unchecked
            setEntityCreature((T) EntityType.create(nbt.getCompound("entity_old"), getCommandSenderWorld()).orElse(null));
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.readAdditionalSaveDataC(nbt);
        if (nbt.contains("entity_old")) {
            //noinspection unchecked
            setEntityCreature((T) EntityType.create(nbt.getCompound("entity_old"), level()).orElse(null));
            if (nil()) {
                LOGGER.warn("Failed to create old entity {}. Maybe the entity does not exist anymore", nbt.getCompound("entity_old"));
            }
        } else {
            LOGGER.warn("Saved entity did not have a old entity");
        }
        if (nbt.contains("converted_canDespawn")) {
            canDespawn = nbt.getBoolean("converted_canDespawn");
        }
        if (nbt.contains("ConversionTime", 99) && nbt.getInt("ConversionTime") > -1) {
            this.startConverting(nbt.hasUUID("ConversionPlayer") ? nbt.getUUID("ConversionPlayer") : null, nbt.getInt("ConversionTime"), this);
        }
        if (!nbt.contains("source_entity")) {
            this.getRepresentingEntity().getEntityData().set(this.getSourceEntityDataParam(), ForgeRegistries.ENTITY_TYPES.getKey(getOldCreature().getType()).toString());
        }
    }

    @Override
    public void playAmbientSound() {
        if (!nil()) {
            this.entityCreature.playAmbientSound();
        }
    }

    @Override
    public void refreshDimensions() {
        super.refreshDimensions();
        this.eyeHeight = this.entityCreature == null ? 0.5f : this.entityCreature.getEyeHeight();
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return super.removeWhenFarAway(distanceToClosestPlayer) && canDespawn;
    }

    /**
     * Allows the entity to despawn
     */
    public void setCanDespawn() {
        canDespawn = true;
    }

    /**
     * Set the old creature (the one before conversion)
     */
    public void setEntityCreature(@Nullable T creature) {
        if ((creature == null && this.entityCreature != null)) {
            entityChanged = true;
            this.entityCreature = null;
        } else if (creature != null) {
            if (!creature.equals(this.entityCreature)) {
                this.entityCreature = creature;
                entityChanged = true;
                this.dimensions = creature.dimensions;
            }
        }
        if (this.entityCreature != null && getConvertedHandler() == null) {
            LOGGER.warn("Cannot find converting handler for converted creature {} ({})", this, this.entityCreature);
            this.entityCreature = null;
        }
    }

    @Nullable
    @Override
    public ItemEntity spawnAtLocation(@NotNull ItemStack stack, float offsetY) {
        ItemStack actualDrop = stack;
        Item item = stack.getItem();
        if (item.isEdible()) {
            if (item.getFoodProperties(stack, this).isMeat()) {
                actualDrop = new ItemStack(Items.ROTTEN_FLESH, stack.getCount()); //Replace all meat with rotten flesh
            }
        }
        return super.spawnAtLocation(actualDrop, offsetY);
    }

    @NotNull
    @Override
    public String toString() {
        return "[" + super.toString() + " representing " + this.entityCreature + "]";
    }

    @Override
    public void writeFullUpdateToNBT(@NotNull CompoundTag nbt) {
        writeOldEntityToNBT(nbt);
    }

    /**
     * @return The {@link de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler.IDefaultHelper} for this creature
     */
    @Nullable
    protected IConvertingHandler<?> getConvertedHandler() {
        if (nil()) return null;
        BiteableEntry biteableEntry = VampirismAPI.entityRegistry().getEntry(entityCreature);
        if (biteableEntry == null) {
            LOGGER.warn("Cannot find biteable entry for {}", entityCreature);
            return null;
        }
        return biteableEntry.convertingHandler;
    }

    @Override
    protected @NotNull EntityType<?> getIMobTypeOpt(boolean iMob) {
        return iMob ? ModEntities.CONVERTED_CREATURE_IMOB.get() : ModEntities.CONVERTED_CREATURE.get();
    }

    @NotNull
    @Override
    protected ResourceLocation getDefaultLootTable() {
        if (entityCreature != null) {
            return entityCreature.getLootTable();
        }
        return super.getDefaultLootTable();
    }

    protected boolean nil() {
        return entityCreature == null;
    }

    protected void updateEntityAttributes() {
        IConvertingHandler<?> convertedHandler = getConvertedHandler();
        if (convertedHandler != null) {
            convertedHandler.updateEntityAttributes(this);
        }
    }

    /**
     * Write the old entity to nbt
     */
    private void writeOldEntityToNBT(@NotNull CompoundTag nbt) {
        if (!nil()) {
            try {
                CompoundTag entity = new CompoundTag();
                entityCreature.revive();
                entityCreature.save(entity);
                entityCreature.discard();
                nbt.put("entity_old", entity);
            } catch (Exception e) {
                LOGGER.error(String.format("Failed to write old entity (%s) to NBT. If this happens more often please report this to the mod author.", entityCreature), e);
                this.setEntityCreature(null);
            }
        }

    }

    @Override
    public boolean canBeLeashed(@NotNull Player player) {
        return true;
    }

    @Override
    public float calculateFireDamage(float amount) {
        return CurableConvertedCreature.super.calculateFireDamage(amount);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class IMob extends ConvertedCreatureEntity implements net.minecraft.world.entity.monster.Enemy {

        public IMob(EntityType<? extends ConvertedCreatureEntity> type, Level world) {
            super(type, world);
        }
    }
}
