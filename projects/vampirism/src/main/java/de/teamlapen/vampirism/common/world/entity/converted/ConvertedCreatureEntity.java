package de.teamlapen.vampirism.common.world.entity.converted;

import de.teamlapen.vampirism.api.VampirismApi;
import de.teamlapen.vampirism.api.world.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.common.core.ModEntities;
import de.teamlapen.vampirism.common.tags.ModBlockTags;
import de.teamlapen.vampirism.common.world.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.misc.mixin.accessor.EntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootTable;
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
public class ConvertedCreatureEntity<T extends PathfinderMob> extends VampireBaseEntity implements CurableConvertedCreature<T, ConvertedCreatureEntity<T>> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final EntityDataAccessor<Boolean> CONVERTING = SynchedEntityData.defineId(ConvertedCreatureEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> OVERLAY_TEXTURE = SynchedEntityData.defineId(ConvertedCreatureEntity.class, EntityDataSerializers.STRING);

    public static boolean spawnPredicate(EntityType<? extends ConvertedCreatureEntity<?>> entityType, @NotNull LevelAccessor iWorld, EntitySpawnReason spawnReason, @NotNull BlockPos blockPos, RandomSource random) {
        return (iWorld.getBlockState(blockPos.below()).getBlock() == Blocks.GRASS_BLOCK || iWorld.getBlockState(blockPos.below()).is(ModBlockTags.CURSED_EARTH)) && iWorld.getRawBrightness(blockPos, 0) > 8;
    }

    private boolean entityChanged = false;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public Optional<T> entityCreature = Optional.empty();
    private boolean canDespawn = false;
    private final Data<T> convertibleData = new Data<>();

    public ConvertedCreatureEntity(EntityType<? extends ConvertedCreatureEntity<?>> type, Level world) {
        super(type, world);
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
    public boolean hurtSuper(ServerLevel level, DamageSource damageSource, float amount) {
        return super.hurtServer(level, damageSource, amount);
    }

    @Override
    public void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        writeOldEntityToNBT(output);
        output.putBoolean("converter_canDespawn", this.canDespawn);
        this.addAdditionalSaveDataC(output);
    }

    @Override
    public void aiStep() {
        if (this.level() instanceof ServerLevel serverLevel) {
            //noinspection unchecked
            this.entityCreature.ifPresent(creature -> aiStepC(serverLevel));
        }
        super.aiStep();
    }

    @Override
    public void die(@NotNull DamageSource pDamageSource) {
        super.die(pDamageSource);
        this.dieC(pDamageSource);
    }

    @Override
    public @NotNull Component getTypeName() {
        return this.entityCreature.map(creature -> this.getNameC(creature.getType()::getDescription)).orElseGet(super::getTypeName);
    }

    @Override
    public boolean hurtServer(ServerLevel level, @NotNull DamageSource pSource, float pAmount) {
        return this.hurtC(level, pSource, pAmount);
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        return mobInteractC(pPlayer, pHand);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        this.registerConvertingData(builder);
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
        if (!level().isClientSide() && this.entityCreature.isEmpty()) {
            LOGGER.debug("Setting dead, since creature is null");
            this.discard();
        }
        super.tick();
        this.tickC();
    }

    @Override
    public T cureEntity(@NotNull ServerLevel world, @NotNull PathfinderMob entity) {
        return this.entityCreature.map(creature -> {
            creature.revive();
            return creature;
        }).orElseGet(() -> CurableConvertedCreature.super.cureEntity(world, entity));
    }

    @Override
    public @NotNull EntityType<T> getCuredEntityType() {
        return this.entityCreature.map(creature -> (EntityType<T>) creature.getType()).orElseGet( ()->{
            LOGGER.warn("Trying to cure a ConvertedCreatureEntity with an empty wrapped entityCreature. Returning dummy entity. Might cause problems.");
            return (EntityType<T>) (this.getType());
        });
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
            entityCreature.walkAnimation.speed(this.walkAnimation.speed());
            entityCreature.walkAnimation.oldSpeed(this.walkAnimation.oldSpeed());
            entityCreature.walkAnimation.position(this.walkAnimation.position());
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
    public void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        this.readAdditionalSaveDataC(input);
        input.child("entity_old").ifPresentOrElse(old -> {
            setEntityCreature((T) EntityType.create(old, level(), EntitySpawnReason.LOAD).orElse(null));
            if (entityCreature.isEmpty()) {
                LOGGER.warn("Failed to create old entity {}. Maybe the entity does not exist anymore", old);
            }
        }, () -> LOGGER.warn("Saved entity did not have a old entity"));


        this.canDespawn = input.getBooleanOr("converted_canDespawn", true);
        input.getInt("ConversionTime").filter(time -> time > -1).ifPresent(time -> this.startConverting(input.read("ConversionPlayer", UUIDUtil.CODEC).orElse(null), time, this));
        if (input.child("source_entity").isEmpty()) {
            getSourceEntityDataParamOpt().ifPresent(p -> getOldCreature().ifPresent(old -> this.asEntity().getEntityData().set(p, BuiltInRegistries.ENTITY_TYPE.getKey(old.getType()).toString())));
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
            ((EntityAccessor) this).setDimensions(this.entityCreature.map(s -> s.dimensions).orElseGet(() -> EntityDimensions.fixed(0.5f, 0.5f)));
        }
        if (this.entityCreature.isPresent() && getConvertedHandler() == null) {
            LOGGER.warn("Cannot find converting handler for converted creature {} ({})", this, this.entityCreature);
            this.entityCreature = Optional.empty();
        }
    }

    @Nullable
    @Override
    public ItemEntity spawnAtLocation(ServerLevel level, @NotNull ItemStack stack, float offsetY) {
        ItemStack actualDrop = stack;
        if (stack.is(ItemTags.MEAT)) {
            actualDrop = new ItemStack(Items.ROTTEN_FLESH, stack.getCount()); //Replace all meat with rotten flesh
        }
        return super.spawnAtLocation(level, actualDrop, offsetY);
    }

    @NotNull
    @Override
    public String toString() {
        return "[" + super.toString() + " representing " + this.entityCreature + "]";
    }

    /**
     * @return The {@link IConvertingHandler.IDefaultHelper} for this creature
     */
    @Nullable
    protected IConvertingHandler<?> getConvertedHandler() {
        if (entityCreature.isEmpty()) return null;
        IConvertingHandler<?> handler = this.entityCreature.map(s -> VampirismApi.services().entityRegistry().getConverterEntry(s)).map(s -> s.converter().createHandler(s.overlay().orElse(null))).orElse(null);
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
    public Optional<ResourceKey<LootTable>> getLootTable() {
        return this.entityCreature.flatMap(Mob::getLootTable).or(super::getLootTable);
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
    private void writeOldEntityToNBT(@NotNull ValueOutput output) {
        this.entityCreature.ifPresent(creature -> {
            try {
                creature.revive();
                creature.save(output.child("entity_old"));
                creature.discard();
            } catch (Exception e) {
                LOGGER.error("Failed to write old entity ({}) to NBT. If this happens more often please report this to the mod author.", creature, e);
                this.setEntityCreature(null);
            }
        });
    }

    @Override
    public boolean canBeLeashed() {
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
