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
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVision;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.Achievements;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.LevelAttributeModifier;
import de.teamlapen.vampirism.entity.player.VampirismPlayer;
import de.teamlapen.vampirism.entity.player.actions.ActionHandler;
import de.teamlapen.vampirism.entity.player.skills.SkillHandler;
import de.teamlapen.vampirism.entity.player.vampire.actions.BatVampireAction;
import de.teamlapen.vampirism.fluids.BloodHelper;
import de.teamlapen.vampirism.potion.FakeNightVisionPotionEffect;
import de.teamlapen.vampirism.potion.PotionSanguinare;
import de.teamlapen.vampirism.util.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.network.play.server.SPacketUseBed;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class for Vampire Players.
 */
public class VampirePlayer extends VampirismPlayer<IVampirePlayer> implements IVampirePlayer {

    @CapabilityInject(IVampirePlayer.class)
    public static final Capability<IVampirePlayer> CAP = null;
    private final static String TAG = "VampirePlayer";

    /**
     * Don't call before the construction event of the player entity is finished
     *
     * @param player
     * @return
     */
    public static VampirePlayer get(EntityPlayer player) {
        return (VampirePlayer) player.getCapability(CAP, null);
    }


    public static void registerCapability() {
        CapabilityManager.INSTANCE.register(IVampirePlayer.class, new Storage(), VampirePlayerDefaultImpl.class);
    }

    public static ICapabilityProvider createNewCapability(final EntityPlayer player) {
        return new ICapabilitySerializable<NBTTagCompound>() {

            IVampirePlayer inst = new VampirePlayer(player);

            @Override
            public void deserializeNBT(NBTTagCompound nbt) {
                CAP.getStorage().readNBT(CAP, inst, null, nbt);
            }

            @Override
            public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
                return CAP.equals(capability) ? CAP.<T>cast(inst) : null;//TODO switch to something like SLEEP_CAP.<T>cast(inst) in 1.9
            }

            @Override
            public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
                return capability == CAP;
            }

