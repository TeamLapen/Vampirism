package de.teamlapen.vampirism.entity.minion;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nonnull;


public class VampireMinionEntity extends MinionEntity<VampireMinionEntity.VampireMinionData> implements IVampire {
    private static final DataParameter<Integer> TYPE = EntityDataManager.createKey(VampireMinionEntity.class, DataSerializers.VARINT);

    static {
        MinionData.registerDataType(VampireMinionEntity.VampireMinionData.ID, VampireMinionEntity.VampireMinionData::new);
    }

    /**
     * Just required to execute static init
     */
    public static void init() {

    }


    private boolean sundamageCache;
    private EnumStrength garlicCache = EnumStrength.NONE;

    public VampireMinionEntity(EntityType<? extends VampirismEntity> type, World world) {
        super(type, world);
    }

    @Override
    public boolean doesResistGarlic(EnumStrength strength) {
        return false;
    }

    @Override
    public void drinkBlood(int amt, float saturationMod, boolean useRemaining) {

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

    public int getVampireType() {
        return Math.max(0, this.dataManager.get(TYPE));
    }

    public void setVampireType(int type) {
        if (this.minionData != null) {
            this.minionData.type = type;
        }
        this.dataManager.set(TYPE, type);
    }

    public boolean shouldRenderLordSkin() {
        return this.getDataManager().get(TYPE) < 0;
    }

    @Override
    public boolean isIgnoringSundamage() {
        return this.isPotionActive(ModEffects.sunscreen);
    }

    @Override
    protected void onMinionDataReceived(@Nonnull VampireMinionData data) {
        this.dataManager.set(TYPE, data.type);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(TYPE, 0);

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
    public boolean useBlood(int amt, boolean allowPartial) {
        return false;
    }

    @Override
    public boolean wantsBlood() {
        return false;
    }

    public static class VampireMinionData extends MinionData {
        public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "vampire");

        private int type;

        public VampireMinionData(int maxHealth, ITextComponent name, int type) {
            super(maxHealth, name);
            this.type = type;
        }

        private VampireMinionData() {
            super();
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            super.deserializeNBT(nbt);
            type = nbt.getInt("vampire_type");
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT tag = super.serializeNBT();
            tag.putInt("vampire_type", type);
            return tag;
        }

        @Override
        protected ResourceLocation getDataType() {
            return ID;
        }
    }
}
