package de.teamlapen.vampirism.entity.minion;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.goals.ForceLookEntityGoal;
import de.teamlapen.vampirism.entity.goals.LookAtClosestVisibleGoal;
import de.teamlapen.vampirism.entity.goals.RestrictSunVampireGoal;
import de.teamlapen.vampirism.entity.minion.goals.DefendAreaGoal;
import de.teamlapen.vampirism.entity.minion.goals.FollowLordGoal;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.entity.minion.management.MinionTask;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;


public class VampireMinionEntity extends MinionEntity<VampireMinionEntity.VampireMinionData> implements IVampire {

    static {
        MinionData.registerDataType(VampireMinionEntity.VampireMinionData.ID, VampireMinionEntity.VampireMinionData::new);
        MinionTask.init();
    }

    /**
     * Just required to execute static init
     */
    public static void init() {

    }


    private boolean sundamageCache;
    private EnumStrength garlicCache = EnumStrength.NONE;

    public VampireMinionEntity(EntityType<? extends VampirismEntity> type, World world) {
        super(type, world, VampirismAPI.factionRegistry().getPredicate(VReference.VAMPIRE_FACTION, true, true, true, false, null));
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

    @Override
    public void activateTask(MinionTask.Type type) {

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
        return this.getMinionData().map(d -> d.type).map(t -> Math.max(0, t)).orElse(0);
    }

    public void setVampireType(int type) {
        getMinionData().ifPresent(d -> d.type = type);
    }

    public boolean shouldRenderLordSkin() {
        return this.getMinionData().map(d -> d.type).orElse(0) < 0;
    }

    @Override
    public boolean isIgnoringSundamage() {
        return this.isPotionActive(ModEffects.sunscreen);
    }

    @Override
    protected void onMinionDataReceived(@Nonnull VampireMinionData data) {
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
    public List<MinionTask.Type> getAvailableTasks() {
        return Lists.newArrayList(MinionTask.Type.FOLLOW, MinionTask.Type.DEFEND_AREA, MinionTask.Type.STAY);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new ForceLookEntityGoal<>(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(3, new RestrictSunVampireGoal<>(this));

        this.goalSelector.addGoal(4, new FollowLordGoal(this, 1.1, 5, 10));

        this.goalSelector.addGoal(10, new LookAtClosestVisibleGoal(this, PlayerEntity.class, 20F, 0.6F));
        this.goalSelector.addGoal(10, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new DefendAreaGoal(this));

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
            super(maxHealth, name, 9);
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
        public void serializeNBT(CompoundNBT tag) {
            super.serializeNBT(tag);
            tag.putInt("vampire_type", type);
        }

        @Override
        protected ResourceLocation getDataType() {
            return ID;
        }
    }
}
