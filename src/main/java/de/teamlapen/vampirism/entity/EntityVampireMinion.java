package de.teamlapen.vampirism.entity;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import de.teamlapen.vampirism.entity.ai.EntityAIFollowBoss;
import de.teamlapen.vampirism.entity.ai.IMinion;

public class EntityVampireMinion extends DefaultVampire implements IMinion{

	private UUID bossId=null;
	protected EntityLiving boss;
	private int lookForBossTimer=0;
	private final static int MAX_SEARCH_TIME=100;
	
	public EntityVampireMinion(World world) {
		super(world);
		
		this.tasks.addTask(4, new EntityAIFollowBoss(this,1.0D));
		this.addAttackingTargetTasks(2);
	}
	
	public void setBossId(UUID id){
		if(bossId!=id){
			bossId=id;
			boss=null;
			lookForBossTimer=0;
		}
	}
	
	@Override
	public void onLivingUpdate() {
		if(!this.worldObj.isRemote){
			if(boss==null){
				List<EntityLiving> list=this.worldObj.getEntitiesWithinAABB(EntityLiving.class, this.boundingBox.expand(20, 15, 20));
				for(EntityLiving e:list){
					if(e.getPersistentID().equals(bossId)){
						boss=e;
						lookForBossTimer=0;
						break;
					}
				}
				if(boss==null){
					lookForBossTimer++;
				}
				if(lookForBossTimer>MAX_SEARCH_TIME){
					this.attackEntityFrom(DamageSource.generic, 5);
				}
			}
			else if(!boss.isEntityAlive()){
				boss=null;
				bossId=null;
			}
			
			
		}
		super.onLivingUpdate();
	}

	@Override
	public EntityLiving getBoss() {
		return boss;
	}
	

}
