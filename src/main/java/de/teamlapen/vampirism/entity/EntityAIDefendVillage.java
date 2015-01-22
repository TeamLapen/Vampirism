package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.village.Village;

public class EntityAIDefendVillage extends EntityAITarget{

	EntityVampireHunter hunter;
    /**
     * The aggressor of the iron golem's village which is now the golem's attack target.
     */
    EntityLivingBase villageAgressorTarget;
    
    public EntityAIDefendVillage(EntityVampireHunter h) {
		super(h, false,true);
		hunter=h;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		Village v=hunter.getHomeVillage();
		if(v==null)return false;
		
		this.villageAgressorTarget=v.findNearestVillageAggressor(hunter);
		
		if(this.isSuitableTarget(villageAgressorTarget, false)){
			return true;
		}
		else if(villageAgressorTarget !=null){
			Logger.i("test", villageAgressorTarget.toString());
			if(villageAgressorTarget instanceof EntityPlayer){
				if(VampirePlayer.get((EntityPlayer)(villageAgressorTarget)).getLevel()>BALANCE.VAMPIRE_HUNTER_ATTACK_LEVEL){
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
   public void startExecuting()
   {
       this.hunter.setAttackTarget(this.villageAgressorTarget);
       super.startExecuting();
   }
}
