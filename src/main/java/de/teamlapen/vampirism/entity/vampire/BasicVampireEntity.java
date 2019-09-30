package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.vampire.IBasicVampire;
import de.teamlapen.vampirism.api.world.IVillageAttributes;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.action.ActionHandlerEntity;
import de.teamlapen.vampirism.entity.goals.*;
import de.teamlapen.vampirism.entity.hunter.HunterBaseEntity;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import de.teamlapen.vampirism.world.loot.LootHandler;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
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
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structures;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Basic vampire mob.
 * Follows nearby advanced hunters
 */
public class BasicVampireEntity extends VampireBaseEntity implements IBasicVampire, IEntityActionUser {

    private static final DataParameter<Integer> LEVEL = EntityDataManager.createKey(BasicVampireEntity.class, DataSerializers.VARINT);
    private final int MAX_LEVEL = 2;
    private final int ANGRY_TICKS_PER_ATTACK = 120;
    private int bloodtimer = 100;
    private AdvancedVampireEntity advancedLeader = null;
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
    public boolean attackEntityFrom(DamageSource damageSource, float p_70097_2_) {
        boolean flag = super.attackEntityFrom(damageSource, p_70097_2_);
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
    public
    @Nullable
    AdvancedVampireEntity getAdvancedLeader() {
        return advancedLeader;
    }

    /**
     * Set an advanced vampire, this vampire should follow
     *
     * @param advancedLeader
     */
    public void setAdvancedLeader(@Nullable AdvancedVampireEntity advancedLeader) {
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
            if (this.isPotionActive(Effects.FIRE_RESISTANCE)) {
                EffectInstance fireResistance = this.removeActivePotionEffect(Effects.FIRE_RESISTANCE);
                onFinishedPotionEffect(fireResistance);
                this.addPotionEffect(new EffectInstance(ModEffects.fire_protection, fireResistance.getDuration(), fireResistance.getAmplifier()));
            }
        }
        if (entityActionHandler != null) {
            entityActionHandler.handle();
        }
    }

