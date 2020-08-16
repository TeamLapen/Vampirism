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
import de.teamlapen.vampirism.entity.minion.goals.DefendLordGoal;
import de.teamlapen.vampirism.entity.minion.goals.FollowLordGoal;
import de.teamlapen.vampirism.entity.minion.goals.MoveToTaskCenterGoal;
import de.teamlapen.vampirism.entity.minion.management.MinionDamageSource;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.inventory.container.MinionContainer;
import de.teamlapen.vampirism.util.IPlayerOverlay;
import de.teamlapen.vampirism.util.PlayerSkinHelper;
import de.teamlapen.vampirism.world.MinionWorldData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.OpenDoorGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
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
    protected static final DataParameter<Optional<UUID>> LORD_ID = EntityDataManager.createKey(MinionEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
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
     * If >0 the conversion animation is running. Set on server side and synced with the spawn packet, afterwards its decreased on both server and client side. Not stored to NBT
     */
    private int convertCounter;
    /**
     * Holds the interacting player while the MinionContainer is open
     */
    @Nullable
    private PlayerEntity interactingPlayer;

    protected MinionEntity(EntityType<? extends VampirismEntity> type, World world, @Nonnull Predicate<LivingEntity> attackPredicate) {
        super(type, world);
        this.softAttackPredicate = attackPredicate;
        this.hardAttackPredicate = livingEntity -> {
            boolean flag1 = getLordOpt().map(ILordPlayer::getPlayer).filter(entity -> entity == livingEntity).isPresent(); //Don't attack lord
            boolean flag2 = livingEntity instanceof MinionEntity && ((MinionEntity<?>) livingEntity).getLordID().filter(id -> getLordID().map(id2 -> id == id2).orElse(false)).isPresent(); //Don't attack other minions of lord
            return !flag1 && !flag2;
        };
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    /**
     * Copy of {@link MobEntity} but with modified DamageSource
     * Check if code still up-to-date
     * TODO 1.15
     * TODO 1.16
     * TODO 1.17
     *
     * @param entityIn
     * @return
     */
    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        float f = (float) this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
        float f1 = (float) this.getAttribute(SharedMonsterAttributes.ATTACK_KNOCKBACK).getValue();
        if (entityIn instanceof LivingEntity) {
            f += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((LivingEntity) entityIn).getCreatureAttribute());
            f1 += (float) EnchantmentHelper.getKnockbackModifier(this);
        }

        int i = EnchantmentHelper.getFireAspectModifier(this);
        if (i > 0) {
            entityIn.setFire(i * 4);
        }

        boolean flag = entityIn.attackEntityFrom(new MinionDamageSource(this), f);
        if (flag) {
            if (f1 > 0.0F && entityIn instanceof LivingEntity) {
                ((LivingEntity) entityIn).knockBack(this, f1 * 0.5F, MathHelper.sin(this.rotationYaw * ((float) Math.PI / 180F)), -MathHelper.cos(this.rotationYaw * ((float) Math.PI / 180F)));
                this.setMotion(this.getMotion().mul(0.6D, 1.0D, 0.6D));
            }

            if (entityIn instanceof PlayerEntity) {
                PlayerEntity playerentity = (PlayerEntity) entityIn;
                ItemStack itemstack = this.getHeldItemMainhand();
                ItemStack itemstack1 = playerentity.isHandActive() ? playerentity.getActiveItemStack() : ItemStack.EMPTY;
                if (!itemstack.isEmpty() && !itemstack1.isEmpty() && itemstack.canDisableShield(itemstack1, playerentity, this) && itemstack1.isShield(playerentity)) {
                    float f2 = 0.25F + (float) EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;
                    if (this.rand.nextFloat() < f2) {
                        playerentity.getCooldownTracker().setCooldown(itemstack.getItem(), 100);
                        this.world.setEntityState(playerentity, (byte) 30);
                    }
                }
            }

            this.applyEnchantments(this, entityIn);
        }

        return flag;
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
            getDataManager().set(LORD_ID, Optional.of(playerMinionController.getUUID()));
        });
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
    }

    @Nonnull
    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return getInventory().map(IMinionInventory::getInventoryArmor).orElse(EMPTY_LIST);
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

    public int getAvailableInvSize() {
        return 9; //TODO integrate with minion data maybe via data parameter as required client side
    }

    public abstract List<IMinionTask<?>> getAvailableTasks();

    @Override
    public Optional<IMinionTask.IMinionTaskDesc> getCurrentTask() {
        return minionData != null ? Optional.of(minionData.getCurrentTaskDesc()) : Optional.empty();
    }

    /**
     * @return Return player (lord) if they are currently interacting with this minion
     */
    @Nonnull
    public Optional<PlayerEntity> getForceLookTarget() {
        return Optional.ofNullable(interactingPlayer);
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

    @Nullable
    @Override
    public GameProfile getOverlayPlayerProfile() {
        if (skinProfile == null) {
            this.getLordID().ifPresent(id -> {
                skinProfile = new GameProfile(id, "Dummy");
                PlayerSkinHelper.updateGameProfileAsync(skinProfile, (profile) -> this.skinProfile = profile);
            });
        }
        return skinProfile;
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
    public EntitySize getSize(@Nonnull Pose p_213305_1_) {
        return super.getSize(p_213305_1_).scale(getScale());
    }

    public boolean isTaskLocked() {
        return minionData != null && minionData.isTaskLocked();
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (convertCounter > 0) {
            convertCounter--;
        }
        if (!this.world.isRemote && !this.isValid() && this.isAlive()) {
            LOGGER.warn("Minion without lord.");
            this.remove();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void loadUpdateFromNBT(CompoundNBT nbt) {
        if (nbt.contains("data_type")) {
            MinionData data = MinionData.fromNBT(nbt);
            try {
                this.onMinionDataReceived((T) data);
                this.minionData = (T) data;
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

    @Override
    public void onDeath(@Nonnull DamageSource p_70645_1_) {
        super.onDeath(p_70645_1_);
        if (this.playerMinionController != null) {
            this.getLordOpt().map(ILordPlayer::getPlayer).ifPresent(p -> p.sendStatusMessage(new TranslationTextComponent("text.vampirism.minion.died", this.getDisplayName()), true));
            this.playerMinionController.markDeadAndReleaseMinionSlot(minionId, token);
            this.playerMinionController = null;
        }
    }

    public void onTaskChanged() {
        HelperLib.sync(this);
    }

    @OnlyIn(Dist.CLIENT)
    public void openAppearanceScreen() {
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
    public void readSpawnData(PacketBuffer additionalData) {
        convertCounter = additionalData.readVarInt();
    }

    @Override
    @Deprecated
    public void recallMinion() {
        this.remove();
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

    @Override
    public void setAttackTarget(@Nullable LivingEntity entitylivingbaseIn) {
        if (entitylivingbaseIn == null || hardAttackPredicate.test(entitylivingbaseIn))
            super.setAttackTarget(entitylivingbaseIn);
    }

    @Override
    public void setCustomName(@Nullable ITextComponent name) {
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
    public void setInteractingPlayer(@Nullable PlayerEntity player) {
        this.interactingPlayer = player;
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

    public abstract boolean shouldRenderLordSkin();

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        if (isValid()) {
            this.getLordID().ifPresent(id -> nbt.putUniqueId("lord", id));
            nbt.putInt("minion_id", minionId);
            nbt.putInt("minion_token", token);
        }
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

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeVarInt(convertCounter);
    }

    @Nullable
    protected ILordPlayer getLord() {
        return this.getLordID().map(this.world::getPlayerByUuid).filter(PlayerEntity::isAlive).map(FactionPlayerHandler::get).orElse(null);
    }

    protected Optional<UUID> getLordID() {
        return this.getDataManager().get(LORD_ID);
    }

    protected boolean isValid() {
        return this.playerMinionController != null;
    }

    protected boolean isLord(PlayerEntity p) {
        return this.getLordID().map(id -> id.equals(p.getUniqueID())).orElse(false);
    }

    /**
     * Called when valid minion data is received on world load.
     * Can  be called client and server side
     */
    protected void onMinionDataReceived(@Nonnull T data) {
    }

    @Override
    protected boolean processInteract(PlayerEntity player, Hand hand) {
        if (isLord(player)) {
            if (player instanceof ServerPlayerEntity) {
                NetworkHooks.openGui((ServerPlayerEntity) player, new SimpleNamedContainerProvider((id, playerInventory, playerIn) -> MinionContainer.create(id, playerInventory, this), new TranslationTextComponent("text.vampirism.name").appendSibling(this.getMinionData().map(MinionData::getFormattedName).orElse(new StringTextComponent("Minion")))), buf -> buf.writeVarInt(this.getEntityId()));
            }
            return true;
        }
        return false;
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.getDataManager().register(LORD_ID, Optional.empty());

    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new ForceLookEntityGoal<>(this));
        this.goalSelector.addGoal(2, new OpenDoorGoal(this, true));

        this.goalSelector.addGoal(4, new FollowLordGoal(this, 1.1));

        this.goalSelector.addGoal(9, new MoveToTaskCenterGoal(this));
        this.goalSelector.addGoal(10, new LookAtClosestVisibleGoal(this, PlayerEntity.class, 20F, 0.6F));
        this.goalSelector.addGoal(10, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new DefendAreaGoal(this));
        this.targetSelector.addGoal(2, new DefendLordGoal(this));

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

    private void handleLoadedMinionData(@Nonnull T data) {
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(data.getMaxHealth());
        super.setHealth(data.getHealth());
        super.setCustomName(data.getFormattedName());
        try {
            this.onMinionDataReceived(data);
        } catch (ClassCastException e) {
            LOGGER.error("Failed to cast minion data. Maybe the correct data was not registered", e);
            this.remove();
        }
    }
}
