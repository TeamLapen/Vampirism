package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.hunter.IBasicHunter;
import de.teamlapen.vampirism.api.entity.hunter.IVampirismCrossbowUser;
import de.teamlapen.vampirism.api.items.IVampirismCrossbow;
import de.teamlapen.vampirism.api.world.ICaptureAttributes;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.effects.BadOmenEffect;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.action.ActionHandlerEntity;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.goals.AttackRangedCrossbowGoal;
import de.teamlapen.vampirism.entity.goals.AttackVillageGoal;
import de.teamlapen.vampirism.entity.goals.DefendVillageGoal;
import de.teamlapen.vampirism.entity.goals.ForceLookEntityGoal;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.inventory.container.HunterBasicContainer;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import de.teamlapen.vampirism.util.HunterVillageData;
import de.teamlapen.vampirism.world.MinionWorldData;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.PatrollerEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.Structure;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;


/**
 * Exists in {@link BasicHunterEntity#MAX_LEVEL}+1 different levels
 */
public class BasicHunterEntity extends HunterBaseEntity implements IBasicHunter, ForceLookEntityGoal.TaskOwner, IVampirismCrossbowUser, IEntityActionUser {
    private static final DataParameter<Integer> LEVEL = EntityDataManager.defineId(BasicHunterEntity.class, DataSerializers.INT);
    private static final DataParameter<Boolean> SWINGING_ARMS = EntityDataManager.defineId(BasicHunterEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> WATCHED_ID = EntityDataManager.defineId(BasicHunterEntity.class, DataSerializers.INT);
    private static final DataParameter<Integer> TYPE = EntityDataManager.defineId(BasicHunterEntity.class, DataSerializers.INT);
    private static final DataParameter<Boolean> IS_CHARGING_CROSSBOW = EntityDataManager.defineId(BasicHunterEntity.class, DataSerializers.BOOLEAN);


    private static final ITextComponent name = new TranslationTextComponent("container.hunter");

    public static AttributeModifierMap.MutableAttribute getAttributeBuilder() {
        return VampirismEntity.getAttributeBuilder()
                .add(Attributes.MAX_HEALTH, BalanceMobProps.mobProps.VAMPIRE_HUNTER_MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BalanceMobProps.mobProps.VAMPIRE_HUNTER_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, BalanceMobProps.mobProps.VAMPIRE_HUNTER_SPEED);
    }


    private final int MAX_LEVEL = 3;
    private final MeleeAttackGoal attackMelee;
    /**
     * available actions for AI task & task
     */
    private final ActionHandlerEntity<?> entityActionHandler;
    private final EntityClassType entityclass;
    private final EntityActionTier entitytier;
    /**
     * Player currently being trained otherwise null
     */
    private @Nullable
    PlayerEntity trainee;
    /**
     * Stores the x axis angle between when targeting an enemy with the crossbow
     */
    private float targetAngle = 0;
    private @Nullable
    ICaptureAttributes villageAttributes;
    //Village capture --------------------------------------------------------------------------------------------------
    private boolean attack;

    public BasicHunterEntity(EntityType<? extends BasicHunterEntity> type, World world) {
        super(type, world, true);
        saveHome = true;
        ((GroundPathNavigator) this.getNavigation()).setCanOpenDoors(true);

        this.setDontDropEquipment();

        this.attackMelee = new MeleeAttackGoal(this, 1.0, false);
        this.updateCombatTask();
        entitytier = EntityActionTier.Medium;
        entityclass = EntityClassType.getRandomClass(this.getRandom());
        IEntityActionUser.applyAttributes(this);
        this.entityActionHandler = new ActionHandlerEntity<>(this);
        this.enableImobConversion();
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("level", getLevel());
        nbt.putBoolean("crossbow", isHoldingCrossbow());
        nbt.putBoolean("attack", attack);
        nbt.putInt("type", getEntityTextureType());
        nbt.putInt("entityclasstype", EntityClassType.getID(entityclass));
        if (entityActionHandler != null) {
            entityActionHandler.write(nbt);
        }
    }

    @Override
    public void attackVillage(ICaptureAttributes attributes) {
        this.villageAttributes = attributes;
        this.attack = true;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (trainee != null && !(trainee.containerMenu instanceof HunterBasicContainer)) {
            this.trainee = null;
        }
        if (!level.isClientSide) {
            LivingEntity target = getTarget();
            int id = target == null ? 0 : target.getId();
            this.updateWatchedId(id);
            if (this.tickCount % 512 == 0 && this.getRandom().nextInt(500) == 0) { //Very very very randomly decide to walk to a random location
                BlockPos randomDestination = new BlockPos(this.getRandom().nextInt(30000) - 15000, 100, this.getRandom().nextInt(30000) - 15000);
                randomDestination = this.level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, randomDestination);
                this.setHomeArea(randomDestination, 10);
            }
        } else {
            targetAngle = 0;
            if (isSwingingArms()) {
                int id = getWatchedId();
                if (id != 0) {
                    Entity target = level.getEntity(id);
                    if (target instanceof LivingEntity) {

                        double dx = target.getX() - (this).getX();
                        double dy = target.getY() - this.getY();
                        double dz = target.getZ() - this.getZ();
                        float dist = MathHelper.sqrt(dx * dx + dz * dz);
                        targetAngle = (float) Math.atan(dy / dist);
                    }
                }
            }

        }
        if (entityActionHandler != null) {
            entityActionHandler.handle();
        }
    }

