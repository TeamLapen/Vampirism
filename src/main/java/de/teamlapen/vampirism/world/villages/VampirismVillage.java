package de.teamlapen.vampirism.world.villages;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.hunter.EntityBasicHunter;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by max on 07.05.16.
 */
public class VampirismVillage {
    public static AxisAlignedBB getBoundingBox(Village v) {
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

    private boolean dirty;
    private int recentlyBittenToDeath;

//    private void checkHunterCount(Village v) {
//        int count = getHunter(v).size();
//        if (count < BALANCE.MOBPROP.VAMPIRE_HUNTER_MAX_PER_VILLAGE || (agressive && count < BALANCE.MOBPROP.VAMPIRE_HUNTER_MAX_PER_VILLAGE * 1.4)) {
//            for (Entity e : Helper.spawnEntityInVillage(v, 2, REFERENCE.ENTITY.VAMPIRE_HUNTER_NAME, world)) {
//                ((EntityVampireHunter) e).setVillageArea(v.getCenter().posX, v.getCenter().posY, v.getCenter().posZ, v.getVillageRadius());
//                if (((EntityVampireHunter) e).getRNG().nextBoolean()) {
//                    ((EntityVampireHunter) e).setLevel(1, true);
//                } else if (agressive) {
//                    ((EntityVampireHunter) e).setLevel(3, true);
//                }
//
//            }
//        }
//    }

    public AxisAlignedBB getBoundingBox() {
        return getBoundingBox(this.getVillage());
    }

    public BlockPos getCenter() {
        return center;
    }

    public void setCenter(BlockPos cc) {
        center = cc;
    }

    public Village getVillage() {
        Village v = world.villageCollectionObj.getNearestVillage(center, 0);
        if (v == null)
            return null;
        if (!v.getCenter().equals(center)) {
            return null;
        }
        return v;
    }

    /**
     * Checks if the corrosponding village still exists
     *
     * @return -1 annihilated,0 center has been updated, 1 ok
     */
    public int isAnnihilated() {
        Village v = world.villageCollectionObj.getNearestVillage(center, 0);
        if (v == null) {
            VampirismMod.log.i(TAG, "Can't find village at " + center.toString());
            return -1;
        }
        if (!this.getCenter().equals(v.getCenter())) {
            this.setCenter(v.getCenter());
            return 0;
        }
        return 1;
    }

    public void onVillagerBitten() {
        recentlyBitten++;
        dirty = true;
    }

    public void onVillagerBittenToDeath() {
        recentlyBittenToDeath++;
        dirty = true;
    }

    public void onVillagerConverted() {
        recentlyConverted++;
        dirty = true;
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
        if (tickCounter % 20 == 13) {
            VampirismMod.log.t("Updating village %s", getCenter());
            Village v = getVillage();
            if (v != null) {
                if (tickCounter % (20 * Balance.village.REDUCE_RATE) == 0) {
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
                List<EntityVillager> villagers = getAllVillager(v);
                List<EntityBasicHunter> hunters = getHunter(v);
                List<EntityVillager> hunterVillagers = filterHunterVillagers(villagers);
                if (world.rand.nextInt(30) == 0) {
                    if ((hunters.size() + hunterVillagers.size()) < Balance.village.MIN_HUNTER_COUNT_VILLAGE) {
                        //spawnHunter(v);
                    }
                }
                if (recentlyBitten > Balance.village.BITTEN_UNTIL_AGRESSIVE || recentlyConverted > Balance.village.CONVERTED_UNTIL_AGRESSIVE) {
                    if (!agressive) {
                        makeAgressive(v);
                    }
                } else if (agressive && (recentlyBitten == 0 || recentlyBitten < Balance.village.BITTEN_UNTIL_AGRESSIVE - 1)
                        && (recentlyConverted == 0 || recentlyConverted < Balance.village.CONVERTED_UNTIL_AGRESSIVE - 1)) {
                    makeCalm(v);
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
     * @param all List to filter
     * @return A new list containing only hunter villagers
     */
    private List<EntityVillager> filterHunterVillagers(List<EntityVillager> all) {
        List<EntityVillager> filtered = new ArrayList<>();
        for (EntityVillager villager : all) {
            if (villager instanceof IHunter) {
                filtered.add(villager);
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
     * @return A new list containing only vampire villagers
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

    private void makeAgressive(Village v) {
//        Logger.d(TAG, "Making agressive");
//        agressive = true;
//        for (EntityVillager e : getVillager(v)) {
//            if (world.rand.nextInt(4) == 0) {
//                EntityVampireHunter h = (EntityVampireHunter) EntityList.createEntityByName(REFERENCE.ENTITY.VAMPIRE_HUNTER_NAME, world);
//                h.copyLocationAndAnglesFrom(e);
//                world.spawnEntityInWorld(h);
//                h.setLevel(1, true);
//                e.setDead();
//            }
//
//        }
//        dirty = true;
    }

    private void makeCalm(Village v) {
//        Logger.d(TAG, "Making calm");
//        agressive = false;
//        for (EntityVampireHunter e : getHunter(v)) {
//            if (e.getLevel() == 1) {
//                Entity ev = EntityList.createEntityByName("Villager", world);
//                ev.copyLocationAndAnglesFrom(e);
//                world.spawnEntityInWorld(ev);
//                e.setDead();
//            }
//        }
//        dirty = true;
    }

    private void spawnVillager(Village v) {

        @SuppressWarnings("rawtypes")
        List l = world.getEntitiesWithinAABB(EntityVillager.class, getBoundingBox(v));
        if (l.size() > 0) {
            EntityVillager ev = (EntityVillager) l.get(world.rand.nextInt(l.size()));
            EntityVillager entityvillager = ev.createChild(ev);
            ev.setGrowingAge(6000);
            entityvillager.setGrowingAge(-24000);
            entityvillager.setLocationAndAngles(ev.posX, ev.posY, ev.posZ, 0.0F, 0.0F);
            world.spawnEntityInWorld(entityvillager);
            world.setEntityState(entityvillager, (byte) 12);
        }
    }
}
