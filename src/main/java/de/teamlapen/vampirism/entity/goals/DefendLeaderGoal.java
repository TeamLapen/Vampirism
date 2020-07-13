package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.vampirism.api.entity.IEntityLeader;
import de.teamlapen.vampirism.entity.vampire.BasicVampireEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;

import java.util.EnumSet;

public class DefendLeaderGoal extends TargetGoal {
    private final BasicVampireEntity entity;
    private LivingEntity attacker;
    private int timestamp;

    public DefendLeaderGoal(BasicVampireEntity basicVampire) {
        super(basicVampire, false);
        this.entity = basicVampire;
        this.setMutexFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    public boolean shouldExecute() {
        IEntityLeader leader = this.entity.getAdvancedLeader();
        if (leader == null) {
            return false;
        } else {
            this.attacker = leader.getRepresentingEntity().getRevengeTarget();
            int i = leader.getRepresentingEntity().getRevengeTimer();
            return i != this.timestamp && this.isSuitableTarget(this.attacker, EntityPredicate.DEFAULT);
        }

    }

    public void startExecuting() {
        this.goalOwner.setAttackTarget(this.attacker);
        IEntityLeader leader = this.entity.getAdvancedLeader();
        if (leader != null) {
            this.timestamp = leader.getRepresentingEntity().getRevengeTimer();
        }

        super.startExecuting();
    }
}