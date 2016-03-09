package de.teamlapen.vampirism.entity.player.vampire;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.EnumGarlicStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IBiteableEntity;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.Achievements;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.LevelAttributeModifier;
import de.teamlapen.vampirism.entity.player.VampirismPlayer;
import de.teamlapen.vampirism.entity.player.actions.ActionHandler;
import de.teamlapen.vampirism.entity.player.skills.SkillHandler;
import de.teamlapen.vampirism.potion.FakeNightVisionPotionEffect;
import de.teamlapen.vampirism.potion.PotionSanguinare;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Permissions;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.FoodStats;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Main class for Vampire Players.
 */
public class VampirePlayer extends VampirismPlayer<IVampirePlayer> implements IVampirePlayer {

    private final static String TAG = "VampirePlayer";

    /**
     * Don't call before the construction event of the player entity is finished
     *
     * @param player
     * @return
     */
    public static VampirePlayer get(EntityPlayer player) {
        return (VampirePlayer) VReference.VAMPIRE_FACTION.getPlayerProp(player);
    }

    public static void register(EntityPlayer player) {
        player.registerExtendedProperties(VReference.VAMPIRE_FACTION.prop(), new VampirePlayer(player));
    }
    private final BloodStats bloodStats;
    private final String KEY_EYE = "eye_type";
    private final String KEY_SPAWN_BITE_PARTICLE = "bite_particle";
    private final ActionHandler<IVampirePlayer> actionHandler;
    private final SkillHandler<IVampirePlayer> skillHandler;
    private final VampirePlayerSpecialAttributes specialAttributes = new VampirePlayerSpecialAttributes();
    private boolean sundamage_cache = false;
    private EnumGarlicStrength garlic_cache = EnumGarlicStrength.NONE;
    private int biteCooldown = 0;
    private int eyeType = 0;
    private int ticksInSun = 0;

    public VampirePlayer(EntityPlayer player) {
        super(player);
        applyEntityAttributes();
        bloodStats = new BloodStats(player);
        actionHandler = new ActionHandler(this);
        skillHandler = new SkillHandler<IVampirePlayer>(this);
    }

    /**
     * Increases exhaustion level by supplied amount
     */
    public void addExhaustion(float p_71020_1_) {
        if (!player.capabilities.disableDamage && getLevel() > 0) {
            if (!isRemote()) {
                bloodStats.addExhaustion(p_71020_1_);
            }
        }
    }

    /**
     * Bite the entity with the given id.
     * Checks reach distance
     *
     * @param entityId
     */
    public void biteEntity(int entityId) {
        Entity e = player.worldObj.getEntityByID(entityId);
        if (e != null && e instanceof EntityLivingBase) {
            if (e.getDistanceToEntity(player) <= ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance() + 2) {
                biteEntity((EntityLivingBase) e);
            } else {
                VampirismMod.log.w(TAG, "Entity sent by client is not in reach " + entityId);
            }
        }
    }

    @Override
    public boolean canBeBitten(IVampire biter) {
        return true;
    }

    @Override
    public boolean canLeaveFaction() {
        return true;
    }

    @Override
    public void consumeBlood(int amt, float saturationMod) {
        amt = this.bloodStats.addBlood(amt, saturationMod);
        if (amt > 0) {
            handleSpareBlood(amt);
        }
    }

    public BITE_TYPE determineBiteType(EntityLivingBase entity) {
        if (entity instanceof IBiteableEntity) {
            if (((IBiteableEntity) entity).canBeBitten(this)) return BITE_TYPE.SUCK_BLOOD;
        }
        if (entity instanceof EntityCreature) {
            if (ExtendedCreature.get((EntityCreature) entity).canBeBitten(this)) {
                return BITE_TYPE.SUCK_BLOOD_CREATURE;
            }
        } else if (entity instanceof EntityPlayer) {
            if (((EntityPlayer) entity).capabilities.isCreativeMode || !Permissions.getPermission("pvp", player)) {
                return BITE_TYPE.NONE;
            }
            if (!UtilLib.canReallySee(entity, player, false) && VampirePlayer.get((EntityPlayer) entity).canBeBitten(this)) {
                return BITE_TYPE.SUCK_BLOOD_PLAYER;
            }
            return BITE_TYPE.ATTACK;
        }
        return BITE_TYPE.ATTACK;
    }

