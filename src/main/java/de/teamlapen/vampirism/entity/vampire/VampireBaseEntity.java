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
import de.teamlapen.vampirism.core.ModAttributes;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.entity.CrossbowArrowEntity;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.SoulOrbEntity;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.items.HunterCoatItem;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

/**
 * Base class for Vampirism's vampire entities
 */
public abstract class VampireBaseEntity extends VampirismEntity implements IVampireMob, Npc/*mainly for JourneyMap*/ {

    public static boolean spawnPredicateVampire(EntityType<? extends VampirismEntity> entityType, ServerLevelAccessor world, MobSpawnType spawnReason, BlockPos blockPos, RandomSource random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && (Monster.isDarkEnoughToSpawn(world, blockPos, random) || spawnPredicateVampireFog(world, blockPos)) && Mob.checkMobSpawnRules(entityType, world, spawnReason, blockPos, random);
    }

    public static AttributeSupplier.Builder getAttributeBuilder() {
        return VampirismEntity.getAttributeBuilder().add(ModAttributes.SUNDAMAGE.get(), BalanceMobProps.mobProps.VAMPIRE_MOB_SUN_DAMAGE);
    }

    private final boolean countAsMonsterForSpawn;
    protected EnumStrength garlicResist = EnumStrength.NONE;
    protected boolean canSuckBloodFromPlayer = false;
    protected boolean vulnerableToFire = true;
    /**
     * Rules to consider for {@link #checkSpawnRules(LevelAccessor, MobSpawnType)}
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
    public VampireBaseEntity(EntityType<? extends VampireBaseEntity> type, Level world, boolean countAsMonsterForSpawn) {
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
            if (isGettingSundamage(level) && tickCount % 40 == 11) {
                double dmg = getAttribute(ModAttributes.SUNDAMAGE.get()).getValue();
                if (dmg > 0) this.hurt(VReference.SUNDAMAGE, (float) dmg);
            }
            if (isGettingGarlicDamage(level) != EnumStrength.NONE) {
                DamageHandler.affectVampireGarlicAmbient(this, isGettingGarlicDamage(level), this.tickCount);
            }
        }
        if (!this.level.isClientSide) {
            if (isAlive() && isInWater()) {
                setAirSupply(300);
                if (tickCount % 16 == 4) {
                    addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0));
                }
            }
        }
        super.aiStep();
    }

    @Override
    public boolean checkSpawnRules(@Nonnull LevelAccessor worldIn, @Nonnull MobSpawnType spawnReasonIn) {
        if (spawnRestriction.level >= SpawnRestriction.SIMPLE.level) {
            if (isGettingSundamage(worldIn, true) || isGettingGarlicDamage(worldIn, true) != EnumStrength.NONE)
                return false;
            if (spawnRestriction.level >= SpawnRestriction.NORMAL.level) {
                if (-worldIn.getPathfindingCostFromLightLevels(blockPosition()) < 0.0 && random.nextInt(5) != 0) {
                    return false;
                }
                if (this.level.isLoaded(blockPosition()) && worldIn instanceof ServerLevel) { //TODO check performance
                    if (UtilLib.getStructureStartAt(level, blockPosition(), StructureTags.VILLAGE).isPresent()) {
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
    public void die(@Nonnull DamageSource cause) {
        super.die(cause);
        if (cause.getDirectEntity() instanceof CrossbowArrowEntity && Helper.isHunter(cause.getEntity())) {
            dropSoul = true;
        } else if (cause.getDirectEntity() instanceof Player && Helper.isHunter(cause.getDirectEntity())) {
            ItemStack weapon = ((Player) cause.getDirectEntity()).getMainHandItem();
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
    public boolean doHurtTarget(@Nonnull Entity entity) {
        if (canSuckBloodFromPlayer && !level.isClientSide && wantsBlood() && entity instanceof Player && !Helper.isHunter(entity) && !UtilLib.canReallySee((LivingEntity) entity, this, true)) {
            int amt = VampirePlayer.getOpt((Player) entity).map(v -> v.onBite(this)).orElse(0);
            drinkBlood(amt, IBloodStats.MEDIUM_SATURATION);
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
                    addEffect(new MobEffectInstance(ModEffects.POISON.get(), (int) (20 * Math.sqrt(j)), j));
            }
        }
        return super.doHurtTarget(entity);
    }

    @Override
    public MobCategory getClassification(boolean forSpawnCount) {
        if (forSpawnCount && countAsMonsterForSpawn) {
            return MobCategory.MONSTER;
        }
        return super.getClassification(forSpawnCount);
    }

    @Override
    public void drinkBlood(int amt, float saturationMod, boolean useRemaining) {
        this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, amt * 20));
    }

    @Override
    public LivingEntity getRepresentingEntity() {
        return this;
    }

    @Nonnull
    @Override
    public EnumStrength isGettingGarlicDamage(LevelAccessor iWorld, boolean forcerefresh) {
        if (forcerefresh) {
            garlicCache = Helper.getGarlicStrength(this, iWorld);
        }
        return garlicCache;
    }

    @Nonnull
    @Override
    public MobType getMobType() {
        return VReference.VAMPIRE_CREATURE_ATTRIBUTE;
    }

    @Override
    public boolean hurt(@Nonnull DamageSource damageSource, float amount) {
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
    public boolean isGettingSundamage(LevelAccessor iWorld, boolean forceRefresh) {
        if (!forceRefresh) return sundamageCache;
        return (sundamageCache = Helper.gettingSundamge(this, iWorld, this.level.getProfiler()));
    }

    @Override
    public boolean isIgnoringSundamage() {
        return this.hasEffect(ModEffects.SUNSCREEN.get());
    }

    /**
     * Select rules to consider for {@link #checkSpawnRules(LevelAccessor, MobSpawnType)}
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
            if (!this.level.isClientSide && (dropSoul && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT))) {
                this.level.addFreshEntity(new SoulOrbEntity(this.level, this.getX(), this.getY(), this.getZ(), SoulOrbEntity.VARIANT.VAMPIRE));
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
    private boolean getCanSpawnHereRestricted(LevelAccessor iWorld) {
        boolean vampireBiome = iWorld.getBiome(this.blockPosition()).is(ModTags.Biomes.IS_VAMPIRE_BIOME);
        boolean lowLightLevel = isLowLightLevel(iWorld);
        if(lowLightLevel) return true;
        if(!vampireBiome) return false;
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

        final int level;

        SpawnRestriction(int level) {
            this.level = level;
        }
    }
}
