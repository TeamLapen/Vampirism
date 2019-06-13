package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.api.items.IVampireFinisher;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.EntityCrossbowArrow;
import de.teamlapen.vampirism.entity.EntitySoulOrb;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityConvertedHorse extends EntityHorse implements IConvertedCreature<EntityHorse> {

    private EnumStrength garlicCache = EnumStrength.NONE;
    private EntityHorse entityCreature;
    private boolean sundamageCache;
    private boolean dropSoul = false;
    protected boolean vulnerableToFire = true;

    public EntityConvertedHorse(World world) {
        super(world);
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return VReference.VAMPIRE_CREATURE_ATTRIBUTE;
    }

    @Override
    public float getEyeHeight() {
        return height * 0.875f;
    }

    @Override
    public EntityLivingBase getRepresentingEntity() {
        return this;
    }

    @Override
    public String getName() {
        return UtilLib.translate("entity.vampirism.vampire.name") + " " + UtilLib.translate("entity.Horse.name");
    }

    @Override
    public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount) {
        if (forSpawnCount) return true;
        return super.isCreatureType(type, forSpawnCount);
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        getConvertedHelper().dropConvertedItems(this, entityCreature, wasRecentlyHit, lootingModifier);
    }

    /**
     * @return The {@link de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler.IDefaultHelper} for this creature
     */
    protected IConvertingHandler.IDefaultHelper getConvertedHelper() {
        IConvertingHandler handler = VampirismAPI.entityRegistry().getEntry(entityCreature).convertingHandler;
        if (handler instanceof ConvertingHandler) {
            return ((ConvertingHandler) handler).getHelper();
        }
        return null;
    }

    @Override
    protected boolean canDespawn() {
        return !isTame();
    }

    @Override
    public boolean doesResistGarlic(EnumStrength strength) {
        return !strength.isStrongerThan(EnumStrength.NONE);
    }

    @Override
    public void drinkBlood(int amt, float saturationMod, boolean useRemaining) {
        this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, amt * 20));
    }

    @Override
    public EnumStrength isGettingGarlicDamage(boolean forceRefresh) {
        if (forceRefresh) {
            garlicCache = Helper.getGarlicStrength(this);
        }
        return garlicCache;
    }

    @Override
    public boolean isGettingSundamage(boolean forceRefresh) {
        if (!forceRefresh)
            return sundamageCache;
        return (sundamageCache = Helper.gettingSundamge(this));
    }

    @Override
    public boolean isIgnoringSundamage() {
        return this.isPotionActive(ModPotions.sunscreen);
    }

    @Override
    public boolean useBlood(int amt, boolean allowPartial) {
        this.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, amt * 20));
        return true;
    }

    @Override
    public boolean wantsBlood() {
        return false;
    }

    @Override
    public void onLivingUpdate() {
        if (this.ticksExisted % REFERENCE.REFRESH_GARLIC_TICKS == 1) {
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
            if (isEntityAlive() && isInWater()) {
                setAir(300);
                if (ticksExisted % 16 == 4) {
                    addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 80, 0));
                }
            }
        }
        super.onLivingUpdate();
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        if (cause.getImmediateSource() instanceof EntityCrossbowArrow && Helper.isHunter(cause.getTrueSource())) {
            dropSoul = true;
        } else if (cause.getImmediateSource() instanceof EntityPlayer && Helper.isHunter(cause.getImmediateSource())) {
            ItemStack weapon = ((EntityPlayer) cause.getImmediateSource()).getHeldItemMainhand();
            if (!weapon.isEmpty() && weapon.getItem() instanceof IVampireFinisher) {
                dropSoul = true;
            }
        } else {
            dropSoul = false;//In case a previous death has been canceled somehow
        }
    }

    @Override
    protected void onDeathUpdate() {
        if (this.deathTime == 19) {
            if (!this.world.isRemote && (dropSoul && this.world.getGameRules().getBoolean("doMobLoot"))) {
                this.world.spawnEntity(new EntitySoulOrb(this.world, this.posX, this.posY, this.posZ, EntitySoulOrb.TYPE.VAMPIRE));
            }
        }
        super.onDeathUpdate();
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

        this.tasks.addTask(1, new EntityAIAvoidEntity<>(this, EntityCreature.class, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, VReference.HUNTER_FACTION), 10, 1.0, 1.1));
        this.tasks.addTask(4, new EntityAIRestrictSun(this));
        this.experienceValue = 2;

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, true, false, null)));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityCreature.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getAttributeMap().registerAttribute(VReference.sunDamage).setBaseValue(Balance.mobProps.VAMPIRE_MOB_SUN_DAMAGE);
    }

    protected void updateEnttityAttributes() {
        IConvertingHandler.IDefaultHelper helper = getConvertedHelper();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(helper.getConvertedMaxHealth(entityCreature));
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(helper.getConvertedKnockbackResistance(entityCreature));
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(helper.getConvertedSpeed(entityCreature));
    }

    protected void setEntityCreature(EntityHorse creature) {
        entityCreature = creature;
    }

    public static class ConvertingHandler extends DefaultConvertingHandler<EntityHorse> {
        public ConvertingHandler() {
            super(null);
        }

        @Override
        public IConvertedCreature<EntityHorse> createFrom(EntityHorse entity) {
            EntityConvertedHorse converted = new EntityConvertedHorse(entity.world);
            copyImportantStuff(converted, entity);
            converted.setUniqueId(MathHelper.getRandomUUID(converted.rand));
            converted.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 200, 2));
            return converted;
        }

        protected void copyImportantStuff(EntityConvertedHorse converted, EntityHorse entity) {
            NBTTagCompound nbt = new NBTTagCompound();
            entity.writeToNBT(nbt);
            converted.readFromNBT(nbt);
            converted.setEntityCreature(entity);
            converted.updateEnttityAttributes();
            converted.setHealth(converted.getMaxHealth() / 3 * 2);
        }
    }
}
