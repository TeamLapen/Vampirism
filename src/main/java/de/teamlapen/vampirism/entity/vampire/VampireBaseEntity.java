package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.api.items.IVampireFinisher;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModBiomes;
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
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Base class for Vampirism's vampire entities
 */
@SuppressWarnings("EntityConstructor")
public abstract class VampireBaseEntity extends VampirismEntity implements IVampireMob {

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
        if (canSuckBloodFromPlayer && !world.isRemote && entity instanceof PlayerEntity && !UtilLib.canReallySee((LivingEntity) entity, this, true) && rand.nextInt(Balance.mobProps.VAMPIRE_BITE_ATTACK_CHANCE) == 0) {
            int amt = VampirePlayer.get((PlayerEntity) entity).onBite(this);
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
            if (isGettingSundamage(true) || isGettingGarlicDamage(true) != EnumStrength.NONE) return false;
            if (spawnRestriction.level >= SpawnRestriction.NORMAL.level) {
                if (spawnReasonIn != SpawnReason.CHUNK_GENERATION) {

                    if (this.world.isDaytime() && rand.nextInt(5) != 0) {
                        return false;
                    }
                    if (this.world.findNearestStructure("Village", getPosition(), 1, false) != null) {
                        if (getRNG().nextInt(60) != 0) {
                            return false;
                        }
                    }

                    if (spawnRestriction.level >= SpawnRestriction.SPECIAL.level) {
                        if (!getCanSpawnHereRestricted()) {
                            return false;
                        }
                    }
                } else {
                    //Must not do anything that affect this.world chunks during chunk gen
                    return false; //Just don't spawn during world gen
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
    public EnumStrength isGettingGarlicDamage(boolean forcerefresh) {
        if (forcerefresh) {
            garlicCache = Helper.getGarlicStrength(this);
        }
        return garlicCache;
    }

    @Override
    public boolean isGettingSundamage(boolean forceRefresh) {
        if (!forceRefresh) return sundamageCache;
        return (sundamageCache = Helper.gettingSundamge(this));
    }

    @Override
    public boolean isIgnoringSundamage() {
        return this.isPotionActive(ModEffects.sunscreen);
    }

    @Override
    public void livingTick() {
        if (this.ticksExisted % REFERENCE.REFRESH_GARLIC_TICKS == 3) {
            isGettingGarlicDamage(true);
        }
        if (this.ticksExisted % REFERENCE.REFRESH_SUNDAMAGE_TICKS == 2) {
            isGettingSundamage(true);
        }
        if (!world.isRemote) {
            if (isGettingSundamage() && ticksExisted % 40 == 11) {
                double dmg = getAttribute(VReference.sunDamage).getValue();
                if (dmg > 0) this.attackEntityFrom(VReference.SUNDAMAGE, (float) dmg);
            }
            if (isGettingGarlicDamage() != EnumStrength.NONE) {
                DamageHandler.affectVampireGarlicAmbient(this, isGettingGarlicDamage(), this.ticksExisted);
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
                this.world.addEntity(new SoulOrbEntity(this.world, this.posX, this.posY, this.posZ, SoulOrbEntity.VARIANT.VAMPIRE));
            }
        }
        super.onDeathUpdate();
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        getAttributes().registerAttribute(VReference.sunDamage).setBaseValue(Balance.mobProps.VAMPIRE_MOB_SUN_DAMAGE);
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
    private boolean getCanSpawnHereRestricted() {
        if (!this.world.isBlockPresent(this.getPosition())) return false;
        boolean vampireBiome = ModBiomes.vampire_forest.equals(this.world.getBiome(this.getPosition()));
        if (!vampireBiome) return isLowLightLevel();
        BlockState iblockstate = this.world.getBlockState((new BlockPos(this)).down());
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
