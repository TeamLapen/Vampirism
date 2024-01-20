package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.mixin.accessor.EntityAccessor;
import de.teamlapen.vampirism.mixin.accessor.WalkAnimationStateAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

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
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public Optional<T> entityCreature = Optional.empty();
    private boolean canDespawn = false;
    private final Data<T> convertibleData = new Data<>();

    public ConvertedCreatureEntity(EntityType<? extends ConvertedCreatureEntity<?>> type, Level world) {
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
        //noinspection unchecked
        this.entityCreature.ifPresent(creature -> aiStepC((EntityType<T>) creature.getType()));
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
        return this.entityCreature.map(creature -> this.getNameC(creature.getType()::getDescription)).orElseGet(super::getName);
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
        if (!level().isClientSide && this.entityCreature.isEmpty()) {
            LOGGER.debug("Setting dead, since creature is null");
            this.discard();
        }
        super.tick();
        this.tickC();
    }

    @Override
    public T cureEntity(@NotNull ServerLevel world, @NotNull PathfinderMob entity, @NotNull EntityType<T> newType) {
        return this.entityCreature.map(creature -> {
            creature.revive();
            return creature;
        }).orElseGet(() -> CurableConvertedCreature.super.cureEntity(world, entity, newType));
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.entityCreature.ifPresent(entityCreature -> {
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
        });
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
    public EntityDataAccessor<String> getSourceEntityDataParam() {
        return OVERLAY_TEXTURE;
    }


    public Optional<T> getOldCreature() {
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
            if (entityCreature.isEmpty()) {
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
            getSourceEntityDataParamOpt().ifPresent(p -> {
                getOldCreature().ifPresent(old -> this.getRepresentingEntity().getEntityData().set(p, BuiltInRegistries.ENTITY_TYPE.getKey(old.getType()).toString()));
            });
        }
    }

    @Override
    public void playAmbientSound() {
        this.entityCreature.ifPresent(Mob::playAmbientSound);
    }

    @Override
    public void refreshDimensions() {
        super.refreshDimensions();
        ((EntityAccessor) this).setEyeHeight(this.entityCreature.map(Entity::getEyeHeight).orElse(0.5f));
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
        T old = this.entityCreature.orElse(null);
        if (!Objects.equals(old, creature)) {
            this.entityCreature = Optional.ofNullable(creature);
            this.entityChanged = true;
            ((EntityAccessor) this).setDimensions(this.entityCreature.map(s -> s.dimensions).orElseGet(() -> EntityDimensions.fixed(0.5f,0.5f)));
        }
        if (this.entityCreature.isPresent() && getConvertedHandler() == null) {
            LOGGER.warn("Cannot find converting handler for converted creature {} ({})", this, this.entityCreature);
            this.entityCreature = Optional.empty();
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
    public CompoundTag writeFullUpdateToNBT() {
        CompoundTag tag = new CompoundTag();
        writeOldEntityToNBT(tag);
        return tag;
    }

    /**
     * @return The {@link de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler.IDefaultHelper} for this creature
     */
    @Nullable
    protected IConvertingHandler<?> getConvertedHandler() {
        if (entityCreature.isEmpty()) return null;
        IConvertingHandler<?> handler = this.entityCreature.map(s -> VampirismAPI.entityRegistry().getEntry(s)).map(s -> s.convertingHandler).orElse(null);
        if (handler == null) {
            LOGGER.warn("No converting handler found for {}", entityCreature.get());
        }
        return handler;
    }

    @Override
    protected @NotNull EntityType<?> getIMobTypeOpt(boolean iMob) {
        return iMob ? ModEntities.CONVERTED_CREATURE_IMOB.get() : ModEntities.CONVERTED_CREATURE.get();
    }

    @NotNull
    @Override
    protected ResourceLocation getDefaultLootTable() {
        return this.entityCreature.map(Mob::getLootTable).orElseGet(super::getDefaultLootTable);
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
        this.entityCreature.ifPresent(creature -> {
            try {
                CompoundTag entity = new CompoundTag();
                creature.revive();
                creature.save(entity);
                creature.discard();
                nbt.put("entity_old", entity);
            } catch (Exception e) {
                LOGGER.error(String.format("Failed to write old entity (%s) to NBT. If this happens more often please report this to the mod author.", creature), e);
                this.setEntityCreature(null);
            }
        });
    }

    @Override
    public boolean canBeLeashed(@NotNull Player player) {
        return true;
    }

    @Override
    public float calculateFireDamage(float amount) {
        return CurableConvertedCreature.super.calculateFireDamage(amount);
    }

    public static class IMob<T extends PathfinderMob> extends ConvertedCreatureEntity<T> implements net.minecraft.world.entity.monster.Enemy {

        public IMob(EntityType<? extends IMob<?>> type, Level world) {
            super(type, world);
        }
    }
}
