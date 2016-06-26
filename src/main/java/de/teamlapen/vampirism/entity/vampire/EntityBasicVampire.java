package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.entity.vampire.IBasicVampire;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.ai.*;
import de.teamlapen.vampirism.entity.hunter.EntityHunterBase;
import de.teamlapen.vampirism.items.ItemStake;
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
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Basic vampire mob.
 * Follows nearby advanced hunters
 */
public class EntityBasicVampire extends EntityVampireBase implements IBasicVampire {

    private static final DataParameter<Integer> LEVEL = EntityDataManager.createKey(EntityBasicVampire.class, DataSerializers.VARINT);
    private final int MAX_LEVEL = 2;
    private int bloodtimer = 100;
    private EntityAdvancedVampire advancedLeader = null;

    public EntityBasicVampire(World world) {
        super(world, true);

        hasArms = true;

        this.setSize(0.6F, 1.8F);


    }


    @Override
    public void consumeBlood(int amt, float saturationMod) {
        super.consumeBlood(amt, saturationMod);
        bloodtimer += amt * 40 + this.getRNG().nextInt(1000);
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
    public boolean getCanSpawnHere() {
        return super.getCanSpawnHere() && isValidLightLevel();
    }

    @Override
    public int getLevel() {
        return getDataManager().get(LEVEL);
    }

    @Override
    public void setLevel(int level) {
        if (level >= 0) {
            this.updateEntityAttributes();
            if (level == 2) {
                this.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 1000000, 1));
            }
            if (level == 1) {
                this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
            } else {
                this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, null);
            }
            getDataManager().set(LEVEL, level);
        }
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    public int getTalkInterval() {
        return 400;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (bloodtimer > 0) {
            bloodtimer--;
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (advancedLeader != null && !advancedLeader.isEntityAlive()) {
            advancedLeader = null;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompund) {
        super.readFromNBT(tagCompund);
        if (tagCompund.hasKey("level")) {
            setLevel(tagCompund.getInteger("level"));
        }

    }

    @Override
    public void setDead() {
        super.setDead();
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
    public boolean wantsBlood() {
        return bloodtimer == 0;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("level", getLevel());
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.updateEntityAttributes();
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int lootingLevel) {
        if (recentlyHit) {
            if (this.rand.nextInt(3) == 0) {
                this.dropItem(ModItems.vampireFang, 1);
            }
        }
    }

    @Override
    protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
        super.dropLoot(wasRecentlyHit, lootingModifier, source);
        if (source instanceof EntityDamageSource) {
            if (source.getEntity() instanceof EntityPlayer) {
                ItemStack active = ((EntityPlayer) source.getEntity()).getHeldItem(((EntityPlayer) source.getEntity()).getActiveHand());
                if (active != null && active.getItem() instanceof ItemStake) {
                    if (this.rand.nextInt(2) == 0) {
                        this.dropItem(ModItems.vampireBlood, 1);

                    }
                }
            }
        }
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        getDataManager().register(LEVEL, -1);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.entity_vampire_ambient;
    }

    @Override
    protected int getExperiencePoints(EntityPlayer player) {
        return 6 + getLevel();
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        if (worldObj.getDifficulty() == EnumDifficulty.HARD) {
            //Only break doors on hard difficulty
            this.tasks.addTask(1, new EntityAIBreakDoor(this));
            ((PathNavigateGround) this.getNavigator()).setBreakDoors(true);
        }
        this.tasks.addTask(2, new EntityAIAvoidEntity<>(this, EntityCreature.class, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, VReference.HUNTER_FACTION), 10, 1.0, 1.1));
        this.tasks.addTask(2, new EntityAIRestrictSun(this));
        this.tasks.addTask(3, new VampireAIFleeSun(this, 0.9, false));
        this.tasks.addTask(3, new VampireAIFleeGarlic(this, 0.9, false));
        this.tasks.addTask(4, new EntityAIAttackMeleeNoSun(this, 1.0, false));
        this.tasks.addTask(5, new VampireAIBiteNearbyEntity(this));
        this.tasks.addTask(6, new VampireAIFollowAdvanced(this, 1.0));
        this.tasks.addTask(7, new VampireAIMoveToBiteable(this, 0.75));
        this.tasks.addTask(8, new EntityAIMoveThroughVillageCustom(this, 0.6, true, 600));
        this.tasks.addTask(9, new EntityAIWander(this, 0.7));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityPlayer.class, 20F, 0.9F));
        this.tasks.addTask(11, new EntityAIWatchClosest(this, EntityHunterBase.class, 17F));
        this.tasks.addTask(12, new EntityAILookIdle(this));

        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, true, false, null)));
        this.targetTasks.addTask(5, new EntityAINearestAttackableTarget<>(this, EntityCreature.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)));//TODO maybe make them not attack hunters, although it looks interesting

    }

    protected boolean isValidLightLevel() {
        BlockPos blockpos = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);

        if (this.worldObj.getLightFor(EnumSkyBlock.SKY, blockpos) > this.rand.nextInt(32)) {
            return false;
        } else {
            int i = this.worldObj.getLightFromNeighbors(blockpos);

            if (this.worldObj.isThundering()) {
                int j = this.worldObj.getSkylightSubtracted();
                this.worldObj.setSkylightSubtracted(10);
                i = this.worldObj.getLightFromNeighbors(blockpos);
                this.worldObj.setSkylightSubtracted(j);
            }

            return i <= this.rand.nextInt(8);
        }
    }

    protected void updateEntityAttributes() {
        int l = Math.max(getLevel(), 0);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Balance.mobProps.VAMPIRE_MAX_HEALTH + Balance.mobProps.VAMPIRE_MAX_HEALTH_PL * l);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(Balance.mobProps.VAMPIRE_ATTACK_DAMAGE + Balance.mobProps.VAMPIRE_ATTACK_DAMAGE_PL * l);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(Balance.mobProps.VAMPIRE_SPEED);
    }

}
