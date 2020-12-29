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
import de.teamlapen.vampirism.entity.action.ActionHandlerEntity;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.goals.*;
import de.teamlapen.vampirism.entity.hunter.HunterBaseEntity;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.util.SharedMonsterAttributes;
import de.teamlapen.vampirism.world.MinionWorldData;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
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
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;

/**
 * Basic vampire mob.
 * Follows nearby advanced vampire
 */
public class BasicVampireEntity extends VampireBaseEntity implements IBasicVampire, IEntityActionUser {

    private static final DataParameter<Integer> LEVEL = EntityDataManager.createKey(BasicVampireEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TYPE = EntityDataManager.createKey(BasicVampireEntity.class, DataSerializers.VARINT);
    private final int MAX_LEVEL = 2;
    private final int ANGRY_TICKS_PER_ATTACK = 120;
    private int bloodtimer = 100;
    private IEntityLeader advancedLeader = null;
    private int angryTimer = 0;
    private Goal tasks_avoidHunter;

    public BasicVampireEntity(EntityType<? extends BasicVampireEntity> type, World world) {
        super(type, world, true);
        this.canSuckBloodFromPlayer = true;
        hasArms = true;
        this.setSpawnRestriction(SpawnRestriction.SPECIAL);
        entitytier = EntityActionTier.Medium;
        entityclass = EntityClassType.getRandomClass(this.getRNG());
        IEntityActionUser.applyAttributes(this);
        this.entityActionHandler = new ActionHandlerEntity<>(this);
        this.enableImobConversion();
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float amount) {
        boolean flag = super.attackEntityFrom(damageSource, amount);
        if (flag) angryTimer += ANGRY_TICKS_PER_ATTACK;
        return flag;
    }

    @Override
    public void drinkBlood(int amt, float saturationMod) {
        super.drinkBlood(amt, saturationMod);
        boolean dedicated = ServerLifecycleHooks.getCurrentServer().isDedicatedServer();
        bloodtimer += amt * 40 + this.getRNG().nextInt(1000) * (dedicated ? 2 : 1);
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

    @Override
    public int getLevel() {
        return getDataManager().get(LEVEL);
    }

    @Override
    public void setLevel(int level) {
        if (level >= 0) {
            getDataManager().set(LEVEL, level);
            this.updateEntityAttributes();
            if (level == 2) {
                this.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 1000000, 1));
            }
            if (level == 1) {
                this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.IRON_SWORD));
            } else {
                this.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
            }

        }
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    public int getTalkInterval() {
        return 600;
    }

    @Override
    public boolean isIgnoringSundamage() {
        float health = this.getHealth() / this.getMaxHealth();
        return super.isIgnoringSundamage() || angryTimer > 0 && health < 0.7f || health < 0.3f;
    }

    @Nullable
    private ICaptureAttributes villageAttributes;

    @Override
    public void attackVillage(ICaptureAttributes totem) {
        this.goalSelector.removeGoal(tasks_avoidHunter);
        this.villageAttributes = totem;
        this.attack = true;
    }

    @Override
    public void defendVillage(ICaptureAttributes totem) {
        this.goalSelector.removeGoal(tasks_avoidHunter);
        this.villageAttributes = totem;
        this.attack = false;
    }

    @Override
    public int getEntityTextureType() {
        int i = getDataManager().get(TYPE);
        return Math.max(i, 0);
    }

    @Override
    public void remove() {
        super.remove();
        if (advancedLeader != null) {
            advancedLeader.decreaseFollowerCount();
        }
    }

    @Override
    public int suggestLevel(Difficulty d) {
        switch (this.rand.nextInt(5)) {
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
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (getDataManager().get(TYPE) == -1) {
            getDataManager().set(TYPE, this.getRNG().nextInt(TYPES));
        }
    }

    @Override
    protected float calculateFireDamage(float amount) {
        float protectionMod = 1F;
        EffectInstance protection = this.getActivePotionEffect(ModEffects.fire_protection);
        if (protection != null) {
            protectionMod = 1F / (2F + protection.getAmplifier());
        }

        return (float) (amount * protectionMod * BalanceMobProps.mobProps.VAMPIRE_FIRE_VULNERABILITY) * (getLevel() * 0.5F + 1);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.entity_vampire_scream;
    }

    @Override
    protected int getExperiencePoints(PlayerEntity player) {
        return 6 + getLevel();
    }



    @Override
    public void readAdditional(CompoundNBT tagCompund) {
        super.readAdditional(tagCompund);
        if (tagCompund.contains("level")) {
            setLevel(tagCompund.getInt("level"));
        }
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


    public static AttributeModifierMap.MutableAttribute getAttributeBuilder() {
        return VampireBaseEntity.getAttributeBuilder()
                .createMutableAttribute(SharedMonsterAttributes.MAX_HEALTH, 1)
                .createMutableAttribute(SharedMonsterAttributes.ATTACK_DAMAGE, BalanceMobProps.mobProps.VAMPIRE_ATTACK_DAMAGE)
                .createMutableAttribute(SharedMonsterAttributes.MOVEMENT_SPEED, BalanceMobProps.mobProps.VAMPIRE_SPEED);
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        nbt.putInt("level", getLevel());
        nbt.putInt("type", getEntityTextureType());
        nbt.putBoolean("attack", this.attack);
        nbt.putInt("entityclasstype", EntityClassType.getID(this.entityclass));
        if (this.entityActionHandler != null) {
            this.entityActionHandler.write(nbt);
        }
    }

    @Override
    protected void registerData() {
        super.registerData();
        getDataManager().register(LEVEL, -1);
        getDataManager().register(TYPE, -1);
    }

//IMob -------------------------------------------------------------------------------------------------------------

    @Override
    protected EntityType<?> getIMobTypeOpt(boolean iMob) {
        return iMob ? ModEntities.vampire_imob : ModEntities.vampire;
    }

    public static class IMob extends BasicVampireEntity implements net.minecraft.entity.monster.IMob {

        public IMob(EntityType<? extends BasicVampireEntity> type, World world) {
            super(type, world);
        }

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



    private boolean attack;

    @Nullable
    @Override
    public ICaptureAttributes getCaptureInfo() {
        return villageAttributes;
    }

    @Nullable
    @Override
    public AxisAlignedBB getTargetVillageArea() {
        return villageAttributes == null ? null : villageAttributes.getVillageArea();
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (bloodtimer > 0) {
            bloodtimer--;
        }
        if (angryTimer > 0) {
            angryTimer--;
        }

        if (this.ticksExisted % 9 == 3) {
            if (VampirismConfig.BALANCE.vpFireResistanceReplace.get() && this.isPotionActive(Effects.FIRE_RESISTANCE)) {
                EffectInstance fireResistance = this.removeActivePotionEffect(Effects.FIRE_RESISTANCE);
                assert fireResistance != null;
                onFinishedPotionEffect(fireResistance);
                this.addPotionEffect(new EffectInstance(ModEffects.fire_protection, fireResistance.getDuration(), fireResistance.getAmplifier()));
            }
        }
        if (entityActionHandler != null) {
            entityActionHandler.handle();
        }
    }

    @Override
    public void stopVillageAttackDefense() {
        this.setCustomName(null);
        this.villageAttributes = null;
    }

    protected void updateEntityAttributes() {
        int l = Math.max(getLevel(), 0);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_MAX_HEALTH + BalanceMobProps.mobProps.VAMPIRE_MAX_HEALTH_PL * l);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_ATTACK_DAMAGE + BalanceMobProps.mobProps.VAMPIRE_ATTACK_DAMAGE_PL * l);
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
                            VampireMinionEntity.VampireMinionData data = new VampireMinionEntity.VampireMinionData("Minion", this.getEntityTextureType(), false);
                            int id = controller.createNewMinionSlot(data, ModEntities.vampire_minion);
                            if (id < 0) {
                                LOGGER.error("Failed to get minion slot");
                                return;
                            }
                            VampireMinionEntity minion = ModEntities.vampire_minion.create(this.world);
                            minion.claimMinionSlot(id, controller);
                            minion.copyLocationAndAnglesFrom(this);
                            minion.markAsConverted();
                            controller.activateTask(0, MinionTasks.stay);
                            this.world.addEntity(minion);
                            this.remove();

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
    public boolean isAttackingVillage() {
        return villageAttributes != null && attack;
    }

    @Override
    public boolean isDefendingVillage() {
        return villageAttributes != null && !attack;
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
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, PatrollerEntity.class, 5, true, true, (living) -> UtilLib.isInsideStructure(living, Structure.field_236381_q_)));
        this.targetSelector.addGoal(8, new DefendLeaderGoal(this));
    }


    @Override
    protected ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        if (this.isAlive() && !player.isSneaking()) {
            if (!world.isRemote) {
                int vampireLevel = FactionPlayerHandler.getOpt(player).map(fph -> fph.getCurrentLevel(VReference.VAMPIRE_FACTION)).orElse(0);
                if (vampireLevel > 0) {
                    FactionPlayerHandler.getOpt(player).ifPresent(fph -> {
                        if (fph.getMaxMinions() > 0) {
                            ItemStack heldItem = player.getHeldItem(hand);

                            if (this.getLevel() > 0) {
                                if (heldItem.getItem() == ModItems.vampire_minion_binding) {
                                    player.sendStatusMessage(new TranslationTextComponent("text.vampirism.basic_vampire.minion.unavailable"), true);
                                }
                            } else {
                                boolean freeSlot = MinionWorldData.getData(player.world).map(data -> data.getOrCreateController(fph)).map(PlayerMinionController::hasFreeMinionSlot).orElse(false);
                                player.sendStatusMessage(new TranslationTextComponent("text.vampirism.basic_vampire.minion.available"), true);
                                if (heldItem.getItem() == ModItems.vampire_minion_binding) {
                                    if (!freeSlot) {
                                        player.sendStatusMessage(new TranslationTextComponent("text.vampirism.basic_vampire.minion.no_free_slot"), true);
                                    } else {
                                        String key;
                                        switch (this.getRNG().nextInt(3)) {
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
                                        player.sendStatusMessage(new TranslationTextComponent(key),false);
                                        convertToMinion(player);
                                        if (!player.abilities.isCreativeMode) heldItem.shrink(1);
                                    }
                                } else if (freeSlot) {
                                    player.sendStatusMessage(new TranslationTextComponent("text.vampirism.basic_vampire.minion.require_binding", UtilLib.translate(ModItems.vampire_minion_binding.getTranslationKey())), true);
                                }
                            }
                        }
                    });
                }
            }
            return ActionResultType.SUCCESS;
        }
        return super.func_230254_b_(player, hand);
    }
}
