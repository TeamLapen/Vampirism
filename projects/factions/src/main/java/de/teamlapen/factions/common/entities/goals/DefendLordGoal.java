package de.teamlapen.factions.common.entities.goals;

import de.teamlapen.factions.common.core.FactionMinionTasks;
import de.teamlapen.factions.common.minions.MinionEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;


public class DefendLordGoal extends TargetGoal {

    protected final @NotNull MinionEntity<?> entity;
    private final @NotNull TargetingConditions predicate;
    private final int maxStartDistSQ = 200;
    private final int maxStopDistSQ = 500;

    public DefendLordGoal(@NotNull MinionEntity<?> mobIn) {
        super(mobIn, false, false);
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        this.entity = mobIn;
        this.predicate = TargetingConditions.forCombat().selector((e, level) -> entity.getAttackPredicate(false).test(e) && entity.getLordOpt().map(lp -> lp.asEntity().distanceToSqr(e) < maxStartDistSQ).orElse(true)).ignoreInvisibilityTesting().range(60);

    }

    @Override
    public boolean canContinueToUse() {
        return entity.getCurrentTask().map(d -> d.getTask() == FactionMinionTasks.PROTECT_LORD.get()).orElse(false) && super.canContinueToUse() && entity.getLordOpt().map(lp -> lp.asEntity().distanceToSqr(targetMob) < maxStopDistSQ).orElse(true);
    }

    @Override
    public boolean canUse() {
        return entity.getCurrentTask().map(d -> d.getTask() == FactionMinionTasks.PROTECT_LORD.get()).orElse(false) && entity.getLordOpt().map(lp -> {
            LivingEntity attackTarget = lp.asEntity().getLastHurtMob();
            if (canAttack(attackTarget, predicate)) {
                this.targetMob = attackTarget;
                return true;
            }
            LivingEntity revengeTarget = lp.asEntity().getLastHurtByMob();
            if (canAttack(revengeTarget, predicate)) {
                this.targetMob = revengeTarget;
                return true;
            }
            return false;
        }).orElse(false);
    }

    @Override
    public void start() {
        super.start();
        this.entity.setTarget(targetMob);
    }
}
