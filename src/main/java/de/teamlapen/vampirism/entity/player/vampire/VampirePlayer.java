package de.teamlapen.vampirism.entity.player.vampire;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.lib.util.ISoundReference;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.advancements.critereon.VampireActionCriterionTrigger;
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
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.entity.player.FactionBasePlayer;
import de.teamlapen.vampirism.entity.player.IVampirismPlayer;
import de.teamlapen.vampirism.entity.player.LevelAttributeModifier;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.entity.player.actions.ActionHandler;
import de.teamlapen.vampirism.entity.player.skills.SkillHandler;
import de.teamlapen.vampirism.entity.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.entity.player.vampire.skills.VampireSkills;
import de.teamlapen.vampirism.fluids.BloodHelper;
import de.teamlapen.vampirism.items.VampirismHunterArmorItem;
import de.teamlapen.vampirism.mixin.ArmorItemAccessor;
import de.teamlapen.vampirism.modcompat.PlayerReviveHelper;
import de.teamlapen.vampirism.network.ServerboundSimpleInputEvent;
import de.teamlapen.vampirism.particle.FlyingBloodEntityParticleOptions;
import de.teamlapen.vampirism.util.DamageHandler;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Permissions;
import de.teamlapen.vampirism.util.ScoreboardUtil;
import de.teamlapen.vampirism.world.MinionWorldData;
import de.teamlapen.vampirism.world.ModDamageSources;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

/**
 * Main class for Vampire Players.
 */
public class VampirePlayer extends FactionBasePlayer<IVampirePlayer> implements IVampirePlayer {

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
    private final static String KEY_DBNO_TIMER = "dbno";
    private final static String KEY_DBNO_MSG = "dbno_msg";
    private final static String KEY_WAS_DBNO = "wasDBNO";

    public static final Capability<IVampirePlayer> CAP = CapabilityManager.get(new CapabilityToken<>() {
    });

    /**
     * Don't call before the construction event of the player entity is finished
     * Must check Entity#isAlive before
     * <br>
     * Always prefer using #getOpt instead
     */
    @Deprecated
    public static @NotNull VampirePlayer get(@NotNull Player player) {
        return (VampirePlayer) player.getCapability(CAP).orElseThrow(() -> new IllegalStateException("Cannot get Vampire player capability from player " + player));
    }


    /**
     * Return a LazyOptional, but print a warning message if not present.
     */
    public static @NotNull LazyOptional<VampirePlayer> getOpt(@NotNull Player player) {
        LazyOptional<VampirePlayer> opt = player.getCapability(CAP).cast();
        if (!opt.isPresent()) {
            LOGGER.warn("Cannot get Vampire player capability. This might break mod functionality.", new Throwable().fillInStackTrace());
        }
        return opt;
    }

