package de.teamlapen.vampirism.entity.minions.ai;

import de.teamlapen.vampirism.api.entity.minions.IMinion;
import de.teamlapen.vampirism.api.entity.minions.IMinionLord;
import de.teamlapen.vampirism.util.MinionHelper;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TargetGoal;

import java.util.EnumSet;

/**
 * TargetTask for minions which fight for their bosses
 *
 * @author maxanier
 */
public class DefendLordMinionGoal extends TargetGoal {

    protected IMinion minion;

    protected LivingEntity target;

    protected int lastUpdate = 0;

    public DefendLordMinionGoal(IMinion minion) {
        super(MinionHelper.entity(minion), false, false);
        this.minion = minion;
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean shouldExecute() {
        if (goalOwner.ticksExisted < lastUpdate + 100) return false;

        IMinionLord l = minion.getLord();
        if (l != null) {
            target = l.getMinionTarget();
        } else {
            return false;
        }

        return this.func_220777_a(target, EntityPredicate.DEFAULT);//TODO mapping -> this.isSuitableTarget(target, EntityPredicates.IS_ALIVE);
    }

    @Override
    public void startExecuting() {
        goalOwner.setAttackTarget(target);
        lastUpdate = goalOwner.ticksExisted;
        super.startExecuting();
    }
}