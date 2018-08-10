package de.teamlapen.vampirism.player.vampire;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.advancements.VampireActionTrigger;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IBiteableEntity;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVision;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.fluids.BloodHelper;
import de.teamlapen.vampirism.items.ItemHunterCoat;
import de.teamlapen.vampirism.modcompat.SpongeModCompat;
import de.teamlapen.vampirism.player.LevelAttributeModifier;
import de.teamlapen.vampirism.player.VampirismPlayer;
import de.teamlapen.vampirism.player.actions.ActionHandler;
import de.teamlapen.vampirism.player.skills.SkillHandler;
import de.teamlapen.vampirism.player.vampire.actions.BatVampireAction;
import de.teamlapen.vampirism.potion.PotionSanguinare;
import de.teamlapen.vampirism.potion.VampireNightVisionEffect;
import de.teamlapen.vampirism.util.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Main class for Vampire Players.
 */
public class VampirePlayer extends VampirismPlayer<IVampirePlayer> implements IVampirePlayer {

    @CapabilityInject(IVampirePlayer.class)
    public static final Capability<IVampirePlayer> CAP = getNull();
    private final static String TAG = "VampirePlayer";
    private final static String KEY_EYE = "eye_type";
    private final static String KEY_FANGS = "fang_type";
    private final static String KEY_GLOWING_EYES = "glowing_eyes";
    private final static String KEY_SPAWN_BITE_PARTICLE = "bite_particle";
    private final static String KEY_VISION = "vision";

    /**
     * Don't call before the construction event of the player entity is finished
     */
    public static VampirePlayer get(EntityPlayer player) {
        return (VampirePlayer) player.getCapability(CAP, null);
    }

    public static void registerCapability() {
        CapabilityManager.INSTANCE.register(IVampirePlayer.class, new Storage(), VampirePlayerDefaultImpl.class);
    }

    @SuppressWarnings("ConstantConditions")
    public static ICapabilityProvider createNewCapability(final EntityPlayer player) {
        return new ICapabilitySerializable<NBTTagCompound>() {

            final IVampirePlayer inst = new VampirePlayer(player);

            @Override
            public void deserializeNBT(NBTTagCompound nbt) {
                CAP.getStorage().readNBT(CAP, inst, null, nbt);
            }

            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
                return CAP.equals(capability) ? CAP.<T>cast(inst) : null;
            }

            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
                return CAP.equals(capability);
            }

