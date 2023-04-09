package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.BiteableEntry;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.api.entity.convertible.ICurableConvertedCreature;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.entity.ai.goals.AttackMeleeNoSunGoal;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
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

import java.util.UUID;

/**
 * Converted creature class.
 * Contains (stores and syncs) a normal Entity for rendering purpose
 */
public class ConvertedCreatureEntity<T extends PathfinderMob> extends VampireBaseEntity implements ICurableConvertedCreature<T>, ISyncable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final EntityDataAccessor<Boolean> CONVERTING = SynchedEntityData.defineId(ConvertedCreatureEntity.class, EntityDataSerializers.BOOLEAN);


    public static boolean spawnPredicate(EntityType<? extends ConvertedCreatureEntity<?>> entityType, @NotNull LevelAccessor iWorld, MobSpawnType spawnReason, @NotNull BlockPos blockPos, RandomSource random) {
        return (iWorld.getBlockState(blockPos.below()).getBlock() == Blocks.GRASS_BLOCK || iWorld.getBlockState(blockPos.below()).is(ModTags.Blocks.CURSED_EARTH)) && iWorld.getRawBrightness(blockPos, 0) > 8;
    }

    private @Nullable T entityCreature;
    private boolean entityChanged = false;
    private boolean canDespawn = false;
    @Nullable
    private Component name;
    private int conversionTime;
    private @Nullable UUID conversationStarter;

    public ConvertedCreatureEntity(EntityType<? extends ConvertedCreatureEntity> type, Level world) {
        super(type, world, false);
        this.enableImobConversion();
        this.xpReward = 2;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        writeOldEntityToNBT(nbt);
        nbt.putBoolean("converter_canDespawn", canDespawn);
        nbt.putInt("ConversionTime", this.isConverting(this) ? this.conversionTime : -1);
        if (this.conversationStarter != null) {
            nbt.putUUID("ConversionPlayer", this.conversationStarter);
        }
    }

    @Override
    public void aiStep() {
        if (!this.level().isClientSide && this.isAlive() && this.isConverting(this)) {
            --this.conversionTime;
            if (this.conversionTime <= 0 && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(this, EntityType.VILLAGER, (timer) -> this.conversionTime = timer)) {
                //noinspection unchecked
                this.cureEntity((ServerLevel) this.level(), this, ((EntityType<T>) entityCreature.getType()));
            }
        }
        super.aiStep();
    }

    @Override
    public T createCuredEntity(PathfinderMob entity, EntityType<T> newType) {
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
    public EntityDataAccessor<Boolean> getConvertingDataParam() {
        return CONVERTING;
    }

    @Override
    protected @NotNull Component getTypeName() {
        if (name == null) {
            this.name = Component.translatable("entity.vampirism.vampire").append(" ").append((nil() ? super.getTypeName() : entityCreature.getName()));
        }
        return name;
    }

    public T getOldCreature() {
        return entityCreature;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (!handleSound(id, this)) {
            super.handleEntityEvent(id);
        }
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
    }

    @Override
    public void playAmbientSound() {
        if (!nil()) {
            entityCreature.playAmbientSound();
        }
    }

    @Override
    public void refreshDimensions() {
        super.refreshDimensions();
        this.eyeHeight = entityCreature == null ? 0.5f : entityCreature.getEyeHeight();
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
        if ((creature == null && entityCreature != null)) {
            entityChanged = true;
            entityCreature = null;
        } else if (creature != null) {
            if (!creature.equals(entityCreature)) {
                entityCreature = creature;
                entityChanged = true;
                this.dimensions = creature.dimensions;
            }
        }
        if (entityCreature != null && getConvertedHelper() == null) {
            LOGGER.warn("Cannot find converting handler for converted creature {} ({})", this, entityCreature);
            entityCreature = null;
        }
    }

    @Override
    public void startConverting(@Nullable UUID conversionStarterIn, int conversionTimeIn, @NotNull PathfinderMob entity) {
        ICurableConvertedCreature.super.startConverting(conversionStarterIn, conversionTimeIn, entity);
        this.conversationStarter = conversionStarterIn;
        this.conversionTime = conversionTimeIn;
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
        return "[" + super.toString() + " representing " + entityCreature + "]";
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && entityCreature == null) {
            LOGGER.debug("Setting dead, since creature is null");
            this.discard();
        }
    }

    @Override
    public void writeFullUpdateToNBT(@NotNull CompoundTag nbt) {
        writeOldEntityToNBT(nbt);

    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.registerConvertingData(this);
    }

    /**
     * @return The {@link de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler.IDefaultHelper} for this creature
     */
    @Nullable
    protected IConvertingHandler.IDefaultHelper getConvertedHelper() {
        if (nil()) return null;
        BiteableEntry biteableEntry = VampirismAPI.entityRegistry().getEntry(entityCreature);
        if (biteableEntry == null) {
            LOGGER.warn("Cannot find biteable entry for {}", entityCreature);
            return null;
        }
        IConvertingHandler<?> handler = biteableEntry.convertingHandler;
        if (handler instanceof DefaultConvertingHandler) {
            return ((DefaultConvertingHandler<?>) handler).getHelper();
        }
        return null;
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

    @NotNull
    @Override
    protected InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == Items.GOLDEN_APPLE){
            return interactWithCureItem(player, stack, this);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, PathfinderMob.class, 10, 1.0, 1.1, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, VReference.HUNTER_FACTION)));
        //this.tasks.addTask(3, new FleeSunVampireGoal(this, 1F));
        this.goalSelector.addGoal(4, new RestrictSunGoal(this));
        this.goalSelector.addGoal(5, new AttackMeleeNoSunGoal(this, 0.9D, false));
        this.xpReward = 2;

        this.goalSelector.addGoal(11, new RandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(13, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(15, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, true, false, null)));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PathfinderMob.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)));
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    protected void updateEntityAttributes() {
        IConvertingHandler.IDefaultHelper helper = getConvertedHelper();
        try {
            if (helper != null) {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(helper.getConvertedDMG((EntityType<? extends PathfinderMob>) entityCreature.getType(), entityCreature.getRandom()));
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(helper.getConvertedMaxHealth((EntityType<? extends PathfinderMob>) entityCreature.getType(), entityCreature.getRandom()));
                this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(helper.getConvertedKnockbackResistance((EntityType<? extends PathfinderMob>) entityCreature.getType(), entityCreature.getRandom()));
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(helper.getConvertedSpeed((EntityType<? extends PathfinderMob>) entityCreature.getType(), entityCreature.getRandom()));
            } else {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20);
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(0);
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0);
            }
        } catch (NullPointerException e) {
            LOGGER.error("Failed to update entity attributes for {} {}", this, e);
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

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class IMob extends ConvertedCreatureEntity implements net.minecraft.world.entity.monster.Enemy {

        public IMob(EntityType<? extends ConvertedCreatureEntity> type, Level world) {
            super(type, world);
        }
    }
}
