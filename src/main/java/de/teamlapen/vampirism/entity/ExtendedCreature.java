package de.teamlapen.vampirism.entity;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.BiteableEntry;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.potion.PotionSanguinare;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Extended entity property which every {@link CreatureEntity} has
 */
public class ExtendedCreature implements ISyncable.ISyncableEntityCapabilityInst, IExtendedCreatureVampirism {

    private final static String KEY_BLOOD = "bloodLevel";
    private final static String KEY_MAX_BLOOD = "maxBlood";
    private final static String POISONOUS_BLOOD = "poisonousBlood";
    @CapabilityInject(IExtendedCreatureVampirism.class)
    public static Capability<IExtendedCreatureVampirism> CAP = getNull();

    /**
     * Must check if {@link Entity#isAlive()} before calling this
     */
    public static IExtendedCreatureVampirism getUnsafe(CreatureEntity mob) {
        return mob.getCapability(CAP, null).orElseThrow(() -> new IllegalStateException("Cannot get ExtendedCreature from EntityCreature " + mob));
    }

    public static LazyOptional<IExtendedCreatureVampirism> getSafe(Entity mob) {
        return mob.getCapability(CAP);
    }


    public static void registerCapability() {
        CapabilityManager.INSTANCE.register(IExtendedCreatureVampirism.class, new Storage(), ExtendedCreatureDefaultImpl::new);
    }

    static <Q extends CreatureEntity> ICapabilityProvider createNewCapability(final Q creature) {
        return new ICapabilitySerializable<CompoundNBT>() {

            Function<Q, IExtendedCreatureVampirism> constructor = VampirismAPI.entityRegistry().getCustomExtendedCreatureConstructor(creature);
            IExtendedCreatureVampirism inst = constructor == null ? new ExtendedCreature(creature) : constructor.apply(creature);
            LazyOptional<IExtendedCreatureVampirism> opt = LazyOptional.of(() -> inst);


            @Override
            public void deserializeNBT(CompoundNBT nbt) {
                CAP.getStorage().readNBT(CAP, inst, null, nbt);
            }

            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {
                return CAP.orEmpty(capability, opt);
            }

            @Override
            public CompoundNBT serializeNBT() {
                return (CompoundNBT) CAP.getStorage().writeNBT(CAP, inst, null);
            }
        };
    }

    private final CreatureEntity entity;
    private final boolean canBecomeVampire;
    private boolean poisonousBlood;
    /**
     * If the blood value of this creatures should be calculated
     */
    private boolean markForBloodCalculation = false;
    private int maxBlood;
    /**
     * Stores the current blood value.
     * If this is -1, this entity never had any blood and this value cannot be changed
     */
    private int blood;

    public ExtendedCreature(CreatureEntity entity) {
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
        return 1.0F;//TODO adjust
    }

    @Override
    public ResourceLocation getCapKey() {
        return REFERENCE.EXTENDED_CREATURE_KEY;
    }

    @Override
    public CreatureEntity getEntity() {
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
        return entity.getEntityId();
    }

    @Override
    public boolean hasPoisonousBlood() {
        return poisonousBlood;
    }

    @Override
    public void loadUpdateFromNBT(CompoundNBT nbt) {
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

    @Override
    public
    @Nullable
    IConvertedCreature makeVampire() {
        if (canBecomeVampire()) {
            blood = 0;
            IConvertedCreature c = VampirismAPI.entityRegistry().convert(entity);
            if (c != null) {
                Entity e = (Entity) c;
                entity.remove();
                entity.getEntityWorld().addEntity(e);
            }
            return c;
        }
        return null;
    }

    @Override
    public int onBite(IVampire biter) {
        if (getBlood() <= 0) return 0;
        int amt = Math.max(1, (getMaxBlood() / (biter instanceof VampirePlayer ? 6 : 2)));
        if (amt > blood) {
            if (blood > 1 && biter.isAdvancedBiter()) {
                amt = blood - 1;
            } else {
                amt = blood;
            }
        }
        blood -= amt;
        boolean killed = false;
        boolean converted = false;
        if (blood < getMaxBlood() / 2) {
            if (blood == 0) {

                if (canBecomeVampire && entity.getRNG().nextBoolean()) {
                    if (VampirismMod.isRealism()) {
                        PotionSanguinare.addRandom(entity, false);
                    } else {
                        makeVampire();
                    }
                    converted = true;

                } else {
                    entity.attackEntityFrom(DamageSource.MAGIC, 1000);
                    killed = true;
                }
            }

        }

        this.sync();

        // If entity is a child only give 1/3 blood
        if (entity instanceof AgeableEntity) {
            if (((AgeableEntity) entity).getGrowingAge() < 0) {
                amt = Math.round((float) amt / 3f);
            }
        }
        //If advanced biter, sometimes return twice the blood amount
        if (biter.isAdvancedBiter()) {
            if (entity.getRNG().nextInt(4) == 0) {
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

    @Override
    public void tick() {
        if (!entity.getEntityWorld().isRemote) {
            if (blood > 0 && blood < getMaxBlood() && entity.ticksExisted % 40 == 8) {
                entity.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 41));
                entity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 41, 2));
                if (entity.getRNG().nextInt(BalanceMobProps.mobProps.BLOOD_REGEN_CHANCE) == 0) {
                    setBlood(getBlood() + 1);
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
    }

    @Override
    public String toString() {
        return super.toString() + " for entity (" + entity.toString() + ") [B" + blood + ",MB" + maxBlood + ",CV" + canBecomeVampire + "]";
    }

    @Override
    public void writeFullUpdateToNBT(CompoundNBT nbt) {
        nbt.putInt(KEY_BLOOD, getBlood());
        nbt.putInt(KEY_MAX_BLOOD, getBlood());
        nbt.putBoolean(POISONOUS_BLOOD, hasPoisonousBlood());
    }

    private void loadNBTData(CompoundNBT compound) {
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

    private void saveNBTData(CompoundNBT compound) {
        compound.putInt(KEY_BLOOD, blood);
        compound.putInt(KEY_MAX_BLOOD, maxBlood);
        compound.putBoolean(POISONOUS_BLOOD, poisonousBlood);
    }

    private void sync() {
        HelperLib.sync(this, getEntity(), false);
    }

    private void sync(CompoundNBT data) {
        HelperLib.sync(this, data, getEntity(), false);

    }

    private static class Storage implements Capability.IStorage<IExtendedCreatureVampirism> {
        @Override
        public void readNBT(Capability<IExtendedCreatureVampirism> capability, IExtendedCreatureVampirism instance, Direction side, INBT nbt) {
            ((ExtendedCreature) instance).loadNBTData((CompoundNBT) nbt);
        }

        @Override
        public INBT writeNBT(Capability<IExtendedCreatureVampirism> capability, IExtendedCreatureVampirism instance, Direction side) {
            CompoundNBT nbt = new CompoundNBT();
            ((ExtendedCreature) instance).saveNBTData(nbt);
            return nbt;
        }
    }

}