    @Override
    public boolean doesResistGarlic(EnumGarlicStrength strength) {
        return false;
    }

    @Override
    public IActionHandler<IVampirePlayer> getActionHandler() {
        return actionHandler;
    }

    @Override
    public int getBloodLevel() {
        return bloodStats.getBloodLevel();
    }

    @Override
    public float getBloodSaturation() {
        return (float) Balance.vp.PLAYER_BLOOD_SATURATION;
    }

    public BloodStats getBloodStats() {
        return bloodStats;
    }

    /**
     * @return Eyetype for rendering
     */
    public int getEyeType() {
        return eyeType;
    }

    @Override
    public IPlayableFaction<IVampirePlayer> getFaction() {
        return VReference.VAMPIRE_FACTION;
    }

    @Override
    public Predicate<? super Entity> getNonFriendlySelector(boolean otherFactionPlayers) {
        if (otherFactionPlayers) {
            return Predicates.alwaysTrue();
        } else {
            return VampirismAPI.factionRegistry().getPredicate(getFaction());
        }

    }

    @Override
    public String getPropertyKey() {
        return VReference.VAMPIRE_FACTION.prop();
    }

    @Override
    public ISkillHandler<IVampirePlayer> getSkillHandler() {
        return skillHandler;
    }

    public VampirePlayerSpecialAttributes getSpecialAttributes() {
        return specialAttributes;
    }

    @Override
    public int getTicksInSun() {
        return ticksInSun;
    }

    @Override
    public boolean isAutoFillEnabled() {
        return false;
    }

    @Override
    public boolean isDisguised() {
        return specialAttributes.disguised;
    }

    @Override
    public EnumGarlicStrength isGettingGarlicDamage() {
        return isGettingGarlicDamage(false);
    }

    @Override
    public EnumGarlicStrength isGettingGarlicDamage(boolean forcerefresh) {
        if (forcerefresh) {
            garlic_cache = Helper.gettingGarlicDamage(player);
        }
        return garlic_cache;
    }

    @Override
    public boolean isGettingSundamage(boolean forcerefresh) {
        if (forcerefresh) {
            sundamage_cache = Helper.gettingSundamge(player);
        }
        return sundamage_cache;
    }

    @Override
    public boolean isGettingSundamage() {
        return isGettingSundamage(false);
    }

    @Override
    public boolean isVampireLord() {
        return false;
    }

    @Override
    public void loadData(NBTTagCompound nbt) {
        bloodStats.readNBT(nbt);
        eyeType = nbt.getInteger(KEY_EYE);
        actionHandler.loadFromNbt(nbt);
        skillHandler.loadFromNbt(nbt);
    }

    @Override
    public int onBite(IVampire biter) {
        if (getLevel() == 0) {
            int amt = player.getFoodStats().getFoodLevel();
            player.getFoodStats().setFoodLevel(0);
            player.addExhaustion(1000F);
            if (!player.isPotionActive(ModPotions.sanguinare) && (!(biter instanceof EntityPlayer) || Permissions.canPlayerTurnPlayer((EntityPlayer) biter)) && Helper.canBecomeVampire(player)) {
                PotionSanguinare.addRandom(player, true);
            }
            return amt;
        }
        int amt = this.getBloodStats().getBloodLevel();
        this.getBloodStats().consumeBlood(amt);
        sync(this.getBloodStats().writeUpdate(new NBTTagCompound()), false);
        return amt;
    }

    @Override
    public void onChangedDimension(int from, int to) {

    }

    @Override
    public void onDeath(DamageSource src) {
        actionHandler.deactivateAllActions();
    }

    @Override
    public boolean onEntityAttacked(DamageSource src, float amt) {
        return false;
    }

    @Override
    public void onJoinWorld() {
        if (getLevel() > 0) {
            actionHandler.onActionsReactivated();
            ticksInSun = 0;
        }
    }

