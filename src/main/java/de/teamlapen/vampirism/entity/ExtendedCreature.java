package de.teamlapen.vampirism.entity;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.BiteableEntry;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.effects.SanguinareEffect;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Extended entity property which every {@link PathfinderMob} has
 */
public class ExtendedCreature implements ISyncable.ISyncableEntityCapabilityInst, IExtendedCreatureVampirism {

    private final static String KEY_BLOOD = "bloodLevel";
    private final static String KEY_MAX_BLOOD = "maxBlood";
    private final static String POISONOUS_BLOOD = "poisonousBlood";
    public static final Capability<IExtendedCreatureVampirism> CAP = CapabilityManager.get(new CapabilityToken<>(){});


    public static LazyOptional<IExtendedCreatureVampirism> getSafe(Entity mob) {
        return mob.getCapability(CAP);
    }

    static <Q extends PathfinderMob> ICapabilityProvider createNewCapability(final Q creature) {
        return new ICapabilitySerializable<CompoundTag>() {

            final Function<Q, IExtendedCreatureVampirism> constructor = VampirismAPI.entityRegistry().getCustomExtendedCreatureConstructor(creature);
            final IExtendedCreatureVampirism inst = constructor == null ? new ExtendedCreature(creature) : constructor.apply(creature);
            final LazyOptional<IExtendedCreatureVampirism> opt = LazyOptional.of(() -> inst);


            @Override
            public void deserializeNBT(CompoundTag nbt) {
                inst.loadData(nbt);
            }

            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {
                return CAP.orEmpty(capability, opt);
            }

            @Override
            public CompoundTag serializeNBT() {
                CompoundTag tag = new CompoundTag();
                inst.saveData(tag);
                return tag;
            }
        };
    }

    private final PathfinderMob entity;
    private final boolean canBecomeVampire;
    private boolean poisonousBlood;
    /**
     * If the blood value of these creatures should be calculated
     */
    private boolean markForBloodCalculation = false;
    private int maxBlood;
    /**
     * Stores the current blood value.
     * If this is -1, this entity never had any blood and this value cannot be changed
     */
    private int blood;
    private int remainingBarkTicks;

    public ExtendedCreature(PathfinderMob entity) {
        this.entity = entity;
        BiteableEntry entry = VampirismAPI.entityRegistry().getEntry(entity);
        if (entry != null && entry.blood > 0) {
            maxBlood = entry.blood;
            canBecomeVampire = entry.convertible;
        } else {
            if (entry == null) {
                markForBloodCalculation = true;
            }
            maxBlood = -1;
            canBecomeVampire = false;
        }
        blood = maxBlood;
        poisonousBlood = false;
    }

    @Override
    public boolean canBeBitten(IVampire biter) {
        return getBlood() > 0;
    }

    @Override
    public boolean canBecomeVampire() {
        return canBecomeVampire;
    }

    @Override
    public int getBlood() {
        return blood;
    }

    @Override
    public void setBlood(int blood) {
        if (blood >= 0 && blood <= getMaxBlood()) {
            if (getBlood() != -1) {
                this.blood = blood;
            }

        }
    }

    @Override
    public float getBloodLevelRelative() {
        return getBlood() / (float) getMaxBlood();
    }

    @Override
    public float getBloodSaturation() {
        return 1.0F;
    }

    @Override
    public ResourceLocation getCapKey() {
        return REFERENCE.EXTENDED_CREATURE_KEY;
    }

    @Override
    public PathfinderMob getEntity() {
        return entity;
    }

    @Override
    public int getMaxBlood() {
        return maxBlood;
    }

    /**
     * Set's maximum blood and current blood
     */
    private void setMaxBlood(int blood) {
        maxBlood = blood;
        this.blood = blood;
    }

    @Override
    public int getTheEntityID() {
        return entity.getId();
    }

    @Override
    public boolean hasPoisonousBlood() {
        return poisonousBlood;
    }

    @Override
    public void loadUpdateFromNBT(CompoundTag nbt) {
        if (nbt.contains(KEY_BLOOD)) {
            setBlood(nbt.getInt(KEY_BLOOD));
        }
        if (nbt.contains(KEY_MAX_BLOOD)) {
            setBlood(nbt.getInt(KEY_MAX_BLOOD));
        }
        if (nbt.contains(POISONOUS_BLOOD)) {
            setPoisonousBlood(nbt.getBoolean(POISONOUS_BLOOD));
        }
    }

    @Nullable
    @Override
    public IConvertedCreature<?> makeVampire() {
        if (canBecomeVampire()) {
            blood = -1;
            IConvertedCreature<?> c = VampirismAPI.entityRegistry().convert(entity);
            if (c != null) {
                UtilLib.replaceEntity(entity, (PathfinderMob) c);
            }
            return c;
        }
        return null;
    }

