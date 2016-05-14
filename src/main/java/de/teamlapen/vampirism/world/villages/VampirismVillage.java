package de.teamlapen.vampirism.world.villages;

import com.google.common.collect.Lists;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.api.world.IVampirismVillage;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.entity.hunter.EntityBasicHunter;
import de.teamlapen.vampirism.entity.hunter.EntityHunterVillager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Vampirism's instance of a village
 */
public class VampirismVillage implements IVampirismVillage {
    private static AxisAlignedBB getBoundingBox(Village v) {
        int r = v.getVillageRadius();
        BlockPos cc = v.getCenter();
        return new AxisAlignedBB(cc.getX() - r, cc.getY() - 10, cc.getZ() - r, cc.getX() + r, cc.getY() + 10, cc.getZ() + r);
    }

    private final String TAG = "VillageVampire";
    private World world;
    private BlockPos center = new BlockPos(0, 0, 0);
    private int recentlyBitten;
    private int recentlyConverted;
    private boolean agressive;
    private List<VillageAggressorVampire> villageAggressorVampires = Lists.newArrayList();
    private boolean dirty;
    private int recentlyBittenToDeath;
    private int tickCounter;

    @Override
    public
    @Nullable
    IVampire findNearestVillageAggressor(@Nonnull EntityLivingBase entityCenter) {
        double d0 = Double.MAX_VALUE;
        VillageAggressorVampire aggressorVampire = null;

        for (int i = 0; i < this.villageAggressorVampires.size(); ++i) {
            VillageAggressorVampire vampire = this.villageAggressorVampires.get(i);
            double d1 = vampire.aggressorEntity.getDistanceSqToEntity(entityCenter);

            if (d1 <= d0) {
                aggressorVampire = vampire;
                d0 = d1;
            }
        }

        return aggressorVampire != null ? aggressorVampire.aggressorVampire : null;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return getBoundingBox(this.getVillage());
    }

    @Override
    public BlockPos getCenter() {
        return center;
    }

    void setCenter(BlockPos cc) {
        center = cc;
    }

    @Override
    public Village getVillage() {
        Village v = world.villageCollectionObj.getNearestVillage(center, 0);
        if (v == null)
            return null;
        if (!v.getCenter().equals(center)) {
            return null;
        }
        return v;
    }

    @Override
    public void onVillagerBitten(IVampire vampire) {
        recentlyBitten++;
        dirty = true;
        addOrRenewAggressor(vampire);
    }

    @Override
    public void onVillagerBittenToDeath(IVampire vampire) {
        recentlyBittenToDeath++;
        dirty = true;
        addOrRenewAggressor(vampire);
    }

    @Override
    public void onVillagerConverted(@Nullable IVampire vampire) {
        recentlyConverted++;
        dirty = true;
        if (vampire != null) {
            addOrRenewAggressor(vampire);
        }
    }

    public void readFromNBT(NBTTagCompound nbt) {
        center = UtilLib.readPos(nbt, "center");
        agressive = nbt.getBoolean("AGR");
        recentlyBitten = nbt.getInteger("BITTEN");
        recentlyConverted = nbt.getInteger("CONVERTED");
        recentlyBittenToDeath = nbt.getInteger("KILLED");
    }

    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * Updates the VillageVampire
     *
     * @param tickCounter
     * @return dirty
     */
    public boolean tick(int tickCounter) {
        this.tickCounter=tickCounter;
        if (tickCounter % 20 == 13) {
            int tick = tickCounter / 20;
            this.removeDeadAndOldAggressors();
            Village v = getVillage();
            if (v != null) {
                if (tick % (Balance.village.REDUCE_RATE) == 0) {
                    if (recentlyBitten > 0) {
                        recentlyBitten--;
                        dirty = true;
                    }
                    boolean respawn = false;
                    if (recentlyConverted > 0) {
                        recentlyConverted--;
                        dirty = true;
                        respawn = true;

                    } else if (recentlyBittenToDeath > 0) {
                        recentlyBittenToDeath--;
                        dirty = true;
                        respawn = true;
                    }
                    if (respawn && world.rand.nextInt(Balance.village.VILLAGER_RESPAWN_RATE) == 0) {
                        spawnVillager(v);

                    }
                }
                List<EntityVillager> allVillagers = getAllVillager(v);
                List<EntityBasicHunter> hunters = getHunter(v);
                List<EntityHunterVillager> hunterVillagers = filterHunterVillagers(allVillagers);
                List<EntityVillager> normalVillager = filterNormalVillagers(allVillagers);
                if (world.rand.nextInt(30) == 0) {
//                    VampirismMod.log.t("Aggro Count %s",calculateAggressiveCounter());
//                    VampirismMod.log.t("Count %s %s %s",normalVillager.size(),hunterVillagers.size(),hunters.size());

                    if ((hunters.size() + hunterVillagers.size()) < (Math.round(Balance.village.MIN_HUNTER_COUNT_VILLAGE_PER_DOOR * v.getNumVillageDoors()) + 1)) {
                        spawnHunter(v);
                    }
                }
                int aggressiveCounter = calculateAggressiveCounter();
                if (aggressiveCounter >= Balance.village.AGGRESSIVE_COUNTER_THRESHOLD) {
                    if (!agressive) {
                        spawnVillager(v);
                        makeAgressive(selectVillagersToBecomeHunter(normalVillager));
                    }
                } else if (agressive && aggressiveCounter < (Balance.village.AGGRESSIVE_COUNTER_THRESHOLD / 2 + 1)) {
                    makeCalm(hunterVillagers);
                }

            }
        }
        if (dirty) {
            dirty = false;
            return true;
        }
        return false;
    }