    public static @NotNull ICapabilityProvider createNewCapability(final Player player) {
        return new ICapabilitySerializable<CompoundTag>() {

            final VampirePlayer inst = new VampirePlayer(player);
            final LazyOptional<IVampirePlayer> opt = LazyOptional.of(() -> inst);

            @Override
            public void deserializeNBT(@NotNull CompoundTag nbt) {
                inst.loadData(nbt);
            }

            @NotNull
            @Override
            public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction facing) {
                return CAP.orEmpty(capability, opt);
            }

            @Override
            public @NotNull CompoundTag serializeNBT() {
                CompoundTag tag = new CompoundTag();
                inst.saveData(tag);
                return tag;
            }
        };
    }

    public static double getNaturalArmorValue(int lvl) {
        return lvl > 0 ? VampirismConfig.BALANCE.vpNaturalArmorBaseValue.get() + (lvl / (double) REFERENCE.HIGHEST_VAMPIRE_LEVEL) * VampirismConfig.BALANCE.vpNaturalArmorIncrease.get() : 0;
    }

    public static double getNaturalArmorToughnessValue(int lvl) {
        return (lvl / (double) REFERENCE.HIGHEST_VAMPIRE_LEVEL) * VampirismConfig.BALANCE.vpNaturalArmorToughnessIncrease.get();
    }

    private final @NotNull BloodStats bloodStats;
    private final @NotNull ActionHandler<IVampirePlayer> actionHandler;
    private final @NotNull SkillHandler<IVampirePlayer> skillHandler;
    private boolean sundamage_cache = false;
    private @NotNull EnumStrength garlic_cache = EnumStrength.NONE;
    private int ticksInSun = 0;
    private int remainingBarkTicks = 0;
    private boolean wasDead = false;
    private final @NotNull VisionStatus vision = new VisionStatus();
    private int feed_victim = -1;
    /**
     * Holds a sound reference (client side only) for the feeding sound while feed_victim!=-1
     */
    @Nullable
    private ISoundReference feedingSoundReference;
    private @Nullable BITE_TYPE feed_victim_bite_type;
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
    private Component dbnoMessage;

    public VampirePlayer(Player player) {
        super(player);
        bloodStats = new BloodStats(player);
        actionHandler = new ActionHandler<>(this);
        skillHandler = new SkillHandler<>(this, VReference.VAMPIRE_FACTION);
    }

    @Override
    public void activateVision(@Nullable IVampireVision vision) {
        if (vision != null && !isRemote()) {
            VampirismAPI.vampireVisionRegistry().getVisionId(vision);
        }
        this.vision.activate(vision);

    }

    @Override
    public void addExhaustion(float exhaustion) {
        if (!player.getAbilities().invulnerable && getLevel() > 0) {
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
    public void biteBlock(@NotNull BlockPos pos) {
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
                switch (feed_victim_bite_type) {
                    case HUNTER_CREATURE:
                        player.addEffect(new MobEffectInstance(ModEffects.POISON.get(), 60));
                        if (player instanceof ServerPlayer) {
                            ModAdvancements.TRIGGER_VAMPIRE_ACTION.trigger((ServerPlayer) player, VampireActionCriterionTrigger.Action.POISONOUS_BITE);
                        }
                        break;
                    case NONE:
                        break;
                    default:
                        if (feed_victim == -1) feedBiteTickCounter = 0;

                        feed_victim = e.getId();

                        ((LivingEntity) e).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 7, false, false));
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 25, 4, false, false));

                        CompoundTag nbt = new CompoundTag();
                        nbt.putInt(KEY_FEED_VICTIM_ID, feed_victim);
                        sync(nbt, true);
                        break;
                }
            } else {
                LOGGER.warn("Entity sent by client is not in reach " + entityId);
            }
        }
    }

    @Override
    public float calculateFireDamage(float amount) {
        float protectionMod = 1F;
        MobEffectInstance protection = player.getEffect(ModEffects.FIRE_PROTECTION.get());
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

    @NotNull
    public BITE_TYPE determineBiteType(LivingEntity entity) {
        if (player instanceof ServerPlayer && !PermissionAPI.getPermission((ServerPlayer) player, Permissions.FEED)) {
            return BITE_TYPE.NONE;
        }
        if (entity instanceof IBiteableEntity) {
            if (((IBiteableEntity) entity).canBeBitten(this)) return BITE_TYPE.SUCK_BLOOD;
        }
        if (entity instanceof PathfinderMob && entity.isAlive()) {
            LazyOptional<IExtendedCreatureVampirism> opt = ExtendedCreature.getSafe(entity);
            if (opt.map(creature -> creature.canBeBitten(this)).orElse(false)) {
                if (opt.map(IExtendedCreatureVampirism::hasPoisonousBlood).orElse(false)) {
                    return BITE_TYPE.HUNTER_CREATURE;
                }
                return BITE_TYPE.SUCK_BLOOD_CREATURE;
            }
        } else if (entity instanceof Player) {
            if (((Player) entity).getAbilities().instabuild || !Permissions.isPvpEnabled(player)) {
                return BITE_TYPE.NONE;
            }
            if (!UtilLib.canReallySee(entity, player, false) && VampirePlayer.getOpt((Player) entity).map(v -> v.canBeBitten(this)).orElse(false) && (!(player instanceof ServerPlayer) || PermissionAPI.<Boolean>getPermission((ServerPlayer) player, Permissions.FEED_PLAYER))) {
                if (!(entity.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof VampirismHunterArmorItem)) {
                    return BITE_TYPE.SUCK_BLOOD_PLAYER;
                }
            } else {
                return BITE_TYPE.NONE;
            }
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
            if (player.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        }
        if (sync) {
            CompoundTag nbt = new CompoundTag();
            nbt.putInt(KEY_FEED_VICTIM_ID, feed_victim);
            sync(nbt, true);
        }
    }

    @NotNull
    @Override
    public IActionHandler<IVampirePlayer> getActionHandler() {
        return actionHandler;
    }

    @Nullable
    @Override
    public IVampireVision getActiveVision() {
        return this.vision.vision;
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

    @NotNull
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
    public @NotNull ResourceLocation getCapKey() {
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
    @Nullable
    public IFaction<?> getDisguisedAs() {
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
     */
    public void setGlowingEyes(boolean value) {
        if (value != this.getSpecialAttributes().glowingEyes) {
            this.getSpecialAttributes().glowingEyes = value;
            if (!isRemote()) {
                CompoundTag nbt = new CompoundTag();
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

    @NotNull
    @Override
    public ISkillHandler<IVampirePlayer> getSkillHandler() {
        return skillHandler;
    }

    /**
     * You can use {@link VampirismPlayerAttributes#getVampSpecial()} instead if you don't have the vampire player already
     */
    @NotNull
    public VampirePlayerSpecialAttributes getSpecialAttributes() {
        return ((IVampirismPlayer) player).getVampAtts().getVampSpecial();
    }

    @Override
    public int getTicksInSun() {
        return ticksInSun;
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

    @NotNull
    @Override
    public EnumStrength isGettingGarlicDamage(LevelAccessor iWorld, boolean forcerefresh) {
        if (forcerefresh) {
            garlic_cache = Helper.getGarlicStrength(player, iWorld);
        }
        return garlic_cache;
    }

    @Override
    public boolean isGettingSundamage(LevelAccessor iWorld, boolean forcerefresh) {
        if (forcerefresh) {
            sundamage_cache = Helper.gettingSundamge(player, iWorld, player.level.getProfiler()) && ModItems.UMBRELLA.get() != player.getMainHandItem().getItem();
        }
        return sundamage_cache;
    }

    @Override
    public boolean isIgnoringSundamage() {
        return false;
    }

    public void loadData(@NotNull CompoundTag nbt) {
        super.loadData(nbt);
        bloodStats.readNBT(nbt);
        VampirePlayerSpecialAttributes a = getSpecialAttributes();
        a.eyeType = nbt.getInt(KEY_EYE);
        a.fangType = nbt.getInt(KEY_FANGS);
        a.glowingEyes = nbt.getBoolean(KEY_GLOWING_EYES);
        actionHandler.loadFromNbt(nbt);
        skillHandler.loadFromNbt(nbt);
        if (nbt.getBoolean(KEY_WAS_DBNO)) wasDBNO = true;
        if (nbt.contains(KEY_VISION, CompoundTag.TAG_COMPOUND)) { //Must be after loading skillHandler due to night vision skill being automatically activated
            this.vision.readTag(nbt.getCompound(KEY_VISION));
        }
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
        sync(this.bloodStats.writeUpdate(new CompoundTag()), true);
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
            sync(this.bloodStats.writeUpdate(new CompoundTag()), true);
            return sucked;
        }
    }

    @Override
    public void onChangedDimension(ResourceKey<Level> from, ResourceKey<Level> to) {

    }

    @Override
    public boolean onDeadlyHit(@NotNull DamageSource source) {
        if (getLevel() > 0 && !this.player.hasEffect(ModEffects.NEONATAL.get()) && !Helper.canKillVampires(source)) {
            int timePreviouslySpentInPlayerRevive = PlayerReviveHelper.getPreviousDownTime(this.player);
            int dbnoTime = Math.max(1, getDbnoDuration()-timePreviouslySpentInPlayerRevive);
            this.setDBNOTimer(dbnoTime);
            this.player.setHealth(0.5f);
            this.player.setForcedPose(Pose.SLEEPING);
            resetNearbyTargetingMobs();
            boolean flag = player.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES);
            if (flag) {
                dbnoMessage = player.getCombatTracker().getDeathMessage();
            }
            CompoundTag nbt = new CompoundTag();
            nbt.putInt(KEY_DBNO_TIMER, dbnoTimer);
            if (dbnoMessage != null) nbt.putString(KEY_DBNO_MSG, Component.Serializer.toJson(dbnoMessage));
            HelperLib.sync(this, nbt, player, true);
            return true;
        }
        return false;
    }

    @Override
    public void onDeath(@NotNull DamageSource src) {
        super.onDeath(src);
        if (actionHandler.isActionActive(VampireActions.BAT.get()) && src.getDirectEntity() instanceof Projectile) {
            if (player instanceof ServerPlayer) {
                ModAdvancements.TRIGGER_VAMPIRE_ACTION.trigger((ServerPlayer) player, VampireActionCriterionTrigger.Action.SNIPED_IN_BAT);
            }
        }
        actionHandler.deactivateAllActions();
        wasDead = true;
        this.setDBNOTimer(-1);
        dbnoMessage = null;
    }

    @Override
    public boolean onEntityAttacked(@NotNull DamageSource src, float amt) {
        if (getLevel() > 0) {
            if (isDBNO() && !Helper.canKillVampires(src)) {
                if (src.getEntity() != null && src.getEntity() instanceof Mob && ((Mob) src.getEntity()).getTarget() == player) {
                    ((Mob) src.getEntity()).setTarget(null);
                }
                return true;
            }
            if (src.is(DamageTypes.ON_FIRE)) {
                DamageHandler.hurtModded(player, ModDamageSources::vampireOnFire, calculateFireDamage(amt));
                return true;
            } else if (src.is(DamageTypes.IN_FIRE) || src.is(DamageTypes.LAVA)) {
                DamageHandler.hurtModded(player, ModDamageSources::vampireInFire, calculateFireDamage(amt));
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
            if (victim instanceof Player) {
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
                player.addEffect(new MobEffectInstance(ModEffects.SUNSCREEN.get(), 400, 4, false, false));
                player.addEffect(new MobEffectInstance(ModEffects.ARMOR_REGENERATION.get(), VampirismConfig.BALANCE.vpNaturalArmorRegenDuration.get() * 20, 0, false, false));
                requestNaturalArmorUpdate();
                player.setHealth(player.getMaxHealth());
                bloodStats.setBloodLevel(bloodStats.getMaxBlood());
            }
        }
    }

    @Override
    public void onLevelChanged(int newLevel, int oldLevel) {
        super.onLevelChanged(newLevel, oldLevel);
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
        } else {
            if (oldLevel == 0) {
                if (player.hasEffect(MobEffects.NIGHT_VISION)) {
                    player.removeEffect(MobEffects.NIGHT_VISION);
                }
            } else if (newLevel == 0) {
                if (player.getEffect(MobEffects.NIGHT_VISION) instanceof VampireNightVisionEffectInstance) {
                    player.removeEffect(MobEffects.NIGHT_VISION);
                }
            }
        }
    }

    @Override
    public void onPlayerLoggedIn() {
        if (getLevel() > 0 && !player.level.isClientSide) {
            player.addEffect(new MobEffectInstance(ModEffects.SUNSCREEN.get(), 200, 4, true, false));
        }
    }

    @Override
    public void onPlayerLoggedOut() {
        endFeeding(false);
        if (this.isDBNO()) {
            this.setDBNOTimer(-1);
            DamageHandler.kill(player, 10000);
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
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 300));
                player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 300));
            });
        }
    }

    @Override
    public void onUpdate() {
        Level world = player.getCommandSenderWorld();
        world.getProfiler().push("vampirism_vampirePlayer");
        if (wasDBNO) {
            wasDBNO = false;
            DamageHandler.kill(player, 100000);
            return;
        } else if (this.dbnoTimer >= 0) {
            if (dbnoTimer > 0) {
                this.setDBNOTimer(dbnoTimer - 1);
                if (dbnoTimer == 0) {
                    CompoundTag nbt = new CompoundTag();
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
        this.vision.tick();

        if (!isRemote()) {
            if (level > 0) {
                boolean sync = false;
                boolean syncToAll = false;
                CompoundTag syncPacket = new CompoundTag();

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
                    if (player.tickCount % 16 == 4 && !getSpecialAttributes().waterResistance && !player.getAbilities().instabuild) {
                        if (player.isInWater()) {
                            FluidState state1 = world.getFluidState(player.blockPosition());
                            FluidState state2 = world.getFluidState(player.blockPosition().above());
                            if ((state1.is(FluidTags.WATER) && (state1.getFlow(world, player.blockPosition()).lengthSqr() > 0)) || (state2.is(FluidTags.WATER) && (state2.getFlow(world, player.blockPosition().above()).lengthSqr() > 0))) {
                                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, (int) (getLevel() / (float) getMaxLevel() * 3)));
                            }
                        }
                    }
                }

                if (player.tickCount % 9 == 3 && VampirismConfig.BALANCE.vpFireResistanceReplace.get() && player.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                    MobEffectInstance fireResistance = player.getEffect(MobEffects.FIRE_RESISTANCE);
                    player.addEffect(new MobEffectInstance(ModEffects.FIRE_PROTECTION.get(), fireResistance.getDuration(), fireResistance.getAmplifier()));
                    player.removeEffect(MobEffects.FIRE_RESISTANCE);
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
                    VampirismMod.dispatcher.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Type.FINISH_SUCK_BLOOD));
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
                    sync(this.bloodStats.writeUpdate(new CompoundTag()), false);
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

    public void saveData(@NotNull CompoundTag nbt) {
        super.saveData(nbt);
        bloodStats.writeNBT(nbt);
        nbt.putInt(KEY_EYE, getEyeType());
        nbt.putInt(KEY_FANGS, getFangType());
        nbt.putBoolean(KEY_GLOWING_EYES, getGlowingEyes());
        actionHandler.saveToNbt(nbt);
        skillHandler.saveToNbt(nbt);
        nbt.put(KEY_VISION, this.vision.createTag());
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
                CompoundTag nbt = new CompoundTag();
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
                CompoundTag nbt = new CompoundTag();
                nbt.putInt(KEY_FANGS, fangType);
                sync(nbt, true);
            }
        }
        return true;
    }

    public void setSkinData(int @NotNull ... data) {
        if (data.length > 0) {
            this.setFangType(data[0]);
            if (data.length > 1) {
                this.setEyeType(data[1]);
                if (data.length > 2) {
                    this.setGlowingEyes(data[2] > 0);
                    if (data.length > 3) {
                        FactionPlayerHandler.getOpt(this.player).ifPresent(handler -> handler.setTitleGender(data[3] > 0));
                    }
                }
            }
        }
    }

    /**
     * Switch to the next vision
     */
    public void switchVision() {
        this.vision.switchVision();
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
            this.player.addEffect(new MobEffectInstance(ModEffects.NEONATAL.get(), duration));
        } else {
            if (this.isRemote()) {
                this.setDBNOTimer(-1);
            } else {
                //If client thinks it is alive again, tell it to die again
                this.sync(false);
            }
        }
    }

    public void giveUpDBNO() {
        if (this.isDBNO()) {
            //Reset dbno state before killing the player in case something is canceling the death event
            this.setDBNOTimer(-1);
            Component msg = this.dbnoMessage;
            this.dbnoMessage = null;
            this.player.setForcedPose(null);
            this.player.refreshDimensions();
            this.sync(true);
            DamageHandler.hurtModded(this.player, sources -> sources.dbno(msg), 10000);
        }
    }

    @Override
    public void unUnlockVision(@NotNull IVampireVision vision) {
        this.vision.lockVision(vision);
    }

    @Override
    public void unlockVision(@NotNull IVampireVision vision) {
        this.vision.unlockVision(vision);
    }

    public void updateNaturalArmor(int lvl) {
        AttributeInstance armorAtt = player.getAttribute(Attributes.ARMOR);
        AttributeInstance toughnessAtt = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if (armorAtt != null && toughnessAtt != null) {
            if (lvl == 0) {
                armorAtt.removeModifier(NATURAL_ARMOR_UUID);
                toughnessAtt.removeModifier(NATURAL_ARMOR_UUID);
            } else {
                AttributeModifier modArmor = armorAtt.getModifier(NATURAL_ARMOR_UUID);
                AttributeModifier modToughness = toughnessAtt.getModifier(NATURAL_ARMOR_UUID);
                double naturalArmor = getNaturalArmorValue(lvl);
                MobEffectInstance armorRegen = player.getEffect(ModEffects.ARMOR_REGENERATION.get());
                double armorRegenerationMod = armorRegen == null ? 0 : armorRegen.getDuration() / ((double) VampirismConfig.BALANCE.vpNaturalArmorRegenDuration.get() * 20);
                naturalArmor *= (1 - 0.75 * armorRegenerationMod); //Modify natural armor between 25% and 100% depending on the armor regen state
                double naturalToughness = getNaturalArmorToughnessValue(lvl);
                Collection<UUID> armorItemModifiers = ArmorItemAccessor.getModifierUUID_vampirism().values();
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
    protected @NotNull FactionBasePlayer<IVampirePlayer> copyFromPlayer(@NotNull Player old) {
        VampirePlayer oldVampire = get(old);
        CompoundTag nbt = new CompoundTag();
        oldVampire.saveData(nbt);
        this.loadData(nbt);
        this.wasDead = oldVampire.wasDead;
        return oldVampire;
    }

    @Override
    protected void loadUpdate(@NotNull CompoundTag nbt) {
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
            if (feed_victim != -1) {
                if (feedingSoundReference == null || !feedingSoundReference.isPlaying()) {
                    feedingSoundReference = VampLib.proxy.createSoundReference(ModSounds.VAMPIRE_FEEDING.get(), SoundSource.PLAYERS, player.getX(), player.getY(), player.getZ(), 0.8f, 1);
                    feedingSoundReference.startPlaying();
                }
            } else {
                if (feedingSoundReference != null) {
                    feedingSoundReference.stopPlaying();
                    feedingSoundReference = null;
                }
            }
        }
        if (nbt.contains(KEY_DBNO_MSG)) {
            dbnoMessage = Component.Serializer.fromJson(nbt.getString(KEY_DBNO_MSG));
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
        if (nbt.contains(KEY_VISION, CompoundTag.TAG_COMPOUND)) {
            this.vision.readTag(nbt.getCompound(KEY_VISION));
        }
    }

    @Override
    protected void writeFullUpdate(@NotNull CompoundTag nbt) {
        super.writeFullUpdate(nbt);
        nbt.putInt(KEY_EYE, getEyeType());
        nbt.putInt(KEY_FANGS, getFangType());
        nbt.putBoolean(KEY_GLOWING_EYES, getGlowingEyes());
        nbt.putInt(KEY_FEED_VICTIM_ID, feed_victim);
        bloodStats.writeUpdate(nbt);
        actionHandler.writeUpdateForClient(nbt);
        skillHandler.writeUpdateForClient(nbt);
        nbt.put(KEY_VISION, this.vision.createTag());
        nbt.putInt(KEY_DBNO_TIMER, getDbnoTimer());
        if (dbnoMessage != null) nbt.putString(KEY_DBNO_MSG, Component.Serializer.toJson(dbnoMessage));
    }

    private void applyEntityAttributes() {
        player.getAttribute(ModAttributes.SUNDAMAGE.get()).setBaseValue(VampirismConfig.BALANCE.vpSundamage.get());
        player.getAttribute(ModAttributes.BLOOD_EXHAUSTION.get()).setBaseValue(VampirismConfig.BALANCE.vpBloodExhaustionFactor.get());
    }

    /**
     * Apply the armor unaffected level scaled entity attribute modifiers
     */
    private void applyLevelModifiersA(int level) {
        LevelAttributeModifier.applyModifier(player, Attributes.MAX_HEALTH, "Vampire", level, getMaxLevel(), VampirismConfig.BALANCE.vpHealthMaxMod.get(), 0.5, AttributeModifier.Operation.ADDITION, true);
        LevelAttributeModifier.applyModifier(player, ModAttributes.BLOOD_EXHAUSTION.get(), "Vampire", level, getMaxLevel(), VampirismConfig.BALANCE.vpExhaustionMaxMod.get(), 0.5, AttributeModifier.Operation.MULTIPLY_BASE, false);
    }

    /**
     * Apply the armor affected level scaled entity attribute modifiers
     */
    private void applyLevelModifiersB(int level, boolean heavyArmor) {
        LevelAttributeModifier.applyModifier(player, Attributes.MOVEMENT_SPEED, "Vampire", level, getMaxLevel(), VampirismConfig.BALANCE.vpSpeedMaxMod.get() * (heavyArmor ? 0.5f : 1), 0.5, AttributeModifier.Operation.MULTIPLY_BASE, false);
        LevelAttributeModifier.applyModifier(player, Attributes.ATTACK_SPEED, "Vampire", level, getMaxLevel(), VampirismConfig.BALANCE.vpAttackSpeedMaxMod.get() * (heavyArmor ? 0.5f : 1), 0.5, AttributeModifier.Operation.MULTIPLY_BASE, false);
    }

    private void biteBlock(@NotNull BlockPos pos, @NotNull BlockState blockState, @Nullable BlockEntity tileEntity) {
        if (isRemote()) return;
        if (getLevel() == 0) return;
        if (!bloodStats.needsBlood()) return;

        int need = Math.min(8, bloodStats.getMaxBlood() - bloodStats.getBloodLevel());
        if (ModBlocks.BLOOD_CONTAINER.get() == blockState.getBlock()) {
            if (tileEntity != null) {
                tileEntity.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(handler -> {
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

                        CompoundTag updatePacket = bloodStats.writeUpdate(new CompoundTag());
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
    private boolean biteFeed(@NotNull LivingEntity entity) {
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
            blood = VampirePlayer.getOpt((Player) entity).map(v -> v.onBite(this)).orElse(0);
            saturationMod = VampirePlayer.getOpt((Player) entity).map(VampirePlayer::getBloodSaturation).orElse(0f);
        } else if (feed_victim_bite_type == BITE_TYPE.SUCK_BLOOD) {
            blood = ((IBiteableEntity) entity).onBite(this);
            saturationMod = ((IBiteableEntity) entity).getBloodSaturation();
        }
        if (blood > 0) {
            drinkBlood(blood, saturationMod);
            CompoundTag updatePacket = bloodStats.writeUpdate(new CompoundTag());
            updatePacket.putInt(KEY_SPAWN_BITE_PARTICLE, entity.getId());
            sync(updatePacket, true);
            if (player instanceof ServerPlayer) {
                ModAdvancements.TRIGGER_VAMPIRE_ACTION.trigger((ServerPlayer) player, VampireActionCriterionTrigger.Action.SUCK_BLOOD);
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
        MobEffectInstance potionEffect = player.getEffect(ModEffects.SUNSCREEN.get());
        int sunscreen = potionEffect == null ? -1 : potionEffect.getAmplifier();
        if (ticksInSun < 100) {
            ticksInSun++;
        }
        if (ticksInSun > 50 && (sunscreen >= 4 || (VampirismConfig.BALANCE.vpSunscreenBuff.get() && sunscreen >= 0))) {
            ticksInSun = 50;
        }
        if (!player.isAlive() || isRemote || player.getAbilities().instabuild || player.getAbilities().invulnerable) return;

        if (ticksInSun == 100 && VampirismConfig.BALANCE.vpSundamageInstantDeath.get()) {
            DamageHandler.kill(player, 100000);
            turnToAsh();
        }

        if (VampirismConfig.BALANCE.vpSundamageNausea.get() && getLevel() >= VampirismConfig.BALANCE.vpSundamageNauseaMinLevel.get() && player.tickCount % 300 == 1 && ticksInSun > 50 && sunscreen == -1) {
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 180));
        }
        if (getLevel() >= VampirismConfig.BALANCE.vpSundamageWeaknessMinLevel.get() && player.tickCount % 150 == 3 && sunscreen < 5) {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 152, 0));
        }
        if (getLevel() >= VampirismConfig.BALANCE.vpSundamageMinLevel.get() && ticksInSun >= 100 && player.tickCount % 40 == 5) {
            float damage = (float) (player.getAttribute(ModAttributes.SUNDAMAGE.get()).getValue());
            if (damage > 0) {
                DamageHandler.hurtModded(player, ModDamageSources::sunDamage, damage);
            }
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
        AABB axisalignedbb = (new AABB(player.blockPosition())).inflate(32.0D, 10.0D, 32.0D);
        player.level.getEntitiesOfClass(Mob.class, axisalignedbb).forEach(e -> {
            if (e.getTarget() == player) {
                e.targetSelector.getRunningGoals().filter(g -> g.getGoal() instanceof TargetGoal).forEach(WrappedGoal::stop);
            }
            if (e instanceof NeutralMob) {
                ((NeutralMob) e).playerDied(player);
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
     * @param entityId ID of the entity
     */
    private void spawnBiteParticle(int entityId) {
        Entity entity = player.level.getEntity(entityId);
        if (entity != null) {
            UtilLib.spawnParticles(player.level, ParticleTypes.CRIT, entity.getX(), entity.getY(), entity.getZ(), player.getX() - entity.getX(), player.getY() - entity.getY(), player.getZ() - entity.getZ(), 10, 1);
        }
        for (int j = 0; j < 16; ++j) {
            Vec3 vec3 = new Vec3((player.getRandom().nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
            vec3 = vec3.xRot(-player.getXRot() * (float) Math.PI / 180F);
            vec3 = vec3.yRot(-player.getYRot() * (float) Math.PI / 180F);
            double d0 = (double) (-player.getRandom().nextFloat()) * 0.6D - 0.3D;
            Vec3 vec31 = new Vec3(((double) player.getRandom().nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
            vec31 = vec31.xRot(-player.getXRot() * (float) Math.PI / 180.0F);
            vec31 = vec31.yRot(-player.getYRot() * (float) Math.PI / 180.0F);
            vec31 = vec31.add(player.getX(), player.getY() + (double) player.getEyeHeight(), player.getZ());

            player.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.APPLE)), vec31.x, vec31.y, vec31.z, vec3.x, vec3.y + 0.05D, vec3.z);
        }
    }

    /**
     * This is called every 20 ticks in onUpdate() to run the continuous feeding effect
     */
    private void updateFeeding() {
        Entity entity = player.level.getEntity(feed_victim);
        if (!(entity instanceof LivingEntity e)) return;
        if (e.getHealth() == 0f) {
            endFeeding(true);
            return;
        }
        e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 7, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 25, 4, false, false));

        ModParticles.spawnParticlesServer(player.level, new FlyingBloodEntityParticleOptions(player.getId(), true), e.getX(), e.getY() + e.getEyeHeight() / 2, e.getZ(), 10, 0.1f, 0.1f, 0.1f, 0);

        if (!biteFeed(e)) {
            endFeeding(true);
        }

        if (!(e.distanceTo(player) <= player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue() + 1) || e.getHealth() == 0f) {
            endFeeding(true);
        }
    }

    @Override
    public void updateMinionAttributes(boolean enabled) {
        MinionWorldData.getData(this.player.level).flatMap(a -> FactionPlayerHandler.getOpt(this.player).map(a::getOrCreateController)).ifPresent(controller -> controller.contactMinions((minion) -> {
            (minion.getMinionData()).ifPresent(b -> ((VampireMinionEntity.VampireMinionData)b).setIncreasedStats(enabled));
            HelperLib.sync(minion);
        }));
    }

    private class VisionStatus {
        private final SortedSet<IVampireVision> unlockedVisions = new TreeSet<>(Comparator.comparing(o -> VampirismAPI.vampireVisionRegistry().getVisionId(o)));
        private ResourceLocation visionId;
        private IVampireVision vision;

        public void deactivate() {
            if (vision != null) {
                vision.onDeactivated(VampirePlayer.this);
                vision = null;
                visionId = null;
            }
        }

        public void deactivate(IVampireVision vision) {
            if (this.vision == vision) {
                deactivate();
            }
        }

        private void tick() {
            if (this.vision != null) {
                this.vision.tick(VampirePlayer.this);
                if (!this.vision.isEnabled()) {
                    deactivate();
                }
            }
        }

        private void switchVision() {
            List<IVampireVision> visions = VampirismAPI.vampireVisionRegistry().getVisions();
            int newIndex;
            if (this.vision != null) {
                newIndex = visions.indexOf(this.vision) + 1;
            } else {
                newIndex = 0;
            }
            var newVision = newIndex == visions.size() ? null : visions.get(++newIndex);
            activate(newVision);
        }

        public void unlockVision(IVampireVision vision) {
            VampirismAPI.vampireVisionRegistry().getVisionId(vision);
            this.unlockedVisions.add(vision);
        }

        public void lockVision(IVampireVision vision) {
            this.deactivate(vision);
            this.unlockedVisions.remove(vision);
        }

        public void activate(@Nullable IVampireVision vision) {
            if (this.vision != null && this.vision == vision) {
                return;
            }
            if (this.vision != null) {
                deactivate();
            }
            if (vision != null) {
                if (vision.isEnabled()) {
                    this.vision = vision;
                    this.vision.onActivated(VampirePlayer.this);
                } else if (VampirePlayer.this.player.isAddedToWorld()) {
                    VampirePlayer.this.player.displayClientMessage(Component.translatable("Vision is disabled by config"),true);
                }
                this.visionId = VampirismAPI.vampireVisionRegistry().getVisionId(vision);
            } else {
                this.vision = null;
                this.visionId = null;
            }

            if (!isRemote() && player.isAddedToWorld()) {
                CompoundTag tag = new CompoundTag();
                tag.put(KEY_VISION, createTag());
                VampirePlayer.this.sync(tag, false);
            }
        }

        private @NotNull CompoundTag createTag() {
            CompoundTag tag = new CompoundTag();
            if (this.visionId == null) {
                tag.putBoolean("hasVision", false);
            } else {
                tag.putBoolean("hasVision", true);
                tag.putString(this.vision != null ? "vision": "visionId", this.visionId.toString());
            }
            return tag;
        }

        private void readTag(@NotNull CompoundTag tag) {
            if (tag.getBoolean("hasVision")) {
                if (tag.contains("vision")) {
                    // legacy support for integer ids
                    if (tag.contains("vision", Tag.TAG_INT)) {
                        //noinspection removal
                        this.activate(VampirismAPI.vampireVisionRegistry().getVisionOfId(tag.getInt("vision")));
                    } else if (tag.contains("vision", Tag.TAG_STRING)) {
                        this.activate(VampirismAPI.vampireVisionRegistry().getVision(new ResourceLocation(tag.getString("vision"))));
                    }
                } else if(tag.contains("visionId")) {
                    this.deactivate();
                    //legacy support for integer ids
                    if (tag.contains("visionId", Tag.TAG_INT)) {
                        //noinspection removal
                        this.visionId = VampirismAPI.vampireVisionRegistry().getIdForId(tag.getInt("visionId"));

                    } else if (tag.contains("visionId", Tag.TAG_STRING)) {
                        this.visionId = new ResourceLocation(tag.getString("visionId"));
                    }
                }
            } else {
                this.deactivate();
                this.vision = null;
                this.visionId = null;
            }
        }

    }
}
