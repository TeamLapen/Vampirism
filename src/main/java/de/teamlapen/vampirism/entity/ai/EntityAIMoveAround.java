package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.entity.minions.IMinion;
import de.teamlapen.vampirism.entity.minions.MinionHelper;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.Vec3;

/**
 * Very similar to EntityAIWander, but with less waiting in between.
 * Trys to make distance by running away from the prior target, also trys to detect if it is stuck and teleports.
 * @author Maxanier
 *
 */
public class EntityAIMoveAround extends EntityAIBase {

	private final EntityCreature creature;
	private double xPosition;
	private double yPosition;
	private double zPosition;
	private Vec3 oldPosition;
	private Vec3 oldStopPosition;
	private double speed;
	private int shortTrys;
	
	public EntityAIMoveAround(EntityCreature creature,double speed){
		this.creature=creature;
		this.setMutexBits(1);
		this.speed=speed;
		oldPosition=Vec3.createVectorHelper(creature.posX, creature.posY, creature.posZ);
		oldStopPosition=oldPosition;
	}
	
	@Override
	public boolean shouldExecute() {
		Vec3 vec3 = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.creature, 30, 10,oldPosition);

        if (vec3 == null)
        {
            return false;
        }
        else
        {
        	oldPosition=Vec3.createVectorHelper(xPosition, yPosition, zPosition);
            this.xPosition = vec3.xCoord;
            this.yPosition = vec3.yCoord;
            this.zPosition = vec3.zCoord;
            
            return true;
        }
	}
	
	/**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.creature.getNavigator().noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
    	testNotify("start");
    	if(shortTrys>10){
    		testNotify("stuck");
    		double d0 = creature.posX + (creature.getRNG().nextDouble() - 0.5D) * 15;
    		double d1 = creature.posY + (creature.getRNG().nextInt(15) - 15 * 0.5D);
    		double d2 = creature.posZ + (creature.getRNG().nextDouble() - 0.5D) * 15;
    		if(Helper.teleportTo(creature,d0, d1, d2,false)){
    			shortTrys=0;
    			this.creature.getNavigator().clearPathEntity();
    		}
    	}
        this.creature.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
    }
    
    @Override
    public void resetTask(){
    	testNotify("stop");
    	Vec3 s=Vec3.createVectorHelper(creature.posX, creature.posY, creature.posZ);
    	if(oldStopPosition.squareDistanceTo(s)<36){
        	shortTrys++;
        }
        else if(shortTrys>0){
        	shortTrys--;
        }
    	oldStopPosition=s;
    }
    
    private void testNotify(String s){
    	IMinion m=MinionHelper.getMinionFromEntity(creature);
    	if(m!=null){
    		MinionHelper.sendMessageToLord(m,"MoveAround "+s);
    	}
    }

}
