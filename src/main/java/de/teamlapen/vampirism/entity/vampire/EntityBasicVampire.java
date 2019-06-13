package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.vampire.IBasicVampire;
import de.teamlapen.vampirism.api.world.IVampirismVillage;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.action.EntityActionHandler;
import de.teamlapen.vampirism.entity.ai.EntityAIDefendVillage;
import de.teamlapen.vampirism.entity.ai.*;
import de.teamlapen.vampirism.entity.hunter.EntityHunterBase;
import de.teamlapen.vampirism.world.loot.LootHandler;
import de.teamlapen.vampirism.world.villages.VampirismVillageHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

/**
 * Basic vampire mob.
 * Follows nearby advanced hunters
 */
public class EntityBasicVampire extends EntityVampireBase implements IBasicVampire, IEntityActionUser {

    private final static Logger LOGGER = LogManager.getLogger(EntityBasicVampire.class);
    private static final DataParameter<Integer> LEVEL = EntityDataManager.createKey(EntityBasicVampire.class, DataSerializers.VARINT);
    private final int MAX_LEVEL = 2;
    private final int ANGRY_TICKS_PER_ATTACK = 120;
    private int bloodtimer = 100;
    private EntityAdvancedVampire advancedLeader = null;
    private int angryTimer = 0;

    private EntityAIBase tasks_avoidHunter;

