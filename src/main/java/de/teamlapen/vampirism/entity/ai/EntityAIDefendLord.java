package de.teamlapen.vampirism.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import de.teamlapen.vampirism.util.Logger;

/**
 * TargetTask for minions which fight for their bosses
 * 
 * @author maxanier
 *
 */
public class EntityAIDefendLord extends EntityAITarget {

	IMinion minion;

	EntityLivingBase target;

	public EntityAIDefendLord(IMinion minion) {
		super(minion.getRepresentingEntity(), false, false);
		this.minion = minion;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		IMinionLord l = minion.getLord();
		if (l != null) {
			target = l.getMinionTarget();
		} else {
			return false;
		}

		if (this.isSuitableTarget(target, false)) {
			return true;
		}
		return false;
	}

	@Override
	public void startExecuting() {
		minion.getRepresentingEntity().setAttackTarget(target);
		Logger.i("defen", "attack " + target);
		super.startExecuting();
	}
}
