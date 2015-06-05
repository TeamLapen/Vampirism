package de.teamlapen.vampirism.entity.ai;

import java.util.ArrayList;
import java.util.Iterator;

import de.teamlapen.vampirism.entity.VampireMob;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;

public class MinionHandler<T extends IMinionLord> {

	private final ArrayList<IMinion> minions;
	private final T lord;
	
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
		return true;
	}
	
	public void unregisterMinion(IMinion m){
		minions.remove(m);
	}
	
	public void checkMinions(){
		Iterator<IMinion> it=minions.iterator();
		while(it.hasNext()){
			IMinion m=it.next();
			if(m.getRepresentingEntity().isDead||!lord.equals(m.getLord())){
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
}
