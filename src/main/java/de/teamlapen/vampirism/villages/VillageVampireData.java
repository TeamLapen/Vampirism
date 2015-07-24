package de.teamlapen.vampirism.villages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import de.teamlapen.vampirism.util.Logger;

/**
 * VillageVampire data handler, implemented similar to VillageCollection
 * 
 * @author Maxanier
 *
 */
public class VillageVampireData extends WorldSavedData {

	private static final String IDENTIFIER = "vampirism_village";
	public static VillageVampireData get(World world) {
		VillageVampireData data = (VillageVampireData) world.perWorldStorage.loadData(VillageVampireData.class, IDENTIFIER);
		if (data == null) {
			data = new VillageVampireData();
			world.perWorldStorage.setData(IDENTIFIER, data);
		}
		data.setWorld(world);
		return data;
	}
	private final List<VillageVampire> villageList = new ArrayList<VillageVampire>();
	private World worldObj;

	private int tickCounter;

	public VillageVampireData() {
		super(IDENTIFIER);
	}

	public VillageVampireData(String identifier) {
		super(identifier);
	}

	private void checkForAnnihilatedVillages() {

		synchronized (villageList) {
			Iterator<VillageVampire> iterator = this.villageList.iterator();

			while (iterator.hasNext()) {
				VillageVampire v =iterator.next();

				switch (v.isAnnihilated()) {
				case -1:
					Logger.d("VillageVampireData", "Removing annihilated village");
					iterator.remove();
					markDirty();
					break;
				case 0:
					markDirty();
					break;
				}
			}
		}
	}

	public VillageVampire findNearestVillage(Entity e) {
		return this.findNearestVillage((int) e.posX, (int) e.posY, (int) e.posZ, 5);
	}

	/**
	 * Finds the nearest village, but only the given coordinates are withing it's bounding box plus the given the distance.
	 */
	public VillageVampire findNearestVillage(int x, int y, int z, int r) {

		Village v = worldObj.villageCollectionObj.findNearestVillage(x, y, z, r);
		if (v == null)
			return null;
		return getVillageVampire(v);
	}

	public VillageVampire getVillageVampire(Village v) {
		synchronized (villageList) {
			for (VillageVampire vv : villageList) {
				if (vv.getCenter().equals(v.getCenter())) {
					return vv;
				}
			}
			VillageVampire vv = new VillageVampire();
			vv.setWorld(worldObj);
			vv.setCenter(v.getCenter());
			Logger.d("VampireVillage", "Created village at " + v.getCenter());
			villageList.add(vv);
			this.markDirty();
			return vv;
		}

	}

	public void onWorldTick(TickEvent.WorldTickEvent event) {
		if (worldObj == null || worldObj.villageCollectionObj == null)
			return;
		if (event.world.equals(worldObj) && event.phase == TickEvent.Phase.END) {
			tickCounter++;
			boolean dirty = false;

			this.checkForAnnihilatedVillages();
			for (VillageVampire v : villageList) {
				if (v.tick(tickCounter))
					dirty = true;
			}

			if (dirty)
				this.markDirty();
		}

	}

	@Override
	public void readFromNBT(NBTTagCompound p_76184_1_) {
		this.tickCounter = p_76184_1_.getInteger("Tick");

		NBTTagList nbttaglist = p_76184_1_.getTagList("Villages", 10);
		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			VillageVampire village = new VillageVampire();
			village.readFromNBT(nbttagcompound1);
			this.villageList.add(village);
		}
	}

	private void setWorld(World world) {
		this.worldObj = world;
		synchronized (villageList) {
			for (VillageVampire vv : villageList) {
				vv.setWorld(world);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound p_76187_1_) {
		p_76187_1_.setInteger("Tick", this.tickCounter);

		NBTTagList nbttaglist = new NBTTagList();
		Iterator<VillageVampire> iterator = this.villageList.iterator();
		while (iterator.hasNext()) {
			VillageVampire village =iterator.next();
			NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			village.writeToNBT(nbttagcompound1);
			nbttaglist.appendTag(nbttagcompound1);
		}

		p_76187_1_.setTag("Villages", nbttaglist);
	}

}
