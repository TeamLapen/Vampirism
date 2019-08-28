package de.teamlapen.vampirism.player.vampire;

import com.mojang.datafixers.util.Either;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.advancements.VampireActionTrigger;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IBiteableEntity;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
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
import de.teamlapen.vampirism.items.HunterCoatItem;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.particle.FlyingBloodEntityParticleData;
import de.teamlapen.vampirism.player.LevelAttributeModifier;
import de.teamlapen.vampirism.player.VampirismPlayer;
import de.teamlapen.vampirism.player.actions.ActionHandler;
import de.teamlapen.vampirism.player.skills.SkillHandler;
import de.teamlapen.vampirism.player.vampire.actions.BatVampireAction;
import de.teamlapen.vampirism.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.potion.PotionSanguinare;
import de.teamlapen.vampirism.potion.VampireNightVisionEffect;
import de.teamlapen.vampirism.util.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.play.server.SAnimateHandPacket;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final Logger LOGGER = LogManager.getLogger(VampirePlayer.class);
    @CapabilityInject(IVampirePlayer.class)
    public static Capability<IVampirePlayer> CAP = getNull();
    private final static String TAG = "VampirePlayer";
    private final static String KEY_EYE = "eye_type";
    private final static String KEY_FANGS = "fang_type";
    private final static String KEY_GLOWING_EYES = "glowing_eyes";
    private final static String KEY_SPAWN_BITE_PARTICLE = "bite_particle";
    private final static String KEY_VISION = "vision";
    private final static String KEY_VICTIM_ID = "feed_victim";

    /**
     * Don't call before the construction event of the player entity is finished
     */
    public static VampirePlayer get(PlayerEntity player) {
        return (VampirePlayer) player.getCapability(CAP, null).orElseThrow(() -> new IllegalStateException("Cannot get Vampire player capability from player " + player));
    }

    public static void registerCapability() {
        CapabilityManager.INSTANCE.register(IVampirePlayer.class, new Storage(), VampirePlayerDefaultImpl::new);
    }

    public static ICapabilityProvider createNewCapability(final PlayerEntity player) {
        return new ICapabilitySerializable<CompoundNBT>() {

            final IVampirePlayer inst = new VampirePlayer(player);
            final LazyOptional<IVampirePlayer> opt = LazyOptional.of(() -> inst);

            @Override
            public void deserializeNBT(CompoundNBT nbt) {
                CAP.getStorage().readNBT(CAP, inst, null, nbt);
            }

            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {
                return CAP.orEmpty(capability, opt);
            }

            @Override
            public CompoundNBT serializeNBT() {
                return (CompoundNBT) CAP.getStorage().writeNBT(CAP, inst, null);
            }
        };
    }

    private final BloodStats bloodStats;
    private final ActionHandler<IVampirePlayer> actionHandler;
    private final SkillHandler<IVampirePlayer> skillHandler;
    private final VampirePlayerSpecialAttributes specialAttributes = new VampirePlayerSpecialAttributes();
    private boolean sundamage_cache = false;
    private EnumStrength garlic_cache = EnumStrength.NONE;
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

    private int feed_victim = -1;
    private BITE_TYPE feed_victim_bite_type;
    private int feedBiteTickCounter = 0;

    public VampirePlayer(PlayerEntity player) {
        super(player);
        applyEntityAttributes();
        bloodStats = new BloodStats(player);
        actionHandler = new ActionHandler<>(this);
        skillHandler = new SkillHandler<>(this, VReference.VAMPIRE_FACTION);
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
                CompoundNBT nbt = new CompoundNBT();
                nbt.putInt(KEY_VISION, activatedVision == null ? -1 : ((GeneralRegistryImpl) VampirismAPI.vampireVisionRegistry()).getIdOfVision(activatedVision));
                this.sync(nbt, false);
            }
        }

    }

    /**
     * Increases exhaustion level by supplied amount
     */
    public void addExhaustion(float p_71020_1_) {
        if (!player.abilities.disableDamage && getLevel() > 0) {
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
            LOGGER.warn("Player can't bite in spectator mode");
            return;
        }
        double dist = player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue() + 1;
        if (player.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) > dist * dist) {
            LOGGER.warn("Block sent by client is not in reach" + pos);
        } else {
            biteBlock(pos, player.world.getBlockState(pos), player.world.getTileEntity(pos));
        }
    }

    /**
     * Bite the entity with the given id.
     * Checks reach distance
     *
     * @param entityId The id of the entity to start biting
     */
    public void biteEntity(int entityId) {
        Entity e = player.getEntityWorld().getEntityByID(entityId);
        if (player.isSpectator()) {
            LOGGER.warn("Player can't bite in spectator mode");
            return;
        }
        if (getActionHandler().isActionActive(VampireActions.bat)) {
            LOGGER.warn("Cannot bite in bat mode");
            return;
        }
        if (e instanceof LivingEntity) {
            if (e.getDistance(player) <= player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue() + 1) {
                feed_victim_bite_type = determineBiteType((LivingEntity) e);
                if (feed_victim_bite_type == BITE_TYPE.ATTACK || feed_victim_bite_type == BITE_TYPE.ATTACK_HUNTER || feed_victim_bite_type == BITE_TYPE.HUNTER_CREATURE) {
                    biteAttack((LivingEntity) e, feed_victim_bite_type == BITE_TYPE.ATTACK_HUNTER);
                } else if (feed_victim_bite_type == BITE_TYPE.NONE) {
                    return;
                } else {
                    if (feed_victim == -1) feedBiteTickCounter = 0;

                    feed_victim = e.getEntityId();

                    ((LivingEntity) e).addPotionEffect(new EffectInstance(Effects.SLOWNESS, 20, 7, false, false));
                    player.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 25, 4, false, false));

                    CompoundNBT nbt = new CompoundNBT();
                    nbt.putInt(KEY_VICTIM_ID, feed_victim);
                    sync(nbt, true);

                }
            } else {
                LOGGER.warn("Entity sent by client is not in reach " + entityId);
            }
        }
    }

    @Override
    public float calculateFireDamage(float amount) {
        float protectionMod = 1F;
        EffectInstance protection = player.getActivePotionEffect(ModEffects.fire_protection);
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

    public BITE_TYPE determineBiteType(LivingEntity entity) {
        if (entity instanceof IBiteableEntity) {
            if (((IBiteableEntity) entity).canBeBitten(this)) return BITE_TYPE.SUCK_BLOOD;
        }
        if (entity instanceof CreatureEntity) {
            if (ExtendedCreature.get((CreatureEntity) entity).canBeBitten(this)) {
                if (ExtendedCreature.get((CreatureEntity) entity).hasPoisonousBlood()) {
                    return BITE_TYPE.HUNTER_CREATURE;
                }
                return BITE_TYPE.SUCK_BLOOD_CREATURE;
            }
        } else if (entity instanceof PlayerEntity) {
            if (((PlayerEntity) entity).abilities.isCreativeMode || !Permissions.getPermission("pvp", player)) {
                return BITE_TYPE.NONE;
            }
            boolean hunter = Helper.isHunter(player);
            if (!UtilLib.canReallySee(entity, player, false) && VampirePlayer.get((PlayerEntity) entity).canBeBitten(this)) {
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
    public void drinkBlood(int amt, float saturationMod, boolean useRemaining) {
        int left = this.bloodStats.addBlood(amt, saturationMod);
        if (useRemaining && left > 0) {
            handleSpareBlood(left);
        }
    }

    /**
     * Cleanly ends biting process
     */
    public void endFeeding(boolean sync) {
        if (feed_victim != -1)
            feed_victim = -1;
        feed_victim_bite_type = null;
        player.removePotionEffect(Effects.SLOWNESS);
        if (sync) {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putInt(KEY_VICTIM_ID, feed_victim);
            sync(nbt, true);
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
        if (getLevel() == 0) {
            return player.getFoodStats().getFoodLevel() / 20f; //Foodstats not synced to other clients so this is incorrect on client side
        }
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

    public void loadData(CompoundNBT nbt) {
        bloodStats.readNBT(nbt);
        eyeType = nbt.getInt(KEY_EYE);
        fangType = nbt.getInt(KEY_FANGS);
        glowingEyes = !nbt.contains(KEY_GLOWING_EYES) || nbt.getBoolean(KEY_GLOWING_EYES);
        actionHandler.loadFromNbt(nbt);
        skillHandler.loadFromNbt(nbt);
    }

    @Override
    public int getMaxLevel() {
        return REFERENCE.HIGHEST_VAMPIRE_LEVEL;
    }

    @Override
    public Predicate<LivingEntity> getNonFriendlySelector(boolean otherFactionPlayers, boolean ignoreDisguise) {
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
    public boolean isAdvancedBiter() {
        return specialAttributes.advanced_biter;
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

    @Override
    public int onBite(IVampire biter) {
        float perc = biter instanceof IVampirePlayer ? 0.2F : 0.08F;
        if (getLevel() == 0) {
            int amt = player.getFoodStats().getFoodLevel();
            int sucked = (int) Math.ceil((amt * perc));
            player.getFoodStats().setFoodLevel(amt - sucked);
            player.addExhaustion(1000F);
            if (!player.isPotionActive(ModEffects.sanguinare) && Helper.canTurnPlayer(biter, player) && Helper.canBecomeVampire(player)) {
                if (!player.isCreative()) PotionSanguinare.addRandom(player, true);
            }
            return sucked;
        }
        int amt = this.getBloodStats().getBloodLevel();
        int sucked = (int) Math.ceil((amt * perc));
        bloodStats.removeBlood(sucked, true);
        sync(this.bloodStats.writeUpdate(new CompoundNBT()), true);
        return sucked;
    }

    @Override
    public void onDeath(DamageSource src) {
        if (actionHandler.isActionActive(VampireActions.bat) && src.getImmediateSource() instanceof IProjectile) {
            if (player instanceof ServerPlayerEntity) {
                ModAdvancements.TRIGGER_VAMPIRE_ACTION.trigger((ServerPlayerEntity) player, VampireActionTrigger.Action.SNIPED_IN_BAT);
            }
        }
        actionHandler.deactivateAllActions();
        wasDead = true;
    }

    @Override
    public void onChangedDimension(DimensionType from, DimensionType to) {

    }

    @Override
    public void onJoinWorld() {
        if (getLevel() > 0) {
            actionHandler.onActionsReactivated();
            ticksInSun = 0;
            if (wasDead) {
                player.addPotionEffect(new EffectInstance(ModEffects.sunscreen, 400, 4, true, false));
                player.setHealth(player.getMaxHealth());
                bloodStats.setBloodLevel(bloodStats.getMaxBlood());
            }
        }
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
        if (getSpecialAttributes().half_invulnerable) {
            if (amt >= getRepresentingEntity().getMaxHealth() * Balance.vpa.HALFINVULNERABLE_THRESHOLD && amt < 10000) { //Make sure "instant kills" are not blocked by this
                if (useBlood(Balance.vpa.HALFINVULNERABLE_BLOOD_COSTS, false)) {
                    return true;
                } else {
                    this.actionHandler.toggleAction(VampireActions.half_invulnerable);
                }
            }
        }
        endFeeding(true);
        return false;
    }

    @Override
    public void onLevelChanged(int newLevel, int oldLevel) {
        if (!isRemote()) {
            ScoreboardUtil.updateScoreboard(player, ScoreboardUtil.VAMPIRE_LEVEL_CRITERIA, newLevel);
            LevelAttributeModifier.applyModifier(player, SharedMonsterAttributes.MOVEMENT_SPEED, "Vampire", getLevel(), Balance.vp.SPEED_LCAP, Balance.vp.SPEED_MAX_MOD, Balance.vp.SPEED_TYPE, AttributeModifier.Operation.MULTIPLY_TOTAL, false);
            LevelAttributeModifier.applyModifier(player, SharedMonsterAttributes.ATTACK_DAMAGE, "Vampire", getLevel(), Balance.vp.STRENGTH_LCAP, Balance.vp.STRENGTH_MAX_MOD, Balance.vp.STRENGTH_TYPE, AttributeModifier.Operation.MULTIPLY_TOTAL, false);
            LevelAttributeModifier.applyModifier(player, SharedMonsterAttributes.MAX_HEALTH, "Vampire", getLevel(), Balance.vp.HEALTH_LCAP, Balance.vp.HEALTH_MAX_MOD, Balance.vp.HEALTH_TYPE, AttributeModifier.Operation.ADDITION, true);
            if (player.getHealth() > player.getMaxHealth()) player.setHealth(player.getMaxHealth());
            LevelAttributeModifier.applyModifier(player, VReference.bloodExhaustion, "Vampire", getLevel(), getMaxLevel(), Balance.vp.EXHAUSTION_MAX_MOD, Balance.vp.EXHAUSTION_TYPE, AttributeModifier.Operation.MULTIPLY_TOTAL, false);
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
                if (player.isPotionActive(Effects.NIGHT_VISION)) {
                    player.removePotionEffect(Effects.NIGHT_VISION);
                }
            } else if (newLevel == 0) {
                if (player.getActivePotionEffect(Effects.NIGHT_VISION) instanceof VampireNightVisionEffect) {
                    player.removePotionEffect(Effects.NIGHT_VISION);
                }
                actionHandler.resetTimers();
            }
        }
    }

    @Override
    public void onPlayerLoggedIn() {
        if (getLevel() > 0 && !player.world.isRemote) {
            player.addPotionEffect(new EffectInstance(ModEffects.sunscreen, 200, 4, true, false));
        }
    }

    /**
     * Called when a sanguinare effect runs out.
     * DON'T add/remove potions here, since it is called while the potion effect list is modified.
     */
    public void onSanguinareFinished() {
        if (Helper.canBecomeVampire(player) && !isRemote()) {
            FactionPlayerHandler handler = FactionPlayerHandler.get(player);
            handler.joinFaction(getFaction());
            player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 300));
            player.addPotionEffect(new EffectInstance(Effects.SATURATION, 300));
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
    public void onPlayerLoggedOut() {
        endFeeding(false);
    }

    @Override
    public void onUpdate() {
        player.getEntityWorld().getProfiler().startSection("vampirism_vampirePlayer");
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
            player.setMotion(0.0D, 0.0D, 0.0D);
            ++this.sleepTimer;

            if (this.sleepTimer > 100) {
                this.sleepTimer = 100;
            }

            if (!player.getEntityWorld().isRemote) {
                BlockState state = player.getEntityWorld().getBlockState(player.getBedLocation());
                boolean bed = state.getBlock().isBed(state, player.getEntityWorld(), player.getBedLocation(), player);
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
            activatedVision.tick(this);
        }


        if (!isRemote()) {
            if (level > 0) {
                boolean sync = false;
                boolean syncToAll = false;
                CompoundNBT syncPacket = new CompoundNBT();

                if (isGettingSundamage()) {
                    handleSunDamage(false);
                } else if (ticksInSun > 0) {
                    ticksInSun--;
                }
                if (isGettingGarlicDamage() != EnumStrength.NONE) {
                    DamageHandler.affectVampireGarlicAmbient(this, isGettingGarlicDamage(), player.ticksExisted);
                }
                if (player.isAlive() && player.isInWater()) {
                    player.setAir(300);
                    if (player.ticksExisted % 16 == 4 && !getSpecialAttributes().waterResistance) {
                        player.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 80, (int) (getLevel() / (float) getMaxLevel() * 3)));
                    }
                }

                if (player.ticksExisted % 9 == 3 && player.isPotionActive(Effects.FIRE_RESISTANCE)) {
                    EffectInstance fireResistance = player.getActivePotionEffect(Effects.FIRE_RESISTANCE);
                    player.addPotionEffect(new EffectInstance(ModEffects.fire_protection, fireResistance.getDuration(), fireResistance.getAmplifier()));
                    player.removePotionEffect(Effects.FIRE_RESISTANCE);
                }
                if (player.ticksExisted % 9 == 3 && player.isPotionActive(Effects.HUNGER)) {
                    EffectInstance hunterEffect = player.getActivePotionEffect(Effects.HUNGER);
                    player.addPotionEffect(new EffectInstance(ModEffects.thirst, hunterEffect.getDuration(), hunterEffect.getAmplifier()));
                    player.removePotionEffect(Effects.HUNGER);
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

                if (feed_victim != -1 && feedBiteTickCounter++ >= 20) {
                    updateFeeding();
                    feedBiteTickCounter = 0;
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

            if (feed_victim != -1 && feedBiteTickCounter++ >= 5) {
                Entity e = VampirismMod.proxy.getMouseOverEntity();
                if (e == null || e.getEntityId() != feed_victim) {
                    VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.ENDSUCKBLOOD, ""));
                    return;
                }
                feedBiteTickCounter = 0;
            }
        }
        player.world.getProfiler().endSection();
    }

    @Override
    public void onUpdatePlayer(TickEvent.Phase phase) {
        if (phase == TickEvent.Phase.END) {
            //Update blood stats
            if (getLevel() > 0) {
                player.world.getProfiler().startSection("vampirism_bloodupdate");
                if (!player.world.isRemote && this.bloodStats.onUpdate()) {
                    sync(this.bloodStats.writeUpdate(new CompoundNBT()), false);
                }
                player.world.getProfiler().endSection();
            }
            if (getSpecialAttributes().bat) {
                BatVampireAction.updatePlayerBatSize(player);
            }
            if (sleepingInCoffin) {
                setEntitySize(0.2F, 0.2F);
            }
        }
    }

    public void saveData(CompoundNBT nbt) {
        bloodStats.writeNBT(nbt);
        nbt.putInt(KEY_EYE, eyeType);
        nbt.putInt(KEY_FANGS, fangType);
        nbt.putBoolean(KEY_GLOWING_EYES, glowingEyes);
        actionHandler.saveToNbt(nbt);
        skillHandler.saveToNbt(nbt);

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
                CompoundNBT nbt = new CompoundNBT();
                nbt.putInt(KEY_EYE, eyeType);
                sync(nbt, true);
            }
        }
        return true;
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
        //TODO 1.14 this is not going to work wait for https://github.com/MinecraftForge/MinecraftForge/pull/6059
        try {
            if (reflectionMethodSetSize == null) {
                reflectionMethodSetSize = ObfuscationReflectionHelper.findMethod(Entity.class, SRGNAMES.Entity_setSize, float.class, float.class);
            }
            reflectionMethodSetSize.invoke(player, width, height);
            return true;
        } catch (Exception e) {
            LOGGER.error("Could not change players size! ", e);
            return false;
        }
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
                CompoundNBT nbt = new CompoundNBT();
                nbt.putInt(KEY_FANGS, fangType);
                sync(nbt, true);
            }
        }
        return true;
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
                CompoundNBT nbt = new CompoundNBT();
                nbt.putBoolean(KEY_GLOWING_EYES, glowingEyes);
                sync(nbt, true);
            }
        }
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
    public Either<PlayerEntity.SleepResult, Unit> trySleep(BlockPos bedLocation) {

        if (!player.world.isRemote) {
            if (player.isSleeping() || !player.isAlive()) {
                return Either.left(PlayerEntity.SleepResult.OTHER_PROBLEM);
            }

            if (!player.world.dimension.isSurfaceWorld()) {
                return Either.left(PlayerEntity.SleepResult.NOT_POSSIBLE_HERE);
            }

            if (Math.abs(player.posX - (double) bedLocation.getX()) > 3.0D || Math.abs(player.posY - (double) bedLocation.getY()) > 2.0D || Math.abs(player.posZ - (double) bedLocation.getZ()) > 3.0D) {
                return Either.left(PlayerEntity.SleepResult.TOO_FAR_AWAY);
            }

            if (!player.isCreative()) {
                double d0 = 8.0D;
                double d1 = 5.0D;
                List<MonsterEntity> list = player.world.getEntitiesWithinAABB(MonsterEntity.class, new AxisAlignedBB((double) bedLocation.getX() - d0, (double) bedLocation.getY() - d1, (double) bedLocation.getZ() - d0, (double) bedLocation.getX() + d0, (double) bedLocation.getY() + d1, (double) bedLocation.getZ() + d0), (p_213820_1_) -> p_213820_1_.isPreventingPlayerRest(player));
                if (!list.isEmpty()) {
                    return Either.left(PlayerEntity.SleepResult.NOT_SAFE);
                }
            }
            if (!player.world.isDaytime()) {
                player.setBedPosition(bedLocation); //Set sleep location even if night time
                return Either.left(PlayerEntity.SleepResult.NOT_POSSIBLE_NOW);
            }
        }

        player.startSleeping(bedLocation); //TODO 1.14 check if this actually replaces most of the commented code below //TODO wait for PR (https://github.com/MinecraftForge/MinecraftForge/pull/6043)
        //if (!setEntitySize(0.2F, 0.2F)) return Either.left(PlayerEntity.SleepResult.OTHER_PROBLEM);


//        BlockState state = null;
//        if (player.world.isBlockLoaded(bedLocation)) state = player.world.getBlockState(bedLocation);
//        if (state != null && state.getBlock().isBed(state, player.world, bedLocation, player)) {
//            Direction enumfacing = state.getBlock().getBedDirection(state, player.world, bedLocation);
//            float f = 0.5F;
//            float f1 = 0.5F;
//
//            switch (enumfacing) {
//                case SOUTH:
//                    f1 = 0.9F;
//                    break;
//                case NORTH:
//                    f1 = 0.1F;
//                    break;
//                case WEST:
//                    f = 0.1F;
//                    break;
//                case EAST:
//                    f = 0.9F;
//                    break;
//                default://Should not happen
//            }
//            //player.setRenderOffsetForSleep(enumfacing);
//
//            player.setPosition((double) ((float) bedLocation.getX() + f), (double) ((float) bedLocation.getY() + 0.6875F), (double) ((float) bedLocation.getZ() + f1));
//        } else {
//            player.setPosition((double) ((float) bedLocation.getX() + 0.5F), (double) ((float) bedLocation.getY() + 0.6875F), (double) ((float) bedLocation.getZ() + 0.5F));
//        }

        player.abilities.isFlying = false;
        player.sendPlayerAbilities();
        sleepTimer = 0;
        sleepingInCoffin = true;
        //player.noClip = true;
        //player.setBedPosition(bedLocation);//Is also set if sleep fails due to night time. See above
        //player.setMotion(0.0D, 0.0D, 0.0D);

//        if (!player.world.isRemote) {
//            DaySleepHelper.updateAllPlayersSleeping(player.world);
//        }
//        if (player instanceof ServerPlayerEntity) {
//            ServerPlayerEntity playerMP = (ServerPlayerEntity) player;
//            IPacket<?> packet = new SPacketUseBed(player, bedLocation);
//            playerMP.getServerWorld().getChunkProvider().sendToAllTracking(playerMP, packet);
//            playerMP.connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
//            playerMP.connection.sendPacket(packet);
//        }

        return Either.right(Unit.INSTANCE);
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
    public boolean useBlood(int amt, boolean allowPartial) {
        return bloodStats.removeBlood(amt, allowPartial);
    }

    @Override
    public void wakeUpPlayer(boolean immediately, boolean updateWorldFlag, boolean setSpawn) {
        LOGGER.debug("Waking up player");
        if (this.isPlayerSleeping() && player instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity) player).getServerWorld().getChunkProvider().sendToTrackingAndSelf(player, new SAnimateHandPacket(player, 2));
        }
        player.wakeUpPlayer(immediately, false, setSpawn);
        this.sleepingInCoffin = false;
        player.noClip = true;
        if (player instanceof ServerPlayerEntity && ((ServerPlayerEntity) player).connection != null) {
            ((ServerPlayerEntity) player).connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
        }
    }

    @Override
    public boolean wantsBlood() {
        return getLevel() > 0 && bloodStats.needsBlood();
    }


    @Override
    protected VampirismPlayer copyFromPlayer(PlayerEntity old) {
        VampirePlayer oldVampire = get(old);
        CompoundNBT nbt = new CompoundNBT();
        oldVampire.saveData(nbt);
        this.loadData(nbt);
        this.wasDead = oldVampire.wasDead;
        return oldVampire;
    }

    @Override
    protected void loadUpdate(CompoundNBT nbt) {
        if (nbt.contains(KEY_EYE)) {
            setEyeType(nbt.getInt(KEY_EYE));
        }
        if (nbt.contains(KEY_FANGS)) {
            setFangType(nbt.getInt(KEY_FANGS));
        }
        if (nbt.contains(KEY_SPAWN_BITE_PARTICLE)) {
            spawnBiteParticle(nbt.getInt(KEY_SPAWN_BITE_PARTICLE));
        }
        if (nbt.contains(KEY_GLOWING_EYES)) {
            setGlowingEyes(nbt.getBoolean(KEY_GLOWING_EYES));
        }
        if (nbt.contains(KEY_VICTIM_ID)) {
            feed_victim = nbt.getInt(KEY_VICTIM_ID);
        }
        bloodStats.loadUpdate(nbt);
        actionHandler.readUpdateFromServer(nbt);
        skillHandler.readUpdateFromServer(nbt);
        if (nbt.contains(KEY_VISION)) {
            int id = nbt.getInt(KEY_VISION);
            IVampireVision vision;
            if (id == -1) {
                vision = null;
            } else {
                vision = ((GeneralRegistryImpl) VampirismAPI.vampireVisionRegistry()).getVisionOfId(id);
                if (vision == null) {
                    LOGGER.warn("Failed to find vision with id {}", id);
                }
            }
            activateVision(vision);

        }

    }

    @Override
    protected void writeFullUpdate(CompoundNBT nbt) {
        nbt.putInt(KEY_EYE, getEyeType());
        nbt.putInt(KEY_FANGS, getFangType());
        nbt.putBoolean(KEY_GLOWING_EYES, getGlowingEyes());
        nbt.putInt(KEY_VICTIM_ID, feed_victim);
        bloodStats.writeUpdate(nbt);
        actionHandler.writeUpdateForClient(nbt);
        skillHandler.writeUpdateForClient(nbt);
        nbt.putInt(KEY_VISION, activatedVision == null ? -1 : ((GeneralRegistryImpl) VampirismAPI.vampireVisionRegistry()).getIdOfVision(activatedVision));
    }

    private void applyEntityAttributes() {
        //Checking if already registered, since this method has to be called multiple times due to SpongeForge not recreating the player, but resetting the attribute map
        if (player.getAttributes().getAttributeInstance(VReference.sunDamage) == null) {
            player.getAttributes().registerAttribute(VReference.sunDamage).setBaseValue(Balance.vp.SUNDAMAGE_DAMAGE);
        }
        if (player.getAttributes().getAttributeInstance(VReference.bloodExhaustion) == null) {
            player.getAttributes().registerAttribute(VReference.bloodExhaustion).setBaseValue(Balance.vp.BLOOD_EXHAUSTION_BASIC_MOD);
        }
        if (player.getAttributes().getAttributeInstance(VReference.biteDamage) == null) {
            player.getAttributes().registerAttribute(VReference.biteDamage).setBaseValue(Balance.vp.BITE_DMG);

        }
    }

    /**
     * Executes attack logic if the bite is used against a hostile mob or a hunter
     *
     * @param entity The entity to attack
     * @param hunter Is the entity a hunter?
     */
    private void biteAttack(LivingEntity entity, boolean hunter) {
        float damage = getSpecialAttributes().bat ? 0.1F : (float) player.getAttribute(VReference.biteDamage).getValue();
        entity.attackEntityFrom(DamageSource.causePlayerDamage(player), damage);
        if ((entity.isEntityUndead() && player.getRNG().nextInt(4) == 0) || entity instanceof CreatureEntity && ExtendedCreature.get((CreatureEntity) entity).hasPoisonousBlood()) {
            player.addPotionEffect(new EffectInstance(Effects.POISON, 60));
            if (player instanceof ServerPlayerEntity) {
                ModAdvancements.TRIGGER_VAMPIRE_ACTION.trigger((ServerPlayerEntity) player, VampireActionTrigger.Action.POISONOUS_BITE);
            }
        } else if (hunter) {
            if (entity instanceof PlayerEntity && HunterCoatItem.isFullyEquipped((PlayerEntity) entity)) {
                player.attackEntityFrom(DamageSource.causeThornsDamage(entity), damage);
            }
        }

    }

    private void biteBlock(@Nonnull BlockPos pos, @Nonnull BlockState blockState, @Nullable TileEntity tileEntity) {
        if (isRemote()) return;
        if (getLevel() == 0) return;
        if (!bloodStats.needsBlood()) return;

        int need = Math.min(8, bloodStats.getMaxBlood() - bloodStats.getBloodLevel());
        if (ModBlocks.blood_container.equals(blockState.getBlock())) {
            if (tileEntity != null) {
                tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).ifPresent(handler -> {
                    int blood = 0;

                    FluidStack drainable = handler.drain(new FluidStack(ModFluids.blood, need * VReference.FOOD_TO_FLUID_BLOOD), false);
                    if (drainable != null && drainable.amount >= VReference.FOOD_TO_FLUID_BLOOD) {
                        FluidStack drained = handler.drain((drainable.amount / VReference.FOOD_TO_FLUID_BLOOD) * VReference.FOOD_TO_FLUID_BLOOD, true);
                        if (drained != null) {
                            blood = drained.amount / VReference.FOOD_TO_FLUID_BLOOD;
                        }
                    }
                    if (blood > 0) {
                        drinkBlood(blood, IBloodStats.LOW_SATURATION);

                        CompoundNBT updatePacket = bloodStats.writeUpdate(new CompoundNBT());
                        sync(updatePacket, true);


                    }
                });

            }
        }



    }

    /**
     * Bite the given entity.
     * Does NOT check reach distance
     *
     * @param entity the entity to feed on
     * @return If feeding can continue
     */
    private boolean biteFeed(LivingEntity entity) {
        if (isRemote()) return true;
        if (getLevel() == 0) return false;
        int blood = 0;
        float saturationMod = IBloodStats.HIGH_SATURATION;
        boolean continue_feeding = true;
        if (feed_victim_bite_type == BITE_TYPE.SUCK_BLOOD_CREATURE) {
            IExtendedCreatureVampirism extendedCreature = ExtendedCreature.get((CreatureEntity) entity);
            blood = extendedCreature.onBite(this);
            saturationMod = extendedCreature.getBloodSaturation();
            if (isAdvancedBiter() && extendedCreature.getBlood() == 1) {
                continue_feeding = false;
            }
        } else if (feed_victim_bite_type == BITE_TYPE.SUCK_BLOOD_PLAYER || feed_victim_bite_type == BITE_TYPE.SUCK_BLOOD_HUNTER_PLAYER) {
            blood = VampirePlayer.get((PlayerEntity) entity).onBite(this);
            saturationMod = VampirePlayer.get((PlayerEntity) entity).getBloodSaturation();
            if (feed_victim_bite_type == BITE_TYPE.SUCK_BLOOD_HUNTER_PLAYER) {
                player.addPotionEffect(new EffectInstance(Effects.POISON, 15, 2));
            }
        } else if (feed_victim_bite_type == BITE_TYPE.SUCK_BLOOD) {
            blood = ((IBiteableEntity) entity).onBite(this);
            saturationMod = ((IBiteableEntity) entity).getBloodSaturation();
        }
        if (blood > 0) {
            drinkBlood(blood, saturationMod);
            CompoundNBT updatePacket = bloodStats.writeUpdate(new CompoundNBT());
            updatePacket.putInt(KEY_SPAWN_BITE_PARTICLE, entity.getEntityId());
            sync(updatePacket, true);
            if (player instanceof ServerPlayerEntity) {
                ModAdvancements.TRIGGER_VAMPIRE_ACTION.trigger((ServerPlayerEntity) player, VampireActionTrigger.Action.SUCK_BLOOD);
            }
            return continue_feeding;
        }
        return false;
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
        EffectInstance potionEffect = player.getActivePotionEffect(ModEffects.sunscreen);
        int sunscreen = potionEffect == null ? -1 : potionEffect.getAmplifier();
        if (ticksInSun < 100) {
            ticksInSun++;
        }
        if (sunscreen >= 5 && ticksInSun > 50) {
            ticksInSun = 50;
        }
        if (isRemote || player.abilities.isCreativeMode || player.abilities.disableDamage) return;
        if (Balance.vp.SUNDAMAGE_NAUSEA && getLevel() >= Balance.vp.SUNDAMAGE_NAUSEA_MINLEVEL && player.ticksExisted % 300 == 1 && ticksInSun > 50 && sunscreen == -1) {
            player.addPotionEffect(new EffectInstance(Effects.NAUSEA, 180));
        }
        if (getLevel() >= Balance.vp.SUNDAMAGE_WEAKNESS_MINLEVEL && player.ticksExisted % 150 == 3 && sunscreen < 5) {
            player.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 152, 0));
        }
        if (getLevel() >= Balance.vp.SUNDAMAGE_MINLEVEL && ticksInSun >= 100 && player.ticksExisted % 40 == 5) {
            float damage = (float) (player.getAttribute(VReference.sunDamage).getValue());
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
            UtilLib.spawnParticles(player.world, ParticleTypes.CRIT, entity.posX, entity.posY, entity.posZ, player.posX - entity.posX, player.posY - entity.posY, player.posZ - entity.posZ, 10, 1);
        }
        for (int j = 0; j < 16; ++j) {
            Vec3d vec3 = new Vec3d((player.getRNG().nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
            vec3 = vec3.rotatePitch(-player.rotationPitch * (float) Math.PI / 180F);
            vec3 = vec3.rotateYaw(-player.rotationYaw * (float) Math.PI / 180F);
            double d0 = (double) (-player.getRNG().nextFloat()) * 0.6D - 0.3D;
            Vec3d vec31 = new Vec3d(((double) player.getRNG().nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
            vec31 = vec31.rotatePitch(-player.rotationPitch * (float) Math.PI / 180.0F);
            vec31 = vec31.rotateYaw(-player.rotationYaw * (float) Math.PI / 180.0F);
            vec31 = vec31.add(player.posX, player.posY + (double) player.getEyeHeight(), player.posZ);

            player.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, new ItemStack(Items.APPLE)), vec31.x, vec31.y, vec31.z, vec3.x, vec3.y + 0.05D, vec3.z);
        }
        //Play bite sounds. Using this method since it is the only client side method. And this is called on every relevant client anyway
        player.world.playSound(player.posX, player.posY, player.posZ, ModSounds.player_bite, SoundCategory.PLAYERS, 1.0F, 1.0F, false);
    }

    /**
     * This is called every 20 ticks in onUpdate() to run the continuous feeding effect
     */
    private void updateFeeding() {
        Entity entity = player.world.getEntityByID(feed_victim);
        if (!(entity instanceof LivingEntity)) return;
        LivingEntity e = (LivingEntity) entity;
        if (e.getHealth() == 0f) {
            endFeeding(true);
            return;
        }
        e.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 20, 7, false, false));

        player.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 25, 4, false, false));

        //ModParticles.spawnParticleClient(player.world, new FlyingBloodEntityParticleData(ModParticles.flying_blood_entity, player.getEntityId(), true), e.posX , e.posY + e.getEyeHeight()/2, e.posZ, 10, 0.1F, player.getRNG());
        ModParticles.spawnParticlesServer(player.world, new FlyingBloodEntityParticleData(ModParticles.flying_blood_entity, player.getEntityId(), true), e.posX, e.posY + e.getEyeHeight() / 2, e.posZ, 10, 0.1f, 0.1f, 0.1f, 0);

        if (!biteFeed(e)) {
            endFeeding(true);
        }

        if (!(e.getDistance(player) <= player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue() + 1) || e.getHealth() == 0f)
            endFeeding(true);
    }

    private static class Storage implements Capability.IStorage<IVampirePlayer> {
        @Override
        public void readNBT(Capability<IVampirePlayer> capability, IVampirePlayer instance, Direction side, INBT nbt) {
            ((VampirePlayer) instance).loadData((CompoundNBT) nbt);
        }

        @Override
        public INBT writeNBT(Capability<IVampirePlayer> capability, IVampirePlayer instance, Direction side) {
            CompoundNBT nbt = new CompoundNBT();
            ((VampirePlayer) instance).saveData(nbt);
            return nbt;
        }
    }
}