            @Override
            public NBTTagCompound serializeNBT() {
                return (NBTTagCompound) CAP.getStorage().writeNBT(CAP, inst, null);
            }
        };
    }

    private final BloodStats bloodStats;
    private final String KEY_EYE = "eye_type";
    private final String KEY_SPAWN_BITE_PARTICLE = "bite_particle";
    private final String KEY_VISION = "vision";
    private final ActionHandler<IVampirePlayer> actionHandler;
    private final SkillHandler<IVampirePlayer> skillHandler;
    private final VampirePlayerSpecialAttributes specialAttributes = new VampirePlayerSpecialAttributes();
    private boolean sundamage_cache = false;
    private EnumGarlicStrength garlic_cache = EnumGarlicStrength.NONE;
    private int biteCooldown = 0;
    private int eyeType = 0;
    private int ticksInSun = 0;
    private boolean sleepingInCoffin = false;
    private int sleepTimer = 0;
    private List<IVampireVision> unlockedVisions = new ArrayList<>();
    private IVampireVision activatedVision = null;

    public VampirePlayer(EntityPlayer player) {
        super(player);
        applyEntityAttributes();
        bloodStats = new BloodStats(player);
        actionHandler = new ActionHandler(this);
        skillHandler = new SkillHandler<IVampirePlayer>(this);
    }

    @Override
    public void activateVision(@Nullable IVampireVision vision) {
        if (vision != null && !isRemote() && ((GeneralRegistryImpl) VampirismAPI.vampireVisionRegistry()).getIdOfVision(vision) == -1) {
            throw new IllegalArgumentException("You have to register the vision first: " + vision);
        }
        if (activatedVision != vision) {
            if (activatedVision != null) {
                activatedVision.onDeactivated(this);
            }
            activatedVision = vision;
            if (vision != null) {
                vision.onActivated(this);
            }
            if (!isRemote()) {
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setInteger(KEY_VISION, activatedVision == null ? -1 : ((GeneralRegistryImpl) VampirismAPI.vampireVisionRegistry()).getIdOfVision(activatedVision));
                this.sync(nbt, false);
            }
        }

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
            if (e.getDistanceToEntity(player) <= ((EntityPlayerMP) player).interactionManager.getBlockReachDistance() + 2) {
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
        int left = this.bloodStats.addBlood(amt, saturationMod);
        if (left > 0) {
            handleSpareBlood(left);
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

    @Nullable
    @Override
    public IVampireVision getActiveVision() {
        return activatedVision;
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

    @Override
    public ResourceLocation getCapKey() {
        return REFERENCE.VAMPIRE_PLAYER_KEY;
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

    public boolean isPlayerFullyAsleep() {
        return sleepingInCoffin && sleepTimer >= 100;
    }

    public boolean isPlayerSleeping() {
        return sleepingInCoffin;
    }

    @Override
    public boolean isVampireLord() {
        return false;
    }

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
        if (isPlayerSleeping()) {
            wakeUpPlayer(true, true, false);
        }
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
            LevelAttributeModifier.applyModifier(player, SharedMonsterAttributes.MOVEMENT_SPEED, "Vampire", getLevel(), Balance.vp.SPEED_LCAP, Balance.vp.SPEED_MAX_MOD, Balance.vp.SPEED_TYPE);
            LevelAttributeModifier.applyModifier(player, SharedMonsterAttributes.ATTACK_DAMAGE, "Vampire", getLevel(), Balance.vp.STRENGTH_LCAP, Balance.vp.STRENGTH_MAX_MOD, Balance.vp.STRENGTH_TYPE);
            LevelAttributeModifier.applyModifier(player, SharedMonsterAttributes.MAX_HEALTH, "Vampire", getLevel(), Balance.vp.HEALTH_LCAP, Balance.vp.HEALTH_MAX_MOD, Balance.vp.HEALTH_TYPE);
            LevelAttributeModifier.applyModifier(player, VReference.bloodExhaustion, "Vampire", getLevel(), getMaxLevel(), Balance.vp.EXAUSTION_MAX_MOD, Balance.vp.EXHAUSTION_TYPE);
            if (newLevel > 0) {
                if (player instanceof EntityPlayerMP && ((EntityPlayerMP) player).playerNetServerHandler != null) {
                    //When loading from NBT the playerNetServerHandler is not always initialized, but that's required for achievements. So checking here
                    player.addStat(Achievements.becomingAVampire, 1);
                }

                if (oldLevel == 0) {
                    skillHandler.enableRootSkill();
                }

            } else {
                actionHandler.resetTimers();
                skillHandler.disableAllSkills();
            }
        } else {
            if (oldLevel == 0) {
                if (player.isPotionActive(MobEffects.NIGHT_VISION)) {
                    player.removePotionEffect(MobEffects.NIGHT_VISION);
                }
            } else if (newLevel == 0) {
                if (player.getActivePotionEffect(MobEffects.NIGHT_VISION) instanceof FakeNightVisionPotionEffect) {
                    player.removePotionEffect(MobEffects.NIGHT_VISION);
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
            player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 300));//TODO add saturation as well
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

        if (this.isPlayerSleeping()) {
            player.noClip = true;
            player.motionX = player.motionY = player.motionZ = 0;
            ++this.sleepTimer;

            if (this.sleepTimer > 100) {
                this.sleepTimer = 100;
            }

            if (!player.worldObj.isRemote) {
                IBlockState state = player.worldObj.getBlockState(player.playerLocation);
                boolean bed = state.getBlock().isBed(state, player.worldObj, player.playerLocation, player);
                if (!bed) {
                    player.wakeUpPlayer(true, true, false);
                } else if (!player.worldObj.isDaytime()) {
                    player.wakeUpPlayer(false, true, true);
                }
            }
        } else if (this.sleepTimer > 0) {
            ++this.sleepTimer;

            if (this.sleepTimer >= 110) {
                this.sleepTimer = 0;
            }
        }
        if (activatedVision != null) {
            activatedVision.onUpdate(this);
        }

        if (getSpecialAttributes().lessOxygen && player.getAir() < 300 && player.ticksExisted % 2 == 1) {
            player.setAir(player.getAir() + 1);
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

    @Override
    public void onUpdatePlayer(TickEvent.Phase phase) {
        if (phase == TickEvent.Phase.END) {
            //Update blood stats
            if (getLevel() > 0) {
                if (this.bloodStats.onUpdate()) {
                    sync(this.bloodStats.writeUpdate(new NBTTagCompound()), false);
                }
            }
            if (getSpecialAttributes().bat) {
                BatVampireAction.updatePlayerBatSize(player);
            }
            if (sleepingInCoffin) {
                setEntitySize(0.2F, 0.2F);
            }
        }
    }

    public void saveData(NBTTagCompound nbt) {
        bloodStats.writeNBT(nbt);
        nbt.setInteger(KEY_EYE, eyeType);
        actionHandler.saveToNbt(nbt);
        skillHandler.saveToNbt(nbt);

    }

    /**
     * Set's the players entity size via reflection.
     * Attention: This is reset by EntityPlayer every tick
     * @param width
     * @param height
     * @return
     */
    public boolean setEntitySize(float width, float height) {

        try {
            Method mSetSize = ReflectionHelper.findMethod(Entity.class, player, new String[]{"setSize", SRGNAMES.Entity_setSize}, float.class, float.class);
            mSetSize.invoke(player, width, height);
            return true;
        } catch (Exception e) {
            VampirismMod.log.e(TAG, e, "Could not change players size! ");
            return false;
        }
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

    /**
     * Switch to the next vision
     */
    public void switchVision() {
        int id = -1;
        if (activatedVision != null) {
            id = unlockedVisions.indexOf(activatedVision);
        }
        id++;
        if (id > unlockedVisions.size() - 1) {
            id = -1;
        }
        activateVision(id == -1 ? null : unlockedVisions.get(id));
    }

    @Override
    public EntityPlayer.EnumStatus trySleep(BlockPos bedLocation) {

        if (!player.worldObj.isRemote) {
            if (player.isPlayerSleeping() || !player.isEntityAlive()) {
                return EntityPlayer.EnumStatus.OTHER_PROBLEM;
            }

            if (!player.worldObj.provider.isSurfaceWorld()) {
                return EntityPlayer.EnumStatus.NOT_POSSIBLE_HERE;
            }

            if (!player.worldObj.isDaytime()) {
                return EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW;
            }

            if (Math.abs(player.posX - (double) bedLocation.getX()) > 3.0D || Math.abs(player.posY - (double) bedLocation.getY()) > 2.0D || Math.abs(player.posZ - (double) bedLocation.getZ()) > 3.0D) {
                return EntityPlayer.EnumStatus.TOO_FAR_AWAY;
            }

            double d0 = 8.0D;
            double d1 = 5.0D;
            List<EntityMob> list = player.worldObj.getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB((double) bedLocation.getX() - d0, (double) bedLocation.getY() - d1, (double) bedLocation.getZ() - d0, (double) bedLocation.getX() + d0, (double) bedLocation.getY() + d1, (double) bedLocation.getZ() + d0));

            if (!list.isEmpty()) {
                return EntityPlayer.EnumStatus.NOT_SAFE;
            }
        }

        if (player.isRiding()) {
            player.dismountRidingEntity();
        }
        if (!setEntitySize(0.2F, 0.2F)) return EntityPlayer.EnumStatus.OTHER_PROBLEM;


        IBlockState state = null;
        if (player.worldObj.isBlockLoaded(bedLocation)) state = player.worldObj.getBlockState(bedLocation);
        if (state != null && state.getBlock().isBed(state, player.worldObj, bedLocation, player)) {
            EnumFacing enumfacing = state.getBlock().getBedDirection(state, player.worldObj, bedLocation);
            float f = 0.5F;
            float f1 = 0.5F;

            switch (enumfacing) {
                case SOUTH:
                    f1 = 0.9F;
                    break;
                case NORTH:
                    f1 = 0.1F;
                    break;
                case WEST:
                    f = 0.1F;
                    break;
                case EAST:
                    f = 0.9F;
            }
            try {
                Method mSetSize = ReflectionHelper.findMethod(EntityPlayer.class, player, new String[]{"setRenderOffsetForSleep", SRGNAMES.EntityPlayer_setRenderOffsetForSleep}, EnumFacing.class);
                mSetSize.invoke(player, enumfacing);
            } catch (Exception e) {
                VampirismMod.log.e(TAG, e, "Could set render offset for sleep! ");
                return EntityPlayer.EnumStatus.OTHER_PROBLEM;
            }

            player.setPosition((double) ((float) bedLocation.getX() + f), (double) ((float) bedLocation.getY() + 0.6875F), (double) ((float) bedLocation.getZ() + f1));
        } else {
            player.setPosition((double) ((float) bedLocation.getX() + 0.5F), (double) ((float) bedLocation.getY() + 0.6875F), (double) ((float) bedLocation.getZ() + 0.5F));
        }


        sleepTimer = 0;
        sleepingInCoffin = true;
        player.noClip = true;
        player.playerLocation = bedLocation;
        player.motionX = player.motionZ = player.motionY = 0.0D;

        if (!player.worldObj.isRemote) {
            DaySleepHelper.updateAllPlayersSleeping(player.worldObj);
        }
        if (player instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            Packet<?> packet = new SPacketUseBed(player, bedLocation);
            playerMP.getServerWorld().getEntityTracker().sendToAllTrackingEntity(playerMP, packet);
            playerMP.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
            playerMP.playerNetServerHandler.sendPacket(packet);
        }

        return EntityPlayer.EnumStatus.OK;
    }

    @Override
    public void unUnlockVision(@Nonnull IVampireVision vision) {
        if (activatedVision == vision) {
            activateVision(null);
        }
        unlockedVisions.remove(vision);
    }

    @Override
    public void unlockVision(@Nonnull IVampireVision vision) {
        if (((GeneralRegistryImpl) VampirismAPI.vampireVisionRegistry()).getIdOfVision(vision) == -1) {
            throw new IllegalArgumentException("You have to register the vision first: " + vision);
        }
        unlockedVisions.add(vision);
    }

    @Override
    public void wakeUpPlayer(boolean immediately, boolean updateWorldFlag, boolean setSpawn) {
        VampirismMod.log.d(TAG, "Waking up player");
        if (this.isPlayerSleeping() && player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).getServerWorld().getEntityTracker().sendToTrackingAndSelf(player, new SPacketAnimation(player, 2));
        }
        player.wakeUpPlayer(immediately, false, setSpawn);
        this.sleepingInCoffin = false;
        player.noClip = true;
        if (updateWorldFlag) {
            DaySleepHelper.updateAllPlayersSleeping(player.worldObj);
        }

        if (player instanceof EntityPlayerMP && ((EntityPlayerMP) player).playerNetServerHandler != null) {
            ((EntityPlayerMP) player).playerNetServerHandler.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
        }
    }

    @Override
    protected VampirismPlayer copyFromPlayer(EntityPlayer old) {
        VampirePlayer oldVampire = get(old);
        NBTTagCompound nbt = new NBTTagCompound();
        oldVampire.saveData(nbt);
        this.loadData(nbt);
        return oldVampire;
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
        if (nbt.hasKey(KEY_VISION)) {
            int id = nbt.getInteger(KEY_VISION);
            IVampireVision vision;
            if (id == -1) {
                vision = null;
            } else {
                vision = ((GeneralRegistryImpl) VampirismAPI.vampireVisionRegistry()).getVisionOfId(id);
                if (vision == null) {
                    VampirismMod.log.w(TAG, "Failed to find vision with id %d", id);
                }
            }
            activateVision(vision);

        }

    }

    @Override
    protected void writeFullUpdate(NBTTagCompound nbt) {
        nbt.setInteger(KEY_EYE, getEyeType());
        bloodStats.writeUpdate(nbt);
        actionHandler.writeUpdateForClient(nbt);
        skillHandler.writeUpdateForClient(nbt);
        nbt.setInteger(KEY_VISION, activatedVision == null ? -1 : ((GeneralRegistryImpl) VampirismAPI.vampireVisionRegistry()).getIdOfVision(activatedVision));
    }

    private void applyEntityAttributes() {
        player.getAttributeMap().registerAttribute(VReference.sunDamage).setBaseValue(Balance.vp.SUNDAMAGE_DAMAGE);
        player.getAttributeMap().registerAttribute(VReference.bloodExhaustion).setBaseValue(Balance.vp.BLOOD_EXHAUSTION_BASIC_MOD);
        player.getAttributeMap().registerAttribute(VReference.biteDamage).setBaseValue(Balance.vp.BITE_DMG);
        player.getAttributeMap().registerAttribute(VReference.garlicDamage).setBaseValue(Balance.vp.GARLIC_DAMAGE);
    }

    /**
     * Bite the given entity.
     * Does NOT check reach distance
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
                player.addPotionEffect(new PotionEffect(MobEffects.POISON, 60));
            }
            if (specialAttributes.poisonous_bite) {
                entity.addPotionEffect(new PotionEffect(MobEffects.POISON, Balance.vps.POISONOUS_BITE_DURATION * 20, 1));
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
     * @param amt In food blood unit
     */
    private void handleSpareBlood(int amt) {
        BloodHelper.fillBloodIntoInventory(player, amt * VReference.FOOD_TO_FLUID_BLOOD);
    }

    /**
     * Handle sun damage
     */
    private void handleSunDamage() {
        if (ticksInSun < 100) {
            ticksInSun++;
        }
        if (player.capabilities.isCreativeMode || player.capabilities.disableDamage) return;
        if (Balance.vp.SUNDAMAGE_NAUSEA && getLevel() >= Balance.vp.SUNDAMAGE_NAUSEA_MINLEVEL && player.ticksExisted % 300 == 1 && ticksInSun > 50) {
            player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 180));
        }
        if (getLevel() >= Balance.vp.SUNDAMAGE_WEAKNESS_MINLEVEL && player.ticksExisted % 150 == 3) {
            player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 152, 0));
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
            Vec3d vec3 = new Vec3d((player.getRNG().nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
            vec3 = vec3.rotatePitch(-player.rotationPitch * (float) Math.PI / 180F);
            vec3 = vec3.rotateYaw(-player.rotationYaw * (float) Math.PI / 180F);
            double d0 = (double) (-player.getRNG().nextFloat()) * 0.6D - 0.3D;
            Vec3d vec31 = new Vec3d(((double) player.getRNG().nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
            vec31 = vec31.rotatePitch(-player.rotationPitch * (float) Math.PI / 180.0F);
            vec31 = vec31.rotateYaw(-player.rotationYaw * (float) Math.PI / 180.0F);
            vec31 = vec31.addVector(player.posX, player.posY + (double) player.getEyeHeight(), player.posZ);

            player.worldObj.spawnParticle(EnumParticleTypes.ITEM_CRACK, vec31.xCoord, vec31.yCoord, vec31.zCoord, vec3.xCoord, vec3.yCoord + 0.05D, vec3.zCoord, Item.getIdFromItem(Items.APPLE));
        }
        //Play bite sounds. Using this method since it is the only client side method. And this is called on every relevant client anyway
        player.worldObj.playSound(player.posX, player.posY, player.posZ, ModSounds.player_bite, SoundCategory.PLAYERS, 1.0F, 1.0F, false);
    }

    private static class Storage implements net.minecraftforge.common.capabilities.Capability.IStorage<IVampirePlayer> {
        @Override
        public void readNBT(Capability<IVampirePlayer> capability, IVampirePlayer instance, EnumFacing side, NBTBase nbt) {
            ((VampirePlayer) instance).loadData((NBTTagCompound) nbt);
        }

        @Override
        public NBTBase writeNBT(Capability<IVampirePlayer> capability, IVampirePlayer instance, EnumFacing side) {
            NBTTagCompound nbt = new NBTTagCompound();
            ((VampirePlayer) instance).saveData(nbt);
            return nbt;
        }
    }
}