    @Override
    public void onLevelChanged(int newLevel, int oldLevel) {
        if (!isRemote()) {
            LevelAttributeModifier.applyModifier(player, SharedMonsterAttributes.movementSpeed, "Vampire", getLevel(), Balance.vp.SPEED_LCAP, Balance.vp.SPEED_MAX_MOD, Balance.vp.SPEED_TYPE);
            LevelAttributeModifier.applyModifier(player, SharedMonsterAttributes.attackDamage, "Vampire", getLevel(), Balance.vp.STRENGTH_LCAP, Balance.vp.STRENGTH_MAX_MOD, Balance.vp.STRENGTH_TYPE);
            LevelAttributeModifier.applyModifier(player, SharedMonsterAttributes.maxHealth, "Vampire", getLevel(), Balance.vp.HEALTH_LCAP, Balance.vp.HEALTH_MAX_MOD, Balance.vp.HEALTH_TYPE);
            LevelAttributeModifier.applyModifier(player, VReference.bloodExhaustion, "Vampire", getLevel(), getMaxLevel(), Balance.vp.EXAUSTION_MAX_MOD, Balance.vp.EXHAUSTION_TYPE);
            if (newLevel > 0) {
                player.addStat(Achievements.becomingAVampire, 1);
                if (oldLevel == 0) {
                    skillHandler.enableRootSkill();
                }

            } else {
                actionHandler.resetTimers();
                skillHandler.disableAllSkills();
            }
        } else {
            if (oldLevel == 0) {
                if (player.isPotionActive(Potion.nightVision)) {
                    player.removePotionEffect(Potion.nightVision.id);
                }
                player.addPotionEffect(new FakeNightVisionPotionEffect());
            } else if (newLevel == 0) {
                if (player.getActivePotionEffect(Potion.nightVision) instanceof FakeNightVisionPotionEffect) {
                    player.removePotionEffect(Potion.nightVision.getId());
                }
                actionHandler.resetTimers();
            }
        }
    }

    @Override
    public void onPlayerLoggedIn() {

    }

    @Override
    public void onPlayerLoggedOut() {

    }

    /**
     * Called when a sanguinare effect runs out.
     * DON'T add/remove potions here, since it is called while the potion effect list is modified.
     */
    public void onSanguinareFinished() {
        if (Helper.canBecomeVampire(player) && !isRemote()) {
            FactionPlayerHandler handler = FactionPlayerHandler.get(player);
            handler.joinFaction(getFaction());
            player.addPotionEffect(new PotionEffect(Potion.resistance.id, 300));//TODO add saturation as well
//            ((WorldServer) player.worldObj).addScheduledTask(new Runnable() {
//                @Override
//                public void run() {
//                    if (player != null && player.isEntityAlive()) {
//
//                    }
//                }
//            });

        }
    }

    @Override
    public void onUpdate() {
        int level = getLevel();
        if (level > 0) {
            if (player.ticksExisted % REFERENCE.REFRESH_SUNDAMAGE_TICKS == 1) {
                isGettingSundamage(true);
            }
            if (player.ticksExisted % REFERENCE.REFRESH_GARLIC_TICKS == 6) {
                isGettingGarlicDamage(true);
            }
        } else {
            sundamage_cache = false;
            garlic_cache = EnumGarlicStrength.NONE;
        }

        if (!isRemote()) {
            if (level > 0) {
                boolean sync = false;
                boolean syncToAll = false;
                NBTTagCompound syncPacket = new NBTTagCompound();

                if (biteCooldown > 0) biteCooldown--;
                if (isGettingSundamage()) {
                    handleSunDamage();
                } else if (ticksInSun > 0) {
                    ticksInSun--;
                }
                if (isGettingGarlicDamage() != EnumGarlicStrength.NONE) {
                    handleGarlicDamage();
                }
                if (actionHandler.updateActions()) {
                    sync = true;
                    syncToAll = true;
                    actionHandler.writeUpdateForClient(syncPacket);
                }
                if (skillHandler.isDirty()) {
                    sync = true;
                    skillHandler.writeUpdateForClient(syncPacket);
                }

                if (sync) {
                    sync(syncPacket, syncToAll);
                }
            } else {

                ticksInSun = 0;
            }


        } else {
            if (level > 0) {
                if (player.ticksExisted % 100 == 8 && player.getActivePotionEffect(Potion.nightVision) == null) {
                    player.addPotionEffect(new FakeNightVisionPotionEffect());
                }
                actionHandler.updateActions();
                if (isGettingSundamage()) {
                    handleSunDamage();
                } else if (ticksInSun > 0) {
                    ticksInSun--;
                }
            } else {
                ticksInSun = 0;
            }

        }
    }