    /**
     * Assumes preconditions as been met. Checks conditions but does not give feedback to user
     *
     * @param lord
     */
    public void convertToMinion(PlayerEntity lord) {
        FactionPlayerHandler.getOpt(lord).ifPresent(fph -> {
            if (fph.getMaxMinions() > 0) {
                MinionWorldData.getData(lord.level).map(w -> w.getOrCreateController(fph)).ifPresent(controller -> {
                    if (controller.hasFreeMinionSlot()) {
                        if (fph.getCurrentFaction() == this.getFaction()) {
                            HunterMinionEntity.HunterMinionData data = new HunterMinionEntity.HunterMinionData("Minion", this.getEntityTextureType(), this.getEntityTextureType() % 4, false);
                            int id = controller.createNewMinionSlot(data, ModEntities.HUNTER_MINION.get());
                            if (id < 0) {
                                LOGGER.error("Failed to get minion slot");
                                return;
                            }
                            HunterMinionEntity minion = ModEntities.HUNTER_MINION.get().create(this.level);
                            minion.claimMinionSlot(id, controller);
                            minion.copyPosition(this);
                            minion.markAsConverted();
                            controller.activateTask(0, MinionTasks.STAY.get());
                            UtilLib.replaceEntity(this, minion);

                        } else {
                            LOGGER.warn("Wrong faction for minion");
                        }
                    } else {
                        LOGGER.warn("No free slot");
                    }
                });
            } else {
                LOGGER.error("Can't have minions");
            }
        });
    }

    @Override
    public void defendVillage(ICaptureAttributes attributes) {
        this.villageAttributes = attributes;
        this.attack = false;
    }

    @Override
    public ActionHandlerEntity getActionHandler() {
        return entityActionHandler;
    }

    @Override
    public @Nullable
    ICaptureAttributes getCaptureInfo() {
        return this.villageAttributes;
    }

    @Override
    public EntityClassType getEntityClass() {
        return entityclass;
    }

    @Override
    public void die(DamageSource cause) {
        if (this.villageAttributes == null) {
            BadOmenEffect.handlePotentialBannerKill(cause.getEntity(), this);
        }
        super.die(cause);
    }

    @Override
    public EntityActionTier getEntityTier() {
        return entitytier;
    }

    @Nonnull
    @Override
    public Optional<PlayerEntity> getForceLookTarget() {
        return Optional.ofNullable(trainee);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean flag = super.doHurtTarget(entity);
        if (flag && this.getMainHandItem().isEmpty()) {
            this.swing(Hand.MAIN_HAND);  //Swing stake if nothing else is held
        }
        return flag;
    }

