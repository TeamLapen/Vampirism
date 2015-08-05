package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.entity.minions.IMinion;
import de.teamlapen.vampirism.entity.minions.IMinionLord;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

/**
 * TargetTask for minions which fight for their bosses
 * 
 * @author maxanier
 *
 */
public class MinionAIDefendLord extends EntityAITarget {

	IMinion minion;

	EntityLivingBase target;
	
	int lastUpdate=0;

	public MinionAIDefendLord(IMinion minion) {
		super(minion.getRepresentingEntity(), false, false);
		this.minion = minion;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		if(minion.getRepresentingEntity().ticksExisted<lastUpdate+100)return false;
		
		IMinionLord l = minion.getLord();
		if (l != null) {
			target = l.getMinionTarget();
		} else {
			return false;
		}

		return this.isSuitableTarget(target, false);
	}

	@Override
	public void startExecuting() {
		minion.getRepresentingEntity().setAttackTarget(target);
		lastUpdate=minion.getRepresentingEntity().ticksExisted;
		super.startExecuting();
	}
}
