package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.IEntityLeader;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.vampire.IBasicVampire;
import de.teamlapen.vampirism.api.world.ICaptureAttributes;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.effects.BadOmenEffect;
import de.teamlapen.vampirism.entity.action.ActionHandlerEntity;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.goals.*;
import de.teamlapen.vampirism.entity.hunter.HunterBaseEntity;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import de.teamlapen.vampirism.util.VampireVillageData;
import de.teamlapen.vampirism.world.MinionWorldData;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.PatrollerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;

/**
 * Basic vampire mob.
 * Follows nearby advanced vampire
 */
public class BasicVampireEntity extends VampireBaseEntity implements IBasicVampire, IEntityActionUser {

    private static final DataParameter<Integer> LEVEL = EntityDataManager.defineId(BasicVampireEntity.class, DataSerializers.INT);
    private static final DataParameter<Integer> TYPE = EntityDataManager.defineId(BasicVampireEntity.class, DataSerializers.INT);

    public static AttributeModifierMap.MutableAttribute getAttributeBuilder() {
        return VampireBaseEntity.getAttributeBuilder()
                .add(Attributes.MAX_HEALTH, 1)
                .add(Attributes.ATTACK_DAMAGE, BalanceMobProps.mobProps.VAMPIRE_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, BalanceMobProps.mobProps.VAMPIRE_SPEED);
    }

    private final int MAX_LEVEL = 2;
    private final int ANGRY_TICKS_PER_ATTACK = 120;
    /**
     * available actions for AI task & task
     */
    private final ActionHandlerEntity<?> entityActionHandler;
    private final EntityClassType entityclass;
    private final EntityActionTier entitytier;
    private int bloodtimer = 100;
    private IEntityLeader advancedLeader = null;
    private int angryTimer = 0;
    private Goal tasks_avoidHunter;
    @Nullable
    private ICaptureAttributes villageAttributes;
    private boolean attack;

    public BasicVampireEntity(EntityType<? extends BasicVampireEntity> type, World world) {
        super(type, world, true);
        this.canSuckBloodFromPlayer = true;
        hasArms = true;
        this.setSpawnRestriction(SpawnRestriction.SPECIAL);
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
        nbt.putInt("type", getEntityTextureType());
        nbt.putBoolean("attack", this.attack);
        nbt.putInt("entityclasstype", EntityClassType.getID(this.entityclass));
        if (this.entityActionHandler != null) {
            this.entityActionHandler.write(nbt);
        }
    }

    @Override
    public void attackVillage(ICaptureAttributes totem) {
        this.goalSelector.removeGoal(tasks_avoidHunter);
        this.villageAttributes = totem;
        this.attack = true;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (bloodtimer > 0) {
            bloodtimer--;
        }
        if (angryTimer > 0) {
            angryTimer--;
        }

        if (this.tickCount % 9 == 3) {
            if (VampirismConfig.BALANCE.vpFireResistanceReplace.get() && this.hasEffect(Effects.FIRE_RESISTANCE)) {
                EffectInstance fireResistance = this.removeEffectNoUpdate(Effects.FIRE_RESISTANCE);
                assert fireResistance != null;
                onEffectRemoved(fireResistance);
                this.addEffect(new EffectInstance(ModEffects.FIRE_PROTECTION.get(), fireResistance.getDuration(), fireResistance.getAmplifier()));
            }
        }
        if (entityActionHandler != null) {
            entityActionHandler.handle();
        }
    }