            @Override
            public NBTTagCompound serializeNBT() {
                return (NBTTagCompound) CAP.getStorage().writeNBT(CAP, inst, null);
            }
        };
    }

    private final BloodStats bloodStats;
    private final ActionHandler<IVampirePlayer> actionHandler;
    private final SkillHandler<IVampirePlayer> skillHandler;
    private final VampirePlayerSpecialAttributes specialAttributes = new VampirePlayerSpecialAttributes();
    private boolean sundamage_cache = false;
    private EnumStrength garlic_cache = EnumStrength.NONE;
    private int biteCooldown = 0;
    private int eyeType = 0;
    private int fangType = 0;
    private boolean glowingEyes = true;
    private int ticksInSun = 0;
    private boolean sleepingInCoffin = false;
    private int sleepTimer = 0;
    private boolean wasDead = false;
    private List<IVampireVision> unlockedVisions = new ArrayList<>();
    private IVampireVision activatedVision = null;
    private Method reflectionMethodSetSize = null;

    public VampirePlayer(EntityPlayer player) {
        super(player);
        applyEntityAttributes();
        bloodStats = new BloodStats(player);
        actionHandler = new ActionHandler<>(this);
        skillHandler = new SkillHandler<>(this);
    }

    @Override
    public void activateVision(@Nullable IVampireVision vision) {
        if (vision != null && !isRemote() && ((GeneralRegistryImpl) VampirismAPI.vampireVisionRegistry()).getIdOfVision(vision) == -1) {
            throw new IllegalArgumentException("You have to register the vision first: " + vision);
        }
        if (!Objects.equals(activatedVision, vision)) {
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
     * Try to drink blood from the given block
     * <p>
     * Named like this to match biteEntity
     */
    public void biteBlock(BlockPos pos) {
        if (player.isSpectator()) {
            VampirismMod.log.w(TAG, "Player can't bite in spectator mode");
            return;
        }
        double dist = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() + 1;
        if (player.getDistanceSq(pos) > dist * dist) {
            VampirismMod.log.w(TAG, "Block sent by client is not in reach" + pos);
        } else {
            biteBlock(pos, player.world.getBlockState(pos), player.world.getTileEntity(pos));
        }
    }

    /**
     * Bite the entity with the given id.
     * Checks reach distance
     *
     * @param entityId
     */
    public void biteEntity(int entityId) {
        Entity e = player.getEntityWorld().getEntityByID(entityId);
        if (player.isSpectator()) {
            VampirismMod.log.w(TAG, "Player can't bite in spectator mode");
            return;
        }
        if (e != null && e instanceof EntityLivingBase) {

            if (e.getDistance(player) <= player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() + 1) {
                biteEntity((EntityLivingBase) e);
            } else {
                VampirismMod.log.w(TAG, "Entity sent by client is not in reach " + entityId);
            }
        }
    }

    @Override
    public float calculateFireDamage(float amount) {
        float protectionMod = 1F;
        PotionEffect protection = player.getActivePotionEffect(ModPotions.fire_protection);
        if (protection != null) {
            protectionMod = 1F / (2F + protection.getAmplifier());
        }

        return amount * protectionMod * (float) LevelAttributeModifier.calculateModifierValue(getLevel(), Balance.vp.FIRE_VULNERABILITY_LCAP, Balance.vp.FIRE_VULNERABILITY_MAX_MOD, Balance.vp.FIRE_VULNERABILITY_TYPE);
    }

    @Override
    public boolean canBeBitten(IVampire biter) {
        return !(player.isSpectator() || player.isCreative());
    }

    @Override
    public boolean canLeaveFaction() {
        return true;
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
            boolean hunter = Helper.isHunter(player);
            if (!UtilLib.canReallySee(entity, player, false) && VampirePlayer.get((EntityPlayer) entity).canBeBitten(this)) {
                return hunter ? BITE_TYPE.SUCK_BLOOD_HUNTER_PLAYER : BITE_TYPE.SUCK_BLOOD_PLAYER;

            } else return hunter ? BITE_TYPE.ATTACK_HUNTER : BITE_TYPE.ATTACK;
        }
        return BITE_TYPE.ATTACK;
    }

    @Override
    public boolean doesResistGarlic(EnumStrength strength) {
        return false;
    }

    @Override
    public void drinkBlood(int amt, float saturationMod) {
        int left = this.bloodStats.addBlood(amt, saturationMod);
        if (left > 0) {
            handleSpareBlood(left);
        }
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
    public float getBloodLevelRelative() {
        return bloodStats.getBloodLevel() / (float) bloodStats.getMaxBlood();
    }

    @Override
    public float getBloodSaturation() {
        return (float) Balance.vp.PLAYER_BLOOD_SATURATION;
    }

    @Override
    public IBloodStats getBloodStats() {
        return bloodStats;
    }

    @Override
    public ResourceLocation getCapKey() {
        return REFERENCE.VAMPIRE_PLAYER_KEY;
    }

    @Override
    public
    @Nullable
    IFaction getDisguisedAs() {
        return isDisguised() ? specialAttributes.disguisedAs : getFaction();
    }

    /**
     * @return Eyetype for rendering
     */
    public int getEyeType() {
        return eyeType;
    }

    /**
     * @return Fangtype for rendering
     */
    public int getFangType() {
        return fangType;
    }

    /**
     * @return Render eyes glowing
     */
    public boolean getGlowingEyes() {
        return glowingEyes;
    }

    /**
     * Sets glowing eyes.
     * Also sends a sync packet if on server
     *
     * @param value
     */
    public void setGlowingEyes(boolean value) {
        if (value != this.glowingEyes) {
            this.glowingEyes = value;
            if (!isRemote()) {
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setBoolean(KEY_GLOWING_EYES, glowingEyes);
                sync(nbt, true);
            }
        }
    }

    @Override
    public int getMaxLevel() {
        return REFERENCE.HIGHEST_VAMPIRE_LEVEL;
    }

    @Override
    public Predicate<Entity> getNonFriendlySelector(boolean otherFactionPlayers, boolean ignoreDisguise) {
        if (otherFactionPlayers) {
            return entity -> true;
        } else {
            return VampirismAPI.factionRegistry().getPredicate(getFaction(), ignoreDisguise);
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


    @Nonnull
    @Override
    public EnumStrength isGettingGarlicDamage(boolean forcerefresh) {
        if (forcerefresh) {
            garlic_cache = Helper.getGarlicStrength(player);
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
    public boolean isIgnoringSundamage() {
        return false;
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
        fangType = nbt.getInteger(KEY_FANGS);
        glowingEyes = !nbt.hasKey(KEY_GLOWING_EYES) || nbt.getBoolean(KEY_GLOWING_EYES);
        actionHandler.loadFromNbt(nbt);
        skillHandler.loadFromNbt(nbt);
    }

    @Override
    public int onBite(IVampire biter) {
        float perc = biter instanceof IVampirePlayer ? 1F : 0.4F;
        if (getLevel() == 0) {
            int amt = player.getFoodStats().getFoodLevel();
            int sucked = (int) (amt * perc);
            player.getFoodStats().setFoodLevel(amt - sucked);
            player.addExhaustion(1000F);
            if (!player.isPotionActive(ModPotions.sanguinare) && Helper.canTurnPlayer(biter, player) && Helper.canBecomeVampire(player)) {
                PotionSanguinare.addRandom(player, true);
            }
            return sucked;
        }
        int amt = this.getBloodStats().getBloodLevel();
        int sucked = (int) (amt * perc);
        this.getBloodStats().consumeBlood(sucked);
        sync(this.bloodStats.writeUpdate(new NBTTagCompound()), true);
        return sucked;
    }

    @Override
    public void onChangedDimension(int from, int to) {

    }

    @Override
    public void onDeath(DamageSource src) {
        actionHandler.deactivateAllActions();
        wasDead = true;
    }

    @Override
    public boolean onEntityAttacked(DamageSource src, float amt) {
        if (isPlayerSleeping()) {
            wakeUpPlayer(true, true, false);
        }
        if (getLevel() > 0) {
            if (DamageSource.ON_FIRE.equals(src)) {

                player.attackEntityFrom(VReference.VAMPIRE_ON_FIRE, calculateFireDamage(amt));
                return true;
            } else if (DamageSource.IN_FIRE.equals(src) || DamageSource.LAVA.equals(src)) {
                player.attackEntityFrom(VReference.VAMPIRE_IN_FIRE, calculateFireDamage(amt));
                return true;
            }
        }

        return false;
    }

    @Override
    public void onJoinWorld() {
        if (getLevel() > 0) {
            actionHandler.onActionsReactivated();
            ticksInSun = 0;
            if (wasDead) {
                if (Loader.isModLoaded(SpongeModCompat.MODID)) {
                    //Workaround for issue caused by https://github.com/SpongePowered/SpongeForge/issues/736 TODO remove?
                    int level = getLevel();
                    onLevelChanged(level, level);
                }
                player.addPotionEffect(new PotionEffect(ModPotions.sunscreen, 400, 4, true, false));
                player.setHealth(player.getMaxHealth());
                bloodStats.setBloodLevel(bloodStats.getMaxBlood());
            }
        }
    }

    @Override
    public void onLevelChanged(int newLevel, int oldLevel) {
        if (!isRemote()) {
            ScoreboardUtil.updateScoreboard(player, ScoreboardUtil.VAMPIRE_LEVEL_CRITERIA, newLevel);
            checkAttributes(VReference.bloodExhaustion);
            LevelAttributeModifier.applyModifier(player, SharedMonsterAttributes.MOVEMENT_SPEED, "Vampire", getLevel(), Balance.vp.SPEED_LCAP, Balance.vp.SPEED_MAX_MOD, Balance.vp.SPEED_TYPE, 2, false);
            LevelAttributeModifier.applyModifier(player, SharedMonsterAttributes.ATTACK_DAMAGE, "Vampire", getLevel(), Balance.vp.STRENGTH_LCAP, Balance.vp.STRENGTH_MAX_MOD, Balance.vp.STRENGTH_TYPE, 2, false);
            LevelAttributeModifier.applyModifier(player, SharedMonsterAttributes.MAX_HEALTH, "Vampire", getLevel(), Balance.vp.HEALTH_LCAP, Balance.vp.HEALTH_MAX_MOD, Balance.vp.HEALTH_TYPE, 0, true);
            if (player.getHealth() > player.getMaxHealth()) player.setHealth(player.getMaxHealth());
            LevelAttributeModifier.applyModifier(player, VReference.bloodExhaustion, "Vampire", getLevel(), getMaxLevel(), Balance.vp.EXHAUSTION_MAX_MOD, Balance.vp.EXHAUSTION_TYPE, 2, false);
            if (newLevel > 13) {
                bloodStats.setMaxBlood(40);
            } else if (newLevel > 9) {
                bloodStats.setMaxBlood(34);
            } else if (newLevel > 6) {
                bloodStats.setMaxBlood(30);
            } else if (newLevel > 3) {
                bloodStats.setMaxBlood(26);
            } else {
                bloodStats.setMaxBlood(20);
            }
            if (newLevel > 0) {
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
                if (player.getActivePotionEffect(MobEffects.NIGHT_VISION) instanceof VampireNightVisionEffect) {
                    player.removePotionEffect(MobEffects.NIGHT_VISION);
                }
                actionHandler.resetTimers();
            }
        }
    }

    @Override
    public void onPlayerLoggedIn() {
        if (getLevel() > 0 && FMLCommonHandler.instance().getSide().isServer()) {
            player.addPotionEffect(new PotionEffect(ModPotions.sunscreen, 200, 4, true, false));
        }
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
//            ((WorldServer) player.world).addScheduledTask(new Runnable() {
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
        player.getEntityWorld().profiler.startSection("vampirism_vampirePlayer");
        int level = getLevel();
        if (level > 0) {
            if (player.ticksExisted % REFERENCE.REFRESH_SUNDAMAGE_TICKS == 0) {
                isGettingSundamage(true);
            }
            if (player.ticksExisted % REFERENCE.REFRESH_GARLIC_TICKS == 0) {
                isGettingGarlicDamage(true);
            }
        } else {
            sundamage_cache = false;
            garlic_cache = EnumStrength.NONE;
        }

        if (this.isPlayerSleeping()) {
            player.noClip = true;
            player.motionX = player.motionY = player.motionZ = 0;
            ++this.sleepTimer;

            if (this.sleepTimer > 100) {
                this.sleepTimer = 100;
            }

            if (!player.getEntityWorld().isRemote) {
                IBlockState state = player.getEntityWorld().getBlockState(player.bedLocation);
                boolean bed = state.getBlock().isBed(state, player.getEntityWorld(), player.bedLocation, player);
                if (!bed) {
                    wakeUpPlayer(true, true, false);
                } else if (!player.getEntityWorld().isDaytime()) {
                    wakeUpPlayer(false, true, true);
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


        if (!isRemote()) {
            if (level > 0) {
                boolean sync = false;
                boolean syncToAll = false;
                NBTTagCompound syncPacket = new NBTTagCompound();

                if (biteCooldown > 0) biteCooldown--;
                if (isGettingSundamage()) {
                    handleSunDamage(false);
                } else if (ticksInSun > 0) {
                    ticksInSun--;
                }
                if (isGettingGarlicDamage() != EnumStrength.NONE) {
                    DamageHandler.affectVampireGarlicAmbient(this, isGettingGarlicDamage(), player.ticksExisted);
                }
                if (player.isEntityAlive() && player.isInWater()) {
                    player.setAir(300);
                    if (player.ticksExisted % 16 == 4 && !getSpecialAttributes().waterResistance) {
                        player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 80, (int) (getLevel() / (float) getMaxLevel() * 3)));
                    }
                }

                if (player.ticksExisted % 9 == 3 && player.isPotionActive(MobEffects.FIRE_RESISTANCE)) {
                    PotionEffect fireResistance = player.getActivePotionEffect(MobEffects.FIRE_RESISTANCE);
                    player.addPotionEffect(new PotionEffect(ModPotions.fire_protection, fireResistance.getDuration(), fireResistance.getAmplifier()));
                    player.removePotionEffect(MobEffects.FIRE_RESISTANCE);
                }
                if (player.ticksExisted % 9 == 3 && player.isPotionActive(MobEffects.HUNGER)) {
                    PotionEffect hunterEffect = player.getActivePotionEffect(MobEffects.HUNGER);
                    player.addPotionEffect(new PotionEffect(ModPotions.thirst, hunterEffect.getDuration(), hunterEffect.getAmplifier()));
                    player.removePotionEffect(MobEffects.HUNGER);
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
                    handleSunDamage(true);
                } else if (ticksInSun > 0) {
                    ticksInSun--;
                }
            } else {
                ticksInSun = 0;
            }

        }
        player.world.profiler.endSection();
    }

    @Override
    public void onUpdatePlayer(TickEvent.Phase phase) {
        if (phase == TickEvent.Phase.END) {
            //Update blood stats
            if (getLevel() > 0) {
                player.world.profiler.startSection("vampirism_bloodupdate");
                if (!player.world.isRemote && this.bloodStats.onUpdate()) {
                    sync(this.bloodStats.writeUpdate(new NBTTagCompound()), false);
                }
                player.world.profiler.endSection();
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
        nbt.setInteger(KEY_FANGS, fangType);
        nbt.setBoolean(KEY_GLOWING_EYES, glowingEyes);
        actionHandler.saveToNbt(nbt);
        skillHandler.saveToNbt(nbt);

    }

    /**
     * Set's the players entity size via reflection.
     * Attention: This is reset by EntityPlayer every tick
     *
     * @param width
     * @param height
     * @return
     */
    public boolean setEntitySize(float width, float height) {

        try {
            if (reflectionMethodSetSize == null) {
                reflectionMethodSetSize = ReflectionHelper.findMethod(Entity.class, "setSize", SRGNAMES.Entity_setSize, float.class, float.class);
            }
            reflectionMethodSetSize.invoke(player, width, height);
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
     * Sets the fangType as long as it is valid.
     * Also sends a sync packet if on server
     *
     * @return Whether the type is valid or not
     */
    public boolean setFangType(int fangType) {
        if (fangType >= REFERENCE.FANG_TYPE_COUNT || fangType < 0) {
            return false;
        }
        if (fangType != this.fangType) {
            this.fangType = fangType;
            if (!isRemote()) {
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setInteger(KEY_FANGS, fangType);
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
    public EntityPlayer.SleepResult trySleep(BlockPos bedLocation) {

        if (!player.world.isRemote) {
            if (player.isPlayerSleeping() || !player.isEntityAlive()) {
                return EntityPlayer.SleepResult.OTHER_PROBLEM;
            }

            if (!player.world.provider.isSurfaceWorld()) {
                return EntityPlayer.SleepResult.NOT_POSSIBLE_HERE;
            }

            if (Math.abs(player.posX - (double) bedLocation.getX()) > 3.0D || Math.abs(player.posY - (double) bedLocation.getY()) > 2.0D || Math.abs(player.posZ - (double) bedLocation.getZ()) > 3.0D) {
                return EntityPlayer.SleepResult.TOO_FAR_AWAY;
            }

            double d0 = 8.0D;
            double d1 = 5.0D;
            List<EntityMob> list = player.world.getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB((double) bedLocation.getX() - d0, (double) bedLocation.getY() - d1, (double) bedLocation.getZ() - d0, (double) bedLocation.getX() + d0, (double) bedLocation.getY() + d1, (double) bedLocation.getZ() + d0));

            if (!list.isEmpty()) {
                return EntityPlayer.SleepResult.NOT_SAFE;
            }
            if (!player.world.isDaytime()) {
                player.bedLocation = bedLocation; //Set sleep location even if night time
                return EntityPlayer.SleepResult.NOT_POSSIBLE_NOW;
            }
        }

        if (player.isRiding()) {
            player.dismountRidingEntity();
        }
        if (!setEntitySize(0.2F, 0.2F)) return EntityPlayer.SleepResult.OTHER_PROBLEM;


        IBlockState state = null;
        if (player.world.isBlockLoaded(bedLocation)) state = player.world.getBlockState(bedLocation);
        if (state != null && state.getBlock().isBed(state, player.world, bedLocation, player)) {
            EnumFacing enumfacing = state.getBlock().getBedDirection(state, player.world, bedLocation);
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
                    break;
                default://Should not happen
            }
            player.setRenderOffsetForSleep(enumfacing);

            player.setPosition((double) ((float) bedLocation.getX() + f), (double) ((float) bedLocation.getY() + 0.6875F), (double) ((float) bedLocation.getZ() + f1));
        } else {
            player.setPosition((double) ((float) bedLocation.getX() + 0.5F), (double) ((float) bedLocation.getY() + 0.6875F), (double) ((float) bedLocation.getZ() + 0.5F));
        }

        player.capabilities.isFlying = false;
        player.sendPlayerAbilities();
        sleepTimer = 0;
        sleepingInCoffin = true;
        player.noClip = true;
        player.bedLocation = bedLocation; //Is also set if sleep fails due to night time. See above
        player.motionX = player.motionZ = player.motionY = 0.0D;

        if (!player.world.isRemote) {
            DaySleepHelper.updateAllPlayersSleeping(player.world);
        }
        if (player instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            Packet<?> packet = new SPacketUseBed(player, bedLocation);
            playerMP.getServerWorld().getEntityTracker().sendToTracking(playerMP, packet);
            playerMP.connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
            playerMP.connection.sendPacket(packet);
        }

        return EntityPlayer.SleepResult.OK;
    }

    @Override
    public void unUnlockVision(@Nonnull IVampireVision vision) {
        if (vision.equals(activatedVision)) {
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
            DaySleepHelper.updateAllPlayersSleeping(player.world);
        }

        if (player instanceof EntityPlayerMP && ((EntityPlayerMP) player).connection != null) {
            ((EntityPlayerMP) player).connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
        }
    }

    @Override
    public boolean wantsBlood() {
        return getLevel() > 0 && bloodStats.needsBlood();
    }

    /**
     * Check if the given attribute is currently registered, if not it registers it
     * Only necessary because SpongeForge currently does not recreate the player on death but resets the attribute map
     * TODO maybe remove once fixed
     * https://github.com/SpongePowered/SpongeForge/issues/736
     *
     * @param attributes
     */
    protected void checkAttributes(IAttribute... attributes) {
        for (IAttribute attribute : attributes) {
            if (player.getEntityAttribute(attribute) == null) {
                applyEntityAttributes();
                return;//apply entity attributes also readds all others
            }
        }

    }

    @Override
    protected VampirismPlayer copyFromPlayer(EntityPlayer old) {
        VampirePlayer oldVampire = get(old);
        NBTTagCompound nbt = new NBTTagCompound();
        oldVampire.saveData(nbt);
        this.loadData(nbt);
        this.wasDead = oldVampire.wasDead;
        return oldVampire;
    }

    @Override
    protected void loadUpdate(NBTTagCompound nbt) {
        if (nbt.hasKey(KEY_EYE)) {
            setEyeType(nbt.getInteger(KEY_EYE));
        }
        if (nbt.hasKey(KEY_FANGS)) {
            setFangType(nbt.getInteger(KEY_FANGS));
        }
        if (nbt.hasKey(KEY_SPAWN_BITE_PARTICLE)) {
            spawnBiteParticle(nbt.getInteger(KEY_SPAWN_BITE_PARTICLE));
        }
        if (nbt.hasKey(KEY_GLOWING_EYES)) {
            setGlowingEyes(nbt.getBoolean(KEY_GLOWING_EYES));
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
        nbt.setInteger(KEY_FANGS, getFangType());
        nbt.setBoolean(KEY_GLOWING_EYES, getGlowingEyes());
        bloodStats.writeUpdate(nbt);
        actionHandler.writeUpdateForClient(nbt);
        skillHandler.writeUpdateForClient(nbt);
        nbt.setInteger(KEY_VISION, activatedVision == null ? -1 : ((GeneralRegistryImpl) VampirismAPI.vampireVisionRegistry()).getIdOfVision(activatedVision));
    }

    private void applyEntityAttributes() {
        //Checking if already registered, since this method has to be called multiple times due to SpongeForge not recreating the player, but resetting the attribute map
        if (player.getAttributeMap().getAttributeInstance(VReference.sunDamage) == null) {
            player.getAttributeMap().registerAttribute(VReference.sunDamage).setBaseValue(Balance.vp.SUNDAMAGE_DAMAGE);
        }
        if (player.getAttributeMap().getAttributeInstance(VReference.bloodExhaustion) == null) {
            player.getAttributeMap().registerAttribute(VReference.bloodExhaustion).setBaseValue(Balance.vp.BLOOD_EXHAUSTION_BASIC_MOD);
        }
        if (player.getAttributeMap().getAttributeInstance(VReference.biteDamage) == null) {
            player.getAttributeMap().registerAttribute(VReference.biteDamage).setBaseValue(Balance.vp.BITE_DMG);

        }
    }

    private void biteBlock(@Nonnull BlockPos pos, @Nonnull IBlockState blockState, @Nullable TileEntity tileEntity) {
        if (isRemote()) return;
        if (getLevel() == 0) return;
        if (biteCooldown > 0) return;
        if (!bloodStats.needsBlood()) return;

        int blood = 0;
        int need = Math.min(8, bloodStats.getMaxBlood() - bloodStats.getBloodLevel());
        if (ModBlocks.blood_container.equals(blockState.getBlock())) {
            if (tileEntity != null && tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
                IFluidHandler handler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                FluidStack drainable = handler.drain(new FluidStack(ModFluids.blood, need * VReference.FOOD_TO_FLUID_BLOOD), false);
                if (drainable != null && drainable.amount >= VReference.FOOD_TO_FLUID_BLOOD) {
                    FluidStack drained = handler.drain((drainable.amount / VReference.FOOD_TO_FLUID_BLOOD) * VReference.FOOD_TO_FLUID_BLOOD, true);
                    if (drained != null) {
                        blood = drained.amount / VReference.FOOD_TO_FLUID_BLOOD;
                    }
                }
            }
        }

        if (blood > 0) {
            drinkBlood(blood, 0.3F);

            NBTTagCompound updatePacket = bloodStats.writeUpdate(new NBTTagCompound());
            sync(updatePacket, true);

            biteCooldown = Balance.vp.BITE_COOLDOWN;

        }

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
        } else if (type == BITE_TYPE.SUCK_BLOOD_PLAYER || type == BITE_TYPE.SUCK_BLOOD_HUNTER_PLAYER) {
            blood = VampirePlayer.get((EntityPlayer) entity).onBite(this);
            saturationMod = VampirePlayer.get((EntityPlayer) entity).getBloodSaturation();
            if (type == BITE_TYPE.SUCK_BLOOD_HUNTER_PLAYER) {
                player.addPotionEffect(new PotionEffect(MobEffects.POISON, 15, 2));
            }
        } else if (type == BITE_TYPE.SUCK_BLOOD) {
            blood = ((IBiteableEntity) entity).onBite(this);
            saturationMod = ((IBiteableEntity) entity).getBloodSaturation();
        } else if (type == BITE_TYPE.ATTACK || type == BITE_TYPE.ATTACK_HUNTER) {
            checkAttributes(VReference.biteDamage);
            float damage = getSpecialAttributes().bat ? 0.1F : (float) player.getEntityAttribute(VReference.biteDamage).getAttributeValue();
            entity.attackEntityFrom(DamageSource.causePlayerDamage(player), damage);
            if (entity.isEntityUndead() && player.getRNG().nextInt(4) == 0) {
                player.addPotionEffect(new PotionEffect(MobEffects.POISON, 60));
            } else if (type == BITE_TYPE.ATTACK_HUNTER) {
                if (entity instanceof EntityPlayer && ItemHunterCoat.isFullyEquipped((EntityPlayer) entity)) {
                    player.attackEntityFrom(DamageSource.causeThornsDamage(entity), damage);
                }
            }
            if (specialAttributes.poisonous_bite) {
                entity.addPotionEffect(new PotionEffect(MobEffects.POISON, (int) (Balance.vps.POISONOUS_BITE_DURATION * 20 * (getSpecialAttributes().bat ? 0.2F : 1F)), 1));
            }
        } else if (type == BITE_TYPE.NONE) {
            return;
        }
        biteCooldown = Balance.vp.BITE_COOLDOWN;
        if (blood > 0) {
            drinkBlood(blood, saturationMod);
            //TODO player.addStat(Achievements.suckingBlood, 1);
            NBTTagCompound updatePacket = bloodStats.writeUpdate(new NBTTagCompound());
            updatePacket.setInteger(KEY_SPAWN_BITE_PARTICLE, entity.getEntityId());
            sync(updatePacket, true);
        }
        if (type == BITE_TYPE.SUCK_BLOOD || type == BITE_TYPE.SUCK_BLOOD_CREATURE || type == BITE_TYPE.SUCK_BLOOD_PLAYER) {
            if (player instanceof EntityPlayerMP) {
                ModAdvancements.TRIGGER_VAMPIRE_ACTION.trigger((EntityPlayerMP) player, VampireActionTrigger.Action.SUCK_BLOOD);
            }
        }
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
    private void handleSunDamage(boolean isRemote) {
        PotionEffect potionEffect = player.getActivePotionEffect(ModPotions.sunscreen);
        int sunscreen = potionEffect == null ? -1 : potionEffect.getAmplifier();
        if (ticksInSun < 100) {
            ticksInSun++;
        }
        if (sunscreen >= 5 && ticksInSun > 50) {
            ticksInSun = 50;
        }
        if (isRemote || player.capabilities.isCreativeMode || player.capabilities.disableDamage) return;
        if (Balance.vp.SUNDAMAGE_NAUSEA && getLevel() >= Balance.vp.SUNDAMAGE_NAUSEA_MINLEVEL && player.ticksExisted % 300 == 1 && ticksInSun > 50 && sunscreen == -1) {
            player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 180));
        }
        if (getLevel() >= Balance.vp.SUNDAMAGE_WEAKNESS_MINLEVEL && player.ticksExisted % 150 == 3 && sunscreen < 5) {
            player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 152, 0));
        }
        if (getLevel() >= Balance.vp.SUNDAMAGE_MINLEVEL && ticksInSun >= 100 && player.ticksExisted % 40 == 5) {
            checkAttributes(VReference.sunDamage);
            float damage = (float) (player.getEntityAttribute(VReference.sunDamage).getAttributeValue());
            if (damage > 0) player.attackEntityFrom(VReference.SUNDAMAGE, damage);
        }
    }

    /**
     * Spawn particle after biting an entity
     *
     * @param entityId Id of the entity
     */
    private void spawnBiteParticle(int entityId) {
        Entity entity = player.world.getEntityByID(entityId);
        if (entity != null) {
            UtilLib.spawnParticles(player.world, EnumParticleTypes.CRIT_MAGIC, entity.posX, entity.posY, entity.posZ, player.posX - entity.posX, player.posY - entity.posY, player.posZ - entity.posZ, 10, 1);
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

            player.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, vec31.x, vec31.y, vec31.z, vec3.x, vec3.y + 0.05D, vec3.z, Item.getIdFromItem(Items.APPLE));
        }
        //Play bite sounds. Using this method since it is the only client side method. And this is called on every relevant client anyway
        player.world.playSound(player.posX, player.posY, player.posZ, ModSounds.player_bite, SoundCategory.PLAYERS, 1.0F, 1.0F, false);
    }

    private static class Storage implements Capability.IStorage<IVampirePlayer> {
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
