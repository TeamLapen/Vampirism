package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.vampirism.api.EnumGarlicStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.Faction;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.EntityVampirism;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

/**
 * Base class for Vampirism's vampire entities
 */
public abstract class EntityVampireBase extends EntityVampirism implements IVampireMob {
    private final boolean countAsMonster;
    private boolean sundamageCache;
    private EnumGarlicStrength garlicCache = EnumGarlicStrength.NONE;
    private EnumGarlicStrength garlicResist = EnumGarlicStrength.NONE;


    public EntityVampireBase(World world, boolean countAsMonster) {
        super(world);
        this.countAsMonster = countAsMonster;
        ((PathNavigateGround) getNavigator()).setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));


    }

    @Override
    public void consumeBlood(int amt, float saturationMod) {
        this.addPotionEffect(new PotionEffect(Potion.regeneration.id, amt * 20));
    }

    @Override
    public boolean doesResistGarlic(EnumGarlicStrength strength) {
        return !strength.isStrongerThan(garlicResist);
    }

    @Override
    public boolean getCanSpawnHere() {
        if (isGettingSundamage(true)) return false;
        if (isGettingGarlicDamage(true) != EnumGarlicStrength.NONE) return false;
        return super.getCanSpawnHere();
    }

    @Override
    public Faction getFaction() {
        return VReference.VAMPIRE_FACTION;
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
            if (isGettingSundamage() && ticksExisted % 20 == 11) {
                float dmg = getSundamage();
                this.attackEntityFrom(VReference.sundamage, dmg);
            }
        }
        super.onLivingUpdate();
    }

    @Override
    public boolean wantsBlood() {
        return false;
    }

    protected float getSundamage() {
        return (float) Balance.mobProps.VAMPIRE_MOB_SUN_DAMAGE;
    }
}
