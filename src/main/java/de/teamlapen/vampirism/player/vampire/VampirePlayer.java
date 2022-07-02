package de.teamlapen.vampirism.player.vampire;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.lib.util.ISoundReference;
import de.teamlapen.vampirism.REFERENCE;
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
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.effects.SanguinareEffect;
import de.teamlapen.vampirism.effects.VampireNightVisionEffectInstance;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.fluids.BloodHelper;
import de.teamlapen.vampirism.items.VampirismHunterArmor;
import de.teamlapen.vampirism.mixin.ArmorItemAccessor;
import de.teamlapen.vampirism.network.CSimpleInputEvent;
import de.teamlapen.vampirism.particle.FlyingBloodEntityParticleData;
import de.teamlapen.vampirism.player.IVampirismPlayer;
import de.teamlapen.vampirism.player.LevelAttributeModifier;
import de.teamlapen.vampirism.player.VampirismPlayer;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.player.actions.ActionHandler;
import de.teamlapen.vampirism.player.skills.SkillHandler;
import de.teamlapen.vampirism.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.player.vampire.skills.VampireSkills;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Permissions;
import de.teamlapen.vampirism.util.ScoreboardUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Main class for Vampire Players.
 */
public class VampirePlayer extends VampirismPlayer<IVampirePlayer> implements IVampirePlayer {

    public final static UUID NATURAL_ARMOR_UUID = UUID.fromString("17dcf6d2-30ac-4730-b16a-528353d0abe5");
    private static final Logger LOGGER = LogManager.getLogger(VampirePlayer.class);
    private final static int FEED_TIMER = 20;
    /**
     * Keys for NBT values
     */
    private final static String KEY_EYE = "eye_type";
    private final static String KEY_FANGS = "fang_type";
    private final static String KEY_GLOWING_EYES = "glowing_eyes";
    private final static String KEY_SPAWN_BITE_PARTICLE = "bite_particle";
    private final static String KEY_VISION = "vision";
    private final static String KEY_FEED_VICTIM_ID = "feed_victim";
    private final static String KEY_WING_COUNTER = "wing";
    private final static String KEY_DBNO_TIMER = "dbno";
    private final static String KEY_DBNO_MSG = "dbno_msg";
    private final static String KEY_WAS_DBNO = "wasDBNO";

    @CapabilityInject(IVampirePlayer.class)
    public static Capability<IVampirePlayer> CAP = getNull();

    /**
     * Always prefer #getOpt
     * Don't call before the construction event of the player entity is finished
     * Must check Entity#isAlive before
     */
    @Deprecated
    public static VampirePlayer get(@Nonnull PlayerEntity player) {
        return (VampirePlayer) player.getCapability(CAP, null).orElseThrow(() -> new IllegalStateException("Cannot get Vampire player capability from player " + player));
    }


    /**
     * Return a LazyOptional, but print a warning message if not present.
     */
    public static LazyOptional<VampirePlayer> getOpt(@Nonnull PlayerEntity player) {
        LazyOptional<VampirePlayer> opt = player.getCapability(CAP, null).cast();
        if (!opt.isPresent()) {
            LOGGER.warn("Cannot get Vampire player capability. This might break mod functionality.", new Throwable().fillInStackTrace());
        }
        return opt;
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

    public static double getNaturalArmorValue(int lvl) {
        return lvl > 0 ? VampirismConfig.BALANCE.vpNaturalArmorBaseValue.get() + (lvl / (double) REFERENCE.HIGHEST_VAMPIRE_LEVEL) * VampirismConfig.BALANCE.vpNaturalArmorIncrease.get() : 0;
    }

    public static double getNaturalArmorToughnessValue(int lvl) {
        return (lvl / (double) REFERENCE.HIGHEST_VAMPIRE_LEVEL) * VampirismConfig.BALANCE.vpNaturalArmorToughnessIncrease.get();
    }
    private final BloodStats bloodStats;
    private final ActionHandler<IVampirePlayer> actionHandler;
    private final SkillHandler<IVampirePlayer> skillHandler;
    private final List<IVampireVision> unlockedVisions = new ArrayList<>();
    private boolean sundamage_cache = false;
    private EnumStrength garlic_cache = EnumStrength.NONE;
    private int ticksInSun = 0;
    private int remainingBarkTicks = 0;
    private boolean wasDead = false;
    private IVampireVision activatedVision = null;
    private int wing_counter = 0;
    private int feed_victim = -1;
    /**
     * Holds a sound reference (client side only) for the feeding sound while feed_victim!=-1
     */
    @Nullable
    private ISoundReference feedingSoundReference;
    private BITE_TYPE feed_victim_bite_type;
    private int feedBiteTickCounter = 0;
    private boolean forceNaturalArmorUpdate;
    /**
     * >=0 if DBNO, counts downwards, if == 0, can resurrect
     */
    private int dbnoTimer = -1;
    /**
     * Only set on data load.
     * Will be active when player rejoined world after being in DBNO state.
     * Will kill player next tick (and remove invulnerable)
     */
    private boolean wasDBNO = false;
    /**
     * The original death message from the event that sent the player to DBNO state
     */
    @Nullable
    private ITextComponent dbnoMessage;

    public VampirePlayer(PlayerEntity player) {
        super(player);
        bloodStats = new BloodStats(player);
        actionHandler = new ActionHandler<>(this);
        skillHandler = new SkillHandler<>(this, VReference.VAMPIRE_FACTION);
    }

    @Override
    public void activateVision(@Nullable IVampireVision vision) {
        if (vision != null && !isRemote() && (VampirismAPI.vampireVisionRegistry()).getIdOfVision(vision) == -1) {
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
                nbt.putInt(KEY_VISION, activatedVision == null ? -1 : (VampirismAPI.vampireVisionRegistry()).getIdOfVision(activatedVision));
                this.sync(nbt, false);
            }
        }

    }