    @Override
    public void defendVillage(ICaptureAttributes totem) {
        this.goalSelector.removeGoal(tasks_avoidHunter);
        this.villageAttributes = totem;
        this.attack = false;
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
                            VampireMinionEntity.VampireMinionData data = new VampireMinionEntity.VampireMinionData("Minion", this.getEntityTextureType(), false);
                            int id = controller.createNewMinionSlot(data, ModEntities.VAMPIRE_MINION.get());
                            if (id < 0) {
                                LOGGER.error("Failed to get minion slot");
                                return;
                            }
                            VampireMinionEntity minion = ModEntities.VAMPIRE_MINION.get().create(this.level);
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
    public ActionHandlerEntity getActionHandler() {
        return entityActionHandler;
    }

    /**
     * @return The advanced vampire this entity is following or null if none
     */
    public @Nullable
    IEntityLeader getAdvancedLeader() {
        return advancedLeader;
    }

    /**
     * Set an advanced vampire, this vampire should follow
     *
     * @param advancedLeader new leader
     */
    public void setAdvancedLeader(@Nullable IEntityLeader advancedLeader) {
        this.advancedLeader = advancedLeader;
    }

    @Nullable
    @Override
    public ICaptureAttributes getCaptureInfo() {
        return villageAttributes;
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

    @Override
    public void drinkBlood(int amt, float saturationMod) {
        super.drinkBlood(amt, saturationMod);
        boolean dedicated = ServerLifecycleHooks.getCurrentServer().isDedicatedServer();
        bloodtimer += amt * 40 + this.getRandom().nextInt(1000) * (dedicated ? 2 : 1);
    }

    @Nullable
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        if ((reason == SpawnReason.NATURAL || reason == SpawnReason.STRUCTURE) && this.getRandom().nextInt(50) == 0) {
            this.setItemSlot(EquipmentSlotType.HEAD, VampireVillageData.createBanner());
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 2400;
    }

    @Nullable
    @Override
    public AxisAlignedBB getTargetVillageArea() {
        return villageAttributes == null ? null : villageAttributes.getVillageArea();
    }

    @Override
    public boolean isAttackingVillage() {
        return villageAttributes != null && attack;
    }

    @Override
    public boolean isDefendingVillage() {
        return villageAttributes != null && !attack;
    }

//IMob -------------------------------------------------------------------------------------------------------------

    @Override
    public boolean isIgnoringSundamage() {
        float health = this.getHealth() / this.getMaxHealth();
        return super.isIgnoringSundamage() || angryTimer > 0 && health < 0.7f || health < 0.3f;
    }

    @Override
    public int getEntityTextureType() {
        int i = getEntityData().get(TYPE);
        return Math.max(i, 0);
    }
    //Entityactions ----------------------------------------------------------------------------------------------------

    @Override
    public int getLevel() {
        return getEntityData().get(LEVEL);
    }

    @Override
    public void setLevel(int level) {
        if (level >= 0) {
            getEntityData().set(LEVEL, level);
            this.updateEntityAttributes();
            if (level == 2) {
                this.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 1000000, 1));
            }
            if (level == 1) {
                this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.IRON_SWORD));
            } else {
                this.setItemSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
            }

        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        boolean flag = super.hurt(damageSource, amount);
        if (flag) angryTimer += ANGRY_TICKS_PER_ATTACK;
        return flag;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (getEntityData().get(TYPE) == -1) {
            getEntityData().set(TYPE, this.getRandom().nextInt(TYPES));
        }
    }

    @Override
    public void remove() {
        super.remove();
        if (advancedLeader != null) {
            advancedLeader.decreaseFollowerCount();
        }
    }

    @Override
    public void stopVillageAttackDefense() {
        this.setCustomName(null);
        this.villageAttributes = null;
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tagCompund) {
        super.readAdditionalSaveData(tagCompund);
        if (tagCompund.contains("level")) {
            setLevel(tagCompund.getInt("level"));
        }
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
    public void tick() {
        super.tick();
        if (advancedLeader != null && !advancedLeader.getRepresentingEntity().isAlive()) {
            advancedLeader = null;
        }
    }

    @Override
    public boolean wantsBlood() {
        return bloodtimer == 0;
    }

    @Override
    public int suggestLevel(Difficulty d) {
        switch (this.random.nextInt(5)) {
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
    protected float calculateFireDamage(float amount) {
        float protectionMod = 1F;
        EffectInstance protection = this.getEffect(ModEffects.FIRE_PROTECTION.get());
        if (protection != null) {
            protectionMod = 1F / (2F + protection.getAmplifier());
        }

        return (float) (amount * protectionMod * BalanceMobProps.mobProps.VAMPIRE_FIRE_VULNERABILITY) * (getLevel() * 0.5F + 1);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(LEVEL, -1);
        getEntityData().define(TYPE, -1);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.ENTITY_VAMPIRE_SCREAM.get();
    }

    @Override
    protected int getExperienceReward(PlayerEntity player) {
        return 6 + getLevel();
    }

    @Override
    protected EntityType<?> getIMobTypeOpt(boolean iMob) {
        return iMob ? ModEntities.VAMPIRE_IMOB.get() : ModEntities.VAMPIRE.get();
    }

    @Override
    protected ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        if (this.isAlive() && !player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                int vampireLevel = FactionPlayerHandler.getOpt(player).map(fph -> fph.getCurrentLevel(VReference.VAMPIRE_FACTION)).orElse(0);
                if (vampireLevel > 0) {
                    return FactionPlayerHandler.getOpt(player).map(fph -> {
                        if (fph.getMaxMinions() > 0) {
                            ItemStack heldItem = player.getItemInHand(hand);
                            boolean freeSlot = MinionWorldData.getData(player.level).map(data -> data.getOrCreateController(fph)).map(controller -> controller.hasFreeMinionSlot()).orElse(false);
                            player.displayClientMessage(new TranslationTextComponent("text.vampirism.basic_vampire.minion.available"), true);
                            if (heldItem.getItem() == ModItems.VAMPIRE_MINION_BINDING.get()) {
                                if (!freeSlot) {
                                    player.displayClientMessage(new TranslationTextComponent("text.vampirism.basic_vampire.minion.no_free_slot"), true);
                                } else {
                                    String key;
                                    switch (this.getRandom().nextInt(3)) {
                                        case 0:
                                            key = "text.vampirism.basic_vampire.minion.start_serving1";
                                            break;
                                        case 1:
                                            key = "text.vampirism.basic_vampire.minion.start_serving2";
                                            break;
                                        default:
                                            key = "text.vampirism.basic_vampire.minion.start_serving3";
                                            break;
                                    }
                                    player.displayClientMessage(new TranslationTextComponent(key), false);
                                    convertToMinion(player);
                                    if (!player.abilities.instabuild) heldItem.shrink(1);
                                }
                            } else if (freeSlot) {
                                player.displayClientMessage(new TranslationTextComponent("text.vampirism.basic_vampire.minion.require_binding", UtilLib.translate(ModItems.VAMPIRE_MINION_BINDING.get().getDescriptionId())), true);
                            }
                            return ActionResultType.SUCCESS;
                        }
                        return ActionResultType.PASS;
                    }).orElse(ActionResultType.PASS);
                }
            }
            return ActionResultType.PASS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new BreakDoorGoal(this, (difficulty) -> difficulty == net.minecraft.world.Difficulty.HARD));//Only break doors on hard difficulty
        this.tasks_avoidHunter = new AvoidEntityGoal<>(this, CreatureEntity.class, 10, 1.0, 1.1, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, VReference.HUNTER_FACTION));
        this.goalSelector.addGoal(2, this.tasks_avoidHunter);
        this.goalSelector.addGoal(2, new RestrictSunVampireGoal<>(this));
        this.goalSelector.addGoal(3, new FleeSunVampireGoal<>(this, 0.9, false));
        this.goalSelector.addGoal(4, new AttackMeleeNoSunGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new BiteNearbyEntityVampireGoal<>(this));
        this.goalSelector.addGoal(6, new FollowAdvancedVampireGoal(this, 1.0));
        this.goalSelector.addGoal(7, new MoveToBiteableVampireGoal<>(this, 0.75));
        this.goalSelector.addGoal(8, new MoveThroughVillageGoal(this, 0.6, true, 600, () -> false));
        this.goalSelector.addGoal(9, new RandomWalkingGoal(this, 0.7));
        this.goalSelector.addGoal(10, new LookAtClosestVisibleGoal(this, PlayerEntity.class, 20F, 0.6F));
        this.goalSelector.addGoal(10, new LookAtGoal(this, HunterBaseEntity.class, 17F));
        this.goalSelector.addGoal(10, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new AttackVillageGoal<>(this));
        this.targetSelector.addGoal(4, new DefendVillageGoal<>(this));//Should automatically be mutually exclusive with  attack village
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, true, false, null)));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, CreatureEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)));//TODO maybe make them not attack hunters, although it looks interesting
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, PatrollerEntity.class, 5, true, true, (living) -> UtilLib.isInsideStructure(living, Structure.VILLAGE)));
        this.targetSelector.addGoal(8, new DefendLeaderGoal(this));
    }

    protected void updateEntityAttributes() {
        int l = Math.max(getLevel(), 0);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_MAX_HEALTH + BalanceMobProps.mobProps.VAMPIRE_MAX_HEALTH_PL * l);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_ATTACK_DAMAGE + BalanceMobProps.mobProps.VAMPIRE_ATTACK_DAMAGE_PL * l);
    }

    public static class IMob extends BasicVampireEntity implements net.minecraft.entity.monster.IMob {

        public IMob(EntityType<? extends BasicVampireEntity> type, World world) {
            super(type, world);
        }

    }
}
