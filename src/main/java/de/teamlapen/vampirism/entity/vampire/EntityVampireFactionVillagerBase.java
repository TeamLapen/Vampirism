package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.EntityFactionVillager;
import de.teamlapen.vampirism.entity.ai.VampireAIFleeSun;
import de.teamlapen.vampirism.entity.ai.VampireAIRestrictSun;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import javax.annotation.Nonnull;

public class EntityVampireFactionVillagerBase extends EntityFactionVillager implements IVampire {
    protected EnumStrength garlicResist = EnumStrength.NONE;
    private boolean sundamageCache;
    private EnumStrength garlicCache = EnumStrength.NONE;

    public EntityVampireFactionVillagerBase(World worldIn) {
        super(worldIn);
    }

    public EntityVampireFactionVillagerBase(World worldIn, int professionId) {
        super(worldIn, professionId);
    }

    @Override
    public boolean doesResistGarlic(EnumStrength strength) {
        return !strength.isStrongerThan(garlicResist);
    }

    @Override
    public void drinkBlood(int amt, float saturationMod, boolean useRemaining) {
        this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, amt * 20));
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
        if (!this.world.isRemote) {
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
    public boolean useBlood(int amt, boolean allowPartial) {
        this.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, amt * 20));
        return true;
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
    
    @Override
    protected void initEntityAI() {
    	this.tasks.addTask(1, new VampireAIRestrictSun(this));
        this.tasks.addTask(1, new VampireAIFleeSun(this, 0.9, false));
    	super.initEntityAI();
    }
}
