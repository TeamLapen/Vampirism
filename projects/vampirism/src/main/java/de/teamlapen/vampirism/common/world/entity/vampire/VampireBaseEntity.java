package de.teamlapen.vampirism.common.world.entity.vampire;

import de.teamlapen.factions.common.util.StructureUtil;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.event.BloodDrinkEvent;
import de.teamlapen.vampirism.api.util.VampirismEventFactory;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IDrinkBloodContext;
import de.teamlapen.vampirism.api.world.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.api.world.items.IItemWithTier;
import de.teamlapen.vampirism.common.config.BalanceMobProps;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.*;
import de.teamlapen.vampirism.common.tags.ModBiomeTags;
import de.teamlapen.vampirism.common.tags.ModBlockTags;
import de.teamlapen.vampirism.common.util.DamageHandler;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.util.UtilLib;
import de.teamlapen.vampirism.common.world.attachments.ModDamageSources;
import de.teamlapen.vampirism.common.world.entity.CrossbowArrowEntity;
import de.teamlapen.vampirism.common.world.entity.SoulOrbEntity;
import de.teamlapen.vampirism.common.world.entity.VampirismEntity;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.world.items.HunterCoatItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Base class for Vampirism's vampire entities
 */
public abstract class VampireBaseEntity extends VampirismEntity implements IVampireMob, Npc/*mainly for JourneyMap*/ {

    public static boolean spawnPredicateVampire(@NotNull EntityType<? extends VampirismEntity> entityType, @NotNull ServerLevelAccessor world, EntitySpawnReason spawnReason, @NotNull BlockPos blockPos, @NotNull RandomSource random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && (Monster.isDarkEnoughToSpawn(world, blockPos, random) || spawnPredicateVampireFog(world, blockPos)) && Mob.checkMobSpawnRules(entityType, world, spawnReason, blockPos, random);
    }

    public static AttributeSupplier.@NotNull Builder getAttributeBuilder() {
        return VampirismEntity.getAttributeBuilder().add(ModAttributes.SUNDAMAGE, BalanceMobProps.mobProps.VAMPIRE_MOB_SUN_DAMAGE);
    }

    protected @NotNull EnumStrength garlicResist = EnumStrength.NONE;
    protected boolean canSuckBloodFromPlayer = false;
    protected boolean vulnerableToFire = true;
    /**
     * Rules to consider for {@link #checkSpawnRules(LevelAccessor, EntitySpawnReason)}
     */
    private SpawnRestriction spawnRestriction = SpawnRestriction.NORMAL;
    private boolean sundamageCache;
    private @NotNull EnumStrength garlicCache = EnumStrength.NONE;
    /**
     * If the vampire should spawn a vampire soul at the end of its death animation.
     * No need to store this in NBT as it is only set during onDeath() so basically 20 ticks beforehand.
     */
    private boolean dropSoul = false;

    public VampireBaseEntity(EntityType<? extends VampireBaseEntity> type, Level world) {
        super(type, world);
    }

