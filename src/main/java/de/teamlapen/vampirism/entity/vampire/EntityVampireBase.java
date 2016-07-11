package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.EnumGarlicStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.EntityVampirism;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

/**
 * Base class for Vampirism's vampire entities
 */
public abstract class EntityVampireBase extends EntityVampirism implements IVampireMob {
    private final boolean countAsMonster;
    protected EnumGarlicStrength garlicResist = EnumGarlicStrength.NONE;
    protected boolean canSuckBloodFromPlayer = false;
    private boolean sundamageCache;
    private EnumGarlicStrength garlicCache = EnumGarlicStrength.NONE;


    public EntityVampireBase(World world, boolean countAsMonster) {
        super(world);
        this.countAsMonster = countAsMonster;

    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        if (canSuckBloodFromPlayer && !worldObj.isRemote && entity instanceof EntityPlayer && !UtilLib.canReallySee((EntityLivingBase) entity, this, true) && rand.nextInt(Balance.mobProps.VAMPIRE_BITE_ATTACK_CHANCE) == 0) {
            int amt = VampirePlayer.get((EntityPlayer) entity).onBite(this);
            drinkBlood(amt, 1.0F);
            return true;
        }
        return super.attackEntityAsMob(entity);
    }

    @Override
    public boolean doesResistGarlic(EnumGarlicStrength strength) {
        return !strength.isStrongerThan(garlicResist);
    }

    @Override
    public void drinkBlood(int amt, float saturationMod) {
        this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, amt * 20));
    }

    @Override
    public boolean getCanSpawnHere() {
        if (isGettingSundamage(true) || (worldObj.isDaytime() && rand.nextInt(5) != 0)) return false;
        if (isGettingGarlicDamage(true) != EnumGarlicStrength.NONE) return false;
        if (worldObj.getVillageCollection().getNearestVillage(getPosition(), 1) != null) {
            return getRNG().nextInt(60) == 0 && super.getCanSpawnHere();
        }
        return super.getCanSpawnHere();
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
        if (forSpawnCount && countAsMonster && type == EnumCreatureType.MONSTER) return true;
        return super.isCreatureType(type, forSpawnCount);
    }

    @Override
    public EnumGarlicStrength isGettingGarlicDamage() {
        return isGettingGarlicDamage(false);
    }

    @Override
    public EnumGarlicStrength isGettingGarlicDamage(boolean forcerefresh) {
        if (forcerefresh) {
            garlicCache = Helper.gettingGarlicDamage(this);
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
    public void onLivingUpdate() {
        if (this.ticksExisted % REFERENCE.REFRESH_GARLIC_TICKS == 3) {
            isGettingGarlicDamage(true);
        }
        if (this.ticksExisted % REFERENCE.REFRESH_SUNDAMAGE_TICKS == 2) {
            isGettingSundamage(true);
        }
        if (!worldObj.isRemote) {
            if (isGettingSundamage() && ticksExisted % 40 == 11) {
                double dmg = getEntityAttribute(VReference.sunDamage).getAttributeValue();
                if (dmg > 0) this.attackEntityFrom(VReference.SUNDAMAGE, (float) dmg);
            }
            //TODO handle garlic
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
        getAttributeMap().registerAttribute(VReference.garlicDamage).setBaseValue(Balance.mobProps.VAMPIRE_MOB_GARLIC_DAMAGE);

    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
    }

}
