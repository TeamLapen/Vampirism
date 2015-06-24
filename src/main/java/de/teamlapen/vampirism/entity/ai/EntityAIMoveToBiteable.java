package de.teamlapen.vampirism.entity.ai;

import java.util.List;

import de.teamlapen.vampirism.entity.DefaultVampire;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.entity.minions.IMinion;
import de.teamlapen.vampirism.entity.minions.MinionHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;

/**
 * Makes the entity move to a biteable.
 * Has a timeout between trys
 * @author Max
 *
 */
public class EntityAIMoveToBiteable extends EntityAIBase {

	private final DefaultVampire vampire;
	private EntityCreature target;
	private int timeout;
	
	public EntityAIMoveToBiteable(DefaultVampire vampire) {
		super();
		this.vampire = vampire;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		if(timeout>0){
			timeout--;
			return false;
		}
		List list=vampire.worldObj.getEntitiesWithinAABB(EntityCreature.class, vampire.boundingBox.expand(10, 3, 10));
		for(Object o:list){
			if(VampireMob.get((EntityCreature) o).canBeBitten()){
				target=(EntityCreature) o;
				return true;
			}
		}
		target=null;
		return false;
	}
	
	public void startExecuting(){
		testNotify("start "+target);
		vampire.getNavigator().tryMoveToEntityLiving(target, 1.0);
	}
	
	public boolean continueExecuting(){
		return (!this.vampire.getNavigator().noPath() && !target.isDead );
	}
	
	public void resetTask(){
		target=null;
		timeout=80;
		testNotify("stopped");
	}
	
    private void testNotify(String s){
    	IMinion m=MinionHelper.getMinionFromEntity(vampire);
    	if(m!=null){
    		MinionHelper.sendMessageToLord(m,"MoveBiteable "+s);
    	}
    }

}
