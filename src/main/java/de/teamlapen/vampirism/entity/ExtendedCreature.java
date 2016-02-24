package de.teamlapen.vampirism.entity;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.BiteableEntry;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.potion.PotionSanguinare;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Extended entity property which every {@link EntityCreature} has
 */
public class ExtendedCreature implements ISyncable.ISyncableExtendedProperties, IExtendedCreatureVampirism {

    private static final String TAG = "ExtendedCreature";
    private final static String KEY_BLOOD = "bloodLevel";

    public static ExtendedCreature get(EntityCreature mob) {
        return (ExtendedCreature) mob.getExtendedProperties(VReference.EXTENDED_CREATURE_PROP);
    }

    public static void register(EntityCreature mob) {
        mob.registerExtendedProperties(VReference.EXTENDED_CREATURE_PROP, new ExtendedCreature(mob));
    }

    private final EntityCreature entity;
    private final int maxBlood;
    private final boolean canBecomeVampire;
    /**
     * Stores the current blood value.
     * If this is -1, this entity never had any blood and this value cannot be changed
     */
    private int blood;

    public ExtendedCreature(EntityCreature entity) {
        this.entity = entity;
        BiteableEntry entry = VampirismAPI.biteableRegistry().getEntry(entity);
        if (entry != null) {
            maxBlood = entry.blood;
            canBecomeVampire = entry.convertible;
        } else {
            maxBlood = -1;
            canBecomeVampire = false;
        }
        blood = maxBlood;
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
    public float getBloodSaturation() {
        return 1.0F;//TODO adjust
    }

    @Override
    public EntityCreature getEntity() {
        return entity;
    }

    @Override
    public int getMaxBlood() {
        return maxBlood;
    }

    @Override
    public String getPropertyKey() {
        return VReference.EXTENDED_CREATURE_PROP;
    }

    @Override
    public int getTheEntityID() {
        return entity.getEntityId();
    }

    @Override
    public void init(Entity entity, World world) {

    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        if (compound.hasKey(KEY_BLOOD)) {
            setBlood(compound.getInteger(KEY_BLOOD));
        }

    }

    @Override
    public void loadUpdateFromNBT(NBTTagCompound nbt) {
        if (nbt.hasKey(KEY_BLOOD)) {
            setBlood(nbt.getInteger(KEY_BLOOD));
        }
    }

    @Override
    public void makeVampire() {
        if (canBecomeVampire()) {
            blood = 0;
            Entity e = (Entity) VampirismAPI.biteableRegistry().convert(entity);
            if (e != null) {
                entity.setDead();
                entity.worldObj.spawnEntityInWorld(e);
            }
        }
    }

    @Override
    public int onBite(IVampire biter) {
        if (getBlood() <= 0) return 0;
        int amt = Math.min(blood, (int) (getMaxBlood() / 2F));
        blood -= amt;
        if (blood < getMaxBlood() / 2) {
            if (blood == 0 || entity.getRNG().nextInt(blood + 1) == 0) {

                if (canBecomeVampire && entity.getRNG().nextBoolean()) {
                    if (VampirismMod.isRealism()) {
                        PotionSanguinare.addRandom(entity, false);
                    } else {
                        makeVampire();
                    }

                } else {
                    entity.attackEntityFrom(DamageSource.magic, 1000);
                }
            }

        }

        // If entity is a child only give 1/3 blood
        if (entity instanceof EntityAgeable) {
            if (((EntityAgeable) entity).getGrowingAge() < 0) {
                return Math.round((float) amt / 3);
            }
        }
        this.sync();
        return amt;
    }

    /**
     * Called every tick
     */
    public void onUpdate() {
        if (!entity.worldObj.isRemote) {
            if (blood > 0 && blood < getMaxBlood() && entity.ticksExisted % 40 == 8) {
                entity.addPotionEffect(new PotionEffect(Potion.weakness.id, 41));
                entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 41, 2));
                if (entity.getRNG().nextInt(Balance.mobProps.BLOOD_REGEN_CHANCE) == 0) {
                    setBlood(getBlood() + 1);
                }
            }
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        compound.setInteger(KEY_BLOOD, blood);
    }

    @Override
    public String toString() {
        return super.toString() + " for entity (" + entity.toString() + ") [B" + blood + ",MB" + maxBlood + ",CV" + canBecomeVampire + "]";
    }

    @Override
    public void writeFullUpdateToNBT(NBTTagCompound nbt) {
        nbt.setInteger(KEY_BLOOD, getBlood());
    }

    private void sync() {
        HelperLib.sync(this, getEntity(), false);
    }

    private void sync(NBTTagCompound data) {
        HelperLib.sync(this, data, getEntity(), false);

    }
}
