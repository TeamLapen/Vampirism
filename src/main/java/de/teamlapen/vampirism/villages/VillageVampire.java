package de.teamlapen.vampirism.villages;

import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import de.teamlapen.vampirism.entity.EntityVampireHunter;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.village.Village;
import net.minecraft.world.World;

public class VillageVampire {
	private final String TAG="VillageVampire";
	private World world;
	private final ChunkCoordinates center = new ChunkCoordinates(0, 0, 0);
	private int recentlyBitten;
	private boolean agressive;
	private boolean dirty;
	
	
	public void setWorld(World world){
		this.world=world;
	}
	public void writeToNBT(NBTTagCompound nbt){
		nbt.setInteger("CX",center.posX);
		nbt.setInteger("CY",center.posY);
		nbt.setInteger("CZ",center.posZ);
		nbt.setBoolean("AGR", agressive);
		nbt.setInteger("BITTEN", recentlyBitten);
	}
	
	public void readFromNBT(NBTTagCompound nbt){
		center.posX=nbt.getInteger("CX");
		center.posY=nbt.getInteger("CY");
		center.posZ=nbt.getInteger("CZ");
		agressive=nbt.getBoolean("AGR");
		recentlyBitten=nbt.getInteger("BITTEN");
	}
	
	public Village getVillage(){
		Village v=world.villageCollectionObj.findNearestVillage(center.posX, center.posY, center.posZ, 0);
		if(v==null)return null;
		if(!v.getCenter().equals(center)){
			Logger.w(TAG, "There is no village at this position: "+center.toString());
			return null;
		}
		return v;
	}
	
	public ChunkCoordinates getCenter(){
		return center;
	}
	
	public void setCenter(ChunkCoordinates cc){
		center.set(cc.posX, cc.posY, cc.posZ);
	}
	
	/**
	 * Checks if the corrosponding village still exists
	 * @return -1 annihilated,0 center has been updated, 1 ok
	 */
	public int isAnnihilated(){
		Village v=world.villageCollectionObj.findNearestVillage(center.posX, center.posY, center.posZ, 0);
		if(v==null||v.isAnnihilated())return -1;
		if(!this.getCenter().equals(v.getCenter())){
			this.setCenter(v.getCenter());
			return 0;
		}
		return 1;
	}
	
	/**
	 * Updates the VillageVampire
	 * @param tickCounter
	 * @return dirty
	 */
	public boolean tick(int tickCounter){
		if(tickCounter%20==0){
			Village v=getVillage();
			if(v!=null){
				if(tickCounter%(20*50)==0){
					if(recentlyBitten>0){
						recentlyBitten--;
						Logger.i(TAG, "Reducing recently bitten");
						dirty=true;
						if(world.rand.nextInt(3)>0){
							spawnVillager(v);
						}
					}

				}
				if(tickCounter%(20*45)==0){
					checkHunterCount(v);
				}
				if(recentlyBitten>2){
					if(!agressive){
						makeAgressive(v);
					}
				}
				else if(agressive){
					makeCalm(v);
				}
				
			}
		}
		if(dirty){
			dirty=false;
			return true;
		}
		return false;
	}
	
	private void makeAgressive(Village v){
		agressive=true;
		for(EntityVampireHunter e:getHunter(v)){
			e.setAgressive(true);
		}
		dirty=true;
	}
	private void makeCalm(Village v){
		agressive=false;
		for(EntityVampireHunter e:getHunter(v)){
			e.setAgressive(false);
		}
		dirty=true;
	}
	
	@SuppressWarnings("unchecked")
	private List<EntityVampireHunter> getHunter(Village v){
		return world.getEntitiesWithinAABB(EntityVampireHunter.class, getBoundingBox(v));
	}
	
	private AxisAlignedBB getBoundingBox(Village v){
		int r=v.getVillageRadius();
		ChunkCoordinates cc=v.getCenter();
		return AxisAlignedBB.getBoundingBox((double)(cc.posX - r), (double)(cc.posY - 10), (double)(cc.posZ - r), (double)(cc.posX + r), (double)(cc.posY + 10), (double)(cc.posZ + r));
	}
	
	public void villagerBitten(){
		recentlyBitten++;
		dirty=true;
	}
	
	private void checkHunterCount(Village v){
		int count=getHunter(v).size();
		if(count <BALANCE.MOBPROP.VAMPIRE_HUNTER_MAX_PER_VILLAGE||(agressive&&count < BALANCE.MOBPROP.VAMPIRE_HUNTER_MAX_PER_VILLAGE*1.4)){
			Logger.i(TAG, "Spawning new hunters");
			for(EntityCreature e:Helper.spawnEntityCreatureInVillage(v, 2, REFERENCE.ENTITY.VAMPIRE_HUNTER_NAME,world)){
					((EntityVampireHunter) e).setHomeArea(v.getCenter().posX, v.getCenter().posY, v.getCenter().posZ, v.getVillageRadius());
					((EntityVampireHunter) e).setAgressive(agressive);
			}
		}
	}
	private void spawnVillager(Village v){

		List l=world.getEntitiesWithinAABB(EntityVillager.class, getBoundingBox(v));
		if(l.size()>0){
			Logger.i(TAG, "Spawning villager");
			EntityVillager ev=(EntityVillager)l.get(world.rand.nextInt(l.size()));
			EntityVillager entityvillager = ev.createChild(ev);
	        ev.setGrowingAge(6000);
	        entityvillager.setGrowingAge(-24000);
	        entityvillager.setLocationAndAngles(ev.posX, ev.posY, ev.posZ, 0.0F, 0.0F);
	        world.spawnEntityInWorld(entityvillager);
	        world.setEntityState(entityvillager, (byte)12);
		}
	}
	

	
}
