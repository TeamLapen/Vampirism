package de.teamlapen.factions.common.factions.minions;

import de.teamlapen.factions.api.factions.lord.ILordPlayer;
import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.api.world.entities.minion.IMinionEntity;
import de.teamlapen.factions.api.world.entities.minion.IMinionInventory;
import de.teamlapen.factions.api.world.entities.minion.IMinionTask;
import de.teamlapen.factions.common.Permissions;
import de.teamlapen.factions.common.components.FactionRestriction;
import de.teamlapen.factions.common.core.FactionEntities;
import de.teamlapen.factions.common.core.FactionMinionTasks;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import de.teamlapen.factions.common.util.PlayerSkinHelper;
import de.teamlapen.factions.common.world.attachments.LevelDamage;
import de.teamlapen.factions.common.world.entities.EntitySyncHolder;
import de.teamlapen.factions.common.world.entities.ForceLookEntityGoal;
import de.teamlapen.factions.common.world.entities.IPlayerOverlay;
import de.teamlapen.factions.common.world.entities.goals.*;
import de.teamlapen.factions.common.world.inventory.MinionContainer;
import de.teamlapen.sync.PropertySync;
import de.teamlapen.sync.api.ISyncable;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.scores.PlayerTeam;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public abstract class MinionEntity<T extends MinionData> extends PathfinderMob implements IPlayerOverlay, ValueIOSerializable, ForceLookEntityGoal.TaskOwner, IMinionEntity, IEntityWithComplexSpawn, EntitySyncHolder.ISyncHolder<T> {
    /**
     * Store the uuid of the lord. Should not be null when joining the world
     */
    private static final String NBT_KEY = "minion_data";
    protected static final EntityDataAccessor<Optional<UUID>> LORD_ID = SynchedEntityData.defineId(MinionEntity.class, FactionEntities.OPTIONAL_UUID.get());
    private final static Logger LOGGER = LogManager.getLogger();
    private final static NonNullList<ItemStack> EMPTY_LIST = NonNullList.create();
    private final static int CONVERT_DURATION = 20;
    /**
     * Predicate that checks that target is not affiliated with the lord
     */
    private final @NotNull Predicate<LivingEntity> hardAttackPredicate;
    /**
     * Predicate that checks if the target should be attacked based on its faction
     */
    private final @NotNull Predicate<LivingEntity> softAttackPredicate;
    /**
     * Only available server side.
     * Should be available on world join
     */
    @Nullable
    protected PlayerMinionController playerMinionController;
    /**
     * Only valid and nonnull if playerMinionController !=null
     */
    protected @Nullable T minionData;

    @Nullable
    private Pair<Identifier, PlayerModelType> skinDetails;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Nullable
    private Optional<PlayerSkinRenderCache.RenderInfo> skinProfile;
    /**
     * Only valid if playerMinionController !=null
     */
    private int minionId;
    /**
     * Only valid if playerMinionController !=null
     */
    private int token;
    /**
     * If >0 the conversion animation is running. Set on server side and synced with the spawn packet, afterwards its decreased on both server and client side. Not stored to NBT
     */
    private int convertCounter;
    /**
     * Holds the interacting player while the MinionContainer is open
     */
    @Nullable
    private Player interactingPlayer;

    private final MinionDataSync property;

    protected MinionEntity(EntityType<? extends PathfinderMob> type, Level world, @NotNull Predicate<LivingEntity> attackPredicate) {
        super(type, world);
        this.softAttackPredicate = attackPredicate;
        this.hardAttackPredicate = livingEntity -> {
            boolean flag1 = getLordOpt().map(ILordPlayer::asEntity).filter(entity -> entity == livingEntity).isPresent(); //Don't attack lord
            boolean flag2 = livingEntity instanceof MinionEntity && ((MinionEntity<?>) livingEntity).getLordID().filter(id -> getLordID().map(id2 -> id == id2).orElse(false)).isPresent(); //Don't attack other minions of lord
            boolean flag3 = livingEntity instanceof Player otherPlayer && getLordOpt().map(ILordPlayer::asEntity).map(player -> !player.canHarmPlayer(otherPlayer)).orElse(!Permissions.isPvpEnabled(otherPlayer));
            return !flag1 && !flag2 && !flag3;
        };
        this.property = createSync();
    }

    private MinionDataSync createSync() {
        return new MinionDataSync();
    }

    @Override
    public PropertySync getSyncData() {
        return this.property;
    }

    @Override
    public void setData(Optional<T> t) {
        this.minionData = t.orElse(null);
    }

    @Override
    public Optional<T> getData() {
        return Optional.ofNullable(this.minionData);
    }

    @Override
    public @NotNull LivingEntity asEntity() {
        return this;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide() && this.isAlive()) {
            if (this.random.nextInt(900) == 0 && this.deathTime == 0) {
                this.heal(1.0F);
            }
            if (this.tickCount % 20 == 0) {
                this.consumeOffhand();
            }
        }
        if (convertCounter > 0) {
            convertCounter--;
        }
        if (!this.level().isClientSide() && !this.isValid() && this.isAlive()) {
            LOGGER.warn("Minion without lord.");
            this.discard();
        }
    }

    public void changeMinionName(String name) {
        if (minionData != null) {
            this.minionData.setName(name);
            super.setCustomName(this.minionData.getFormattedName());
        }
    }

    public void claimMinionSlot(int id, @NotNull PlayerMinionController controller) {
        assert minionId == 0;
        controller.claimMinionSlot(id).ifPresent(token -> {
            playerMinionController = controller;
            minionId = id;
            this.token = token;
            getEntityData().set(LORD_ID, Optional.of(playerMinionController.getUUID()));
        });
    }

    @Override
    public void die(@NotNull DamageSource cause) {
        super.die(cause);
        if (this.playerMinionController != null) {
            this.getLordOpt().map(ILordPlayer::asEntity).ifPresent(p -> p.displayClientMessage(Component.translatable("text.factions.minion.died", this.getDisplayName()), true));
            this.playerMinionController.markDeadAndReleaseMinionSlot(minionId, token);
            this.playerMinionController = null;
        }
    }

    /**
     * Copy of {@link net.minecraft.world.entity.Mob} but with modified DamageSource
     * Check if code still up-to-date
     * TODO 1.22
     */
    @Override
    public boolean doHurtTarget(ServerLevel level, Entity pEntity) {
        float f = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        DamageSource damagesource = LevelDamage.get(this.level()).minion(this);
        if (this.level() instanceof ServerLevel serverlevel) {
            f = EnchantmentHelper.modifyDamage(serverlevel, this.getWeaponItem(), pEntity, damagesource, f);
        }

        boolean flag = pEntity.hurtServer(level,damagesource, f);
        if (flag) {
            float f1 = this.getKnockback(pEntity, damagesource);
            if (f1 > 0.0F && pEntity instanceof LivingEntity livingentity) {
                livingentity.knockback(
                        f1 * 0.5F,
                        Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)),
                        -Mth.cos(this.getYRot() * (float) (Math.PI / 180.0))
                );
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }

            if (this.level() instanceof ServerLevel serverlevel1) {
                EnchantmentHelper.doPostAttackEffects(serverlevel1, pEntity, damagesource);
            }

            this.setLastHurtMob(pEntity);
            this.playAttackSound();
        }

        return flag;
    }

    @Nullable
    @Override
    public PlayerTeam getTeam() {
        return getLordOpt().map(s -> s.asEntity().getTeam()).orElseGet(super::getTeam);
    }

    @Override
    protected boolean considersEntityAsAlly(Entity pEntity) {
        return getLordOpt().map(s -> s.asEntity() == pEntity).orElseGet(() -> super.considersEntityAsAlly(pEntity));
    }

    public void eat(@NotNull Level world, @NotNull ItemStack stack, FoodProperties properties) {
        float healAmount = properties.nutrition() / 2f;
        this.heal(healAmount);
    }

    /**
     * Return
     *
     * @param onlyShould If true only hostile (faction-wise) entities are targeted otherwise anything that is not affiliated with the lord is targeted
     * @return a predicate that checks if the target should be attacked
     */
    public Predicate<LivingEntity> getAttackPredicate(boolean onlyShould) {
        return onlyShould ? this.hardAttackPredicate.and(this.softAttackPredicate) : this.hardAttackPredicate;
    }

    public abstract List<IMinionTask<?, ?>> getAvailableTasks();

    @Override
    public @NotNull Optional<IMinionTask.IMinionTaskDesc<?>> getCurrentTask() {
        return minionData != null ? Optional.of(minionData.getCurrentTaskDesc()) : Optional.empty();
    }

    /**
     * @return Return player (lord) if they are currently interacting with this minion
     */
    @NotNull
    public Optional<Player> getForceLookTarget() {
        return Optional.ofNullable(interactingPlayer);
    }

    @Override
    public @NotNull Optional<IMinionInventory> getInventory() {
        if (this.minionData != null) {
            return Optional.of(this.minionData.getInventory());
        }
        return Optional.empty();
    }

    @Override
    @NotNull
    public Optional<ILordPlayer<?>> getLordOpt() {
        return getLord();
    }

    public @NotNull Optional<T> getMinionData() {
        return Optional.ofNullable(minionData);
    }

    @Override
    public @NotNull Optional<Integer> getMinionId() {
        return this.minionData == null ? Optional.empty() : Optional.of(minionId);
    }

    @Override
    public @NotNull Optional<PlayerSkinRenderCache.RenderInfo> getPlayerOverlay() {
        //noinspection OptionalAssignedToNull
        if (this.skinProfile == null) {
            PlayerSkinHelper.getPlayerRenderInfo(getLordID().orElse(null), x -> this.skinProfile = x);
        }
        return this.skinProfile;
    }

    @Override
    protected float sanitizeScale(float scale) {
        return 0.8f + convertCounter / (float) CONVERT_DURATION * 0.2f;
    }

    @Override
    protected @NotNull EntityDimensions getDefaultDimensions(Pose pose) {
        return super.getDefaultDimensions(pose).scale(getScale());
    }

    public boolean isTaskLocked() {
        return minionData != null && minionData.isTaskLocked();
    }

    /**
     * Call server side before adding entity to the world.
     * Once spawned the entity will perform the conversion animation on client side.
     */
    public void markAsConverted() {
        convertCounter = CONVERT_DURATION;
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        checkoutMinionData(this.level().registryAccess());
    }

    @Override
    public void onRemovedFromLevel() {
        if (playerMinionController != null) {
            playerMinionController.checkInMinion(this.minionId, this.token);
            this.minionData.updateEntityCaps(this.serializeMinionCaps());
            this.minionData = null;
            this.playerMinionController = null;
        }
        super.onRemovedFromLevel();
    }

    @NotNull
    @Override
    public ItemStack getItemBySlot(@NotNull EquipmentSlot slotIn) {
        return switch (slotIn.getType()) {
            case HAND -> getInventory().map(IMinionInventory::getInventoryHands).map(i -> i.get(slotIn.getIndex())).orElse(ItemStack.EMPTY);
            case HUMANOID_ARMOR -> getInventory().map(IMinionInventory::getInventoryArmor).map(i -> i.get(slotIn.getIndex())).orElse(ItemStack.EMPTY);
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        input.child("minion").ifPresent(minion -> {
            var id = minion.read("lord", UUIDUtil.CODEC);
            if (id.isPresent()) {
                if (level() instanceof ServerLevel serverLevel) {
                    this.playerMinionController = MinionWorldData.getData(serverLevel).getController(id.get());
                    if (this.playerMinionController == null) {
                        LOGGER.warn("Cannot get PlayerMinionController for {}", id.get());
                    } else {
                        this.minionId = minion.getInt("minion_id").orElseThrow();
                        this.token = minion.getInt("minion_token").orElseThrow();
                        this.getEntityData().set(LORD_ID, id);
                    }
                }
            }
        });
    }

    @Override
    public void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        getLordID().ifPresent(id -> {
            ValueOutput minion = output.child("minion");
            minion.putInt("minion_id", minionId);
            minion.putInt("minion_token", token);
            minion.store("lord", UUIDUtil.CODEC, id);
        });
    }

    @SuppressWarnings("EmptyMethod")
    public void onTaskChanged() {
        sync();
    }

    public void openAppearanceScreen() {
    }

    public void openStatsScreen() {

    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    @Deprecated
    public void recallMinion() {
        this.discard();
    }

    @Override
    public void setItemSlot(@NotNull EquipmentSlot slotIn, @NotNull ItemStack stack) {
        if (minionData == null) return;
        switch (slotIn.getType()) {
            case HAND -> getInventory().map(IMinionInventory::getInventoryHands).ifPresent(i -> i.set(slotIn.getIndex(), stack));
            case HUMANOID_ARMOR -> getInventory().map(IMinionInventory::getInventoryArmor).ifPresent(i -> i.set(slotIn.getIndex(), stack));
        }
    }

    public @NotNull Predicate<ItemStack> getEquipmentPredicate(EquipmentSlot slotType) {
        return itemStack -> FactionRestriction.canUse(this, itemStack, false);

    }

    @Override
    public void setCustomName(@Nullable Component name) {
        //NOP
    }

    @Override
    public void setHealth(float health) {
        super.setHealth(health);
        if (minionData != null) {
            minionData.setHealth(health);
        }
    }

    /**
     * Set/Reset currently interacting player
     */
    public void setInteractingPlayer(@Nullable Player player) {
        this.interactingPlayer = player;
    }

    @Override
    public void setTarget(@Nullable LivingEntity entitylivingbaseIn) {
        if (entitylivingbaseIn == null || hardAttackPredicate.test(entitylivingbaseIn)) {
            super.setTarget(entitylivingbaseIn);
        }
    }

    public abstract boolean shouldRenderLordSkin();

    @Override
    public boolean shouldShowName() {
        return true;
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(convertCounter);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        convertCounter = additionalData.readVarInt();
    }

    protected boolean canConsume(@NotNull ItemStack stack, @NotNull Consumable consumable) {
        return consumable.animation() == ItemUseAnimation.EAT || consumable.animation() == ItemUseAnimation.DRINK;
    }

    protected void consumeOffhand() {
        if (isUsingItem()) return;
        if (this.targetSelector.getAvailableGoals().stream().anyMatch(WrappedGoal::isRunning)) return;
        ItemStack stack = this.getInventory().map(i -> i.getItem(1)).orElse(ItemStack.EMPTY);
        if (stack.isEmpty()) return;
        Consumable consumable = stack.get(DataComponents.CONSUMABLE);
        if (consumable == null) return;
        if (!canConsume(stack, consumable)) return;
        this.startUsingItem(InteractionHand.OFF_HAND);
        this.setYRot(this.getYHeadRot());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(LORD_ID, Optional.empty());
    }

    protected Optional<ILordPlayer<?>> getLord() {
        return this.getLordID().map(this.level()::getPlayerByUUID).filter(Player::isAlive).map(FactionPlayerHandler::get).<ILordPlayer<?>>flatMap(x -> x.getLordPlayer());
    }

    public @NotNull Optional<UUID> getLordID() {
        return this.getEntityData().get(LORD_ID);
    }

    @Override
    protected void hurtArmor(@NotNull DamageSource damageSource, float damage) {
        this.doHurtEquipment(damageSource, damage, EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD);
    }

    protected boolean isLord(@NotNull Player p) {
        return this.getLordID().map(id -> id.equals(p.getUUID())).orElse(false);
    }

    protected boolean isValid() {
        return this.playerMinionController != null;
    }

    /**
     * Called when valid minion data is received on world load.
     * {@link MinionEntity#minionData} is already set
     * Can be called client and server side
     */
    protected void onMinionDataReceived(@NotNull T data) {
        var input = TagValueInput.create(ProblemReporter.DISCARDING, registryAccess(), data.getEntityCaps());
        this.deserializeAttachments(input);
    }

    @NotNull
    @Override
    protected InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (isLord(player)) {
            if (player instanceof ServerPlayer) {
                player.openMenu(new SimpleMenuProvider((id, playerInventory, playerIn) -> MinionContainer.create(id, playerInventory, this, getLord().orElseThrow()).orElse(null), Component.translatable("text.factions.name").append(this.getMinionData().map(MinionData::getFormattedName).orElse(Component.literal("Minion")))), buf -> buf.writeVarInt(this.getId()));
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ForceLookEntityGoal<>(this));
        this.goalSelector.addGoal(2, new OpenDoorGoal(this, true));

        this.goalSelector.addGoal(4, new FollowLordGoal(this, 1.1));

        this.goalSelector.addGoal(9, new MoveToTaskCenterGoal(this));
        this.goalSelector.addGoal(10, new LookAtClosestVisibleGoal(this, Player.class, 20F, 0.6F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && MinionEntity.this.getCurrentTask().filter(t -> t.getTask() == FactionMinionTasks.STAY.get()).isEmpty();
            }
        });

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new DefendAreaGoal(this));
        this.targetSelector.addGoal(2, new DefendLordGoal(this));

    }

    /**
     * Checkout the minion data from the playerMinionController (if available).
     * Call as early as possible but only if being added to world
     * Can be called from different locations. Only executes if not checkout already.
     * Happens either in {@link net.minecraft.world.entity.Entity#onAddedToLevel()} or if tracking starts before during {@link ISyncable#serializeUpdate(ValueOutput)}
     */
    private void checkoutMinionData(HolderLookup.Provider provider) {
        if (playerMinionController != null && minionData == null) {
            this.minionData = playerMinionController.checkoutMinion(this.minionId, this.token, this);
            if (minionData == null) {
                this.playerMinionController = null;
            } else {
                this.handleLoadedMinionData(minionData);
            }
        }
    }

    public final void handleLoadedMinionData(@NotNull T data) {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(data.getMaxHealth());
        super.setHealth(data.getHealth());
        super.setCustomName(data.getFormattedName());
        try {
            this.onMinionDataReceived(data);
        } catch (ClassCastException e) {
            LOGGER.error("Failed to cast minion data. Maybe the correct data was not registered", e);
            this.discard();
        }
        sync();
    }

    /**
     * serializes all allowed {@link net.neoforged.neoforge.capabilities.EntityCapability}s
     */
    protected CompoundTag serializeMinionCaps() {
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, this.level().registryAccess());

        Collection<String> allowedCapTags = getAllowedCapTags();
        this.serializeAttachments(output);

        CompoundTag tag = output.buildResult();
        tag.keySet().removeIf(s -> !allowedCapTags.contains(s));
        return tag;
    }

    @Override
    public void serialize(@NotNull ValueOutput output) {
        if (this.minionData == null && this.level().getEntity(this.getId()) != null) { //If tracking is started already while adding to world (and thereby before {@link Entity#onAddedToWorld}) trigger the checkout here (but only if actually added to world).
            this.checkoutMinionData(registryAccess());
        }
        if (this.minionData != null) {
            this.minionData.serialize(output.child("data"));
            output.putInt("minion_id", minionId);
        }
    }

    @Override
    public void deserialize(@NotNull ValueInput input) {
        input.child("data").ifPresent(data -> {
            T minionData = MinionData.<T>fromNBT(data);
            if (minionData == null) {
                LOGGER.warn("Failed to find correct minion data");
            } else {
                this.minionData = minionData;
                this.onMinionDataReceived(minionData);
                this.minionId = input.getInt("minion_id").orElseThrow();
                super.setCustomName(minionData.getFormattedName());
            }
        });
    }

    /**
     * @return all allowed capability identifiers
     */
    protected Collection<String> getAllowedCapTags() {
        return Collections.singleton(FResourceLocation.loc("armourers_workshop", "entity-skin-provider").toString());
    }

    public class MinionDataSync extends PropertySync {

        @Override
        public void sync() {
            MinionEntity.this.sync();
        }

        @Override
        protected void registerProperties() {
            registerProperty(FResourceLocation.mod("id"))
                    .simple(0, () -> minionId, id -> minionId = id);
            registerProperty(FResourceLocation.mod("content"))
                    .optionalSubProperty(MinionEntity.this::getData)
                    .withFactory(MinionEntity.this::createData)
                    .withNewInstanceSetter(MinionEntity.this::setData)
                    .register();
        }
    }
}
