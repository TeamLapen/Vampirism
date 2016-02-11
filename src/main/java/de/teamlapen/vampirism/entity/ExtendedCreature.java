package de.teamlapen.vampirism.entity;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.convertible.BiteableEntry;
import de.teamlapen.vampirism.api.entity.convertible.BiteableRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Extended entity property which every {@link EntityCreature} has
 */
public class ExtendedCreature implements ISyncable.ISyncableExtendedProperties, IExtendedCreatureVampirism {

    private static final String TAG = "ExtendedCreature";
    private final static String KEY_BLOOD = "bloodLevel";
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
        BiteableEntry entry = BiteableRegistry.getEntry(entity);
        if (entry != null) {
            maxBlood = entry.blood;
            canBecomeVampire = entry.convertible;
        } else {
            maxBlood = -1;
            canBecomeVampire = false;
        }
        blood = maxBlood;
    }

    public static ExtendedCreature get(EntityCreature mob) {
        return (ExtendedCreature) mob.getExtendedProperties(VampirismAPI.EXTENDED_CREATURE_PROP);
    }

    public static void register(EntityCreature mob) {
        mob.registerExtendedProperties(VampirismAPI.EXTENDED_CREATURE_PROP, new ExtendedCreature(mob));
    }

    public boolean canBecomeVampire() {
        return canBecomeVampire;
    }

    @Override
    public int getTheEntityID() {
        return entity.getEntityId();
    }

    @Override
    public String getPropertyKey() {
        return VampirismAPI.EXTENDED_CREATURE_PROP;
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        compound.setInteger(KEY_BLOOD, blood);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        setBlood(compound.getInteger(KEY_BLOOD));
    }

    @Override
    public void init(Entity entity, World world) {

    }

    @Override
    public void loadUpdateFromNBT(NBTTagCompound nbt) {
        if (nbt.hasKey(KEY_BLOOD)) {
            setBlood(nbt.getInteger(KEY_BLOOD));
        }
    }

    @Override
    public void writeFullUpdateToNBT(NBTTagCompound nbt) {
        nbt.setInteger(KEY_BLOOD, getBlood());
    }

    @Override
    public EntityCreature getEntity() {
        return entity;
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
    public int getMaxBlood() {
        return maxBlood;
    }

    private void sync() {

        HelperLib.sync(this, getEntity(), false);


    }

    private void sync(NBTTagCompound data) {
        HelperLib.sync(this, data, getEntity(), false);

    }
}
