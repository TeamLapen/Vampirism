package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.hunter.IBasicHunter;
import de.teamlapen.vampirism.api.world.ICaptureAttributes;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
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
import de.teamlapen.vampirism.items.VampirismItemCrossbow;
import de.teamlapen.vampirism.player.VampirismPlayer;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.util.HunterVillageData;
import de.teamlapen.vampirism.util.SharedMonsterAttributes;
import de.teamlapen.vampirism.world.MinionWorldData;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.PatrollerEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
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
public class BasicHunterEntity extends HunterBaseEntity implements IBasicHunter, ForceLookEntityGoal.TaskOwner, AttackRangedCrossbowGoal.IAttackWithCrossbow, IEntityActionUser {
    private static final DataParameter<Integer> LEVEL = EntityDataManager.createKey(BasicHunterEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> SWINGING_ARMS = EntityDataManager.createKey(BasicHunterEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> WATCHED_ID = EntityDataManager.createKey(BasicHunterEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TYPE = EntityDataManager.createKey(BasicHunterEntity.class, DataSerializers.VARINT);

    private static final ITextComponent name = new TranslationTextComponent("container.hunter");

    public static AttributeModifierMap.MutableAttribute getAttributeBuilder() {
        return VampirismEntity.getAttributeBuilder()
                .createMutableAttribute(SharedMonsterAttributes.MAX_HEALTH, BalanceMobProps.mobProps.VAMPIRE_HUNTER_MAX_HEALTH)
                .createMutableAttribute(SharedMonsterAttributes.ATTACK_DAMAGE, BalanceMobProps.mobProps.VAMPIRE_HUNTER_ATTACK_DAMAGE)
                .createMutableAttribute(SharedMonsterAttributes.MOVEMENT_SPEED, BalanceMobProps.mobProps.VAMPIRE_HUNTER_SPEED);
    }


    private final int MAX_LEVEL = 3;
    private final MeleeAttackGoal attackMelee;
    private final AttackRangedCrossbowGoal<BasicHunterEntity> attackRange;

    /**
     * Player currently being trained otherwise null
     */
    private @Nullable
    PlayerEntity trainee;

    /**
     * Stores the x axis angle between when targeting an enemy with the crossbow
     */
    private float targetAngle = 0;

    public BasicHunterEntity(EntityType<? extends BasicHunterEntity> type, World world) {
        super(type, world, true);
        saveHome = true;
        ((GroundPathNavigator) this.getNavigator()).setBreakDoors(true);

        this.setDontDropEquipment();

        this.attackMelee = new MeleeAttackGoal(this, 1.0, false);
        this.attackRange = new AttackRangedCrossbowGoal<>(this, 0.6, 60, 20);
        this.updateCombatTask();
        entitytier = EntityActionTier.Medium;
        entityclass = EntityClassType.getRandomClass(this.getRNG());
        IEntityActionUser.applyAttributes(this);
        this.entityActionHandler = new ActionHandlerEntity<>(this);
        this.enableImobConversion();
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        boolean flag = super.attackEntityAsMob(entity);
        if (flag && this.getHeldItemMainhand().isEmpty()) {
            this.swingArm(Hand.MAIN_HAND);  //Swing stake if nothing else is held
        }
        return flag;
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return super.canDespawn(distanceToClosestPlayer) && getHome() != null;
    }

    @Override
    public void attackVillage(ICaptureAttributes attributes) {
        this.villageAttributes = attributes;
        this.attack = true;
    }

    @Override
    public @Nonnull
    ItemStack getArrowStackForAttack(LivingEntity target) {
        return new ItemStack(ModItems.crossbow_arrow_normal);
    }

    @Override
    public int getLevel() {
        return getDataManager().get(LEVEL);
    }

    @Override
    public void setLevel(int level) {
        if (level >= 0) {
            getDataManager().set(LEVEL, level);
            this.updateEntityAttributes();
            if (level == 3) {
                this.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 1000000, 1));
            }
        }
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    public float getTargetAngle() {
        return targetAngle;
    }

    @Nonnull
    @Override
    public Optional<PlayerEntity> getForceLookTarget() {
        return Optional.ofNullable(trainee);
    }



    @Override
    public boolean isCrossbowInMainhand() {
        return !this.getHeldItemMainhand().isEmpty() && this.getHeldItemMainhand().getItem() instanceof VampirismItemCrossbow;
    }

    @Override
    public boolean isLookingForHome() {
        return getHome() == null;
    }

    public boolean isSwingingArms() {
        return this.getDataManager().get(SWINGING_ARMS);
    }

    private void setSwingingArms(boolean b) {
        this.getDataManager().set(SWINGING_ARMS, b);
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (trainee != null && !(trainee.openContainer instanceof HunterBasicContainer)) {
            this.trainee = null;
        }
        if (!world.isRemote) {
            LivingEntity target = getAttackTarget();
            int id = target == null ? 0 : target.getEntityId();
            this.updateWatchedId(id);
            if (this.ticksExisted % 512 == 0 && this.getRNG().nextInt(500) == 0) { //Very very very randomly decide to walk to a random location
                BlockPos randomDestination = new BlockPos(this.getRNG().nextInt(30000) - 15000, 100, this.getRNG().nextInt(30000) - 15000);
                randomDestination = this.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, randomDestination);
                this.setHomeArea(randomDestination, 10);
            }
        } else {
            targetAngle = 0;
            if (isSwingingArms()) {
                int id = getWatchedId();
                if (id != 0) {
                    Entity target = world.getEntityByID(id);
                    if (target instanceof LivingEntity) {

                        double dx = target.getPosX() - (this).getPosX();
                        double dy = target.getPosY() - this.getPosY();
                        double dz = target.getPosZ() - this.getPosZ();
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

    private @Nullable
    ICaptureAttributes villageAttributes;

    @Override
    public int getEntityTextureType() {
        return Math.max(0, getDataManager().get(TYPE));
    }

    @Override
    public void startTargeting() {
        this.setSwingingArms(true);
    }

    @Override
    public void stopTargeting() {
        this.setSwingingArms(false);
    }

    /**
     * Assumes preconditions as been met. Checks conditions but does not give feedback to user
     *
     * @param lord
     */
    public void convertToMinion(PlayerEntity lord) {
        FactionPlayerHandler.getOpt(lord).ifPresent(fph -> {
            if (fph.getMaxMinions() > 0) {
                MinionWorldData.getData(lord.world).map(w -> w.getOrCreateController(fph)).ifPresent(controller -> {
                    if (controller.hasFreeMinionSlot()) {
                        if (fph.getCurrentFaction() == this.getFaction()) {
                            HunterMinionEntity.HunterMinionData data = new HunterMinionEntity.HunterMinionData("Minion", this.getEntityTextureType(), this.getEntityTextureType() % 4, false);
                            int id = controller.createNewMinionSlot(data, ModEntities.hunter_minion);
                            if (id < 0) {
                                LOGGER.error("Failed to get minion slot");
                                return;
                            }
                            HunterMinionEntity minion = ModEntities.hunter_minion.create(this.world);
                            minion.claimMinionSlot(id, controller);
                            minion.copyLocationAndAnglesFrom(this);
                            minion.markAsConverted();
                            controller.activateTask(0, MinionTasks.stay);
                            UtilLib.replaceEntity(this,minion);

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
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (getDataManager().get(TYPE) == -1) {
            getDataManager().set(TYPE, this.getRNG().nextInt(TYPES));
        }
    }

    @Override
    protected int getExperiencePoints(PlayerEntity player) {
        return 6 + getLevel();
    }

    @Override
    public int suggestLevel(Difficulty d) {
        switch (this.rand.nextInt(6)) {
            case 0:
                return (int) (d.minPercLevel / 100F * MAX_LEVEL);
            case 1:
                return (int) (d.avgPercLevel / 100F * MAX_LEVEL);
            case 2:
                return (int) (d.maxPercLevel / 100F * MAX_LEVEL);
            default:
                return this.rand.nextInt(MAX_LEVEL + 1);
        }

    }


    @Override
    protected void registerData() {
        super.registerData();
        this.getDataManager().register(LEVEL, -1);
        this.getDataManager().register(SWINGING_ARMS, false);
        this.getDataManager().register(WATCHED_ID, 0);
        this.getDataManager().register(TYPE, -1);
    }

    @Override
    protected ActionResultType func_230254_b_(PlayerEntity player, Hand hand) { //processInteract
        if (hand == Hand.MAIN_HAND && tryCureSanguinare(player)) return ActionResultType.SUCCESS;
        int hunterLevel = HunterPlayer.getOpt(player).map(VampirismPlayer::getLevel).orElse(0);
        if (this.isAlive() && !player.isSneaking() && hand == Hand.MAIN_HAND) {
            if (!world.isRemote) {
                if (HunterLevelingConf.instance().isLevelValidForBasicHunter(hunterLevel + 1)) {
                    if (trainee == null) {
                        player.openContainer(new SimpleNamedContainerProvider((id, playerInventory, playerEntity) -> new HunterBasicContainer(id, playerInventory, this), name));
                        trainee = player;
                        this.getNavigator().clearPath();
                    } else {
                        player.sendMessage(new TranslationTextComponent("text.vampirism.i_am_busy_right_now"), Util.DUMMY_UUID);
                    }
                    return ActionResultType.SUCCESS;
                } else if (hunterLevel > 0) {
                    FactionPlayerHandler.getOpt(player).ifPresent(fph -> {
                        if (fph.getMaxMinions() > 0) {
                            ItemStack heldItem = player.getHeldItem(hand);

                            if (this.getLevel() > 0) {
                                if (heldItem.getItem() == ModItems.hunter_minion_equipment) {
                                    player.sendStatusMessage(new TranslationTextComponent("text.vampirism.basic_hunter.minion.unavailable"), true);
                                }
                            } else {
                                boolean freeSlot = MinionWorldData.getData(player.world).map(data -> data.getOrCreateController(fph)).map(PlayerMinionController::hasFreeMinionSlot).orElse(false);
                                player.sendStatusMessage(new TranslationTextComponent("text.vampirism.basic_hunter.minion.available"), false);
                                if (heldItem.getItem() == ModItems.hunter_minion_equipment) {
                                    if (!freeSlot) {
                                        player.sendStatusMessage(new TranslationTextComponent("text.vampirism.basic_hunter.minion.no_free_slot"), false);
                                    } else {
                                        player.sendStatusMessage(new TranslationTextComponent("text.vampirism.basic_hunter.minion.start_serving"), false);
                                        convertToMinion(player);
                                        if (!player.abilities.isCreativeMode) heldItem.shrink(1);
                                    }
                                } else if (freeSlot) {
                                    player.sendStatusMessage(new TranslationTextComponent("text.vampirism.basic_hunter.minion.require_equipment", UtilLib.translate(ModItems.hunter_minion_equipment.getTranslationKey())), false);
                                }
                            }
                        } else {
                            player.sendStatusMessage(new TranslationTextComponent("text.vampirism.basic_hunter.cannot_train_you_any_further"), false);
                        }
                    });
                    return ActionResultType.SUCCESS;
                }

            }
        }
        return super.func_230254_b_(player, hand);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        //Attack task is added in #updateCombatTasks which is e.g. called at end of constructor
        this.goalSelector.addGoal(3, new ForceLookEntityGoal<>(this));
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
            protected double getTargetDistance() {
                return super.getTargetDistance() / 2;
            }
        });
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, ZombieEntity.class, true, true));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, PatrollerEntity.class, 5, true, true, (living) -> UtilLib.isInsideStructure(living, Structure.VILLAGE)));        //Also check the priority of tasks that are dynamically added. See top of class
    }

    private int getWatchedId() {
        return getDataManager().get(WATCHED_ID);
    }

    private void updateCombatTask() {
            this.goalSelector.removeGoal(attackMelee);
            this.goalSelector.removeGoal(attackRange);
            ItemStack stack = this.getHeldItemMainhand();
            if (!stack.isEmpty() && stack.getItem() instanceof VampirismItemCrossbow) {
                this.goalSelector.addGoal(2, this.attackRange);
            } else {
                this.goalSelector.addGoal(2, this.attackMelee);
            }
    }

    private void updateWatchedId(int id) {
        getDataManager().set(WATCHED_ID, id);
    }

    @Override
    public void readAdditional(CompoundNBT tagCompund) {
        super.readAdditional(tagCompund);
        if (tagCompund.contains("level")) {
            setLevel(tagCompund.getInt("level"));
        }

        if (tagCompund.contains("crossbow") && tagCompund.getBoolean("crossbow")) {
            this.setLeftHanded(true);
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(ModItems.basic_crossbow));
        } else {
            this.setLeftHanded(false);
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
        }
        this.updateCombatTask();
        if (tagCompund.contains("attack")) {
            this.attack = tagCompund.getBoolean("attack");
        }
        if (tagCompund.contains("type")) {
            int t = tagCompund.getInt("type");
            getDataManager().set(TYPE, t < TYPES && t >= 0 ? t : -1);
        }

        if (entityActionHandler != null) {
            entityActionHandler.read(tagCompund);
        }
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        nbt.putInt("level", getLevel());
        nbt.putBoolean("crossbow", isCrossbowInMainhand());
        nbt.putBoolean("attack", attack);
        nbt.putInt("type", getEntityTextureType());
        nbt.putInt("entityclasstype", EntityClassType.getID(entityclass));
        if (entityActionHandler != null) {
            entityActionHandler.write(nbt);
        }
    }

    @Override
    public void onDeath(DamageSource cause) {
        if (cause.getTrueSource() instanceof PlayerEntity && this.villageAttributes == null) {
            if (this.getFaction().getVillageData().isBanner(this.getItemStackFromSlot(EquipmentSlotType.HEAD))) {
                ((PlayerEntity) cause.getTrueSource()).addPotionEffect(new EffectInstance(ModEffects.bad_omen_hunter, 120000,0,false,false, true));
            }
        }
        super.onDeath(cause);
    }

    //Entityactions ----------------------------------------------------------------------------------------------------
    /**
     * available actions for AI task & task
     */
    private final ActionHandlerEntity<?> entityActionHandler;
    private final EntityClassType entityclass;
    private final EntityActionTier entitytier;

    @Override
    public EntityClassType getEntityClass() {
        return entityclass;
    }

    @Override
    public EntityActionTier getEntityTier() {
        return entitytier;
    }

    @Override
    public ActionHandlerEntity getActionHandler() {
        return entityActionHandler;
    }

    //IMob -------------------------------------------------------------------------------------------------------------
    @Override
    protected EntityType<?> getIMobTypeOpt(boolean iMob) {
        return iMob ? ModEntities.hunter_imob : ModEntities.hunter;
    }

    public static class IMob extends BasicHunterEntity implements net.minecraft.entity.monster.IMob {

        public IMob(EntityType<? extends BasicHunterEntity> type, World world) {
            super(type, world);
        }

    }

    //Village capture --------------------------------------------------------------------------------------------------
    private boolean attack;

    @Override
    public void defendVillage(ICaptureAttributes attributes) {
        this.villageAttributes = attributes;
        this.attack = false;
    }

    @Override
    public void stopVillageAttackDefense() {
        this.setCustomName(null);
        this.villageAttributes = null;
    }

    @Override
    public boolean isAttackingVillage() {
        return villageAttributes != null && attack;
    }

    @Override
    public boolean isDefendingVillage() {
        return villageAttributes != null && !attack;
    }

    @Override
    public @Nullable
    ICaptureAttributes getCaptureInfo() {
        return this.villageAttributes;
    }

    @Override
    public @Nullable
    AxisAlignedBB getTargetVillageArea() {
        return villageAttributes == null ? null : villageAttributes.getVillageArea();
    }

    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        if (!(reason == SpawnReason.SPAWN_EGG || reason == SpawnReason.BUCKET || reason == SpawnReason.CONVERSION || reason == SpawnReason.COMMAND) && this.getRNG().nextInt(50) == 0) {
            this.setItemStackToSlot(EquipmentSlotType.HEAD, HunterVillageData.createBanner());
        }
        ILivingEntityData livingData = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);

        if (this.getRNG().nextInt(4) == 0) {
            this.setLeftHanded(true);
            Item crossBow = getLevel() > 1 ? ModItems.enhanced_crossbow : ModItems.basic_crossbow;
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(crossBow));

        } else {
            this.setLeftHanded(false);
        }

        this.updateCombatTask();
        return livingData;
    }

    protected void updateEntityAttributes() {
        int l = Math.max(getLevel(), 0);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_HUNTER_MAX_HEALTH + BalanceMobProps.mobProps.VAMPIRE_HUNTER_MAX_HEALTH_PL * l);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_HUNTER_ATTACK_DAMAGE + BalanceMobProps.mobProps.VAMPIRE_HUNTER_ATTACK_DAMAGE_PL * l);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_HUNTER_SPEED);
    }
}
