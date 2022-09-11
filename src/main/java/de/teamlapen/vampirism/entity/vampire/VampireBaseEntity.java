package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.api.items.IVampireFinisher;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.entity.CrossbowArrowEntity;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.SoulOrbEntity;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.items.HunterCoatItem;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.*;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Base class for Vampirism's vampire entities
 */
public abstract class VampireBaseEntity extends VampirismEntity implements IVampireMob, INPC/*mainly for JourneyMap*/ {

    public static boolean spawnPredicateVampire(EntityType<? extends VampirismEntity> entityType, IServerWorld world, SpawnReason spawnReason, BlockPos blockPos, Random random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && (spawnPredicateLight(world, blockPos, random) || spawnPredicateVampireFog(world, blockPos)) && spawnPredicateCanSpawn(entityType, world, spawnReason, blockPos, random);
    }

    public static AttributeModifierMap.MutableAttribute getAttributeBuilder() {
        return VampirismEntity.getAttributeBuilder().add(ModAttributes.SUNDAMAGE.get(), BalanceMobProps.mobProps.VAMPIRE_MOB_SUN_DAMAGE);
    }
    private final boolean countAsMonsterForSpawn;
    protected EnumStrength garlicResist = EnumStrength.NONE;
    protected boolean canSuckBloodFromPlayer = false;
    protected boolean vulnerableToFire = true;
    /**
     * Rules to consider for {@link #canSpawn(IWorld, SpawnReason)}
     */
    private SpawnRestriction spawnRestriction = SpawnRestriction.NORMAL;
    private boolean sundamageCache;
    private EnumStrength garlicCache = EnumStrength.NONE;
    /**
     * If the vampire should spawn a vampire soul at the end of its death animation.
     * No need to store this in NBT as it is only set during onDeath() so basically 20 ticks beforehand.
     */
    private boolean dropSoul = false;

    /**
     * @param countAsMonsterForSpawn If this entity should be counted as vampire and as monster during spawning
     */
    public VampireBaseEntity(EntityType<? extends VampireBaseEntity> type, World world, boolean countAsMonsterForSpawn) {
        super(type, world);
        this.countAsMonsterForSpawn = countAsMonsterForSpawn;

    }