    /**
     * Cached village. Serverside
     */
    @Nullable
    private IVampirismVillage cachedVillage;

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float p_70097_2_) {
        boolean flag = super.attackEntityFrom(damageSource, p_70097_2_);
        if (flag) angryTimer += ANGRY_TICKS_PER_ATTACK;
        IVampirismVillage v = getCurrentFriendlyVillage();
        if (v != null) {
            v.addOrRenewAggressor(damageSource.getTrueSource());
        }
        return flag;
    }

    /**
     * If this is non-null we are currently attacking a village center
     */
    @Nullable
    private AxisAlignedBB village_attack_area;
    /**
     * If this is non-null we are currently defending a village center
     */
    @Nullable
    private AxisAlignedBB village_defense_area;

    public EntityBasicVampire(World world) {
        super(world, true);
        this.canSuckBloodFromPlayer = true;
        hasArms = true;
        this.setSpawnRestriction(SpawnRestriction.SPECIAL);
        this.setSize(0.6F, 1.95F);
        this.entitytier = EntityActionTier.Medium;
        this.entityActionHandler = new EntityActionHandler<>(this);
    }

    @Nullable
    @Override
    public IVampirismVillage getCurrentFriendlyVillage() {
        return cachedVillage != null ? cachedVillage.getControllingFaction() == VReference.VAMPIRE_FACTION ? cachedVillage : null : null;
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
    EntityAdvancedVampire getAdvancedLeader() {
        return advancedLeader;
    }

    /**
     * Set an advanced vampire, this vampire should follow
     *
     * @param advancedLeader
     */
    public void setAdvancedLeader(@Nullable EntityAdvancedVampire advancedLeader) {
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
                this.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 1000000, 1));
            }
            if (level == 1) {
                this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
            } else {
                this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
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
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (bloodtimer > 0) {
            bloodtimer--;
        }
        if (angryTimer > 0) {
            angryTimer--;
        }

        if (this.ticksExisted % 9 == 3) {
            if (this.isPotionActive(MobEffects.FIRE_RESISTANCE)) {
                PotionEffect fireResistance = this.removeActivePotionEffect(MobEffects.FIRE_RESISTANCE);
                onFinishedPotionEffect(fireResistance);
                this.addPotionEffect(new PotionEffect(ModPotions.fire_protection, fireResistance.getDuration(), fireResistance.getAmplifier()));
            }
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
    public void read(NBTTagCompound tagCompund) {
        super.read(tagCompund);
        if (tagCompund.contains("level")) {
            setLevel(tagCompund.getInt("level"));
        }
        if (tagCompund.contains("village_attack_area")) {
            this.attackVillage(UtilLib.intToBB(tagCompund.getIntArray("village_attack_area")));
        } else if (tagCompund.contains("village_defense_area")) {
            this.defendVillage(UtilLib.intToBB(tagCompund.getIntArray("village_defense_area")));
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (advancedLeader != null && !advancedLeader.isAlive()) {
            advancedLeader = null;
        }
        if (!this.world.isRemote && this.ticksExisted % 40 == 8) {
            cachedVillage = VampirismVillageHelper.getNearestVillage(this);
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
    public boolean wantsBlood() {
        return bloodtimer == 0;
    }

    @Override
    public void writeAdditional(NBTTagCompound nbt) {
        super.writeAdditional(nbt);
        nbt.putInt("level", getLevel());
        if (village_attack_area != null) {
            nbt.putIntArray("village_attack_area", UtilLib.bbToInt(village_attack_area));
        } else if (village_defense_area != null) {
            nbt.putIntArray("village_defense_area", UtilLib.bbToInt(village_defense_area));
        }
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.updateEntityAttributes();
    }

    @Override
    protected float calculateFireDamage(float amount) {
        float protectionMod = 1F;
        PotionEffect protection = this.getActivePotionEffect(ModPotions.fire_protection);
        if (protection != null) {
            protectionMod = 1F / (2F + protection.getAmplifier());
        }

        return (float) (amount * protectionMod * Balance.mobProps.VAMPIRE_FIRE_VULNERABILITY) * (getLevel() * 0.5F + 1);
    }

    @Override
    protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
        super.dropLoot(wasRecentlyHit, lootingModifier, source);
    }

    @Override
    protected void registerData() {
        super.registerData();
        getDataManager().register(LEVEL, -1);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.entity_vampire_scream;
    }

    @Override
    protected int getExperiencePoints(EntityPlayer player) {
        return 6 + getLevel();
    }

    @Nullable
    @Override
    protected ResourceLocation getLootTable() {
        return LootHandler.BASIC_VAMPIRE;
    }

    @Override
    public void attackVillage(AxisAlignedBB area) {
        this.tasks.removeTask(tasks_avoidHunter);
        village_attack_area = area;
    }

    @Override
    public void defendVillage(AxisAlignedBB area) {
        this.tasks.removeTask(tasks_avoidHunter);
        village_defense_area = area;
    }

    @Nullable
    @Override
    public AxisAlignedBB getTargetVillageArea() {
        return village_attack_area == null ? village_defense_area : village_attack_area;
    }

    @Override
    public boolean isAttackingVillage() {
        return village_attack_area != null;
    }

    @Override
    public void stopVillageAttackDefense() {
        this.setCustomNameTag("");
        if (village_defense_area != null) {
            this.tasks.addTask(2, this.tasks_avoidHunter);
            village_defense_area = null;
        } else if (village_attack_area != null) {
            this.tasks.addTask(2, this.tasks_avoidHunter);
            village_attack_area = null;
        }
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        if (world.getDifficulty() == EnumDifficulty.HARD) {
            //Only break doors on hard difficulty
            this.tasks.addTask(1, new EntityAIBreakDoor(this));
            ((PathNavigateGround) this.getNavigator()).setBreakDoors(true);
        }
        this.tasks_avoidHunter = new EntityAIAvoidEntity<>(this, EntityCreature.class, 10, 1.0, 1.1, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, VReference.HUNTER_FACTION));
        this.tasks.addTask(2, this.tasks_avoidHunter);
        this.tasks.addTask(2, new VampireAIRestrictSun(this));
        this.tasks.addTask(3, new VampireAIFleeSun(this, 0.9, false));
        this.tasks.addTask(3, new VampireAIFleeGarlic(this, 0.9, false));
        this.tasks.addTask(4, new EntityAIAttackMeleeNoSun(this, 1.0, false));
        this.tasks.addTask(5, new VampireAIBiteNearbyEntity(this));
        this.tasks.addTask(6, new VampireAIFollowAdvanced(this, 1.0));
        this.tasks.addTask(7, new VampireAIMoveToBiteable(this, 0.75));
        this.tasks.addTask(8, new EntityAIMoveThroughVillageCustom(this, 0.6, true, 600));
        this.tasks.addTask(9, new EntityAIWander(this, 0.7));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityPlayer.class, 20F, 0.6F));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityHunterBase.class, 17F));
        this.tasks.addTask(10, new EntityAILookIdle(this));

        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(4, new EntityAIAttackVillage<>(this));
        this.targetTasks.addTask(4, new EntityAIDefendVillage<>(this));//Should automatically be mutually exclusive with  attack village
        this.targetTasks.addTask(5, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, true, false, null)));
        this.targetTasks.addTask(6, new EntityAINearestAttackableTarget<>(this, EntityCreature.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)));//TODO maybe make them not attack hunters, although it looks interesting

    }

    protected void updateEntityAttributes() {
        int l = Math.max(getLevel(), 0);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Balance.mobProps.VAMPIRE_MAX_HEALTH + Balance.mobProps.VAMPIRE_MAX_HEALTH_PL * l);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(Balance.mobProps.VAMPIRE_ATTACK_DAMAGE + Balance.mobProps.VAMPIRE_ATTACK_DAMAGE_PL * l);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(Balance.mobProps.VAMPIRE_SPEED);
    }
}
