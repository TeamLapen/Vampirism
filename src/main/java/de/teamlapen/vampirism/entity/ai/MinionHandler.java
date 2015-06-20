package de.teamlapen.vampirism.entity.ai;

import java.util.ArrayList;
import java.util.Iterator;

import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Used in IMinionLord classes to manage their minions
 * @author Maxanier
 *
 * @param <T>Lord class 
 */
public class MinionHandler<T extends IMinionLord> {

	private final ArrayList<IMinion> minions;
	private ArrayList<IMinion> loadedMinions;
	private final T lord;
	private final static String TAG="MinionHandler";
	
	private final IEntitySelector livingBaseSelector;
	
	public MinionHandler(final T lord){
		minions=new ArrayList<IMinion>();
		this.lord=lord;
		if(lord==null){
			throw new IllegalArgumentException("Lord cannot be null");
		}
		
		livingBaseSelector=new IEntitySelector(){

			@Override
			public boolean isEntityApplicable(Entity e) {
				if(!(e instanceof EntityLivingBase)){
					return false;
				}
				if(e instanceof IMinion){
					if(lord.equals(((IMinion) e).getLord())){
						return false;
					}
				}
				if(e instanceof EntityCreature){
					VampireMob mob=VampireMob.get((EntityCreature) e);
					if(mob.isMinion()&&lord.equals(mob.getLord())){
						return false;
					}
				}
				return true;
			}
			
		};
	}
	
	
	public boolean registerMinion(IMinion m, boolean force){
		minions.add(m);
		Logger.i("test", "register minion %s", m);
		return true;
	}
	
	public void unregisterMinion(IMinion m){
		minions.remove(m);
	}
	
	/**
	 * Removes all minions are either dead or have found another lord
	 */
	public void checkMinions(){
		Iterator<IMinion> it=minions.iterator();
		while(it.hasNext()){
			IMinion m=it.next();
			if(m.getRepresentingEntity().isDead||!lord.equals(m.getLord())){
				Logger.i("tes", "removed because %b %b %s %s",m.getRepresentingEntity().isDead,!lord.equals(m.getLord()),lord,m.getLord());
				it.remove();
			}
		}
	}
	
	@Deprecated
	public ArrayList<IMinion> getMinionListForDebug(){
		return minions;
	}
	
	public int getMinionCount(){
		return minions.size();
	}
	
	public int getMinionsLeft(){
		return Math.max(lord.getMaxMinionCount()-this.getMinionCount(), 0);
	}
	
	/**
	 * Returns an IEntitySelector which only accepts EntityLivingBases which are not minions of this lord
	 * @return
	 */
	public IEntitySelector getLivingBaseSelectorExludingMinions(){
		return livingBaseSelector;
	}
	
	public void killMinions(boolean instant,boolean onlySaveable){
		for(IMinion m:minions){
			if(!onlySaveable||m.shouldBeSavedWithLord()){
				EntityCreature e=m.getRepresentingEntity();
				if(instant){
					e.setDead();
				}
				else{
					e.attackEntityFrom(DamageSource.magic, 100);
				}
			}
		}
	}
	
	/**
	 * Returns a list of entity NBTTags to save with the player
	 * Dead entitys are saves as alive, so in multiplayer entities removed by {@link de.teamlapen.vampirism.entity.player.VampirePlayer#onPlayerLoggedOut()} are saved as well
	 * @return
	 */
	public NBTTagList getMinionsToSave(){
		NBTTagList list=new NBTTagList();
		for(IMinion m:minions){
			if(m.shouldBeSavedWithLord()){
				Entity e=m.getRepresentingEntity();
				boolean dead=e.isDead;
				e.isDead=false;
				NBTTagCompound nbt=new NBTTagCompound();
				m.getRepresentingEntity().writeMountToNBT(nbt);
				list.appendTag(nbt);
				if(dead)e.isDead=true;
			}
		}
		Logger.d(TAG, "Saved "+list.tagCount()+" minions");
		return list;
	}
	
	public void loadMinions(NBTTagList list){
		if(list==null||list.tagCount()==0){
			Logger.d(TAG, "Empty minion list to load");
			return;
		}
		Logger.d(TAG, "Loading "+list.tagCount()+" minions");
		loadedMinions = new ArrayList<IMinion>();
		for(int i=0;i<list.tagCount();i++){
			NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
	        Entity entity = EntityList.createEntityFromNBT(nbttagcompound, lord.getRepresentingEntity().worldObj);
	        if(entity!=null&&entity instanceof IMinion){
	        	entity.posY=entity.posY+entity.height;
	        	loadedMinions.add((IMinion) entity);
	        	Logger.d(TAG, "Loaded minion from nbt");
	        }
	        else{
	        	Logger.d(TAG, "Failed to load minion from NBT");
	        }
		}

	}
	
	public void addLoadedMinions(){
		if(loadedMinions==null)return;
		ArrayList<IMinion> list=loadedMinions;
		loadedMinions=null;
		Logger.d(TAG, "Going to add "+list.size());
		World world=lord.getRepresentingEntity().worldObj;
		for(IMinion m:list){
			Entity entity=m.getRepresentingEntity();
			entity.forceSpawn=true;
	    	world.spawnEntityInWorld(entity);
	    	((IMinion)entity).setLord(lord);
		}
		Logger.d(TAG, "Added "+list.size()+" minions");
		list.clear();
		
	}
	
	public void teleportMinionsToLord(){
		ArrayList<IMinion> toTeleportDim=new ArrayList<IMinion>();
		Entity e1=lord.getRepresentingEntity();
		Iterator<IMinion> it=minions.iterator();
		while(it.hasNext()){
			IMinion m=it.next();
			Entity e=m.getRepresentingEntity();
			if(e1.dimension!=e.dimension){
				toTeleportDim.add(m);
				it.remove();
			}
		}
		for(IMinion m:minions){
			Entity e=m.getRepresentingEntity();
			
			e.copyLocationAndAnglesFrom(e1);
		}
		for(IMinion m:toTeleportDim){
			Logger.d(TAG, "Teleporting minion");
			Entity e=m.getRepresentingEntity();
				e.travelToDimension(e1.dimension);
				e.timeUntilPortal=e.getPortalCooldown();
				
		}

	}
}
