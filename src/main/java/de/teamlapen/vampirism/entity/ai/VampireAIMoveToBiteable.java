package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.entity.DefaultVampire;
import de.teamlapen.vampirism.entity.VampireMob;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;

import java.util.List;

/**
 * Makes the entity move to a biteable. Has a timeout between trys
 * 
 * @author Max
 *
 */
public class VampireAIMoveToBiteable extends EntityAIBase {

	private final DefaultVampire vampire;
	private EntityCreature target;
	private int timeout;

	public VampireAIMoveToBiteable(DefaultVampire vampire) {
		super();
		this.vampire = vampire;
		this.setMutexBits(1);
	}

	@Override
	public boolean continueExecuting() {
		return (!this.vampire.getNavigator().noPath() && !target.isDead);
	}

	@Override
	public void resetTask() {
		target = null;
		timeout = (vampire.getRNG().nextInt(5) == 0 ? 80 : 3);
	}

	@Override
	public boolean shouldExecute() {
		if (timeout > 0) {
			timeout--;
			return false;
		}
		List list = vampire.worldObj.getEntitiesWithinAABB(EntityCreature.class, vampire.boundingBox.expand(10, 3, 10));
		for (Object o : list) {
			if (VampireMob.get((EntityCreature) o).getBlood() > 0) {
				target = (EntityCreature) o;
				return true;
			}
		}
		target = null;
		return false;
	}

	@Override
	public void startExecuting() {
		vampire.getNavigator().tryMoveToEntityLiving(target, 1.0);
	}

}
