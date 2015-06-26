package de.teamlapen.vampirism.villages;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import de.teamlapen.vampirism.entity.EntityVampireHunter;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;

/**
 * Saveable class which handle and stores vampirism related data for villages
 * 
 * @author Maxanier
 *
 */
public class VillageVampire {
	public static AxisAlignedBB getBoundingBox(Village v) {
		int r = v.getVillageRadius();
		ChunkCoordinates cc = v.getCenter();
		return AxisAlignedBB.getBoundingBox(cc.posX - r, cc.posY - 10, cc.posZ - r, cc.posX + r, cc.posY + 10, cc.posZ + r);
	}
	private final String TAG = "VillageVampire";
	private World world;
	private final ChunkCoordinates center = new ChunkCoordinates(0, 0, 0);
	private int recentlyBitten;
	private int recentlyConverted;
	private boolean agressive;

	private boolean dirty;

	private void checkHunterCount(Village v) {
		int count = getHunter(v).size();
		if (count < BALANCE.MOBPROP.VAMPIRE_HUNTER_MAX_PER_VILLAGE || (agressive && count < BALANCE.MOBPROP.VAMPIRE_HUNTER_MAX_PER_VILLAGE * 1.4)) {
			for (Entity e : Helper.spawnEntityInVillage(v, 2, REFERENCE.ENTITY.VAMPIRE_HUNTER_NAME, world)) {
				((EntityVampireHunter) e).setHomeArea(v.getCenter().posX, v.getCenter().posY, v.getCenter().posZ, v.getVillageRadius());
				if (agressive) {
					((EntityVampireHunter) e).setLevel(3, true);
				}
			}
		}
	}

	public AxisAlignedBB getBoundingBox() {
		return getBoundingBox(this.getVillage());
	}

	public ChunkCoordinates getCenter() {
		return center;
	}

	@SuppressWarnings("unchecked")
	private List<EntityVampireHunter> getHunter(Village v) {
		return world.getEntitiesWithinAABB(EntityVampireHunter.class, getBoundingBox(v));
	}

	public Village getVillage() {
		Village v = world.villageCollectionObj.findNearestVillage(center.posX, center.posY, center.posZ, 0);
		if (v == null)
			return null;
		if (!v.getCenter().equals(center)) {
			Logger.w(TAG, "There is no village at this position: " + center.toString());
			return null;
		}
		return v;
	}

	@SuppressWarnings("unchecked")
	private List<EntityVillager> getVillager(Village v) {
		return world.getEntitiesWithinAABB(EntityVillager.class, getBoundingBox(v));
	}

	/**
	 * Checks if the corrosponding village still exists
	 * 
	 * @return -1 annihilated,0 center has been updated, 1 ok
	 */
	public int isAnnihilated() {
		Village v = world.villageCollectionObj.findNearestVillage(center.posX, center.posY, center.posZ, 0);
		if (v == null) {
			Logger.i(TAG, "Can't find village at " + center.toString());
			return -1;
		}
		if (!this.getCenter().equals(v.getCenter())) {
			this.setCenter(v.getCenter());
			return 0;
		}
		return 1;
	}

	private void makeAgressive(Village v) {
		Logger.d(TAG, "Making agrressive");
		agressive = true;
		for (EntityVillager e : getVillager(v)) {
			if (!VampireMob.get(e).isVampire() && world.rand.nextInt(4) == 0) {
				EntityVampireHunter h = (EntityVampireHunter) EntityList.createEntityByName(REFERENCE.ENTITY.VAMPIRE_HUNTER_NAME, world);
				h.copyLocationAndAnglesFrom(e);
				world.spawnEntityInWorld(h);
				h.setLevel(1, true);
				e.setDead();
			}

		}
		dirty = true;
	}

	private void makeCalm(Village v) {
		Logger.d(TAG, "Making calm");
		agressive = false;
		for (EntityVampireHunter e : getHunter(v)) {
			if (e.getLevel() == 1) {
				Entity ev = EntityList.createEntityByName("Villager", world);
				ev.copyLocationAndAnglesFrom(e);
				world.spawnEntityInWorld(ev);
				e.setDead();
			}
		}
		dirty = true;
	}

	public void readFromNBT(NBTTagCompound nbt) {
		center.posX = nbt.getInteger("CX");
		center.posY = nbt.getInteger("CY");
		center.posZ = nbt.getInteger("CZ");
		agressive = nbt.getBoolean("AGR");
		recentlyBitten = nbt.getInteger("BITTEN");
		recentlyConverted = nbt.getInteger("CONVERTED");
	}

	public void setCenter(ChunkCoordinates cc) {
		center.set(cc.posX, cc.posY, cc.posZ);
	}

	public void setWorld(World world) {
		this.world = world;
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

	/**
	 * Updates the VillageVampire
	 * 
	 * @param tickCounter
	 * @return dirty
	 */
	public boolean tick(int tickCounter) {
		if (tickCounter % 20 == 0) {
			Village v = getVillage();
			if (v != null) {
				if ((tickCounter % (20 * BALANCE.VV_PROP.REDUCE_RATE)) == 0) {
					if (recentlyBitten > 0) {
						recentlyBitten--;
						dirty = true;
						// if(world.rand.nextInt(3)>0){
						spawnVillager(v);
						// }
					}
					if (recentlyConverted > 0) {
						recentlyConverted--;
						dirty = true;
						// if(world.rand.nextInt(3)>0){
						spawnVillager(v);
						// }
					}
				}
				if (tickCounter % (20 * 45) == 0) {
					checkHunterCount(v);
				}
				if (recentlyBitten > BALANCE.VV_PROP.BITTEN_UNTIL_AGRESSIVE || recentlyConverted > BALANCE.VV_PROP.CONVERTED_UNTIL_AGRESSIVE) {
					if (!agressive) {
						makeAgressive(v);
					}
				} else if (agressive && (recentlyBitten == 0 || recentlyBitten < BALANCE.VV_PROP.BITTEN_UNTIL_AGRESSIVE - 1)
						&& (recentlyConverted == 0 || recentlyConverted < BALANCE.VV_PROP.CONVERTED_UNTIL_AGRESSIVE - 1)) {
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

	public void villagerBitten() {
		recentlyBitten++;
		dirty = true;
	}

	public void villagerConvertedByVampire() {
		recentlyConverted++;
		dirty = true;
	}

	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("CX", center.posX);
		nbt.setInteger("CY", center.posY);
		nbt.setInteger("CZ", center.posZ);
		nbt.setBoolean("AGR", agressive);
		nbt.setInteger("BITTEN", recentlyBitten);
		nbt.setInteger("CONVERTED", recentlyConverted);
	}

}