    public void writeToNBT(NBTTagCompound nbt) {
        UtilLib.write(nbt, "center", center);
        nbt.setBoolean("AGR", agressive);
        nbt.setInteger("BITTEN", recentlyBitten);
        nbt.setInteger("CONVERTED", recentlyConverted);
        nbt.setInteger("KILLED", recentlyBittenToDeath);
    }

    /**
     * Adds or updates the aggressor entry for the given vampire
     *
     * @param vampire
     */
    private void addOrRenewAggressor(@Nonnull IVampire vampire) {
        for (VillageAggressorVampire aggressor : this.villageAggressorVampires) {
            if (aggressor.aggressorVampire.equals(vampire)) {
                aggressor.agressionTime = this.tickCounter;
                return;
            }
        }
        this.villageAggressorVampires.add(new VillageAggressorVampire(vampire.getRepresentingEntity(), vampire, this.tickCounter));
    }

    /**
     * Calculates the aggressive counter values from recently bitten/converted/killed villagers
     */
    private int calculateAggressiveCounter() {
        return recentlyBitten * Balance.village.BITTEN_AGGRESSIVE_FACTOR + recentlyBittenToDeath * Balance.village.BITTEN_TO_DEATH_AGGRESSIVE_FACTOR + recentlyConverted * Balance.village.CONVERTED_AGGRESSIVE_FACTOR;
    }

    /**
     * @param all List to filter
     * @return A new list containing only {@link EntityHunterVillager}
     */
    private List<EntityHunterVillager> filterHunterVillagers(List<EntityVillager> all) {
        List<EntityHunterVillager> filtered = new ArrayList<>();
        for (EntityVillager villager : all) {
            if (villager instanceof EntityHunterVillager) {
                filtered.add((EntityHunterVillager) villager);
            }
        }
        return filtered;
    }

    /**
     * @param all List to filter
     * @return A new list containing only normal villagers
     */
    private List<EntityVillager> filterNormalVillagers(List<EntityVillager> all) {
        List<EntityVillager> filtered = new ArrayList<>();
        for (EntityVillager villager : all) {
            if (!(villager instanceof IVampire || villager instanceof IHunter)) {
                filtered.add(villager);
            }
        }
        return filtered;
    }

    /**
     * @param all List to filter
     * @return A new list containing only {@link IVampire} villagers
     */
    private List<EntityVillager> filterVampireVillagers(List<EntityVillager> all) {
        List<EntityVillager> filtered = new ArrayList<>();
        for (EntityVillager villager : all) {
            if (villager instanceof IVampire) {
                filtered.add(villager);
            }
        }
        return filtered;
    }

    /**
     * @param v
     * @return A list of all villagers in the given village
     */
    private List<EntityVillager> getAllVillager(Village v) {
        return world.getEntitiesWithinAABB(EntityVillager.class, getBoundingBox(v));
    }

    private List<EntityBasicHunter> getHunter(Village v) {
        return world.getEntitiesWithinAABB(EntityBasicHunter.class, getBoundingBox(v));
    }

