package de.teamlapen.vampirism.entity.minion;

import com.mojang.authlib.GameProfile;
import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.storage.ISyncable;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.minion.IMinionEntity;
import de.teamlapen.vampirism.api.entity.minion.IMinionInventory;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.ai.goals.ForceLookEntityGoal;
import de.teamlapen.vampirism.entity.ai.goals.LookAtClosestVisibleGoal;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.goals.DefendAreaGoal;
import de.teamlapen.vampirism.entity.minion.goals.DefendLordGoal;
import de.teamlapen.vampirism.entity.minion.goals.FollowLordGoal;
import de.teamlapen.vampirism.entity.minion.goals.MoveToTaskCenterGoal;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.inventory.MinionContainer;
import de.teamlapen.vampirism.util.DamageHandler;
import de.teamlapen.vampirism.util.IPlayerOverlay;
import de.teamlapen.vampirism.util.Permissions;
import de.teamlapen.vampirism.util.PlayerModelType;
import de.teamlapen.vampirism.world.LevelDamage;
import de.teamlapen.vampirism.world.MinionWorldData;
import de.teamlapen.vampirism.world.ModDamageSources;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
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
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public abstract class MinionEntity<T extends MinionData> extends VampirismEntity implements IPlayerOverlay, ISyncable, ForceLookEntityGoal.TaskOwner, IMinionEntity, IEntityWithComplexSpawn {
    /**
     * Store the uuid of the lord. Should not be null when joining the world
     */
    private static final String NBT_KEY = "minion_data";
    protected static final EntityDataAccessor<Optional<UUID>> LORD_ID = SynchedEntityData.defineId(MinionEntity.class, EntityDataSerializers.OPTIONAL_UUID);
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
    private Pair<ResourceLocation, PlayerModelType> skinDetails;
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

    protected MinionEntity(EntityType<? extends VampirismEntity> type, Level world, @NotNull Predicate<LivingEntity> attackPredicate) {
        super(type, world);
        this.softAttackPredicate = attackPredicate;
        this.hardAttackPredicate = livingEntity -> {
            boolean flag1 = getLordOpt().map(ILordPlayer::getPlayer).filter(entity -> entity == livingEntity).isPresent(); //Don't attack lord
            boolean flag2 = livingEntity instanceof MinionEntity && ((MinionEntity<?>) livingEntity).getLordID().filter(id -> getLordID().map(id2 -> id == id2).orElse(false)).isPresent(); //Don't attack other minions of lord
            boolean flag3 = livingEntity instanceof Player otherPlayer && getLordOpt().map(ILordPlayer::getPlayer).map(player -> !player.canHarmPlayer(otherPlayer)).orElse(!Permissions.isPvpEnabled(otherPlayer));
            return !flag1 && !flag2 && !flag3;
        };
        setDontDropEquipment();
        this.peaceful = true;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
//        if (isValid()) {
        this.getLordID().ifPresent(id -> nbt.putUUID("lord", id));
        nbt.putInt("minion_id", minionId);
        nbt.putInt("minion_token", token);
//        }
    }

    @Override
    public String nbtKey() {
        return NBT_KEY;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.isAlive()) {
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
        if (!this.level().isClientSide && !this.isValid() && this.isAlive()) {
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
            this.getLordOpt().map(ILordPlayer::getPlayer).ifPresent(p -> p.displayClientMessage(Component.translatable("text.vampirism.minion.died", this.getDisplayName()), true));
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
    public boolean doHurtTarget(Entity pEntity) {
        float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        DamageSource damagesource = LevelDamage.get(this.level()).minion(this);
        if (this.level() instanceof ServerLevel serverlevel) {
            f = EnchantmentHelper.modifyDamage(serverlevel, this.getWeaponItem(), pEntity, damagesource, f);
        }

        boolean flag = pEntity.hurt(damagesource, f);
        if (flag) {
            float f1 = this.getKnockback(pEntity, damagesource);
            if (f1 > 0.0F && pEntity instanceof LivingEntity livingentity) {
                livingentity.knockback(
                        (double)(f1 * 0.5F),
                        (double)Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)),
                        (double)(-Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)))
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

    @NotNull
    @Override
    public ItemStack eat(@NotNull Level world, @NotNull ItemStack stack, FoodProperties properties) {
        if (stack.getFoodProperties(this) != null) {
            float healAmount = properties.nutrition() / 2f;
            this.heal(healAmount);
        }
        return super.eat(world, stack);
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

    @NotNull
    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return getInventory().map(IMinionInventory::getInventoryArmor).orElse(EMPTY_LIST);
    }

    @Override
    @NotNull
    public Optional<ILordPlayer> getLordOpt() {
        return Optional.ofNullable(getLord());
    }

    public @NotNull Optional<T> getMinionData() {
        return Optional.ofNullable(minionData);
    }

    @Override
    public @NotNull Optional<Integer> getMinionId() {
        return this.minionData == null ? Optional.empty() : Optional.of(minionId);
    }

    public @NotNull Optional<Pair<ResourceLocation, PlayerModelType>> getOverlayPlayerProperties() {
        if (skinDetails == null) {
            this.getLordID().ifPresent(id -> {
                VampirismMod.proxy.obtainPlayerSkins(new GameProfile(id, "Dummy"), p -> this.skinDetails = p);
            });
            skinDetails = PENDING_PROP;
        }
        return Optional.of(skinDetails);
    }

    @Override
    public LivingEntity getRepresentingEntity() {
        return this;
    }

    public float getScale() {
        return 0.8f + convertCounter / (float) CONVERT_DURATION * 0.2f;
    }

    @Override
    protected @NotNull EntityDimensions getDefaultDimensions(Pose pose) {
        return super.getDefaultDimensions(pose).scale(getScale());
    }

    public boolean isTaskLocked() {
        return minionData != null && minionData.isTaskLocked();
    }

    @NotNull
    @Override
    public Iterable<ItemStack> getHandSlots() {
        return getInventory().map(IMinionInventory::getInventoryHands).orElse(EMPTY_LIST);
    }

    @Override
    public void deserializeUpdateNBT(HolderLookup.Provider provider, @NotNull CompoundTag nbt) {
        if (nbt.contains("data_type", Tag.TAG_STRING)) {
            try {
                @Nullable
                MinionData data = MinionData.fromNBT(provider, nbt);
                if (data == null) {
                    LOGGER.warn("Failed to find correct minion data");
                } else {
                    @SuppressWarnings("unchecked")
                    T cast = ((T) data);
                    this.minionData = cast;
                    this.onMinionDataReceived(provider, cast);
                    this.minionId = nbt.getInt("minion_id");
                    super.setCustomName(data.getFormattedName());
                }
            } catch (ClassCastException e) {
                LOGGER.error("Failed to cast minion data. Maybe the correct data was not registered", e);
            }
        } else {
            LOGGER.warn("Received empty minion data");
        }
    }

    /**
     * Call server side before adding entity to the world.
     * Once spawned the entity will perform the conversion animation on client side.
     */
    public void markAsConverted() {
        convertCounter = CONVERT_DURATION;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        checkoutMinionData(this.level().registryAccess());
    }

    @Override
    public void onRemovedFromWorld() {
        if (playerMinionController != null) {
            playerMinionController.checkInMinion(this.minionId, this.token);
            this.minionData.updateEntityCaps(this.serializeMinionCaps(this.level().registryAccess()));
            this.minionData = null;
            this.playerMinionController = null;
        }
        super.onRemovedFromWorld();
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
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        UUID id = nbt.hasUUID("lord") ? nbt.getUUID("lord") : null;
        if (id != null && level() instanceof ServerLevel) {
            this.playerMinionController = MinionWorldData.getData((ServerLevel) this.level()).getController(id);
            if (this.playerMinionController == null) {
                LOGGER.warn("Cannot get PlayerMinionController for {}", id);
            } else {
                this.minionId = nbt.getInt("minion_id");
                this.token = nbt.getInt("minion_token");
                this.getEntityData().set(LORD_ID, Optional.of(id));
            }
        }
    }

    public void onTaskChanged() {
        HelperLib.sync(this);
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
        return itemStack -> !(itemStack.getItem() instanceof IFactionExclusiveItem) || this.getFaction().equals(((IFactionExclusiveItem) itemStack.getItem()).getExclusiveFaction(itemStack));

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
    public @NotNull CompoundTag serializeUpdateNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        if (minionData == null && this.level().getEntity(this.getId()) != null) { //If tracking is started already while adding to world (and thereby before {@link Entity#onAddedToWorld}) trigger the checkout here (but only if actually added to world).
            this.checkoutMinionData(provider);
        }
        if (minionData != null) {
            minionData.serializeNBT(tag, provider);
            tag.putInt("minion_id", minionId);
        }
        return tag;
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(convertCounter);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        convertCounter = additionalData.readVarInt();
    }

    protected boolean canConsume(@NotNull ItemStack stack) {
        if (!(stack.getUseAnimation() == UseAnim.DRINK || stack.getUseAnimation() == UseAnim.EAT)) return false;
        return !stack.isEmpty();
    }

    protected void consumeOffhand() {
        if (isUsingItem()) return;
        if (this.targetSelector.getAvailableGoals().stream().anyMatch(WrappedGoal::isRunning)) return;
        ItemStack stack = this.getInventory().map(i -> i.getItem(1)).orElse(ItemStack.EMPTY);
        if (!canConsume(stack)) return;
        this.startUsingItem(InteractionHand.OFF_HAND);
        this.setYRot(this.getYHeadRot());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(LORD_ID, Optional.empty());
    }

    @Nullable
    protected ILordPlayer getLord() {
        return this.getLordID().map(this.level()::getPlayerByUUID).filter(Player::isAlive).map(FactionPlayerHandler::get).orElse(null);
    }

    protected @NotNull Optional<UUID> getLordID() {
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
     * {@link de.teamlapen.vampirism.entity.minion.MinionEntity#minionData} is already set
     * Can be called client and server side
     */
    protected void onMinionDataReceived(HolderLookup.Provider provider, @NotNull T data) {
        this.deserializeAttachments(provider, data.getEntityCaps());
    }

    @NotNull
    @Override
    protected InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (isLord(player)) {
            if (player instanceof ServerPlayer) {
                player.openMenu(new SimpleMenuProvider((id, playerInventory, playerIn) -> MinionContainer.create(id, playerInventory, this, getLord()), Component.translatable("text.vampirism.name").append(this.getMinionData().map(MinionData::getFormattedName).orElse(Component.literal("Minion")))), buf -> buf.writeVarInt(this.getId()));
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
                return super.canUse() && MinionEntity.this.getCurrentTask().filter(t -> t.getTask() == MinionTasks.STAY.get()).isEmpty();
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
     * Happens either in {@link net.minecraft.world.entity.Entity#onAddedToWorld()} or if tracking starts before during {@link MinionEntity#serializeUpdateNBT(net.minecraft.core.HolderLookup.Provider)}
     */
    private void checkoutMinionData(HolderLookup.Provider provider) {
        if (playerMinionController != null && minionData == null) {
            this.minionData = playerMinionController.checkoutMinion(this.minionId, this.token, this);
            if (minionData == null) {
                this.playerMinionController = null;
            } else {
                this.handleLoadedMinionData(provider, minionData);
            }
        }
    }

    public final void handleLoadedMinionData(HolderLookup.Provider provider, @NotNull T data) {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(data.getMaxHealth());
        super.setHealth(data.getHealth());
        super.setCustomName(data.getFormattedName());
        try {
            this.onMinionDataReceived(provider, data);
        } catch (ClassCastException e) {
            LOGGER.error("Failed to cast minion data. Maybe the correct data was not registered", e);
            this.discard();
        }
    }

    /**
     * serializes all allowed {@link net.neoforged.neoforge.capabilities.EntityCapability}s
     */
    protected CompoundTag serializeMinionCaps(HolderLookup.Provider provider) {
        Collection<String> allowedCapTags = getAllowedCapTags();
        CompoundTag tag = this.serializeAttachments(provider);
        if (tag != null) {
            tag.getAllKeys().removeIf(s -> {
                return !allowedCapTags.contains(s);
            });
            return tag;
        } else {
            return new CompoundTag();
        }
    }

    /**
     * @return all allowed capability identifiers
     */
    protected Collection<String> getAllowedCapTags() {
        return Collections.singleton(VResourceLocation.loc("armourers_workshop", "entity-skin-provider").toString());
    }
}
