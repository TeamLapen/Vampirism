package de.teamlapen.vampirism.entity.minion;

import com.mojang.authlib.GameProfile;
import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.vampirism.api.entity.minion.IMinionInventory;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.goals.ForceLookEntityGoal;
import de.teamlapen.vampirism.entity.goals.LookAtClosestVisibleGoal;
import de.teamlapen.vampirism.entity.minion.goals.DefendAreaGoal;
import de.teamlapen.vampirism.entity.minion.goals.FollowLordGoal;
import de.teamlapen.vampirism.entity.minion.goals.MoveToTaskCenterGoal;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.inventory.container.MinionContainer;
import de.teamlapen.vampirism.util.IPlayerOverlay;
import de.teamlapen.vampirism.util.PlayerSkinHelper;
import de.teamlapen.vampirism.world.MinionWorldData;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;


public abstract class MinionEntity<T extends MinionData> extends VampirismEntity implements IPlayerOverlay, ISyncable, ForceLookEntityGoal.TaskOwner, de.teamlapen.vampirism.api.entity.minion.IMinionEntity {
    private final static Logger LOGGER = LogManager.getLogger();
    private final static NonNullList<ItemStack> EMPTY_LIST = NonNullList.create();

    /**
     * Store the uuid of the lord. Should not be null when joining the world
     */
    protected static final DataParameter<Optional<UUID>> LORD_ID = EntityDataManager.createKey(MinionEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);

    /**
     * Only available server side.
     * Should be available on world join
     */
    @Nullable
    protected PlayerMinionController playerMinionController;

    @Nullable
    private GameProfile skinProfile;

    /**
     * Only valid if playerMinionController !=null
     */
    private int minionId;
    /**
     * Only valid if playerMinionController !=null
     */
    private int token;
    /**
     * Only valid and nonnull if playerMinionController !=null
     */
    protected T minionData;

    /**
     * Holds the interacting player while the MinionContainer is open
     */
    @Nullable
    private PlayerEntity interactingPlayer;

    /**
     * Predicate that checks that target is not affiliated with the lord
     */
    private final Predicate<LivingEntity> hardAttackPredicate;
    /**
     * Predicate that checks if the target should be attacked based on its faction
     */
    private final Predicate<LivingEntity> softAttackPredicate;

    protected MinionEntity(EntityType<? extends VampirismEntity> type, World world, @Nonnull Predicate<LivingEntity> attackPredicate) {
        super(type, world);
        this.softAttackPredicate = attackPredicate;
        this.hardAttackPredicate = livingEntity -> {
            boolean flag1 = getLordOpt().map(ILordPlayer::getPlayer).filter(entity -> entity == livingEntity).isPresent(); //Don't attack lord
            boolean flag2 = livingEntity instanceof MinionEntity && ((MinionEntity<?>) livingEntity).getLordID().filter(id -> getLordID().map(id2 -> id == id2).orElse(false)).isPresent(); //Don't attack other minions of lord
            return !flag1 && !flag2;
        };
    }