    /**
     * Update the blood stats.
     * Is called by a separate event handler, so this is done after all exhaustion is added to {@link FoodStats#foodExhaustionLevel}
     */
    public void onUpdateBloodStats() {
        if (getLevel() > 0) {
            if (this.bloodStats.onUpdate()) {
                sync(this.bloodStats.writeUpdate(new NBTTagCompound()), false);
            }
        }
    }

    @Override
    public void saveData(NBTTagCompound nbt) {
        bloodStats.writeNBT(nbt);
        nbt.setInteger(KEY_EYE, eyeType);
        actionHandler.saveToNbt(nbt);
        skillHandler.saveToNbt(nbt);

    }

    /**
     * Sets the eyeType as long as it is valid.
     * Also sends a sync packet if on server
     *
     * @param eyeType
     * @return Whether the type is valid or not
     */
    public boolean setEyeType(int eyeType) {
        if (eyeType >= REFERENCE.EYE_TYPE_COUNT || eyeType < 0) {
            return false;
        }
        if (eyeType != this.eyeType) {
            this.eyeType = eyeType;
            if (!isRemote()) {
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setInteger(KEY_EYE, eyeType);
                sync(nbt, true);
            }
        }
        return true;
    }

    @Override
    protected VampirismPlayer copyFromPlayer(EntityPlayer old) {
        return get(old);
    }

    @Override
    protected int getMaxLevel() {
        return REFERENCE.HIGHEST_VAMPIRE_LEVEL;
    }

    @Override
    protected void loadUpdate(NBTTagCompound nbt) {
        if (nbt.hasKey(KEY_EYE)) {
            setEyeType(nbt.getInteger(KEY_EYE));
        }
        if (nbt.hasKey(KEY_SPAWN_BITE_PARTICLE)) {
            spawnBiteParticle(nbt.getInteger(KEY_SPAWN_BITE_PARTICLE));
        }
        bloodStats.loadUpdate(nbt);
        actionHandler.readUpdateFromServer(nbt);
        skillHandler.readUpdateFromServer(nbt);
    }

    @Override
    protected void writeFullUpdate(NBTTagCompound nbt) {
        nbt.setInteger(KEY_EYE, getEyeType());
        bloodStats.writeUpdate(nbt);
        actionHandler.writeUpdateForClient(nbt);
        skillHandler.writeUpdateForClient(nbt);
    }

    private void applyEntityAttributes() {
        player.getAttributeMap().registerAttribute(VReference.sunDamage).setBaseValue(Balance.vp.SUNDAMAGE_DAMAGE);
        player.getAttributeMap().registerAttribute(VReference.bloodExhaustion).setBaseValue(Balance.vp.BLOOD_EXHAUSTION_BASIC_MOD);
        player.getAttributeMap().registerAttribute(VReference.biteDamage).setBaseValue(Balance.vp.BITE_DMG);
        player.getAttributeMap().registerAttribute(VReference.garlicDamage).setBaseValue(Balance.vp.GARLIC_DAMAGE);
    }

    /**
     * Bite the given entity.
     * Does NOT chack reach distance
     *
     * @param entity
     */
    private void biteEntity(EntityLivingBase entity) {
        if (isRemote()) return;
        if (getLevel() == 0) return;
        if (biteCooldown > 0) return;
        int blood = 0;
        float saturationMod = 1.0F;
        BITE_TYPE type = determineBiteType(entity);
        if (type == BITE_TYPE.SUCK_BLOOD_CREATURE) {
            blood = ExtendedCreature.get((EntityCreature) entity).onBite(this);
            saturationMod = ExtendedCreature.get((EntityCreature) entity).getBloodSaturation();
        } else if (type == BITE_TYPE.SUCK_BLOOD_PLAYER) {
            blood = VampirePlayer.get((EntityPlayer) entity).onBite(this);
            saturationMod = VampirePlayer.get((EntityPlayer) entity).getBloodSaturation();
        } else if (type == BITE_TYPE.SUCK_BLOOD) {
            blood = ((IBiteableEntity) entity).onBite(this);
            saturationMod = ((IBiteableEntity) entity).getBloodSaturation();
        } else if (type == BITE_TYPE.ATTACK) {
            entity.attackEntityFrom(DamageSource.causePlayerDamage(player), (float) player.getEntityAttribute(VReference.biteDamage).getAttributeValue());
            if (entity.isEntityUndead() && player.getRNG().nextInt(4) == 0) {
                player.addPotionEffect(new PotionEffect(Potion.poison.id, 60));
            }
            if (specialAttributes.poisonous_bite) {
                entity.addPotionEffect(new PotionEffect(Potion.poison.id, Balance.vp.POISONOUS_BITE_DURATION * 20, 1));
            }
        } else if (type == BITE_TYPE.NONE) {
            return;
        }
        biteCooldown = Balance.vp.BITE_COOLDOWN;
        if (blood > 0) {
            consumeBlood(blood, saturationMod);
            player.addStat(Achievements.suckingBlood, 1);
            NBTTagCompound updatePacket = bloodStats.writeUpdate(new NBTTagCompound());
            updatePacket.setInteger(KEY_SPAWN_BITE_PARTICLE, entity.getEntityId());
            sync(updatePacket, true);
        }
    }

