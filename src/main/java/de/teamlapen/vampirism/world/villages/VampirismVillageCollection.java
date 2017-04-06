package de.teamlapen.vampirism.world.villages;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.world.IVampirismVillageProvider;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSavedData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Stores all Vampirism Villages
 */
public class VampirismVillageCollection extends WorldSavedData implements IVampirismVillageProvider {

    private static final String IDENTIFIER = "vampirism_villages";

    public static VampirismVillageCollection get(World world) {
        String s = fileNameForProvider(world.provider);
        VampirismVillageCollection data = (VampirismVillageCollection) world.getPerWorldStorage().getOrLoadData(VampirismVillageCollection.class, s);
        if (data == null) {
            data = new VampirismVillageCollection(world);
            world.getPerWorldStorage().setData(s, data);
        } else {
            data.setWorldsForAll(world);
        }
        return data;
    }

    private static String fileNameForProvider(WorldProvider provider) {
        return IDENTIFIER + provider.getDimensionType().getSuffix();
    }

    private final List<VampirismVillage> villageList = new ArrayList<>();
    private World worldObj;
    private int tickCounter;

    public VampirismVillageCollection(String name) {
        super(name);
    }

    private VampirismVillageCollection(World world) {
        this(fileNameForProvider(world.provider));
        this.worldObj = world;
        this.markDirty();
    }

    @Override
    public
    @Nullable
    VampirismVillage getNearestVillage(Entity e) {
        return this.getNearestVillage(e.getPosition(), 5);
    }

    @Override
    public
    @Nullable
    VampirismVillage getNearestVillage(BlockPos pos, int r) {

        Village v = worldObj.villageCollectionObj.getNearestVillage(pos, r);
        if (v == null)
            return null;
        return getVampirismVillage(v);
    }

    @Override
    public
    @Nullable
    VampirismVillage getVampirismVillage(Village v) {
        synchronized (villageList) {
            for (VampirismVillage vv : villageList) {
                if (vv.getCenter().equals(v.getCenter())) {
                    return vv;
                }
            }
            VampirismVillage vv = new VampirismVillage();
            vv.setWorld(worldObj);
            vv.setCenter(v.getCenter());

            VampirismMod.log.d("VampirismVillages", "Created village at " + v.getCenter());
            villageList.add(vv);
            this.markDirty();
            return vv;
        }

    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.tickCounter = nbt.getInteger("Tick");

        NBTTagList nbttaglist = nbt.getTagList("Villages", 10);
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            VampirismVillage village = new VampirismVillage();
            village.readFromNBT(nbttagcompound1);
            this.villageList.add(village);
        }
    }

    /**
     * Called world tick by an EventHandler
     */
    public void tick() {
        if (worldObj == null || worldObj.villageCollectionObj == null) return;
        tickCounter++;
        boolean dirty = false;
        worldObj.theProfiler.startSection("vampirism_vampireVillages_checkAnnihilated");
        this.checkForAnnihilatedVillages();
        worldObj.theProfiler.endStartSection("vampirism_vampireVillages_tick");
        for (VampirismVillage v : villageList) {
            if (v.tick(tickCounter))
                dirty = true;
        }
        worldObj.theProfiler.endSection();

        if (dirty)
            this.markDirty();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("Tick", this.tickCounter);

        NBTTagList nbttaglist = new NBTTagList();
        for (VampirismVillage village : this.villageList) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            village.writeToNBT(nbttagcompound1);
            nbttaglist.appendTag(nbttagcompound1);
        }

        nbt.setTag("Villages", nbttaglist);
        return nbt;
    }

    private void checkForAnnihilatedVillages() {

        synchronized (villageList) {
            Iterator<VampirismVillage> iterator = this.villageList.iterator();

            while (iterator.hasNext()) {
                VampirismVillage v = iterator.next();

                switch (v.isAnnihilated()) {
                    case -1:
                        VampirismMod.log.d("VampirismVillages", "Removing annihilated village at %s", v.getCenter());
                        iterator.remove();
                        markDirty();
                        break;
                    case 0:
                        markDirty();
                        break;
                    default://Do nothing
                }
            }
        }
    }

    private void setWorldsForAll(World world) {
        this.worldObj = world;
        synchronized (villageList) {
            for (VampirismVillage vv : villageList) {
                vv.setWorld(world);
            }
        }
    }
}
