package de.teamlapen.vampirism.entity.minion;

import com.mojang.authlib.GameProfile;
import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.vampirism.api.entity.minion.IMinionInventory;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.goals.ForceLookEntityGoal;
import de.teamlapen.vampirism.entity.goals.LookAtClosestVisibleGoal;
import de.teamlapen.vampirism.entity.minion.goals.DefendAreaGoal;
import de.teamlapen.vampirism.entity.minion.goals.DefendLordGoal;
import de.teamlapen.vampirism.entity.minion.goals.FollowLordGoal;
import de.teamlapen.vampirism.entity.minion.goals.MoveToTaskCenterGoal;
import de.teamlapen.vampirism.entity.minion.management.MinionDamageSource;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.inventory.container.MinionContainer;
import de.teamlapen.vampirism.util.IPlayerOverlay;
import de.teamlapen.vampirism.util.PlayerSkinHelper;
import de.teamlapen.vampirism.world.MinionWorldData;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
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
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkHooks;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public abstract class MinionEntity<T extends MinionData> extends VampirismEntity implements IPlayerOverlay, ISyncable, ForceLookEntityGoal.TaskOwner, de.teamlapen.vampirism.api.entity.minion.IMinionEntity, IEntityAdditionalSpawnData {
    /**
     * Store the uuid of the lord. Should not be null when joining the world
     */
    protected static final EntityDataAccessor<Optional<UUID>> LORD_ID = SynchedEntityData.defineId(MinionEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private final static Logger LOGGER = LogManager.getLogger();
    private final static NonNullList<ItemStack> EMPTY_LIST = NonNullList.create();
    private final static int CONVERT_DURATION = 20;
    /**
     * Predicate that checks that target is not affiliated with the lord
     */
    private final Predicate<LivingEntity> hardAttackPredicate;
    /**
     * Predicate that checks if the target should be attacked based on its faction
     */
    private final Predicate<LivingEntity> softAttackPredicate;
    /**
     * Only available server side.
     * Should be available on world join
     */
    @Nullable
    protected PlayerMinionController playerMinionController;
    /**
     * Only valid and nonnull if playerMinionController !=null
     */
    protected T minionData;

    @Nullable
    private Pair<ResourceLocation, Boolean> skinDetails;
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

    protected MinionEntity(EntityType<? extends VampirismEntity> type, Level world, @Nonnull Predicate<LivingEntity> attackPredicate) {
        super(type, world);
        this.softAttackPredicate = attackPredicate;
        this.hardAttackPredicate = livingEntity -> {
            boolean flag1 = getLordOpt().map(ILordPlayer::getPlayer).filter(entity -> entity == livingEntity).isPresent(); //Don't attack lord
            boolean flag2 = livingEntity instanceof MinionEntity && ((MinionEntity<?>) livingEntity).getLordID().filter(id -> getLordID().map(id2 -> id == id2).orElse(false)).isPresent(); //Don't attack other minions of lord
            return !flag1 && !flag2;
        };
        setDontDropEquipment();
        this.peaceful=true;
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        if (isValid()) {
            this.getLordID().ifPresent(id -> nbt.putUUID("lord", id));
            nbt.putInt("minion_id", minionId);
            nbt.putInt("minion_token", token);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide && this.isAlive()) {
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
        if (!this.level.isClientSide && !this.isValid() && this.isAlive()) {
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

    public void claimMinionSlot(int id, @Nonnull PlayerMinionController controller) {
        assert minionId == 0;
        controller.claimMinionSlot(id).ifPresent(token -> {
            playerMinionController = controller;
            minionId = id;
            this.token = token;
            getEntityData().set(LORD_ID, Optional.of(playerMinionController.getUUID()));
        });
    }

    @Override
    public void die(@Nonnull DamageSource cause) {
        super.die(cause);
        if (this.playerMinionController != null) {
            this.getLordOpt().map(ILordPlayer::getPlayer).ifPresent(p -> p.displayClientMessage(new TranslatableComponent("text.vampirism.minion.died", this.getDisplayName()), true));
            this.playerMinionController.markDeadAndReleaseMinionSlot(minionId, token);
            this.playerMinionController = null;
        }
    }

    /**
     * Copy of {@link net.minecraft.world.entity.Mob} but with modified DamageSource
     * Check if code still up-to-date
     * TODO 1.19
     */
    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        float f = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float f1 = (float) this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        if (entityIn instanceof LivingEntity) {
            f += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity) entityIn).getMobType());
            f1 += (float) EnchantmentHelper.getKnockbackBonus(this);
        }

        int i = EnchantmentHelper.getFireAspect(this);
        if (i > 0) {
            entityIn.setSecondsOnFire(i * 4);
        }

        boolean flag = entityIn.hurt(new MinionDamageSource(this), f);
        if (flag) {
            if (f1 > 0.0F && entityIn instanceof LivingEntity) {
                ((LivingEntity) entityIn).knockback(f1 * 0.5F, Mth.sin(this.getYRot() * ((float) Math.PI / 180F)), -Mth.cos(this.getYRot() * ((float) Math.PI / 180F)));
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
            }
            ItemStack itemstack = this.getMainHandItem();

            if (entityIn instanceof Player player) {
                this.maybeDisableShield(player, this.getMainHandItem(), player.isUsingItem() ? player.getUseItem() : ItemStack.EMPTY);
            }
            if (!this.level.isClientSide && !itemstack.isEmpty() && entityIn instanceof LivingEntity) {
                itemstack.getItem().hurtEnemy(itemstack, (LivingEntity) entityIn, this);
                if (itemstack.isEmpty()) {
                    this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                }
            }

            this.doEnchantDamageEffects(this, entityIn);
            this.setLastHurtMob(entityIn);
        }

        return flag;
    }

    @Nonnull
    @Override
    public ItemStack eat(@Nonnull Level world, @Nonnull ItemStack stack) {
        if (stack.isEdible()) {
            float healAmount = stack.getItem().getFoodProperties().getNutrition() / 2f;
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
    public Optional<IMinionTask.IMinionTaskDesc<?>> getCurrentTask() {
        return minionData != null ? Optional.of(minionData.getCurrentTaskDesc()) : Optional.empty();
    }

    /**
     * @return Return player (lord) if they are currently interacting with this minion
     */
    @Nonnull
    public Optional<Player> getForceLookTarget() {
        return Optional.ofNullable(interactingPlayer);
    }

    @Nonnull
    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public Optional<IMinionInventory> getInventory() {
        if (this.minionData != null) {
            return Optional.of(this.minionData.getInventory());
        }
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return getInventory().map(IMinionInventory::getInventoryArmor).orElse(EMPTY_LIST);
    }

    @Override
    @Nonnull
    public Optional<ILordPlayer> getLordOpt() {
        return Optional.ofNullable(getLord());
    }

    public Optional<T> getMinionData() {
        return Optional.ofNullable(minionData);
    }

    @Override
    public Optional<Integer> getMinionId() {
        return this.minionData == null ? Optional.empty() : Optional.of(minionId);
    }

    public Optional<Pair<ResourceLocation, Boolean>> getOverlayPlayerProperties() {
        if (skinDetails == null) {
            this.getLordID().ifPresent(id -> {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PlayerSkinHelper.obtainPlayerSkinPropertiesAsync(new GameProfile(id, "Dummy"), p -> this.skinDetails = p));
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

    @Nonnull
    @Override
    public EntityDimensions getDimensions(@Nonnull Pose poseIn) {
        return super.getDimensions(poseIn).scale(getScale());
    }

    public boolean isTaskLocked() {
        return minionData != null && minionData.isTaskLocked();
    }

    @Nonnull
    @Override
    public Iterable<ItemStack> getHandSlots() {
        return getInventory().map(IMinionInventory::getInventoryHands).orElse(EMPTY_LIST);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void loadUpdateFromNBT(CompoundTag nbt) {
        if (nbt.contains("data_type")) {
            try {
                @SuppressWarnings("unchecked")
                T data = (T) MinionData.fromNBT(nbt);
                this.minionData = data;
                this.onMinionDataReceived(data);
                this.minionId = nbt.getInt("minion_id");
                super.setCustomName(data.getFormattedName());
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
        checkoutMinionData();
    }

    @Nonnull
    @Override
    public ItemStack getItemBySlot(@Nonnull EquipmentSlot slotIn) {
        return switch (slotIn.getType()) {
            case HAND -> getInventory().map(IMinionInventory::getInventoryHands).map(i -> i.get(slotIn.getIndex())).orElse(ItemStack.EMPTY);
            case ARMOR -> getInventory().map(IMinionInventory::getInventoryArmor).map(i -> i.get(slotIn.getIndex())).orElse(ItemStack.EMPTY);
        };
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        UUID id = nbt.hasUUID("lord") ? nbt.getUUID("lord") : null;
        if (id != null && level instanceof ServerLevel) {
            this.playerMinionController = MinionWorldData.getData((ServerLevel) this.level).getController(id);
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

    @OnlyIn(Dist.CLIENT)
    public void openAppearanceScreen() {
    }

    @OnlyIn(Dist.CLIENT)
    public void openStatsScreen() {

    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        convertCounter = additionalData.readVarInt();
    }

    @Override
    @Deprecated
    public void recallMinion() {
        this.discard();
    }

    @Override
    public void remove(@Nonnull RemovalReason p_146834_) {
        super.remove(p_146834_);
        if (playerMinionController != null) {
            playerMinionController.checkInMinion(this.minionId, this.token);
            this.minionData = null;
            this.playerMinionController = null;
        }
    }

    @Override
    public void setItemSlot(@Nonnull EquipmentSlot slotIn, @Nonnull ItemStack stack) {
        if (minionData == null) return;
        switch (slotIn.getType()) {
            case HAND -> getInventory().map(IMinionInventory::getInventoryHands).ifPresent(i -> i.set(slotIn.getIndex(), stack));
            case ARMOR -> getInventory().map(IMinionInventory::getInventoryArmor).ifPresent(i -> i.set(slotIn.getIndex(), stack));
        }
    }

    public Predicate<ItemStack> getEquipmentPredicate(EquipmentSlot slotType) {
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
        if (entitylivingbaseIn == null || hardAttackPredicate.test(entitylivingbaseIn))
            super.setTarget(entitylivingbaseIn);
    }

    public abstract boolean shouldRenderLordSkin();

    @Override
    public boolean shouldShowName() {
        return true;
    }

    @Override
    public void writeFullUpdateToNBT(CompoundTag nbt) {
        if (minionData == null && this.level.getEntity(this.getId()) != null) { //If tracking is started already while adding to world (and thereby before {@link Entity#onAddedToWorld}) trigger the checkout here (but only if actually added to world).
            this.checkoutMinionData();
        }
        if (minionData != null) {
            minionData.serializeNBT(nbt);
            nbt.putInt("minion_id", minionId);
        }
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeVarInt(convertCounter);
    }

    protected boolean canConsume(ItemStack stack) {
        if (!(stack.getUseAnimation() == UseAnim.DRINK || stack.getUseAnimation() == UseAnim.EAT)) return false;
        return !stack.isEmpty();
    }

    protected void consumeOffhand() {
        if (isUsingItem()) return;
        if (this.targetSelector.getRunningGoals().findAny().isPresent()) return;
        ItemStack stack = this.getInventory().map(i -> i.getItem(1)).orElse(ItemStack.EMPTY);
        if (!canConsume(stack)) return;
        this.startUsingItem(InteractionHand.OFF_HAND);
        this.setYRot(this.getYHeadRot());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(LORD_ID, Optional.empty());

    }

    @Nullable
    protected ILordPlayer getLord() {
        return this.getLordID().map(this.level::getPlayerByUUID).filter(Player::isAlive).flatMap(p->FactionPlayerHandler.getOpt(p).resolve()).orElse(null);
    }

    protected Optional<UUID> getLordID() {
        return this.getEntityData().get(LORD_ID);
    }

    @Override
    protected void hurtArmor(@Nonnull DamageSource damageSource, float damage) {
        if (this.minionData != null) this.minionData.getInventory().damageArmor(damageSource, damage, this);
    }

    protected boolean isLord(Player p) {
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
    protected void onMinionDataReceived(@Nonnull T data) {
    }

    @Nonnull
    @Override
    protected InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
        if (isLord(player)) {
            if (player instanceof ServerPlayer) {
                NetworkHooks.openGui((ServerPlayer) player, new SimpleMenuProvider((id, playerInventory, playerIn) -> MinionContainer.create(id, playerInventory, this), new TranslatableComponent("text.vampirism.name").append(this.getMinionData().map(MinionData::getFormattedName).orElse(new TextComponent("Minion")))), buf -> buf.writeVarInt(this.getId()));
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
                return super.canUse() && !MinionEntity.this.getCurrentTask().filter(t -> t.getTask()== MinionTasks.STAY.get()).isPresent();
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
     * Happens either in {@link Entity#onAddedToWorld()} or if tracking starts before during {@link MinionEntity#writeFullUpdateToNBT(CompoundTag)}
     */
    private void checkoutMinionData() {
        if (playerMinionController != null && minionData == null) {
            this.minionData = playerMinionController.checkoutMinion(this.minionId, this.token, this);
            if (minionData == null) {
                this.playerMinionController = null;
            } else {
                this.handleLoadedMinionData(minionData);
            }
        }
    }

    private void handleLoadedMinionData(@Nonnull T data) {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(data.getMaxHealth());
        super.setHealth(data.getHealth());
        super.setCustomName(data.getFormattedName());
        try {
            this.onMinionDataReceived(data);
        } catch (ClassCastException e) {
            LOGGER.error("Failed to cast minion data. Maybe the correct data was not registered", e);
            this.discard();
        }
    }
}