    /**
     * Handle garlic damage
     */
    private void handleGarlicDamage() {
        //TODO
        VReference.garlicDamage.getDefaultValue();
    }

    /**
     * Handle blood which could not be filled into the blood stats
     *
     * @param amt
     */
    private void handleSpareBlood(int amt) {
        //TODO
    }

    /**
     * Handle sun damage
     */
    private void handleSunDamage() {
        if (ticksInSun < 100) {
            ticksInSun++;
        }
        if (player.capabilities.isCreativeMode || player.capabilities.disableDamage) return;
        if (Balance.vp.SUNDAMAGE_NAUSEA && player.ticksExisted % 300 == 1 && ticksInSun > 50) {
            player.addPotionEffect(new PotionEffect(Potion.confusion.id, 180));
        }
        if (getLevel() >= Balance.vp.SUNDAMAGE_WEAKNESS_MINLEVEL && player.ticksExisted % 150 == 3) {
            player.addPotionEffect(new PotionEffect(Potion.weakness.id, 152, 1));
        }
        if (getLevel() >= Balance.vp.SUNDAMAGE_MINLEVEL && ticksInSun >= 100 && player.ticksExisted % 40 == 5) {
            float damage = (float) (player.getEntityAttribute(VReference.sunDamage).getAttributeValue());
            player.attackEntityFrom(VReference.sundamage, damage);
        }
    }

    /**
     * Spawn particle after biting an entity
     *
     * @param entityId Id of the entity
     */
    @SideOnly(Side.CLIENT)
    private void spawnBiteParticle(int entityId) {
        Entity entity = player.worldObj.getEntityByID(entityId);
        if (entity != null) {
            UtilLib.spawnParticles(player.worldObj, EnumParticleTypes.CRIT_MAGIC, entity.posX, entity.posY, entity.posZ, player.posX - entity.posX, player.posY - entity.posY, player.posZ - entity.posZ, 10);
        }
        for (int j = 0; j < 16; ++j) {
            Vec3 vec3 = new Vec3((player.getRNG().nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
            vec3 = vec3.rotatePitch(-player.rotationPitch * (float) Math.PI / 180F);
            vec3 = vec3.rotateYaw(-player.rotationYaw * (float) Math.PI / 180F);
            double d0 = (double) (-player.getRNG().nextFloat()) * 0.6D - 0.3D;
            Vec3 vec31 = new Vec3(((double) player.getRNG().nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
            vec31 = vec31.rotatePitch(-player.rotationPitch * (float) Math.PI / 180.0F);
            vec31 = vec31.rotateYaw(-player.rotationYaw * (float) Math.PI / 180.0F);
            vec31 = vec31.addVector(player.posX, player.posY + (double) player.getEyeHeight(), player.posZ);

            player.worldObj.spawnParticle(EnumParticleTypes.ITEM_CRACK, vec31.xCoord, vec31.yCoord, vec31.zCoord, vec3.xCoord, vec3.yCoord + 0.05D, vec3.zCoord, Item.getIdFromItem(Items.apple));
        }
        //Play bite sounds. Using this method since it is the only client side method. And this is called on every relevant client anyway
        player.worldObj.playSound(player.posX, player.posY, player.posZ, REFERENCE.MODID + ":player.bite", 1.0F, 1.0F, false);
    }
}
