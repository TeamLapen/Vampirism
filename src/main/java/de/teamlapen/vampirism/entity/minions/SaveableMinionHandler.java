package de.teamlapen.vampirism.entity.minions;

import com.google.common.base.Predicate;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.minions.IMinion;
import de.teamlapen.vampirism.api.entity.minions.IMinionLord;
import de.teamlapen.vampirism.api.entity.minions.ISaveableMinion;
import de.teamlapen.vampirism.api.entity.minions.ISaveableMinionHandler;
import de.teamlapen.vampirism.util.MinionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of saveableminion handler
 */
public class SaveableMinionHandler<T extends ISaveableMinion> implements ISaveableMinionHandler<T> {
    private final static String TAG = "MinionHandler";
    private final List<T> minions;
    private final IMinionLord lord;
    private final Predicate<EntityLivingBase> entityPredicate;
    private List<T> loadedMinions;

    public SaveableMinionHandler(IMinionLord lord) {
        this.lord = lord;
        this.minions = new ArrayList<>();
        entityPredicate = new Predicate<EntityLivingBase>() {
            @Override
            public boolean apply(@Nullable EntityLivingBase e) {
                if (e == null) return false;
                if (e instanceof IMinion) {
                    if (SaveableMinionHandler.this.lord.equals(((IMinion) e).getLord())) {
                        return false;
                    }
                }
                return !e.equals(SaveableMinionHandler.this.lord);

            }
        };
    }

    @Override
    public void addLoadedMinions() {
        if (loadedMinions == null)
            return;
        List<T> list = loadedMinions;
        loadedMinions = null;
        //VampirismMod.log.d(TAG, "Going to add " + list.size());
        World world = lord.getRepresentingEntity().world;
        for (T m : list) {
            Entity entity = MinionHelper.entity(m);
            entity.forceSpawn = true;
            world.spawnEntity(entity);
            m.setLord(lord);
        }
        //VampirismMod.log.d(TAG, "Added " + list.size() + " minions");
        list.clear();

    }

    @Override
    public void checkMinions() {
        Iterator<T> it = minions.iterator();
        while (it.hasNext()) {
            T m = it.next();
            if (MinionHelper.entity(m).isDead || !lord.equals(m.getLord())) {
                it.remove();
            }
        }
    }

    @Override
    public int getLeftMinionSlots() {
        return Math.max(lord.getMaxMinionCount() - this.getMinionCount(), 0);
    }

    @Override
    public int getMinionCount() {
        return minions.size();
    }

    /**
     * Returns a list of entity NBTTags to save with the player Dead entitys are saves as alive, so in multiplayer entities removed by
     * {@link de.teamlapen.vampirism.player.vampire.VampirePlayer#onPlayerLoggedOut()} are saved as well
     *
     * @return
     */
    public NBTTagList getMinionsToSave() {
        NBTTagList list = new NBTTagList();
        for (IMinion m : minions) {
            Entity e = MinionHelper.entity(m);
            boolean dead = e.isDead;
            e.isDead = false;
            NBTTagCompound nbt = new NBTTagCompound();
            e.writeToNBTAtomically(nbt);
            list.appendTag(nbt);
            if (dead)
                e.isDead = true;
        }
        //VampirismMod.log.d(TAG, "Saved " + list.tagCount() + " minions");
        return list;
    }

    @Override
    public Predicate<EntityLivingBase> getNonMinionSelector() {
        return entityPredicate;
    }

    @Override
    public void killMinions(boolean instant) {
        for (T m : minions) {
            EntityLivingBase e = MinionHelper.entity(m);
            if (instant) {
                e.setDead();
            } else {
                e.attackEntityFrom(DamageSource.MAGIC, 100);
            }
        }
    }

    /**
     * Load and instantiate minions from the given nbt
     *
     * @param list
     */
    public void loadMinions(NBTTagList list) {
        if (list == null || list.tagCount() == 0) {
            VampirismMod.log.d(TAG, "Empty minion list to load");
            return;
        }
        loadedMinions = new ArrayList<>();
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
            Entity entity = EntityList.createEntityFromNBT(nbttagcompound, lord.getRepresentingEntity().world);
            if (entity != null && entity instanceof ISaveableMinion) {
                entity.posY = entity.posY + entity.height;
                loadedMinions.add((T) entity);
                VampirismMod.log.d(TAG, "Loaded minion from nbt");
            } else {
                VampirismMod.log.d(TAG, "Failed to load minion from NBT");
            }
        }

    }


    @Override
    public void registerMinion(T m, boolean force) {
        if (force || getLeftMinionSlots() > 0) {
            minions.add(m);
        }
    }

    @Override
    public void teleportMinionsToLord() {
        List<T> toTeleportDim = new ArrayList<>();
        Entity e1 = lord.getRepresentingEntity();
        Iterator<T> it = minions.iterator();
        while (it.hasNext()) {
            T m = it.next();
            Entity e = MinionHelper.entity(m);
            if (e1.dimension != e.dimension) {
                toTeleportDim.add(m);
                it.remove();
            }
        }
        for (IMinion m : minions) {
            Entity e = MinionHelper.entity(m);

            e.copyLocationAndAnglesFrom(e1);
        }
        for (IMinion m : toTeleportDim) {
            //Logger.d(TAG, "Teleporting minion");
            Entity e = MinionHelper.entity(m);
            e.changeDimension(e1.dimension);
            e.timeUntilPortal = e.getPortalCooldown();

        }
    }

    @Override
    public String toString() {
        return TAG + " for " + lord.toString() + " with " + getMinionCount() + " minions";
    }

    @Override
    public void unregisterMinion(T m) {
        minions.remove(m);
    }
}
