package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.util.Logger;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

/**
 * TargetTask for minions which fight for their bosses
 * @author maxanier
 *
 */
public class EntityAIDefendLord extends EntityAITarget {

	EntityCreature entity;

	EntityLivingBase target;

	public EntityAIDefendLord(EntityCreature e) {
		super(e, false, false);
		if(!(e instanceof IMinion)){
			throw new IllegalArgumentException("This Task can only be used by IMinion");
		}
		entity = e;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		IMinionLord l = ((IMinionLord) ((IMinion) entity).getLord());
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
		this.entity.setAttackTarget(target);
		super.startExecuting();
	}
}
