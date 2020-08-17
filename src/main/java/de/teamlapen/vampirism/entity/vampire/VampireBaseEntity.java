package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.api.items.IVampireFinisher;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.CrossbowArrowEntity;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.SoulOrbEntity;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.items.HunterCoatItem;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.world.gen.biome.VampireBiome;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Base class for Vampirism's vampire entities
 */
@SuppressWarnings("EntityConstructor")
public abstract class VampireBaseEntity extends VampirismEntity implements IVampireMob {

    public static boolean spawnPredicateVampire(EntityType<? extends VampirismEntity> entityType, IWorld world, SpawnReason spawnReason, BlockPos blockPos, Random random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && (spawnPredicateLight(world, blockPos, random) || spawnPredicateVampireFog(world, blockPos)) && spawnPredicateCanSpawn(entityType, world, spawnReason, blockPos, random);
    }

    private final boolean countAsMonsterForSpawn;

    /**
     * Rules to consider for {@link #canSpawn(IWorld, SpawnReason)}
     */
    private SpawnRestriction spawnRestriction = SpawnRestriction.NORMAL;
    protected EnumStrength garlicResist = EnumStrength.NONE;
    protected boolean canSuckBloodFromPlayer = false;
    protected boolean vulnerableToFire = true;
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
    public boolean attackEntityAsMob(Entity entity) {
        if (canSuckBloodFromPlayer && !world.isRemote && wantsBlood() && entity instanceof PlayerEntity && !Helper.isHunter(entity) && !UtilLib.canReallySee((LivingEntity) entity, this, true)) {
            int amt = VampirePlayer.getOpt((PlayerEntity) entity).map(v -> v.onBite(this)).orElse(0);
            drinkBlood(amt, IBloodStats.MEDIUM_SATURATION);
            return true;
        }
        for (ItemStack e : entity.getArmorInventoryList()) {
            if (e != null && e.getItem() instanceof HunterCoatItem) {
                int j = 1;
                if (((HunterCoatItem) e.getItem()).getVampirismTier().equals(IItemWithTier.TIER.ENHANCED))
                    j = 2;
                else if (((HunterCoatItem) e.getItem()).getVampirismTier().equals(IItemWithTier.TIER.ULTIMATE))
                    j = 3;
                if (getRNG().nextInt((4 - j) * 2) == 0)
                    addPotionEffect(new EffectInstance(ModEffects.poison, (int) (20 * Math.sqrt(j)), j));
            }
        }
        return super.attackEntityAsMob(entity);
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float amount) {
        if (vulnerableToFire) {
            if (DamageSource.IN_FIRE.equals(damageSource)) {
                return this.attackEntityFrom(VReference.VAMPIRE_IN_FIRE, calculateFireDamage(amount));
            } else if (DamageSource.ON_FIRE.equals(damageSource)) {
                return this.attackEntityFrom(VReference.VAMPIRE_ON_FIRE, calculateFireDamage(amount));
            }
        }
        return super.attackEntityFrom(damageSource, amount);
    }

    @Override
    public EntityClassification getClassification(boolean forSpawnCount) {
        if (forSpawnCount && countAsMonsterForSpawn) {
            return EntityClassification.MONSTER;
        }
        return super.getClassification(forSpawnCount);
    }

