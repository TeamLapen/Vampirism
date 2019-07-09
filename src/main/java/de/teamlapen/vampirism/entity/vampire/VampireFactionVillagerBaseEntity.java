package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.FactionVillagerEntity;
import de.teamlapen.vampirism.entity.ai.FleeSunVampireGoal;
import de.teamlapen.vampirism.entity.ai.RestrictSunVampireGoal;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.villager.IVillagerType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class VampireFactionVillagerBaseEntity extends FactionVillagerEntity implements IVampire {
    protected EnumStrength garlicResist = EnumStrength.NONE;
    private boolean sundamageCache;
    private EnumStrength garlicCache = EnumStrength.NONE;

    public VampireFactionVillagerBaseEntity(EntityType<? extends VampireFactionVillagerBaseEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public VampireFactionVillagerBaseEntity(EntityType<? extends VampireFactionVillagerBaseEntity> type, World worldIn, IVillagerType villagerType) {
        super(type, worldIn, villagerType);
    }

    @Override
    public boolean doesResistGarlic(EnumStrength strength) {
        return !strength.isStrongerThan(garlicResist);
    }

    @Override
    public void drinkBlood(int amt, float saturationMod, boolean useRemaining) {
        this.addPotionEffect(new EffectInstance(Effects.REGENERATION, amt * 20));
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
        return this.isPotionActive(ModPotions.sunscreen);
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
    public boolean useBlood(int amt, boolean allowPartial) {
        this.addPotionEffect(new EffectInstance(Effects.WEAKNESS, amt * 20));
        return true;
    }

    @Override
    public boolean wantsBlood() {
        return false;
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        getAttributeMap().registerAttribute(VReference.sunDamage).setBaseValue(Balance.mobProps.VAMPIRE_MOB_SUN_DAMAGE);
    }
    
    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new RestrictSunVampireGoal<>(this));
        this.tasks.addTask(1, new FleeSunVampireGoal<>(this, 0.9, false));
    	super.initEntityAI();
    }
}
