package de.teamlapen.vampirism.entity.minions.ai;

import de.teamlapen.vampirism.api.entity.minions.IMinion;
import de.teamlapen.vampirism.util.MinionHelper;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;

public class HurtByTargetMinionGoal extends HurtByTargetGoal {
    final IMinion minion;

    public HurtByTargetMinionGoal(IMinion minion) {
        super(MinionHelper.entity(minion));
        this.minion = minion;
    }

    @Override
    public boolean shouldExecute() { //TODO 1.14 check
        if (MinionHelper.isLordSafe(minion, target)) return false;
        return super.shouldExecute();
    }
}