    @Override
    public void aiStep() {
        if (this.tickCount % REFERENCE.REFRESH_GARLIC_TICKS == 3) {
            isGettingGarlicDamage(level(), true);
        }
        if (this.tickCount % REFERENCE.REFRESH_SUNDAMAGE_TICKS == 2) {
            isGettingSundamage(level(), true);
        }
        if (level() instanceof ServerLevel level) {
            if (isGettingSundamage(level()) && this.isAlive()) {
                if (ModConfig.balance().vpSundamageInstantDeath.get()) {
                    DamageHandler.hurtModded(level, this, ModDamageSources::sunDamage, 1000);
                    turnToAsh();
                } else if (tickCount % 40 == 11) {
                    double dmg = getAttribute(ModAttributes.SUNDAMAGE).getValue();
                    if (dmg > 0) {
                        DamageHandler.hurtModded(level, this, ModDamageSources::sunDamage, (float) dmg);
                    }
                }

            }
            if (isGettingGarlicDamage(level()) != EnumStrength.NONE) {
                DamageHandler.affectVampireGarlicAmbient(this, isGettingGarlicDamage(level()), this.tickCount);
            }
        }
        if (!this.level().isClientSide()) {
            if (isAlive() && isInWater()) {
                setAirSupply(300);
                if (tickCount % 16 == 4) {
                    addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0));
                }
            }
        }
        super.aiStep();
    }

    /**
     * Spawn ash particles and remove body.
     * Must be dead already
     */
    private void turnToAsh() {
        if (!this.isAlive()) {
            this.deathTime = 19;
            ModParticles.spawnParticlesServer(this.level(), ParticleTypes.WHITE_ASH, this.getX() + 0.5, this.getY() + this.getBbHeight(), this.getZ() + 0.5f, 20, 0.2, this.getBbHeight() * 0.2d, 0.2, 0.1);
            ModParticles.spawnParticlesServer(this.level(), ParticleTypes.ASH, this.getX() + 0.5, this.getY() + this.getBbHeight() / 2, this.getZ() + 0.5f, 20, 0.2, this.getBbHeight() * 0.2d, 0.2, 0.1);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public boolean checkSpawnRules(@NotNull LevelAccessor worldIn, @NotNull EntitySpawnReason spawnReasonIn) {
        if (spawnRestriction.level >= SpawnRestriction.SIMPLE.level) {
            if (isGettingSundamage(worldIn, true) || isGettingGarlicDamage(worldIn, true) != EnumStrength.NONE) {
                return false;
            }
            if (spawnRestriction.level >= SpawnRestriction.NORMAL.level) {
                if (-worldIn.getPathfindingCostFromLightLevels(blockPosition()) < 0.0 && random.nextInt(5) != 0) {
                    return false;
                }
                if (this.level().isLoaded(blockPosition()) && worldIn instanceof ServerLevel) { //TODO check performance
                    if (StructureUtil.getStructureStartAt(level(), blockPosition(), StructureTags.VILLAGE).isPresent()) {
                        if (getRandom().nextInt(60) != 0) {
                            return false;
                        }
                    }
                }
                if (spawnRestriction.level >= SpawnRestriction.SPECIAL.level) {
                    if (!getCanSpawnHereRestricted(worldIn)) {
                        return false;
                    }
                }
            }
        }

        return super.checkSpawnRules(worldIn, spawnReasonIn);
    }

    @Override
    public void die(@NotNull DamageSource cause) {
        super.die(cause);
        if (cause.getDirectEntity() instanceof CrossbowArrowEntity && Helper.isHunter(cause.getEntity())) {
            dropSoul = true;
        } else if (cause.getDirectEntity() instanceof Player && Helper.isHunter(cause.getDirectEntity())) {
            ItemStack weapon = ((Player) cause.getDirectEntity()).getMainHandItem();
            if (!weapon.isEmpty() && weapon.has(ModDataComponents.DROP_VAMPIRE_SOUL)) {
                dropSoul = true;
            }
        } else {
            dropSoul = false;//In case a previous death has been canceled somehow
        }
    }

    @Override
    public boolean doesResistGarlic(@NotNull EnumStrength strength) {
        return !strength.isStrongerThan(garlicResist);
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, @NotNull Entity entity) {
        if (canSuckBloodFromPlayer && !level().isClientSide() && wantsBlood() && entity instanceof Player player && !Helper.isHunter(player) && !UtilLib.canReallySee(player, this, true)) {
            int amt = VampirePlayer.get(player).onBite(this);
            drinkBlood(amt, IBloodStats.MEDIUM_SATURATION, new DrinkBloodContext(player));
            VampirePlayer.get(player).tryInfect(this);
            return true;
        }
        if (entity instanceof LivingEntity living) {
            for (ItemStack e : Arrays.stream(EquipmentSlot.values()).filter(x -> x.getType() == EquipmentSlot.Type.HUMANOID_ARMOR).map(living::getItemBySlot).toList()) {
                if (e != null && e.getItem() instanceof HunterCoatItem) {
                    int j = 1;
                    if (((HunterCoatItem) e.getItem()).getVampirismTier().equals(IItemWithTier.Tier.ENHANCED)) {
                        j = 2;
                    } else if (((HunterCoatItem) e.getItem()).getVampirismTier().equals(IItemWithTier.Tier.ULTIMATE)) {
                        j = 3;
                    }
                    if (getRandom().nextInt((4 - j) * 2) == 0) {
                        addEffect(new MobEffectInstance(ModEffects.POISON, (int) (20 * Math.sqrt(j)), j));
                    }
                }
            }
        }
        return super.doHurtTarget(level, entity);
    }

    @Override
    public void drinkBlood(int amt, float saturationMod, boolean useRemaining, IDrinkBloodContext drinkContext) {
        BloodDrinkEvent.@NotNull EntityDrinkBloodEvent event = VampirismEventFactory.fireVampireDrinkBlood(this, amt, saturationMod, useRemaining, drinkContext);
        this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, event.getAmount() * 20));
    }

    @NotNull
    @Override
    public EnumStrength isGettingGarlicDamage(LevelAccessor iWorld, boolean forcerefresh) {
        if (forcerefresh) {
            garlicCache = Helper.getGarlicStrength(this, iWorld);
        }
        return garlicCache;
    }

    @Override
    public boolean hurtServer(ServerLevel level, @NotNull DamageSource damageSource, float amount) {
        if (vulnerableToFire) {
            if (damageSource.is(DamageTypes.IN_FIRE)) {
                return DamageHandler.hurtModded(level, this, ModDamageSources::vampireInFire, calculateFireDamage(amount));
            } else if (damageSource.is(DamageTypes.ON_FIRE)) {
                return DamageHandler.hurtModded(level, this, ModDamageSources::vampireOnFire, calculateFireDamage(amount));
            }
        }
        return super.hurtServer(level, damageSource, amount);
    }

    @Override
    public boolean isGettingSundamage(LevelAccessor iWorld, boolean forceRefresh) {
        if (!forceRefresh) return sundamageCache;
        return (sundamageCache = Helper.gettingSundamge(this, iWorld));
    }

    @Override
    public boolean isIgnoringSundamage() {
        return this.hasEffect(ModEffects.SUNSCREEN);
    }

    /**
     * Select rules to consider for {@link #checkSpawnRules(LevelAccessor, EntitySpawnReason)}
     */
    public void setSpawnRestriction(SpawnRestriction r) {
        this.spawnRestriction = r;
    }

    @Override
    public boolean useBlood(int amt, boolean allowPartial) {
        this.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, amt * 20));
        return true;
    }

    @Override
    public boolean wantsBlood() {
        return false;
    }

    /**
     * Calculates the increased fire damage is this vampire creature is especially vulnerable to fire
     */
    protected float calculateFireDamage(float amount) {
        return amount;
    }

    @Override
    protected void tickDeath() {
        if (this.deathTime == 19) {
            if (this.level() instanceof ServerLevel level && (dropSoul && level.getGameRules().get(GameRules.MOB_DROPS))) {
                level.addFreshEntity(new SoulOrbEntity(this.level(), this.getX(), this.getY(), this.getZ(), SoulOrbEntity.VARIANT.VAMPIRE));
            }
        }
        super.tickDeath();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
    }

    /**
     * Checks if light level is low enough
     * Only exception is the vampire biome in which it returns true if ontop of {@link ModBlocks#CURSED_EARTH}
     */
    private boolean getCanSpawnHereRestricted(@NotNull LevelAccessor iWorld) {
        boolean vampireBiome = iWorld.getBiome(this.blockPosition()).is(ModBiomeTags.HasFaction.IS_VAMPIRE_BIOME);
        boolean lowLightLevel = isLowLightLevel(iWorld);
        if (lowLightLevel) return true;
        if (!vampireBiome) return false;
        BlockState iblockstate = iWorld.getBlockState((this.blockPosition()).below());
        return iblockstate.is(ModBlockTags.CURSED_EARTH);
    }

    public enum SpawnRestriction {
        /**
         * Only entity spawn checks
         */
        NONE(0),
        /**
         * +No direct sunlight or garlic
         */
        SIMPLE(1),
        /**
         * +Avoid villages and daytime (random chance)
         */
        NORMAL(2),
        /**
         * +Only at low light level or in vampire biome on cursed earth
         */
        SPECIAL(3);

        final int level;

        SpawnRestriction(int level) {
            this.level = level;
        }
    }
}
