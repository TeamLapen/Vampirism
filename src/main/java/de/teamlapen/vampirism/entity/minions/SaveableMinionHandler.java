package de.teamlapen.vampirism.entity.minions;

import de.teamlapen.vampirism.api.entity.minions.IMinion;
import de.teamlapen.vampirism.api.entity.minions.IMinionLord;
import de.teamlapen.vampirism.api.entity.minions.ISaveableMinion;
import de.teamlapen.vampirism.api.entity.minions.ISaveableMinionHandler;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.MinionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Implementation of saveableminion handler
 */
public class SaveableMinionHandler<T extends ISaveableMinion> implements ISaveableMinionHandler<T> {
    private final static Logger LOGGER = LogManager.getLogger(SaveableMinionHandler.class);
    private final List<T> minions;
    private final IMinionLord lord;
    private final Predicate<LivingEntity> entityPredicate;
    private List<T> loadedMinions;

    public SaveableMinionHandler(IMinionLord lord) {
        this.lord = lord;
        this.minions = new ArrayList<>();
        entityPredicate = e -> {
            if (e == null) return false;
            if (e instanceof IMinion) {
                if (SaveableMinionHandler.this.lord.equals(((IMinion) e).getLord())) {
                    return false;
                }
            }
            return !e.equals(SaveableMinionHandler.this.lord);

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
        minions.removeIf(m -> !MinionHelper.entity(m).isAlive() || !lord.equals(m.getLord()));
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
     * {@link VampirePlayer#onPlayerLoggedOut()} are saved as well
     *
     * @return
     */
    public ListNBT getMinionsToSave() {
        ListNBT list = new ListNBT();
        for (IMinion m : minions) {
            Entity e = MinionHelper.entity(m);
            boolean removed = !e.isAlive();
            e.removed = false;
            CompoundNBT nbt = new CompoundNBT();
            e.writeUnlessRemoved(nbt);
            list.add(nbt);
            if (removed)
                e.removed = true;
        }
        //VampirismMod.log.d(TAG, "Saved " + list.tagCount() + " minions");
        return list;
    }

    @Override
    public java.util.function.Predicate<LivingEntity> getNonMinionSelector() {
        return entityPredicate;
    }

    @Override
    public void killMinions(boolean instant) {
        for (T m : minions) {
            LivingEntity e = MinionHelper.entity(m);
            if (instant) {
                e.remove();
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
    public void loadMinions(ListNBT list) {
        if (list == null || list.size() == 0) {
            LOGGER.debug("Empty minion list to load");
            return;
        }
        loadedMinions = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CompoundNBT nbttagcompound = list.getCompound(i);
            Entity entity = EntityType.create(nbttagcompound, lord.getRepresentingEntity().world);
            if (entity != null && entity instanceof ISaveableMinion) {
                entity.posY = entity.posY + entity.height;
                loadedMinions.add((T) entity);
                LOGGER.debug("Loaded minion from nbt");
            } else {
                LOGGER.debug("Failed to load minion from NBT");
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
        return "SaveableMinionHandler" + " for " + lord.toString() + " with " + getMinionCount() + " minions";
    }

    @Override
    public void unregisterMinion(T m) {
        minions.remove(m);
    }
}