    @Override
    public void aiStep() {
        if (this.tickCount % REFERENCE.REFRESH_GARLIC_TICKS == 3) {
            isGettingGarlicDamage(level, true);
        }
        if (this.tickCount % REFERENCE.REFRESH_SUNDAMAGE_TICKS == 2) {
            isGettingSundamage(level, true);
        }
        if (!level.isClientSide) {
            if (isGettingSundamage(level) && this.isAlive()) {
                if (VampirismConfig.BALANCE.vpSundamageInstantDeath.get()) {
                    this.hurt(VReference.SUNDAMAGE, 1000);
                    turnToAsh();
                } else if (tickCount % 40 == 11) {
                    double dmg = getAttribute(ModAttributes.SUNDAMAGE.get()).getValue();
                    if (dmg > 0) this.hurt(VReference.SUNDAMAGE, (float) dmg);
                }

            }
            if (isGettingGarlicDamage(level) != EnumStrength.NONE) {
                DamageHandler.affectVampireGarlicAmbient(this, isGettingGarlicDamage(level), this.tickCount);
            }
        }
        if (!this.level.isClientSide) {
            if (isAlive() && isInWater()) {
                setAirSupply(300);
                if (tickCount % 16 == 4) {
                    addEffect(new EffectInstance(Effects.WEAKNESS, 80, 0));
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
            ModParticles.spawnParticlesServer(this.level, ParticleTypes.WHITE_ASH, this.getX() + 0.5, this.getY() + this.getBbHeight(), this.getZ() + 0.5f, 20, 0.2, this.getBbHeight() * 0.2d, 0.2, 0.1);
            ModParticles.spawnParticlesServer(this.level, ParticleTypes.ASH, this.getX() + 0.5, this.getY() + this.getBbHeight() / 2, this.getZ() + 0.5f, 20, 0.2, this.getBbHeight() * 0.2d, 0.2, 0.1);
            this.remove();
        }
    }

    @Override
    public boolean checkSpawnRules(IWorld worldIn, SpawnReason spawnReasonIn) {
        if (spawnRestriction.level >= SpawnRestriction.SIMPLE.level) {
            if (isGettingSundamage(worldIn, true) || isGettingGarlicDamage(worldIn, true) != EnumStrength.NONE)
                return false;
            if (spawnRestriction.level >= SpawnRestriction.NORMAL.level) {
                if (worldIn.getBrightness(blockPosition()) > 0.5 && random.nextInt(5) != 0) {
                    return false;
                }
                if (this.level.isLoaded(blockPosition()) && worldIn instanceof ServerWorld) { //TODO check performance
                    if (((ServerWorld) level).getWorldServer().startsForFeature(SectionPos.of(blockPosition()), Structure.VILLAGE).findAny().isPresent()) {
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
    public void die(DamageSource cause) {
        super.die(cause);
        if (cause.getDirectEntity() instanceof CrossbowArrowEntity && Helper.isHunter(cause.getEntity())) {
            dropSoul = true;
        } else if (cause.getDirectEntity() instanceof PlayerEntity && Helper.isHunter(cause.getDirectEntity())) {
            ItemStack weapon = ((PlayerEntity) cause.getDirectEntity()).getMainHandItem();
            if (!weapon.isEmpty() && weapon.getItem() instanceof IVampireFinisher) {
                dropSoul = true;
            }
        } else {
            dropSoul = false;//In case a previous death has been canceled somehow
        }
    }

    @Override
    public boolean doesResistGarlic(EnumStrength strength) {
        return !strength.isStrongerThan(garlicResist);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        if (canSuckBloodFromPlayer && !level.isClientSide && wantsBlood() && entity instanceof PlayerEntity && !Helper.isHunter(entity) && !UtilLib.canReallySee((LivingEntity) entity, this, true)) {
            int amt = VampirePlayer.getOpt((PlayerEntity) entity).map(v -> v.onBite(this)).orElse(0);
            drinkBlood(amt, IBloodStats.MEDIUM_SATURATION);
            VampirePlayer.getOpt((PlayerEntity) entity).ifPresent(v -> v.tryInfect(this));
            return true;
        }
        for (ItemStack e : entity.getArmorSlots()) {
            if (e != null && e.getItem() instanceof HunterCoatItem) {
                int j = 1;
                if (((HunterCoatItem) e.getItem()).getVampirismTier().equals(IItemWithTier.TIER.ENHANCED))
                    j = 2;
                else if (((HunterCoatItem) e.getItem()).getVampirismTier().equals(IItemWithTier.TIER.ULTIMATE))
                    j = 3;
                if (getRandom().nextInt((4 - j) * 2) == 0)
                    addEffect(new EffectInstance(ModEffects.POISON.get(), (int) (20 * Math.sqrt(j)), j));
            }
        }
        return super.doHurtTarget(entity);
    }

    @Override
    public EntityClassification getClassification(boolean forSpawnCount) {
        if (forSpawnCount && countAsMonsterForSpawn) {
            return EntityClassification.MONSTER;
        }
        return super.getClassification(forSpawnCount);
    }

    @Override
    public void drinkBlood(int amt, float saturationMod, boolean useRemaining) {
        this.addEffect(new EffectInstance(Effects.REGENERATION, amt * 20));
    }

    @Override
    public LivingEntity getRepresentingEntity() {
        return this;
    }

    @Nonnull
    @Override
    public EnumStrength isGettingGarlicDamage(IWorld iWorld, boolean forcerefresh) {
        if (forcerefresh) {
            garlicCache = Helper.getGarlicStrength(this, iWorld);
        }
        return garlicCache;
    }

    @Override
    public CreatureAttribute getMobType() {
        return VReference.VAMPIRE_CREATURE_ATTRIBUTE;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        if (vulnerableToFire) {
            if (DamageSource.IN_FIRE.equals(damageSource)) {
                return this.hurt(VReference.VAMPIRE_IN_FIRE, calculateFireDamage(amount));
            } else if (DamageSource.ON_FIRE.equals(damageSource)) {
                return this.hurt(VReference.VAMPIRE_ON_FIRE, calculateFireDamage(amount));
            }
        }
        return super.hurt(damageSource, amount);
    }

    @Override
    public boolean isGettingSundamage(IWorld iWorld, boolean forceRefresh) {
        if (!forceRefresh) return sundamageCache;
        return (sundamageCache = Helper.gettingSundamge(this, iWorld, this.level.getProfiler()));
    }

    @Override
    public boolean isIgnoringSundamage() {
        return this.hasEffect(ModEffects.SUNSCREEN.get());
    }

    /**
     * Select rules to consider for {@link #canSpawn(IWorld, SpawnReason)}
     */
    public void setSpawnRestriction(SpawnRestriction r) {
        this.spawnRestriction = r;
    }

    @Override
    public boolean useBlood(int amt, boolean allowPartial) {
        this.addEffect(new EffectInstance(Effects.WEAKNESS, amt * 20));
        return true;
    }

    @Override
    public boolean wantsBlood() {
        return false;
    }

    /**
     * Calculates the increased fire damage is this vampire creature is especially vulnerable to fire
     *
     * @param amount
     * @return
     */
    protected float calculateFireDamage(float amount) {
        return amount;
    }

    @Override
    protected void tickDeath() {
        if (this.deathTime == 19) {
            if (!this.level.isClientSide && (dropSoul && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT))) {
                this.level.addFreshEntity(new SoulOrbEntity(this.level, this.getX(), this.getY(), this.getZ(), SoulOrbEntity.VARIANT.VAMPIRE));
            }
        }
        super.tickDeath();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
    }

    /**
     * Checks if light level is low enough
     * Only exception is the vampire biome in which it returns true if ontop of {@link ModBlocks#cursed_earth}
     */
    private boolean getCanSpawnHereRestricted(IWorld iWorld) {
        boolean vampireBiome = ModBiomes.VAMPIRE_FOREST.get().getRegistryName().equals(Helper.getBiomeId(iWorld, this.blockPosition())) || ModBiomes.VAMPIRE_FOREST_HILLS.get().getRegistryName().equals(Helper.getBiomeId(iWorld, this.blockPosition()));
        if (!vampireBiome) return isLowLightLevel(iWorld);
        BlockState iblockstate = iWorld.getBlockState((this.blockPosition()).below());
        return iblockstate.is(ModTags.Blocks.CURSEDEARTH);
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

        int level;

        SpawnRestriction(int level) {
            this.level = level;
        }
    }
}