    @Override
    public void read(CompoundNBT tagCompund) {
        super.read(tagCompund);
        if (tagCompund.contains("level")) {
            setLevel(tagCompund.getInt("level"));
        }
        if (tagCompund.contains("attack")) {
            this.attack = tagCompund.getBoolean("attack");
        }
        if (tagCompund.contains("x")) {
            this.villageAttributes = TotemTileEntity.getVillageAttributes((TotemTileEntity) this.world.getTileEntity(new BlockPos(tagCompund.getInt("x"), tagCompund.getInt("y"), tagCompund.getInt("z"))));
        }

        if (entityActionHandler != null) {
            entityActionHandler.read(tagCompund);
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
        if (advancedLeader != null && !advancedLeader.isAlive()) {
            advancedLeader = null;
        }
    }

    @Override
    public boolean wantsBlood() {
        return bloodtimer == 0;
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        nbt.putInt("level", getLevel());
        nbt.putBoolean("attack", this.attack);
        if (this.villageAttributes != null) {
            nbt.putInt("x", this.villageAttributes.getPosition().getX());
            nbt.putInt("y", this.villageAttributes.getPosition().getY());
            nbt.putInt("z", this.villageAttributes.getPosition().getZ());
        }
        nbt.putInt("entityclasstype", EntityClassType.getID(this.entityclass));
        if (this.entityActionHandler != null) {
            this.entityActionHandler.write(nbt);
        }
    }

    @Override
    protected float calculateFireDamage(float amount) {
        float protectionMod = 1F;
        EffectInstance protection = this.getActivePotionEffect(ModEffects.fire_protection);
        if (protection != null) {
            protectionMod = 1F / (2F + protection.getAmplifier());
        }

        return (float) (amount * protectionMod * Balance.mobProps.VAMPIRE_FIRE_VULNERABILITY) * (getLevel() * 0.5F + 1);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.entity_vampire_scream;
    }

    @Override
    protected int getExperiencePoints(PlayerEntity player) {
        return 6 + getLevel();
    }

    @Nullable
    @Override
    protected ResourceLocation getLootTable() {
        return LootHandler.BASIC_VAMPIRE;
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.updateEntityAttributes();
    }

    @Override
    protected void registerData() {
        super.registerData();
        getDataManager().register(LEVEL, -1);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new BreakDoorGoal(this, (difficulty) -> difficulty == net.minecraft.world.Difficulty.HARD));//Only break doors on hard difficulty
        this.tasks_avoidHunter = new AvoidEntityGoal<CreatureEntity>(this, CreatureEntity.class, 10, 1.0, 1.1, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, VReference.HUNTER_FACTION));
        this.goalSelector.addGoal(2, this.tasks_avoidHunter);
        this.goalSelector.addGoal(2, new RestrictSunVampireGoal<>(this));
        this.goalSelector.addGoal(3, new FleeSunVampireGoal<>(this, 0.9, false));
        this.goalSelector.addGoal(4, new AttackMeleeNoSunGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new BiteNearbyEntityVampireGoal<>(this));
        this.goalSelector.addGoal(6, new FollowAdvancedVampireGoal(this, 1.0));
        this.goalSelector.addGoal(7, new MoveToBiteableVampireGoal<>(this, 0.75));
        this.goalSelector.addGoal(8, new MoveThroughVillageGoal(this, 0.6, true, 600, () -> false));//TODO was MoveThroughVillageCustomGoal (test)
        this.goalSelector.addGoal(9, new RandomWalkingGoal(this, 0.7));
        this.goalSelector.addGoal(10, new LookAtClosestVisibleGoal(this, PlayerEntity.class, 20F, 0.6F));
        this.goalSelector.addGoal(10, new LookAtGoal(this, HunterBaseEntity.class, 17F));
        this.goalSelector.addGoal(10, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new AttackVillageGoal<>(this));
        this.targetSelector.addGoal(4, new DefendVillageGoal<>(this));//Should automatically be mutually exclusive with  attack village
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, true, false, null)));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, CreatureEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)));//TODO maybe make them not attack hunters, although it looks interesting
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, PatrollerEntity.class, 5, true, true, (living) -> Structures.VILLAGE.isPositionInStructure(living.world, living.getPosition())));
    }

    protected void updateEntityAttributes() {
        int l = Math.max(getLevel(), 0);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Balance.mobProps.VAMPIRE_MAX_HEALTH + Balance.mobProps.VAMPIRE_MAX_HEALTH_PL * l);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(Balance.mobProps.VAMPIRE_ATTACK_DAMAGE + Balance.mobProps.VAMPIRE_ATTACK_DAMAGE_PL * l);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(Balance.mobProps.VAMPIRE_SPEED);
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
    public List<IEntityAction> getAvailableActions() {
        return VampirismAPI.entityActionManager().getAllEntityActionsByTierAndClassType(((IFactionEntity) this).getFaction(), entitytier, entityclass);
    }

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

    //Village stuff ----------------------------------------------------------------------------------------------------
    private @Nullable
    IVillageAttributes villageAttributes;
    private boolean attack;

    @Override
    public void attackVillage(IVillageAttributes totem) {
        this.goalSelector.removeGoal(tasks_avoidHunter);
        this.villageAttributes = totem;
        this.attack = true;
    }

    @Override
    public void defendVillage(IVillageAttributes totem) {
        this.goalSelector.removeGoal(tasks_avoidHunter);
        this.villageAttributes = totem;
        this.attack = false;
    }

    @Nullable
    @Override
    public IVillageAttributes getVillageAttributes() {
        return villageAttributes;
    }

    @Override
    public void stopVillageAttackDefense() {
        this.setCustomName(null);
        this.villageAttributes = null;
    }

    @Nullable
    @Override
    public AxisAlignedBB getTargetVillageArea() {
        return villageAttributes.getVillageArea();
    }

    @Override
    public boolean isAttackingVillage() {
        return villageAttributes != null && attack;
    }

    @Override
    public boolean isDefendingVillage() {
        return villageAttributes != null && !attack;
    }
}
