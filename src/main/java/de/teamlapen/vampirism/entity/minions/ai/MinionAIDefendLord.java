package de.teamlapen.vampirism.entity.minions.ai;

import de.teamlapen.vampirism.api.entity.minions.IMinion;
import de.teamlapen.vampirism.api.entity.minions.IMinionLord;
import de.teamlapen.vampirism.util.MinionHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

/**
 * TargetTask for minions which fight for their bosses
 *
 * @author maxanier
 */
public class MinionAIDefendLord extends EntityAITarget {

    protected IMinion minion;

    protected EntityLivingBase target;

    protected int lastUpdate = 0;

    public MinionAIDefendLord(IMinion minion) {
        super(MinionHelper.entity(minion), false, false);
        this.minion = minion;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (taskOwner.ticksExisted < lastUpdate + 100) return false;

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
        taskOwner.setAttackTarget(target);
        lastUpdate = taskOwner.ticksExisted;
        super.startExecuting();
    }
}