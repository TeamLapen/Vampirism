package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModBiomes;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.EntityVampirism;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Base class for Vampirism's vampire entities
 */
@SuppressWarnings("EntityConstructor")
public abstract class EntityVampireBase extends EntityVampirism implements IVampireMob {
    private final boolean countAsMonsterForSpawn;
    /**
     * If this creature is only allowed to spawn at low light level or in the vampire biome on cursed earth
     */
    protected boolean restrictedSpawn = false;
    protected EnumStrength garlicResist = EnumStrength.NONE;
    protected boolean canSuckBloodFromPlayer = false;
    protected boolean vulnerableToFire = true;
    private boolean sundamageCache;
    private EnumStrength garlicCache = EnumStrength.NONE;


    /**
     * @param countAsMonsterForSpawn If this entity should be counted as vampire and as monster during spawning
     */
    public EntityVampireBase(World world, boolean countAsMonsterForSpawn) {
        super(world);
        this.countAsMonsterForSpawn = countAsMonsterForSpawn;

    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        if (canSuckBloodFromPlayer && !world.isRemote && entity instanceof EntityPlayer && !UtilLib.canReallySee((EntityLivingBase) entity, this, true) && rand.nextInt(Balance.mobProps.VAMPIRE_BITE_ATTACK_CHANCE) == 0) {
            int amt = VampirePlayer.get((EntityPlayer) entity).onBite(this);
            drinkBlood(amt, 1.0F);
            return true;
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
    public boolean doesResistGarlic(EnumStrength strength) {
        return !strength.isStrongerThan(garlicResist);
    }

    @Override
    public void drinkBlood(int amt, float saturationMod) {
        this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, amt * 20));
    }

    @Override
    public boolean getCanSpawnHere() {
        if (isGettingSundamage(true) || (world.isDaytime() && rand.nextInt(5) != 0)) return false;
        if (isGettingGarlicDamage(true) != EnumStrength.NONE) return false;
        if (world.getVillageCollection().getNearestVillage(getPosition(), 1) != null) {
            if (getRNG().nextInt(60) != 0) {
                return false;
            }
        }
        return super.getCanSpawnHere() && (!restrictedSpawn || getCanSpawnHereRestricted());
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return VReference.VAMPIRE_CREATURE_ATTRIBUTE;
    }

    @Override
    public IFaction getFaction() {
        return VReference.VAMPIRE_FACTION;
    }

    @Override
    public EntityLivingBase getRepresentingEntity() {
        return this;
    }

    @Override
    public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount) {
        if (forSpawnCount && countAsMonsterForSpawn && type == EnumCreatureType.MONSTER) return true;
        return super.isCreatureType(type, forSpawnCount);
    }

    @Override
    public EnumStrength isGettingGarlicDamage() {
        return isGettingGarlicDamage(false);
    }

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
    public boolean isGettingSundamage() {
        return isGettingSundamage(false);
    }

    @Override
    public boolean isIgnoringSundamage() {
        return this.isPotionActive(ModPotions.sunscreen);
    }

    @Override
    public void onLivingUpdate() {
        if (this.ticksExisted % REFERENCE.REFRESH_GARLIC_TICKS == 3) {
            isGettingGarlicDamage(true);
        }
        if (this.ticksExisted % REFERENCE.REFRESH_SUNDAMAGE_TICKS == 2) {
            isGettingSundamage(true);
        }
        if (!world.isRemote) {
            if (isGettingSundamage() && ticksExisted % 40 == 11) {
                double dmg = getEntityAttribute(VReference.sunDamage).getAttributeValue();
                if (dmg > 0) this.attackEntityFrom(VReference.SUNDAMAGE, (float) dmg);
            }
            if (isGettingGarlicDamage() != EnumStrength.NONE) {
                DamageHandler.affectVampireGarlicAmbient(this, isGettingGarlicDamage(), this.ticksExisted);
            }
        }
        super.onLivingUpdate();
    }

    @Override
    public boolean wantsBlood() {
        return false;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getAttributeMap().registerAttribute(VReference.sunDamage).setBaseValue(Balance.mobProps.VAMPIRE_MOB_SUN_DAMAGE);
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
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
    }

    /**
     * Checks if light level is low enough
     * Only exception is the vampire biome in which it returns true if ontop of {@link ModBlocks#cursed_earth}
     */
    private boolean getCanSpawnHereRestricted() {
        boolean vampireBiome = ModBiomes.vampireForest.equals(this.world.getBiome(this.getPosition()));
        if (!vampireBiome) return isLowLightLevel();
        IBlockState iblockstate = this.world.getBlockState((new BlockPos(this)).down());
        return ModBlocks.cursed_earth.equals(iblockstate.getBlock());
    }
}