    @Override
    public void addExhaustion(float exhaustion) {
        if (!player.abilities.invulnerable && getLevel() > 0) {
            if (!isRemote()) {
                bloodStats.addExhaustion(exhaustion);
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
        double dist = player.getAttribute(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue() + 1;
        if (player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) > dist * dist) {
            LOGGER.warn("Block sent by client is not in reach" + pos);
        } else {
            biteBlock(pos, player.level.getBlockState(pos), player.level.getBlockEntity(pos));
        }
    }

    /**
     * Bite the entity with the given id.
     * Checks reach distance
     *
     * @param entityId The id of the entity to start biting
     */
    public void biteEntity(int entityId) {
        if (this.getLevel() == 0) {
            LOGGER.warn("Player can't bite. Isn't a vampire");
            return;
        }
        Entity e = player.getCommandSenderWorld().getEntity(entityId);
        if (player.isSpectator()) {
            LOGGER.warn("Player can't bite in spectator mode");
            return;
        }
        if (getActionHandler().isActionActive(VampireActions.BAT.get())) {
            LOGGER.warn("Cannot bite in bat mode");
            return;
        }
        if (e instanceof LivingEntity) {
            if (e.distanceTo(player) <= player.getAttribute(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue() + 1) {
                feed_victim_bite_type = determineBiteType((LivingEntity) e);
                if (feed_victim_bite_type == BITE_TYPE.NONE) {
                } else if (feed_victim_bite_type == BITE_TYPE.HUNTER_CREATURE) {
                    player.addEffect(new EffectInstance(ModEffects.POISON.get(), 60));
                    if (player instanceof ServerPlayerEntity) {
                        ModAdvancements.TRIGGER_VAMPIRE_ACTION.trigger((ServerPlayerEntity) player, VampireActionTrigger.Action.POISONOUS_BITE);
                    }
                } else {
                    if (feed_victim == -1) feedBiteTickCounter = 0;

                    feed_victim = e.getId();

                    ((LivingEntity) e).addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 20, 7, false, false));
                    player.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 25, 4, false, false));

                    CompoundNBT nbt = new CompoundNBT();
                    nbt.putInt(KEY_FEED_VICTIM_ID, feed_victim);
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
        EffectInstance protection = player.getEffect(ModEffects.FIRE_PROTECTION.get());
        if (protection != null) {
            int amplifier = protection.getAmplifier();
            protectionMod = amplifier >= 5 ? 0 : 1F / (2F + amplifier);
        }

        return amount * protectionMod * (float) LevelAttributeModifier.calculateModifierValue(getLevel(), getMaxLevel(), VampirismConfig.BALANCE.vpFireVulnerabilityMod.get(), 0.5);
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
        if (entity instanceof CreatureEntity && entity.isAlive()) {
            LazyOptional<IExtendedCreatureVampirism> opt = ExtendedCreature.getSafe(entity);
            if (opt.map(creature -> creature.canBeBitten(this)).orElse(false)) {
                if (opt.map(IExtendedCreatureVampirism::hasPoisonousBlood).orElse(false)) {
                    return BITE_TYPE.HUNTER_CREATURE;
                }
                return BITE_TYPE.SUCK_BLOOD_CREATURE;
            }
        } else if (entity instanceof PlayerEntity) {
            if (((PlayerEntity) entity).abilities.instabuild || !Permissions.isPvpEnabled(player)) {
                return BITE_TYPE.NONE;
            }
            if (!UtilLib.canReallySee(entity, player, false) && VampirePlayer.getOpt((PlayerEntity) entity).map(v -> v.canBeBitten(this)).orElse(false) && PermissionAPI.hasPermission(player, Permissions.FEED_PLAYER)) {
                if (!(entity.getItemBySlot(EquipmentSlotType.CHEST).getItem() instanceof VampirismHunterArmor)) {
                    return BITE_TYPE.SUCK_BLOOD_PLAYER;
                }
            } else return BITE_TYPE.NONE;
        }
        return BITE_TYPE.NONE;
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
        if (feed_victim != -1 || feed_victim_bite_type != null) {
            feed_victim = -1;
            feed_victim_bite_type = null;
            if (player.hasEffect(Effects.MOVEMENT_SLOWDOWN)) player.removeEffect(Effects.MOVEMENT_SLOWDOWN);
        }
        if (sync) {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putInt(KEY_FEED_VICTIM_ID, feed_victim);
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
            return player.getFoodData().getFoodLevel() / 20f; //Foodstats not synced to other clients so this is incorrect on client side
        }
        return bloodStats.getBloodLevel() / (float) bloodStats.getMaxBlood();
    }

    @Override
    public float getBloodSaturation() {
        return VampirismConfig.BALANCE.vpPlayerBloodSaturation.get().floatValue();
    }

    @Override
    public IBloodStats getBloodStats() {
        return bloodStats;
    }

    public int getRemainingBarkTicks() {
        return remainingBarkTicks;
    }

    public void increaseRemainingBarkTicks(int additionalTicks) {
        this.remainingBarkTicks = additionalTicks;
    }

    @Override
    public ResourceLocation getCapKey() {
        return REFERENCE.VAMPIRE_PLAYER_KEY;
    }

    public int getDbnoDuration() {
        int duration = VampirismConfig.BALANCE.vpDbnoDuration.get() * 20;
        if (this.skillHandler.isSkillEnabled(VampireSkills.DBNO_DURATION.get())) {
            duration = Math.max(1, (int) (duration * VampirismConfig.BALANCE.vsDbnoReduction.get()));
        }
        return duration;
    }

    public int getDbnoTimer() {
        return this.dbnoTimer;
    }

    @Override
    public
    @Nullable
    IFaction getDisguisedAs() {
        return isDisguised() ? getSpecialAttributes().disguisedAs : getFaction();
    }

    /**
     * @return Eyetype for rendering
     */
    public int getEyeType() {
        return getSpecialAttributes().eyeType;
    }

    /**
     * @return Fangtype for rendering
     */
    public int getFangType() {
        return getSpecialAttributes().fangType;
    }

    /**
     * @return 0-1f
     */
    public float getFeedProgress() {
        return feedBiteTickCounter / (float) FEED_TIMER;
    }

    /**
     * @return Render eyes glowing
     */
    public boolean getGlowingEyes() {
        return getSpecialAttributes().glowingEyes;
    }

    /**
     * Sets glowing eyes.
     * Also sends a sync packet if on server
     *
     * @param value
     */
    public void setGlowingEyes(boolean value) {
        if (value != this.getSpecialAttributes().glowingEyes) {
            this.getSpecialAttributes().glowingEyes = value;
            if (!isRemote()) {
                CompoundNBT nbt = new CompoundNBT();
                nbt.putBoolean(KEY_GLOWING_EYES, value);
                sync(nbt, true);
            }
        }
    }

    @Override
    public int getLevel() {
        return ((IVampirismPlayer) player).getVampAtts().vampireLevel;
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

    @Nonnull
    @Override
    public ISkillHandler<IVampirePlayer> getSkillHandler() {
        return skillHandler;
    }

    /**
     * You can use {@link VampirismPlayerAttributes#getVampSpecial()} instead if you don't have the vampire player already
     */
    @Nonnull
    public VampirePlayerSpecialAttributes getSpecialAttributes() {
        return ((IVampirismPlayer) player).getVampAtts().getVampSpecial();
    }

    @Override
    public int getTicksInSun() {
        return ticksInSun;
    }

    public int getWingCounter() {
        return this.wing_counter;
    }

    @Override
    public boolean isAdvancedBiter() {
        return getSpecialAttributes().advanced_biter;
    }

    @Override
    public boolean isAutoFillEnabled() {
        return false;
    }

    @Override
    public boolean isDBNO() {
        return this.dbnoTimer >= 0;
    }

    @Override
    public boolean isDisguised() {
        return getSpecialAttributes().disguised;
    }

    @Nonnull
    @Override
    public EnumStrength isGettingGarlicDamage(IWorld iWorld, boolean forcerefresh) {
        if (forcerefresh) {
            garlic_cache = Helper.getGarlicStrength(player, iWorld);
        }
        return garlic_cache;
    }

    @Override
    public boolean isGettingSundamage(IWorld iWorld, boolean forcerefresh) {
        if (forcerefresh) {
            sundamage_cache = Helper.gettingSundamge(player, iWorld, player.level.getProfiler()) && !ModItems.UMBRELLA.get().equals(player.getMainHandItem().getItem());
        }
        return sundamage_cache;
    }

    @Override
    public boolean isIgnoringSundamage() {
        return false;
    }

    public void loadData(CompoundNBT nbt) {
        super.loadData(nbt);
        bloodStats.readNBT(nbt);
        VampirePlayerSpecialAttributes a = getSpecialAttributes();
        a.eyeType = nbt.getInt(KEY_EYE);
        a.fangType = nbt.getInt(KEY_FANGS);
        a.glowingEyes = nbt.getBoolean(KEY_GLOWING_EYES);
        actionHandler.loadFromNbt(nbt);
        skillHandler.loadFromNbt(nbt);
        if (nbt.getBoolean(KEY_WAS_DBNO)) wasDBNO = true;
        IVampireVision vision = null;
        if (nbt.contains(KEY_VISION)) { //Must be after loading skillHandler due to night vision skill being automatically activated
            vision = (VampirismAPI.vampireVisionRegistry()).getVisionOfId(nbt.getInt(KEY_VISION));
        }
        activatedVision = vision;

    }

    @Override
    public boolean canBeInfected(IVampire vampire) {
        return !player.hasEffect(ModEffects.SANGUINARE.get()) && Helper.canTurnPlayer(vampire, player) && Helper.canBecomeVampire(player);
    }

    @Override
    public boolean tryInfect(IVampire vampire) {
        if (canBeInfected(vampire)) {
            SanguinareEffect.addRandom(player, true);
            return true;
        }
        return false;
    }

    @Override
    public int onBite(IVampire biter) {
        float perc = biter instanceof IVampirePlayer ? 0.2F : 0.08F;
        if (getLevel() == 0) {
            int amt = player.getFoodData().getFoodLevel();
            int sucked = (int) Math.ceil((amt * perc));
            player.getFoodData().setFoodLevel(amt - sucked);
            player.causeFoodExhaustion(1000F);
            return sucked;
        }
        int amt = this.getBloodStats().getBloodLevel();
        int sucked = (int) Math.ceil((amt * perc));
        bloodStats.removeBlood(sucked, true);
        sync(this.bloodStats.writeUpdate(new CompoundNBT()), true);
        return sucked;
    }

    public int removeBlood(float percentage) {
        if (getLevel() == 0) {
            int amt = player.getFoodData().getFoodLevel();
            int sucked = (int) Math.ceil((amt * percentage));
            player.getFoodData().setFoodLevel(amt - sucked);
            return sucked;
        } else {
            int amt = this.getBloodStats().getBloodLevel();
            int sucked = (int) Math.ceil((amt * percentage));
            bloodStats.removeBlood(sucked, true);
            sync(this.bloodStats.writeUpdate(new CompoundNBT()), true);
            return sucked;
        }
    }

    @Override
    public void onChangedDimension(RegistryKey<World> from, RegistryKey<World> to) {

    }

    @Override
    public boolean onDeadlyHit(DamageSource source) {
        if (getLevel() > 0 && !this.player.hasEffect(ModEffects.NEONATAL.get()) && !Helper.canKillVampires(source)) {
            this.setDBNOTimer(getDbnoDuration());
            this.player.setHealth(0.5f);
            this.player.setForcedPose(Pose.SLEEPING);
            resetNearbyTargetingMobs();
            boolean flag = player.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES);
            if (flag) {
                dbnoMessage = player.getCombatTracker().getDeathMessage();
            }
            CompoundNBT nbt = new CompoundNBT();
            nbt.putInt(KEY_DBNO_TIMER, dbnoTimer);
            if (dbnoMessage != null) nbt.putString(KEY_DBNO_MSG, ITextComponent.Serializer.toJson(dbnoMessage));
            HelperLib.sync(this, nbt, player, true);
            return true;
        }
        return false;
    }

    @Override
    public void onDeath(DamageSource src) {
        super.onDeath(src);
        if (actionHandler.isActionActive(VampireActions.BAT.get()) && src.getDirectEntity() instanceof ProjectileEntity) {
            if (player instanceof ServerPlayerEntity) {
                ModAdvancements.TRIGGER_VAMPIRE_ACTION.trigger((ServerPlayerEntity) player, VampireActionTrigger.Action.SNIPED_IN_BAT);
            }
        }
        actionHandler.deactivateAllActions();
        wasDead = true;
        this.setDBNOTimer(-1);
        dbnoMessage = null;
    }

    @Override
    public boolean onEntityAttacked(DamageSource src, float amt) {
        if (getLevel() > 0) {
            if (isDBNO() && !Helper.canKillVampires(src)) {
                if (src.getEntity() != null && src.getEntity() instanceof MobEntity && ((MobEntity) src.getEntity()).getTarget() == player) {
                    ((MobEntity) src.getEntity()).setTarget(null);
                }
                return true;
            }
            if (DamageSource.ON_FIRE.equals(src)) {
                player.hurt(VReference.VAMPIRE_ON_FIRE, calculateFireDamage(amt));
                return true;
            } else if (DamageSource.IN_FIRE.equals(src) || DamageSource.LAVA.equals(src)) {
                player.hurt(VReference.VAMPIRE_IN_FIRE, calculateFireDamage(amt));
                return true;
            }
        }
        endFeeding(true);
        if (getSpecialAttributes().half_invulnerable) {
            if (amt >= getRepresentingEntity().getMaxHealth() * (this.skillHandler.isRefinementEquipped(ModRefinements.HALF_INVULNERABLE.get()) ? VampirismConfig.BALANCE.vrHalfInvulnerableThresholdMod.get() : 1) * VampirismConfig.BALANCE.vaHalfInvulnerableThreshold.get() && amt < 999) { //Make sure "instant kills" are not blocked by this
                if (useBlood(VampirismConfig.BALANCE.vaHalfInvulnerableBloodCost.get(), false)) {
                    return true;
                } else {
                    this.actionHandler.deactivateAction(VampireActions.HALF_INVULNERABLE.get());
                }
            }
        }

        return false;
    }

    @Override
    public void onEntityKilled(LivingEntity victim, DamageSource src) {
        if (this.getSkillHandler().isRefinementEquipped(ModRefinements.RAGE_FURY.get())) {
            //No need to check if rage active, extending only has an effect when already active
            int bonus = VampirismConfig.BALANCE.vrRageFuryDurationBonus.get() * 20;
            if (victim instanceof PlayerEntity) {
                bonus *= 2;
            }
            this.getActionHandler().extendActionTimer(VampireActions.VAMPIRE_RAGE.get(), bonus);
        }
    }

    @Override
    public void onJoinWorld() {
        if (getLevel() > 0) {
            actionHandler.onActionsReactivated();
            ticksInSun = 0;
            if (wasDead) {
                player.addEffect(new EffectInstance(ModEffects.SUNSCREEN.get(), 400, 4, false, false));
                player.addEffect(new EffectInstance(ModEffects.ARMOR_REGENERATION.get(), VampirismConfig.BALANCE.vpNaturalArmorRegenDuration.get() * 20, 0, false, false));
                requestNaturalArmorUpdate();
                player.setHealth(player.getMaxHealth());
                bloodStats.setBloodLevel(bloodStats.getMaxBlood());
            }
        }
    }

    @Override
    public void onLevelChanged(int newLevel, int oldLevel) {
        this.applyEntityAttributes();
        if (!isRemote()) {
            ScoreboardUtil.updateScoreboard(player, ScoreboardUtil.VAMPIRE_LEVEL_CRITERIA, newLevel);
            applyLevelModifiersA(newLevel);
            applyLevelModifiersB(newLevel, false);
            if (player.getHealth() > player.getMaxHealth()) player.setHealth(player.getMaxHealth());
            updateNaturalArmor(newLevel);
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
            } else {
                actionHandler.resetTimers();
                skillHandler.disableAllSkills();
                this.skillHandler.resetRefinements();
            }
        } else {
            if (oldLevel == 0) {
                if (player.hasEffect(Effects.NIGHT_VISION)) {
                    player.removeEffect(Effects.NIGHT_VISION);
                }
            } else if (newLevel == 0) {
                if (player.getEffect(Effects.NIGHT_VISION) instanceof VampireNightVisionEffectInstance) {
                    player.removeEffect(Effects.NIGHT_VISION);
                }
                actionHandler.resetTimers();
                this.skillHandler.resetRefinements();
            }
        }
    }

    @Override
    public void onPlayerLoggedIn() {
        if (getLevel() > 0 && !player.level.isClientSide) {
            player.addEffect(new EffectInstance(ModEffects.SUNSCREEN.get(), 200, 4, true, false));
        }
    }

    @Override
    public void onPlayerLoggedOut() {
        endFeeding(false);
        if (this.isDBNO()) {
            this.setDBNOTimer(-1);
            this.player.hurt(DamageSource.GENERIC, 10000);
        }
    }

    /**
     * Called when a sanguinare effect runs out.
     * DON'T add/remove potions here, since it is called while the potion effect list is modified.
     */
    public void onSanguinareFinished() {
        if (Helper.canBecomeVampire(player) && !isRemote() && player.isAlive()) {
            FactionPlayerHandler.getOpt(player).ifPresent(handler -> {
                handler.joinFaction(getFaction());
                player.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 300));
                player.addEffect(new EffectInstance(Effects.SATURATION, 300));
            });
        }
    }

    @Override
    public void onUpdate() {
        World world = player.getCommandSenderWorld();
        world.getProfiler().push("vampirism_vampirePlayer");
        if (wasDBNO) {
            wasDBNO = false;
            this.player.hurt(DamageSource.GENERIC, 100000);
            return;
        } else if (this.dbnoTimer >= 0) {
            if (dbnoTimer > 0) {
                this.setDBNOTimer(dbnoTimer - 1);
                if (dbnoTimer == 0) {
                    CompoundNBT nbt = new CompoundNBT();
                    nbt.putInt(KEY_DBNO_TIMER, 0);
                    HelperLib.sync(this, nbt, player, false);
                }
            }
            player.setAirSupply(300);
            player.setDeltaMovement(0, Math.min(0, player.getDeltaMovement().y()), 0);
            player.removeAllEffects();
            return;
        }
        super.onUpdate();
        int level = getLevel();
        if (level > 0) {
            if (player.tickCount % REFERENCE.REFRESH_SUNDAMAGE_TICKS == 0) {
                isGettingSundamage(world, true);
            }
            if (player.tickCount % REFERENCE.REFRESH_GARLIC_TICKS == 0) {
                isGettingGarlicDamage(world, true);
            }
        } else {
            sundamage_cache = false;
            garlic_cache = EnumStrength.NONE;
        }
        if (activatedVision != null) {
            activatedVision.tick(this);
        }

        if (!isRemote()) {
            if (level > 0) {
                boolean sync = false;
                boolean syncToAll = false;
                CompoundNBT syncPacket = new CompoundNBT();

                if (isGettingSundamage(world)) {
                    handleSunDamage(false);
                } else if (ticksInSun > 0) {
                    ticksInSun--;
                }
                if (isGettingGarlicDamage(world) != EnumStrength.NONE) {
                    DamageHandler.affectVampireGarlicAmbient(this, isGettingGarlicDamage(world), player.tickCount);
                }
                if (player.isAlive()) {
                    player.setAirSupply(300);
                    if (player.tickCount % 16 == 4 && !getSpecialAttributes().waterResistance && !player.abilities.instabuild) {
                        if (player.isInWater()) {
                            FluidState state1 = world.getFluidState(player.blockPosition());
                            FluidState state2 = world.getFluidState(player.blockPosition().above());
                            if ((FluidTags.WATER.contains(state1.getType()) && (state1.getFlow(world, player.blockPosition()).lengthSqr() > 0)) || (FluidTags.WATER.contains(state2.getType()) && (state2.getFlow(world, player.blockPosition().above()).lengthSqr() > 0))) {
                                player.addEffect(new EffectInstance(Effects.WEAKNESS, 80, (int) (getLevel() / (float) getMaxLevel() * 3)));
                            }
                        }
                    }
                }

                if (player.tickCount % 9 == 3 && VampirismConfig.BALANCE.vpFireResistanceReplace.get() && player.hasEffect(Effects.FIRE_RESISTANCE)) {
                    EffectInstance fireResistance = player.getEffect(Effects.FIRE_RESISTANCE);
                    player.addEffect(new EffectInstance(ModEffects.FIRE_PROTECTION.get(), fireResistance.getDuration(), fireResistance.getAmplifier()));
                    player.removeEffect(Effects.FIRE_RESISTANCE);
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

                if (feed_victim != -1 && feedBiteTickCounter++ >= FEED_TIMER) {
                    updateFeeding();
                    feedBiteTickCounter = 0;
                }

                if (forceNaturalArmorUpdate || player.tickCount % 128 == 0) {
                    updateNaturalArmor(getLevel());
                    forceNaturalArmorUpdate = false;
                }

            } else {
                ticksInSun = 0;
            }
        } else {
            if (level > 0) {
                actionHandler.updateActions();
                if (isGettingSundamage(world)) {
                    handleSunDamage(true);
                } else if (ticksInSun > 0) {
                    ticksInSun--;
                }
            } else {
                ticksInSun = 0;
            }

            if (feed_victim != -1 && feedBiteTickCounter++ % 5 == 0) {
                Entity e = VampirismMod.proxy.getMouseOverEntity();
                if (e == null || e.getId() != feed_victim) {
                    VampirismMod.dispatcher.sendToServer(new CSimpleInputEvent(CSimpleInputEvent.Type.FINISH_SUCK_BLOOD));
                    feedBiteTickCounter = 0;
                    feed_victim = -1;
                    return;
                }
                if (feedBiteTickCounter >= FEED_TIMER) {
                    feedBiteTickCounter = 0;
                }
            }
        }
        if (feed_victim == -1) {
            feedBiteTickCounter = 0;
        }
        if (wing_counter > 0) {
            --wing_counter;
        }
        if (remainingBarkTicks > 0) {
            --remainingBarkTicks;
        }
        world.getProfiler().pop();
    }

    @Override
    public void onUpdatePlayer(TickEvent.Phase phase) {
        if (phase == TickEvent.Phase.END) {
            //update sleeping pose
            if (getLevel() > 0) {
                VampirismMod.proxy.handleSleepClient(player);
            }

            //Update blood stats
            if (getLevel() > 0 && !isDBNO()) {
                player.level.getProfiler().push("vampirism_bloodupdate");
                if (!player.level.isClientSide && this.bloodStats.onUpdate()) {
                    sync(this.bloodStats.writeUpdate(new CompoundNBT()), false);
                }
                player.level.getProfiler().pop();
            }
        }
    }

    /**
     * Request an update to the player natural armor next tick
     */
    public void requestNaturalArmorUpdate() {
        this.forceNaturalArmorUpdate = true;
    }

    public void saveData(CompoundNBT nbt) {
        super.saveData(nbt);
        bloodStats.writeNBT(nbt);
        nbt.putInt(KEY_EYE, getEyeType());
        nbt.putInt(KEY_FANGS, getFangType());
        nbt.putBoolean(KEY_GLOWING_EYES, getGlowingEyes());
        actionHandler.saveToNbt(nbt);
        skillHandler.saveToNbt(nbt);
        if (activatedVision != null)
            nbt.putInt(KEY_VISION, (VampirismAPI.vampireVisionRegistry()).getIdOfVision(activatedVision));
        if (isDBNO()) nbt.putBoolean(KEY_WAS_DBNO, true);
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
        if (eyeType != this.getEyeType()) {
            getSpecialAttributes().eyeType = eyeType;
            if (!isRemote()) {
                CompoundNBT nbt = new CompoundNBT();
                nbt.putInt(KEY_EYE, eyeType);
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
        if (fangType != this.getFangType()) {
            this.getSpecialAttributes().fangType = fangType;
            if (!isRemote()) {
                CompoundNBT nbt = new CompoundNBT();
                nbt.putInt(KEY_FANGS, fangType);
                sync(nbt, true);
            }
        }
        return true;
    }

    public void setSkinData(int... data) {
        if (data.length > 0) {
            this.setFangType(data[0]);
            if (data.length > 1) {
                this.setEyeType(data[1]);
                if (data.length > 2) {
                    this.setGlowingEyes(data[2] > 0);
                    if (data.length > 3) {
                        FactionPlayerHandler.getOpt(this.player).ifPresent(p -> p.setTitleGender(data[3] > 0));
                    }
                }
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

    public void triggerWings() {
        this.wing_counter = 1200;
        this.sync(true);
    }

    public void tryResurrect() {
        if (this.getDbnoTimer() == 0) {
            this.setDBNOTimer(-1);
            this.dbnoMessage = null;
            this.player.setHealth(Math.max(0.5f, bloodStats.getBloodLevel() - 1));
            this.bloodStats.removeBlood(bloodStats.getBloodLevel() - 1, true);
            this.player.setForcedPose(null);
            this.player.refreshDimensions();
            this.sync(true);
            int duration = VampirismConfig.BALANCE.vpNeonatalDuration.get() * 20;
            if (this.skillHandler.isSkillEnabled(VampireSkills.NEONATAL_DECREASE.get())) {
                duration = Math.max(1, (int) (duration * VampirismConfig.BALANCE.vsNeonatalReduction.get()));
            }
            this.player.addEffect(new EffectInstance(ModEffects.NEONATAL.get(), duration));
        } else {
            if (this.isRemote()) {
                this.setDBNOTimer(-1);
            } else {
                //If client thinks it is alive again, tell it to die again
                this.sync(false);
            }
        }
    }

    public void giveUpDBNO(){
        if (this.isDBNO()) {
            //Reset dbno state before killing the player in case something is canceling the death event
            this.setDBNOTimer(-1);
            this.dbnoMessage = null;
            this.player.setForcedPose(null);
            this.player.refreshDimensions();
            this.sync(true);
            this.player.hurt(DamageSource.GENERIC, 10000);
        }
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
        if ((VampirismAPI.vampireVisionRegistry()).getIdOfVision(vision) == -1) {
            throw new IllegalArgumentException("You have to register the vision first: " + vision);
        }
        unlockedVisions.add(vision);
    }

    public void updateNaturalArmor(int lvl) {
        ModifiableAttributeInstance armorAtt = player.getAttribute(Attributes.ARMOR);
        ModifiableAttributeInstance toughnessAtt = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if (armorAtt != null && toughnessAtt != null) {
            if (lvl == 0) {
                armorAtt.removeModifier(NATURAL_ARMOR_UUID);
                toughnessAtt.removeModifier(NATURAL_ARMOR_UUID);
            } else {
                AttributeModifier modArmor = armorAtt.getModifier(NATURAL_ARMOR_UUID);
                AttributeModifier modToughness = toughnessAtt.getModifier(NATURAL_ARMOR_UUID);
                double naturalArmor = getNaturalArmorValue(lvl);
                EffectInstance armorRegen = player.getEffect(ModEffects.ARMOR_REGENERATION.get());
                double armorRegenerationMod = armorRegen == null ? 0 : armorRegen.getDuration() / ((double) VampirismConfig.BALANCE.vpNaturalArmorRegenDuration.get() * 20);
                naturalArmor *= (1 - 0.75 * armorRegenerationMod); //Modify natural armor between 25% and 100% depending on the armor regen state
                double naturalToughness = getNaturalArmorToughnessValue(lvl);
                List<UUID> armorItemModifiers = Arrays.asList(ArmorItemAccessor.getModifierUUID_vampirism());
                double baseArmor = armorAtt.getModifiers(AttributeModifier.Operation.ADDITION).stream().filter(m -> armorItemModifiers.contains(m.getId())).map(AttributeModifier::getAmount).mapToDouble(Double::doubleValue).sum();
                double baseToughness = toughnessAtt.getModifiers(AttributeModifier.Operation.ADDITION).stream().filter(m -> armorItemModifiers.contains(m.getId())).map(AttributeModifier::getAmount).mapToDouble(Double::doubleValue).sum();
                double targetArmor = Math.max(0, naturalArmor - baseArmor);
                double targetToughness = Math.max(0, naturalToughness - baseToughness);
                if (modArmor != null && targetArmor != modArmor.getAmount()) {
                    armorAtt.removeModifier(modArmor);
                    modArmor = null;
                }
                if (targetArmor != 0 && modArmor == null) {
                    armorAtt.addTransientModifier(new AttributeModifier(NATURAL_ARMOR_UUID, "Natural Vampire Armor", targetArmor, AttributeModifier.Operation.ADDITION));
                }
                if (modToughness != null && targetToughness != modToughness.getAmount()) {
                    toughnessAtt.removeModifier(modToughness);
                    modToughness = null;
                }
                if (targetToughness != 0 && modToughness == null) {
                    toughnessAtt.addTransientModifier(new AttributeModifier(NATURAL_ARMOR_UUID, "Natural Vampire Armor Toughness", targetToughness, AttributeModifier.Operation.ADDITION));
                }
                applyLevelModifiersB(lvl, VampirismConfig.BALANCE.vpArmorPenalty.get() && baseArmor > 7);

            }
        }
    }

    @Override
    public boolean useBlood(int amt, boolean allowPartial) {
        return bloodStats.removeBlood(amt, allowPartial);
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
        super.loadUpdate(nbt);
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
        if (nbt.contains(KEY_FEED_VICTIM_ID)) {
            feed_victim = nbt.getInt(KEY_FEED_VICTIM_ID);
            if(feed_victim != -1){
                if(feedingSoundReference == null || !feedingSoundReference.isPlaying()){
                    feedingSoundReference = VampLib.proxy.createSoundReference(ModSounds.PLAYER_FEEDING.get(), SoundCategory.PLAYERS, player.getX(), player.getY(), player.getZ(),1,1);
                    feedingSoundReference.startPlaying();
                }
            }
            else{
                if(feedingSoundReference != null){
                    feedingSoundReference.stopPlaying();
                    feedingSoundReference = null;
                }
            }
        }
        if (nbt.contains(KEY_WING_COUNTER)) {
            wing_counter = nbt.getInt(KEY_WING_COUNTER);
        }
        if (nbt.contains(KEY_DBNO_MSG)) {
            dbnoMessage = ITextComponent.Serializer.fromJson(nbt.getString(KEY_DBNO_MSG));
        }
        if (nbt.contains(KEY_DBNO_TIMER)) {
            boolean wasDBNOClient = isDBNO();
            setDBNOTimer(nbt.getInt(KEY_DBNO_TIMER));
            if (!wasDBNOClient && isDBNO()) {
                VampirismMod.proxy.showDBNOScreen(player, dbnoMessage);
                player.setForcedPose(Pose.SLEEPING);
                player.refreshDimensions();
            } else if (wasDBNOClient && !isDBNO()) {
                player.setForcedPose(null);
                player.refreshDimensions();
            }
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
                vision = (VampirismAPI.vampireVisionRegistry()).getVisionOfId(id);
                if (vision == null) {
                    LOGGER.warn("Failed to find vision with id {}", id);
                }
            }
            activateVision(vision);
        }
    }

    @Override
    protected void writeFullUpdate(CompoundNBT nbt) {
        super.writeFullUpdate(nbt);
        nbt.putInt(KEY_EYE, getEyeType());
        nbt.putInt(KEY_FANGS, getFangType());
        nbt.putBoolean(KEY_GLOWING_EYES, getGlowingEyes());
        nbt.putInt(KEY_FEED_VICTIM_ID, feed_victim);
        nbt.putInt(KEY_WING_COUNTER, wing_counter);
        bloodStats.writeUpdate(nbt);
        actionHandler.writeUpdateForClient(nbt);
        skillHandler.writeUpdateForClient(nbt);
        nbt.putInt(KEY_VISION, activatedVision == null ? -1 : (VampirismAPI.vampireVisionRegistry()).getIdOfVision(activatedVision));
        nbt.putInt(KEY_DBNO_TIMER, getDbnoTimer());
        if (dbnoMessage != null) nbt.putString(KEY_DBNO_MSG, ITextComponent.Serializer.toJson(dbnoMessage));
    }

    private void applyEntityAttributes() {
        player.getAttribute(ModAttributes.SUNDAMAGE.get()).setBaseValue(VampirismConfig.BALANCE.vpSundamage.get());
        player.getAttribute(ModAttributes.BLOOD_EXHAUSTION.get()).setBaseValue(VampirismConfig.BALANCE.vpBasicBloodExhaustionMod.get());
        player.getAttribute(ModAttributes.BITE_DAMAGE.get()).setBaseValue(0);
    }

    /**
     * Apply the armor unaffected level scaled entity attribute modifiers
     */
    private void applyLevelModifiersA(int level) {
        LevelAttributeModifier.applyModifier(player, Attributes.MAX_HEALTH, "Vampire", level, getMaxLevel(), VampirismConfig.BALANCE.vpHealthMaxMod.get(), 0.5, AttributeModifier.Operation.ADDITION, true);
        LevelAttributeModifier.applyModifier(player, ModAttributes.BLOOD_EXHAUSTION.get(), "Vampire", level, getMaxLevel(), VampirismConfig.BALANCE.vpExhaustionMaxMod.get(), 0.5, AttributeModifier.Operation.ADDITION, false);
    }

    /**
     * Apply the armor affected level scaled entity attribute modifiers
     */
    private void applyLevelModifiersB(int level, boolean heavyArmor) {
        LevelAttributeModifier.applyModifier(player, Attributes.MOVEMENT_SPEED, "Vampire", level, getMaxLevel(), VampirismConfig.BALANCE.vpSpeedMaxMod.get() * (heavyArmor ? 0.5f : 1), 0.5, AttributeModifier.Operation.MULTIPLY_BASE, false);
        LevelAttributeModifier.applyModifier(player, Attributes.ATTACK_SPEED, "Vampire", level, getMaxLevel(), VampirismConfig.BALANCE.vpAttackSpeedMaxMod.get() * (heavyArmor ? 0.5f : 1), 0.5, AttributeModifier.Operation.MULTIPLY_BASE, false);
    }

    private void biteBlock(@Nonnull BlockPos pos, @Nonnull BlockState blockState, @Nullable TileEntity tileEntity) {
        if (isRemote()) return;
        if (getLevel() == 0) return;
        if (!bloodStats.needsBlood()) return;

        int need = Math.min(8, bloodStats.getMaxBlood() - bloodStats.getBloodLevel());
        if (ModBlocks.BLOOD_CONTAINER.get().equals(blockState.getBlock())) {
            if (tileEntity != null) {
                tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).ifPresent(handler -> {
                    int blood = 0;

                    FluidStack drainable = handler.drain(new FluidStack(ModFluids.BLOOD.get(), need * VReference.FOOD_TO_FLUID_BLOOD), IFluidHandler.FluidAction.SIMULATE);
                    if (drainable.getAmount() >= VReference.FOOD_TO_FLUID_BLOOD) {
                        FluidStack drained = handler.drain((drainable.getAmount() / VReference.FOOD_TO_FLUID_BLOOD) * VReference.FOOD_TO_FLUID_BLOOD, IFluidHandler.FluidAction.EXECUTE);
                        if (!drained.isEmpty()) {
                            blood = drained.getAmount() / VReference.FOOD_TO_FLUID_BLOOD;
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
        if (feed_victim_bite_type == BITE_TYPE.SUCK_BLOOD_CREATURE && entity.isAlive()) {
            LazyOptional<IExtendedCreatureVampirism> opt = ExtendedCreature.getSafe(entity);
            blood = opt.map(creature -> creature.onBite(this)).orElse(0);
            saturationMod = opt.map(IBiteableEntity::getBloodSaturation).orElse(0f);
            if (isAdvancedBiter() && opt.map(IExtendedCreatureVampirism::getBlood).orElse(0) == 1) {
                continue_feeding = false;
            }
        } else if (feed_victim_bite_type == BITE_TYPE.SUCK_BLOOD_PLAYER) {
            blood = VampirePlayer.getOpt((PlayerEntity) entity).map(v -> v.onBite(this)).orElse(0);
            saturationMod = VampirePlayer.getOpt((PlayerEntity) entity).map(VampirePlayer::getBloodSaturation).orElse(0f);
        } else if (feed_victim_bite_type == BITE_TYPE.SUCK_BLOOD) {
            blood = ((IBiteableEntity) entity).onBite(this);
            saturationMod = ((IBiteableEntity) entity).getBloodSaturation();
        }
        if (blood > 0) {
            drinkBlood(blood, saturationMod);
            CompoundNBT updatePacket = bloodStats.writeUpdate(new CompoundNBT());
            updatePacket.putInt(KEY_SPAWN_BITE_PARTICLE, entity.getId());
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
        EffectInstance potionEffect = player.getEffect(ModEffects.SUNSCREEN.get());
        int sunscreen = potionEffect == null ? -1 : potionEffect.getAmplifier();
        if (ticksInSun < 100) {
            ticksInSun++;
        }
        if (ticksInSun > 50 && (sunscreen >= 4 || (VampirismConfig.BALANCE.vpSunscreenBuff.get() && sunscreen>=0)) ) {
            ticksInSun = 50;
        }
        if (!player.isAlive() || isRemote || player.abilities.instabuild || player.abilities.invulnerable) return;

        if (ticksInSun == 100 && VampirismConfig.BALANCE.vpSundamageInstantDeath.get()) {
            player.hurt(VReference.SUNDAMAGE, 1000);
            turnToAsh();
        }

        if (VampirismConfig.BALANCE.vpSundamageNausea.get() && getLevel() >= VampirismConfig.BALANCE.vpSundamageNauseaMinLevel.get() && player.tickCount % 300 == 1 && ticksInSun > 50 && sunscreen == -1) {
            player.addEffect(new EffectInstance(Effects.CONFUSION, 180));
        }
        if (getLevel() >= VampirismConfig.BALANCE.vpSundamageWeaknessMinLevel.get() && player.tickCount % 150 == 3 && sunscreen < 5) {
            player.addEffect(new EffectInstance(Effects.WEAKNESS, 152, 0));
        }
        if (getLevel() >= VampirismConfig.BALANCE.vpSundamageMinLevel.get() && ticksInSun >= 100 && player.tickCount % 40 == 5) {
            float damage = (float) (player.getAttribute(ModAttributes.SUNDAMAGE.get()).getValue());
            if (damage > 0) player.hurt(VReference.SUNDAMAGE, damage);
            if (!player.isAlive()) {
                turnToAsh(); //Instead of the normal dying animation, just turn to ash
            }
        }
    }

    /**
     * Spawn ash particles and remove body.
     * Must be dead already
     */
    private void turnToAsh() {
        if (!player.isAlive()) {
            player.deathTime = 19;
            ModParticles.spawnParticlesServer(player.level, ParticleTypes.WHITE_ASH, player.getX() + 0.5, player.getY() + player.getBbHeight(), player.getZ() + 0.5f, 20, 0.2, player.getBbHeight() * 0.2d, 0.2, 0.1);
            ModParticles.spawnParticlesServer(player.level, ParticleTypes.ASH, player.getX() + 0.5, player.getY() + player.getBbHeight() / 2, player.getZ() + 0.5f, 20, 0.2, player.getBbHeight() * 0.2d, 0.2, 0.1);
        }
    }

    /**
     * Make sure no nearby mob continues targets the player
     */
    private void resetNearbyTargetingMobs() {
        AxisAlignedBB axisalignedbb = (new AxisAlignedBB(player.blockPosition())).inflate(32.0D, 10.0D, 32.0D);
        player.level.getLoadedEntitiesOfClass(MobEntity.class, axisalignedbb).forEach(e -> {
            if (e.getTarget() == player) {
                e.targetSelector.getRunningGoals().filter(g -> g.getGoal() instanceof TargetGoal).forEach(PrioritizedGoal::stop);
            }
            if (e instanceof IAngerable) {
                ((IAngerable) e).playerDied(player);
            }
        });
    }

    private void setDBNOTimer(int newValue) {
        this.dbnoTimer = newValue;
        this.getSpecialAttributes().isDBNO = isDBNO();
    }

    /**
     * Spawn particle after biting an entity
     *
     * @param entityId Id of the entity
     */
    private void spawnBiteParticle(int entityId) {
        Entity entity = player.level.getEntity(entityId);
        if (entity != null) {
            UtilLib.spawnParticles(player.level, ParticleTypes.CRIT, entity.getX(), entity.getY(), entity.getZ(), player.getX() - entity.getX(), player.getY() - entity.getY(), player.getZ() - entity.getZ(), 10, 1);
        }
        for (int j = 0; j < 16; ++j) {
            Vector3d vec3 = new Vector3d((player.getRandom().nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
            vec3 = vec3.xRot(-player.xRot * (float) Math.PI / 180F);
            vec3 = vec3.yRot(-player.yRot * (float) Math.PI / 180F);
            double d0 = (double) (-player.getRandom().nextFloat()) * 0.6D - 0.3D;
            Vector3d vec31 = new Vector3d(((double) player.getRandom().nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
            vec31 = vec31.xRot(-player.xRot * (float) Math.PI / 180.0F);
            vec31 = vec31.yRot(-player.yRot * (float) Math.PI / 180.0F);
            vec31 = vec31.add(player.getX(), player.getY() + (double) player.getEyeHeight(), player.getZ());

            player.level.addParticle(new ItemParticleData(ParticleTypes.ITEM, new ItemStack(Items.APPLE)), vec31.x, vec31.y, vec31.z, vec3.x, vec3.y + 0.05D, vec3.z);
        }
    }

    /**
     * This is called every 20 ticks in onUpdate() to run the continuous feeding effect
     */
    private void updateFeeding() {
        Entity entity = player.level.getEntity(feed_victim);
        if (!(entity instanceof LivingEntity)) return;
        LivingEntity e = (LivingEntity) entity;
        if (e.getHealth() == 0f) {
            endFeeding(true);
            return;
        }
        e.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 20, 7, false, false));
        player.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 25, 4, false, false));

        ModParticles.spawnParticlesServer(player.level, new FlyingBloodEntityParticleData(ModParticles.FLYING_BLOOD_ENTITY.get(), player.getId(), true), e.getX(), e.getY() + e.getEyeHeight() / 2, e.getZ(), 10, 0.1f, 0.1f, 0.1f, 0);

        if (!biteFeed(e)) {
            endFeeding(true);
        }

        if (!(e.distanceTo(player) <= player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue() + 1) || e.getHealth() == 0f)
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