    @Nonnull
    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return getInventory().map(IMinionInventory::getInventoryArmor).orElse(EMPTY_LIST);
    }

    @Nullable
    @Override
    public GameProfile getOverlayPlayerProfile() {
        if (skinProfile == null) {
            skinProfile = this.getLordID().map(id->new GameProfile(id,"")).orElse(null);
            if(skinProfile!=null)PlayerSkinHelper.updateGameProfileAsync(skinProfile, updatedProfile->this.skinProfile=updatedProfile);
        }
        return skinProfile;
    }

    public abstract List<IMinionTask<?>> getAvailableTasks();

    public int getAvailableInvSize() {
        return 9; //TODO integrate with minion data maybe via data parameter as required client side
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

    @Override
    public Optional<IMinionTask.IMinionTaskDesc> getCurrentTask() {
        return minionData != null ? Optional.of(minionData.getCurrentTaskDesc()) : Optional.empty();
    }

    @Nonnull
    @Override
    public Iterable<ItemStack> getHeldEquipment() {
        return getInventory().map(IMinionInventory::getInventoryHands).orElse(EMPTY_LIST);
    }

    @Override
    public Optional<IMinionInventory> getInventory() {
        if (this.minionData != null) {
            return Optional.of(this.minionData.getInventory());
        }
        return Optional.empty();
    }

    @Override
    public LivingEntity getRepresentingEntity() {
        return this;
    }

    public boolean isTaskLocked() {
        return minionData != null && minionData.isTaskLocked();
    }

    public void claimMinionSlot(int id, @Nonnull PlayerMinionController controller) {
        assert minionId == 0;
        controller.claimMinionSlot(id).ifPresent(token -> {
            playerMinionController = controller;
            minionId = id;
            this.token = token;
            getDataManager().set(LORD_ID, Optional.of(playerMinionController.getUUID()));
        });
    }

    public abstract boolean shouldRenderLordSkin();

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack getItemStackFromSlot(@Nonnull EquipmentSlotType slotIn) {
        switch (slotIn.getSlotType()) {
            case HAND:
                return getInventory().map(IMinionInventory::getInventoryHands).map(i -> i.get(slotIn.getIndex())).orElse(ItemStack.EMPTY);
            case ARMOR:
                return getInventory().map(IMinionInventory::getInventoryArmor).map(i -> i.get(slotIn.getIndex())).orElse(ItemStack.EMPTY);
            default:
                return ItemStack.EMPTY;
        }
    }

    public float getScale() {
        return 0.8f;
    }

    @Override
    @Nonnull
    public Optional<ILordPlayer> getLordOpt() {
        return Optional.ofNullable(getLord());
    }

    @Nonnull
    @Override
    public EntitySize getSize(@Nonnull Pose p_213305_1_) {
        return super.getSize(p_213305_1_).scale(getScale());
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (!this.world.isRemote && !this.isValid() && this.isAlive()) {
            LOGGER.warn("Minion without lord.");
            this.remove();
        }
    }

    @Override
    public Optional<Integer> getMinionId() {
        return this.minionData == null ? Optional.empty() : Optional.of(minionId);
    }

    /**
     * @return Return player (lord) if they are currently interacting with this minion
     */
    @Nonnull
    public Optional<PlayerEntity> getForceLookTarget() {
        return Optional.ofNullable(interactingPlayer);
    }

    /**
     * Set/Reset currently interacting player
     */
    public void setInteractingPlayer(@Nullable PlayerEntity player) {
        this.interactingPlayer = player;
    }

    @Override
    public void loadUpdateFromNBT(CompoundNBT nbt) {
        if (nbt.contains("data_type")) {
            MinionData data = MinionData.fromNBT(nbt);
            try {
                this.onMinionDataReceived((T) data);
                this.minionData = (T) data;
                this.minionId = nbt.getInt("minion_id");
            } catch (ClassCastException e) {
                LOGGER.error("Failed to cast minion data. Maybe the correct data was not registered", e);
            }
        } else {
            LOGGER.warn("Received empty minion data");
        }
    }

    public void onTaskChanged() {
        HelperLib.sync(this);
    }

    @Override
    public void setCustomName(@Nullable ITextComponent name) {
        super.setCustomName(name);
        if (minionData != null) {
            minionData.setName(name);
        }
    }

    @Override
    public void setAttackTarget(@Nullable LivingEntity entitylivingbaseIn) {
        if (entitylivingbaseIn == null || hardAttackPredicate.test(entitylivingbaseIn))
            super.setAttackTarget(entitylivingbaseIn);
    }

    @Override
    public void setHealth(float health) {
        super.setHealth(health);
        if (minionData != null) {
            minionData.setHealth(health);
        }
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        checkoutMinionData();
    }

    @Override
    @Deprecated
    public void recallMinion() {
        this.remove();
    }

    @Override
    public void onDeath(@Nonnull DamageSource p_70645_1_) {
        super.onDeath(p_70645_1_);
        if (this.playerMinionController != null) {
            this.getLordOpt().map(ILordPlayer::getPlayer).ifPresent(p -> p.sendStatusMessage(new TranslationTextComponent("text.vampirism.minion.died", this.getDisplayName()), true));
            this.playerMinionController.markDeadAndReleaseMinionSlot(minionId, token);
            this.playerMinionController = null;
        }
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);
        UUID id = nbt.hasUniqueId("lord") ? nbt.getUniqueId("lord") : null;
        if (id != null && world instanceof ServerWorld) {
            this.playerMinionController = MinionWorldData.getData((ServerWorld) this.world).getController(id);
            if (this.playerMinionController == null) {
                LOGGER.warn("Cannot get PlayerMinionController for {}", id);
            } else {
                this.minionId = nbt.getInt("minion_id");
                this.token = nbt.getInt("minion_token");
                this.getDataManager().set(LORD_ID, Optional.of(id));
            }
        }
    }

    @Override
    public void setItemStackToSlot(@Nonnull EquipmentSlotType slotIn, @Nonnull ItemStack stack) {
        if (minionData == null) return;
        switch (slotIn.getSlotType()) {
            case HAND:
                getInventory().map(IMinionInventory::getInventoryHands).ifPresent(i -> i.set(slotIn.getIndex(), stack));
                break;
            case ARMOR:
                getInventory().map(IMinionInventory::getInventoryArmor).ifPresent(i -> i.set(slotIn.getIndex(), stack));
        }
    }

    @Override
    public void remove(boolean p_remove_1_) {
        super.remove(p_remove_1_);
        if (playerMinionController != null) {
            playerMinionController.checkInMinion(this.minionId, this.token);
            this.minionData = null;
            this.playerMinionController = null;
        }
    }

    @Nullable
    protected ILordPlayer getLord() {
        return this.getLordID().map(this.world::getPlayerByUuid).filter(PlayerEntity::isAlive).map(FactionPlayerHandler::get).orElse(null);
    }

    protected Optional<UUID> getLordID() {
        return this.getDataManager().get(LORD_ID);
    }



    @Override
    protected void registerData() {
        super.registerData();
        this.getDataManager().register(LORD_ID, Optional.empty());
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        if (isValid()) {
            this.getLordID().ifPresent(id -> nbt.putUniqueId("lord", id));
            nbt.putInt("minion_id", minionId);
            nbt.putInt("minion_token", token);
        }
    }

    protected boolean isValid() {
        return this.playerMinionController != null;
    }

    @Override
    public void writeFullUpdateToNBT(CompoundNBT nbt) {
        if (minionData == null && this.world.getEntityByID(this.getEntityId()) != null) { //If tracking is started already while adding to world (and thereby before {@link Entity#onAddedToWorld}) trigger the checkout here (but only if actually added to world).
            this.checkoutMinionData();
        }
        if (minionData != null) {
            minionData.serializeNBT(nbt);
            nbt.putInt("minion_id", minionId);
        }
    }

    protected Optional<T> getMinionData() {
        return Optional.ofNullable(minionData);
    }

    /**
     * Called when valid minion data is received on world load.
     * Can  be called client and server side
     */
    protected void onMinionDataReceived(@Nonnull T data) {

    }

    @Override
    protected boolean processInteract(PlayerEntity player, Hand hand) {
        if (this.getLordOpt().filter(p -> p.getPlayer().equals(player)).isPresent()) {
            if (player instanceof ServerPlayerEntity) {
                NetworkHooks.openGui((ServerPlayerEntity) player, new SimpleNamedContainerProvider((id, playerInventory, playerIn) -> MinionContainer.create(id, playerInventory, this), new TranslationTextComponent("text.vampirism.name").appendSibling(this.getMinionData().map(MinionData::getName).orElse(new StringTextComponent("Minion")))), buf -> buf.writeVarInt(this.getEntityId()));
            }
            return true;
        }
        return false;
    }

    /**
     * Checkout the minion data from the playerMinionController (if available).
     * Call as early as possible but only if being added to world
     * Can be called from different locations. Only executes if not checkout already.
     * Happens either in {@link Entity#onAddedToWorld()} or if tracking starts before during {@link MinionEntity#writeFullUpdateToNBT(CompoundNBT)}
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

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new ForceLookEntityGoal<>(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new OpenDoorGoal(this, true));

        this.goalSelector.addGoal(4, new FollowLordGoal(this, 1.1));

        this.goalSelector.addGoal(9, new MoveToTaskCenterGoal(this));
        this.goalSelector.addGoal(10, new LookAtClosestVisibleGoal(this, PlayerEntity.class, 20F, 0.6F));
        this.goalSelector.addGoal(10, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new DefendAreaGoal(this));

    }

    private void handleLoadedMinionData(@Nonnull T data) {
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(data.getMaxHealth());
        this.setHealth(data.getHealth());
        super.setCustomName(data.getName());
        try {
            this.onMinionDataReceived(data);
        } catch (ClassCastException e) {
            LOGGER.error("Failed to cast minion data. Maybe the correct data was not registered", e);
            this.remove();
        }
    }
}