    @Override
    public boolean canBeInfected(IVampire vampire) {
        return canBecomeVampire && !hasPoisonousBlood();
    }

    @Override
    public boolean tryInfect(IVampire vampire) {
        if (canBeInfected(vampire)) {
            SanguinareEffect.addRandom(entity, false);
            return true;
        }
        return false;
    }

    @Override
    public int onBite(IVampire biter) {
        if (getBlood() <= 0) return 0;
        int amt = Math.max(1, (getMaxBlood() / (biter instanceof VampirePlayer ? 6 : 2)));
        if (amt >= blood) {
            if (blood > 1 && biter.isAdvancedBiter()) {
                amt = blood - 1;
            } else {
                amt = blood;
            }
        }
        blood -= amt;
        if (blood == 0) {
            entity.hurt(VReference.NO_BLOOD, 1000);
        }

        this.sync();
        entity.setLastHurtByMob(biter.getRepresentingEntity());

        // If entity is a child only give 1/3 blood
        if (entity instanceof AgeableMob) {
            if (((AgeableMob) entity).getAge() < 0) {
                amt = Math.round((float) amt / 3f);
            }
        }
        //If advanced biter, sometimes return twice the blood amount
        if (biter.isAdvancedBiter()) {
            if (entity.getRandom().nextInt(4) == 0) {
                amt = 2 * amt;
            }
        }

        return amt;
    }

    @Override
    public void setPoisonousBlood(boolean poisonous) {
        if (poisonous == !poisonousBlood) {
            poisonousBlood = poisonous;
            sync();
        }
    }

    public int getRemainingBarkTicks() {
        return remainingBarkTicks;
    }

    public void increaseRemainingBarkTicks(int additionalBarkTicks) {
        this.remainingBarkTicks += additionalBarkTicks;
    }

    @Override
    public void tick() {
        if (!entity.getCommandSenderWorld().isClientSide) {
            /*
             * Make sure all entities with no blood die
             * check for sanguinare as the entity might be converting instead of dying
             */
            if (blood == 0 && entity.tickCount % 20 == 10 && entity.getEffect(ModEffects.SANGUINARE.get()) == null) {
                entity.hurt(VReference.NO_BLOOD, 1000);
            }
            if (blood > 0 && blood < getMaxBlood() && entity.tickCount % 40 == 8) {
                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 41));
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 41, 2));
                if (entity.getRandom().nextInt(BalanceMobProps.mobProps.BLOOD_REGEN_CHANCE) == 0) {
                    setBlood(getBlood() + 1);
                    sync();
                }
            }
        }
        if (markForBloodCalculation) {
            if (VampirismEntityRegistry.biteableEntryManager.init()) {
                BiteableEntry entry = VampirismEntityRegistry.biteableEntryManager.calculate(entity);
                if (entry != null) {
                    setMaxBlood(entry.blood);
                }
                markForBloodCalculation = false;
            }
        }
        if (this.remainingBarkTicks > 0) {
            --this.remainingBarkTicks;
        }
    }

    @Override
    public String toString() {
        return super.toString() + " for entity (" + entity.toString() + ") [B" + blood + ",MB" + maxBlood + ",CV" + canBecomeVampire + "]";
    }

    @Override
    public void writeFullUpdateToNBT(CompoundTag nbt) {
        nbt.putInt(KEY_BLOOD, getBlood());
        nbt.putInt(KEY_MAX_BLOOD, getBlood());
        nbt.putBoolean(POISONOUS_BLOOD, hasPoisonousBlood());
    }

    @Override
    public void loadData(CompoundTag compound) {
        if (compound.contains(KEY_MAX_BLOOD)) {
            setMaxBlood(compound.getInt(KEY_MAX_BLOOD));
        }
        if (compound.contains(KEY_BLOOD)) {
            setBlood(compound.getInt(KEY_BLOOD));
        }
        if (compound.contains(POISONOUS_BLOOD)) {
            setPoisonousBlood(compound.getBoolean(POISONOUS_BLOOD));
        }
    }

    @Override
    public void saveData(CompoundTag compound) {
        compound.putInt(KEY_BLOOD, blood);
        compound.putInt(KEY_MAX_BLOOD, maxBlood);
        compound.putBoolean(POISONOUS_BLOOD, poisonousBlood);
    }

    private void sync() {
        HelperLib.sync(this, getEntity(), false);
    }

    private void sync(CompoundTag data) {
        HelperLib.sync(this, data, getEntity(), false);

    }


}