    @Override
    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        if (spawnRestriction.level >= SpawnRestriction.SIMPLE.level) {
            if (isGettingSundamage(worldIn, true) || isGettingGarlicDamage(worldIn, true) != EnumStrength.NONE)
                return false;
            if (spawnRestriction.level >= SpawnRestriction.NORMAL.level) {
                if (worldIn.getDimension().isDaytime() && rand.nextInt(5) != 0) {
                    return false;
                }
                if (this.world.isBlockPresent(getPosition()) && worldIn instanceof ServerWorld) {
                    BlockPos nearestVillage = ((ServerWorld) worldIn).findNearestStructure("Village", getPosition(), 1, false);
                    if (nearestVillage != null && nearestVillage.withinDistance(getPosition(), 50)) {
                        if (getRNG().nextInt(60) != 0) {
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

        return super.canSpawn(worldIn, spawnReasonIn);
    }

    @Override
    public boolean doesResistGarlic(EnumStrength strength) {
        return !strength.isStrongerThan(garlicResist);
    }

    @Override
    public void drinkBlood(int amt, float saturationMod, boolean useRemaining) {
        this.addPotionEffect(new EffectInstance(Effects.REGENERATION, amt * 20));
    }

    @Override
    public CreatureAttribute getCreatureAttribute() {
        return VReference.VAMPIRE_CREATURE_ATTRIBUTE;
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
    public boolean isGettingSundamage(IWorld iWorld, boolean forceRefresh) {
        if (!forceRefresh) return sundamageCache;
        return (sundamageCache = Helper.gettingSundamge(this, iWorld, this.world.getProfiler()));
    }

    @Override
    public boolean isIgnoringSundamage() {
        return this.isPotionActive(ModEffects.sunscreen);
    }

    @Override
    public void livingTick() {
        if (this.ticksExisted % REFERENCE.REFRESH_GARLIC_TICKS == 3) {
            isGettingGarlicDamage(world, true);
        }
        if (this.ticksExisted % REFERENCE.REFRESH_SUNDAMAGE_TICKS == 2) {
            isGettingSundamage(world, true);
        }
        if (!world.isRemote) {
            if (isGettingSundamage(world) && ticksExisted % 40 == 11) {
                double dmg = getAttribute(VReference.sunDamage).getValue();
                if (dmg > 0) this.attackEntityFrom(VReference.SUNDAMAGE, (float) dmg);
            }
            if (isGettingGarlicDamage(world) != EnumStrength.NONE) {
                DamageHandler.affectVampireGarlicAmbient(this, isGettingGarlicDamage(world), this.ticksExisted);
            }
        }
        if (!this.world.isRemote) {
            if (isAlive() && isInWater()) {
                setAir(300);
                if (ticksExisted % 16 == 4) {
                    addPotionEffect(new EffectInstance(Effects.WEAKNESS, 80, 0));
                }
            }
        }
        super.livingTick();
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        if (cause.getImmediateSource() instanceof CrossbowArrowEntity && Helper.isHunter(cause.getTrueSource())) {
            dropSoul = true;
        } else if (cause.getImmediateSource() instanceof PlayerEntity && Helper.isHunter(cause.getImmediateSource())) {
            ItemStack weapon = ((PlayerEntity) cause.getImmediateSource()).getHeldItemMainhand();
            if (!weapon.isEmpty() && weapon.getItem() instanceof IVampireFinisher) {
                dropSoul = true;
            }
        } else {
            dropSoul = false;//In case a previous death has been canceled somehow
        }
    }

    /**
     * Select rules to consider for {@link #canSpawn(IWorld, SpawnReason)}
     */
    public void setSpawnRestriction(SpawnRestriction r) {
        this.spawnRestriction = r;
    }

    @Override
    public boolean useBlood(int amt, boolean allowPartial) {
        this.addPotionEffect(new EffectInstance(Effects.WEAKNESS, amt * 20));
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
    protected void onDeathUpdate() {
        if (this.deathTime == 19) {
            if (!this.world.isRemote && (dropSoul && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT))) {
                this.world.addEntity(new SoulOrbEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ(), SoulOrbEntity.VARIANT.VAMPIRE));
            }
        }
        super.onDeathUpdate();
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        getAttributes().registerAttribute(VReference.sunDamage).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_MOB_SUN_DAMAGE);
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
        boolean vampireBiome = iWorld.getBiome(this.getPosition()) instanceof VampireBiome;
        if (!vampireBiome) return isLowLightLevel(iWorld);
        BlockState iblockstate = iWorld.getBlockState((new BlockPos(this)).down());
        return ModBlocks.cursed_earth.equals(iblockstate.getBlock());
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