    /**
     * Checks if the corrosponding village still exists
     *
     * @return -1 annihilated,0 center has been updated, 1 ok
     */
    int isAnnihilated() {
        Village v = world.villageCollectionObj.getNearestVillage(center, 0);
        if (v == null) {
            VampirismMod.log.i(TAG, "Can't find village at %s anymore", center);
            return -1;
        }
        if (!this.getCenter().equals(v.getCenter())) {
            this.setCenter(v.getCenter());
            return 0;
        }
        return 1;
    }

    private void makeAgressive(List<EntityVillager> villagers) {
        VampirismMod.log.d(TAG, "Making villagers aggressive");
        agressive = true;
        dirty = true;
        for (EntityVillager v : villagers) {
            if (world.rand.nextInt(4) == 0) {
                EntityHunterVillager hunter = EntityHunterVillager.makeHunter(v);
                v.worldObj.spawnEntityInWorld(hunter);
                v.setDead();

            }
        }
    }

    private void makeCalm(List<EntityHunterVillager> hunters) {
        VampirismMod.log.d(TAG, "Making villagers calm");
        for (EntityHunterVillager h : hunters) {
            EntityVillager villager = EntityHunterVillager.makeNormal(h);
            h.worldObj.spawnEntityInWorld(villager);
            h.setDead();
        }
        agressive=false;
        dirty = true;
    }

    private void removeDeadAndOldAggressors() {
        Iterator<VillageAggressorVampire> iterator = villageAggressorVampires.iterator();
        while (iterator.hasNext()) {
            VillageAggressorVampire aggressorVampire = iterator.next();
            if (!aggressorVampire.aggressorEntity.isEntityAlive() || Math.abs(this.tickCounter - aggressorVampire.agressionTime) > 600) {
                iterator.remove();
            }

        }

    }

    /**
     * Creates a list of villagers that should become hunters. This considers things like childhood or trading. Also uses random.

     */
    private List<EntityVillager> selectVillagersToBecomeHunter(List<EntityVillager> villagers) {
        List<EntityVillager> selected = new LinkedList<>();
        for (EntityVillager v : villagers) {
            if (v.isChild() || !v.isEntityAlive()) {
                continue;
            }
            if (v.isPotionActive(ModPotions.sanguinare)) {
                continue;
            }
            if (v.isTrading() || v.isMating()) {
                continue;
            }
            if (v.getRNG().nextInt(Balance.village.VILLAGER_HUNTER_CHANCE) == 0) {
                selected.add(v);
            }
        }
        return selected;
    }

    private void spawnHunter(Village v) {
        EntityBasicHunter hunter = new EntityBasicHunter(world);
        boolean flag = UtilLib.spawnEntityInWorld(world, getBoundingBox(v), hunter, 5);
        if (flag) {
            hunter.makeVillageHunter(this);
        } else {
            hunter.setDead();
        }
        VampirismMod.log.t("Spawning Vampire Hunter %s", flag);
    }

    private void spawnVillager(Village v) {
        VampirismMod.log.t("Spawning villager at village %s",v.getCenter());
        @SuppressWarnings("rawtypes")
        List l = world.getEntitiesWithinAABB(EntityVillager.class, getBoundingBox(v));
        if (l.size() > 0) {
            EntityVillager ev = (EntityVillager) l.get(world.rand.nextInt(l.size()));
            EntityVillager entityvillager;
            if (agressive && ev.getRNG().nextInt(Balance.village.VILLAGER_HUNTER_CHANCE) == 0) {
                EntityVillager temp = new EntityVillager(ev.worldObj);
                entityvillager = EntityHunterVillager.makeHunter(temp);
                temp.setDead();
            } else {
                entityvillager = ev.createChild(ev);
                entityvillager.setGrowingAge(-24000);
                ev.setGrowingAge(6000);
            }
            entityvillager.setLocationAndAngles(ev.posX, ev.posY, ev.posZ, 0.0F, 0.0F);
            world.spawnEntityInWorld(entityvillager);
            world.setEntityState(entityvillager, (byte) 12);
        }
    }


    /**
     * Keeps track of a vampire that bit a villager
     */
    private class VillageAggressorVampire {
        final EntityLivingBase aggressorEntity;
        final IVampire aggressorVampire;
        int agressionTime;

        private VillageAggressorVampire(EntityLivingBase aggressorEntity, IVampire aggressorVampire, int agressionTime) {
            this.aggressorEntity = aggressorEntity;
            this.aggressorVampire = aggressorVampire;
            this.agressionTime = agressionTime;
        }
    }
}