    @Nullable
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        if (!(reason == SpawnReason.SPAWN_EGG || reason == SpawnReason.BUCKET || reason == SpawnReason.CONVERSION || reason == SpawnReason.COMMAND) && this.getRandom().nextInt(50) == 0) {
            this.setItemSlot(EquipmentSlotType.HEAD, HunterVillageData.createBanner());
        }
        ILivingEntityData livingData = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);

        if (this.getRandom().nextInt(4) == 0) {
            this.setLeftHanded(true);
            Item crossBow = getLevel() > 1 ? ModItems.ENHANCED_CROSSBOW.get() : ModItems.BASIC_CROSSBOW.get();
            this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(crossBow));

        } else {
            this.setLeftHanded(false);
        }

        this.updateCombatTask();
        return livingData;
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    public float getTargetAngle() {
        return targetAngle;
    }

    @Override
    public @Nullable
    AxisAlignedBB getTargetVillageArea() {
        return villageAttributes == null ? null : villageAttributes.getVillageArea();
    }

    @Override
    public boolean isAttackingVillage() {
        return villageAttributes != null && attack;
    }

    @Override
    public int getEntityTextureType() {
        return Math.max(0, getEntityData().get(TYPE));
    }

    @Override
    public boolean isDefendingVillage() {
        return villageAttributes != null && !attack;
    }

    @Override
    public boolean isLookingForHome() {
        return getHome() == null;
    }

    @Override
    public int getLevel() {
        return getEntityData().get(LEVEL);
    }

    @Override
    public void setLevel(int level) {
        if (level >= 0) {
            getEntityData().set(LEVEL, level);
            this.updateEntityAttributes();
            if (level == 3) {
                this.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 1000000, 1));
            }
        }
    }

    @Override
    public void makeNormalHunter() {
        super.setHome(null);
        this.disableMoveTowardsRestriction();
    }

    @Override
    public void makeVillageHunter(AxisAlignedBB box) {
        super.setHome(box);
        this.setMoveTowardsRestriction(MOVE_TO_RESTRICT_PRIO, true);
    }

    public boolean isSwingingArms() {
        return this.getEntityData().get(SWINGING_ARMS);
    }

    private void setSwingingArms(boolean b) {
        this.getEntityData().set(SWINGING_ARMS, b);
    }

    //Entityactions ----------------------------------------------------------------------------------------------------

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (getEntityData().get(TYPE) == -1) {
            getEntityData().set(TYPE, this.getRandom().nextInt(TYPES));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tagCompund) {
        super.readAdditionalSaveData(tagCompund);
        if (tagCompund.contains("level")) {
            setLevel(tagCompund.getInt("level"));
        }

        if (tagCompund.contains("crossbow") && tagCompund.getBoolean("crossbow")) {
            this.setLeftHanded(true);
            this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(ModItems.BASIC_CROSSBOW.get()));
        } else {
            this.setLeftHanded(false);
            this.setItemSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
        }
        this.updateCombatTask();
        if (tagCompund.contains("attack")) {
            this.attack = tagCompund.getBoolean("attack");
        }
        if (tagCompund.contains("type")) {
            int t = tagCompund.getInt("type");
            getEntityData().set(TYPE, t < TYPES && t >= 0 ? t : -1);
        }

        if (entityActionHandler != null) {
            entityActionHandler.read(tagCompund);
        }
    }

    @Override
    public void stopVillageAttackDefense() {
        this.setCustomName(null);
        this.villageAttributes = null;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return super.removeWhenFarAway(distanceToClosestPlayer) && getHome() != null;
    }

    @Override
    public int suggestLevel(Difficulty d) {
        switch (this.random.nextInt(6)) {
            case 0:
                return (int) (d.minPercLevel / 100F * MAX_LEVEL);
            case 1:
                return (int) (d.avgPercLevel / 100F * MAX_LEVEL);
            case 2:
                return (int) (d.maxPercLevel / 100F * MAX_LEVEL);
            default:
                return this.random.nextInt(MAX_LEVEL + 1);
        }

    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(LEVEL, -1);
        this.getEntityData().define(SWINGING_ARMS, false);
        this.getEntityData().define(WATCHED_ID, 0);
        this.getEntityData().define(TYPE, -1);
        this.getEntityData().define(IS_CHARGING_CROSSBOW, false);
    }

    @Override
    protected int getExperienceReward(PlayerEntity player) {
        return 6 + getLevel();
    }

    //IMob -------------------------------------------------------------------------------------------------------------
    @Override
    protected EntityType<?> getIMobTypeOpt(boolean iMob) {
        return iMob ? ModEntities.HUNTER_IMOB.get() : ModEntities.HUNTER.get();
    }

    @Override
    protected ActionResultType mobInteract(PlayerEntity player, Hand hand) { //processInteract
        if (hand == Hand.MAIN_HAND && tryCureSanguinare(player)) return ActionResultType.SUCCESS;
        int hunterLevel = VampirismPlayerAttributes.get(player).hunterLevel;
        if (this.isAlive() && !player.isShiftKeyDown() && hand == Hand.MAIN_HAND) {
            if (!level.isClientSide) {
                if (HunterLevelingConf.instance().isLevelValidForBasicHunter(hunterLevel + 1)) {
                    if (trainee == null) {
                        player.openMenu(new SimpleNamedContainerProvider((id, playerInventory, playerEntity) -> new HunterBasicContainer(id, playerInventory, this), name));
                        trainee = player;
                        this.getNavigation().stop();
                    } else {
                        player.sendMessage(new TranslationTextComponent("text.vampirism.i_am_busy_right_now"), Util.NIL_UUID);
                    }
                    return ActionResultType.SUCCESS;
                } else if (hunterLevel > 0) {
                    FactionPlayerHandler.getOpt(player).ifPresent(fph -> {
                        if (fph.getMaxMinions() > 0) {
                            ItemStack heldItem = player.getItemInHand(hand);

                            if (this.getLevel() > 0) {
                                if (heldItem.getItem() == ModItems.HUNTER_MINION_EQUIPMENT.get()) {
                                    player.displayClientMessage(new TranslationTextComponent("text.vampirism.basic_hunter.minion.unavailable"), true);
                                }
                            } else {
                                boolean freeSlot = MinionWorldData.getData(player.level).map(data -> data.getOrCreateController(fph)).map(PlayerMinionController::hasFreeMinionSlot).orElse(false);
                                player.displayClientMessage(new TranslationTextComponent("text.vampirism.basic_hunter.minion.available"), false);
                                if (heldItem.getItem() == ModItems.HUNTER_MINION_EQUIPMENT.get()) {
                                    if (!freeSlot) {
                                        player.displayClientMessage(new TranslationTextComponent("text.vampirism.basic_hunter.minion.no_free_slot"), false);
                                    } else {
                                        player.displayClientMessage(new TranslationTextComponent("text.vampirism.basic_hunter.minion.start_serving"), false);
                                        convertToMinion(player);
                                        if (!player.abilities.instabuild) heldItem.shrink(1);
                                    }
                                } else if (freeSlot) {
                                    player.displayClientMessage(new TranslationTextComponent("text.vampirism.basic_hunter.minion.require_equipment", UtilLib.translate(ModItems.HUNTER_MINION_EQUIPMENT.get().getDescriptionId())), false);
                                }
                            }
                        } else {
                            player.displayClientMessage(new TranslationTextComponent("text.vampirism.basic_hunter.cannot_train_you_any_further"), false);
                        }
                    });
                    return ActionResultType.SUCCESS;
                }

            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void setChargingCrossbow(boolean p_213671_1_) {
        this.getEntityData().set(IS_CHARGING_CROSSBOW, p_213671_1_);
    }

    @Override
    public void shootCrossbowProjectile(LivingEntity p_230284_1_, ItemStack p_230284_2_, ProjectileEntity p_230284_3_, float p_230284_4_) {
        this.shootCrossbowProjectile(this, p_230284_1_, p_230284_3_, p_230284_4_, 1.6f);
    }

    @Override
    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    @Override
    public void performRangedAttack(LivingEntity p_82196_1_, float p_82196_2_) {
        this.performCrossbowAttack(this, 1.6f);
    }

    @Override
    public boolean isHoldingCrossbow(){
        return this.isHolding(IVampirismCrossbow.class::isInstance);
    }

    @Override
    public boolean isChargingCrossbow() {
        return this.getEntityData().get(IS_CHARGING_CROSSBOW);
    }

    @Nonnull
    @Override
    public ItemStack getProjectile(ItemStack stack) {
        if (stack.getItem() instanceof IVampirismCrossbow) {
            return ModItems.CROSSBOW_ARROW_NORMAL.get().getDefaultInstance();
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        //Attack task is added in #updateCombatTasks which is e.g. called at end of constructor
        this.goalSelector.addGoal(3, new ForceLookEntityGoal<>(this));
        this.goalSelector.addGoal(3, new AttackRangedCrossbowGoal<>(this, 0.6, 60));
        this.goalSelector.addGoal(5, new MoveThroughVillageGoal(this, 0.7F, false, 300, () -> false));
        this.goalSelector.addGoal(6, new RandomWalkingGoal(this, 0.7, 50));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 13F));
        this.goalSelector.addGoal(8, new LookAtGoal(this, VampireBaseEntity.class, 17F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new AttackVillageGoal<>(this));
        this.targetSelector.addGoal(2, new DefendVillageGoal<>(this));//Should automatically be mutually exclusive with  attack village
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, false, false, null)));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<CreatureEntity>(this, CreatureEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)) {
            @Override
            protected double getFollowDistance() {
                return super.getFollowDistance() / 2;
            }
        });
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, ZombieEntity.class, true, true));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, PatrollerEntity.class, 5, true, true, (living) -> UtilLib.isInsideStructure(living, Structure.VILLAGE)));        //Also check the priority of tasks that are dynamically added. See top of class
    }

    protected void updateEntityAttributes() {
        int l = Math.max(getLevel(), 0);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_HUNTER_MAX_HEALTH + BalanceMobProps.mobProps.VAMPIRE_HUNTER_MAX_HEALTH_PL * l);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_HUNTER_ATTACK_DAMAGE + BalanceMobProps.mobProps.VAMPIRE_HUNTER_ATTACK_DAMAGE_PL * l);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_HUNTER_SPEED);
    }

    private int getWatchedId() {
        return getEntityData().get(WATCHED_ID);
    }

    private void updateCombatTask() {
        if (true) return;
        this.goalSelector.removeGoal(attackMelee);
        ItemStack stack = this.getMainHandItem();
        if (!stack.isEmpty() && stack.getItem() instanceof IVampirismCrossbow) {
        } else {
            this.goalSelector.addGoal(2, this.attackMelee);
        }
    }

    private void updateWatchedId(int id) {
        getEntityData().set(WATCHED_ID, id);
    }

    public static class IMob extends BasicHunterEntity implements net.minecraft.entity.monster.IMob {

        public IMob(EntityType<? extends BasicHunterEntity> type, World world) {
            super(type, world);
        }

    }
}
